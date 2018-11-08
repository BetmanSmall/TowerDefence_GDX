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
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.betmansmall.game.gameLogic.mapLoader.AnimatedTile;
import com.betmansmall.game.gameLogic.mapLoader.MapLoader;
import com.betmansmall.game.gameLogic.mapLoader.StaticTile;
import com.betmansmall.game.gameLogic.mapLoader.Tile;

import java.util.StringTokenizer;

/**
 * Created by betmansmall on 09.02.2016.
 */
public class TemplateForUnit extends Template {
//    private Faction faction;
    public String factionName;
    public String name;

    public Float healthPoints;
    public Float bounty;
    public Float cost;
    public Float speed;
    public String type;

    public ObjectMap<String, AnimatedTile> animations;

    public TemplateForUnit(FileHandle templateFile) throws Exception {
        try {
            animations = new ObjectMap<String, AnimatedTile>();
            loadBasicTemplate(templateFile);
            specificLoad();
            validate();
        } catch (Exception exp) {
            Gdx.app.log("TemplateForUnit::TemplateForUnit()", "-- Could not load TemplateForUnit from " + templateFile.path() + " Exp:" + exp);
            throw new Exception("TemplateForUnit::TemplateForUnit() -- Could not load TemplateForUnit from " + templateFile.path() + " Exp:" + exp);
        }
    }

//    void loadExplosion(SimpleTemplate explosion) {
//        if (explosion != null) {
//            for (AnimatedTile animatedTile : explosion.animatedTiles.values()) {
//                String tileName = animatedTile.getProperties().get("tileName", null);
//                if (tileName != null) {
//                    if(tileName.contains("fireball_")) {
//                        animations(tileName.replace("fireball_", "ammo_"), animatedTile);
//                    }
//                }
//            }
//        }
//    }

    void specificLoad() {
        for (AnimatedTile animatedTile : animatedTiles.values()) {
            String actionAndDirection = animatedTile.getProperties().get("actionAndDirection", null);
            if (actionAndDirection != null) {
                animations.put(actionAndDirection, animatedTile);
                if(actionAndDirection.equals("walk_" + Direction.UP_RIGHT)) {
                    animations.put("walk_" + Direction.UP_LEFT, flipAnimatedTiledMapTile(animatedTile));
                } else if(actionAndDirection.equals("walk_" + Direction.RIGHT)) {
                    animations.put("walk_" + Direction.LEFT, flipAnimatedTiledMapTile(animatedTile));
                } else if(actionAndDirection.equals("walk_" + Direction.DOWN_RIGHT)) {
                    animations.put("walk_" + Direction.DOWN_LEFT, flipAnimatedTiledMapTile(animatedTile));
                }
                if(actionAndDirection.equals("death_" + Direction.UP_RIGHT)) {
                    animations.put("death_" + Direction.UP_LEFT, flipAnimatedTiledMapTile(animatedTile));
                } else if(actionAndDirection.equals("death_" + Direction.RIGHT)) {
                    animations.put("death_" + Direction.LEFT, flipAnimatedTiledMapTile(animatedTile));
                } else if(actionAndDirection.equals("death_" + Direction.DOWN_RIGHT)) {
                    animations.put("death_" + Direction.DOWN_LEFT, flipAnimatedTiledMapTile(animatedTile));
                }
            }
        }
    }

//    private void setAnimationFrames(TiledMapTileSet tileSet) {
//        for (TiledMapTile tile : tileSet) {
//            if (tile instanceof AnimatedTiledMapTile) {
//                AnimatedTiledMapTile aTile = (AnimatedTiledMapTile) tile;
//                String actionAndDirection = aTile.getProperties().get("actionAndDirection", String.class);
//                if (actionAndDirection != null) {
//                    if (actionAndDirection.contains("idle"))
//                        setIdleAnimationFrames(actionAndDirection, aTile);
//                    else if (actionAndDirection.contains("walk"))
//                        setWalkAnimationFrames(actionAndDirection, aTile);
//                    else if (actionAndDirection.contains("death"))
//                        setDeathAnimationFrames(actionAndDirection, aTile);
//                }
//            }
//        }
//    }

    private void setIdleAnimationFrames(String actionAndDirection, AnimatedTile aTile) {
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

    private void setWalkAnimationFrames(String actionAndDirection, AnimatedTile aTile) {
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

    private void setDeathAnimationFrames(String actionAndDirection, AnimatedTile aTile) {
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

    private AnimatedTile flipAnimatedTiledMapTile(AnimatedTile animatedTiledMapTile) {
        Array<StaticTile> frames = new Array<StaticTile>(animatedTiledMapTile.getFrameTiles());
        for (int k = 0; k < frames.size; k++) {
            TextureRegion textureRegion = new TextureRegion(frames.get(k).getTextureRegion());
            textureRegion.flip(true, false);
            StaticTile frame = new StaticTile(textureRegion);
            frames.set(k, frame);
        }
        IntArray intervals = new IntArray(animatedTiledMapTile.getAnimationIntervals());
        return new AnimatedTile(intervals, frames);
    }

    private void validate() {
        basicValidate();
        // Need check range values
        if (!properties.containsKey("factionName")) {
            Gdx.app.log("TemplateForUnit::validate()", "-- NotFound: factionName");
        } else {
            factionName = properties.get("factionName");
        }
        if (!properties.containsKey("name")) {
            Gdx.app.log("TemplateForUnit::validate()", "-- NotFound: name");
        } else {
            name = properties.get("name");
        }
        if (!properties.containsKey("healthPoints")) {
            Gdx.app.log("TemplateForUnit::validate()", "-- NotFound: healthPoints");
        } else {
            healthPoints = Float.parseFloat(properties.get("healthPoints"));
        }
        if (!properties.containsKey("bounty")) {
            Gdx.app.log("TemplateForUnit::validate()", "-- NotFound: bounty");
        } else {
            bounty = Float.parseFloat(properties.get("bounty"));
        }
        if (!properties.containsKey("cost")) {
            Gdx.app.log("TemplateForUnit::validate()", "-- NotFound: cost");
        } else {
            cost = Float.parseFloat(properties.get("cost"));
        }
        if (!properties.containsKey("speed")) {
            Gdx.app.log("TemplateForUnit::validate()", "-- NotFound: speed");
        } else {
            speed = Float.parseFloat(properties.get("speed"));
        }
        if (!properties.containsKey("type")) {
            Gdx.app.log("TemplateForUnit::validate()", "-- NotFound: type");
        } else {
            type = properties.get("type");
        }

        if(animations.size == 0)
            Gdx.app.log("TemplateForUnit::validate()", "-- NotFound: animations");

//        for (String key : animations.keys()) {
//            Gdx.app.log("TemplateForUnit::validate()", "-- Dir:" + key + " length:" + animations.get(key).getFrameTiles().length);
//        }
//        Gdx.app.log("TemplateForUnit::validate()", "-- " + toString(true));
    }

    public String toString() {
        return toString(false);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("TemplateForUnit[");
        sb.append(toStringBasicParam());
        if(full) {
            sb.append(",factionName:" + factionName);
            sb.append(",name:" + name);
            sb.append(",healthPoints:" + healthPoints);
            sb.append(",bounty:" + bounty);
            sb.append(",cost:" + cost);
            sb.append(",speed:" + speed);
            sb.append(",type:" + type);
            sb.append(",animations.size:" + animations.size);
        }
        sb.append("]");
        return sb.toString();
    }
}
