package com.betmansmall.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class MainMenuScreen extends AbstractScreen {
    private Stage stage;

    private VisTextButton helpButton;
    private VisTextButton playButton;
    private VisTextButton secondButton;
    private VisTextButton exitButton;
    private VisTextButton backButton;
    private VisTextButton homeButton;
    private VisTextButton serverButton;
    private VisTextButton clientButton;

    private int menuLvl;
    private VisLabel sizeLabel1;
    private VisLabel sizeLabel2;
    private VisLabel sizeLabel3;
    private VisSlider slider1;
    private VisSlider slider2;
    private VisSlider slider3;
    private float cellsSize = 0.001f;
    private float sizeFont2 = 2.5f;
    private float sizeFont3 = 0.01f;

    public MainMenuScreen(GameMaster gameMaster) {
        super(gameMaster);
        Logger.logFuncStart();

        stage = new Stage(new ScreenViewport());
        stage.addActor(game.backgroundImages.get(0));

        stage.setDebugUnderMouse(true);
        stage.setDebugParentUnderMouse(true);

        VisTable rootTable = new VisTable();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        VisTable leftTable = new VisTable();
        helpButton = new VisTextButton("HELP"); {
            helpButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.log("MainMenuScreen::helpButton::clicked()", "-- event:" + event);
                    super.clicked(event, x, y);
                    game.addScreen(game.helpMenuScreen);
                }
            });
        }
        leftTable.add(helpButton).expand().fill().pad(5).colspan(2).row(); // prefHeight(Gdx.app.getGraphics().getHeight() * cellsSize).pad(Gdx.graphics.getHeight() * cellsSize)

        serverButton = new VisTextButton("GameServerScreen"); {
            serverButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.log("MainMenuScreen::serverButton::clicked()", "-- event:" + event);
                    super.clicked(event, x, y);
                    game.addScreen(new ServerSettingsScreen(game));
                }
            });
        }
        leftTable.add(serverButton).expand().fill().pad(5).colspan(2).row(); // .prefHeight(Gdx.app.getGraphics().getHeight() * cellsSize).pad(Gdx.graphics.getHeight() * cellsSize)

        clientButton = new VisTextButton("GameClientScreen"); {
            clientButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.log("MainMenuScreen::clientButton::clicked()", "-- event:" + event);
                    super.clicked(event, x, y);
                    game.addScreen(new ClientSettingsScreen(game));
                }
            });
        }
        leftTable.add(clientButton).expand().fill().pad(5).colspan(2).row(); // .prefHeight(Gdx.app.getGraphics().getHeight() * cellsSize).pad(Gdx.graphics.getHeight() * cellsSize)
        rootTable.add(leftTable).expandX().fillX().left();

        backButton = new VisTextButton("BACK"); {
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
        leftTable.add(backButton).expand().fill().pad(5); // .prefHeight(Gdx.graphics.getHeight() * cellsSize).pad(Gdx.graphics.getHeight() * cellsSize)

        homeButton = new VisTextButton("HOME"); {
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
        leftTable.add(homeButton).expand().fill().pad(5).row(); // .prefHeight(Gdx.graphics.getHeight() * cellsSize).pad(Gdx.graphics.getHeight() * cellsSize)

        VisTable middleTable = new VisTable();

        sizeLabel2 = new VisLabel(Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight());
        sizeLabel2.setFontScale(Gdx.graphics.getHeight() * sizeFont2);
        middleTable.add(sizeLabel2).expand().fill().row();

        sizeLabel1 = new VisLabel(Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight());
        sizeLabel1.setFontScale(Gdx.graphics.getHeight() * cellsSize);
        middleTable.add(sizeLabel1).expand().fill().row();

        sizeLabel3 = new VisLabel(Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight());
        sizeLabel3.setFontScale(Gdx.graphics.getHeight() * sizeFont3);
        middleTable.add(sizeLabel3).expand().fill().row();

        slider2 = new VisSlider(0.01f, 8f, 0.015f, false);
        slider2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sizeFont2 = slider2.getValue();
                Gdx.app.log("MainMenuScreen::slider2::changed()", "-- sizeFont2:" + sizeFont2);
//                sizeLabel2.setFontScale( (Gdx.graphics.getHeight() * sizeFont2 == 0) ? 0.001f : Gdx.graphics.getHeight() * sizeFont2);
                if (sizeFont2 != 0) {
                    sizeLabel2.setFontScale(sizeFont2);
                }
                sizeLabel3.setText("sizeFont2:" + sizeFont2);
            }
        });
        slider2.setValue(sizeFont2);
        middleTable.add(slider2).expand().fill().row();

        slider1 = new VisSlider(-0.1f, 0.1f, 0.001f, false);
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
        middleTable.add(slider1).expand().fill().row();

        slider3 = new VisSlider(-1f, 1f, 0.001f, false);
        slider3.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sizeFont3 = slider3.getValue();
                Gdx.app.log("MainMenuScreen::slider3::changed()", "-- sizeFont3:" + sizeFont3);
                sizeLabel3.setFontScale( (Gdx.graphics.getHeight() * sizeFont3 == 0) ? 0.001f : Gdx.graphics.getHeight() * sizeFont3);
                if (sizeFont3 != 0) {
                    sizeLabel3.setFontScale(sizeFont3);
                    cellsSize = sizeFont3;
                }
            }
        });
        slider3.setValue(sizeFont3);
        middleTable.add(slider3).expand().fill().row();

        rootTable.add(middleTable).center().expand().pad(10).fill();

        VisTable rightTable = new VisTable();
        playButton = new VisTextButton("PLAY"); {
            playButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.log("MainMenuScreen::playButton::clicked()", "-- event:" + event);
                    super.clicked(event, x, y);
                    clickAnalyzer((short) 1);
                }
            });
        }
        rightTable.add(playButton).expand().fill().row(); // .prefHeight(Gdx.graphics.getHeight() * cellsSize).pad(Gdx.graphics.getHeight() * cellsSize)

        secondButton = new VisTextButton("OPTIONS"); {
            secondButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.log("MainMenuScreen::secondButton::clicked()", "-- event:" + event);
                    super.clicked(event, x, y);
                    clickAnalyzer((short) 2);
                }
            });
        }
        rightTable.add(secondButton).expand().fill().pad(10).row(); // .prefHeight(Gdx.graphics.getHeight() * cellsSize).pad(Gdx.graphics.getHeight() * cellsSize)

        exitButton = new VisTextButton("EXIT"); {
            exitButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.log("MainMenuScreen::exitButton::clicked()", "-- event:" + event);
                    super.clicked(event, x, y);
                    clickAnalyzer((short) 3);
                }
            });
        }
        rightTable.add(exitButton).expand().fill().pad(10).row(); // .prefHeight(Gdx.graphics.getHeight() * cellsSize).pad(Gdx.graphics.getHeight() * cellsSize)
        rootTable.add(rightTable).expand().fillX().right().pad(10).row();
    }

    @Override
    public void show() {
        Gdx.app.log("MainMenuScreen::show()", "-- Called!");
        Gdx.input.setInputProcessor(stage);
//        resize();
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
                        game.sessionSettings.gameSettings.mapPath = "maps/randomMap.tmx";
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
