package com.betmansmall.game.gameLogic.playerTemplates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

import java.util.StringTokenizer;

/**
 * Created by betmansmall on 09.02.2016.
 */
public class TemplateForUnit {
    private Faction faction;
    private String templateName;

    public Integer bounty;
    public String factionName;
    public Integer healthPoints;
    public String name;
    public Float speed;
    public String type;

    public ObjectMap<String, AnimatedTiledMapTile> animations;

    public TemplateForUnit(FileHandle templateFile) throws Exception {
        try {
            XmlReader xmlReader = new XmlReader();
            Element templateORtileset = xmlReader.parse(templateFile);
            this.templateName = templateORtileset.getAttribute("name");

            int firstgid = templateORtileset.getIntAttribute("firstgid", 1);
            int tilewidth = templateORtileset.getIntAttribute("tilewidth", 0);
            int tileheight = templateORtileset.getIntAttribute("tileheight", 0);
            int spacing = templateORtileset.getIntAttribute("spacing", 0);
            int margin = templateORtileset.getIntAttribute("margin", 0);

            Element properties = templateORtileset.getChildByName("properties");
            if (properties != null) {
                for (Element property : properties.getChildrenByName("property")) {
                    String key = property.getAttribute("name");
                    String value = property.getAttribute("value");
                    if (key.equals("bounty")) {
                        this.bounty = Integer.parseInt(value);
                    } else if (key.equals("factionName")) {
                        this.factionName = value;
                    } else if (key.equals("healthPoints")) {
                        this.healthPoints = Integer.parseInt(value);
                    } else if (key.equals("name")) {
                        this.name = value;
                    } else if (key.equals("speed")) {
                        this.speed = Float.parseFloat(value);
                    } else if (key.equals("type")) {
                        this.type = value;
                    }
                }
            }
            Element imageElement = templateORtileset.getChildByName("image");
            String source = imageElement.getAttribute("source");
            FileHandle textureFile = getRelativeFileHandle(templateFile, source);
            Texture texture = new Texture(Gdx.files.internal(textureFile.path()));

            int stopWidth = texture.getWidth() - tilewidth;
            int stopHeight = texture.getHeight() - tileheight;
            int id = firstgid;

            ObjectMap<Integer, TiledMapTile> tiles = new ObjectMap<Integer, TiledMapTile>();
            for (int y = margin; y <= stopHeight; y += tileheight + spacing) {
                for (int x = margin; x <= stopWidth; x += tilewidth + spacing) {
                    TextureRegion tileRegion = new TextureRegion(texture, x, y, tilewidth, tileheight);
                    TiledMapTile tile = new StaticTiledMapTile(tileRegion);
                    tile.setId(id);
                    tiles.put(id++, tile);
                }
            }

            Array<Element> tileElements = templateORtileset.getChildrenByName("tile");

            animations = new ObjectMap<String, AnimatedTiledMapTile>();
            Array<AnimatedTiledMapTile> animatedTiles = new Array<AnimatedTiledMapTile>();

            for (Element tileElement : tileElements) {
                int localtid = tileElement.getIntAttribute("id", 0);
                TiledMapTile tile = tiles.get(localtid);
                if (tile != null) {
                    Element propertiesElement = tileElement.getChildByName("properties");
                    if (propertiesElement != null) {
                        for (Element property : propertiesElement.getChildrenByName("property")) {
                            String name = property.getAttribute("name", null);
                            String value = property.getAttribute("value", null);
                            if (value == null) {
                                value = property.getText();
                            }
                            tile.getProperties().put(name, value);
                        }
                    }

                    Element animationElement = tileElement.getChildByName("animation");
                    if (animationElement != null) {
                        Array<StaticTiledMapTile> staticTiles = new Array<StaticTiledMapTile>();
                        IntArray intervals = new IntArray();
                        for (Element frameElement : animationElement.getChildrenByName("frame")) {
                            staticTiles.add((StaticTiledMapTile) tiles.get(frameElement.getIntAttribute("tileid")));
                            intervals.add(frameElement.getIntAttribute("duration"));
                        }

                        AnimatedTiledMapTile animatedTile = new AnimatedTiledMapTile(intervals, staticTiles);
                        animatedTile.setId(tile.getId());
                        animatedTiles.add(animatedTile);
                        tile = animatedTile;

                        String actionAndDirection = tile.getProperties().get("actionAndDirection", String.class);
                        if(actionAndDirection != null) {
                            animations.put(actionAndDirection, animatedTile);
                        }
                    }
                }
            }

            validate();
        } catch (Exception exp) {
            Gdx.app.log("TemplateForUnit::TemplateForUnit()", " -- Could not load TemplateForUnit from " + templateFile.path());
            throw new Exception("TemplateForUnit::TemplateForUnit() -- Could not load TemplateForUnit from " + templateFile.path());
        }
    }

