package com.betmansmall.game.gameInterface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.betmansmall.enums.GameState;
import com.betmansmall.enums.GameType;
import com.betmansmall.game.gameLogic.CameraController;
import com.betmansmall.game.gameLogic.Tower;
import com.betmansmall.game.gameLogic.UnderConstruction;
import com.betmansmall.screens.client.GameScreen;
import com.betmansmall.utils.logging.Logger;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;

public class GameScreenInterface extends GameInterface {
    private final GameScreen gameScreen;

    private float currentTextureTime, maxTextureTime;
    private Texture winTexture, loseTexture;

    private VisLabel connectedPlayerCount;
    private VisLabel unitsCount; // duplicate unitsManagerSize

    public GameScreenInterface(final GameScreen gameScreen) {
        super();
        Logger.logFuncStart();
        this.gameScreen = gameScreen;

        this.currentTextureTime = 0f;
        this.maxTextureTime = 5f;
        this.winTexture = new Texture(Gdx.files.internal("concepts/littlegame-concept-2-1.jpg"));
        this.loseTexture = new Texture(Gdx.files.internal("concepts/2018-12-03_19-43-03.png"));

        pauseMenuTable = new VisTable();
        pauseMenuTable.setFillParent(true);
        pauseMenuTable.setVisible(false);
        addActor(pauseMenuTable);

        firstOptionTable = new VisTable();
        firstOptionTable.setVisible(false);
        pauseMenuTable.add(firstOptionTable);

        optionTable = new VisTable();
        optionTable.setVisible(false);
        pauseMenuTable.add(optionTable);

        Table verticalButtonsTable = new Table();
        pauseMenuTable.add(verticalButtonsTable);

        resumeButton = new VisTextButton("RESUME");
        verticalButtonsTable.add(resumeButton).fill().prefWidth(Gdx.graphics.getHeight()*0.2f).prefHeight(Gdx.graphics.getHeight()*0.2f).row();
        nextLevelButton = new VisTextButton("NEXT LEVEL");
        verticalButtonsTable.add(nextLevelButton).fill().prefWidth(Gdx.graphics.getHeight()*0.2f).prefHeight(Gdx.graphics.getHeight()*0.2f).row();
        optionButton = new VisTextButton("OPTION");
        verticalButtonsTable.add(optionButton).fill().prefWidth(Gdx.graphics.getHeight()*0.2f).prefHeight(Gdx.graphics.getHeight()*0.2f).row();
        exitButton = new VisTextButton("EXIT");
        verticalButtonsTable.add(exitButton).fill().prefWidth(Gdx.graphics.getHeight()*0.2f).prefHeight(Gdx.graphics.getHeight()*0.2f).row();

        playersViewTable = new PlayersViewTable(gameScreen.playersManager, VisUI.getSkin());
        playersViewTable.setFillParent(true);
        playersViewTable.setVisible(false);
        addActor(playersViewTable.scrollPane);

        tableWithButtons = new VisTable();
        tableWithButtons.setFillParent(true);
        addActor(tableWithButtons);

        Table horizontalGroupTop = new VisTable();
        tableWithButtons.add(horizontalGroupTop).expandY().top().row();

        disconnectButtons = new VisTextButton("DISC(X)NNECT");
        horizontalGroupTop.add(disconnectButtons).prefWidth(Gdx.graphics.getHeight()*0.2f).prefHeight(Gdx.graphics.getHeight()*0.12f).expandY();

        playersViewButton = new VisTextButton("PLAYERS");
        horizontalGroupTop.add(playersViewButton).prefWidth(Gdx.graphics.getHeight()*0.2f).prefHeight(Gdx.graphics.getHeight()*0.12f).expandY();

        pauseMenuButton = new VisTextButton("||");
        horizontalGroupTop.add(pauseMenuButton).prefWidth(Gdx.graphics.getHeight()*0.2f).prefHeight(Gdx.graphics.getHeight()*0.12f).expandY();

        gridNav1 = new VisTextButton("gNv1");
        horizontalGroupTop.add(gridNav1).prefWidth(Gdx.graphics.getWidth()*0.01f).prefHeight(Gdx.graphics.getHeight()*0.01f);

        gridNav2 = new VisTextButton("gridNav2");
        horizontalGroupTop.add(gridNav2).prefWidth(Gdx.graphics.getWidth()*0.02f).prefHeight(Gdx.graphics.getHeight()*0.01f);

        gridNav3 = new VisTextButton("gridNav2");
        horizontalGroupTop.add(gridNav3).prefWidth(Gdx.graphics.getWidth()*0.01f).prefHeight(Gdx.graphics.getHeight()*0.01f).row();

        connectedPlayerCount = new VisLabel("players:{players.size}");
        horizontalGroupTop.add(connectedPlayerCount).colspan(3).row();

        unitsCount = new VisLabel("unitsCount:{unitsCount}");
        horizontalGroupTop.add(unitsCount).colspan(3).row();

        Table horizontalGroupBottom = new VisTable();
        tableWithButtons.add(horizontalGroupBottom).expandY().bottom();

        gameSpeedMinus = new VisTextButton("<<");
        horizontalGroupBottom.add(gameSpeedMinus).prefWidth(Gdx.graphics.getWidth()*0.1f).prefHeight(Gdx.graphics.getHeight()*0.1f);

        startAndPauseButton = new VisTextButton("startAndPauseButton", "default");
        horizontalGroupBottom.add(startAndPauseButton).prefWidth(Gdx.graphics.getWidth()*0.2f).prefHeight(Gdx.graphics.getHeight()*0.1f);

        gameSpeedPlus = new VisTextButton(">>");
        horizontalGroupBottom.add(gameSpeedPlus).prefWidth(Gdx.graphics.getWidth()*0.1f).prefHeight(Gdx.graphics.getHeight()*0.1f);

        tableInfoTablo = new VisTable();
        tableInfoTablo.setFillParent(true);
        addActor(tableInfoTablo);

        infoTabloTable = new VisTable();
        infoTabloTable.setVisible(false);
        tableInfoTablo.add(infoTabloTable).expand().left();

        fpsLabel = new VisLabel("FPS:000", new VisLabel.LabelStyle(bitmapFont, Color.WHITE));
        infoTabloTable.add(fpsLabel).left().row();
        deltaTimeLabel = new VisLabel("deltaTime:000", new VisLabel.LabelStyle(bitmapFont, Color.WHITE));
        infoTabloTable.add(deltaTimeLabel).left().row();
        mapPathLabel = new VisLabel("MapName:arena0tmx", new VisLabel.LabelStyle(bitmapFont, Color.WHITE));
        infoTabloTable.add(mapPathLabel).left().row();
        gameType = new VisLabel("gameType:", new VisLabel.LabelStyle(bitmapFont, Color.YELLOW));
        infoTabloTable.add(gameType).left().row();
        isometricLabel = new VisLabel("isometricLabel:", new VisLabel.LabelStyle(bitmapFont, Color.WHITE));
        infoTabloTable.add(isometricLabel).left().row();
        underConstrEndCoord = new VisLabel("CoordCell:(0,0)", new VisLabel.LabelStyle(bitmapFont, Color.WHITE));
        infoTabloTable.add(underConstrEndCoord).left().row();
        underConstructionLabel = new VisLabel("UnderConstrTemplateName:tower1", new VisLabel.LabelStyle(bitmapFont, Color.WHITE));
        infoTabloTable.add(underConstructionLabel).left().row();
        unitsManagerSize = new VisLabel("unitsManagerSize:", new VisLabel.LabelStyle(bitmapFont, Color.GREEN));
        infoTabloTable.add(unitsManagerSize).left().row();
        towersManagerSize = new VisLabel("towersManagerSize:", new VisLabel.LabelStyle(bitmapFont, Color.YELLOW));
        infoTabloTable.add(towersManagerSize).left().row();
        gamerGoldLabel = new VisLabel("GamerGold:000", new VisLabel.LabelStyle(bitmapFont, Color.YELLOW));
        infoTabloTable.add(gamerGoldLabel).left().row();
        missedAndMaxForPlayer1 = new VisLabel("UnitsLimitPL1:10/100", new VisLabel.LabelStyle(bitmapFont, Color.GREEN));
        infoTabloTable.add(missedAndMaxForPlayer1).left().row();
        missedAndMaxForComputer0 = new VisLabel("UnitsLimitComp0:10/100", new VisLabel.LabelStyle(bitmapFont, Color.RED));
        infoTabloTable.add(missedAndMaxForComputer0).left().row();
        nextUnitSpawnLabel = new VisLabel("NextUnitSpawnAfter:0.12sec", new VisLabel.LabelStyle(bitmapFont, Color.ORANGE));
        infoTabloTable.add(nextUnitSpawnLabel).left().row();
        unitsSpawn = new VisLabel("unitsSpawn:", new VisLabel.LabelStyle(bitmapFont, Color.RED));
        infoTabloTable.add(unitsSpawn).left().row();
        gamePaused = new VisLabel("gamePaused:", new VisLabel.LabelStyle(bitmapFont, Color.GREEN));
        infoTabloTable.add(gamePaused).left().row();

        tableWithSelectors = new VisTable(); // WTF??? почему нельзя селекторы на одну таблицу со всем остальным??
        tableWithSelectors.setFillParent(true);
        addActor(tableWithSelectors);

        tableTowerButtons = new VisTable();
        addActor(tableTowerButtons);

        sellTowerBtn = new VisTextButton("SELL");
        sellTowerBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Tower tower = gameScreen.playersManager.getLocalPlayer().selectedTower;
                if (tower != null) {
                    gameScreen.removeTower(tower.cell.cellX, tower.cell.cellY);
                }
            }
        });
        tableTowerButtons.add(sellTowerBtn);

        upgradeTowerBtn = new VisTextButton("UP");
        upgradeTowerBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Tower tower = gameScreen.playersManager.getLocalPlayer().selectedTower;
                if (tower != null) {
                    tower.upgrade();
                }
            }
        });
        tableTowerButtons.add(upgradeTowerBtn);

        closeTowerBtn = new VisTextButton("X");
        closeTowerBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                tableTowerButtons.setVisible(false);
                gameScreen.playersManager.getLocalPlayer().selectedTower = null;
            }
        });
        tableTowerButtons.add(closeTowerBtn);

        addListeners();
