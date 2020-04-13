package com.betmansmall.server.data;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.gameLogic.Cell;
import com.betmansmall.game.gameLogic.GameField;
import com.betmansmall.game.gameLogic.Unit;
import com.betmansmall.game.gameLogic.playerTemplates.Direction;
import com.betmansmall.game.gameLogic.playerTemplates.TowerShellEffect;
import com.betmansmall.game.gameLogic.playerTemplates.UnitAttack;
import com.betmansmall.utils.logging.Logger;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class UnitInstanceData implements NetworkPackage {
    public int id;
    public List<GridPoint2> route;
    public String templateForUnit;
    public PlayerInfoData playerInfoData; // mb more useless info

    public GridPoint2 exitCell;
//    public Cell currentCell;
//    public Cell nextCell;

    public float hp;
    public float speed;
    public float stepsInTime;
    public float deathElapsedTime;

    public GridPoint2 cellTowerAttack;
    public UnitAttack unitAttack;
    public Vector2 currentPoint;
    public Vector2 backStepPoint;
    public Vector2 velocity;
    public Vector2 displacement;

    public List<Circle> circles;

    public List<TowerShellEffect> shellEffectTypes;
//    public List<UnitBulletData> bullets;
    public Direction direction;
//    private Animation animation;

    public UnitInstanceData(Unit unit) {
        this.id = unit.id;
        this.route = new ArrayList<>(unit.route.size());
        this.route.add(new GridPoint2(unit.currentCell.cellX, unit.currentCell.cellY)); // need? or not?
        this.route.add(new GridPoint2(unit.nextCell.cellX, unit.nextCell.cellY));
        for (Cell cell : unit.route) {
            this.route.add(new GridPoint2(cell.cellX, cell.cellY));
        }
        this.templateForUnit = unit.templateForUnit.templateName;
        this.playerInfoData = new PlayerInfoData(unit.player); // mb only playerID?

        if (unit.exitCell != null) {
            this.exitCell = new GridPoint2(unit.exitCell.cellX, unit.exitCell.cellY);
        }

        this.hp = unit.hp;
        this.speed = unit.speed;
        this.stepsInTime = unit.stepsInTime;
        this.deathElapsedTime = unit.deathElapsedTime;

        if (unit.towerAttack != null) {
            this.cellTowerAttack = new GridPoint2(unit.towerAttack.cell.cellX, unit.towerAttack.cell.cellY);
        }
        if (unit.unitAttack != null) {
            this.unitAttack = unit.unitAttack;
        }
        this.currentPoint = unit.currentPoint;
        this.backStepPoint = unit.backStepPoint;
        this.velocity = unit.velocity;
        this.displacement = unit.displacement;

        this.circles = new ArrayList<>(unit.circles.size);
        for (Circle circle : unit.circles) {
            this.circles.add(circle);
        }

        this.shellEffectTypes = new ArrayList<>(unit.shellEffectTypes.size);
        for (TowerShellEffect towerShellEffect : unit.shellEffectTypes) {
            this.shellEffectTypes.add(towerShellEffect);
        }
//        this.bullets = ...
        this.direction = unit.direction;
    }

    public ArrayDeque<Cell> getRoute(GameField gameField) {
        if (this.route != null && this.route.size() != 0) {
            ArrayDeque<Cell> route = new ArrayDeque<>();
            for (GridPoint2 gridPoint2 : this.route) {
                Cell cell = gameField.getCell(gridPoint2.x, gridPoint2.y);
                if (cell != null) {
                    route.addLast(cell);
                } else {
                    Logger.logError("no cell by gridPoint2:" + gridPoint2);
                }
            }
            if (route.size() != 0) {
                return route;
            }
        }
        Logger.logError("this.route:" + this.route);
        return null;
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
