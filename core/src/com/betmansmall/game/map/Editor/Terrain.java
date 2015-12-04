package com.betmansmall.game.map.Editor;

public class Terrain {
	private String name;
	private Integer tile;
	
	void setName(String name) {
		this.name = name;
	}
	String getName() {
		return this.name;
	}
	
	void setTile(Integer tile) {
		this.tile = tile;
	}
	Integer getTile() {
		return this.tile;
	}
}