//        resize();
    }

    @Override
    public void dispose() {
        super.dispose();
        this.winTexture.dispose();
        this.loseTexture.dispose();
    }

    public void addListeners() {
        Logger.logFuncStart();
        disconnectButtons.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface:changed:addListeners()", "-- disconnectButtons.isChecked():" + disconnectButtons.isChecked());
//                gameScreen.dispose();
                gameScreen.gameMaster.removeTopScreen();
            }
        });
        playersViewButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface:changed:addListeners()", "-- playersViewButton.isChecked():" + playersViewButton.isChecked());
                playersViewTable.setVisible(!playersViewTable.isVisible());
            }
        });
        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface:changed:addListeners()", "-- resumeButton.isChecked():" + resumeButton.isChecked());
                pauseMenuButton.toggle();
            }
        });
        nextLevelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface:changed:addListeners()", "-- nextLevelButton.isChecked():" + nextLevelButton.isChecked());
                gameScreen.gameMaster.nextGameLevel();
            }
        });
        optionButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface:changed:addListeners()", "-- optionButton.isChecked():" + optionButton.isChecked());
                optionTable.setVisible(optionButton.isChecked());
                firstOptionTable.setVisible(optionButton.isChecked());
            }
        });
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface:changed:addListeners()", "-- exitButton.isChecked():" + exitButton.isChecked());
                gameScreen.gameMaster.removeTopScreen();
            }
        });
        gridNav1.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface:changed:addListeners()", "-- gridNav1.isChecked():" + gridNav1.isChecked());
                cameraController.isDrawableGrid++;
                cameraController.isDrawableGridNav++;
                cameraController.isDrawableRoutes++;
                if (cameraController.isDrawableGrid > 5 || cameraController.isDrawableGridNav > 5 || cameraController.isDrawableRoutes > 5) {
                    cameraController.isDrawableGrid = 0;
                    cameraController.isDrawableGridNav = 0;
                    cameraController.isDrawableRoutes = 0;
                    gridNav1.setVisible(true);
                    gridNav2.setVisible(true);
                    gridNav3.setVisible(true);
                }
            }
        });
        gridNav2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface:changed:addListeners()", "-- gridNav2.isChecked():" + gridNav2.isChecked());
                cameraController.isDrawableGridNav = 0;
                cameraController.isDrawableRoutes = 0;
                gridNav2.setVisible(false);
            }
        });
        gridNav3.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface:changed:addListeners()", "-- gridNav3.isChecked():" + gridNav3.isChecked());
                cameraController.isDrawableGridNav = 5;
                cameraController.isDrawableRoutes = 5;
                gridNav3.setVisible(false);
            }
        });
        pauseMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface:changed:addListeners()", "-- pauseMenuButton.isChecked():" + pauseMenuButton.isChecked());
                boolean gamePaused = pauseMenuButton.isChecked();
                gameScreen.gameField.gamePaused = gamePaused;
                pauseMenuTable.setVisible(gamePaused);
                tableWithSelectors.setVisible(!gamePaused);
                tableWithButtons.setVisible(!gamePaused);
                if (playersViewTable.isVisible()) {
                    playersViewTable.setVisible(false);
                }
                interfaceTouched = true;
                gameScreen.sendGameFieldVariables();
            }
        });
        gameSpeedMinus.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface:changed:addListeners()", "-- gameSpeedMinus.isChecked():" + gameSpeedMinus.isChecked());
                if (gameScreen.gameField.gameSpeed > 0.1f) {
                    gameScreen.gameField.gameSpeed -= 0.1f;
                    gameScreen.sendGameFieldVariables();
                }
            }
        });
        startAndPauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface:changed:addListeners()", "-- startAndPauseButton.isChecked():" + startAndPauseButton.isChecked());
