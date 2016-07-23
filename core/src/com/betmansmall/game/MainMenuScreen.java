package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
        create();
    }

    private void create() {
        textureMB1 = new Texture(Gdx.files.internal("menubutons/play.png"));
        textureMB2 = new Texture(Gdx.files.internal("menubutons/options.png"));
        textureMB3 = new Texture(Gdx.files.internal("menubutons/exit.png"));

        Gdx.app.log("Tag", "Resolution: " + Gdx.graphics.getWidth() + ", " + Gdx.graphics.getHeight());
        screenXScale = (((float) Gdx.graphics.getWidth()) / 1980);
        screenYScale = (((float) Gdx.graphics.getHeight()) / 1080);
        Gdx.app.log("Tag", "Scales: " + screenXScale + ", " + screenYScale);

        welcomeScreen = new Image((new Texture(Gdx.files.internal("img/welcomescreen.png"))));
        welcomeScreen.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        welcomeScreen.setPosition(0f, 0f);

        //Creating background
        background = new Image(new Texture(Gdx.files.internal("menubutons/background1.png")));
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        background.setPosition(0f, 0f);

        //Menu buttons
        menuButton1 = new Image(textureMB1);
        menuButton1.setSize(400 * screenXScale, 100 * screenYScale);
        menuButton1.setPosition(Gdx.graphics.getWidth() - 415 * screenXScale, 370 * screenYScale);
        menuButton1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickAnalyzer((short) 1);
                Gdx.app.log("button 1", "clicked");
            }
        });

        menuButton2 = new Image(textureMB2);
        menuButton2.setSize(400 * screenXScale, 100 * screenYScale);
        menuButton2.setPosition(Gdx.graphics.getWidth() - 415 * screenXScale, (370 - 158) * screenYScale);
        menuButton2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickAnalyzer((short) 2);
                Gdx.app.log("button 2", "clicked");
            }
        });

        menuButton3 = new Image(textureMB3);
        menuButton3.setSize(400 * screenXScale, 100 * screenYScale);
        menuButton3.setPosition(Gdx.graphics.getWidth() - 415 * screenXScale, (370 - 316) * screenYScale);
        menuButton3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickAnalyzer((short) 3);
                Gdx.app.log("menu  button 3 ", "clicked");
            }
        });

        //Adding the return button
        returnButton = new Image(new Texture(Gdx.files.internal("menubutons/backbutton.png")));
        returnButton.setSize(266 * screenXScale, 140 * screenYScale);
        returnButton.setPosition(0f, 0f);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (menuLvl > 0) {
                    menuLvl -= 1;
                    switchMenuButtons();
                }
                Gdx.app.log("Return button ", "clicked");
            }
        });
        returnButton.setVisible(false);

        homeButton = new Image(new Texture(Gdx.files.internal("menubutons/home.png")));
        homeButton.setSize((382 * screenXScale) / 2, (360 * screenYScale) / 2);
        homeButton.setPosition((266 * screenYScale) + 20 * screenYScale, 0);
        homeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                menuLvl = 0;
                switchMenuButtons();
                Gdx.app.log("Home button ", "clicked");
            }
        });

        infoScreen = new Image(new Texture(Gdx.files.internal("menubutons/infoscreen.png")));
        infoScreen.setSize((515 * screenXScale) * 2, (320 * screenYScale) * 2);
        infoScreen.setPosition(0, (Gdx.graphics.getHeight() / 2) - ((320 * screenYScale) / 2));

        mmStage = new Stage(new ScreenViewport());

        mmStage.addActor(background);
        mmStage.addActor(infoScreen);
        mmStage.addActor(homeButton);
        mmStage.addActor(menuButton1);
        mmStage.addActor(menuButton2);
        mmStage.addActor(menuButton3);
        mmStage.addActor(returnButton);
