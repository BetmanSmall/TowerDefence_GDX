package com.betmansmall.game.gameLogic.playerTemplates;

import com.badlogic.gdx.utils.Array;

/**
 * Created by betmansmall on 22.02.2016.
 */
public class Faction {
    private String name;

    private Array<TemplateForUnit> templateForUnits;
    private Array<TemplateForTower> templateForTowers;

    public Faction(String name) {
        this.name = name;
        this.templateForUnits = new Array<TemplateForUnit>();
        this.templateForTowers = new Array<TemplateForTower>();
    }

    public String getName() {
        return name;
    }

    public Array<TemplateForUnit> getTemplateForUnits() {
        return templateForUnits;
    }

    public Array<TemplateForTower> getTemplateForTowers() {
        return templateForTowers;
    }
}