//                if (!gameScreen.gameField.gamePaused) {
//                    if (gameScreen.gameField.gameSpeed < 1f) {
//                        gameScreen.gameField.gameSpeed = 1f;
//                    } else if (gameScreen.gameField.gameSpeed < 2f) {
//                        gameScreen.gameField.gameSpeed = 2f;
//                    } else if (gameScreen.gameField.gameSpeed < 3f) {
//                        gameScreen.gameField.gameSpeed = 3f;
//                    } else if (gameScreen.gameField.gameSpeed < 4f) {
//                        gameScreen.gameField.gameSpeed = 4f;
//                    } else if (gameScreen.gameField.gameSpeed < 5f) {
//                        gameScreen.gameField.gameSpeed = 5f;
//                    } else {
//                        gameScreen.gameField.gamePaused = !gameScreen.gameField.gamePaused;
//                    }
//                } else {
                gameScreen.gameField.gameSpeed = 1f;
                gameScreen.gameField.gamePaused = !gameScreen.gameField.gamePaused;
                if (!gameScreen.gameField.unitsSpawn && gameScreen.gameField.unitsManager.units.size == 0) {
                    gameScreen.gameField.unitsSpawn = true;
                    gameScreen.gameField.gamePaused = false;
                }
//                }
                gameScreen.sendGameFieldVariables();
                interfaceTouched = true;
            }
        });
        gameSpeedPlus.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface:changed:addListeners()", "-- gameSpeedPlus.isChecked():" + gameSpeedPlus.isChecked());
                gameScreen.gameField.gameSpeed += 0.1f;
                gameScreen.sendGameFieldVariables();
            }
        });
    }

    public void setCameraController(final CameraController cameraController) {
        this.cameraController = cameraController;

        infoTabloHideButton = new VisTextButton("Hide Info Tablo");
        infoTabloHideButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface::setCameraController()", "-changed- infoTabloHideButton.isChecked():" + infoTabloHideButton.isChecked());
                infoTabloTable.setVisible(infoTabloHideButton.isChecked());
                tableWithButtons.setVisible(infoTabloHideButton.isChecked());
                tableWithSelectors.setVisible(infoTabloHideButton.isChecked());
                if (infoTabloTable.isVisible()) {
                    infoTabloHideButton.setText("Hide Info Tablo");
                } else {
                    infoTabloHideButton.setText("Show Info Tablo");
                }
            }
        });
        optionTable.add(infoTabloHideButton).colspan(2).fill().prefHeight(Gdx.graphics.getHeight()*0.1f).row();

        final VisLabel drawGridLabel = new VisLabel("drawGrid:" + cameraController.isDrawableGrid);
        drawGridLabel.setFontScale(Gdx.graphics.getHeight()*0.003f);
        optionTable.add(drawGridLabel).left();

        drawGrid = new VisSlider(0, 5, 1, false);
        drawGrid.setValue(cameraController.isDrawableGrid);
        drawGrid.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface::setCameraController()", "-changed- drawGrid.getValue():" + drawGrid.getValue());
                cameraController.isDrawableGrid = (int)drawGrid.getValue();
                drawGridLabel.setText("drawGrid:" + cameraController.isDrawableGrid);
            }
        });
        drawGrid.getStyle().knob.setMinWidth(Gdx.graphics.getHeight()*0.05f);
        drawGrid.getStyle().knob.setMinHeight(Gdx.graphics.getHeight()*0.05f);
        optionTable.add(drawGrid).expand().fill().prefWidth(Gdx.graphics.getHeight()*0.3f).row();

        final VisLabel drawUnitsLabel = new VisLabel("drawUnits:" + cameraController.isDrawableUnits);
        drawUnitsLabel.setFontScale(Gdx.graphics.getHeight()*0.003f);
        optionTable.add(drawUnitsLabel).left();

        drawUnits = new VisSlider(0, 5, 1, false);
        drawUnits.setValue(cameraController.isDrawableUnits);
        drawUnits.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface::setCameraController()", "-changed- drawUnits.getValue():" + drawUnits.getValue());
                cameraController.isDrawableUnits = (int)drawUnits.getValue();
                drawUnitsLabel.setText("drawUnits:" + cameraController.isDrawableUnits);
            }
        });
        optionTable.add(drawUnits).fill().row();

        final VisLabel drawTowersLabel = new VisLabel("drawTowers:" + cameraController.isDrawableTowers);
        drawTowersLabel.setFontScale(Gdx.graphics.getHeight()*0.003f);
        optionTable.add(drawTowersLabel).left();

        drawTowers = new VisSlider(0, 5, 1, false);
        drawTowers.setValue(cameraController.isDrawableTowers);
        drawTowers.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface::setCameraController()", "-changed- drawTowers.getValue():" + drawTowers.getValue());
                cameraController.isDrawableTowers = (int)drawTowers.getValue();
                drawTowersLabel.setText("drawTowers:" + cameraController.isDrawableTowers);
            }
        });
        optionTable.add(drawTowers).fill().row();

        final VisLabel drawBackgroundLabel = new VisLabel("drawBackground:" + cameraController.isDrawableBackground);
        drawBackgroundLabel.setFontScale(Gdx.graphics.getHeight()*0.003f);
        optionTable.add(drawBackgroundLabel).left();

        drawBackground = new VisSlider(0, 5, 1, false);
        drawBackground.setValue(cameraController.isDrawableBackground);
        drawBackground.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface::setCameraController()", "-changed- drawBackground.getValue():" + drawBackground.getValue());
                cameraController.isDrawableBackground = (int)drawBackground.getValue();
                drawBackgroundLabel.setText("drawBackground:" + cameraController.isDrawableBackground);
            }
        });
        optionTable.add(drawBackground).fill().row();

        final VisLabel drawGroundLabel = new VisLabel("drawGround:" + cameraController.isDrawableGround);
        drawGroundLabel.setFontScale(Gdx.graphics.getHeight()*0.003f);
        optionTable.add(drawGroundLabel).left();

        drawGround = new VisSlider(0, 5, 1, false);
        drawGround.setValue(cameraController.isDrawableGround);
        drawGround.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface::setCameraController()", "-changed- drawGround.getValue():" + drawGround.getValue());
                cameraController.isDrawableGround = (int)drawGround.getValue();
                drawGroundLabel.setText("drawGround:" + cameraController.isDrawableGround);
            }
        });
        optionTable.add(drawGround).fill().row();

        final VisLabel drawForegroundLabel = new VisLabel("drawForeground:" + cameraController.isDrawableForeground);
        drawForegroundLabel.setFontScale(Gdx.graphics.getHeight()*0.003f);
        optionTable.add(drawForegroundLabel).left();

        drawForeground = new VisSlider(0, 5, 1, false);
        drawForeground.setValue(cameraController.isDrawableForeground);
        drawForeground.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface::setCameraController()", "-changed- drawForeground.getValue():" + drawForeground.getValue());
                cameraController.isDrawableForeground = (int)drawForeground.getValue();
                drawForegroundLabel.setText("drawForeground:" + cameraController.isDrawableForeground);
            }
        });
        optionTable.add(drawForeground).fill().row();

        final VisLabel drawGridNavLabel = new VisLabel("drawGridNav:" + cameraController.isDrawableGridNav);
        drawGridNavLabel.setFontScale(Gdx.graphics.getHeight()*0.003f);
        optionTable.add(drawGridNavLabel).left();

        drawGridNav = new VisSlider(0, 5, 1, false);
        drawGridNav.setValue(cameraController.isDrawableGridNav);
        drawGridNav.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface::setCameraController()", "-changed- drawGridNav.getValue():" + drawGridNav.getValue());
                cameraController.isDrawableGridNav = (int)drawGridNav.getValue();
                drawGridNavLabel.setText("drawGridNav:" + cameraController.isDrawableGridNav);
            }
        });
        optionTable.add(drawGridNav).fill().row();

        final VisLabel drawRoutesLabel = new VisLabel("drawRoutes:" + cameraController.isDrawableRoutes);
        drawRoutesLabel.setFontScale(Gdx.graphics.getHeight()*0.003f);
        optionTable.add(drawRoutesLabel).left();

        drawRoutes = new VisSlider(0, 5, 1, false);
        drawRoutes.setValue(cameraController.isDrawableRoutes);
        drawRoutes.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface::setCameraController()", "-changed- drawRoutes.getValue():" + drawRoutes.getValue());
                cameraController.isDrawableRoutes = (int)drawRoutes.getValue();
                drawRoutesLabel.setText("drawRoutes:" + cameraController.isDrawableRoutes);
            }
        });
        optionTable.add(drawRoutes).fill().row();

        final VisLabel drawOrderLabel = new VisLabel("drawOrder:" + cameraController.drawOrder);
        drawOrderLabel.setFontScale(Gdx.graphics.getHeight()*0.003f);
        optionTable.add(drawOrderLabel).left();

        drawOrder = new VisSlider(0, 8, 1, false);
        drawOrder.setValue(cameraController.drawOrder);
        drawOrder.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface::setCameraController()", "-changed- drawOrder.getValue():" + drawOrder.getValue());
                cameraController.drawOrder = (int)drawOrder.getValue();
                drawOrderLabel.setText("drawOrder:" + cameraController.drawOrder);
            }
        });
        optionTable.add(drawOrder).fill().row();

        resetDrawSettingsButton = new VisTextButton("Reset Draw Settings");
        resetDrawSettingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface::setCameraController()", "-changed- resetDrawSettingsButton.isChecked():" + resetDrawSettingsButton.isChecked());
                if (resetDrawSettingsButton.isChecked()) {
                    cameraController.camera.position.set(0.0f, 0.0f, 0.0f);
                } else {
                    drawAll.setValue(-1); // libgdx... not call changed() if value == value
                    drawAll.setValue(1);
                }
                resetDrawSettingsButton.setText(resetDrawSettingsButton.isChecked() ? "reset camera POS" : "Reset Draw Settings");
            }
        });
        optionTable.add(resetDrawSettingsButton).colspan(2).fill().prefHeight(Gdx.graphics.getHeight()*0.1f).row();

        final VisLabel drawAllLabel = new VisLabel("drawAll:" + cameraController.isDrawableGrid);
