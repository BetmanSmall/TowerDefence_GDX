package com.betmansmall.game.gameLogic.playerTemplates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by betmansmall on 22.02.2016.
 */
public class TemplateForTower {
    private Faction faction;

    public Integer  ammoDistance;
    public Float    ammoSize;
    public Float    ammoSpeed;
    public Integer  cost;
    public Integer  damage;
    public String   factionName;
    public String   name;
    public Integer  radius;
    public Float    reloadTime;
    public Integer  size;
    public String   type;

    public TiledMapTile idleTile;
    public ObjectMap<String, TiledMapTile> ammunitionPictures;

    public TemplateForTower(TiledMapTileSet tileSet) {
        try {
            this.ammoDistance = Integer.parseInt(tileSet.getProperties().get("ammoDistance", String.class));
            this.ammoSize =     Float.parseFloat(tileSet.getProperties().get("ammoSize", String.class));
            this.ammoSpeed =    Float.parseFloat(tileSet.getProperties().get("ammoSpeed", String.class));
            this.cost =         Integer.parseInt(tileSet.getProperties().get("cost", String.class));
            this.damage =       Integer.parseInt(tileSet.getProperties().get("damage", String.class));
            this.factionName =  tileSet.getProperties().get("factionName", String.class);
            this.name =         tileSet.getProperties().get("name", String.class);
            this.radius =       Integer.parseInt(tileSet.getProperties().get("radius", String.class));
            this.reloadTime =   Float.parseFloat(tileSet.getProperties().get("reloadTime", String.class));
            this.size =         Integer.parseInt(tileSet.getProperties().get("size", String.class));
            this.type =         tileSet.getProperties().get("type", String.class);
        } catch(Exception exp) {
            Gdx.app.error("TemplateForTower::TemplateForTower()", " -- Exp: " + exp + " Cheak the file!");
        }

        this.ammunitionPictures = new ObjectMap<String, TiledMapTile>();

        setTiledMapTiles(tileSet);
        validate();
    }

    private void setTiledMapTiles(TiledMapTileSet tileSet) {
        for(TiledMapTile tile: tileSet) {
            String tileName = tile.getProperties().get("tileName", String.class);
            if (tileName != null) {
                if(tileName.equals("idleTile")) {
                    idleTile = tile;
                } else if(tileName.contains("ammo_")) {
                    setAmmoTiles(tileName, tile);
                }
            }
        }
    }

    private void setAmmoTiles(String tileName, TiledMapTile tile) {
        if(tile != null) {
            if(tileName.equals("ammo_" + Direction.UP)) {
                ammunitionPictures.put("ammo_" + Direction.UP, tile);
            } else if(tileName.equals("ammo_" + Direction.UP_RIGHT)) {
                ammunitionPictures.put("ammo_" + Direction.UP_RIGHT, tile);
                ammunitionPictures.put("ammo_" + Direction.UP_LEFT, flipTiledMapTile(tile));
            } else if(tileName.equals("ammo_" + Direction.RIGHT)) {
                ammunitionPictures.put("ammo_" + Direction.RIGHT, tile);
                ammunitionPictures.put("ammo_" + Direction.LEFT, flipTiledMapTile(tile));
            } else if(tileName.equals("ammo_" + Direction.DOWN_RIGHT)) {
                ammunitionPictures.put("ammo_" + Direction.DOWN_RIGHT, tile);
                ammunitionPictures.put("ammo_" + Direction.DOWN_LEFT, flipTiledMapTile(tile));
            } else if(tileName.equals("ammo_" + Direction.DOWN)) {
                ammunitionPictures.put("ammo_" + Direction.DOWN, tile);
            }
        }
    }

    private TiledMapTile flipTiledMapTile(TiledMapTile tiledMapTile) {
        TextureRegion textureRegion = new TextureRegion(tiledMapTile.getTextureRegion());
        textureRegion.flip(true, false);
        return new StaticTiledMapTile(textureRegion);
    }

    private void validate() {
        // Need cheak range values
        if(this.ammoDistance == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'ammoDistance'! Check the file");
        else if(this.ammoSize == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'ammoSize'! Check the file");
        else if(this.ammoSpeed == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'ammoSpeed'! Check the file");
        else if(this.cost == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'cost'! Check the file");
        else if(this.damage == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'damage'! Check the file");
        else if(this.factionName == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'factionName'! Check the file");
        else if(this.name == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'name'! Check the file");
        else if(this.radius == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'radius'! Check the file");
        else if(this.reloadTime == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'reloadTime'! Check the file");
        else if(this.size == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'size'! Check the file");
        else if(this.type == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'type'! Check the file");

        if(idleTile == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'idleTile'! Check the file");
        else if(ammunitionPictures.size == 0)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'ammo'! Check the file");
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }
    public String getFactionName() {
        return factionName;
    }
}
