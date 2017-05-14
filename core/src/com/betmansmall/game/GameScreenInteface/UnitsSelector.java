package com.betmansmall.game.GameScreenInteface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import com.betmansmall.game.gameLogic.GameField;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;

/**
 * Created by Дима Цыкунов on 20.02.2016.
 */
public class UnitsSelector {
    public GameField gameField;
    public BitmapFont bitmapFont;
    public Table table;

    public Array<TemplateForUnit> templateForUnits;
    public VerticalGroup verticalGroupWithUnits;
    public boolean pan = false;

    public UnitsSelector(GameField gameField, BitmapFont bitmapFont, Table table) {
        this.gameField = gameField;
        this.bitmapFont = bitmapFont;
        this.table = table;

        this.templateForUnits = gameField.getAllTemplateForUnits();
        Gdx.app.log("UnitsSelector::UnitsSelector()", "-- templateForUnits:" + templateForUnits);

        this.verticalGroupWithUnits = new VerticalGroup();
        verticalGroupWithUnits.align(Align.left);
        table.add(verticalGroupWithUnits).left().bottom().expand();

        for (int unitIndex = 0; unitIndex < templateForUnits.size; unitIndex++) {
            TemplateForUnit templateForUnit = templateForUnits.get(unitIndex);
            String nameUnit = templateForUnit.name;
            String hpUnit = templateForUnit.healthPoints.toString();
            String speedUnit = templateForUnit.speed.toString();
            String costUnit = templateForUnit.cost.toString();
            Label nameUnitLabel = new Label(nameUnit, new Label.LabelStyle(bitmapFont, Color.WHITE));
            Label hpUnitLabel = new Label(hpUnit, new Label.LabelStyle(bitmapFont, Color.RED));
            Label speedUnitLabel = new Label(speedUnit, new Label.LabelStyle(bitmapFont, Color.GREEN));
            Label costUnitLabel = new Label(costUnit, new Label.LabelStyle(bitmapFont, Color.YELLOW));
            nameUnitLabel.setName("nameUnitLabel");
            hpUnitLabel.setName("hpUnitLabel");
            speedUnitLabel.setName("speedUnitLabel");
            costUnitLabel.setName("costUnitLabel");

            Table unitTable = new Table(table.getSkin());
//            unitTable.setDebug(true);

            unitTable.add(nameUnitLabel).colspan(2).row();
            VerticalGroup verticalGroupHar = new VerticalGroup();
            verticalGroupHar.addActor(hpUnitLabel);
            verticalGroupHar.addActor(speedUnitLabel);
            verticalGroupHar.addActor(costUnitLabel);
            unitTable.add(verticalGroupHar).expandY().left();

            Image templateButton = new Image(templateForUnit.animations.values().toArray().get(6).getTextureRegion());
            unitTable.add(templateButton).expand();

            Button button = new Button(unitTable, table.getSkin());
            button.setUserObject(unitIndex);
            verticalGroupWithUnits.addActor(button);
        }
    }

    public boolean tap(float x, float y, int count, int button) {
        Gdx.app.log("UnitsSelector::tap()", "-- x:" + x + " y:" + y + " count:" + count + " button:" + button);
        y = Gdx.graphics.getHeight() - y;
        for (Actor actor : verticalGroupWithUnits.getChildren()) {
            Gdx.app.log("UnitsSelector::tap()", "-- actor:" + actor);
            if (actor instanceof Button) {
                Button buttonActor = (Button)actor;
                Gdx.app.log("UnitsSelector::tap()", "-- buttonActor.isPressed():" + buttonActor.isPressed());
                if(buttonActor.isPressed()) {
                    Integer unitIndex = (Integer) buttonActor.getUserObject();
                    if (unitIndex != null) {
                        Gdx.app.log("UnitsSelector::tap()", "-- unitIndex:" + unitIndex);
                        gameField.spawnCreepFromUser(templateForUnits.get(unitIndex));
                    }
                    Gdx.app.log("UnitsSelector::tap()", "-- return true");
                    return true;
                }
            }
        }
        Gdx.app.log("UnitsSelector::tap()", "-- return false");
        return false;
    }