//        drawAllLabel.setFontScale(Gdx.graphics.getHeight()*0.003f);
        optionTable.add(drawAllLabel).left();

        drawAll = new VisSlider(0, 5, 1, false);
        drawAll.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface::setCameraController()", "-changed- drawAll.getValue():" + drawAll.getValue());
                drawGrid.setValue(drawAll.getValue());
                drawUnits.setValue(drawAll.getValue());
                drawTowers.setValue(drawAll.getValue());
                drawBackground.setValue(drawAll.getValue());
                drawGround.setValue(drawAll.getValue());
                drawForeground.setValue(drawAll.getValue());
                drawGridNav.setValue(drawAll.getValue());
                drawRoutes.setValue(drawAll.getValue());
                drawAllLabel.setText("drawAll:" + drawAll.getValue());
            }
        });
        drawAll.setValue(cameraController.isDrawableGrid);
        optionTable.add(drawAll).fill().row();

        topBottomLeftRightSelector = new VisCheckBox("topBottomLeftRightSelector");
        topBottomLeftRightSelector.setChecked(gameScreen.gameField.gameSettings.topBottomLeftRightSelector);
//        topBottomLeftRightSelector.getBackgroundImage().setScaling(Scaling.stretch);
//        topBottomLeftRightSelector.getTickImage().scaleBy(Gdx.graphics.getHeight()*0.06f);
//        topBottomLeftRightSelector.getLabel().setFontScale(Gdx.graphics.getHeight()*0.003f);
        topBottomLeftRightSelector.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface::setCameraController()", "-- topBottomLeftRightSelector.isChecked():" + topBottomLeftRightSelector.isChecked());
                gameScreen.gameField.gameSettings.topBottomLeftRightSelector = topBottomLeftRightSelector.isChecked();
                if (unitsSelector != null) {
                    unitsSelector.updateBorders(gameScreen.gameField.gameSettings.verticalSelector,
                            !gameScreen.gameField.gameSettings.topBottomLeftRightSelector,
                            gameScreen.gameField.gameSettings.smoothFlingSelector);
                }
                if (towersSelector != null) {
                    towersSelector.updateBorders(gameScreen.gameField.gameSettings.verticalSelector,
                            gameScreen.gameField.gameSettings.topBottomLeftRightSelector,
                            gameScreen.gameField.gameSettings.smoothFlingSelector);
                }
            }
        });
        optionTable.add(topBottomLeftRightSelector).colspan(2).fill().row();

        towerMoveAlgorithm = new VisCheckBox("towerMoveAlgorithm");
        towerMoveAlgorithm.setChecked(gameScreen.gameField.gameSettings.towerMoveAlgorithm);
        towerMoveAlgorithm.getBackgroundImage().setScaling(Scaling.stretch);
