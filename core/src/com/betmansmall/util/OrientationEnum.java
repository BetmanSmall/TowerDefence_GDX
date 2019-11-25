package com.betmansmall.util;

/**
 * @author Alexander_Kuzyakov on 25.11.2019.
 */
public enum OrientationEnum {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    public static OrientationEnum getOpposite(OrientationEnum base) {
        if(base == null) throw new NullPointerException("No opposite orientation for null");
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
        throw new IllegalArgumentException("Given orientation is not valid");
    }
}
