package com.betmansmall.game;

import com.badlogic.gdx.Gdx;

/**
 * Created by betma on 17.11.2018.
 */

public enum GameType {
    LittleGame("LittleGame"),
    TowerDefence("WidgetController"),
    OrthogonalityTowerDefence("OrthogonalityTowerDefence");

    private final String text;

    private GameType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static GameType getType(String type) {
        for (GameType t : GameType.values()) {
            if (t.name().equals(type)) {
                return t;
            }
        }
        Gdx.app.error("GameType::getType()", "-- BadType:" + type);
        return null;
    }
}
