package com.betmansmall.game.map.Editor;

import java.util.ArrayList;


public class ObjectGroup {
	private String name;
	
	private ArrayList<Object> Object;
	
	void setName(String name) {
		this.name = name;
	}
	String getName() {
		return this.name;
	}
	
	void setObject(ArrayList<Object> Object) {
		this.Object = Object;
	}
	ArrayList<Object> getObjet() {
		return this.Object;
	}
}
