package com.betmansmall.game.gameLogic.playerTemplates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by betmansmall on 09.02.2016.
 */
public class TemplateForUnit {
    private Faction  faction;

    private String   factionName;
    private String   name;
    private Integer  hp;
    private String   type;
    private float interval;

    private AnimatedTiledMapTile idle;
    private ObjectMap<String, AnimatedTiledMapTile> walks;
    private ObjectMap<String, AnimatedTiledMapTile> deaths;

    public TemplateForUnit(TiledMapTileSet tileSet) {
        this.factionName =  tileSet.getProperties().get("faction_name", String.class);
        this.name =         tileSet.getProperties().get("name", String.class);
        this.hp =           new Integer(tileSet.getProperties().get("health_point", String.class));
        this.type =         tileSet.getProperties().get("type", String.class);
//        this.interval = 0.02f;
        walks = new ObjectMap<String, AnimatedTiledMapTile>();
        deaths = new ObjectMap<String, AnimatedTiledMapTile>();

        setAnimationFrames(tileSet);
        validate();
    }

    private void setAnimationFrames(TiledMapTileSet tileSet) {
        for(TiledMapTile tile: tileSet) {
            if(tile instanceof AnimatedTiledMapTile) {
                AnimatedTiledMapTile aTile = (AnimatedTiledMapTile) tile;
                String actionAndDirection = tile.getProperties().get("actionAndDirection", String.class);
                if(actionAndDirection != null) {
                    if(actionAndDirection.contains("idle"))
                        setIdleAnimationFrames(actionAndDirection, aTile);
                    if(actionAndDirection.contains("walk"))
                        setWalkAnimationFrames(actionAndDirection, aTile);
                    else if(actionAndDirection.contains("death"))
                        setDeathAnimationFrames(actionAndDirection, aTile);
                }
            }
        }

    }

    private void setIdleAnimationFrames(String actionAndDirection, AnimatedTiledMapTile tile) {
        idle = tile;
    }

    private void setWalkAnimationFrames(String actionAndDirection, AnimatedTiledMapTile tile) {
        if(actionAndDirection.equals("walk3_up")) {
//            walkUp = tile;
            walks.put("walkUp", tile);
        } else if(actionAndDirection.equals("walk3_up_right")) {
            walks.put("walkUpRight", tile);

            Array<StaticTiledMapTile> frames = new Array<StaticTiledMapTile>(tile.getFrameTiles());
            for(int k = 0; k < frames.size; k++) {
                StaticTiledMapTile tmpFrame = frames.get(k);
                TextureRegion textureRegion = tmpFrame.getTextureRegion();
                textureRegion.flip(true, false);
                tmpFrame.setTextureRegion(textureRegion);
                frames.set(k, tmpFrame);
            }
            IntArray intervals = new IntArray(tile.getAnimationIntervals());
            walks.put("walkDownRight", new AnimatedTiledMapTile(intervals, frames));
        } else if(actionAndDirection.equals("walk3_right")) {
            walks.put("walkRight", tile);

            Array<StaticTiledMapTile> frames = new Array<StaticTiledMapTile>(tile.getFrameTiles());
            for(int k = 0; k < frames.size; k++) {
                StaticTiledMapTile tmpFrame = frames.get(k);
                TextureRegion textureRegion = tmpFrame.getTextureRegion();
                textureRegion.flip(true, false);
                tmpFrame.setTextureRegion(textureRegion);
                frames.set(k, tmpFrame);
            }
            IntArray intervals = new IntArray(tile.getAnimationIntervals());
            walks.put("walkLeft", new AnimatedTiledMapTile(intervals, frames));
        } else if(actionAndDirection.equals("walk3_down_right")) {
            walks.put("walkDownRight", tile);

            Array<StaticTiledMapTile> frames = new Array<StaticTiledMapTile>(tile.getFrameTiles());
            for(int k = 0; k < frames.size; k++) {
                StaticTiledMapTile tmpFrame = frames.get(k);
                TextureRegion textureRegion = tmpFrame.getTextureRegion();
                textureRegion.flip(true, false);
                tmpFrame.setTextureRegion(textureRegion);
                frames.set(k, tmpFrame);
            }
            IntArray intervals = new IntArray(tile.getAnimationIntervals());
            walks.put("walkDownLeft", new AnimatedTiledMapTile(intervals, frames));

        } else if(actionAndDirection.equals("walk3_down")) {
            walks.put("walkDown", tile);
        }
    }

