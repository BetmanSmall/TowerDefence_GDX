package com.betmansmall.game.gameInterface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.betmansmall.enums.GameState;
import com.betmansmall.screens.client.GameScreen;
import com.betmansmall.enums.GameType;
import com.betmansmall.game.gameLogic.CameraController;
import com.betmansmall.game.gameLogic.UnderConstruction;

/**
 * Created by Transet/AndeyA on 07.02.2016. (GovnoDoderbI)
 * This class provides elements which placed on game screen.
 * Changed by nMukhin on 2019
 */
public class GameInterface extends Stage implements GestureDetector.GestureListener/*, InputProcessor*/ {
    private GameScreen gameScreen;
//    private GameField gameField;
//    private ShapeRenderer shapeRenderer;
//    private SpriteBatch spriteBatch;
    private BitmapFont bitmapFont;
    private CameraController cameraController;

    private Skin skin;

    public PlayersViewTable playersViewTable;
    public Table tableWithButtons, tableWithSelectors, tableConsoleLog, tableInfoTablo, pauseMenuTable, optionTable;
    public Table infoTabloTable;

    public TextButton resumeButton, nextLevelButton, optionButton, exitButton;
    public TextButton infoTabloHideButton, resetDrawSettingsButton;
    public Slider drawGrid, drawUnits, drawTowers, drawBackground, drawGround, drawForeground, drawGridNav, drawRoutes, drawOrder, drawAll;
    public CheckBox topBottomLeftRightSelector, verticalSelector;

    // Console need
    public Array<String> arrayActionsHistory;
    private float deleteActionThrough, actionInHistoryTime;
    private Label actionsHistoryLabel;
    // Console need

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

    public boolean interfaceTouched;
    public TowersSelector towersSelector;
    public UnitsSelector unitsSelector;

    private Texture winTexture, loseTexture;
    private float currentTextureTime, maxTextureTime;
    public int prevMouseX, prevMouseY;

