package com.betmansmall.game.GameScreenInteface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.WidgetController;
import com.betmansmall.game.gameLogic.CameraController;
import com.betmansmall.game.gameLogic.GameField;
import com.betmansmall.game.gameLogic.UnderConstruction;

/**
 * Created by Transet/AndeyA on 07.02.2016. (GovnoDoderbI)
 * This class provides elements which placed on game screen.
 * TODO implement more interface options
 */
public class GameInterface extends Stage implements GestureDetector.GestureListener/*, InputProcessor*/ {
    private GameField gameField;
//    private ShapeRenderer shapeRenderer;
//    private SpriteBatch spriteBatch;
    private BitmapFont bitmapFont;
    private CameraController cameraController;

    private Skin skin;
//    public Stage stage;
    public Table tableBack, tableFront, pauseMenuTable, optionTable;
    public VerticalGroup infoTablo;
    public TextButton resumeButton, nextLevelButton, optionButton, exitButton;
    public TextButton infoTabloHideButton, resetDrawSettingsButton;
    public Slider drawGrid, drawUnits, drawTowers, drawBackground, drawGround, drawForeground, drawGridNav, drawRoutes, drawOrder, drawAll;

    // Console need
    public Array<String> arrayActionsHistory;
    private float deleteActionThrough, actionInHistoryTime;
    private Label actionsHistoryLabel;

    public TextButton pauseMenuButton;
    public TextButton startAndPauseButton;
    public TextButton gameSpeedMinus, gameSpeedPlus;
    public Label fpsLabel, deltaTimeLabel, mapPathLabel, gameType, isometricLabel,
            underConstrEndCoord, underConstructionLabel,
            gamerGoldLabel, unitsManagerSize, towersManagerSize,
            missedAndMaxForPlayer1, missedAndMaxForComputer0,
            nextUnitSpawnLabel,
            unitsSpawn, gamePaused;

    public boolean interfaceTouched;
    public TowersSelector towersSelector;
    public UnitsSelector unitsSelector;

    private Texture winTexture, loseTexture;
    private float currentTextureTime, maxTextureTime;
    private int prevMouseX, prevMouseY;

    public GameInterface(final GameField gameField, BitmapFont bitmapFont) {
        Gdx.app.log("GameInterface::GameInterface(" + gameField + "," + bitmapFont + ")", "-- Called!");
        this.gameField = gameField;
//        this.shapeRenderer = shapeRenderer;
//        this.spriteBatch = spriteBatch;
        this.bitmapFont = bitmapFont;

        this.skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
//        this.stage = new Stage(new ScreenViewport());
//        stage.getViewport().update(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2, true);
//        stage = new Stage(new ExtendViewport(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        setDebugAll(true);

        this.pauseMenuTable = new Table(skin);
        pauseMenuTable.setFillParent(true);
        pauseMenuTable.setVisible(false);
        addActor(pauseMenuTable);

        this.optionTable = new Table(skin);
        optionTable.setVisible(false);
        pauseMenuTable.add(optionTable);

//        setCameraController(cameraController); // need set draw settings to optionTable

        Table verticalButtonsTable = new Table();

        resumeButton = new TextButton("RESUME", skin);
        verticalButtonsTable.add(resumeButton).fill().prefWidth(Gdx.graphics.getHeight()*0.2f).prefHeight(Gdx.graphics.getHeight()*0.2f).row();
        nextLevelButton = new TextButton("NEXT LEVEL", skin);
        verticalButtonsTable.add(nextLevelButton).fill().prefWidth(Gdx.graphics.getHeight()*0.2f).prefHeight(Gdx.graphics.getHeight()*0.2f).row();
        optionButton = new TextButton("OPTION", skin);
        verticalButtonsTable.add(optionButton).fill().prefWidth(Gdx.graphics.getHeight()*0.2f).prefHeight(Gdx.graphics.getHeight()*0.2f).row();
        exitButton = new TextButton("EXIT", skin);
        verticalButtonsTable.add(exitButton).fill().prefWidth(Gdx.graphics.getHeight()*0.2f).prefHeight(Gdx.graphics.getHeight()*0.2f).row();

        pauseMenuTable.add(verticalButtonsTable);

        this.tableBack = new Table(skin);
        tableBack.setFillParent(true);
        addActor(tableBack);

        pauseMenuButton = new TextButton("||", skin);
        pauseMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface::GameInterface()", "-changed- pauseMenuButton.isChecked():" + pauseMenuButton.isChecked());
                gameField.gamePaused = pauseMenuButton.isChecked();
                pauseMenuTable.setVisible(gameField.gamePaused);
                tableFront.setVisible(!pauseMenuButton.isChecked());
                tableBack.setVisible(!pauseMenuButton.isChecked());
                interfaceTouched = true;
            }
        });
        tableBack.add(pauseMenuButton).prefWidth(Gdx.graphics.getHeight()*0.1f).prefHeight(Gdx.graphics.getHeight()*0.1f).top().left();

        arrayActionsHistory = new Array<String>();
        deleteActionThrough = 0f;
        actionInHistoryTime = 1f;
        actionsHistoryLabel = new Label("", new Label.LabelStyle(bitmapFont, Color.WHITE));
        tableBack.add(actionsHistoryLabel).expand().left();

        gameSpeedMinus = new TextButton("<<", skin);
        gameSpeedMinus.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface:changed:GameInterface()", "-- gameSpeedMinus.isChecked():" + gameSpeedMinus.isChecked());
                if (gameField.gameSpeed > 0.1f) {
                    gameField.gameSpeed -= 0.1f;
                }
            }
        });
        tableBack.add(gameSpeedMinus).prefWidth(Gdx.graphics.getWidth()*0.1f).prefHeight(Gdx.graphics.getHeight()*0.1f).bottom();

        startAndPauseButton = new TextButton("", skin, "default");
        startAndPauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface:changed:GameInterface()", "-- startAndPauseButton.isChecked():" + startAndPauseButton.isChecked());