//        towerMoveAlgorithm.getTickImage().scaleBy(Gdx.graphics.getHeight()*0.06f);
//        towerMoveAlgorithm.getLabel().setFontScale(Gdx.graphics.getHeight()*0.003f);
        towerMoveAlgorithm.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface::setCameraController()", "-- towerMoveAlgorithm.isChecked():" + towerMoveAlgorithm.isChecked());
                gameScreen.gameField.gameSettings.towerMoveAlgorithm = towerMoveAlgorithm.isChecked();
                UnderConstruction underConstruction = gameScreen.gameField.getUnderConstruction();
                if (underConstruction != null) {
                    underConstruction.setBuildType(gameScreen.gameField.gameSettings.towerMoveAlgorithm);
                }
            }
        });
        firstOptionTable.add(towerMoveAlgorithm).colspan(2).fill().row();

        verticalSelector = new VisCheckBox("verticalSelector");
        verticalSelector.setChecked(gameScreen.gameField.gameSettings.verticalSelector);
        verticalSelector.getBackgroundImage().setScaling(Scaling.stretch);
//        verticalSelector.getTickImage().scaleBy(Gdx.graphics.getHeight()*0.06f);
//        verticalSelector.getLabel().setFontScale(Gdx.graphics.getHeight()*0.003f);
        verticalSelector.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface::setCameraController()", "-- verticalSelector.isChecked():" + verticalSelector.isChecked());
                gameScreen.gameField.gameSettings.verticalSelector = verticalSelector.isChecked();
                if (unitsSelector != null) {
                    unitsSelector.updateBorders(gameScreen.gameField.gameSettings.verticalSelector,
                            !gameScreen.gameField.gameSettings.topBottomLeftRightSelector,
                            gameScreen.gameField.gameSettings.smoothFlingSelector);
                }
                if (towersSelector != null) {
                    towersSelector.updateBorders(gameScreen.gameField.gameSettings.verticalSelector,
                            gameScreen.gameField.gameSettings.topBottomLeftRightSelector,
                            gameScreen.gameField.gameSettings.smoothFlingSelector);
                }
            }
        });
        optionTable.add(verticalSelector).colspan(2).fill().row();

        smoothFlingSelector = new VisCheckBox("smoothFlingSelector");
        smoothFlingSelector.setChecked(gameScreen.gameField.gameSettings.smoothFlingSelector);
