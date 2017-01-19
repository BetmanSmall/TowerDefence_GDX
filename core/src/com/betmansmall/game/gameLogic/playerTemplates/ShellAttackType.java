package com.betmansmall.game.gameLogic.playerTemplates;

/**
 * Created by betma on 19.01.2017.
 */

public enum ShellAttackType {
    None("None"),
    SingleTarget("SingleTarget"),
    MultipleTarget("MultipleTarget");

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
        return null;
    }
}
