package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;

/**
 * Created by betmansmall on 22.02.2016.
 */
public class TowersManager {
    public Array<Tower> towers;

    public TowersManager() {
        towers = new Array<Tower>();
    }

    public Tower createTower(Cell cell, TemplateForTower templateForTower, int player) {
        Tower tower = new Tower(cell, templateForTower, player);
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

    public Tower getTower(Cell cell) {
        for(int i=0; i < towers.size; i++) {
            Cell towerCell = towers.get(i).cell;
            if(towerCell.equals(cell)) {
                return towers.get(i);
            }
        }
        return null;
    }

    public void removeTower(Tower tower) {
        towers.removeValue(tower, false);
    }

    public void removeTower(Cell cell) {
        towers.removeValue(getTower(cell), false);
    }
}
