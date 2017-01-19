package com.betmansmall.game.gameLogic.playerTemplates;

/**
 * Created by betma on 19.01.2017.
 */

public enum ShellEffectType {
    None("None"),
    FreezeEffect("FreezeEffect"),
    FireEffect("FireEffect");

    private final String text;

    private ShellEffectType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static ShellEffectType getType(String type) {
        for (ShellEffectType t : ShellEffectType.values()) {
            if (t.name().equals(type)) {
                return t;
            }
        }
        return null;
    }
}
