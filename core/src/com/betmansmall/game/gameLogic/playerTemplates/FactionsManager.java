package com.betmansmall.game.gameLogic.playerTemplates;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

import java.util.StringTokenizer;

/**
 * Created by betmansmall on 23.02.2016.
 */
public class FactionsManager {
    public SimpleTemplate fireball_0;
    public SimpleTemplate explosion;
    private Array<Faction> factions;

    public static Float defHealthPoints = 500f;
    public static Float defBounty = 50f;
    public static Float defCost = 50f;
    public static Float defSpeed = 1f;

    public FactionsManager() throws Exception {
        Gdx.app.log("FactionsManager::FactionsManager()", "-- :");
        this.fireball_0 = new SimpleTemplate(Gdx.files.internal("maps/other/fireball_0.tsx"));
        Gdx.app.log("FactionsManager::FactionsManager()", "-- fireball_0:" + fireball_0.toString());
        this.explosion = new SimpleTemplate(Gdx.files.internal("maps/other/explosion.tsx"));
        Gdx.app.log("FactionsManager::FactionsManager()", "-- explosion:" + explosion.toString());
        this.factions = new Array<Faction>();
        loadFactions();
    }

    public TemplateForUnit getRandomTemplateForUnitFromFirstFaction() {
        Faction faction = factions.first();
        if (faction != null) {
            TemplateForUnit templateForUnit = faction.getTemplateForUnits().random();
            if (templateForUnit != null) {
                return templateForUnit;
            }
        }
        return null;
    }

    public TemplateForUnit getRandomTemplateForUnitFromSecondFaction() {
        Faction faction = factions.get(1);
        if (faction != null) {
            TemplateForUnit templateForUnit = faction.getTemplateForUnits().random();
            if (templateForUnit != null) {
                return templateForUnit;
            }
        }
        return null;
    }

    public TemplateForTower getRandomTemplateForTowerFromFirstFaction() {
        Faction faction = factions.first();
        if (faction != null) {
            TemplateForTower templateForTower = faction.getTemplateForTowers().random();
            if (templateForTower != null) {
                return templateForTower;
            }
        }
        return null;
    }

    public TemplateForTower getRandomTemplateForTowerFromAllFaction() {
        Array<TemplateForTower> allTowers = getAllTemplateForTowers();
//        int r = 0 + (int) (Math.random() * allTowers.size);
//        Gdx.app.log("TemplateForTower", "getRandomTemplateForTowerFromAllFaction(); -- r:" + r);
//        return allTowers.get(r);
        return allTowers.random();
    }
    public TemplateForUnit getTemplateForUnitFromFirstFactionByIndex(int index) {
        Faction faction = factions.first();
        if (faction != null) {
            TemplateForUnit templateForUnit = faction.getTemplateForUnits().get(index);
            if (templateForUnit != null) {
                return templateForUnit;
            }
        }
        return null;
    }

    public TemplateForUnit getTemplateForUnitFromFirstFactionByName(String templateName) {
        Faction faction = factions.first();
        if (faction != null) {
            for (TemplateForUnit templateForUnit : faction.getTemplateForUnits()) {
                if (templateForUnit != null) {
                    if (templateForUnit.templateName.equals(templateName)) {
                        return templateForUnit;
                    }
                }
            }
        }
        return null;
    }

    public TemplateForUnit getTemplateForUnitByName(String templateName) {
        for (Faction faction : factions) {
            if (faction != null) {
                for (TemplateForUnit templateForUnit : faction.getTemplateForUnits()) {
                    if (templateForUnit != null) {
                        if (templateForUnit.templateName.equals(templateName)) {
                            return templateForUnit;
                        }
                    }
                }
            }
        }
        return null;
    }

    public Array<TemplateForTower> getAllFirstTowersFromFirstFaction() {
        return factions.first().getTemplateForTowers();
    }

    public Array<TemplateForTower> getAllTemplateForTowers() {
        Array<TemplateForTower> allTowers = new Array<TemplateForTower>();
        for (Faction faction : factions) {
            for (TemplateForTower template : faction.getTemplateForTowers()) {
                allTowers.add(template);
            }
        }
        return allTowers;
    }

    public Array<TemplateForUnit> getAllTemplateForUnits() {
        Array<TemplateForUnit> allTowers = new Array<TemplateForUnit>();
        for (Faction faction : factions) {
            for (TemplateForUnit template : faction.getTemplateForUnits()) {
                allTowers.add(template);
            }
        }
        return allTowers;
    }

