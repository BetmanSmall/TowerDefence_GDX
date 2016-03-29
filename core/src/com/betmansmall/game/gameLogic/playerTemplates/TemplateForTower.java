package com.betmansmall.game.gameLogic.playerTemplates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.utils.Array;

/**
 * Created by betmansmall on 22.02.2016.
 */
public class TemplateForTower {
    private Faction faction;

    public Integer  cost;
    public Integer  damage;
    public String   factionName;
    public String   name;
    public Integer  radius;
    public Float    reloadTime;
    public Integer  size;
    public String   type;

    public Array<TiledMapTile> ammunition;
    public TiledMapTile idleTile;

    public TemplateForTower(TiledMapTileSet tileSet) {
        try {
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

        this.ammunition = new Array<TiledMapTile>();

        setTiledMapTiles(tileSet);
        validate();
    }

    private void setTiledMapTiles(TiledMapTileSet tileSet) {
        for(TiledMapTile tile: tileSet) {
            String tileName = tile.getProperties().get("tileName", String.class);
            if (tileName != null) {
                if(tileName.equals("idleTile")) {
                    idleTile = tile;
                } else if(tileName.contains("arrow")) {
                    ammunition.add(tile);
                }
            }
        }
    }

    private void validate() {
        // Need cheak range values
        if(this.cost == null)
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
        else if(ammunition.size == 0)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'ammo'! Check the file");
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }
    public String getFactionName() {
        return factionName;
    }
}
