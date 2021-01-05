package com.betmansmall.maps.jsons;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.GridPoint2;
import com.betmansmall.maps.xmls.Grid;
import com.betmansmall.maps.xmls.Image;
import com.betmansmall.maps.xmls.TerrainTypes;
import com.betmansmall.maps.xmls.Tile;
import com.google.common.base.MoreObjects;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class TileSet {
    public String version;
    public String tiledversion;
    public String name;
    public String tilewidth;
    public String tileheight;
    public String spacing;
    public String margin;
    public String tilecount;
    public String columns;

    public GridPoint2 tileoffset;
    public Grid grid;
    public Image image;
    @SerializedName("terraintypes")
    public TerrainTypes terrainTypes;
    public Tile[] tile;
    public HashMap<Integer, Tile> tileHashMap;

    public MapProperties getProperties() {
        MapProperties mapProperties = new MapProperties();
        mapProperties.put("version", version);
        mapProperties.put("tiledversion", tiledversion);
        mapProperties.put("name", name);
        mapProperties.put("tilewidth", tilewidth);
        mapProperties.put("tileheight", tileheight);
        mapProperties.put("spacing", spacing);
        mapProperties.put("margin", margin);
        mapProperties.put("tilecount", tilecount);
        mapProperties.put("columns", columns);
        return mapProperties;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).omitNullValues()
                .add("version", version)
                .add("tiledversion", tiledversion)
                .add("name", name)
                .add("tilewidth", tilewidth)
                .add("tileheight", tileheight)
                .add("spacing", spacing)
                .add("margin", margin)
                .add("tilecount", tilecount)
                .add("columns", columns)
                .add("tileoffset", tileoffset)
                .add("grid", grid)
                .add("image", image)
                .add("terrainTypes", terrainTypes)
//                .add("tile", tile)
//                .add("tileHashMap", tileHashMap)
                .toString();
    }
}
