package com.betmansmall.game.map.Editor;

import java.util.ArrayList;


public class TileSet {
	private Integer firstGid;
	private String name;
	private Integer tileWidth, tileHeight;
	private Integer spacing, margin;
	
	private Image image;
	private ArrayList<TerrainTypes> TerrainTypes;
	private ArrayList<TileTerrain> TileTerrain;
	private ArrayList<Properties> Properties;
	
	void setFirstGid(Integer firstGid) {
		this.firstGid = firstGid;
	}
	Integer getfirstGid() {
		return this.firstGid;
	}
	
	void setName(String name) {
		this.name = name;
	}
	String getName() {
		return this.name;
	}
	
	void setTileWidth(Integer tileWidth) {
		this.tileWidth = tileWidth;
	}
	Integer getTileWidth() {
		return this.tileWidth;
	}
	
	void setTileHeight(Integer tileHeight) {
		this.tileHeight = tileHeight;
	}
	Integer getTileHeight() {
		return this.tileHeight;
	}
	
	void setSpacing(Integer spacing) {
		this.spacing = spacing;
	}
	Integer getSpacing() {
		return this.spacing;
	}
	
	void setMargin(Integer margin) {
		this.margin = margin;
	}
	Integer getMargin() {
		return this.margin;
	}
	
	void setImage(Image image) {
		this.image = image;
	}
	Image getImage() {
		return this.image;
	}
	
	void setTerrainTypes(ArrayList<TerrainTypes> TerrainTypes) {
		this.TerrainTypes = TerrainTypes;
	}
	ArrayList<TerrainTypes> getTerrainTypes() {
		return this.TerrainTypes;
	}
	
	void setTileTerrain(ArrayList<TileTerrain> TileTerrain) {
		this.TileTerrain = TileTerrain;
	}
	ArrayList<TileTerrain> getTileTerrain() {
		return this.TileTerrain;
	}
	
	void setProperties(ArrayList<Properties> Properties) {
		this.Properties = Properties;
	}
	ArrayList<Properties> getProperties() {
		return this.Properties;
	}
}