    public TemplateForUnit(TiledMapTileSet tileSet) {
        try {
            this.templateName = tileSet.getName();
            this.bounty = Integer.parseInt(tileSet.getProperties().get("bounty", String.class));
            this.factionName = tileSet.getProperties().get("factionName", String.class);
            this.healthPoints = Integer.parseInt(tileSet.getProperties().get("healthPoints", String.class));
            this.name = tileSet.getProperties().get("name", String.class);
            this.speed = Float.parseFloat(tileSet.getProperties().get("speed", String.class));
            this.type = tileSet.getProperties().get("type", String.class);

            this.speed = this.speed * 2;
        } catch (Exception exp) {
            Gdx.app.error("TemplateForUnit::TemplateForUnit()", " -- Exp: " + exp + " Cheak the file!");
        }

        this.animations = new ObjectMap<String, AnimatedTiledMapTile>();

        setAnimationFrames(tileSet);
        validate();
    }

    private void setAnimationFrames(TiledMapTileSet tileSet) {
        for (TiledMapTile tile : tileSet) {
            if (tile instanceof AnimatedTiledMapTile) {
                AnimatedTiledMapTile aTile = (AnimatedTiledMapTile) tile;
                String actionAndDirection = aTile.getProperties().get("actionAndDirection", String.class);
                if (actionAndDirection != null) {
                    if (actionAndDirection.contains("idle"))
                        setIdleAnimationFrames(actionAndDirection, aTile);
                    else if (actionAndDirection.contains("walk"))
                        setWalkAnimationFrames(actionAndDirection, aTile);
                    else if (actionAndDirection.contains("death"))
                        setDeathAnimationFrames(actionAndDirection, aTile);
                }
            }
        }
    }

    private void setIdleAnimationFrames(String actionAndDirection, AnimatedTiledMapTile aTile) {
//        idle = aTile.getTextureRegion();
        if (actionAndDirection.equals("idle_" + Direction.UP)) {
            animations.put("idle_" + Direction.UP, aTile);
        } else if (actionAndDirection.equals("idle_" + Direction.UP_RIGHT)) {
            animations.put("idle_" + Direction.UP_RIGHT, aTile);
            animations.put("idle_" + Direction.UP_LEFT, flipAnimatedTiledMapTile(aTile));
        } else if (actionAndDirection.equals("idle_" + Direction.RIGHT)) {
            animations.put("idle_" + Direction.RIGHT, aTile);
            animations.put("idle_" + Direction.LEFT, flipAnimatedTiledMapTile(aTile));
        } else if (actionAndDirection.equals("idle_" + Direction.DOWN_RIGHT)) {
            animations.put("idle_" + Direction.DOWN_RIGHT, aTile);
            animations.put("idle_" + Direction.DOWN_LEFT, flipAnimatedTiledMapTile(aTile));
        } else if (actionAndDirection.equals("idle_" + Direction.DOWN)) {
            animations.put("idle_" + Direction.DOWN, aTile);
        }
    }

