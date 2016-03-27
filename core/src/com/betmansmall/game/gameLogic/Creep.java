package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.GridNav.Vertex;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;

import java.util.ArrayDeque;

/**
 * Created by betmansmall on 22.09.2015.
 */
public class  Creep {
    private GridPoint2 position;
    private int hp;
    private float speed;
    private float elapsedTime;

    private ArrayDeque<Vertex> route;

    private TemplateForUnit templateForUnit;
    private TextureRegion curentFrame;

    public Creep(GridPoint2 position, ArrayDeque<Vertex> route, TemplateForUnit templateForUnit) {
        this.position = position;
        this.hp = templateForUnit.getHp();
        this.speed = templateForUnit.getSpeed();
        this.elapsedTime = 0;

        this.route = route;

        this.templateForUnit = templateForUnit;
        this.curentFrame = templateForUnit.getCurrentIdleFrame().getTextureRegion();
    }

    public void dispose() {
        position = null;
        route = null;
        templateForUnit = null;
        curentFrame = null;
    }

    public GridPoint2 move() {
        if(route != null && !route.isEmpty()) {
            Vertex nextCoordinate = route.pollFirst();
            position.set(nextCoordinate.getX(), nextCoordinate.getY());
            return position;
        } else {
            dispose();
            return null;
        }
    }

//    public void setPosition(GridPoint2 position) {
//        this.position = position;
//    }

    public GridPoint2 getPosition() {
        return position;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getHp() {
        return hp;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }

    public void setElapsedTime(float elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public float getElapsedTime() {
        return elapsedTime;
    }

    public void setRoute(ArrayDeque<Vertex> route) {
        this.route = route;
    }

    public ArrayDeque<Vertex> getRoute() {
        return route;
    }

    public TemplateForUnit getTemplateForUnit() {
        return templateForUnit;
    }

    public TextureRegion getCurentFrame() {
        return curentFrame;
    }
}
