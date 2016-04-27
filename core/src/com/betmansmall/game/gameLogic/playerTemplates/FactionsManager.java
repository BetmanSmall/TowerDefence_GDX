package com.betmansmall.game.gameLogic.playerTemplates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by betmansmall on 23.02.2016.
 */
public class FactionsManager {

    private static volatile FactionsManager instance;

    private TemplateForTower currentTemplateTower;

    public static FactionsManager getInstance() {
        FactionsManager localInstance = instance;
        if (localInstance == null) {
            synchronized (FactionsManager.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new FactionsManager();
                }
            }
        }
        return localInstance;
    }

    private Array<Faction> factions;

    public FactionsManager() {
        instance = this; //TODO init with singleton
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

    public void addTowerToFaction(TemplateForTower tower) {
//        Gdx.app.log("FactionsManager::addTowerToFaction()", " -- Tower name:" + tower.name);
        String newFactionName = tower.getFactionName();
        for(Faction faction: factions) {
            if(faction.getName().equals(newFactionName)) {
                faction.getTowers().add(tower);
                tower.setFaction(faction);
                return;
            }
        }
        Faction faction = new Faction(newFactionName);
        faction.getTowers().add(tower);
        tower.setFaction(faction);
        factions.add(faction);
    }

    public TemplateForUnit getRandomTemplateForUnitFromFirstFaction() {
        Faction faction = factions.first();
        if(faction != null) {
            TemplateForUnit templateForUnit = faction.getUnits().random();
            if(templateForUnit != null) {
                return templateForUnit;
            }
        }
        return null;
    }

    public TemplateForTower getRandomTemplateForTowerFromFirstFaction() {
        Faction faction = factions.first();
        if(faction != null) {
            TemplateForTower templateForTower = faction.getTowers().random();
            if(templateForTower != null) {
                return templateForTower;
            }
        }
        return null;
    }

    public TemplateForUnit getTemplateForUnitFromFirstFactionByIndex(int index) {
        Faction faction = factions.first();
        if(faction != null) {
            TemplateForUnit templateForUnit = faction.getUnits().get(index);
            if(templateForUnit != null) {
                return templateForUnit;
            }
        }
        return null;
    }

    public List<TemplateForTower> getAllTowers(){
        List<TemplateForTower> allTowers = new ArrayList<TemplateForTower>();
        for(Faction faction : factions) {
            for(TemplateForTower template : faction.getTowers()) {
                allTowers.add(template);
            }
        }
        return allTowers;
    }
    //Mukhin gad
    public TemplateForTower getCurrentTemplateTower() {
        if(currentTemplateTower == null) {
            currentTemplateTower = getAllTowers().get(0);
        }
        return currentTemplateTower;
    }

    public void setCurrentTemplateTower(TemplateForTower currentTemplateTower) {
        this.currentTemplateTower = currentTemplateTower;
    }
}
