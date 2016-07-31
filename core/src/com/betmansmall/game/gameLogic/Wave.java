package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.utils.Array;

import java.util.ArrayDeque;

/**
 * Created by betmansmall on 29.03.2016.
 */
public class Wave {
    public ArrayDeque<Integer> units;

    Wave(Array<Integer> units) {
        this.units = new ArrayDeque<Integer>();
        for(int k = 0; k < units.size; k++) {
            this.units.add(units.get(k));
        }
    }
}
