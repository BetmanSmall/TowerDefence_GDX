package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {
    private TowerDefence towerDefence;

    private Stage stage;

    private TextButton helpButton;
    private TextButton playButton;
    private TextButton secondButton;
    private TextButton exitButton;
    private TextButton backButton;
    private TextButton homeButton;

    private Label sizeLabel;

    private int menuLvl;

    public String mapName;

    public MainMenuScreen(final TowerDefence towerDefence) {
        this.towerDefence = towerDefence;

        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
//        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
//        Button.ButtonStyle buttonStyle = new Button.ButtonStyle();
//        textButtonStyle.font = skin.getFont("default-font");
//        textButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("helpImages/button.png"))));
//        textButtonStyle.down = skin.getDrawable("default-round-down");
//        textButtonStyle.up = Color.RED;
//        textButtonStyle.downFontColor = Color.RED;
//        skin.newDrawable("default-round-down");

        stage = new Stage(new ScreenViewport());
        stage.addActor(towerDefence.backgroundImages.get(0));
        stage.setDebugAll(true);

        Table rootTable = new Table(skin);
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        Table leftTable = new Table(skin);
        helpButton = new TextButton("HELP", skin);
        helpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MainMenuScreen::helpButton::clicked()", "-- event:" + event);
                super.clicked(event, x, y);
                towerDefence.addScreen(towerDefence.helpMenuScreen);
            }
        });
        leftTable.add(helpButton).expand().fill().prefHeight(Gdx.app.getGraphics().getHeight()*0.3f).pad(Gdx.graphics.getHeight()*0.01f).colspan(2).row();

        backButton = new TextButton("BACK", skin);
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
        leftTable.add(backButton).expand().fill().prefHeight(Gdx.graphics.getHeight()*0.3f).pad(Gdx.graphics.getHeight()*0.01f);

        homeButton = new TextButton("HOME", skin);
        homeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MainMenuScreen::homeButton::clicked()", "-- event:" + event);
                super.clicked(event, x, y);
                menuLvl = 0;
                switchMenuButtons();
            }
        });
        leftTable.add(homeButton).expand().fill().prefHeight(Gdx.graphics.getHeight()*0.3f).pad(Gdx.graphics.getHeight()*0.01f);
        rootTable.add(leftTable).expandX().fillX().left();

        Table middleTable = new Table(skin);
        sizeLabel = new Label(Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight(), skin);
        sizeLabel.setFontScale(Gdx.graphics.getHeight()*0.01f);
//        sizeLabel.setFHeight(Gdx.graphics.getHeight()*0.3f);
        middleTable.add(sizeLabel).center();
        rootTable.add(middleTable).top();

        Table rightTable = new Table(skin);
        playButton = new TextButton("PLAY", skin);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MainMenuScreen::playButton::clicked()", "-- event:" + event);
                super.clicked(event, x, y);
                clickAnalyzer((short) 1);
            }
        });
        rightTable.add(playButton).expand().fill().prefHeight(Gdx.graphics.getHeight()*0.3f).pad(Gdx.graphics.getHeight()*0.01f).row();

        secondButton = new TextButton("OPTION", skin);
        secondButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MainMenuScreen::secondButton::clicked()", "-- event:" + event);
                super.clicked(event, x, y);
                clickAnalyzer((short) 2);
            }
        });
        rightTable.add(secondButton).expand().fill().prefHeight(Gdx.graphics.getHeight()*0.3f).pad(Gdx.graphics.getHeight()*0.01f).row();

        exitButton = new TextButton("EXIT", skin);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MainMenuScreen::exitButton::clicked()", "-- event:" + event);
                super.clicked(event, x, y);
                clickAnalyzer((short) 3);
            }
        });
        rightTable.add(exitButton).expand().fill().prefHeight(Gdx.graphics.getHeight()*0.3f).pad(Gdx.graphics.getHeight()*0.01f);
        rootTable.add(rightTable).expand().fillX().right();

//        towerDefence.gameLevelMaps.add("maps/test.tmx");
        // Campaign levels
