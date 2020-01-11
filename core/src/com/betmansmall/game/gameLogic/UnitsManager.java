package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.Player;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.PathFinder.Node;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;
import com.betmansmall.server.data.UnitInstanceData;
import com.betmansmall.util.logging.Logger;

import java.util.ArrayDeque;

public class UnitsManager {
    public int unitsCount; // unsigned int
    public Array<Unit> hero;
    public Array<Unit> units;

    public UnitsManager() {
        Gdx.app.log("UnitsManager::UnitsManager()", "-- ");
        this.unitsCount = 0;
        this.hero = new Array<>();
        this.units = new Array<>();
    }

    public void dispose() {
        Gdx.app.log("UnitsManager::dispose()", "-- ");
//        hero.clear(); // because hero include in units array
        units.clear();
    }

    public Unit createUnit(ArrayDeque<Cell> route, TemplateForUnit templateForUnit, Player player, Cell exitCell) {
        Unit unit = new Unit(unitsCount, route, templateForUnit, player, exitCell);
        unitsCount++;
        units.add(unit);
        if (player.playerID != 0) {
            hero.add(unit);
        }
        return unit;
    }

    public boolean updateUnit(UnitInstanceData unitInstanceData, ArrayDeque<Cell> route, GameField gameField) {
        Unit mbUnit = getUnitById(unitInstanceData.id);
        if (mbUnit != null) {
            mbUnit.updateData(unitInstanceData, route, gameField);
            return true;
        }
        return false;
    }

    public Unit getUnitById(int id) { // need optimization!
        for (int u = 0; u < units.size; u++) {
            Unit unit = units.get(u);
//        for (Unit unit : units) { // pizda baga || GdxRuntimeException: #iterator() cannot be used nested
            if (unit.id == id) {
                return unit;
            }
        }
        return null;
    }

    public Unit getUnitByIndexInArray(int index) {
        return units.get(index);
    }

    public int getUnit(Unit unit) {
        return units.indexOf(unit, false);
    }

    public Unit getUnit(Node position) {
        for (int i = 0; i < units.size; i++) {
            Cell unitPosition = units.get(i).nextCell;
            if (unitPosition.equals(position)) {
                return units.get(i);
            }
        }
        return null;
    }

    public void removeUnit(Unit unit) {
        Logger.logFuncStart("unit:" + unit);
        units.removeValue(unit, false);
        if (hero.contains(unit, false)) {
            hero.removeValue(unit, false);
        }
        unit.dispose();
    }

    public void removeAllUnits() {
        Logger.logFuncStart();
        for (int u = 0; u < units.size; u++) {
            Unit unit = units.get(u);
            Logger.logDebug("unit:" + unit);
            Cell currentCell = unit.currentCell;
            currentCell.removeUnit(unit);
            if (unit.towerAttack != null && unit.towerAttack.whoAttackMe != null) {
                unit.towerAttack.whoAttackMe.removeValue(unit, true);
            }
//            units.removeValue(unit, true);
            unit.dispose();
        }
        unitsCount = 0;
        units.clear();
    }

    public String toString() {
        return toString(false);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("UnitsManager[");
        sb.append("hero.size:" + hero.size);
        if (full) {
            for (Unit unit : hero) {
                sb.append("," + unit);
            }
        }
        sb.append(",units.size:" + units.size);
        if (full) {
            for (Unit unit : units) {
                sb.append("," + unit);
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
