package com.betmansmall.server.data;

import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.gameLogic.Tower;

public class BuildTowerData implements NetworkPackage {
    public Integer playerID;
    public int buildX;
    public int buildY;
    public String templateName;
    public String factionName;
//    public boolean reroute;

    public BuildTowerData(Tower tower) {
        this.playerID = tower.player.playerID;
        this.buildX = tower.cell.cellX;
        this.buildY = tower.cell.cellY;
        this.templateName = tower.templateForTower.templateName;
        this.factionName = tower.templateForTower.factionName;
//        this.reroute = true;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("BuildTowerData[");
        sb.append("playerID:" + playerID);
        sb.append(",buildX:" + buildX);
        sb.append(",buildY:" + buildY);
        if (full) {
            sb.append(",templateName:" + templateName);
            sb.append(",factionName:" + factionName);
        }
        sb.append("]");
        return sb.toString();
    }
}