    public void loadFactions() {
        Array<FileHandle> factions = new Array<FileHandle>();
        if(Gdx.app.getType() == Application.ApplicationType.Android) {
//            Gdx.app.log("FactionsManager::loadFactions()", "-- ApplicationType.Android");
            FileHandle factionsDir = Gdx.files.internal("maps/factions");
            factions.addAll(factionsDir.list());
        } else if(Gdx.app.getType() == Application.ApplicationType.Desktop) {
            boolean isExtAvailable = Gdx.files.isExternalStorageAvailable();
            boolean isLocAvailable = Gdx.files.isLocalStorageAvailable();
            String extRoot = Gdx.files.getExternalStoragePath();
            String locRoot = Gdx.files.getLocalStoragePath();
//            Gdx.app.log("FactionsManager::loadFactions()", "-- ApplicationType.Desktop -- isExtAvailable:" + isExtAvailable + " isLocAvailable:" + isLocAvailable);
//            Gdx.app.log("FactionsManager::loadFactions()", "-- extRoot:" + extRoot + " locRoot:" + locRoot);
            FileHandle factionsDir = Gdx.files.internal("maps/factions");
//            Gdx.app.log("FactionsManager::loadFactions()", "-- factionsDir.length:" + factionsDir.list().length);
            if(factionsDir.list().length == 0) {
                factions.add(Gdx.files.internal("maps/factions/humans_faction.fac"));
                factions.add(Gdx.files.internal("maps/factions/orcs_faction.fac"));
//                factions.add(Gdx.files.internal("!!!add new faction in the future!!!"));
            } else {
                factions.addAll(factionsDir.list());
            }
        }
        Gdx.app.log("FactionsManager::loadFactions()", "-- factions.size:" + factions.size);
        for (FileHandle factionFile : factions) {
            if (factionFile.extension().equals("fac")) {
                loadFaction(factionFile);
            }
        }
    }

    private void loadFaction(FileHandle factionFile) {
        if(factionFile != null && !factionFile.isDirectory()) {
            Gdx.app.log("FactionsManager::loadFaction(" + factionFile + ")", "-- absolutePath:" + factionFile.file().getAbsolutePath());
            try {
                XmlReader xmlReader = new XmlReader();
                Element root = xmlReader.parse(factionFile);
                String factionName = root.getAttribute("name", null);
                if (factionName != null) {
                    Faction faction = new Faction(factionName);
                    Array<Element> templateForUnitElements = root.getChildrenByName("templateForUnit");
                    Gdx.app.log("FactionsManager::loadFaction()", "-- templateForUnitElements.size:" + templateForUnitElements.size);
                    for (Element templateForUnitElement : templateForUnitElements) {
                        String source = templateForUnitElement.getAttribute("source", null);
                        if (source != null) {
                            FileHandle templateFile = getRelativeFileHandle(factionFile, source);
                            TemplateForUnit templateForUnit = new TemplateForUnit(templateFile);
//                            templateForUnit.setFaction(faction);
                            faction.getTemplateForUnits().add(templateForUnit);
                            Gdx.app.log("FactionsManager::loadFaction()", "-- " + templateForUnit.toString(true));
                        }
                    }
                    Array<Element> templateForTowerElements = root.getChildrenByName("templateForTower");
                    Gdx.app.log("FactionsManager::loadFaction()", "-- templateForTowerElements.size:" + templateForTowerElements.size);
                    for (Element templateForTowerElement : templateForTowerElements) {
                        String source = templateForTowerElement.getAttribute("source", null);
                        if (source != null) {
                            FileHandle templateFile = getRelativeFileHandle(factionFile, source);
                            TemplateForTower templateForTower = new TemplateForTower(templateFile);
//                            templateForTower.setFaction(faction);
                            if (templateForTower.towerAttackType == TowerAttackType.FireBall) {
//                            if (templateForTower.templateName.contains("tower_FireBall")) {
                                templateForTower.loadFireBall(fireball_0);
                            }
                            faction.getTemplateForTowers().add(templateForTower);
                            Gdx.app.log("FactionsManager::loadFaction()", "-- " + templateForTower.toString(true));
                        }
                    }
                    factions.add(faction);
                }
            } catch (Exception exp) {
                Gdx.app.error("FactionsManager::loadFaction()", "-- Could not load Faction! Exp:" + exp);
            }
        } else {
            Gdx.app.error("FactionsManager::loadFaction()", "-- Could not load Faction! (factionFile == null) or (factionFile.isDirectory() == true)");
        }
    }

    protected static FileHandle getRelativeFileHandle(FileHandle file, String path) {
        StringTokenizer tokenizer = new StringTokenizer(path, "\\/");
        FileHandle result = file.parent();
        while (tokenizer.hasMoreElements()) {
            String token = tokenizer.nextToken();
            if (token.equals(".."))
                result = result.parent();
            else {
                result = result.child(token);
            }
        }
        return result;
    }

    public String toString() {
        return toString(false);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("FactionsManager[");
        sb.append("factions.size:" + factions.size);
        for (Faction faction : factions) {
            sb.append("," + faction);
        }
        if (full) {
            sb.append(",fireball_0:" + fireball_0);
            sb.append(",explosion:" + explosion);
            sb.append(",defHealthPoints:" + defHealthPoints);
            sb.append(",defBounty:" + defBounty);
            sb.append(",defCost:" + defCost);
            sb.append(",defSpeed:" + defSpeed);
        }
        sb.append("]");
        return sb.toString();
    }
}
