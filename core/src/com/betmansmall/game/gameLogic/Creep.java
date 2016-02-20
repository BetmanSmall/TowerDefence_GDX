package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;

import java.awt.Point;

/**
 * Created by betmansmall on 22.09.2015.
 */
public class  Creep {
    private int hp;
    private boolean alive;
    private TiledMapTileLayer collisionLayer;
    private TemplateForUnit template;

    private GridPoint2 position;

    public Creep() {

    }

    public Creep(GridPoint2 position, TiledMapTileLayer collisionLayer, TemplateForUnit unit) {
        setTemplate(unit);
        setCollisionLayer(collisionLayer);
        setAlive(true);
        setHp(unit.getHp());
        setPosition(position);
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        getCollisionLayer().setCell(position.x, position.y, cell);
    }

    public void moveTo(GridPoint2 position) {
        getCollisionLayer().setCell(this.position.x, this.position.y, null);
        this.position = position;
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        getCollisionLayer().setCell(position.x, position.y, cell);
        cell.setTile(getTemplate().getIdle().first());
    }

    public TiledMapTileLayer getCollisionLayer() {
        return collisionLayer;
    }

    public void setCollisionLayer(TiledMapTileLayer collisionLayer) {
        this.collisionLayer = collisionLayer;
    }

    public GridPoint2 getPosition() {
        return position;
    }

    public void setPosition(GridPoint2 position) {
        this.position = position;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public TemplateForUnit getTemplate() {
        return template;
    }

    public void setTemplate(TemplateForUnit template) {
        this.template = template;
    }
}
