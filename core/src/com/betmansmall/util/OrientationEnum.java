package com.betmansmall.util;

import com.badlogic.gdx.utils.Align;

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

    public static OrientationEnum fromAlign(int align) {
        switch (align) {
            case Align.left:
            case Align.topLeft:
            case Align.bottomLeft:
                return LEFT;
            case Align.right:
            case Align.topRight:
            case Align.bottomRight:
                return RIGHT;
            case Align.top:
                return UP;
            case Align.bottom:
                return DOWN;
        }
        throw new IllegalArgumentException("Value " + align + " is not part of com.badlogic.gdx.utils.Align");

    }
}
