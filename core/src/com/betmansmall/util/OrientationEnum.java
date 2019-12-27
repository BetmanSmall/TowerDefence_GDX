package com.betmansmall.util;

import com.badlogic.gdx.utils.Align;

/**
 * Enum of four orientation sides.
 * Can give opposite side and be translated from/to LibGDX {@link Align}.
 *
 * @author Alexander on 25.11.2019.
 */
public enum OrientationEnum {
    UP("Up", Align.top, false),
    DOWN("Down", Align.bottom, false),
    LEFT("Left", Align.left, true),
    RIGHT("Right", Align.right, true);

    public final String VALUE;
    public final int ALIGN;
    public final boolean VERTICAL;

    OrientationEnum(String value, int align, boolean vertical) {
        VALUE = value;
        ALIGN = align;
        VERTICAL = vertical;
    }

    private OrientationEnum getOpposite() {
        switch (this) {
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
            default:
                return null;
        }
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
