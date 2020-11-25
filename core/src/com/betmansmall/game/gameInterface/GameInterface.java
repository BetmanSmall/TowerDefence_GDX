package com.betmansmall.game.gameInterface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.betmansmall.enums.GameState;
import com.betmansmall.game.gameLogic.Tower;
import com.betmansmall.screens.client.GameScreen;
import com.betmansmall.enums.GameType;
import com.betmansmall.game.gameLogic.CameraController;
import com.betmansmall.game.gameLogic.UnderConstruction;
import com.betmansmall.utils.logging.Logger;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class GameInterface extends GameAbsInterface {
    protected CameraController cameraController;
    public BitmapFont bitmapFont;

    public Skin skin;

    public PlayersViewTable playersViewTable;
    public Table tableConsoleLog;
    public Table tableWithButtons, tableWithSelectors, tableInfoTablo, pauseMenuTable, optionTable, firstOptionTable;
    public Table infoTabloTable;

    public TextButton resumeButton, nextLevelButton, optionButton, exitButton;
    public TextButton infoTabloHideButton, resetDrawSettingsButton;
    public Slider drawGrid, drawUnits, drawTowers, drawBackground, drawGround, drawForeground, drawGridNav, drawRoutes, drawOrder, drawAll;
    public CheckBox topBottomLeftRightSelector, verticalSelector, smoothFlingSelector, towerMoveAlgorithm;

    // Console need
    public Array<String> arrayActionsHistory;
    private float deleteActionThrough, actionInHistoryTime;
    private Label actionsHistoryLabel;
    // Console need

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
        this.bitmapFont = new BitmapFont();
        this.bitmapFont.getData().scale(Gdx.graphics.getHeight()*0.001f);

        this.skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        this.interfaceTouched = false;
//        this.setDebugAll(true);

        tableConsoleLog = new Table(skin);
        tableConsoleLog.setFillParent(true);
        addActor(tableConsoleLog);

        arrayActionsHistory = new Array<String>();
        arrayActionsHistory.add("actionsHistoryLabel");
        deleteActionThrough = 0f;
        actionInHistoryTime = 1f;
        actionsHistoryLabel = new Label("actionsHistoryLabel", new Label.LabelStyle(bitmapFont, Color.WHITE));
        tableConsoleLog.add(actionsHistoryLabel).expand().left();
    }

    @Override
    public void dispose() {
        Gdx.app.log("GameInterface::dispose()", "-- Called!");
//        this.cameraController.dispose();
//        this.gameScreen.dispose();
        this.bitmapFont.dispose();

        this.skin.dispose();

        super.dispose();

        this.arrayActionsHistory.clear();
    }

    public void addActionToHistory(String action) {
        if(arrayActionsHistory != null) {
            arrayActionsHistory.add(action);
        }
    }

    @Override
    public void render(float delta) {
        act(delta);
        draw();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(arrayActionsHistory.size > 0) {
            deleteActionThrough += delta;
            if (deleteActionThrough > actionInHistoryTime) {
                arrayActionsHistory.removeIndex(0);
                deleteActionThrough = 0f;
            }
            StringBuilder sb = new StringBuilder();
            for(String str : arrayActionsHistory) {
                sb.append("\n" + str);
            }
            actionsHistoryLabel.setText(sb.toString());
        }
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
        Logger.logFuncStart("skin:" + skin);
        if (skin != null) {
            skin.getFont("default-font").getData().setScale(2f, 2f);
            resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
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
