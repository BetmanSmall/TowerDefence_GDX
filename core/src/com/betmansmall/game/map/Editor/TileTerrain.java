package com.betmansmall.game.map.Editor;

public class TileTerrain {
	private Integer id;
	private String terrain;
	
	void setId(Integer id) {
		this.id = id;
	}
	Integer getId() {
		return this.id;
	}
	
	void setTerrain(String terrain) {
		this.terrain = terrain;
	}
	String getTerrain() {
		return this.terrain;
	}
}
