package com.betmansmall.server.data;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.Player;
import com.betmansmall.game.gameLogic.Cell;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;

public class CreateUnitData implements NetworkPackage {
    public GridPoint2 spawnCell;
    public GridPoint2 destCell;
    public String templateForUnit;
    public GridPoint2 exitCell;
    public PlayerInfoData player;

    public CreateUnitData(Cell spawnCell, Cell destCell, TemplateForUnit templateForUnit, Cell exitCell, Player player) {
        this.spawnCell = new GridPoint2(spawnCell.cellX, spawnCell.cellY);
        this.destCell = new GridPoint2(destCell.cellX, destCell.cellY);
        this.templateForUnit = templateForUnit.templateName;
        if (exitCell != null) {
            this.exitCell = new GridPoint2(exitCell.cellX, exitCell.cellY);
        }
        this.player = new PlayerInfoData(player);
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("CreateUnitData[");
        sb.append("spawnCell:" + spawnCell);
        sb.append(",destCell:" + destCell);
        sb.append("templateForUnit,:" + templateForUnit);
        sb.append(",exitCell:" + exitCell);
        sb.append(",player:" + player);
        sb.append("]");
        return sb.toString();
    }
}
