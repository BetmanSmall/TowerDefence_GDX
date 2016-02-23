package com.betmansmall.game.gameLogic.playerTemplates;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by betmansmall on 23.02.2016.
 */
public class FactionsManager {
    private Array<Faction> factions;

    public FactionsManager() {
        factions = new Array<Faction>();
    }

    public void addUnitToFaction(TemplateForUnit unit) {
        String newFactionName = unit.getFactionName();
        for(Faction faction: factions) {
            if(faction.getName().equals(newFactionName)) {
                faction.getUnits().add(unit);
                unit.setFaction(faction);
                return;
            }
        }
        Faction faction = new Faction(newFactionName);
        faction.getUnits().add(unit);
        unit.setFaction(faction);
        factions.add(faction);
    }

    public TemplateForUnit getDefaultTemplateForUnitFromFirstFaction() {
        Faction faction = factions.first();
        if(faction != null) {
            TemplateForUnit templateForUnit = faction.getUnits().random();
            if(templateForUnit != null) {
                return templateForUnit;
            }
        }
        return null;
    }
}
