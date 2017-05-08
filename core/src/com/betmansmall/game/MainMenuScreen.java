package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.io.File;

import javax.swing.JFileChooser;

public class MainMenuScreen implements Screen {
    private TowerDefence towerDefence;

    private Image menuButton1;
    private Image menuButton2;
    private Image menuButton3;
    private Texture textureMB1;
    private Texture textureMB2;
    private Texture textureMB3;

    private Stage mmStage;
    private Image background;
    private Image returnButton;
    private Image homeButton;
    private Image welcomeScreen;
    private Image infoScreen;

    private int menuLvl;
    private float timer;
    private float screenXScale, screenYScale;

    public String mapName;

    public MainMenuScreen(TowerDefence towerDefence) {
        this.towerDefence = towerDefence;
        textureMB1 = new Texture(Gdx.files.internal("menubutons/play.png"));
        textureMB2 = new Texture(Gdx.files.internal("menubutons/options.png"));
        textureMB3 = new Texture(Gdx.files.internal("menubutons/exit.png"));
        welcomeScreen = new Image((new Texture(Gdx.files.internal("img/welcomescreen.png"))));
        background = new Image(new Texture(Gdx.files.internal("menubutons/background1.png")));
        menuButton1 = new Image(textureMB1);
        menuButton1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MainMenuScreen::Button1", "Clicked!");
                clickAnalyzer((short) 1);
            }
        });
        menuButton2 = new Image(textureMB2);
        menuButton2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MainMenuScreen::Button2", "Clicked!");
                clickAnalyzer((short) 2);
            }
        });
        menuButton3 = new Image(textureMB3);
        menuButton3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MainMenuScreen::Button3", "Clicked!");
                clickAnalyzer((short) 3);
            }
        });
        returnButton = new Image(new Texture(Gdx.files.internal("menubutons/backbutton.png")));
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MainMenuScreen::ReturnButton", "Clicked!");
                if (menuLvl > 0) {
                    menuLvl -= 1;
                    switchMenuButtons();
                }
            }
        });
        returnButton.setVisible(false);

        homeButton = new Image(new Texture(Gdx.files.internal("menubutons/home.png")));
        homeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MainMenuScreen::HomeButton ", "Clicked!");
                menuLvl = 0;
                switchMenuButtons();
            }
        });
        infoScreen = new Image(new Texture(Gdx.files.internal("menubutons/infoscreen.png")));

        mmStage = new Stage(new ScreenViewport());
        mmStage.addActor(background);
        mmStage.addActor(infoScreen);
        mmStage.addActor(homeButton);
        mmStage.addActor(menuButton1);
        mmStage.addActor(menuButton2);
        mmStage.addActor(menuButton3);
        mmStage.addActor(returnButton);
