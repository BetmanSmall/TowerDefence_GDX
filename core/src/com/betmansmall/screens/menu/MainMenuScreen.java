package com.betmansmall.screens.menu;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.betmansmall.GameMaster;
import com.betmansmall.screens.AbstractScreen;
import com.betmansmall.screens.client.ClientSettingsScreen;
import com.betmansmall.screens.client.GameScreen;
import com.betmansmall.screens.server.ServerSettingsScreen;
import com.betmansmall.util.logging.Logger;

public class MainMenuScreen extends AbstractScreen {
    private Skin skin;
    private Stage stage;

    private TextButton helpButton;
    private TextButton playButton;
    private TextButton secondButton;
    private TextButton exitButton;
    private TextButton backButton;
    private TextButton homeButton;
    private TextButton serverButton;
    private TextButton clientButton;

    private int menuLvl;
    private Label sizeLabel1;
    private Label sizeLabel2;
    private Label sizeLabel3;
    private Slider slider1;
    private Slider slider2;
    private Slider slider3;
    private float cellsSize = 0.001f;
    private float sizeFont2 = 2.5f;
    private float sizeFont3 = 0.01f;

    public MainMenuScreen(GameMaster gameMaster) {
        super(gameMaster);
        Logger.logFuncStart();

        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
//        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
//        Button.ButtonStyle buttonStyle = new Button.ButtonStyle();
//        textButtonStyle.font = skin.getFont("default-font");
//        textButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("helpImages/button.png"))));
//        textButtonStyle.down = skin.getDrawable("default-round-down");
//        textButtonStyle.up = Color.RED;
//        textButtonStyle.downFontColor = Color.RED;
//        skin.newDrawable("default-round-down");
//        skin.getFont("default-font").getData().setScale(cellsSize, cellsSize);

        stage = new Stage(new ScreenViewport());
        stage.addActor(game.backgroundImages.get(0));
//        stage.setDebugAll(true);
        stage.setDebugUnderMouse(true);
        stage.setDebugParentUnderMouse(true);

        Table rootTable = new Table(skin);
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        Table leftTable = new Table(skin);
        helpButton = new TextButton("HELP", skin); {
            helpButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.log("MainMenuScreen::helpButton::clicked()", "-- event:" + event);
                    super.clicked(event, x, y);
                    game.addScreen(game.helpMenuScreen);
                }
            });
        }
        leftTable.add(helpButton).expand().fill().prefHeight(Gdx.app.getGraphics().getHeight() * cellsSize).pad(Gdx.graphics.getHeight() * cellsSize).colspan(2).row();

        backButton = new TextButton("BACK", skin); {
            backButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.log("MainMenuScreen::backButton::clicked()", "-- event:" + event);
                    super.clicked(event, x, y);
                    if (menuLvl > 0) {
                        menuLvl -= 1;
                        switchMenuButtons();
                    }
                }
            });
        }
        leftTable.add(backButton).expand().fill().prefHeight(Gdx.graphics.getHeight() * cellsSize).pad(Gdx.graphics.getHeight() * cellsSize);

        homeButton = new TextButton("HOME", skin); {
            homeButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.log("MainMenuScreen::homeButton::clicked()", "-- event:" + event);
                    super.clicked(event, x, y);
                    menuLvl = 0;
                    switchMenuButtons();
                }
            });
        }
        leftTable.add(homeButton).expand().fill().prefHeight(Gdx.graphics.getHeight() * cellsSize).pad(Gdx.graphics.getHeight() * cellsSize).row();

        serverButton = new TextButton("GameServerScreen", skin); {
            serverButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.log("MainMenuScreen::serverButton::clicked()", "-- event:" + event);
                    super.clicked(event, x, y);
                    game.addScreen(new ServerSettingsScreen(game));
                }
            });
        }
        leftTable.add(serverButton).expand().fill().prefHeight(Gdx.app.getGraphics().getHeight() * cellsSize).pad(Gdx.graphics.getHeight() * cellsSize).colspan(2).row();

        clientButton = new TextButton("GameClientScreen", skin); {
            clientButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.log("MainMenuScreen::clientButton::clicked()", "-- event:" + event);
                    super.clicked(event, x, y);
                    game.addScreen(new ClientSettingsScreen(game));
                }
            });
        }
        leftTable.add(clientButton).expand().fill().prefHeight(Gdx.app.getGraphics().getHeight() * cellsSize).pad(Gdx.graphics.getHeight() * cellsSize).colspan(2).row();
        rootTable.add(leftTable).expandX().fillX().left();

        Table middleTable = new Table(skin);

        sizeLabel1 = new Label(Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight(), skin);
        sizeLabel1.setFontScale(Gdx.graphics.getHeight() * cellsSize);
        middleTable.add(sizeLabel1).row();

        sizeLabel2 = new Label(Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight(), skin);
        sizeLabel2.setFontScale(Gdx.graphics.getHeight() * sizeFont2);
        middleTable.add(sizeLabel2).row();

        sizeLabel3 = new Label(Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight(), skin);
        sizeLabel3.setFontScale(Gdx.graphics.getHeight() * sizeFont3);
        middleTable.add(sizeLabel3).row();

        slider1 = new Slider(-0.1f, 0.1f, 0.001f, false, skin);
        slider1.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                cellsSize = slider1.getValue();
                Gdx.app.log("MainMenuScreen::slider1::changed()", "-- cellsSize:" + cellsSize);
                if (cellsSize != 0) {
                    sizeLabel1.setText("cellsSize:" + cellsSize);
                }
            }
        });
        slider1.setValue(cellsSize);
        middleTable.add(slider1).row();

        slider2 = new Slider(0.01f, 5f, 0.015f, false, skin);
        slider2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sizeFont2 = slider2.getValue();
                Gdx.app.log("MainMenuScreen::slider2::changed()", "-- sizeFont2:" + sizeFont2);
