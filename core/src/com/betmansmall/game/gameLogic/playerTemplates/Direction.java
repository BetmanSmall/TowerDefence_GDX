package com.betmansmall.game.gameLogic.playerTemplates;

/**
 * Created by betmansmall on 27.03.2016.
 */
public enum Direction {
    UP("UP"),
    UP_RIGHT("UP_RIGHT"),
    RIGHT("RIGHT"),
    DOWN_RIGHT("DOWN_RIGHT"),
    DOWN("DOWN"),
    DOWN_LEFT("DOWN_LEFT"),
    LEFT("LEFT"),
    UP_LEFT("UP_LEFT");

    private final String text;

    private Direction(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
