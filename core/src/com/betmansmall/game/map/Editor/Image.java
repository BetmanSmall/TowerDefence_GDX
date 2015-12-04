package com.betmansmall.game.map.Editor;

public class Image {
	private String source;
	private String trans;
	private Integer width, height;
	
	void setSource(String source) {
		this.source = source;
	}
	String getSource() {
		return this.source;
	}
	
	void setTrans(String trans) {
		this.trans = trans;
	}
	String getTrans() {
		return this.trans;
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
}