    private void setWalkAnimationFrames(String actionAndDirection, AnimatedTiledMapTile aTile) {
        if (actionAndDirection.equals("walk3_" + Direction.UP)) {
            animations.put("walk_" + Direction.UP, aTile);
        } else if (actionAndDirection.equals("walk3_" + Direction.UP_RIGHT)) {
            animations.put("walk_" + Direction.UP_RIGHT, aTile);
            animations.put("walk_" + Direction.UP_LEFT, flipAnimatedTiledMapTile(aTile));
        } else if (actionAndDirection.equals("walk3_" + Direction.RIGHT)) {
            animations.put("walk_" + Direction.RIGHT, aTile);
            animations.put("walk_" + Direction.LEFT, flipAnimatedTiledMapTile(aTile));
        } else if (actionAndDirection.equals("walk3_" + Direction.DOWN_RIGHT)) {
            animations.put("walk_" + Direction.DOWN_RIGHT, aTile);
            animations.put("walk_" + Direction.DOWN_LEFT, flipAnimatedTiledMapTile(aTile));
        } else if (actionAndDirection.equals("walk3_" + Direction.DOWN)) {
            animations.put("walk_" + Direction.DOWN, aTile);
        }
    }

    private void setDeathAnimationFrames(String actionAndDirection, AnimatedTiledMapTile aTile) {
        if (actionAndDirection.equals("death1_" + Direction.UP)) {
            animations.put("death_" + Direction.UP, aTile);
        } else if (actionAndDirection.equals("death1_" + Direction.UP_RIGHT)) {
            animations.put("death_" + Direction.UP_RIGHT, aTile);
            animations.put("death_" + Direction.UP_LEFT, flipAnimatedTiledMapTile(aTile));
        } else if (actionAndDirection.equals("death1_" + Direction.RIGHT)) {
            animations.put("death_" + Direction.RIGHT, aTile);
            animations.put("death_" + Direction.LEFT, flipAnimatedTiledMapTile(aTile));
        } else if (actionAndDirection.equals("death1_" + Direction.DOWN_RIGHT)) {
            animations.put("death_" + Direction.DOWN_RIGHT, aTile);
            animations.put("death_" + Direction.DOWN_LEFT, flipAnimatedTiledMapTile(aTile));
        } else if (actionAndDirection.equals("death1_" + Direction.DOWN)) {
            animations.put("death_" + Direction.DOWN, aTile);
        }
    }

    private AnimatedTiledMapTile flipAnimatedTiledMapTile(AnimatedTiledMapTile animatedTiledMapTile) {
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

    private void validate() {
        // Need check range values

        if (this.templateName != null) {
            Gdx.app.error("TemplateForUnit::validate()", "-- Load TemplateForUnit: " + this.templateName);
        }
        if (this.bounty == null) {
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'bounty'! Check the file");
        } else if (this.factionName == null) {
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'factionName'! Check the file");
        } else if (this.healthPoints == null) {
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'healthPoints'! Check the file");
        } else if (this.name == null) {
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'name'! Check the file");
        } else if (this.speed == null) {
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'speed'! Check the file");
        } else if (this.type == null) {
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'type'! Check the file");
        }

        for (String key : animations.keys()) {
            Gdx.app.log("TemplateForUnit::validate()", "-- Dir:" + key + " Lenght:" + animations.get(key).getFrameTiles().length);
        }
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }

    public String getFactionName() {
        return factionName;
    }

    public String getTemplateName() {
        return this.templateName;
    }

    protected static FileHandle getRelativeFileHandle(FileHandle file, String path) {
        StringTokenizer tokenizer = new StringTokenizer(path, "\\/");
        FileHandle result = file.parent();
        while (tokenizer.hasMoreElements()) {
            String token = tokenizer.nextToken();
            if (token.equals(".."))
                result = result.parent();
            else {
                result = result.child(token);
            }
        }
        return result;
    }
}
