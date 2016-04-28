package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.utils.ObjectMap;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.GridNav.Vertex;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;

/**
 * Created by betmansmall on 29.03.2016.
 */
public class ProjecTile {
    public float x, y;
    public Creep creep;
    public float ammoDistance;
    public float ammoSize;

    public TemplateForTower templateForTower;

    public ObjectMap<String, TiledMapTile> ammunitionPictures;

    ProjecTile(float x, float y, Creep creep, TemplateForTower templateForTower) {
        this.x = x;
        this.y = y;
        this.creep = creep;
        this.ammoDistance = templateForTower.ammoDistance;
        this.ammoSize = templateForTower.ammoSize;

        this.templateForTower = templateForTower;

        this.ammunitionPictures = templateForTower.ammunitionPictures;
    }

    public boolean move() {
        float destX = creep.graphicalCoordinateX;
        float destY = creep.graphicalCoordinateY;

        if(x != destX || y != destY) {
            if(x == destX) {
                if(y < destY) {
                    y += ammoDistance;
                } else if (y > destY) {
                    y -= ammoDistance;
                }
            } else if(y == destY) {
                if(x < destX) {
                    x += ammoDistance;
                } else if(x > destX) {
                    x -= ammoDistance;
                }
            } else if(x < destX && y > destY) {
                x += ammoDistance/2;
                y -= ammoDistance/2;
            } else if(x > destX && y > destY) {
                x -= ammoDistance/2;
                y -= ammoDistance/2;
            } else if(x < destX && y < destY) {
                x += ammoDistance/2;
                y += ammoDistance/2;
            } else if(x > destX && y < destY) {
                x -= ammoDistance/2;
                y += ammoDistance/2;
            }
            return true;
        } else {
            return false;
        }
//        return false;
    }
}