//                if (!gameField.gamePaused) {
//                    if (gameField.gameSpeed < 1f) {
//                        gameField.gameSpeed = 1f;
//                    } else if (gameField.gameSpeed < 2f) {
//                        gameField.gameSpeed = 2f;
//                    } else if (gameField.gameSpeed < 3f) {
//                        gameField.gameSpeed = 3f;
//                    } else if (gameField.gameSpeed < 4f) {
//                        gameField.gameSpeed = 4f;
//                    } else if (gameField.gameSpeed < 5f) {
//                        gameField.gameSpeed = 5f;
//                    } else {
//                        gameField.gamePaused = !gameField.gamePaused;
//                    }
//                } else {
                    gameField.gameSpeed = 1f;
                    gameField.gamePaused = !gameField.gamePaused;
                    if (!gameField.unitsSpawn && GameField.unitsManager.units.size == 0) {
                        gameField.unitsSpawn = true;
                        gameField.gamePaused = false;
                    }
//                }
                interfaceTouched = true;
            }
        });
        tableBack.add(startAndPauseButton).prefWidth(Gdx.graphics.getWidth()*0.2f).prefHeight(Gdx.graphics.getHeight()*0.1f).bottom();

        gameSpeedPlus = new TextButton(">>", skin);
        gameSpeedPlus.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface:changed:GameInterface()", "-- gameSpeedPlus.isChecked():" + gameSpeedPlus.isChecked());
                    gameField.gameSpeed += 0.1f;
            }
        });
        tableBack.add(gameSpeedPlus).prefWidth(Gdx.graphics.getWidth()*0.1f).prefHeight(Gdx.graphics.getHeight()*0.1f).bottom();

        infoTablo = new VerticalGroup();
        tableBack.add(infoTablo).top().expand();

        fpsLabel = new Label("FPS:000", new Label.LabelStyle(bitmapFont, Color.WHITE));
        infoTablo.addActor(fpsLabel);
        deltaTimeLabel = new Label("deltaTime:000", new Label.LabelStyle(bitmapFont, Color.WHITE));
        infoTablo.addActor(deltaTimeLabel);
        mapPathLabel = new Label("MapName:arena0tmx", new Label.LabelStyle(bitmapFont, Color.WHITE));
        infoTablo.addActor(mapPathLabel);
        gameType = new Label("gameType:", new Label.LabelStyle(bitmapFont, Color.YELLOW));
        infoTablo.addActor(gameType);
        isometricLabel = new Label("isometricLabel:", new Label.LabelStyle(bitmapFont, Color.WHITE));
        infoTablo.addActor(isometricLabel);
        underConstrEndCoord = new Label("CoordCell:(0,0)", new Label.LabelStyle(bitmapFont, Color.WHITE));
        infoTablo.addActor(underConstrEndCoord);
        underConstructionLabel = new Label("UnderConstrTemplateName:tower1", new Label.LabelStyle(bitmapFont, Color.WHITE));
        infoTablo.addActor(underConstructionLabel);
        gamerGoldLabel = new Label("GamerGold:000", new Label.LabelStyle(bitmapFont, Color.YELLOW));
        infoTablo.addActor(gamerGoldLabel);
        unitsManagerSize = new Label("unitsManagerSize:", new Label.LabelStyle(bitmapFont, Color.GREEN));
        infoTablo.addActor(unitsManagerSize);
        towersManagerSize = new Label("towersManagerSize:", new Label.LabelStyle(bitmapFont, Color.YELLOW));
        infoTablo.addActor(towersManagerSize);
        missedAndMaxForPlayer1 = new Label("UnitsLimitPL1:10/100", new Label.LabelStyle(bitmapFont, Color.GREEN));
        infoTablo.addActor(missedAndMaxForPlayer1);
        missedAndMaxForComputer0 = new Label("UnitsLimitComp0:10/100", new Label.LabelStyle(bitmapFont, Color.RED));
        infoTablo.addActor(missedAndMaxForComputer0);
        nextUnitSpawnLabel = new Label("NextUnitSpawnAfter:0.12sec", new Label.LabelStyle(bitmapFont, Color.ORANGE));
        infoTablo.addActor(nextUnitSpawnLabel);
        unitsSpawn = new Label("unitsSpawn:", new Label.LabelStyle(bitmapFont, Color.RED));
        infoTablo.addActor(unitsSpawn);
        gamePaused = new Label("gamePaused:", new Label.LabelStyle(bitmapFont, Color.GREEN));
        infoTablo.addActor(gamePaused);
