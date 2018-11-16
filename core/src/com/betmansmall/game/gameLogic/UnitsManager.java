package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.PathFinder.Node;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.PathFinder.PathFinder;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.PathFinder.Options;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;

import java.util.ArrayDeque;

public class UnitsManager {
    public Array<Unit> units;

    public UnitsManager() {
        units = new Array<Unit>();
    }

    public Unit createUnit(ArrayDeque<Node> route, TemplateForUnit templateForUnit, int player, Cell exitCell) {
        Unit unit = new Unit(route, templateForUnit, player, exitCell);
        units.add(unit);
        return unit;
    }

    public Unit getUnit(int id) {
        return units.get(id);
    }

    public int getUnit(Unit unit) {
        return units.indexOf(unit, false);
    }

    public Unit getUnit(Node position) {
        for (int i = 0; i < units.size; i++) {
            Node unitPosition = units.get(i).newPosition;
            if (unitPosition.equals(position)) {
                return units.get(i);
            }
        }
        return null;
    }

    public void removeUnit(Unit unit) {
        units.removeValue(unit, false);
    }
}