    private void setDeathAnimationFrames(String actionAndDirection, AnimatedTiledMapTile tile) {
        if(actionAndDirection.equals("death1_up")) {
//            deathUp = tile;
            deaths.put("deathUp", tile);
        } else if(actionAndDirection.equals("death1_up_right")) {
            deaths.put("deathUpRight", tile);

            Array<StaticTiledMapTile> frames = new Array<StaticTiledMapTile>(tile.getFrameTiles());
            for(int k = 0; k < frames.size; k++) {
                StaticTiledMapTile tmpFrame = frames.get(k);
                TextureRegion textureRegion = tmpFrame.getTextureRegion();
                textureRegion.flip(true, false);
                tmpFrame.setTextureRegion(textureRegion);
                frames.set(k, tmpFrame);
            }
            IntArray intervals = new IntArray(tile.getAnimationIntervals());
            deaths.put("deathDownRight", new AnimatedTiledMapTile(intervals, frames));
        } else if(actionAndDirection.equals("death1_right")) {
            deaths.put("deathRight", tile);

            Array<StaticTiledMapTile> frames = new Array<StaticTiledMapTile>(tile.getFrameTiles());
            for(int k = 0; k < frames.size; k++) {
                StaticTiledMapTile tmpFrame = frames.get(k);
                TextureRegion textureRegion = tmpFrame.getTextureRegion();
                textureRegion.flip(true, false);
                tmpFrame.setTextureRegion(textureRegion);
                frames.set(k, tmpFrame);
            }
            IntArray intervals = new IntArray(tile.getAnimationIntervals());
            deaths.put("deathLeft", new AnimatedTiledMapTile(intervals, frames));
        } else if(actionAndDirection.equals("death1_down_right")) {
            deaths.put("deathDownRight", tile);

            Array<StaticTiledMapTile> frames = new Array<StaticTiledMapTile>(tile.getFrameTiles());
            for(int k = 0; k < frames.size; k++) {
                StaticTiledMapTile tmpFrame = frames.get(k);
                TextureRegion textureRegion = tmpFrame.getTextureRegion();
                textureRegion.flip(true, false);
                tmpFrame.setTextureRegion(textureRegion);
                frames.set(k, tmpFrame);
            }
            IntArray intervals = new IntArray(tile.getAnimationIntervals());
            deaths.put("deathDownLeft", new AnimatedTiledMapTile(intervals, frames));

        } else if(actionAndDirection.equals("death1_down")) {
            deaths.put("deathDown", tile);
        }
    }

    private void validate() {
        if(this.factionName == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'factionName'! Check the file");
        else if(this.name == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'name'! Check the file");
        else if(this.hp == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'hp'! Check the file");
        else if(this.type == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'type'! Check the file");

        if(idle == null) {
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'idle'! Check the file");
        }
//        else if(walks.get("walkUp") == null) {
//            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'walkUp'! Check the file");
//        }
//        for (String key: walks.keys()) {
//            Gdx.app.log("TemplateForUnit::validate()", "-- Dir:" + key + " TextureRegX:" + walks.get(key).getTextureRegion().getRegionX() + " TextureRegY:" + walks.get(key).getTextureRegion().getRegionY());
//        }
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }

    public String getFactionName() {
        return factionName;
    }

    public int getHp() {
        return hp;
    }

    public TiledMapTile getCurrentIdleFrame() {
        if(idle != null) {
            Gdx.app.log("TemplateForUnit::getCurrentIdleFrame()", "-- CurrentFrameId:" + idle.getCurrentFrameIndex());
            return idle.getCurrentFrame();
        }
        return null;
    }
}
