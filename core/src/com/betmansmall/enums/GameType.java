package com.betmansmall.enums;

import com.badlogic.gdx.Gdx;

/**
 * Created by betma on 17.11.2018.
 */

public enum GameType {
    ProtoServer("ProtoServer"),
    LittleGame("LittleGame"),
    TowerDefence("TowerDefence");
//    OrthogonalityTowerDefence("OrthogonalityTowerDefence");

    private final String text;

    GameType(final String text) {
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