//        smoothFlingSelector.getBackgroundImage().setScaling(Scaling.stretch);
//        smoothFlingSelector.getTickImage().setScale(Gdx.graphics.getHeight()*0.06f);
//        smoothFlingSelector.getLabel().setFontScale(Gdx.graphics.getHeight()*0.003f);
        smoothFlingSelector.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface::setCameraController()", "-- smoothFlingSelector.isChecked():" + smoothFlingSelector.isChecked());
                gameScreen.gameField.gameSettings.smoothFlingSelector = smoothFlingSelector.isChecked();
                if (unitsSelector != null) {
                    unitsSelector.updateBorders(gameScreen.gameField.gameSettings.verticalSelector,
                            !gameScreen.gameField.gameSettings.topBottomLeftRightSelector,
                            gameScreen.gameField.gameSettings.smoothFlingSelector);
                }
                if (towersSelector != null) {
                    towersSelector.updateBorders(gameScreen.gameField.gameSettings.verticalSelector,
                            gameScreen.gameField.gameSettings.topBottomLeftRightSelector,
                            gameScreen.gameField.gameSettings.smoothFlingSelector);
                }
            }
        });
        optionTable.add(smoothFlingSelector).colspan(2).fill().row();

        if (cameraController.gameField.waveManager.wavesForUser.size > 0) {
            unitsSelector = new UnitsSelector(gameScreen);
            tableWithSelectors.add(unitsSelector).expand();
        }

        if (cameraController.gameField.gameSettings.gameType == GameType.TowerDefence) {
            towersSelector = new TowersSelector(gameScreen);
            tableWithSelectors.add(towersSelector).expand();

            towersSelectorCoord = new VisLabel("towersSelectorCoord:", new VisLabel.LabelStyle(bitmapFont, Color.GREEN));
            infoTabloTable.add(towersSelectorCoord).left().row();
            selectorBorderVertical = new VisLabel("selectorBorderVertical:", new VisLabel.LabelStyle(bitmapFont, Color.WHITE));
            infoTabloTable.add(selectorBorderVertical).left().row();
            selectorBorderHorizontal = new VisLabel("selectorBorderHorizontal:", new VisLabel.LabelStyle(bitmapFont, Color.WHITE));
            infoTabloTable.add(selectorBorderHorizontal).left().row();
        }
    }

    protected Vector2 getStagePositionOfWorldPosition(Circle cicrle) {
        if (cicrle != null) {
            this.getBatch().setProjectionMatrix(this.getCamera().combined);
            Vector3 screenPosition = cameraController.camera.project(new Vector3(cicrle.x, cicrle.y, 0));
            return new Vector2(screenPosition.x, screenPosition.y);
        }
        return null;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        Tower tower = gameScreen.playersManager.getLocalPlayer().selectedTower;
        if (tower != null) {
//            Logger.logDebug("tower:" + tower);
            Vector2 vector2 = getStagePositionOfWorldPosition(tower.getCircle(cameraController.isDrawableTowers));
            if (vector2 != null) {
                tableTowerButtons.setPosition(vector2.x, vector2.y - (-1f * cameraController.camera.zoom)*50f, Align.center);
                if (!tableTowerButtons.isVisible()) {
                    tableTowerButtons.setVisible(true);
                }
            }
        } else {
            if (tableTowerButtons.isVisible()) {
                tableTowerButtons.setVisible(false);
            }
        }

        fpsLabel.setText("FPS:" + String.valueOf(Gdx.graphics.getFramesPerSecond()));
        deltaTimeLabel.setText("delta:" + String.valueOf(delta));
        mapPathLabel.setText("gameScreen.gameField.tmxMap.mapPath:" + gameScreen.gameField.tmxMap.mapPath);
        gameType.setText("gameScreen.gameField.gameSettings.gameType:" + gameScreen.gameField.gameSettings.gameType);
        isometricLabel.setText("gameScreen.gameField.tmxMap.isometric:" + gameScreen.gameField.tmxMap.isometric);
        UnderConstruction underConstruction = gameScreen.gameField.getUnderConstruction();
        if (underConstruction != null) {
            underConstrEndCoord.setText("underConstructionEndCoord:(" + underConstruction.endX + "," + underConstruction.endY + ")");
            underConstructionLabel.setText("underConstructionTemplateName:" + underConstruction.templateForTower.name);
            underConstructionLabel.setColor(Color.GREEN);
        } else {
            underConstrEndCoord.setText("underConstructionEndCoord:(WTF,WTF)");
            underConstructionLabel.setText("underConstructionTemplateName:NULL");
            underConstructionLabel.setColor(Color.RED);
        }
        unitsManagerSize.setText("gameScreen.gameField.unitsManager.units.size:" + gameScreen.gameField.unitsManager.units.size);
        towersManagerSize.setText("gameScreen.gameField.towersManager.towers.size:" + gameScreen.gameField.towersManager.towers.size);
        if (gameScreen.playersManager.getLocalPlayer() != null) {
            gamerGoldLabel.setText("getLocalPlayer().gold:" + gameScreen.playersManager.getLocalPlayer().gold);
            missedAndMaxForPlayer1.setText("UnitsLimitPL1:" + gameScreen.playersManager.getLocalPlayer().missedUnits + "/" + gameScreen.playersManager.getLocalPlayer().maxOfMissedUnits);
        }
        if (gameScreen.playersManager.getLocalServer() != null) {
            missedAndMaxForComputer0.setText("UnitsLimitComp0:" + gameScreen.playersManager.getLocalServer().missedUnits + "/" + gameScreen.playersManager.getLocalServer().maxOfMissedUnits);
        }
        nextUnitSpawnLabel.setText("NextUnitSpawnAfter:" + ((gameScreen.gameField.waveManager.waitForNextSpawnUnit > 0f) ? String.format("%.2f", gameScreen.gameField.waveManager.waitForNextSpawnUnit) + "sec" : "PRESS_PLAY_BUTTON"));
        unitsSpawn.setText("unitsSpawn:" + gameScreen.gameField.unitsSpawn);
        gamePaused.setText("gamePaused:" + gameScreen.gameField.gamePaused);
        if (towersSelector != null) {
            towersSelectorCoord.setText("towersSelectorCoord:" + towersSelector.coordinateX + "," + towersSelector.coordinateY);
            selectorBorderVertical.setText("selectorBorderVertical:" + towersSelector.selectorBorderVertical);
            selectorBorderHorizontal.setText("selectorBorderHorizontal:" + towersSelector.selectorBorderHorizontal);
        }

        startAndPauseButton.setText((gameScreen.gameField.gamePaused) ? "PLAY" : (gameScreen.gameField.unitsSpawn) ? "PAUSE | GameSpeed:" + gameScreen.gameField.gameSpeed : (gameScreen.gameField.unitsManager.units.size > 0) ? "PAUSE | GameSpeed:" + gameScreen.gameField.gameSpeed : "START NEXT WAVE");
        if (playersViewTable.getChildren().size != playersViewTable.playersManager.getPlayers().size) {
            connectedPlayerCount.setText("players:" + playersViewTable.playersManager.getPlayers().size);
            playersViewTable.updateView(); // real time update if new player connected!
        }
        unitsCount.setText("unitsCount:" + gameScreen.gameField.unitsManager.units.size);
    }

    @Override
    public void draw() {
        super.draw();
    }

    public void renderEndGame(float delta, GameState gameState) {
        currentTextureTime += delta;
        if (currentTextureTime > maxTextureTime) {
//            this.dispose();
            gameScreen.gameMaster.nextGameLevel();
            return; // It'is really need???
        }
        Batch batch = getBatch(); // Need have own batch. mb get from GameScreen
        Gdx.app.log("GameInterface::renderEndGame()", "-- gameState:" + gameState);
        batch.begin();
        if(gameState == GameState.WIN) {
            batch.draw(winTexture, 0, 0, getWidth(), getHeight());
        } else if(gameState == GameState.LOSE) {
            batch.draw(loseTexture, 0, 0, getWidth(), getHeight());
        }
        batch.end();
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
//        Gdx.app.log("GameInterface::panStop()", "-- x:" + x + " y:" + y + " pointer:" + pointer + " button:" + button);
        if (unitsSelector != null) {
            if (unitsSelector.panStop(x, y, pointer, button)) {
//                return true;
            }
        }
        if (towersSelector != null) {
            if (towersSelector.panStop(x, y, pointer, button)) {
//                return true;
            }
        }
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
//        Logger.logFuncStart("amountX:" + amountX, "amountY:" + amountY);

//        if (tableTowerButtons != null) {
//            tableTowerButtons.scaleBy(cameraController.camera.zoom);
//            tableTowerButtons.setSize(cameraController.camera.zoom, cameraController.camera.zoom);
//            for (Actor actor : tableTowerButtons.getChildren()) {
//                tableTowerButtons.getCell(actor).prefSize(cameraController.camera.zoom);
//                actor.setScale(cameraController.camera.zoom);
//                actor.sizeBy(amount);
//                actor.setSize(cameraController.camera.zoom*100f, cameraController.camera.zoom*100f);
//            }
//        }
        if (unitsSelector != null) {
            if (unitsSelector.scrolled(amountY)) {
                return true;
            }
        }
        if (towersSelector != null) {
            if (towersSelector.scrolled(amountY)) {
                return true;
            }
        }
        return super.scrolled(amountX, amountY);
//        return false;
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//        Gdx.app.log("GameInterface::touchDown()", "-- screenX:" + screenX + " screenY:" + screenY + " pointer:" + pointer + " button:" + button);
        this.prevMouseX = screenX;
        this.prevMouseY = screenY;
        boolean returnSuperTouchDown = super.touchDown(screenX, screenY, pointer, button);
//        Gdx.app.log("GameInterface::touchDown()", "-- returnSuperTouchDown :" + returnSuperTouchDown);
        if (unitsSelector != null) {
            if (unitsSelector.touchDown(screenX, screenY, pointer, button)) {
                return true;
            }
        }
        if (towersSelector != null) {
            if (towersSelector.touchDown(screenX, screenY, pointer, button)) {
                return true;
            }
        }
        return returnSuperTouchDown;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
//        Gdx.app.log("GameInterface::touchUp()", "-- screenX:" + screenX + " screenY:" + screenY + " pointer:" + pointer + " button:" + button);
        boolean returnSuperTouchUp = super.touchUp(screenX, screenY, pointer, button);
//        Gdx.app.log("GameInterface::touchUp()", "-- returnSuperTouchUp:" + returnSuperTouchUp);
        if (unitsSelector != null) {
            if (unitsSelector.panStop(screenX, screenY, pointer, button)) {
//                return true;
            }
        }
        if (towersSelector != null) {
            if (towersSelector.panStop(screenX, screenY, pointer, button)) {
//                return true;
            }
        }
        return returnSuperTouchUp;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
//        Gdx.app.log("GameInterface::touchDragged()", "-- screenX:" + screenX + " screenY:" + screenY + " pointer:" + pointer);
        float deltaX = screenX - prevMouseX;
        float deltaY = screenY - prevMouseY;
        this.prevMouseX = screenX;
        this.prevMouseY = screenY;
        boolean returnSuperTouchDragged = super.touchDragged(screenX, screenY, pointer);
//        Gdx.app.log("GameInterface::touchDragged()", "-- returnSuperTouchDown:" + returnSuperTouchDragged);
        if (unitsSelector != null) {
            if (unitsSelector.pan(screenX, screenY, deltaX, deltaY)) {
                return true;
            }
        }
        if (towersSelector != null) {
            if (towersSelector.pan(screenX, screenY, deltaX, deltaY)) {
                return true;
            }
        }
        return returnSuperTouchDragged;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        this.prevMouseX = screenX;
        this.prevMouseY = screenY;
        return super.mouseMoved(screenX, screenY);
    }

    @Override
    public void resize(int width, int height) {
        Logger.logFuncStart("width:" + width, "height:" + height);

        for (Actor actor : getActors()) {
            if (actor instanceof Table) {
                actor.setSize(width, height);
            }
        }
        super.getViewport().update(width, height, true);

        if (unitsSelector!= null) {
            unitsSelector.resize(width, height);
        }
        if (towersSelector != null) {
            towersSelector.resize(width, height);
        }
    }
}
