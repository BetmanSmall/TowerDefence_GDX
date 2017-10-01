package com.betmansmall.game.server;

import com.betmansmall.game.gameLogic.Cell;

import java.io.Serializable;

/**
 * Created by betma on 11.09.2017.
 */

public class GameFieldData implements Serializable {
    public static boolean server;
    public Cell[][] field;

    public GameFieldData() {
        this.server = server;
    }
}
