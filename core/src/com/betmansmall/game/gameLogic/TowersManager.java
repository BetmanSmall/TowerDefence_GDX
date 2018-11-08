package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;

/**
 * Created by betmansmall on 22.02.2016.
 */
public class TowersManager {
    private Array<Tower> towers;

    public TowersManager() {
        towers = new Array<Tower>();
    }

    public Tower createTower(GridPoint2 position, TemplateForTower templateForTower, int player) {
        Tower tower = new Tower(position, templateForTower, player);
        towers.add(tower);
        return tower;
    }

    public Tower getTower(int id) {
        if(id < towers.size) {
            return towers.get(id);
        } else {
            return null;
        }
    }

    public Tower getTower(GridPoint2 position) {
        for(int i=0; i < towers.size; i++) {
            GridPoint2 towerPosition = towers.get(i).position;
            if(towerPosition.equals(position)) {
                return towers.get(i);
            }
        }
        return null;
    }

    public void removeTower(Tower tower) {
        towers.removeValue(tower, false);
    }

    public void removeTower(GridPoint2 position) {
        towers.removeValue(getTower(position), false);
    }

    public Array<Tower> getAllTowers() {
        return towers;
    }

    public int amountTowers() {
        return towers.size;
    }
}