    public boolean pan(float x, float y, float deltaX, float deltaY) {
        Gdx.app.log("UnitsSelector::pan(" + x + "," + y + "," + deltaX + "," + deltaY + ")", "--");
        float groupX = verticalGroupWithUnits.getX();
        float groupY = verticalGroupWithUnits.getY();
        float groupWidth = verticalGroupWithUnits.getWidth();
        float groupHeight = verticalGroupWithUnits.getHeight();
        float groupPrefWidth = verticalGroupWithUnits.getPrefWidth();
        float groupPrefHeight = verticalGroupWithUnits.getPrefHeight();
        float tableWidth = table.getWidth();
        float tableHeight = table.getHeight();
        Gdx.app.log("UnitsSelector::pan()", "-- groupX:" + groupX + " groupY:" + groupY + " groupWidth:" + groupWidth + " groupHeight:" + groupHeight);
        Gdx.app.log("UnitsSelector::pan()", "-- groupPrefWidth:" + groupPrefWidth + " groupPrefHeight:" + groupPrefHeight + " tableWidth:" + tableWidth + " tableHeight:" + tableHeight);
        Gdx.app.log("UnitsSelector::pan()", "-- Gdx.graphics.getWidth():" + Gdx.graphics.getWidth() + " Gdx.graphics.getHeight():" + Gdx.graphics.getHeight());
        Gdx.app.log("UnitsSelector::pan()", "-- table.getStage().getViewport().getScreenWidth():" + table.getStage().getViewport().getScreenWidth());
        Gdx.app.log("UnitsSelector::pan()", "-- table.getStage().getViewport().getScreenHeight():" + table.getStage().getViewport().getScreenHeight());
        Gdx.app.log("UnitsSelector::pan()", "-- table.getStage().getViewport().getWorldWidth():" + table.getStage().getViewport().getWorldWidth());
        Gdx.app.log("UnitsSelector::pan()", "-- table.getStage().getViewport().getWorldHeight():" + table.getStage().getViewport().getWorldHeight());
        Gdx.app.log("UnitsSelector::pan()", "-- table.getWidth():" + table.getWidth() + " table.getHeight():" + table.getHeight());
        if (Math.abs(deltaX) > Math.abs(deltaY) && !pan) {
            if (x <= (groupWidth*2f) && deltaX < 0) {
                verticalGroupWithUnits.moveBy(deltaX, 0);
                if(verticalGroupWithUnits.getX() < (0-groupWidth)) {
                    verticalGroupWithUnits.setX((0-groupWidth));
                }
                pan = false;
                return true;
            } else if (x <= groupWidth && deltaX > 0) {
                verticalGroupWithUnits.moveBy(deltaX, 0);
                if(verticalGroupWithUnits.getX() > (0-groupWidth)) {
                    verticalGroupWithUnits.setX(0);
                }
                pan = true;
                return true;
            }
        } else if (x <= groupWidth || pan) {
//            pan = true;
            if (deltaX < 0) {
                verticalGroupWithUnits.moveBy(0, -deltaY);
            } else if (deltaX > 0) {
                verticalGroupWithUnits.moveBy(0, -deltaY);
            }
            if (verticalGroupWithUnits.getY() > 0) {
                verticalGroupWithUnits.setY(0);
            } else if(verticalGroupWithUnits.getY()+ verticalGroupWithUnits.getHeight() < tableHeight) {
                verticalGroupWithUnits.setY( (0-(verticalGroupWithUnits.getHeight()-tableHeight)) );
            }
            return true;
        }
        return false;
    }

    public boolean panStop(float x, float y, int pointer, int button) {
        Gdx.app.log("UnitsSelector::panStop()", "-- x:" + x + " y:" + y + " pointer:" + pointer + " button:" + button);
        pan = false;
        return true;
    }

    public boolean scrolled(int amount) {
        Gdx.app.log("TowersSelector::scrolled()", "-- amount:" + amount);
        float groupWidth = verticalGroupWithUnits.getWidth();
        if (Gdx.input.getX() <= groupWidth) {
            verticalGroupWithUnits.moveBy(0, amount*10f);
            return true;
        }
        return false;
    }
}
