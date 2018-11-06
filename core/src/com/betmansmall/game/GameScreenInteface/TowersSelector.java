package com.betmansmall.game.GameScreenInteface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.gameLogic.GameField;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;

/**
 * Created by Дима BetmanSmall on 14.05.2017.
 */
public class TowersSelector {
    public GameField gameField;
    public BitmapFont bitmapFont;
    public Table table;

    public Array<TemplateForTower> templateForTowers;
    public VerticalGroup verticalGroupWithTowers;
    public boolean pan = false;

    public TowersSelector(GameField gameField, BitmapFont bitmapFont, Table table) {
        this.gameField = gameField;
        this.bitmapFont = bitmapFont;
        this.table = table;

        templateForTowers = gameField.getAllTemplateForTowers();
        Gdx.app.log("TowersSelector::TowersSelector()", "-- templateForTowers:" + templateForTowers);

        verticalGroupWithTowers = new VerticalGroup();
        verticalGroupWithTowers.align(Align.right);
        table.add(verticalGroupWithTowers).right().bottom().expand();

        for(int towerIndex = 0; towerIndex < templateForTowers.size; towerIndex++) {
            TemplateForTower templateForTower = templateForTowers.get(towerIndex);
            String nameTower = templateForTower.name;
            String attackTower = templateForTower.damage.toString();
            String radiusDetectionTower = templateForTower.radiusDetection.toString();
            String costTower = templateForTower.cost.toString();
            Label nameTowerLabel = new Label(nameTower, new Label.LabelStyle(bitmapFont, Color.WHITE));
            Label attackTowerLabel = new Label(attackTower, new Label.LabelStyle(bitmapFont, Color.RED));
            Label radiusDetectionTowerLabel = new Label(radiusDetectionTower, new Label.LabelStyle(bitmapFont, Color.GREEN));
            Label costTowerLabel = new Label(costTower, new Label.LabelStyle(bitmapFont, Color.YELLOW));
            nameTowerLabel.setName("nameTowerLabel");
            attackTowerLabel.setName("attackTowerLabel");
            radiusDetectionTowerLabel.setName("radiusDetectionTowerLabel");
            costTowerLabel.setName("costTowerLabel");

            Table towerTable = new Table(table.getSkin());
//            towerTable.setDebug(true);

            towerTable.add(nameTowerLabel).colspan(2).row();
            Image templateButton = new Image(templateForTower.idleTile.getTextureRegion());
            towerTable.add(templateButton).expand();

            VerticalGroup verticalGroupHar = new VerticalGroup();
            verticalGroupHar.addActor(attackTowerLabel);
            verticalGroupHar.addActor(radiusDetectionTowerLabel);
            verticalGroupHar.addActor(costTowerLabel);
            towerTable.add(verticalGroupHar).expandY().right();

            Button button = new Button(towerTable, table.getSkin());
            button.setUserObject(towerIndex);
            verticalGroupWithTowers.addActor(button);
        }
    }

    public boolean tap(float x, float y, int count, int button) {
        Gdx.app.log("TowersSelector::tap()", "-- x:" + x + " y:" + y + " count:" + count + " button:" + button);
        for (Actor actor : verticalGroupWithTowers.getChildren()) {
//            Gdx.app.log("TowersSelector::tap()", "-- actor:" + actor);
            if (actor instanceof Button) {
                Button buttonActor = (Button)actor;
//                Gdx.app.log("TowersSelector::tap()", "-- buttonActor.isPressed():" + buttonActor.isPressed());
                if(buttonActor.isPressed()) {
                    Integer towerIndex = (Integer) buttonActor.getUserObject();
                    if (towerIndex != null) {
                        Gdx.app.log("TowersSelector::tap()", "-- towerIndex:" + towerIndex);
                        gameField.createdUnderConstruction(templateForTowers.get(towerIndex));
                    }
//                    Gdx.app.log("TowersSelector::tap()", "-- return true");
                    return true;
                }
            }
        }
        Gdx.app.log("TowersSelector::tap()", "-- return false");
        return false;
    }

