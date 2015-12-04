package com.betmansmall.game.map.Editor;

import java.util.ArrayList;


public class Map {
	
	private Integer width, height, tileWidth;
	
	private ArrayList<TileSet> TileSet;
	private ArrayList<Layer> Layer;
	private ArrayList<ObjectGroup> ObjectGroup;
	
	void setWidth(Integer width) {
		this.width = width;
	}
	Integer getWidth() {
		return this.width;
	}
	
	void setHeight(Integer height) {
		this.height = height;
	}
	Integer getHeight() {
		return this.height;
	}
	
	void setTileWidth(Integer tileWidth) {
		this.tileWidth = tileWidth;
	}
	Integer getTileWidth() {
		return this.tileWidth;
	}
	
	void setTileSet(ArrayList<TileSet> TileSet) {
		this.TileSet = TileSet;
	}
	ArrayList<TileSet> getTileSet() {
		return this.TileSet;
	}
	
	void setLayer(ArrayList<Layer> Layer) {
		this.Layer = Layer;
	}
	ArrayList<Layer> getLayer() {
		return this.Layer;
	}
	
	void setObjectGroup(ArrayList<ObjectGroup> ObjectGroup) {
		this.ObjectGroup = ObjectGroup;
	}
	ArrayList<ObjectGroup> getObjectGroup() {
		return this.ObjectGroup;
	}
}