//        FileHandle mapsDir = Gdx.files.internal("maps");
//        if(mapsDir.list().length == 0) {
//            towerDefence.gameLevelMaps.add("maps/desert.tmx");
//            towerDefence.gameLevelMaps.add("maps/summer.tmx");
//            towerDefence.gameLevelMaps.add("maps/winter.tmx");
            towerDefence.gameLevelMaps.add("maps/arena0.tmx");
            towerDefence.gameLevelMaps.add("maps/randomMap.tmx");
            towerDefence.gameLevelMaps.add("maps/island.tmx");
            towerDefence.gameLevelMaps.add("maps/arena1.tmx");
            towerDefence.gameLevelMaps.add("maps/arena2.tmx");
//            towerDefence.gameLevelMaps.add("maps/old/arena3.tmx");
            towerDefence.gameLevelMaps.add("maps/arena4.tmx");
            towerDefence.gameLevelMaps.add("maps/arena4_1.tmx");
            towerDefence.gameLevelMaps.add("maps/sample.tmx");
//        } else {
//            for(FileHandle fileHandle : mapsDir.list()) {
//                if(fileHandle.extension().equals("tmx")) {
//                    Gdx.app.log("MainMenuScreen::MainMenuScreen()", "-- towerDefence.gameLevelMaps.add():" + fileHandle.path());
//                    towerDefence.gameLevelMaps.add(fileHandle.path());
//                }
//            }
//        }
        Gdx.app.log("MainMenuScreen::MainMenuScreen()", "-- towerDefence.gameLevelMaps.size:" + towerDefence.gameLevelMaps.size);
    }

    @Override
    public void show() {
        Gdx.app.log("MainMenuScreen::show()", "-- Called!");
        Gdx.input.setInputProcessor(stage);
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
        sizeLabel.setText(width + "x" + height);

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
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            Gdx.app.log("MainMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.BACK || Input.Keys.BACKSPACE);");
            menuLvl--;
            if(menuLvl == -1) {
                towerDefence.dispose();
            }
            switchMenuButtons();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_0) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
            Gdx.app.log("HelpMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUMPAD_0 || Input.Keys.NUM_0);");
            towerDefence.addScreen(towerDefence.helpMenuScreen);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            Gdx.app.log("MainMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUMPAD_1 || Input.Keys.NUM_1);");
            clickAnalyzer((short)1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            Gdx.app.log("MainMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUMPAD_2 || Input.Keys.NUM_2);");
            clickAnalyzer((short)2);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_3) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            Gdx.app.log("MainMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUMPAD_3 || Input.Keys.NUM_3);");
            clickAnalyzer((short)3);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            Gdx.app.log("MainMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.ENTER);");
            Gdx.app.log("MainMenuScreen::inputHandler()", "-- Campaign levels:" + towerDefence.gameLevelMaps.toString());
            towerDefence.nextGameLevel();
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
                        towerDefence.addScreen(towerDefence.optionMenuScreen);
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
//                        } else {
                            towerDefence.addScreen(new MapEditorScreen(towerDefence, "maps/aaagen.tmx"));
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
                        mapName = "maps/arena0.tmx";
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
                        towerDefence.gameSettings.difficultyLevel = 0.5f;
                        towerDefence.addScreen(new GameScreen(mapName, towerDefence.factionsManager, towerDefence.gameSettings));
                        break;
                    case 2:
                        //start game with NORMAL
                        towerDefence.gameSettings.difficultyLevel = 1f;
                        towerDefence.addScreen(new GameScreen(mapName, towerDefence.factionsManager, towerDefence.gameSettings));
                        break;
                    case 3:
                        //start game with HARD
                        towerDefence.gameSettings.difficultyLevel = 2f;
                        towerDefence.addScreen(new GameScreen(mapName, towerDefence.factionsManager, towerDefence.gameSettings));
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
        sb.append("mapName:" + mapName);
        sb.append(",menuLvl:" + menuLvl);
        if (full) {
            sb.append(",stage:" + stage);
        }
        sb.append("]");
        return sb.toString();
    }
}
