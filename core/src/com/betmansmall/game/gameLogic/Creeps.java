package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

/**
 * Created by betmansmall on 08.02.2016.
 */
public class Creeps {
    Array<Creep> creeps;
    int size, amount;

    Creeps(int newSize) {
        createMass(newSize);
    }

    public void createMass(int newSize) {
        if(creeps == null) {
            creeps = new Array<Creep>(); // Capacity????????
            size = newSize;
            amount = 0;
        } else {
            deleteMass();
            createMass(newSize);
        }
    }

    public void deleteMass() {
        if(creeps != null) {
            creeps.clear();
            creeps = null;
        }
    }

    public int getSize() {
        return size;
    }

    public int getAmount() {
        return amount;
    }

    public Creep getCreep(int index) {
        return creeps.get(index);
    }

    public Creep getCreep(int x, int y) {
        for(int k = 0; k < amount; k++) {
            Creep localCreep = creeps.get(k);
            int localX = localCreep.getPosition().x;
            int localY = localCreep.getPosition().y;

            if(localX == x && localY == y) {
                return localCreep;
            }
        }
        return null;
    }

//    public Creep createCreep(int coorByCellX, int coorByCellY) { //, int coorByMapX, int coorByMapY, DefaultUnit* unit)
//
//    }
}