//        mmStage.addActor(welcomeScreen);
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
                        //Campaign menu
                        break;
                    case 2:
                        menuLvl = 2;
                        switchMenuButtons();
                        break;
                    case 3:
                        //Editor mode
                        JFileChooser fileopen = new JFileChooser();
                        fileopen.setCurrentDirectory(new File("."));
                        int ret = fileopen.showDialog(null, "Открыть файл");
                        if (ret == JFileChooser.APPROVE_OPTION) {
                            String fileName = fileopen.getSelectedFile().getAbsolutePath();
                            towerDefence.setScreen(new MapEditorScreen(towerDefence, fileName));
                        }
                        break;
                }
                break;
            case 2:                                             //Choose map menu
                switch (buttonNumber) {
                    case 1:
                        //Choose map FOREST
                        menuLvl = 3;
                        switchMenuButtons();
                        mapName = "maps/arena4.tmx";
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
                        mapName = "maps/arena3.tmx";
                        break;
                }
                break;
            case 3:                                             //Difficulty menu
                switch (buttonNumber) {
                    case 1:
                        //start game with EASY
                        towerDefence.setScreen(new GameScreen(mapName));
                        break;
                    case 2:
                        //start game with NORMAL
                        break;
                    case 3:
                        //start game with HARD
                        break;
                }
                break;
            default:
                break;
        }
    }

    private void switchMenuButtons() {
        Gdx.app.log("swithcButtons", " menuLvl =" + menuLvl);
        switch (menuLvl) {
            case 0:         //main menu
                textureMB1 = new Texture(Gdx.files.internal("menubutons/play.png"));
                textureMB2 = new Texture((Gdx.files.internal("menubutons/options.png")));
                textureMB3 = new Texture((Gdx.files.internal("menubutons/exit.png")));
                buttonsUpdate();
                break;
            case 1:         //"Play" menu
                textureMB1 = new Texture((Gdx.files.internal("menubutons/campaign.png")));
                textureMB2 = new Texture((Gdx.files.internal("menubutons/single_map.png")));
                textureMB3 = new Texture((Gdx.files.internal("menubutons/editor.png")));
                buttonsUpdate();
                Gdx.app.log("Draw", "shit1");
                break;
            case 2:         //"Choose map" menu
                textureMB1 = new Texture((Gdx.files.internal("menubutons/forest_lake.png")));
                textureMB2 = new Texture((Gdx.files.internal("menubutons/map2.png")));
                textureMB3 = new Texture((Gdx.files.internal("menubutons/map3.png")));
                buttonsUpdate();
                Gdx.app.log("Draw", "shit2");
                break;
            case 3:         //"Difficulty" menu
                textureMB1 = new Texture((Gdx.files.internal("menubutons/easy.png")));
                textureMB2 = new Texture((Gdx.files.internal("menubutons/normal.png")));
                textureMB3 = new Texture((Gdx.files.internal("menubutons/hard.png")));
                buttonsUpdate();
                Gdx.app.log("Draw", "shit3");
                break;
            default:
                break;
        }
    }

    private void buttonsUpdate() {
        menuButton1 = new Image(textureMB1);
        menuButton1.setSize(400 * screenXScale, 100 * screenYScale);
        menuButton1.setPosition(Gdx.graphics.getWidth() - 415 * screenXScale, 370 * screenYScale);
        menuButton1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickAnalyzer((short) 1);
                Gdx.app.log("button 1", "clicked");
            }
        });

        menuButton2 = new Image(textureMB2);
        menuButton2.setSize(400 * screenXScale, 100 * screenYScale);
        menuButton2.setPosition(Gdx.graphics.getWidth() - 415 * screenXScale, (370 - 158) * screenYScale);
        menuButton2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickAnalyzer((short) 2);
                Gdx.app.log("button 2", "clicked");
            }
        });

        menuButton3 = new Image(textureMB3);
        menuButton3.setSize(400 * screenXScale, 100 * screenYScale);
        menuButton3.setPosition(Gdx.graphics.getWidth() - 415 * screenXScale, (370 - 316) * screenYScale);
        menuButton3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickAnalyzer((short) 3);
                Gdx.app.log("menu  button 3 ", "clicked");
            }
        });
        mmStage.addActor(menuButton1);
        mmStage.addActor(menuButton2);
        mmStage.addActor(menuButton3);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(mmStage);
    }

    @Override
    public void render(float delta) {
//        Gdx.app.log("MainMenuScreen::render()", "FPS: " + (1/delta) + "");
//        Gdx.app.log("MainMenuScreen::render()", "-- delta:" + delta);
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
        buttonsUpdate();
//        mmStage.getViewport().update(width, height, true);
//        mmStage.setViewport(mmStage.getViewport());
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        //Should not be here!
        //dispose();
    }

    @Override
    public void dispose() {
        textureMB1.dispose();
        textureMB2.dispose();
        textureMB3.dispose();

        if (mmStage != null) {
            mmStage.dispose();
            mmStage = null;
        }
    }
}
