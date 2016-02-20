package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

/**
 * Created by betmansmall on 09.02.2016.
 */
public class TemplateForUnit {
    private int hp;

    private Array<TiledMapTile> idle;

    private Array<TiledMapTile> walkUp;
    private Array<TiledMapTile> walkUpRight;
    private Array<TiledMapTile> walkRight;
    private Array<TiledMapTile> walkDownRight;
    private Array<TiledMapTile> walkDown;
    private Array<TiledMapTile> walkDownLeft;
    private Array<TiledMapTile> walkLeft;
    private Array<TiledMapTile> walkUpLeft;

    private Array<TiledMapTile> deathUp;
    private Array<TiledMapTile> deathUpRight;
    private Array<TiledMapTile> deathRight;
    private Array<TiledMapTile> deathDownRight;
    private Array<TiledMapTile> deathDown;
    private Array<TiledMapTile> deathDownLeft;
    private Array<TiledMapTile> deathLeft;
    private Array<TiledMapTile> deathUpLeft;

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public Array<TiledMapTile> getDeathUpLeft() {
        return deathUpLeft;
    }

    public void setDeathUpLeft(Array<TiledMapTile> deathUpLeft) {
        this.deathUpLeft = deathUpLeft;
    }

    public Array<TiledMapTile> getDeathLeft() {
        return deathLeft;
    }

    public void setDeathLeft(Array<TiledMapTile> deathLeft) {
        this.deathLeft = deathLeft;
    }

    public Array<TiledMapTile> getDeathDownLeft() {
        return deathDownLeft;
    }

    public void setDeathDownLeft(Array<TiledMapTile> deathDownLeft) {
        this.deathDownLeft = deathDownLeft;
    }

    public Array<TiledMapTile> getDeathDown() {
        return deathDown;
    }

    public void setDeathDown(Array<TiledMapTile> deathDown) {
        this.deathDown = deathDown;
    }

    public Array<TiledMapTile> getDeathDownRight() {
        return deathDownRight;
    }

    public void setDeathDownRight(Array<TiledMapTile> deathDownRight) {
        this.deathDownRight = deathDownRight;
    }

    public Array<TiledMapTile> getDeathRight() {
        return deathRight;
    }

    public void setDeathRight(Array<TiledMapTile> deathRight) {
        this.deathRight = deathRight;
    }

    public Array<TiledMapTile> getDeathUpRight() {
        return deathUpRight;
    }

    public void setDeathUpRight(Array<TiledMapTile> deathUpRight) {
        this.deathUpRight = deathUpRight;
    }

    public Array<TiledMapTile> getDeathUp() {
        return deathUp;
    }

    public void setDeathUp(Array<TiledMapTile> deathUp) {
        this.deathUp = deathUp;
    }

    public Array<TiledMapTile> getWalkUpLeft() {
        return walkUpLeft;
    }

    public void setWalkUpLeft(Array<TiledMapTile> walkUpLeft) {
        this.walkUpLeft = walkUpLeft;
    }

    public Array<TiledMapTile> getWalkLeft() {
        return walkLeft;
    }

    public void setWalkLeft(Array<TiledMapTile> walkLeft) {
        this.walkLeft = walkLeft;
    }

    public Array<TiledMapTile> getWalkDownLeft() {
        return walkDownLeft;
    }

    public void setWalkDownLeft(Array<TiledMapTile> walkDownLeft) {
        this.walkDownLeft = walkDownLeft;
    }

    public Array<TiledMapTile> getWalkDown() {
        return walkDown;
    }

    public void setWalkDown(Array<TiledMapTile> walkDown) {
        this.walkDown = walkDown;
    }

    public Array<TiledMapTile> getWalkRight() {
        return walkRight;
    }

    public void setWalkRight(Array<TiledMapTile> walkRight) {
        this.walkRight = walkRight;
    }

    public Array<TiledMapTile> getWalkDownRight() {
        return walkDownRight;
    }

    public void setWalkDownRight(Array<TiledMapTile> walkDownRight) {
        this.walkDownRight = walkDownRight;
    }

    public Array<TiledMapTile> getWalkUpRight() {
        return walkUpRight;
    }

    public void setWalkUpRight(Array<TiledMapTile> walkUpRight) {
        this.walkUpRight = walkUpRight;
    }

    public Array<TiledMapTile> getWalkUp() {
        return walkUp;
    }

    public void setWalkUp(Array<TiledMapTile> walkUp) {
        this.walkUp = walkUp;
    }

    public Array<TiledMapTile> getIdle() {
        return idle;
    }

    public void setIdle(Array<TiledMapTile> idle) {
        this.idle = idle;
    }
}