    public GameInterface(final GameScreen gameScreen) {
        super(new ScreenViewport());
//        Gdx.app.log("GameInterface::GameInterface(" + gameField + "," + bitmapFont + ")", "-- Called!");
        this.gameScreen = gameScreen;
//        this.gameField = gameField;
//        this.shapeRenderer = shapeRenderer;
//        this.spriteBatch = spriteBatch;
        this.bitmapFont = new BitmapFont();
        this.bitmapFont.getData().scale(Gdx.graphics.getHeight()*0.001f);

        this.skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        this.winTexture = new Texture(Gdx.files.internal("concepts/littlegame-concept-2-1.jpg"));
        this.loseTexture = new Texture(Gdx.files.internal("concepts/2018-12-03_19-43-03.png"));
        this.currentTextureTime = 0f;
        this.maxTextureTime = 5f;

        this.interfaceTouched = false;

        initInterface();
        addListeners();
//        setCameraController(cameraController); // need set draw settings to optionTable

//        setDebugAll(true);
//        setDebugUnderMouse(true);
//        setDebugTableUnderMouse(true);
//        setDebugParentUnderMouse(true);
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

    public void initInterface() {
        Gdx.app.log("GameInterface::initInterface()", "--");

        pauseMenuTable = new Table(skin);
        pauseMenuTable.setFillParent(true);
        pauseMenuTable.setVisible(false);
        addActor(pauseMenuTable);

        optionTable = new Table(skin);
        optionTable.setVisible(false);
        pauseMenuTable.add(optionTable);

        Table verticalButtonsTable = new Table();
        pauseMenuTable.add(verticalButtonsTable);

        resumeButton = new TextButton("RESUME", skin);
        verticalButtonsTable.add(resumeButton).fill().prefWidth(Gdx.graphics.getHeight()*0.2f).prefHeight(Gdx.graphics.getHeight()*0.2f).row();
        nextLevelButton = new TextButton("NEXT LEVEL", skin);
        verticalButtonsTable.add(nextLevelButton).fill().prefWidth(Gdx.graphics.getHeight()*0.2f).prefHeight(Gdx.graphics.getHeight()*0.2f).row();
        optionButton = new TextButton("OPTION", skin);
        verticalButtonsTable.add(optionButton).fill().prefWidth(Gdx.graphics.getHeight()*0.2f).prefHeight(Gdx.graphics.getHeight()*0.2f).row();
        exitButton = new TextButton("EXIT", skin);
        verticalButtonsTable.add(exitButton).fill().prefWidth(Gdx.graphics.getHeight()*0.2f).prefHeight(Gdx.graphics.getHeight()*0.2f).row();

        tableConsoleLog = new Table(skin);
        tableConsoleLog.setFillParent(true);
        addActor(tableConsoleLog);

        arrayActionsHistory = new Array<String>();
        arrayActionsHistory.add("actionsHistoryLabel");
        deleteActionThrough = 0f;
        actionInHistoryTime = 1f;
        actionsHistoryLabel = new Label("actionsHistoryLabel", new Label.LabelStyle(bitmapFont, Color.WHITE));
        tableConsoleLog.add(actionsHistoryLabel).expand().left();

        playersViewTable = new PlayersViewTable(gameScreen.playersManager, skin);
        playersViewTable.setFillParent(true);
        addActor(playersViewTable.scrollPane);

        tableWithButtons = new Table(skin);
        tableWithButtons.setFillParent(true);
        addActor(tableWithButtons);

        Table horizontalGroupTop = new Table(skin);
        tableWithButtons.add(horizontalGroupTop).expandY().top().row();

        playersViewButton = new TextButton("PLAYERS", skin);
        horizontalGroupTop.add(playersViewButton).prefWidth(Gdx.graphics.getHeight()*0.2f).prefHeight(Gdx.graphics.getHeight()*0.12f).expandY();

        pauseMenuButton = new TextButton("||", skin);
        horizontalGroupTop.add(pauseMenuButton).prefWidth(Gdx.graphics.getHeight()*0.2f).prefHeight(Gdx.graphics.getHeight()*0.12f).expandY();

        Table horizontalGroupBottom = new Table(skin);
        tableWithButtons.add(horizontalGroupBottom).expandY().bottom();

        gameSpeedMinus = new TextButton("<<", skin);
        horizontalGroupBottom.add(gameSpeedMinus).prefWidth(Gdx.graphics.getWidth()*0.1f).prefHeight(Gdx.graphics.getHeight()*0.1f);

        startAndPauseButton = new TextButton("startAndPauseButton", skin, "default");
        horizontalGroupBottom.add(startAndPauseButton).prefWidth(Gdx.graphics.getWidth()*0.2f).prefHeight(Gdx.graphics.getHeight()*0.1f);

        gameSpeedPlus = new TextButton(">>", skin);
        horizontalGroupBottom.add(gameSpeedPlus).prefWidth(Gdx.graphics.getWidth()*0.1f).prefHeight(Gdx.graphics.getHeight()*0.1f);

        tableInfoTablo = new Table(skin);
        tableInfoTablo.setFillParent(true);
        addActor(tableInfoTablo);

        infoTabloTable = new Table();
        infoTabloTable.setVisible(false);
        tableInfoTablo.add(infoTabloTable).expand().left();

        fpsLabel = new Label("FPS:000", new Label.LabelStyle(bitmapFont, Color.WHITE));
        infoTabloTable.add(fpsLabel).left().row();
        deltaTimeLabel = new Label("deltaTime:000", new Label.LabelStyle(bitmapFont, Color.WHITE));
        infoTabloTable.add(deltaTimeLabel).left().row();
        mapPathLabel = new Label("MapName:arena0tmx", new Label.LabelStyle(bitmapFont, Color.WHITE));
        infoTabloTable.add(mapPathLabel).left().row();
        gameType = new Label("gameType:", new Label.LabelStyle(bitmapFont, Color.YELLOW));
        infoTabloTable.add(gameType).left().row();
        isometricLabel = new Label("isometricLabel:", new Label.LabelStyle(bitmapFont, Color.WHITE));
        infoTabloTable.add(isometricLabel).left().row();
        underConstrEndCoord = new Label("CoordCell:(0,0)", new Label.LabelStyle(bitmapFont, Color.WHITE));
        infoTabloTable.add(underConstrEndCoord).left().row();
        underConstructionLabel = new Label("UnderConstrTemplateName:tower1", new Label.LabelStyle(bitmapFont, Color.WHITE));
        infoTabloTable.add(underConstructionLabel).left().row();
        unitsManagerSize = new Label("unitsManagerSize:", new Label.LabelStyle(bitmapFont, Color.GREEN));
        infoTabloTable.add(unitsManagerSize).left().row();
        towersManagerSize = new Label("towersManagerSize:", new Label.LabelStyle(bitmapFont, Color.YELLOW));
        infoTabloTable.add(towersManagerSize).left().row();
        gamerGoldLabel = new Label("GamerGold:000", new Label.LabelStyle(bitmapFont, Color.YELLOW));
        infoTabloTable.add(gamerGoldLabel).left().row();
        missedAndMaxForPlayer1 = new Label("UnitsLimitPL1:10/100", new Label.LabelStyle(bitmapFont, Color.GREEN));
        infoTabloTable.add(missedAndMaxForPlayer1).left().row();
        missedAndMaxForComputer0 = new Label("UnitsLimitComp0:10/100", new Label.LabelStyle(bitmapFont, Color.RED));
        infoTabloTable.add(missedAndMaxForComputer0).left().row();
        nextUnitSpawnLabel = new Label("NextUnitSpawnAfter:0.12sec", new Label.LabelStyle(bitmapFont, Color.ORANGE));
        infoTabloTable.add(nextUnitSpawnLabel).left().row();
        unitsSpawn = new Label("unitsSpawn:", new Label.LabelStyle(bitmapFont, Color.RED));
        infoTabloTable.add(unitsSpawn).left().row();
        gamePaused = new Label("gamePaused:", new Label.LabelStyle(bitmapFont, Color.GREEN));
        infoTabloTable.add(gamePaused).left().row();

        tableWithSelectors = new Table(skin); // WTF??? почему нельзя селекторы на одну таблицу со всем остальным??
        tableWithSelectors.setFillParent(true);
        addActor(tableWithSelectors);
    }

    public void addListeners() {
        Gdx.app.log("GameInterface::addListeners()", "--");
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
                gameScreen.game.nextGameLevel();
            }
        });
        optionButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface:changed:addListeners()", "-- optionButton.isChecked():" + optionButton.isChecked());
                optionTable.setVisible(optionButton.isChecked());
            }
        });
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface:changed:addListeners()", "-- exitButton.isChecked():" + exitButton.isChecked());
                gameScreen.game.removeTopScreen();
            }
        });
        pauseMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface:changed:addListeners()", "-- pauseMenuButton.isChecked():" + pauseMenuButton.isChecked());
                gameScreen.gameField.gamePaused = pauseMenuButton.isChecked();
                pauseMenuTable.setVisible(gameScreen.gameField.gamePaused);
                tableWithSelectors.setVisible(!gameScreen.gameField.gamePaused);
                tableWithButtons.setVisible(!gameScreen.gameField.gamePaused);
                playersViewTable.setVisible((!gameScreen.gameField.gamePaused));
                interfaceTouched = true;
            }
        });
        gameSpeedMinus.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface:changed:addListeners()", "-- gameSpeedMinus.isChecked():" + gameSpeedMinus.isChecked());
                if (gameScreen.gameField.gameSpeed > 0.1f) {
                    gameScreen.gameField.gameSpeed -= 0.1f;
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
                interfaceTouched = true;
            }
        });
        gameSpeedPlus.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface:changed:addListeners()", "-- gameSpeedPlus.isChecked():" + gameSpeedPlus.isChecked());
                gameScreen.gameField.gameSpeed += 0.1f;
            }
        });
    }

    public void setCameraController(final CameraController cameraController) {
        this.cameraController = cameraController;

        infoTabloHideButton = new TextButton("Hide Info Tablo", skin);
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
        optionTable.add(drawAll).fill().row();

        topBottomLeftRightSelector = new CheckBox("topBottomLeftRightSelector", skin);
        topBottomLeftRightSelector.setChecked(gameScreen.gameField.gameSettings.topBottomLeftRightSelector);
        topBottomLeftRightSelector.getImage().setScaling(Scaling.stretch);
        topBottomLeftRightSelector.getImageCell().size(Gdx.graphics.getHeight()*0.06f);
        topBottomLeftRightSelector.getLabel().setFontScale(Gdx.graphics.getHeight()*0.003f);
        topBottomLeftRightSelector.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface::setCameraController()", "-- topBottomLeftRightSelector.isChecked():" + topBottomLeftRightSelector.isChecked());
                gameScreen.gameField.gameSettings.topBottomLeftRightSelector = topBottomLeftRightSelector.isChecked();
            }
        });
        optionTable.add(topBottomLeftRightSelector).colspan(2).fill().row();

        verticalSelector = new CheckBox("verticalSelector", skin);
        verticalSelector.setChecked(gameScreen.gameField.gameSettings.verticalSelector);
        verticalSelector.getImage().setScaling(Scaling.stretch);
        verticalSelector.getImageCell().size(Gdx.graphics.getHeight()*0.06f);
        verticalSelector.getLabel().setFontScale(Gdx.graphics.getHeight()*0.003f);
        verticalSelector.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("GameInterface::setCameraController()", "-- verticalSelector.isChecked():" + verticalSelector.isChecked());
                gameScreen.gameField.gameSettings.verticalSelector = verticalSelector.isChecked();
            }
        });
        optionTable.add(verticalSelector).colspan(2).fill();

        if (cameraController.gameField.waveManager.wavesForUser.size > 0) {
            unitsSelector = new UnitsSelector(gameScreen.gameField, bitmapFont, tableWithSelectors);
        }

        if (cameraController.gameField.gameSettings.gameType == GameType.TowerDefence) {
            towersSelector = new TowersSelector(gameScreen.gameField, bitmapFont, skin, this);
            tableWithSelectors.add(towersSelector).expand();

            towersSelectorCoord = new Label("towersSelectorCoord:", new Label.LabelStyle(bitmapFont, Color.GREEN));
            infoTabloTable.add(towersSelectorCoord).left().row();
            selectorBorderVertical = new Label("selectorBorderVertical:", new Label.LabelStyle(bitmapFont, Color.WHITE));
            infoTabloTable.add(selectorBorderVertical).left().row();
            selectorBorderHorizontal = new Label("selectorBorderHorizontal:", new Label.LabelStyle(bitmapFont, Color.WHITE));
            infoTabloTable.add(selectorBorderHorizontal).left().row();
        }
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

