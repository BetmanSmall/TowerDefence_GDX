package com.betmansmall.server.data;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.gameLogic.Cell;
import com.betmansmall.game.gameLogic.Unit;
import com.betmansmall.game.gameLogic.playerTemplates.TowerShellEffect;

import java.util.ArrayList;
import java.util.List;

public class UnitInstanceData implements NetworkPackage {
    public int id;
    public List<GridPoint2> route;
    public String templateForUnit;
    public PlayerInfoData playerInfoData; // mb more useless info

    public GridPoint2 exitCell;
    public GridPoint2 currentCell;
    public GridPoint2 nextCell;

    public float hp;
    public float speed;
    public float stepsInTime;
    public float deathElapsedTime;

    public GridPoint2 cellTowerAttack;

    public List<TowerShellEffect> shellEffectTypes;
//    public List<UnitBulletData> bullets;

    public UnitInstanceData(Unit unit) {
        this.id = unit.id;
        this.route = new ArrayList<>(unit.route.size());
        for (Cell cell : unit.route) {
            this.route.add(new GridPoint2(cell.cellX, cell.cellY));
        }
        this.templateForUnit = unit.templateForUnit.templateName;
        this.playerInfoData = new PlayerInfoData(unit.player); // mb only playerID?

        if (unit.exitCell != null) {
            this.exitCell = new GridPoint2(unit.exitCell.cellX, unit.exitCell.cellY);
        }
        this.currentCell = new GridPoint2(unit.currentCell.cellX, unit.currentCell.cellY);
        this.nextCell = new GridPoint2(unit.nextCell.cellX, unit.nextCell.cellY);

        this.hp = unit.hp;
        this.speed = unit.speed;
        this.stepsInTime = unit.stepsInTime;
        this.deathElapsedTime = unit.deathElapsedTime;

        if (unit.towerAttack != null) {
            this.cellTowerAttack = new GridPoint2(unit.towerAttack.cell.cellX, unit.towerAttack.cell.cellY);
        }

        this.shellEffectTypes = new ArrayList<>(unit.shellEffectTypes.size);
        for (TowerShellEffect towerShellEffect : unit.shellEffectTypes) {
            this.shellEffectTypes.add(towerShellEffect);
        }
//        this.bullets = ...
    }

    public String toString() {
        return toString(false);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("UnitInstanceData[");
        sb.append("id:" + id);
        if (full) {
            sb.append(",route:" + route);
            sb.append(",templateForUnit:" + templateForUnit);
            sb.append(",playerInfoData:" + playerInfoData);
        }
        sb.append(",exitCell:" + ( (exitCell!=null) ? exitCell : null ) );
        sb.append(",currentCell:" + currentCell);
        sb.append(",nextCell:" + nextCell);

        sb.append(",hp:" + hp);
        sb.append(",speed:" + speed);
        if (full) {
            sb.append(",stepsInTime:" + stepsInTime);
            sb.append(",deathElapsedTime:" + deathElapsedTime);

            sb.append(",cellTowerAttack:" + cellTowerAttack);

            sb.append(",shellEffectTypes:" + shellEffectTypes);
//            sb.append(",bullets:" + bullets);
        }
        sb.append("]");
        return sb.toString();
    }
}
