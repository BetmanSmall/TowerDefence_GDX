package com.betmansmall.game.gameLogic.playerTemplates;

import com.badlogic.gdx.utils.Array;

/**
 * Created by betmansmall on 22.02.2016.
 */
public class Faction {
    private String name;

    private Array<TemplateForUnit> units;
    private Array<TemplateForTower> towers;

    public Faction(String name) {
        this.name = name;
        this.units = new Array<TemplateForUnit>();
        this.towers = new Array<TemplateForTower>();
    }

    public String getName() {
        return name;
    }

    public Array<TemplateForUnit> getUnits() {
        return units;
    }

    public Array<TemplateForTower> getTowers() {
        return towers;
    }
}
