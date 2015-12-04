package com.betmansmall.game.map.Editor;

import java.util.ArrayList;


public class TerrainTypes {
	private ArrayList<Terrain> Terrain;
	
	void setTerrain(ArrayList<Terrain> Terrain) {
		this.Terrain = Terrain;
	}
	ArrayList<Terrain> getTerrain() {
		return this.Terrain;
	}
}
