package com.betmansmall.server.data;

import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.gameLogic.Tower;
import com.betmansmall.game.gameLogic.TowersManager;

import java.io.Serializable;
import java.util.ArrayList;

public class TowersManagerData implements NetworkPackage {
    public class TowerData implements Serializable {
        public int cellX;
        public int cellY;
        public String templateName;
        public int playerID;

        public float elapsedReloadTime;
        public float hp;

        public TowerData(Tower tower) {
            this.cellX = tower.cell.cellX;
            this.cellY = tower.cell.cellY;
            this.templateName = tower.templateForTower.templateName;
            this.playerID = tower.player.playerID;

            this.elapsedReloadTime = tower.elapsedReloadTime;
            this.hp = tower.hp;
        }

        @Override
        public String toString() {
            return toString(false);
        }

        public String toString(boolean full) {
            StringBuilder sb = new StringBuilder();
            sb.append("TowerData[");
            sb.append("cellX:" + cellX);
            sb.append(",cellY:" + cellY);
            sb.append(",templateName:" + templateName);
            sb.append(",playerID:" + playerID);
            if (full) {
                sb.append(",elapsedReloadTime:" + elapsedReloadTime);
                sb.append(",hp:" + hp);
            }
            sb.append("]");
            return sb.toString();
        }
    }
    public ArrayList<TowerData> towers;

    public TowersManagerData(TowersManager towersManager) {
        this.towers = new ArrayList<>(towersManager.towers.size);

        for (int t = 0; t < towersManager.towers.size; t++) {
            Tower tower = towersManager.towers.get(t);
//        for (Tower tower : towersManager.towers) {
            TowerData towerData = new TowerData(tower);
            this.towers.add(towerData);
        }
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("TowersManagerData[");
        sb.append("towers.size():" + towers.size());
        if (full) {
            for (TowerData towerData : towers) {
                sb.append("," + towerData);
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
