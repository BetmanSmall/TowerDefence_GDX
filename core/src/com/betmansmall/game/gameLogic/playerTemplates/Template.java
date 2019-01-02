package com.betmansmall.game.gameLogic.playerTemplates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.XmlReader;
import com.betmansmall.game.gameLogic.mapLoader.AnimatedTiledMapTile;
import com.betmansmall.game.gameLogic.mapLoader.MapLoader;
import com.betmansmall.game.gameLogic.mapLoader.StaticTiledMapTile;
import com.betmansmall.game.gameLogic.mapLoader.TiledMapTile;

import java.util.Iterator;

/**
 * Created by betma on 06.11.2018.
 */

public class Template {
    public String templateName;
    public ObjectMap<String, String> properties;
    public ObjectMap<Integer, TiledMapTile> tiles;
    public ObjectMap<Integer, AnimatedTiledMapTile> animatedTiles;

    public Template() {
        Gdx.app.log("Template::Template()", "-- ");
        templateName = "NULL";
        properties = new ObjectMap<String, String>();
        tiles = new ObjectMap<Integer, TiledMapTile>();
        animatedTiles = new ObjectMap<Integer, AnimatedTiledMapTile>();
    }

    public void dispose() {
        Gdx.app.log("Template::dispose()", "-- ");
    }

    public void loadBasicTemplate(FileHandle templateFile) throws Exception {
    Gdx.app.log("Template::loadBasicTemplate()", "-- templateFile:" + templateFile);
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
            Gdx.app.log("Template::loadBasicTemplate()", "-- textureFile:" + textureFile);
            Gdx.app.log("Template::loadBasicTemplate()", "-- texture:" + texture);

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
            Gdx.app.log("Template::loadBasicTemplate()", "-- Could not load Template from " + templateFile.path() + " Exp:" + exp);
            throw new Exception("Template::loadBasicTemplate() -- Could not load Template from " + templateFile.path() + " Exp:" + exp);
        }
    }

    public void basicValidate() {
        if (properties.containsKey("templateName") || templateName != null) {
            Gdx.app.log("Template::basicValidate()", "-- templateName:" + templateName);
            Gdx.app.log("Template::basicValidate()", "-- tiles.size:" + tiles.size);
            Gdx.app.log("Template::basicValidate()", "-- animatedTiles.size:" + animatedTiles.size);
        }
    }

    public ObjectMap<String, String> getProperties() {
        return properties;
    }

    public String toStringProperties() {
        StringBuilder sb = new StringBuilder();
        sb.append("Properties:[");
//        Iterator<String> keys = properties.getKeys();
//        while (keys.hasNext()) {
        for (String key : properties.keys()) {
//            String key = keys.next();
            sb.append(key + ":" + properties.get(key) + ",");
        }
        sb.replace(sb.length-2, sb.length-1, "");
        sb.append("]");
        return sb.toString();
    }

    public String toStringBasicParam() {
        StringBuilder sb = new StringBuilder();
        sb.append("Template:[");
        sb.append("templateName:" + templateName);
        sb.append(",tiles.size:" + tiles.size);
        sb.append(",animatedTiles.size:" + animatedTiles.size);
        sb.append("]");
        return sb.toString();
    }
}