//                sizeLabel2.setFontScale( (Gdx.graphics.getHeight() * sizeFont2 == 0) ? 0.001f : Gdx.graphics.getHeight() * sizeFont2);
                if (sizeFont2 != 0) {
                    sizeLabel2.setFontScale(sizeFont2);
//                    cellsSize = sizeFont2;
                    resize();
                }
//                sizeLabel2.setText("cellsSize:" + cellsSize);
                sizeLabel3.setText("sizeFont2:" + sizeFont2);
            }
        });
        slider2.setValue(sizeFont2);
        middleTable.add(slider2).row();

        slider3 = new Slider(-1f, 1f, 0.001f, false, skin);
        slider3.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sizeFont3 = slider3.getValue();
                Gdx.app.log("MainMenuScreen::slider3::changed()", "-- sizeFont3:" + sizeFont3);
                sizeLabel3.setFontScale( (Gdx.graphics.getHeight() * sizeFont3 == 0) ? 0.001f : Gdx.graphics.getHeight() * sizeFont3);
                if (sizeFont3 != 0) {
                    sizeLabel3.setFontScale(sizeFont3);
                    cellsSize = sizeFont3;
                    resize();
                }
            }
        });
        slider3.setValue(sizeFont3);
        middleTable.add(slider3).row();

        rootTable.add(middleTable).center().expand().fill();

        Table rightTable = new Table(skin);
        playButton = new TextButton("PLAY", skin); {
//            Label playText = new Label("PLAY", skin);
//            playText.setFontScale(Gdx.graphics.getHeight() * cellsSize);
//            playButton.add(playText).expand().top().row();
            playButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.log("MainMenuScreen::playButton::clicked()", "-- event:" + event);
                    super.clicked(event, x, y);
                    clickAnalyzer((short) 1);
                }
            });
        }
        rightTable.add(playButton).expand().fill().prefHeight(Gdx.graphics.getHeight() * cellsSize).pad(Gdx.graphics.getHeight() * cellsSize).row();

        secondButton = new TextButton("OPTIONS", skin); {
//            Label playText = new Label("OPTIONS", skin);
//            playText.setFontScale(Gdx.graphics.getHeight() * cellsSize);
//            secondButton.add(playText).expand();
            secondButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.log("MainMenuScreen::secondButton::clicked()", "-- event:" + event);
                    super.clicked(event, x, y);
                    clickAnalyzer((short) 2);
                }
            });
        }
        rightTable.add(secondButton).expand().fill().prefHeight(Gdx.graphics.getHeight() * cellsSize).pad(Gdx.graphics.getHeight() * cellsSize).row();

        exitButton = new TextButton("EXIT", skin); {
//            Label playText = new Label("EXIT", skin);
//            playText.setFontScale(Gdx.graphics.getHeight() * cellsSize);
//            exitButton.add(playText).expand();
            exitButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.log("MainMenuScreen::exitButton::clicked()", "-- event:" + event);
                    super.clicked(event, x, y);
                    clickAnalyzer((short) 3);
                }
            });
        }
        rightTable.add(exitButton).expand().fill().prefHeight(Gdx.graphics.getHeight() * cellsSize).pad(Gdx.graphics.getHeight() * cellsSize);
        rootTable.add(rightTable).expand().fillX().right();

