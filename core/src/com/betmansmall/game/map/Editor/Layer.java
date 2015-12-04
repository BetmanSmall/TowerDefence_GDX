package com.betmansmall.game.map.Editor;

import java.util.ArrayList;


public class Layer {
	private String name;
	private Integer width, height;
	
	private ArrayList<Data> Data;
	
	void setName(String name) {
		this.name = name;
	}
	String getName() {
		return this.name;
	}
	
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
	
	void setData(ArrayList<Data> Data) {
		this.Data = Data;
	}
	ArrayList<Data> getData() {
		return this.Data;
	}
}