//    @Override
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
//        if (pauseMenuButton.isChecked()) {
//            interfaceTouched = true;
//        }
//        stage.act(delta);
//        stage.draw();
        if (playersViewTable.getChildren().size != playersViewTable.playersManager.getPlayers().size) {
            playersViewTable.updateView(); // real time update if new player connected!
        }
    }

//    @Override
    public void draw() {
//        Gdx.app.log("GameInterface::draw()", "--");
        super.draw();
    }

    public void renderEndGame(float delta, GameState gameState) {
        currentTextureTime += delta;
        if (currentTextureTime > maxTextureTime) {
//            this.dispose();
            gameScreen.game.nextGameLevel();
            return; // It'is really need???
        }
        Batch batch = getBatch(); // Need have own batch. mb get from GameScreen
        Gdx.app.log("GameInterface::renderEndGame()", "-- batch:" + batch);
        batch.begin();
        if(gameState == GameState.WIN) {
            batch.draw(winTexture, 0, 0, getWidth(), getHeight());
        } else if(gameState == GameState.LOSE) {
            batch.draw(loseTexture, 0, 0, getWidth(), getHeight());
        }
        batch.end();
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
        Gdx.app.log("GameInterface::longPress()", "-- x:" + x + " y:" + y);
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        Gdx.app.log("GameInterface::fling()", "-- velocityX:" + velocityX + " velocityY:" + velocityY);
        if (towersSelector != null) {
            return towersSelector.fling(velocityX, velocityY, button);
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
        Gdx.app.log("GameInterface::panStop()", "-- x:" + x + " y:" + y + " pointer:" + pointer + " button:" + button);
        if (towersSelector != null) {
            return towersSelector.panStop(x, y, pointer, button); // it is not good mb!?!?!
        }
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
//        Gdx.app.log("GameInterface::touchDown()", "-- screenX:" + screenX + " screenY:" + screenY + " pointer:" + pointer + " button:" + button);
        this.prevMouseX = screenX;
        this.prevMouseY = screenY;
        boolean returnSuperTouchDown = super.touchDown(screenX, screenY, pointer, button);
//        Gdx.app.log("GameInterface::touchDown()", "-- returnSuperTouchDown :" + returnSuperTouchDown);
//        Actor actor = hit(screenX, screenY, true);
//        Gdx.app.log("GameInterface::touchDown()", "-- actor:" + actor);
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
        return returnSuperTouchDown;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
//        Gdx.app.log("GameInterface::touchUp()", "-- screenX:" + screenX + " screenY:" + screenY + " pointer:" + pointer + " button:" + button);
        boolean returnSuperTouchUp = super.touchUp(screenX, screenY, pointer, button);
//        Gdx.app.log("GameInterface::touchUp()", "-- returnSuperTouchUp:" + returnSuperTouchUp);
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
//        if(unitsSelector != null) {
//            if (unitsSelector.scrolled(amount)) {
//                return true;
//            }
//        }
        if(towersSelector != null) {
            if(towersSelector.scrolled(amount)) {
                return true;
            }
        }
        return false;
    }

    public void resize(int width, int height) {
        Gdx.app.log("GameInterface::resize()", "-- width:" + width + " height:" + height);
        for (Actor actor : getActors()) {
            if (actor instanceof Table) {
                actor.setSize(width, height);
            }
        }
        getViewport().update(width, height, true);
//        cameraController.camera.update(); // I don't know need this or not.
        if (towersSelector != null) {
            towersSelector.resize(width, height);
        }
//        if (unitsSelector!= null) {
//            unitsSelector.resize(width, height);
//        }
    }

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