//        mmStage.addActor(welcomeScreen);

        // Campaign levels
        FileHandle mapsDir = Gdx.files.internal("maps");
        if(mapsDir.list().length == 0) {
            towerDefence.gameLevelMaps.add("maps/arena4_1.tmx");
            towerDefence.gameLevelMaps.add("maps/arena0.tmx");
            towerDefence.gameLevelMaps.add("maps/arena1.tmx");
            towerDefence.gameLevelMaps.add("maps/arena2.tmx");
            towerDefence.gameLevelMaps.add("maps/arena3.tmx");
            towerDefence.gameLevelMaps.add("maps/arena4.tmx");
            towerDefence.gameLevelMaps.add("maps/arena666.tmx");
            towerDefence.gameLevelMaps.add("maps/govnoAndreyMapa.tmx");
//            towerDefence.gameLevelMaps.add("maps/arena2.tmx");
        } else {
            for(FileHandle fileHandle : mapsDir.list()) {
                if(fileHandle.extension().equals("tmx")) {
                    Gdx.app.log("MainMenuScreen::MainMenuScreen()", " -- towerDefence.gameLevelMaps.add():" + fileHandle.path());
                    towerDefence.gameLevelMaps.add(fileHandle.path());
                }
            }
        }
        Gdx.app.log("MainMenuScreen::MainMenuScreen()", " -- towerDefence.gameLevelMaps.size:" + towerDefence.gameLevelMaps.size);
    }

    private void create(int width, int height) {
//        screenXScale = (width / 1980); // ?? 1920 ?? FULL HD ??
//        screenYScale = (height / 1080);
//        Gdx.app.log("Tag", "Scales: " + screenXScale + ", " + screenYScale);
//        Gdx.app.log("Tag", "Resolution: " + width + ", " + height);

        //Creating background
        welcomeScreen.setSize(width, height);
        welcomeScreen.setPosition(0f, 0f);
        background.setSize(width, height);
        background.setPosition(0f, 0f);

        infoScreen.setSize(width/2, height/1.7f);
        infoScreen.setPosition(0, height - height/1.7f);
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
                        //TODO: Options menu Descogle
                        Gdx.app.log("MainMenuScreen::clickAnalyzer()", "-- Options function not implemented");
                        break;
                    case 3:
                        //Exit button
                        towerDefence.dispose();
                        break;
                }
                break;
            case 1:                                             //Play menu
                switch (buttonNumber) {
                    case 1:
                        Gdx.app.log("MainMenuScreen::clickAnalyzer()", "-- Campaign levels:" + towerDefence.gameLevelMaps.toString());
                        towerDefence.nextGameLevel();
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
//                            towerDefence.setScreen(new MapEditorScreen(towerDefence, fileName));
//                        }
                        towerDefence.addScreen(new MapEditorScreen(towerDefence, "maps/arena2.tmx"));
                        break;
                }
                break;
            case 2:                                             //Choose map menu
                switch (buttonNumber) {
                    case 1:
                        //Choose map FOREST
                        menuLvl = 3;
                        switchMenuButtons();
                        mapName = "maps/arena3.tmx";
                        break;
                    case 2:
                        //Choose map2
                        menuLvl = 3;
                        switchMenuButtons();
                        mapName = "maps/arena2.tmx";
                        break;
                    case 3:
                        //Choose map3
                        menuLvl = 3;
                        switchMenuButtons();
                        mapName = "maps/arena4.tmx";
                        break;
                }
                break;
            case 3:                                             //Difficulty menu
                switch (buttonNumber) {
                    case 1:
                        //start game with EASY
                        towerDefence.addScreen(new GameScreen(mapName, 0.5f));
                        break;
                    case 2:
                        //start game with NORMAL
                        towerDefence.addScreen(new GameScreen(mapName, 1f));
                        break;
                    case 3:
                        //start game with HARD
                        towerDefence.addScreen(new GameScreen(mapName, 2f));
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
                textureMB1 = new Texture(Gdx.files.internal("menubutons/play.png"));
                textureMB2 = new Texture(Gdx.files.internal("menubutons/options.png"));
                textureMB3 = new Texture(Gdx.files.internal("menubutons/exit.png"));
                break;
            case 1:         //"Play" menu
                textureMB1 = new Texture(Gdx.files.internal("menubutons/campaign.png"));
                textureMB2 = new Texture(Gdx.files.internal("menubutons/single_map.png"));
                textureMB3 = new Texture(Gdx.files.internal("menubutons/editor.png"));
                break;
            case 2:         //"Choose map" menu
                textureMB1 = new Texture(Gdx.files.internal("menubutons/forest_lake.png"));
                textureMB2 = new Texture(Gdx.files.internal("menubutons/map2.png"));
                textureMB3 = new Texture(Gdx.files.internal("menubutons/map3.png"));
                break;
            case 3:         //"Difficulty" menu
                textureMB1 = new Texture(Gdx.files.internal("menubutons/easy.png"));
                textureMB2 = new Texture(Gdx.files.internal("menubutons/normal.png"));
                textureMB3 = new Texture(Gdx.files.internal("menubutons/hard.png"));
                break;
            default:
                break;
        }
        buttonsUpdate(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void buttonsUpdate(int width, int height) {
        //Menu buttons
        float buttonsWidth = width/5; // 9
        float buttonsHeight = height/10; // 16
        Image menuButton1 = new Image(textureMB1);
        Image menuButton2 = new Image(textureMB2);
        Image menuButton3 = new Image(textureMB3);
        menuButton1.setSize(buttonsWidth, buttonsHeight);
        menuButton2.setSize(buttonsWidth, buttonsHeight);
        menuButton3.setSize(buttonsWidth, buttonsHeight);
        menuButton1.setPosition(width - buttonsWidth, buttonsHeight * 2 + (buttonsHeight/3)*3);
        menuButton2.setPosition(width - buttonsWidth, buttonsHeight * 1 + (buttonsHeight/3)*2);
        menuButton3.setPosition(width - buttonsWidth, buttonsHeight * 0 + (buttonsHeight/3)*1);
        menuButton1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MainMenuScreen::ClickListener::clicked(" + event + "," + x + "," + y + ")", " -- menuButton1 pressed");
                clickAnalyzer((short) 1);
            }
        });
        menuButton2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MainMenuScreen::ClickListener::clicked(" + event + "," + x + "," + y + ")", " -- menuButton2 pressed");
                clickAnalyzer((short) 2);
            }
        });
        menuButton3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MainMenuScreen::ClickListener::clicked(" + event + "," + x + "," + y + ")", " -- menuButton3 pressed");
                clickAnalyzer((short) 3);
            }
        });

        //Adding the return button
        returnButton.setSize(buttonsWidth, buttonsHeight);
        returnButton.setPosition(0f, 0f);
        homeButton.setSize(buttonsWidth, buttonsHeight*3);
        homeButton.setPosition(buttonsWidth, 0);

        mmStage.addActor(menuButton1);
        mmStage.addActor(menuButton2);
        mmStage.addActor(menuButton3);
        Gdx.app.log("MainMenuScreen::buttonsUpdate()", "-- mmStage:" + mmStage.getActors().size);
    }

    @Override
    public void show() {
        Gdx.app.log("MainMenuScreen::show()", "-- Called!");
        create(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        buttonsUpdate(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.input.setInputProcessor(mmStage);
    }

    private void inputHandler(float delta) {
//        Gdx.app.log("MainMenuScreen::inputHandler(" + delta + ");");
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            Gdx.app.log("MainMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.BACK || Input.Keys.BACKSPACE);");
            menuLvl--;
            if(menuLvl == -1) {
                towerDefence.dispose();
            }
            switchMenuButtons();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            Gdx.app.log("MainMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUMPAD_1 || Input.Keys.NUM_1);");
            clickAnalyzer((short)1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            Gdx.app.log("MainMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUMPAD_2 || Input.Keys.NUM_2);");
            clickAnalyzer((short)2);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_3) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            Gdx.app.log("MainMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUMPAD_3 || Input.Keys.NUM_3);");
            clickAnalyzer((short)3);
        }
    }

    @Override
    public void render(float delta) {
//        Gdx.app.log("MainMenuScreen::render()", "FPS: " + (1/delta) + "");
//        Gdx.app.log("MainMenuScreen::render()", "-- delta:" + delta);
        inputHandler(delta);
        if (mmStage != null) {
            Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
            mmStage.act(delta);
            mmStage.draw();
            if (menuLvl > 0) {
                returnButton.setVisible(true);
                homeButton.setVisible(true);
            } else {
                returnButton.setVisible(false);
                homeButton.setVisible(false);
            }
            if (timer > 3) {
                welcomeScreen.remove();
            }
            timer = timer + delta;
        }
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("MainMenuScreen::resize(" + width + ", " + height + ")", "--");
//        create(width, height);
//        buttonsUpdate(width, height);
//        mmStage.getViewport().update(width, height, true);
//        mmStage.setViewport(mmStage.getViewport());
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
        //Should not be here!
        //dispose();
    }

    @Override
    public void dispose() {
        Gdx.app.log("MainMenuScreen::dispose()", "--");
        textureMB1.dispose();
        textureMB2.dispose();
        textureMB3.dispose();

        if (mmStage != null) {
            mmStage.dispose();
            mmStage = null;
        }
    }
}