    public boolean pan(float x, float y, float deltaX, float deltaY) {
//        Gdx.app.log("TowersSelector::pan(" + x + "," + y + "," + deltaX + "," + deltaY + ")", "--");
        float groupX = verticalGroupWithTowers.getX();
        float groupY = verticalGroupWithTowers.getY();
        float groupWidth = verticalGroupWithTowers.getWidth();
        float groupHeight = verticalGroupWithTowers.getHeight();
        float groupPrefWidth = verticalGroupWithTowers.getPrefWidth();
        float groupPrefHeight = verticalGroupWithTowers.getPrefHeight();
        float tableWidth = table.getWidth();
        float tableHeight = table.getHeight();
//        Gdx.app.log("TowersSelector::pan()", "-- groupX:" + groupX + " groupY:" + groupY + " groupWidth:" + groupWidth + " groupHeight:" + groupHeight);
//        Gdx.app.log("TowersSelector::pan()", "-- groupPrefWidth:" + groupPrefWidth + " groupPrefHeight:" + groupPrefHeight + " tableWidth:" + tableWidth + " tableHeight:" + tableHeight);
//        Gdx.app.log("TowersSelector::pan()", "-- Gdx.graphics.getWidth():" + Gdx.graphics.getWidth() + " Gdx.graphics.getHeight():" + Gdx.graphics.getHeight());
//        Gdx.app.log("TowersSelector::pan()", "-- table.getStage().getViewport().getScreenWidth():" + table.getStage().getViewport().getScreenWidth());
//        Gdx.app.log("TowersSelector::pan()", "-- table.getStage().getViewport().getScreenHeight():" + table.getStage().getViewport().getScreenHeight());
//        Gdx.app.log("TowersSelector::pan()", "-- table.getStage().getViewport().getWorldWidth():" + table.getStage().getViewport().getWorldWidth());
//        Gdx.app.log("TowersSelector::pan()", "-- table.getStage().getViewport().getWorldHeight():" + table.getStage().getViewport().getWorldHeight());
//        Gdx.app.log("TowersSelector::pan()", "-- table.getWidth():" + table.getWidth() + " table.getHeight():" + table.getHeight());
        if (Math.abs(deltaX) > Math.abs(deltaY) && !pan) {
            if (x >= (tableWidth-groupWidth/**2f*/) && deltaX > 0) {
                verticalGroupWithTowers.moveBy(deltaX, 0);
                if(verticalGroupWithTowers.getX() > tableWidth) {
                    verticalGroupWithTowers.setX(tableWidth);
                    gameField.cancelUnderConstruction();
                }
                pan = false;
                return true;
            } else if (x >= (tableWidth-groupWidth) && deltaX < 0) {
                verticalGroupWithTowers.moveBy(deltaX, 0);
                if(verticalGroupWithTowers.getX() < tableWidth) {
                    verticalGroupWithTowers.setX(tableWidth-groupWidth);
                }
                pan = true;
                return true;
            }
        } else if (x >= (tableWidth-groupWidth) || pan) {
//            pan = true;
            if (deltaX < 0) {
                verticalGroupWithTowers.moveBy(0, -deltaY);
            } else if (deltaX > 0) {
                verticalGroupWithTowers.moveBy(0, -deltaY);
            }
            if (verticalGroupWithTowers.getY() > 0) {
                verticalGroupWithTowers.setY(0);
            } else if(verticalGroupWithTowers.getY()+ verticalGroupWithTowers.getHeight() < tableHeight) {
                verticalGroupWithTowers.setY( (0-(verticalGroupWithTowers.getHeight()-tableHeight)) );
            }
            return true;
        }
        return false;
    }

    public boolean panStop(float x, float y, int pointer, int button) {
        Gdx.app.log("TowersSelector::panStop()", "-- x:" + x + " y:" + y + " pointer:" + pointer + " button:" + button);
        pan = false;
        return true;
    }

    public boolean scrolled(int amount) {
        Gdx.app.log("TowersSelector::scrolled()", "-- amount:" + amount);
        float groupWidth = verticalGroupWithTowers.getWidth();
        float tableWidth = table.getWidth();
        if (Gdx.input.getX() >= (tableWidth-groupWidth)) {
            verticalGroupWithTowers.moveBy(0, amount*10f);
            return true;
        }
        return false;
    }
}
