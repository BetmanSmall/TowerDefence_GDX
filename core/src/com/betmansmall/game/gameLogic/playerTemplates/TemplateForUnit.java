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
    private Faction faction;

    public Integer bounty;
    public String  factionName;
    public Integer healthPoints;
    public String  name;
    public Float   speed;
    public String  type;

    public ObjectMap<String, AnimatedTiledMapTile> animations;

    public TemplateForUnit(TiledMapTileSet tileSet) {
        try {
            this.bounty =       Integer.parseInt(tileSet.getProperties().get("bounty", String.class));
            this.factionName =  tileSet.getProperties().get("factionName", String.class);
            this.healthPoints = Integer.parseInt(tileSet.getProperties().get("healthPoints", String.class));
            this.name =         tileSet.getProperties().get("name", String.class);
            this.speed =        Float.parseFloat(tileSet.getProperties().get("speed", String.class));
            this.type =         tileSet.getProperties().get("type", String.class);

            this.speed = this.speed*2;
        } catch(Exception exp) {
            Gdx.app.error("TemplateForUnit::TemplateForUnit()", " -- Exp: " + exp + " Cheak the file!");
        }

        this.animations = new ObjectMap<String, AnimatedTiledMapTile>();

        setAnimationFrames(tileSet);
        validate();
    }

    private void setAnimationFrames(TiledMapTileSet tileSet) {
        for(TiledMapTile tile: tileSet) {
            if(tile instanceof AnimatedTiledMapTile) {
                AnimatedTiledMapTile aTile = (AnimatedTiledMapTile) tile;
                String actionAndDirection = aTile.getProperties().get("actionAndDirection", String.class);
                if(actionAndDirection != null) {
                    if(actionAndDirection.contains("idle"))
                        setIdleAnimationFrames(actionAndDirection, aTile);
                    else if(actionAndDirection.contains("walk"))
                        setWalkAnimationFrames(actionAndDirection, aTile);
                    else if(actionAndDirection.contains("death"))
                        setDeathAnimationFrames(actionAndDirection, aTile);
                }
            }
        }
    }

    private void setIdleAnimationFrames(String actionAndDirection, AnimatedTiledMapTile aTile) {
//        idle = aTile.getTextureRegion();
        if(actionAndDirection.equals("idle_" + Direction.UP)) {
            animations.put("idle_" + Direction.UP, aTile);
        } else if(actionAndDirection.equals("idle_" + Direction.UP_RIGHT)) {
            animations.put("idle_" + Direction.UP_RIGHT, aTile);
            animations.put("idle_" + Direction.UP_LEFT, flipAnimatedTiledMapTile(aTile));
        } else if(actionAndDirection.equals("idle_" + Direction.RIGHT)) {
            animations.put("idle_" + Direction.RIGHT, aTile);
            animations.put("idle_" + Direction.LEFT, flipAnimatedTiledMapTile(aTile));
        } else if(actionAndDirection.equals("idle_" + Direction.DOWN_RIGHT)) {
            animations.put("idle_" + Direction.DOWN_RIGHT, aTile);
            animations.put("idle_" + Direction.DOWN_LEFT, flipAnimatedTiledMapTile(aTile));
        } else if(actionAndDirection.equals("idle_" + Direction.DOWN)) {
            animations.put("idle_" + Direction.DOWN, aTile);
        }
    }
    private void setWalkAnimationFrames(String actionAndDirection, AnimatedTiledMapTile aTile) {
        if(actionAndDirection.equals("walk3_" + Direction.UP)) {
            animations.put("walk_" + Direction.UP, aTile);
        } else if(actionAndDirection.equals("walk3_" + Direction.UP_RIGHT)) {
            animations.put("walk_" + Direction.UP_RIGHT, aTile);
            animations.put("walk_" + Direction.UP_LEFT, flipAnimatedTiledMapTile(aTile));
        } else if(actionAndDirection.equals("walk3_" + Direction.RIGHT)) {
            animations.put("walk_" + Direction.RIGHT, aTile);
            animations.put("walk_" + Direction.LEFT, flipAnimatedTiledMapTile(aTile));
        } else if(actionAndDirection.equals("walk3_" + Direction.DOWN_RIGHT)) {
            animations.put("walk_" + Direction.DOWN_RIGHT, aTile);
            animations.put("walk_" + Direction.DOWN_LEFT, flipAnimatedTiledMapTile(aTile));
        } else if(actionAndDirection.equals("walk3_" + Direction.DOWN)) {
            animations.put("walk_" + Direction.DOWN, aTile);
        }
    }
    private void setDeathAnimationFrames(String actionAndDirection, AnimatedTiledMapTile aTile) {
        if(actionAndDirection.equals("death1_" + Direction.UP)) {
            animations.put("death_" + Direction.UP, aTile);
        } else if(actionAndDirection.equals("death1_" + Direction.UP_RIGHT)) {
            animations.put("death_" + Direction.UP_RIGHT, aTile);
            animations.put("death_" + Direction.UP_LEFT, flipAnimatedTiledMapTile(aTile));
        } else if(actionAndDirection.equals("death1_" + Direction.RIGHT)) {
            animations.put("death_" + Direction.RIGHT, aTile);
            animations.put("death_" + Direction.LEFT, flipAnimatedTiledMapTile(aTile));
        } else if(actionAndDirection.equals("death1_" + Direction.DOWN_RIGHT)) {
            animations.put("death_" + Direction.DOWN_RIGHT, aTile);
            animations.put("death_" + Direction.DOWN_LEFT, flipAnimatedTiledMapTile(aTile));
        } else if(actionAndDirection.equals("death1_" + Direction.DOWN)) {
            animations.put("death_" + Direction.DOWN, aTile);
        }
    }

    private AnimatedTiledMapTile flipAnimatedTiledMapTile(AnimatedTiledMapTile animatedTiledMapTile) {
        Array<StaticTiledMapTile> frames = new Array<StaticTiledMapTile>(animatedTiledMapTile.getFrameTiles());
        for(int k = 0; k < frames.size; k++) {
            TextureRegion textureRegion = new TextureRegion(frames.get(k).getTextureRegion());
            textureRegion.flip(true, false);
            StaticTiledMapTile frame = new StaticTiledMapTile(textureRegion);
            frames.set(k, frame);
        }
        IntArray intervals = new IntArray(animatedTiledMapTile.getAnimationIntervals());
        return new AnimatedTiledMapTile(intervals, frames);
    }

    private void validate() {
        // Need cheak range values
        if(this.bounty == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'bounty'! Check the file");
        else if(this.factionName == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'factionName'! Check the file");
        else if(this.healthPoints == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'healthPoints'! Check the file");
        else if(this.name == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'name'! Check the file");
        else if(this.speed == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'speed'! Check the file");
        else if(this.type == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'type'! Check the file");

        for(String key: animations.keys()) {
            Gdx.app.log("TemplateForUnit::validate()", "-- Dir:" + key + " Lenght:" + animations.get(key).getFrameTiles().length);
        }
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }
    public String getFactionName() {
        return factionName;
    }
}