//        bitmapFont.getData().;

        this.tableFront = new Table(skin); // WTF??? почему нельзя селекторы на одну таблицу со всем остальным??
        addActor(tableFront);
        tableFront.setFillParent(true);
        interfaceTouched = false;
        if (gameField.waveManager.wavesForUser.size > 0) {
            unitsSelector = new UnitsSelector(gameField, bitmapFont, tableFront);
        }

        towersSelector = new TowersSelector(gameField, bitmapFont, skin);
        tableFront.add(towersSelector).expand().right();

        winTexture = new Texture(Gdx.files.internal("concepts/littlegame-concept-2-1.jpg"));
        loseTexture = new Texture(Gdx.files.internal("concepts/2018-12-03_19-43-03.png"));
        currentTextureTime = 0f;
        maxTextureTime = 5f;
    }

    @Override
    public void dispose() {
        Gdx.app.log("GameInterface::dispose()", "-- Called!");
//        bitmapFont.dispose();
//        dispose();
//        towersSelector.dispose();
//        unitsSelector.dispose();
        winTexture.dispose();
        loseTexture.dispose();
    }

    public void setCameraController(final CameraController cameraController) {
        this.cameraController = cameraController;

        infoTabloHideButton = new TextButton("Hide Info Tablo", skin);
        infoTabloHideButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface::setCameraController()", "-changed- infoTabloHideButton.isChecked():" + infoTabloHideButton.isChecked());
                infoTablo.setVisible(infoTabloHideButton.isChecked());
                tableBack.setVisible(infoTabloHideButton.isChecked());
                tableFront.setVisible(infoTabloHideButton.isChecked());
                if (infoTablo.isVisible()) {
                    infoTabloHideButton.setText("Hide Info Tablo");
                } else {
                    infoTabloHideButton.setText("Show Info Tablo");
                }
            }
        });
        optionTable.add(infoTabloHideButton).colspan(2).fill().prefHeight(Gdx.graphics.getHeight()*0.1f).row();

        final Label drawGridLabel = new Label("drawGrid:" + cameraController.isDrawableGrid, skin);
        drawGridLabel.setFontScale(Gdx.graphics.getHeight()*0.003f);
        optionTable.add(drawGridLabel).left();

        drawGrid = new Slider(0, 5, 1, false, skin);
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

        final Label drawUnitsLabel = new Label("drawUnits:" + cameraController.isDrawableUnits, skin);
        drawUnitsLabel.setFontScale(Gdx.graphics.getHeight()*0.003f);
        optionTable.add(drawUnitsLabel).left();

        drawUnits = new Slider(0, 5, 1, false, skin);
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

        final Label drawTowersLabel = new Label("drawTowers:" + cameraController.isDrawableTowers, skin);
        drawTowersLabel.setFontScale(Gdx.graphics.getHeight()*0.003f);
        optionTable.add(drawTowersLabel).left();

        drawTowers = new Slider(0, 5, 1, false, skin);
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

        final Label drawBackgroundLabel = new Label("drawBackground:" + cameraController.isDrawableBackground, skin);
        drawBackgroundLabel.setFontScale(Gdx.graphics.getHeight()*0.003f);
        optionTable.add(drawBackgroundLabel).left();

        drawBackground = new Slider(0, 5, 1, false, skin);
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

        final Label drawGroundLabel = new Label("drawGround:" + cameraController.isDrawableGround, skin);
        drawGroundLabel.setFontScale(Gdx.graphics.getHeight()*0.003f);
        optionTable.add(drawGroundLabel).left();

        drawGround = new Slider(0, 5, 1, false, skin);
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

        final Label drawForegroundLabel = new Label("drawForeground:" + cameraController.isDrawableForeground, skin);
        drawForegroundLabel.setFontScale(Gdx.graphics.getHeight()*0.003f);
        optionTable.add(drawForegroundLabel).left();

        drawForeground = new Slider(0, 5, 1, false, skin);
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

        final Label drawGridNavLabel = new Label("drawGridNav:" + cameraController.isDrawableGridNav, skin);
        drawGridNavLabel.setFontScale(Gdx.graphics.getHeight()*0.003f);
        optionTable.add(drawGridNavLabel).left();

        drawGridNav = new Slider(0, 5, 1, false, skin);
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

        final Label drawRoutesLabel = new Label("drawRoutes:" + cameraController.isDrawableRoutes, skin);
        drawRoutesLabel.setFontScale(Gdx.graphics.getHeight()*0.003f);
        optionTable.add(drawRoutesLabel).left();

        drawRoutes = new Slider(0, 5, 1, false, skin);
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

        final Label drawOrderLabel = new Label("drawOrder:" + cameraController.drawOrder, skin);
        drawOrderLabel.setFontScale(Gdx.graphics.getHeight()*0.003f);
        optionTable.add(drawOrderLabel).left();

        drawOrder = new Slider(0, 8, 1, false, skin);
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

        resetDrawSettingsButton = new TextButton("Reset Draw Settings", skin);
        resetDrawSettingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface::setCameraController()", "-changed- resetDrawSettingsButton.isChecked():" + resetDrawSettingsButton.isChecked());
                resetDrawSettingsButton.setText(resetDrawSettingsButton.isChecked() ? "NOT WORK" : "Reset Draw Settings");
            }
        });
        optionTable.add(resetDrawSettingsButton).colspan(2).fill().prefHeight(Gdx.graphics.getHeight()*0.1f).row();

        final Label drawAllLabel = new Label("drawAll:" + cameraController.isDrawableGrid, skin);
        drawAllLabel.setFontScale(Gdx.graphics.getHeight()*0.003f);
        optionTable.add(drawAllLabel).left();

        drawAll = new Slider(0, 5, 1, false, skin);
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
        optionTable.add(drawAll).fill();
    }

    public void addActionToHistory(String action) {
        if(arrayActionsHistory != null) {
            arrayActionsHistory.add(action);
        }
    }

