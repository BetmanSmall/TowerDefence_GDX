package com.betmansmall.game.gameInterface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.gameLogic.CameraController;
import com.betmansmall.utils.logging.ConsoleLoggerTable;
import com.betmansmall.utils.logging.Logger;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class GameInterface extends GameAbsInterface {
    protected CameraController cameraController;
    public BitmapFont bitmapFont;
    public ConsoleLoggerTable consoleLoggerTable;

    public PlayersViewTable playersViewTable;
    public Table tableWithButtons, tableWithSelectors, tableInfoTablo, pauseMenuTable, optionTable, firstOptionTable;
    public Table infoTabloTable;

    public TextButton resumeButton, nextLevelButton, optionButton, exitButton;
    public TextButton infoTabloHideButton, resetDrawSettingsButton;
    public Slider drawGrid, drawUnits, drawTowers, drawBackground, drawGround, drawForeground, drawGridNav, drawRoutes, drawOrder, drawAll;
    public VisCheckBox topBottomLeftRightSelector, verticalSelector, smoothFlingSelector, towerMoveAlgorithm;

    public TextButton gridNav1, gridNav2, gridNav3;
    public TextButton disconnectButtons;
    public TextButton playersViewButton;
    public TextButton pauseMenuButton;
    public TextButton startAndPauseButton;
    public TextButton gameSpeedMinus, gameSpeedPlus;
    public Label fpsLabel, deltaTimeLabel, mapPathLabel, gameType, isometricLabel,
            underConstrEndCoord, underConstructionLabel, unitsManagerSize, towersManagerSize,
            gamerGoldLabel, missedAndMaxForPlayer1, missedAndMaxForComputer0,
            nextUnitSpawnLabel,
            unitsSpawn, gamePaused,
            towersSelectorCoord,
            selectorBorderVertical, selectorBorderHorizontal;

    public UnitsSelector unitsSelector;
    public TowersSelector towersSelector;

    public int prevMouseX, prevMouseY;
    public boolean interfaceTouched;

    public VisTable tableTowerButtons;
    public VisTextButton sellTowerBtn, upgradeTowerBtn, closeTowerBtn;

    public GameInterface() {
        super();
        Logger.logFuncStart();
        this.bitmapFont = new BitmapFont();
        this.bitmapFont.getData().scale(Gdx.graphics.getHeight()*0.001f);

        this.interfaceTouched = false;
//        this.setDebugAll(true);

        consoleLoggerTable = ConsoleLoggerTable.instance();
        addActor(consoleLoggerTable);
    }

    @Override
    public void dispose() {
//        Logger.logFuncStart();
//        this.consoleLoggerTable.dispose();
//        this.cameraController.dispose();
//        this.gameScreen.dispose();
        this.bitmapFont.dispose();
        super.dispose();
    }

    @Override
    public void render(float delta) {
        act(delta);
        draw();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
//        Gdx.app.log("GameInterface::touchDown()", "-- x:" + x + " y:" + y + " pointer:" + pointer + " button:" + button);
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
//        Gdx.app.log("GameInterface::tap()", "-- x:" + x + " y:" + y + " count:" + count + " button:" + button);
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
//        Gdx.app.log("GameInterface::longPress()", "-- x:" + x + " y:" + y);
        Logger.logDebug("x:" + x, "y:" + y);
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
//        Gdx.app.log("GameInterface::fling()", "-- velocityX:" + velocityX + " velocityY:" + velocityY);
        if (unitsSelector != null) {
             if (unitsSelector.fling(velocityX, velocityY, button)) {
//                 this.interfaceTouched = true;
                 return true;
             }
        }
        if (towersSelector != null) {
            if (towersSelector.fling(velocityX, velocityY, button)) {
//                this.interfaceTouched = true;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
//        Gdx.app.log("GameInterface::pan()", "-- x:" + x + " y:" + y + " deltaX:" + deltaX + " deltaY:" + deltaY);
        return false;
    }
    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
//        Gdx.app.log("GameInterface::panStop()", "-- x:" + x + " y:" + y + " deltaX:" + deltaX + " deltaY:" + deltaY);
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
//        Gdx.app.log("GameInterface::zoom()", "-- initialDistance:" + initialDistance + " distance:" + distance);
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
//        Gdx.app.log("GameInterface::pinch()", "-- initialPointer1:" + initialPointer1 + " initialPointer2:" + initialPointer2 + " pointer1:" + pointer1 + " pointer2:" + pointer2);
        return false;
    }

    @Override
    public void pinchStop() {
//        Gdx.app.log("GameInterface::pinchStop()", "--");
    }

    @Override
    public boolean keyDown(int keyCode) {
//        Gdx.app.log("GameInterface::keyDown()", "-- keyCode:" + keyCode);
        return super.keyDown(keyCode);
    }

    @Override
    public boolean keyUp(int keyCode) {
//        Gdx.app.log("GameInterface::keyUp()", "-- keyCode:" + keyCode);
        return super.keyUp(keyCode);
    }

    @Override
    public boolean keyTyped(char character) {
//        Gdx.app.log("GameInterface::keyTyped()", "-- character:" + character);
        return super.keyTyped(character);
    }

    @Override
    public void resize() {
//        super.resize();
        Logger.logFuncStart();
//        Logger.logFuncStart("skin:" + skin);
//        if (skin != null) {
//            skin.getFont("default-font").getData().setScale(2f, 2f);
//            resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        }
    }

    @Override
    public void resize(int width, int height) {
        Logger.logFuncStart("width:" + width, "height:" + height);
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("GameInterface[");
        sb.append("prevMouseX:" + prevMouseX);
        sb.append(",prevMouseY:" + prevMouseY);
        if (full == true) {
            sb.append(",tableWithButtons:" + tableWithButtons);
        }
        sb.append("]");
        return sb.toString();
    }
}
