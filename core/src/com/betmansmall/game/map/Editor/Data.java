package com.betmansmall.game.map.Editor;
import java.util.ArrayList;


public class Data {
	private ArrayList<Tile> Tile;
	
	void setTile(ArrayList<Tile> Tile){
		this.Tile = Tile;
	}
	
	ArrayList<Tile> getTile(){
		return this.Tile;
	}
}
