package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.gameLogic.GridNav.GridNav;
import com.betmansmall.game.gameLogic.GridNav.Options;
import com.betmansmall.game.gameLogic.GridNav.Vertex;
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

    public Creep createCreep(GridPoint2 position, TiledMapTileLayer layer, TemplateForUnit templateForUnit) {
        creeps.add(new Creep(position, layer, templateForUnit));
        return creeps.peek();
    }

    public int getCreep(Creep creep) { return creeps.indexOf(creep, false); }

    public Creep getCreep(int id) {
        return creeps.get(id);
    }

    public Creep getCreep(GridPoint2 position) {
        for(int i=0; i < creeps.size; i++) {
            GridPoint2 creepPosition = creeps.get(i).getPosition();
            if(creepPosition.equals(position)) {
                return creeps.get(i);
            }
        }
        return null;
    }

    public int amountCreeps() {
        return creeps.size;
    }

    public boolean setRouteForCreeps(GridNav gridNav, GridPoint2 exitPoint) {
        for(int i=0;i<creeps.size;i++) {
            ArrayDeque<Vertex> adv = gridNav.route(new int[]{creeps.get(i).getPosition().x, creeps.get(i).getPosition().y},
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
