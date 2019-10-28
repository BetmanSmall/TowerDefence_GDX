package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.Player;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;

/**
 * Created by betmansmall on 22.02.2016.
 */
public class TowersManager {
    public Array<Tower> towers;

    public TowersManager() {
        Gdx.app.log("TowersManager::TowersManager()", "-- ");
        towers = new Array<Tower>();
    }

    public void dispose() {
        Gdx.app.log("TowersManager::dispose()", "-- ");
        towers.clear();
    }

    public Tower createTower(Cell cell, TemplateForTower templateForTower, Player player) {
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

    public Tower getTower() {
        return getTower(null);
    }

    public Tower getTower(Cell cell) {
        if (cell == null) {
            if (towers.size > 0) {
                return towers.get(towers.size);
            }
        } else {
            for (Tower tower : towers) {
                if (tower.cell.cellX == cell.cellX && tower.cell.cellY == cell.cellY) {
                    return tower;
                }
            }
//            for (int i = 0; i < towers.size; i++) {
//                Cell towerCell = towers.get(i).cell;
//                if (towerCell.equals(cell)) {
//                    return towers.get(i);
//                }
//            }
        }
        return null;
    }

    public boolean removeTower(Tower tower) {
        if (towers.removeValue(tower, false)) {
            tower.dispose();
            return true;
        }
        return false;
    }

    public void removeTower(Cell cell) {
        towers.removeValue(getTower(cell), false);
    }

    /**
     * Selects tower if it belongs to player.
     */
    public void selectTower(Player player, Tower tower) {
        if(tower.player == player) player.selectedTower = tower;
    }

    public String toString() {
        return toString(false);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("TowersManager[");
        sb.append("towers.size:" + towers.size);
        if (full) {
            for (Tower tower: towers) {
                sb.append("," + tower);
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
