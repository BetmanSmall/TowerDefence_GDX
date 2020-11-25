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
import com.betmansmall.game.SessionThread;
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
import com.betmansmall.utils.logging.Logger;

public class GameScreen extends AbstractScreen {
    public GameField gameField;
    public GameScreenInterface gameInterface;
    public GameFieldRenderer gameFieldRenderer;
    public CameraController cameraController;

    public PlayersManager playersManager;
    public SessionThread sessionThread;

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

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(new GestureDetector(gameInterface));
        inputMultiplexer.addProcessor(gameInterface);
        inputMultiplexer.addProcessor(cameraController);
        inputMultiplexer.addProcessor(new GestureDetector(cameraController));
        Gdx.input.setInputProcessor(inputMultiplexer);

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void inputHandler(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
                Gdx.app.log("GameScreen::inputHandler()", "-- Gdx.input.isKeyJustPressed(Input.Keys.MINUS && ALT_LEFT)");
                gameInterface.infoTabloTable.setVisible(!gameInterface.infoTabloTable.isVisible());
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
                Logger.logDebug("-- Gdx.input.isKeyJustPressed(Input.Keys.C && ALT_LEFT)");
                gameInterface.playersViewTable.setVisible(!gameInterface.playersViewTable.isVisible());
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- Gdx.input.isKeyJustPressed(Input.Keys.MINUS)");
            if (cameraController.camera.zoom <= cameraController.zoomMax) {
                cameraController.camera.zoom += 0.1f;
            }
            cameraController.camera.update();
            gameInterface.addActionToHistory("-- cameraController.camera.zoom:" + cameraController.camera.zoom);
            Gdx.app.log("GameScreen::inputHandler()", "-- cameraController.camera.zoom:" + cameraController.camera.zoom);
            if (gameField.gameSpeed > 0.1f) {
                gameField.gameSpeed -= 0.1f;
            }
            this.sendGameFieldVariables();
            gameInterface.addActionToHistory("-- gameField.gameSpeed:" + gameField.gameSpeed);
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.gameSpeed:" + gameField.gameSpeed);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.PLUS)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- Gdx.input.isKeyJustPressed(Input.Keys.PLUS)");
            if (cameraController.camera.zoom >= cameraController.zoomMin) {
                cameraController.camera.zoom -= 0.1f;
            }
            cameraController.camera.update();
            gameInterface.addActionToHistory("-- cameraController.camera.zoom:" + cameraController.camera.zoom);
            Gdx.app.log("GameScreen::inputHandler()", "-- cameraController.camera.zoom:" + cameraController.camera.zoom);
            gameField.gameSpeed += 0.1f;
            this.sendGameFieldVariables();
            gameInterface.addActionToHistory("-- gameField.gameSpeed:" + gameField.gameSpeed);
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.gameSpeed:" + gameField.gameSpeed);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_0)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUM_0 || Input.Keys.NUMPAD_0)");
//            gameInterface.unitsSelector.changeGameState(); need func() here
            cameraController.isDrawableFullField = !cameraController.isDrawableFullField;
            cameraController.camera.position.set(0.0f, 0.0f, 0.0f);
            gameInterface.addActionToHistory("-- cameraController.camera.position:" + cameraController.camera.position);
            Gdx.app.log("GameScreen::inputHandler()", "-- cameraController.camera.position:" + cameraController.camera.position);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- Gdx.input.isKeyJustPressed(Input.Keys.SPACE)");
            gameInterface.startAndPauseButton.toggle();
            gameInterface.addActionToHistory("-- gameField.gamePaused:" + gameField.gamePaused);
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.gamePaused:" + gameField.gamePaused);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- Gdx.input.isKeyJustPressed(Input.Keys.F1)");
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                cameraController.isDrawableGrid--;
                if (cameraController.isDrawableGrid < 0) {
                    cameraController.isDrawableGrid = 5;
                }
            } else {
                cameraController.isDrawableGrid++;
                if (cameraController.isDrawableGrid > 5) {
                    cameraController.isDrawableGrid = 0;
                }
            }
            cameraController.isDrawableUnits = cameraController.isDrawableGrid;
            cameraController.isDrawableTowers = cameraController.isDrawableGrid;
            cameraController.isDrawableBackground = cameraController.isDrawableGrid;
            cameraController.isDrawableGround = cameraController.isDrawableGrid;
            cameraController.isDrawableForeground = cameraController.isDrawableGrid;
            cameraController.isDrawableGridNav = cameraController.isDrawableGrid;
            cameraController.isDrawableRoutes = cameraController.isDrawableGrid;
            gameInterface.addActionToHistory("-and other- cameraController.isDrawableGrid:" + cameraController.isDrawableGrid);
            Gdx.app.log("GameScreen::inputHandler()", "-and other- cameraController.isDrawableGrid:" + cameraController.isDrawableGrid);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUM_1 || Input.Keys.NUMPAD_1)");
            cameraController.isDrawableGrid++;
            if (cameraController.isDrawableGrid > 5) {
                cameraController.isDrawableGrid = 0;
            }
            gameInterface.addActionToHistory("-- cameraController.isDrawableGrid:" + cameraController.isDrawableGrid);
            Gdx.app.log("GameScreen::inputHandler()", "-- cameraController.isDrawableGrid:" + cameraController.isDrawableGrid);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUM_2 || Input.Keys.NUMPAD_2)");
            cameraController.isDrawableUnits++;
            if (cameraController.isDrawableUnits > 5) {
                cameraController.isDrawableUnits = 0;
            }
            gameInterface.addActionToHistory("-- cameraController.isDrawableUnits:" + cameraController.isDrawableUnits);
            Gdx.app.log("GameScreen::inputHandler()", "-- cameraController.isDrawableUnits:" + cameraController.isDrawableUnits);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_3)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUM_3 || Input.Keys.NUMPAD_3)");
            cameraController.isDrawableTowers++;
            if (cameraController.isDrawableTowers > 5) {
                cameraController.isDrawableTowers = 0;
            }
            gameInterface.addActionToHistory("-- cameraController.isDrawableTowers:" + cameraController.isDrawableTowers);
            Gdx.app.log("GameScreen::inputHandler()", "-- cameraController.isDrawableTowers:" + cameraController.isDrawableTowers);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_4)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUM_4 || Input.Keys.NUMPAD_4)");
            cameraController.isDrawableBackground++;
            if (cameraController.isDrawableBackground > 5) {
                cameraController.isDrawableBackground = 0;
            }
            gameInterface.addActionToHistory("-- cameraController.isDrawableBackground:" + cameraController.isDrawableBackground);
            Gdx.app.log("GameScreen::inputHandler()", "-- cameraController.isDrawableBackground:" + cameraController.isDrawableBackground);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_5)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUM_5 || Input.Keys.NUMPAD_5)");
            cameraController.isDrawableGround++;
            if (cameraController.isDrawableGround > 5) {
                cameraController.isDrawableGround = 0;
            }
            gameInterface.addActionToHistory("-- cameraController.isDrawableGround:" + cameraController.isDrawableGround);
            Gdx.app.log("GameScreen::inputHandler()", "-- cameraController.isDrawableGround:" + cameraController.isDrawableGround);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_6)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUM_6 || Input.Keys.NUMPAD_6)");
            cameraController.isDrawableForeground++;
            if (cameraController.isDrawableForeground > 5) {
                cameraController.isDrawableForeground = 0;
            }
            gameInterface.addActionToHistory("-- cameraController.isDrawableForeground:" + cameraController.isDrawableForeground);
            Gdx.app.log("GameScreen::inputHandler()", "-- cameraController.isDrawableForeground:" + cameraController.isDrawableForeground);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_7)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUM_7 || Input.Keys.NUMPAD_7)");
            cameraController.isDrawableGridNav++;
            if (cameraController.isDrawableGridNav > 5) {
                cameraController.isDrawableGridNav = 0;
            }
            gameInterface.addActionToHistory("-- cameraController.isDrawableGridNav:" + cameraController.isDrawableGridNav);
            Gdx.app.log("GameScreen::inputHandler()", "-- cameraController.isDrawableGridNav:" + cameraController.isDrawableGridNav);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_8)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUM_8 || Input.Keys.NUMPAD_8)");
            cameraController.isDrawableRoutes++;
            if (cameraController.isDrawableRoutes > 5) {
                cameraController.isDrawableRoutes = 0;
            }
            gameInterface.addActionToHistory("-- cameraController.isDrawableRoutes:" + cameraController.isDrawableRoutes);
            Gdx.app.log("GameScreen::inputHandler()", "-- cameraController.isDrawableRoutes:" + cameraController.isDrawableRoutes);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_9) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_9)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUM_9 || Input.Keys.NUMPAD_9)");
            cameraController.drawOrder++;
            if (cameraController.drawOrder > 8) {
                cameraController.drawOrder = 0;
            }
            gameInterface.addActionToHistory("-- cameraController.drawOrder:" + cameraController.drawOrder);
            Gdx.app.log("GameScreen::inputHandler()", "-- cameraController.drawOrder:" + cameraController.drawOrder);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.BACK)");
            gameMaster.removeTopScreen();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.ENTER)");
            gameMaster.nextGameLevel();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.A)");
            gameField.turnLeft();
            gameInterface.addActionToHistory("-- gameField.turnLeft()");
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.turnLeft()");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.S)");
            gameField.turnRight();
            gameInterface.addActionToHistory("-- gameField.turnRight()");
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.turnRight()");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.Q)");
            gameField.flipX();
            gameInterface.addActionToHistory("-- gameField.flipX()");
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.flipX()");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.W)");
            gameField.flipY();
            gameInterface.addActionToHistory("-- gameField.flipY()");
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.flipY()");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.PERIOD)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.PERIOD)");
            gameInterface.arrayActionsHistory.clear();
            gameInterface.addActionToHistory("-- gameInterface.arrayActionsHistory.clear()");
            Gdx.app.log("GameScreen::inputHandler()", "-- gameInterface.arrayActionsHistory.clear()");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.ESCAPE)");
            gameInterface.pauseMenuButton.toggle();
            gameInterface.addActionToHistory("-- gameInterface.pauseMenuButton.toggle()");
            Gdx.app.log("GameScreen::inputHandler()", "-- gameInterface.pauseMenuButton.toggle()");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.N)");
            gameField.cancelUnderConstruction();
            gameInterface.addActionToHistory("-- gameField.cancelUnderConstruction()");
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.cancelUnderConstruction()");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.B)");
            UnderConstruction underConstruction = gameField.createdRandomUnderConstruction();
            gameInterface.addActionToHistory("-- factionsManager.createdRandomUnderConstruction(" + underConstruction.templateForTower.name + ")");
            Gdx.app.log("GameScreen::inputHandler()", "-- factionsManager.createdRandomUnderConstruction(" + underConstruction.templateForTower.name + ")");
        }
    }

    public boolean spawnUnitFromServerScreenByWaves() {
        return true; // useless;
    }

    @Override
    public void render(float delta) {
//      Gdx.app.log("GameScreen::render()", "-- delta:" + delta + " FPS:" + Gdx.graphics.getFramesPerSecond());
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (gameField == null) {
            initGameField();
        }

        inputHandler(delta);

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
    }

    @Override
    public void resize(int width, int height) {
//        Gdx.app.log("GameScreen::resize(" + width + ", " + height + ")", "--");
        if (gameInterface != null) {
            gameInterface.resize(width, height);
        }
        if (cameraController != null) {
            cameraController.camera.viewportWidth = width;
            cameraController.camera.viewportHeight = height;
            cameraController.camera.update();
        }
    }

    public void sendGameFieldVariables() {
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
