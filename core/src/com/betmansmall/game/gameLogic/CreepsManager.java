package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Андрей on 20.02.2016.
 */
public class CreepsManager {
    private Array<Creep> creepsArray;

    public CreepsManager(int amountCreeps) {
        creepsArray = new Array<Creep>(amountCreeps);
    }

    public Creep createCreep(GridPoint2 position, TiledMapTileLayer layer, TemplateForUnit templateForUnit) {
        creepsArray.add(new Creep(position, layer, templateForUnit));
        return creepsArray.get(creepsArray.size);
    }

//    public Array<Creep> getCreepsArray() {
//        return creepsArray;
//    }

    public Creep getCreep(int id) {
        return creepsArray.get(id);
    }

    public Creep getCreep(GridPoint2 position) {
        for(int i=0; i < creepsArray.size; i++) {
            if(creepsArray.get(i).getPosition().x == position.x && creepsArray.get(i).getPosition().y == position.y) {
                return creepsArray.get(i);
            }
        }
        return null;
    }

//    public void addCreeps(Creep creep) {
//        creepsArray.add(creep);
//    }

    public int amountCreeps() {
        return creepsArray.size;
    }
}