//        resize();
    }

    @Override
    public void show() {
        Gdx.app.log("MainMenuScreen::show()", "-- Called!");
        Gdx.input.setInputProcessor(stage);
//        resize();
    }

    public void resize() {
        Logger.logFuncStart("skin:" + skin);
        if (skin != null) {
            Logger.logInfo("cellsSize:" + cellsSize);
            Logger.logInfo("sizeFont2:" + sizeFont2);
            if (Gdx.app.getType() == Application.ApplicationType.Android && sizeFont2 < 1) {
                skin.getFont("default-font").getData().setScale(7.25f, 7.25f);
            } else {
                skin.getFont("default-font").getData().setScale(sizeFont2, sizeFont2);
            }
            resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }

    @Override
    public void render(float delta) {
//      Gdx.app.log("MainMenuScreen::render()", "-- delta:" + delta + " FPS:" + Gdx.graphics.getFramesPerSecond());
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        inputHandler(delta);

        if (stage != null) {
            stage.act(delta);
            stage.draw();
//            if (menuLvl > 0) {
//                backButton.setVisible(true);
//                homeButton.setVisible(true);
//            } else {
//                backButton.setVisible(false);
//                homeButton.setVisible(false);
//            }
        }
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("MainMenuScreen::resize(" + width + ", " + height + ")", "--");
        stage.getViewport().update(width, height, true);
        sizeLabel1.setText(width + "x" + height);

//        for (Actor actor : stage.getActors()) {
//            if (actor instanceof Button) {
//                Button button = (Button)actor;
//                button.pad(Gdx.graphics.getHeight()*0.9f);
////                for (Actor actor1 : button.getParent().getChildren()) {
////                    if (actor instanceof )
////                }
//            }
//        }
    }

    @Override
    public void pause() {
        Gdx.app.log("MainMenuScreen::pause()", "--");
    }

    @Override
    public void resume() {
        Gdx.app.log("MainMenuScreen::resume()", "--");
    }

    @Override
    public void hide() {
        Gdx.app.log("MainMenuScreen::hide()", "--");
//        TTW.input.removeProcessor(stage);
    }

    @Override
    public void dispose() {
        Gdx.app.log("MainMenuScreen::dispose()", "--");
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
    }

    private void inputHandler(float delta) {
//        Gdx.app.log("MainMenuScreen::inputHandler(" + delta + ");");
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.log("MainMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.BACK || Input.Keys.ESCAPE);");
            menuLvl--;
            if (menuLvl == -1) {
                game.dispose();
            }
            switchMenuButtons();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_0) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
            Gdx.app.log("HelpMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUMPAD_0 || Input.Keys.NUM_0);");
            game.addScreen(game.helpMenuScreen);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            Gdx.app.log("MainMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUMPAD_1 || Input.Keys.NUM_1);");
            clickAnalyzer((short) 1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            Gdx.app.log("MainMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUMPAD_2 || Input.Keys.NUM_2);");
            clickAnalyzer((short) 2);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_3) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            Gdx.app.log("MainMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUMPAD_3 || Input.Keys.NUM_3);");
            clickAnalyzer((short) 3);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            Gdx.app.log("MainMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.ENTER || Input.Keys.SPACE);");
            Gdx.app.log("MainMenuScreen::inputHandler()", "-- Campaign levels:" + game.gameLevelMaps.toString());
            game.nextGameLevel();
        }
    }

    private void clickAnalyzer(short buttonNumber) {
        switch (menuLvl) {
            case 0:                                             //main menu
                switch (buttonNumber) {
                    case 1:
                        menuLvl = 1;
                        switchMenuButtons();
                        break;
                    case 2:
                        game.addScreen(game.optionMenuScreen);
                        break;
                    case 3:
                        //Exit button
                        game.dispose();
                        break;
                }
                break;
            case 1:                                             //Play menu
                switch (buttonNumber) {
                    case 1:
                        Gdx.app.log("MainMenuScreen::clickAnalyzer()", "-- Campaign levels:" + game.gameLevelMaps.toString());
                        game.nextGameLevel();
                        break;
                    case 2:
                        menuLvl = 2;
                        switchMenuButtons();
                        break;
                    case 3:
                        //Editor mode
//                        JFileChooser fileopen = new JFileChooser();
//                        fileopen.setCurrentDirectory(new File("."));
//                        int ret = fileopen.showDialog(null, "Открыть файл");
//                        if (ret == JFileChooser.APPROVE_OPTION) {
//                            String fileName = fileopen.getSelectedFile().getAbsolutePath();
//                            game.setScreen(new MapEditorScreen(game, fileName));
//                        } else {
                        game.addScreen(new MapEditorScreen(game, "maps/aaagen.tmx"));
//                        }
                        break;
                }
                break;
            case 2:                                             //Choose map menu
                switch (buttonNumber) {
                    case 1:
                        //Choose map FOREST
                        menuLvl = 3;
                        switchMenuButtons();
                        game.sessionSettings.gameSettings.mapPath = "maps/arena0.tmx";
                        break;
                    case 2:
                        //Choose map2
                        menuLvl = 3;
                        switchMenuButtons();
                        game.sessionSettings.gameSettings.mapPath = "maps/arena2.tmx";
                        break;
                    case 3:
                        //Choose map3
                        menuLvl = 3;
                        switchMenuButtons();
                        game.sessionSettings.gameSettings.mapPath = "maps/arena4.tmx";
                        break;
                }
                break;
            case 3:                                             //Difficulty menu
                switch (buttonNumber) {
                    case 1:
                        //start game with EASY
                        game.sessionSettings.gameSettings.difficultyLevel = 0.5f;
                        game.addScreen(new GameScreen(game));
                        break;
                    case 2:
                        //start game with NORMAL
                        game.sessionSettings.gameSettings.difficultyLevel = 1f;
                        game.addScreen(new GameScreen(game));
                        break;
                    case 3:
                        //start game with HARD
                        game.sessionSettings.gameSettings.difficultyLevel = 2f;
                        game.addScreen(new GameScreen(game));
                        break;
                }
                break;
            default:
                break;
        }
    }

    private void switchMenuButtons() {
        Gdx.app.log("MainMenuScreen::switchMenuButtons()", "-- menuLvl:" + menuLvl);
        switch (menuLvl) {
            case 0:         //main menu
                playButton.setText("PLAY");
                secondButton.setText("OPTIONS");
                exitButton.setText("EXIT");
                break;
            case 1:         //"Play" menu
                playButton.setText("CAMPAIGN");
                secondButton.setText("SINGLE MAP");
                exitButton.setText("mapEditor");
                break;
            case 2:         //"Choose map" menu
                playButton.setText("map1");
                secondButton.setText("map2");
                exitButton.setText("map3");
                break;
            case 3:         //"Difficulty" menu
                playButton.setText("EASY");
                secondButton.setText("NORMAL");
                exitButton.setText("HARD");
                break;
            default:
                break;
        }
    }

    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("MainMenuScreen[");
        sb.append("menuLvl:" + menuLvl);
        if (full) {
            sb.append(",stage:" + stage);
        }
        sb.append("]");
        return sb.toString();
    }
}
