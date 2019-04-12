package com.betmansmall.game.gameLogic.playerTemplates;

import com.badlogic.gdx.Gdx;

/**
 * Created by betma on 19.01.2017.
 */

public enum TowerShellType {
//    FireBall("FireBall"),

    AutoTarget("AutoTarget"),
    FirstTarget("FirstTarget"),
    SingleTarget("SingleTarget"),
    MultipleTarget("MultipleTarget"),

    MassAddEffect("MassAddEffect");

    private final String text;
    TowerShellType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static TowerShellType getType(String type) {
        for (TowerShellType t : TowerShellType.values()) {
            if (t.name().equals(type)) {
                return t;
            }
        }
        Gdx.app.error("TowerShellType::getType()", "-- BadType:" + type);
        return null;
    }
}
