package com.betmansmall.maps;

import com.badlogic.gdx.utils.Array;

public class TilesetConfig {
    private String texturePath;
    private int tileWidth;
    private int tileHeight;
    private Array<Array<String>> terrainDefs;

    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
    }

    public void setTileWidth(int tileWidth) {
        this.tileWidth = tileWidth;
    }

    public void setTileHeight(int tileHeight) {
        this.tileHeight = tileHeight;
    }

    public void setTerrainDefs(Array<Array<String>> terrainDefs) {
        this.terrainDefs = terrainDefs;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public Array<Array<String>> getTerrainDefs() {
        return terrainDefs;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TilesetConfig{");
        sb.append("texturePath='").append(texturePath).append('\'');
        sb.append(", tileWidth=").append(tileWidth);
        sb.append(", tileHeight=").append(tileHeight);
        sb.append(", terrainDefs=").append(terrainDefs);
        sb.append('}');
        return sb.toString();
    }
}
