package com.betmansmall.game.gameLogic.playerTemplates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.utils.Array;

/**
 * Created by betmansmall on 22.02.2016.
 */
public class TemplateForTower {
    private Faction faction;

    private String factionName;
    private String name;
    private float attack;
    private Integer radius;
    private Integer size;
    private String type;
    private Integer damage;

    private TiledMapTile idleTile;
    private Array<TiledMapTile> ammunition;

    public TemplateForTower(TiledMapTileSet tileSet) {
        this.factionName =  tileSet.getProperties().get("factionName", String.class);
        this.name =         tileSet.getProperties().get("name", String.class);
        this.attack =       0.5f;
        this.radius =       1;//new Integer(tileSet.getProperties().get("radius", String.class));
        this.size =         new Integer(tileSet.getProperties().get("size", String.class));
        this.type =         tileSet.getProperties().get("type", String.class);
        this.damage =       50;

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
        if(this.factionName == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'factionName'! Check the file");
        else if(this.name == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'name'! Check the file");
        else if(this.attack == 0)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'attack'! Check the file");
        else if(this.radius == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'radius'! Check the file");
        else if(this.size == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'size'! Check the file");
        else if(this.type == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'type'! Check the file");

        if(idleTile == null)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'idleTile'! Check the file");
        else if(ammunition.size == 0)
            Gdx.app.error("TemplateForUnit::validate()", "-- Can't get 'arrows'! Check the file");
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }

    public String getFactionName() {
        return factionName;
    }

    public TiledMapTile getIdleTile() {
        return idleTile;
    }

    public float getAttack() {
        return attack;
    }

    public Integer getRadius() {
        return radius;
    }
    public Integer getDamage() {
        return damage;
    }
}
