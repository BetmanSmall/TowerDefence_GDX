package com.betmansmall.game.gameLogic.playerTemplates;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;

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

    public TemplateForTower getTemplateForTower(String templateName) {
        for (TemplateForTower templateForTower : templateForTowers) {
            if (templateForTower.templateName.equals(templateName)) {
                return templateForTower;
            }
        }
        return null;
    }

    public String toString() {
        return toString(false);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("Faction[");
        sb.append("name:" + name);
        sb.append(",templateForUnits.size:" + templateForUnits.size);
        if (full) {
            for (TemplateForUnit templateForUnit : templateForUnits) {
                sb.append("," + templateForUnit);
            }
        }
        sb.append(",templateForTowers.size:" + templateForTowers.size);
        if (full) {
            for (TemplateForTower templateForTower : templateForTowers) {
                sb.append("," + templateForTower);
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
