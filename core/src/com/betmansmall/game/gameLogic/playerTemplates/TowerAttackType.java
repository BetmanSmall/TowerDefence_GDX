package com.betmansmall.game.gameLogic.playerTemplates;

import com.badlogic.gdx.Gdx;

/**
 * Created by betma on 19.01.2017.
 */

public enum TowerAttackType {
    FireBall("FireBall"),

    Pit("Pit"),
    Melee("Melee"),
    Range("Range"),

    RangeFly("RangeFly");

    private final String text;
    TowerAttackType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static TowerAttackType getType(String type) {
        for (TowerAttackType t : TowerAttackType.values()) {
            if (t.name().equals(type)) {
                return t;
            }
        }
        Gdx.app.error("TowerAttackType::getType()", "-- BadType:" + type);
        return null;
    }
}
