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

    public Integer bounty;
    public String  factionName;
    public Integer hp;
    public String  name;
    public Float   speed;
    public String  type;

    public TextureRegion idle;
    public ObjectMap<String, AnimatedTiledMapTile> walks;
    public ObjectMap<String, AnimatedTiledMapTile> deaths;

    public TemplateForUnit(TiledMapTileSet tileSet) {
        try {
            this.bounty =       Integer.parseInt(tileSet.getProperties().get("bounty", String.class));
            this.factionName =  tileSet.getProperties().get("faction_name", String.class);
            this.hp =           Integer.parseInt(tileSet.getProperties().get("health_point", String.class));
            this.name =         tileSet.getProperties().get("name", String.class);
            this.speed =        Float.parseFloat(tileSet.getProperties().get("speed", String.class));
            this.type =         tileSet.getProperties().get("type", String.class);
        } catch(Exception exp) {
            Gdx.app.error("TemplateForUnit::TemplateForUnit()", " -- Exp: " + exp + " Cheak the file!");
        }

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
        idle = tile.getTextureRegion();
    }
    private void setWalkAnimationFrames(String actionAndDirection, AnimatedTiledMapTile tile) {
        if(actionAndDirection.equals("walk3_" + Direction.UP)) {
            walks.put("walk_" + Direction.UP, tile);
        } else if(actionAndDirection.equals("walk3_" + Direction.UP_RIGHT)) {
            walks.put("walk_" + Direction.UP_RIGHT, tile);

            Array<StaticTiledMapTile> frames = new Array<StaticTiledMapTile>(tile.getFrameTiles());
            for(int k = 0; k < frames.size; k++) {
                StaticTiledMapTile tmpFrame = frames.get(k);
                TextureRegion textureRegion = tmpFrame.getTextureRegion();
                textureRegion.flip(true, false);
                tmpFrame.setTextureRegion(textureRegion);
                frames.set(k, tmpFrame);
            }
            IntArray intervals = new IntArray(tile.getAnimationIntervals());
            walks.put("walk_" + Direction.UP_LEFT, new AnimatedTiledMapTile(intervals, frames));
        } else if(actionAndDirection.equals("walk3_" + Direction.RIGHT)) {
            walks.put("walk_" + Direction.RIGHT, tile);

            Array<StaticTiledMapTile> frames = new Array<StaticTiledMapTile>(tile.getFrameTiles());
            for(int k = 0; k < frames.size; k++) {
                StaticTiledMapTile tmpFrame = frames.get(k);
                TextureRegion textureRegion = tmpFrame.getTextureRegion();
                textureRegion.flip(true, false);
                tmpFrame.setTextureRegion(textureRegion);
                frames.set(k, tmpFrame);
            }
            IntArray intervals = new IntArray(tile.getAnimationIntervals());
            walks.put("walk_" + Direction.LEFT, new AnimatedTiledMapTile(intervals, frames));
        } else if(actionAndDirection.equals("walk3_" + Direction.DOWN_RIGHT)) {
            walks.put("walk_" + Direction.DOWN_RIGHT, tile);

            Array<StaticTiledMapTile> frames = new Array<StaticTiledMapTile>(tile.getFrameTiles());
            for(int k = 0; k < frames.size; k++) {
                StaticTiledMapTile tmpFrame = frames.get(k);
                TextureRegion textureRegion = tmpFrame.getTextureRegion();
                textureRegion.flip(true, false);
                tmpFrame.setTextureRegion(textureRegion);
                frames.set(k, tmpFrame);
            }
            IntArray intervals = new IntArray(tile.getAnimationIntervals());
            walks.put("walk_" + Direction.DOWN_LEFT, new AnimatedTiledMapTile(intervals, frames));

        } else if(actionAndDirection.equals("walk3_" + Direction.DOWN)) {
            walks.put("walk_" + Direction.DOWN, tile);
        }
    }
    private void setDeathAnimationFrames(String actionAndDirection, AnimatedTiledMapTile tile) {
        if(actionAndDirection.equals("death1_" + Direction.UP)) {
            deaths.put("death_" + Direction.UP, tile);
        } else if(actionAndDirection.equals("death1_" + Direction.UP_RIGHT)) {
            deaths.put("death_" + Direction.UP_RIGHT, tile);

            Array<StaticTiledMapTile> frames = new Array<StaticTiledMapTile>(tile.getFrameTiles());
            for(int k = 0; k < frames.size; k++) {
                StaticTiledMapTile tmpFrame = frames.get(k);
                TextureRegion textureRegion = tmpFrame.getTextureRegion();
                textureRegion.flip(true, false);
                tmpFrame.setTextureRegion(textureRegion);
                frames.set(k, tmpFrame);
            }
            IntArray intervals = new IntArray(tile.getAnimationIntervals());
            deaths.put("death_" + Direction.UP_LEFT, new AnimatedTiledMapTile(intervals, frames));
        } else if(actionAndDirection.equals("death1_" + Direction.RIGHT)) {
            deaths.put("death_" + Direction.RIGHT, tile);

            Array<StaticTiledMapTile> frames = new Array<StaticTiledMapTile>(tile.getFrameTiles());
            for(int k = 0; k < frames.size; k++) {
                StaticTiledMapTile tmpFrame = frames.get(k);
                TextureRegion textureRegion = tmpFrame.getTextureRegion();
                textureRegion.flip(true, false);
                tmpFrame.setTextureRegion(textureRegion);
                frames.set(k, tmpFrame);
            }
            IntArray intervals = new IntArray(tile.getAnimationIntervals());
            deaths.put("death_" + Direction.LEFT, new AnimatedTiledMapTile(intervals, frames));
        } else if(actionAndDirection.equals("death1_" + Direction.DOWN_RIGHT)) {
            deaths.put("death_" + Direction.DOWN_RIGHT, tile);

            Array<StaticTiledMapTile> frames = new Array<StaticTiledMapTile>(tile.getFrameTiles());
            for(int k = 0; k < frames.size; k++) {
                StaticTiledMapTile tmpFrame = frames.get(k);
                TextureRegion textureRegion = tmpFrame.getTextureRegion();
                textureRegion.flip(true, false);
                tmpFrame.setTextureRegion(textureRegion);
                frames.set(k, tmpFrame);
            }
            IntArray intervals = new IntArray(tile.getAnimationIntervals());
            deaths.put("death_" + Direction.DOWN_LEFT, new AnimatedTiledMapTile(intervals, frames));

        } else if(actionAndDirection.equals("death1_" + Direction.DOWN)) {
            deaths.put("death_" + Direction.DOWN, tile);
        }
    }

    private void validate() {
        // Need cheak range values
        if(this.bounty == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'bounty'! Check the file");
        else if(this.factionName == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'factionName'! Check the file");
        else if(this.hp == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'hp'! Check the file");
        else if(this.name == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'name'! Check the file");
        else if(this.speed == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'speed'! Check the file");
        else if(this.type == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'type'! Check the file");

        if(idle == null) {
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'idle'! Check the file");
        }
//        else if(walks.get("walkUp") == null) {
//            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'walkUp'! Check the file");
//        }
        for (String key: walks.keys()) {
            Gdx.app.log("TemplateForUnit::validate()", "-- Dir:" + key + " Lenght:" + walks.get(key).getFrameTiles().length);
        }
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }
    public String getFactionName() {
        return factionName;
    }
}
