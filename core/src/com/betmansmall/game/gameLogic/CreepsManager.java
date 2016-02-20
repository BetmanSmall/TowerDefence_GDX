package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Андрей on 20.02.2016.
 */
public class CreepsManager {
    private static Array<Creep> creepsArray;

    public static Array<Creep> getCreepsArray() {
        return creepsArray;
    }

    public static void setCreepsArray(Array<Creep> creepsArray) {
        CreepsManager.creepsArray = creepsArray;
    }

    public static Creep getCreep(int id) {
        return creepsArray.get(id);
    }

    public static Creep getCreep(GridPoint2 position) {
        for(int i=0;i<creepsArray.size;i++) {
            if(creepsArray.get(i).getPosition().x == position.x &&
                    creepsArray.get(i).getPosition().y == position.y) {
                return creepsArray.get(i);
            }
        }
        return null;
    }

    public static void addCreeps(Creep creep) {
        creepsArray.add(creep);
    }

    public static int amountCreeps() {
        return creepsArray.size;
    }
}
