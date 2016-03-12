package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.betmansmall.game.gameLogic.GridNav.GridNav;
import com.betmansmall.game.gameLogic.GridNav.Options;
import com.betmansmall.game.gameLogic.GridNav.Vertex;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;

import java.util.ArrayDeque;

/**
 * Created by betmansmall on 22.09.2015.
 */
public class  Creep {
    private GridPoint2 position;
    private int hp;
    private TemplateForUnit templateForUnit;
    private TextureRegion curentFrame;
    private float speed;
    private float elapsedTime;
    private ArrayDeque<Vertex> route;

    private TiledMapTileLayer layer;

    public Creep(GridPoint2 position, TiledMapTileLayer layer, TemplateForUnit templateForUnit) {
        this.position = position;
        this.hp = templateForUnit.getHp();
        this.templateForUnit = templateForUnit;
        this.curentFrame = templateForUnit.getCurrentIdleFrame().getTextureRegion();
        this.speed = templateForUnit.getSpeed();
        this.elapsedTime = 0;

        this.layer = layer;

        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        getCollisionLayer().setCell(position.x, position.y, cell);
    }

    public void moveTo(GridPoint2 position) {
        getCollisionLayer().setCell(this.position.x, this.position.y, null);
        this.position = position;
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        getCollisionLayer().setCell(position.x, position.y, cell);
        cell.setTile(templateForUnit.getCurrentIdleFrame());
    }

    public TiledMapTileLayer getCollisionLayer() {
        return layer;
    }

    public GridPoint2 getPosition() {
        return position;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getHp() {
        return hp;
    }

    public TemplateForUnit getTemplateForUnit() {
        return templateForUnit;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(float elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public ArrayDeque<Vertex> getRoute() {
        return route;
    }

    public void setRoute(ArrayDeque<Vertex> route) {
        this.route = route;
    }

    public void setRoute(GridNav gridNav, GridPoint2 position, GridPoint2 exitPoint) {
       this.route = gridNav.route(new int[]{position.x, position.y}, new int[]{exitPoint.x, exitPoint.y},
                Options.ASTAR, Options.EUCLIDEAN_HEURISTIC, true);
    }
}
