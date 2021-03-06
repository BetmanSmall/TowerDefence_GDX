package com.betmansmall.game.gameLogic.playerTemplates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.XmlReader;
import com.betmansmall.maps.MapLoader;
import com.betmansmall.utils.logging.Logger;

public class Template {
    public String templateName;
    public ObjectMap<String, String> properties;
    public ObjectMap<Integer, TiledMapTile> tiles;
    public ObjectMap<Integer, AnimatedTiledMapTile> animatedTiles;

    public Template() {
        Logger.logFuncStart();
        templateName = "NULL";
        properties = new ObjectMap<String, String>();
        tiles = new ObjectMap<Integer, TiledMapTile>();
        animatedTiles = new ObjectMap<Integer, AnimatedTiledMapTile>();
    }

    public void dispose() {
        Logger.logFuncStart();
    }

    public void loadBasicTemplate(FileHandle templateFile) throws Exception {
        Logger.logFuncStart("templateFile:" + templateFile);
        try {
            XmlReader xmlReader = new XmlReader();
            XmlReader.Element templateORtileset = xmlReader.parse(templateFile);
            this.templateName = templateORtileset.getAttribute("name");

            int firstgid = templateORtileset.getIntAttribute("firstgid", 0);
            int tilewidth = templateORtileset.getIntAttribute("tilewidth", 0);
            int tileheight = templateORtileset.getIntAttribute("tileheight", 0);
            int spacing = templateORtileset.getIntAttribute("spacing", 0);
            int margin = templateORtileset.getIntAttribute("margin", 0);

            MapLoader.loadPropertiesStatic(getProperties(), templateORtileset.getChildByName("properties"));
            XmlReader.Element imageElement = templateORtileset.getChildByName("image");
            String source = imageElement.getAttribute("source", null);
            FileHandle textureFile = MapLoader.getRelativeFileHandle(templateFile, source);
            Texture texture = new Texture(textureFile);
            Logger.logDebug("textureFile:" + textureFile);
            Logger.logDebug("texture:" + texture);

            int stopWidth = texture.getWidth() - tilewidth;
            int stopHeight = texture.getHeight() - tileheight;
            int id = firstgid;

            for (int y = margin; y <= stopHeight; y += tileheight + spacing) {
                for (int x = margin; x <= stopWidth; x += tilewidth + spacing) {
                    TextureRegion tileRegion = new TextureRegion(texture, x, y, tilewidth, tileheight);
                    TiledMapTile tiledMapTile = new StaticTiledMapTile(tileRegion);
                    tiledMapTile.setId(id);
                    tiles.put(id++, tiledMapTile);
                }
            }
            Array<XmlReader.Element> tileElements = templateORtileset.getChildrenByName("tile");
            for (XmlReader.Element tileElement : tileElements) {
                int localtid = tileElement.getIntAttribute("id", 0);
                TiledMapTile tiledMapTile = tiles.get(firstgid + localtid);
                if (tiledMapTile != null) {
                    MapLoader.loadPropertiesStatic(tiledMapTile.getProperties(), tileElement.getChildByName("properties"));
                    XmlReader.Element animationElement = tileElement.getChildByName("animation");
                    if (animationElement != null) {
                        Array<StaticTiledMapTile> staticTiles = new Array<StaticTiledMapTile>();
                        IntArray intervals = new IntArray();
                        for (XmlReader.Element frameElement : animationElement.getChildrenByName("frame")) {
                            staticTiles.add((StaticTiledMapTile) tiles.get(firstgid + frameElement.getIntAttribute("tileid")));
                            intervals.add(frameElement.getIntAttribute("duration"));
                        }
                        AnimatedTiledMapTile animatedTile = new AnimatedTiledMapTile(intervals, staticTiles);
                        animatedTile.setId(tiledMapTile.getId());
                        animatedTile.getProperties().putAll(tiledMapTile.getProperties());
                        animatedTiles.put(animatedTile.getId(), animatedTile);
//                        tiledMapTile = animatedTile;
                    }
                }
            }
        } catch (Exception exp) {
            Logger.logError("Could not load Template from " + templateFile.path() + " Exp:" + exp);
            throw new Exception("Could not load Template from " + templateFile.path() + " Exp:" + exp);
        }
    }

    public void basicValidate() {
        if (properties.containsKey("templateName") || templateName != null) {
            Logger.logDebug("templateName:" + templateName);
            Logger.logDebug("tiles.size:" + tiles.size);
            Logger.logDebug("animatedTiles.size:" + animatedTiles.size);
        }
    }

    public ObjectMap<String, String> getProperties() {
        return properties;
    }

    protected AnimatedTiledMapTile flipAnimatedTiledMapTile(AnimatedTiledMapTile animatedTiledMapTile) {
        Array<StaticTiledMapTile> frames = new Array<StaticTiledMapTile>(animatedTiledMapTile.getFrameTiles());
        for (int k = 0; k < frames.size; k++) {
            TextureRegion textureRegion = new TextureRegion(frames.get(k).getTextureRegion());
            textureRegion.flip(true, false);
            StaticTiledMapTile frame = new StaticTiledMapTile(textureRegion);
            frames.set(k, frame);
        }
        IntArray intervals = new IntArray(animatedTiledMapTile.getAnimationIntervals());
        return new AnimatedTiledMapTile(intervals, frames);
    }

    public String toStringProperties() {
        StringBuilder sb = new StringBuilder();
        sb.append("Properties[");
        if (properties.size != 0) {
//        Iterator<String> keys = properties.getKeys();
//        while (keys.hasNext()) {
            for (String key : properties.keys()) {
//            String key = keys.next();
                sb.append(key + ":" + properties.get(key) + ",");
            }
            sb.deleteCharAt(sb.length - 1);
        } else {
            sb.append("empty");
        }
        sb.append("]");
        return sb.toString();
    }

    public String toStringBasicParam() {
        StringBuilder sb = new StringBuilder();
        sb.append("Template[");
        sb.append("templateName:" + templateName);
        sb.append(",tiles.size:" + tiles.size);
        sb.append(",animatedTiles.size:" + animatedTiles.size);
        sb.append("]");
        return sb.toString();
    }
}
