package com.betmansmall.maps.jsons;

import com.betmansmall.maps.xmls.Image;
import com.betmansmall.maps.xmls.Terraintypes;
import com.betmansmall.maps.xmls.Tile;

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

    public Image image;
    public Terraintypes terraintypes;
    public Tile[] tile;
}