//    @Override
    public void render(float delta) {
//        Gdx.app.log("GameInterface::render()", "-- delta:" + delta);
        act(delta);
        draw();
    }

    @Override
    public void act(float delta) {
//        Gdx.app.log("GameInterface::act()", "-- delta:" + delta);
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
        fpsLabel.setText("FPS:" + String.valueOf(Gdx.graphics.getFramesPerSecond()));
        deltaTimeLabel.setText("delta:" + String.valueOf(delta));
        mapPathLabel.setText("gameField.map.mapPath:" + gameField.map.mapPath);
        gameType.setText("gameField.gameSettings.gameType:" + gameField.gameSettings.gameType);
        isometricLabel.setText("gameField.gameSettings.isometric:" + gameField.gameSettings.isometric);
        UnderConstruction underConstruction = gameField.getUnderConstruction();
        if (underConstruction != null) {
            underConstrEndCoord.setText("underConstructionEndCoord:(" + underConstruction.endX + "," + underConstruction.endY + ")");
            underConstructionLabel.setText("underConstructionTemplateName:" + underConstruction.templateForTower.name);
            underConstructionLabel.setColor(Color.GREEN);
        } else {
            underConstrEndCoord.setText("underConstructionEndCoord:(WTF,WTF)");
            underConstructionLabel.setText("underConstructionTemplateName:NULL");
            underConstructionLabel.setColor(Color.RED);
        }
        gamerGoldLabel.setText("gameField.gamerGold:" + gameField.gamerGold);
        unitsManagerSize.setText("gameField.unitsManager.units.size:" + gameField.unitsManager.units.size);
        towersManagerSize.setText("gameField.towersManager.towers.size:" + gameField.towersManager.towers.size);
        missedAndMaxForPlayer1.setText("UnitsLimitPL1:" + gameField.gameSettings.missedUnitsForPlayer1 + "/" + gameField.gameSettings.maxOfMissedUnitsForPlayer1);
        missedAndMaxForComputer0.setText("UnitsLimitComp0:" + gameField.gameSettings.missedUnitsForComputer0 + "/" + gameField.gameSettings.maxOfMissedUnitsForComputer0);
        nextUnitSpawnLabel.setText("NextUnitSpawnAfter:" + ((gameField.waveManager.waitForNextSpawnUnit > 0f) ? String.format("%.2f", gameField.waveManager.waitForNextSpawnUnit) + "sec" : "PRESS_PLAY_BUTTON"));
        unitsSpawn.setText("unitsSpawn:" + gameField.unitsSpawn);
        gamePaused.setText("gamePaused:" + gameField.gamePaused);

        startAndPauseButton.setText((gameField.gamePaused) ? "PLAY" : (gameField.unitsSpawn) ? "PAUSE | GameSpeed:" + gameField.gameSpeed : (GameField.unitsManager.units.size > 0) ? "PAUSE | GameSpeed:" + gameField.gameSpeed : "START NEXT WAVE");
//        if (pauseMenuButton.isChecked()) {
//            interfaceTouched = true;
//        }
//        stage.act(delta);
//        stage.draw();
    }

    @Override
    public void draw() {
//        Gdx.app.log("GameInterface::draw()", "--");
        super.draw();
    }

    public void renderEndGame(float delta, String gameState) {
        currentTextureTime += delta;
        if (currentTextureTime > maxTextureTime) {
//            this.dispose();
            WidgetController.getInstance().nextGameLevel();
            return; // It'is really need???
        }
        Batch batch = getBatch(); // Need have own batch. mb get from GameScreen
        Gdx.app.log("GameInterface::renderEndGame()", "-- batch:" + batch);
        batch.begin();
        if(gameState.equals("Win")) {
            batch.draw(winTexture, 0, 0, getWidth(), getHeight());
        } else if(gameState.equals("Lose")) {
            batch.draw(loseTexture, 0, 0, getWidth(), getHeight());
        }
        batch.end();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        Gdx.app.log("GameInterface::touchDown()", "-- x:" + x + " y:" + y + " pointer:" + pointer + " button:" + button);
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        Gdx.app.log("GameInterface::tap()", "-- x:" + x + " y:" + y + " count:" + count + " button:" + button);
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        Gdx.app.log("GameInterface::longPress()", "-- x:" + x + " y:" + y);
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        Gdx.app.log("GameInterface::fling()", "-- velocityX:" + velocityX + " velocityY:" + velocityY);
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        Gdx.app.log("GameInterface::pan()", "-- x:" + x + " y:" + y + " deltaX:" + deltaX + " deltaY:" + deltaY);
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        Gdx.app.log("GameInterface::panStop()", "-- x:" + x + " y:" + y + " pointer:" + pointer + " button:" + button);
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        Gdx.app.log("GameInterface::zoom()", "-- initialDistance:" + initialDistance + " distance:" + distance);
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        Gdx.app.log("GameInterface::pinch()", "-- initialPointer1:" + initialPointer1 + " initialPointer2:" + initialPointer2 + " pointer1:" + pointer1 + " pointer2:" + pointer2);
        return false;
    }

    @Override
    public void pinchStop() {
        Gdx.app.log("GameInterface::pinchStop()", "--");
    }

    @Override
    public boolean keyDown(int keyCode) {
        Gdx.app.log("GameInterface::keyDown()", "-- keyCode:" + keyCode);
        return super.keyDown(keyCode);
    }

    @Override
    public boolean keyUp(int keyCode) {
        Gdx.app.log("GameInterface::keyUp()", "-- keyCode:" + keyCode);
        return super.keyUp(keyCode);
    }

    @Override
    public boolean keyTyped(char character) {
        Gdx.app.log("GameInterface::keyTyped()", "-- character:" + character);
        return super.keyTyped(character);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Gdx.app.log("GameInterface::touchDown()", "-- screenX:" + screenX + " screenY:" + screenY + " pointer:" + pointer + " button:" + button);
        this.prevMouseX = screenX;
        this.prevMouseY = screenY;
        boolean returnSuperTouchDown = super.touchDown(screenX, screenY, pointer, button);
        if (gameSpeedMinus.isPressed()) {
            Gdx.app.log("GameInterface::touchDown()", "-- gameSpeedMinus.isChecked():" + gameSpeedMinus.isChecked());
            if (gameField.gameSpeed > 0.1f) {
                gameField.gameSpeed -= 0.1f;
                return true;
            }
        }
        Actor actor = hit(screenX, screenY, true);
        Gdx.app.log("GameInterface::touchDown()", "-- actor:" + actor);
        if (gameSpeedPlus.isPressed()) {
            Gdx.app.log("GameInterface::touchDown()", "-- gameSpeedPlus.isChecked():" + gameSpeedPlus.isChecked());
            gameField.gameSpeed += 0.1f;
            return true;
        }
        if (resumeButton.isPressed()) {
            Gdx.app.log("GameInterface::touchDown()", "-- resumeButton.isChecked():" + resumeButton.isChecked());
            pauseMenuButton.toggle();
            return true;
        }
        if (nextLevelButton.isPressed()) {
            Gdx.app.log("GameInterface::touchDown()", "-- nextLevelButton.isChecked():" + nextLevelButton.isChecked());
            WidgetController.getInstance().nextGameLevel();
            return true;
        }
        if (optionButton.isPressed()) {
            Gdx.app.log("GameInterface::touchDown()", "-- optionButton.isChecked():" + optionButton.isChecked());
            optionTable.setVisible(optionButton.isChecked());
            return true;
        }
        if (exitButton.isPressed()) {
            Gdx.app.log("GameInterface::touchDown()", "-- exitButton.isChecked():" + exitButton.isChecked());
            WidgetController.getInstance().removeTopScreen();
            return true;
        }
        if(unitsSelector != null) {
            if(unitsSelector.touchDown(screenX, screenY, pointer, button)) {
                return true;
            }
        }
        if(towersSelector != null) {
            if (towersSelector.touchDown(screenX, screenY, pointer, button)) {
                return true;
            }
        }
        Gdx.app.log("GameInterface::touchDown()", "-- returnSuperTouchDown :" + returnSuperTouchDown);
        return returnSuperTouchDown;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Gdx.app.log("GameInterface::touchUp()", "-- screenX:" + screenX + " screenY:" + screenY + " pointer:" + pointer + " button:" + button);
        if(unitsSelector != null) {
            if (unitsSelector.panStop(screenX, screenY, pointer, button)) {
                return true;
            }
        }
        if(towersSelector != null) {
            if(towersSelector.panStop(screenX, screenY, pointer, button)) {
                return true;
            }
        }
        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Gdx.app.log("GameInterface::touchDragged()", "-- screenX:" + screenX + " screenY:" + screenY + " pointer:" + pointer);
        float deltaX = screenX - prevMouseX;
        float deltaY = screenY - prevMouseY;
        this.prevMouseX = screenX;
        this.prevMouseY = screenY;
        boolean returnSuperTouchDragged = super.touchDragged(screenX, screenY, pointer);
        if(unitsSelector != null) {
            if(unitsSelector.pan(screenX, screenY, deltaX, deltaY)) {
                return true;
            }
        }
        if(towersSelector != null) {
            if (towersSelector.pan(screenX, screenY, deltaX, deltaY)) {
                return true;
            }
        }
        return returnSuperTouchDragged;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
//        Gdx.app.log("GameInterface::mouseMoved()", "-- screenX:" + screenX + " screenY:" + screenY);
        return super.mouseMoved(screenX, screenY);
    }

    @Override
    public boolean scrolled(int amount) {
        Gdx.app.log("GameInterface::scrolled()", "-- amount:" + amount);
        if(unitsSelector != null) {
            if (unitsSelector.scrolled(amount)) {
                return true;
            }
        }
        if(towersSelector != null) {
            if(towersSelector.scrolled(amount)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("GameInterface[");
        sb.append("tableBack:" + tableBack);
        sb.append("]");
        return sb.toString();
    }
}
