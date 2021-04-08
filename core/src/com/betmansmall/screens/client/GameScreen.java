package com.betmansmall.screens.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.enums.GameState;
import com.betmansmall.GameMaster;
import com.betmansmall.enums.SessionType;
import com.betmansmall.game.Player;
import com.betmansmall.game.PlayersManager;
import com.betmansmall.game.gameInterface.GameScreenInterface;
import com.betmansmall.game.gameLogic.CameraController;
import com.betmansmall.game.gameLogic.Cell;
import com.betmansmall.game.gameLogic.GameField;
import com.betmansmall.game.gameLogic.GameScreenCameraController;
import com.betmansmall.game.gameLogic.Tower;
import com.betmansmall.game.gameLogic.UnderConstruction;
import com.betmansmall.game.gameLogic.Unit;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;
import com.betmansmall.render.GameFieldRenderer;
import com.betmansmall.utils.AbstractScreen;
import com.betmansmall.server.accouting.UserAccount;
import com.betmansmall.utils.logging.ConsoleLoggerTable;
import com.betmansmall.utils.logging.Logger;

public class GameScreen extends AbstractScreen {
    public GameField gameField;
    public GameScreenInterface gameInterface;
    public GameFieldRenderer gameFieldRenderer;
    public CameraController cameraController;

    public PlayersManager playersManager;

    public GameScreen(GameMaster gameMaster) {
        super(gameMaster);
        Logger.logFuncStart();
        gameMaster.sessionSettings.sessionType = SessionType.CLIENT_STANDALONE;

        this.gameMaster.userAccount.loginName = "ClientStandalone";
        this.gameMaster.userAccount.factionName = this.gameMaster.factionsManager.getFactionsNames().random();
//        game.userAccount.accountID = "accID_12345";
        this.playersManager = new PlayersManager(gameMaster.sessionSettings.sessionType, gameMaster.factionsManager, this.gameMaster.userAccount);
    }

    public GameScreen(GameMaster gameMaster, UserAccount userAccount) {
        super(gameMaster);
        Logger.logFuncStart();
        this.playersManager = new PlayersManager(gameMaster.sessionSettings.sessionType, gameMaster.factionsManager, userAccount);
    }

    @Override
    public void dispose() {
        Gdx.app.log("GameScreen::dispose()", "--");
        gameField.dispose();
        gameInterface.dispose();
        cameraController.dispose();
        gameFieldRenderer.dispose();
        playersManager.dispose();
    }

    public void initGameField() {
        Logger.logFuncStart();
        gameField = new GameField(this);
        gameInterface = new GameScreenInterface(this);
        cameraController = new GameScreenCameraController(this);
        gameFieldRenderer = new GameFieldRenderer(gameField, cameraController);
        gameInterface.setCameraController(cameraController);

        InputMultiplexer inputMultiplexer = new InputMultiplexer(Gdx.input.getInputProcessor());
        inputMultiplexer.addProcessor(new GestureDetector(gameInterface));
        inputMultiplexer.addProcessor(gameInterface);
        inputMultiplexer.addProcessor(cameraController);
        inputMultiplexer.addProcessor(new GestureDetector(cameraController));
        Gdx.input.setInputProcessor(inputMultiplexer);

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public boolean spawnUnitFromServerScreenByWaves() {
        return true; // useless;
    }

    @Override
    public void render(float delta) {
        super.render(delta);
//        Gdx.gl20.glClearColor(0, 0, 0, 1);
//        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (gameField == null) {
            initGameField();
        }

        GameState gameState = gameField.getGameState(); // Need change to enum GameState
        if (gameState == GameState.IN_PROGRESS) {
            cameraController.update(delta);
            gameField.update(delta, cameraController);
            gameFieldRenderer.render();
            gameInterface.render(delta);
        } else if (gameState == GameState.LOSE || gameState == GameState.WIN) {
//            gameField.dispose();
            gameInterface.renderEndGame(delta, gameState);
        } else if (gameState == GameState.LITTLE_GAME_WIN) {
            gameField.dispose();
            gameInterface.renderEndGame(delta, gameState);
        } else {
            Gdx.app.log("GameScreen::render()", "-- Not get normal gameState:" + gameState);
        }
//        super.render(delta);
        cameraController.inputHandler(delta);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        if (gameInterface != null) {
            gameInterface.resize(width, height);
        }
        if (cameraController != null) {
            cameraController.camera.viewportWidth = width;
            cameraController.camera.viewportHeight = height;
            cameraController.camera.update();
        }
        Logger.logDebug("New width:" + width + " height:" + height);
    }

    public void sendGameFieldVariables() {
        Logger.logFuncStart();
    }

    public Tower createTowerWithGoldCheck(int buildX, int buildY, TemplateForTower templateForTower) {
//        Logger.logFuncStart("buildX:" + buildX, "buildY:" + buildY, "templateForTower:" + templateForTower);
        return gameField.createTowerWithGoldCheck(buildX, buildY, templateForTower);
    }

//    public Tower towerToggle(int buildX, int buildY) {
//        Logger.logFuncStart("buildX:" + buildX, "buildY:" + buildY);
//        Cell cell = gameField.getCell(buildX, buildY);
//        if (cell != null) {
//            if (cell.isEmpty()) {
//                TemplateForTower randomTemplateForTower = gameField.factionsManager.getRandomTemplateForTowerFromAllFaction();
////                TemplateForTower small_fireTemplateForTower = gameField.factionsManager.getTemplateForTowerFromFirstFactionByName("small_fire");
//                Tower tower = gameField.createTowerWithGoldCheck(buildX, buildY, randomTemplateForTower);
//                if (tower != null) {
//                    gameField.rerouteAllUnits();
//                }
//                return tower;
//            } else if (cell.getTower() != null) {
//                cameraController.templateForTower = cell.getTower().templateForTower;
//                gameField.removeTowerWithGold(buildX, buildY, playersManager.getLocalPlayer());
//            }
//        }
//        return null;
//    }

    public Tower createTower(int buildX, int buildY) {
        Logger.logFuncStart("buildX:" + buildX, "buildY:" + buildY);
        Cell cell = gameField.getCell(buildX, buildY);
        if (cell != null) {
            if (cell.isEmpty()) {
                TemplateForTower randomTemplateForTower = gameField.factionsManager.getRandomTemplateForTowerFromAllFaction();
//                TemplateForTower small_fireTemplateForTower = gameField.factionsManager.getTemplateForTowerFromFirstFactionByName("small_fire");
                Tower tower = gameField.createTowerWithGoldCheck(buildX, buildY, randomTemplateForTower);
                if (tower != null) {
                    gameField.rerouteAllUnits();
                }
                return tower;
            }
        }
        return null;
    }

    public boolean removeTower(int buildX, int buildY) {
        Logger.logFuncStart("buildX:" + buildX, "buildY:" + buildY);
        Cell cell = gameField.getCell(buildX, buildY);
        if (cell != null) {
            if (cell.getTower() != null) {
                cameraController.templateForTower = cell.getTower().templateForTower;
                return gameField.removeTowerWithGold(buildX, buildY, playersManager.getLocalPlayer());
            }
        }
        return false;
    }

    public Unit createUnit(Cell spawnCell, Cell destCell, TemplateForUnit templateForUnit, Cell exitCell, Player player) {
        return gameField.createUnit(spawnCell, destCell, templateForUnit, exitCell, player);
    }

    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("GameScreen[");
        sb.append("gameField:" + gameField);
        if (full) {
            sb.append(",gameInterface:" + gameInterface);
            sb.append(",cameraController:" + cameraController);
        }
        sb.append("]");
        return sb.toString();
    }
}
