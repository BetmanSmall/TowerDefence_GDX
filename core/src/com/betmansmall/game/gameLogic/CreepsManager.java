package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.GridNav.GridNav;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.GridNav.Options;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.GridNav.Vertex;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;

import java.util.ArrayDeque;

/**
 * Created by Андрей on 20.02.2016.
 */
public class CreepsManager {
    private Array<Creep> creeps;

    public CreepsManager(int amountCreeps) {
        creeps = new Array<Creep>(amountCreeps);
    }

    public Creep createCreep(ArrayDeque<Vertex> route, TemplateForUnit templateForUnit) {
        Creep newCreep = new Creep(route, templateForUnit);
        creeps.add(newCreep);
        return newCreep;
    }

    public int getCreep(Creep creep) { return creeps.indexOf(creep, false); }

    public Creep getCreep(int id) {
        return creeps.get(id);
    }

    public Creep getCreep(Vertex position) {
        for(int i=0; i < creeps.size; i++) {
            Vertex creepPosition = creeps.get(i).getNewPosition();
            if(creepPosition.equals(position)) {
                return creeps.get(i);
            }
        }
        return null;
    }

    public Array<Creep> getAllCreeps() {
        return creeps;
    }

    public int amountCreeps() {
        return creeps.size;
    }

    public void removeCreep(Creep creep) {
        creeps.removeValue(creep, false);
    }

    public boolean setRouteForCreeps(GridNav gridNav, GridPoint2 exitPoint) {
        for(int i=0;i<creeps.size;i++) {
            ArrayDeque<Vertex> adv = gridNav.route(new int[]{creeps.get(i).getNewPosition().getX(), creeps.get(i).getNewPosition().getY()},
                    new int[]{exitPoint.x, exitPoint.y}, Options.ASTAR, Options.EUCLIDEAN_HEURISTIC, true);
            if(adv != null) {
                creeps.get(i).setRoute(adv);
            } else {
                return false;
            }
        }
        return true;
    }
}
