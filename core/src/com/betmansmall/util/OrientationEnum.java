package com.betmansmall.util;

/**
 * @author Alexander on 25.11.2019.
 */
public enum OrientationEnum {
    UP("Up"),
    DOWN("Down"),
    LEFT("Left"),
    RIGHT("Right");

    public final String VALUE;

    OrientationEnum(String value) {
        VALUE = value;
    }

    private static OrientationEnum getOpposite(OrientationEnum base) {
        switch(base) {
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
        }
        return null;
    }
}
