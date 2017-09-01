package com.betmansmall.game.gameLogic.playerTemplates;

import com.badlogic.gdx.Gdx;

/**
 * Created by betma on 19.01.2017.
 */

public enum ShellAttackType {
    AutoTarget("AutoTarget"),
    FirstTarget("FirstTarget"),
    SingleTarget("SingleTarget"),
    MultipleTarget("MultipleTarget"),
    MassAddEffect("MassAddEffect");

    private final String text;

    private ShellAttackType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static ShellAttackType getType(String type) {
        for (ShellAttackType t : ShellAttackType.values()) {
            if (t.name().equals(type)) {
                return t;
            }
        }
        Gdx.app.error("ShellAttackType", "getType(" + type + "); -- Bad type!");
        return null;
    }
}
