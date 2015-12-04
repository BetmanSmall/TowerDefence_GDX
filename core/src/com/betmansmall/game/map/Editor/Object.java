package com.betmansmall.game.map.Editor;

public class Object {
	private Integer id;
	private String name;
	private Integer gid;
	private Integer x;
	private Integer y;
	
	void setId(Integer id) {
		this.id = id;
	}
	Integer getId() {
		return this.id;
	}
	
	void setName(String name) {
		this.name = name;
	}
	String getName() {
		return this.name;
	}
	
	void setGid(Integer gid) {
		this.gid = gid;
	}
	Integer getGid() {
		return this.gid;
	}
	
	void setX(Integer x) {
		this.x = x;
	}
	Integer getX() {
		return this.x;
	}
	
	void setY(Integer y) {
		this.y = y;
	}
	Integer getY() {
		return this.y;
	}
}
