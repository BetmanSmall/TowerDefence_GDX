package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.betmansmall.server.GameServerScreen;
import com.betmansmall.server.SessionSettings;

public class MainMenuScreen implements Screen {
    private WidgetController widgetController;

    private Stage stage;

    private TextButton helpButton;
    private TextButton playButton;
    private TextButton secondButton;
    private TextButton exitButton;
    private TextButton backButton;
    private TextButton homeButton;
    private TextButton serverButton;
    private TextButton clientButton;

    private Label sizeLabel;

    private int menuLvl;

//    public String mapName;

    public MainMenuScreen(final WidgetController widgetController) {
        this.widgetController = widgetController;

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
        stage.addActor(widgetController.backgroundImages.get(0));
//        stage.setDebugAll(true);
        stage.setDebugUnderMouse(true);
        stage.setDebugParentUnderMouse(true);

        Table rootTable = new Table(skin);
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        Table leftTable = new Table(skin);
        helpButton = new TextButton("HELP", skin);
//        helpButton.setScale(Gdx.graphics.getHeight()*0.01f);
        helpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MainMenuScreen::helpButton::clicked()", "-- event:" + event);
                super.clicked(event, x, y);
                widgetController.addScreen(widgetController.helpMenuScreen);
            }
        });
        leftTable.add(helpButton).expand().fill().prefHeight(Gdx.app.getGraphics().getHeight()*0.3f).pad(Gdx.graphics.getHeight()*0.01f).colspan(2).row();

        backButton = new TextButton("BACK", skin);
//        backButton.setScale(Gdx.graphics.getHeight()*0.1f);
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
//        homeButton.setScale(Gdx.graphics.getHeight()*0.01f);
        homeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MainMenuScreen::homeButton::clicked()", "-- event:" + event);
                super.clicked(event, x, y);
                menuLvl = 0;
                switchMenuButtons();
            }
        });
        leftTable.add(homeButton).expand().fill().prefHeight(Gdx.graphics.getHeight()*0.3f).pad(Gdx.graphics.getHeight()*0.01f).row();

        serverButton = new TextButton("GameServerScreen", skin);
        serverButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MainMenuScreen::serverButton::clicked()", "-- event:" + event);
                super.clicked(event, x, y);
                SessionSettings sessionSettings = new SessionSettings(widgetController.gameSettings);
                sessionSettings.localServer = true;
                sessionSettings.gameSettings.mapPath = "maps/arena0.tmx";
                sessionSettings.gameSettings.gameType = GameType.TowerDefence;
                widgetController.addScreen(new GameServerScreen(widgetController.factionsManager, sessionSettings));
            }
        });
        leftTable.add(serverButton).expand().fill().prefHeight(Gdx.app.getGraphics().getHeight()*0.1f).pad(Gdx.graphics.getHeight()*0.01f).colspan(2).row();

        clientButton = new TextButton("GameClientScreen", skin);
        clientButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MainMenuScreen::clientButton::clicked()", "-- event:" + event);
                super.clicked(event, x, y);
                SessionSettings sessionSettings = new SessionSettings(widgetController.gameSettings);
//                sessionSettings.localServer = false;
//                sessionSettings.host = "localhost";
//                sessionSettings.port = 48999;
//                sessionSettings.gameSettings.mapPath = "maps/arena0.tmx";
//                sessionSettings.gameSettings.gameType = GameType.TowerDefence;
                widgetController.addScreen(new GameClientScreen(widgetController.factionsManager, sessionSettings));
            }
        });
        leftTable.add(clientButton).expand().fill().prefHeight(Gdx.app.getGraphics().getHeight()*0.1f).pad(Gdx.graphics.getHeight()*0.01f).colspan(2).row();
        rootTable.add(leftTable).expandX().fillX().left();

        Table middleTable = new Table(skin);
        sizeLabel = new Label(Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight(), skin);
        sizeLabel.setFontScale(Gdx.graphics.getHeight()*0.01f);
//        sizeLabel.setFHeight(Gdx.graphics.getHeight()*0.3f);
        middleTable.add(sizeLabel).center();
        rootTable.add(middleTable).top();

        Table rightTable = new Table(skin);
        playButton = new TextButton("PLAY", skin);
//        playButton.setScale(Gdx.graphics.getHeight()*0.01f);
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
//        secondButton.setScale(Gdx.graphics.getHeight()*0.01f);
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
//        exitButton.setScale(Gdx.graphics.getHeight()*0.01f);
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

//        widgetController.gameLevelMaps.add("maps/test.tmx");
        // Campaign levels
//        FileHandle mapsDir = Gdx.files.internal("maps");
//        if(mapsDir.list().length == 0) {
//            widgetController.gameLevelMaps.add("maps/desert.tmx");
//            widgetController.gameLevelMaps.add("maps/summer.tmx");
//            widgetController.gameLevelMaps.add("maps/winter.tmx");
            widgetController.gameLevelMaps.add("maps/arena0.tmx");
            widgetController.gameLevelMaps.add("maps/randomMap.tmx");
            widgetController.gameLevelMaps.add("maps/island.tmx");
            widgetController.gameLevelMaps.add("maps/arena1.tmx");
            widgetController.gameLevelMaps.add("maps/arena2.tmx");
//            widgetController.gameLevelMaps.add("maps/old/arena3.tmx");
            widgetController.gameLevelMaps.add("maps/arena4.tmx");
            widgetController.gameLevelMaps.add("maps/arena4_1.tmx");
            widgetController.gameLevelMaps.add("maps/sample.tmx");
//        } else {
//            for(FileHandle fileHandle : mapsDir.list()) {
//                if(fileHandle.extension().equals("tmx")) {
//                    Gdx.app.log("MainMenuScreen::MainMenuScreen()", "-- widgetController.gameLevelMaps.add():" + fileHandle.path());
//                    widgetController.gameLevelMaps.add(fileHandle.path());
//                }
//            }
//        }
        Gdx.app.log("MainMenuScreen::MainMenuScreen()", "-- widgetController.gameLevelMaps.size:" + widgetController.gameLevelMaps.size);
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
                widgetController.dispose();
            }
            switchMenuButtons();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_0) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
            Gdx.app.log("HelpMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUMPAD_0 || Input.Keys.NUM_0);");
            widgetController.addScreen(widgetController.helpMenuScreen);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            Gdx.app.log("MainMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUMPAD_1 || Input.Keys.NUM_1);");
            clickAnalyzer((short)1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            Gdx.app.log("MainMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUMPAD_2 || Input.Keys.NUM_2);");
            clickAnalyzer((short)2);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_3) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            Gdx.app.log("MainMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUMPAD_3 || Input.Keys.NUM_3);");
            clickAnalyzer((short)3);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            Gdx.app.log("MainMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.ENTER || Input.Keys.SPACE);");
            Gdx.app.log("MainMenuScreen::inputHandler()", "-- Campaign levels:" + widgetController.gameLevelMaps.toString());
            widgetController.nextGameLevel();
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
                        widgetController.addScreen(widgetController.optionMenuScreen);
                        break;
                    case 3:
                        //Exit button
                        widgetController.dispose();
                        break;
                }
                break;
            case 1:                                             //Play menu
                switch (buttonNumber) {
                    case 1:
                        Gdx.app.log("MainMenuScreen::clickAnalyzer()", "-- Campaign levels:" + widgetController.gameLevelMaps.toString());
                        widgetController.nextGameLevel();
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
//                            widgetController.setScreen(new MapEditorScreen(widgetController, fileName));
//                        } else {
                            widgetController.addScreen(new MapEditorScreen(widgetController, "maps/aaagen.tmx"));
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
                        widgetController.gameSettings.mapPath = "maps/arena0.tmx";
                        break;
                    case 2:
                        //Choose map2
                        menuLvl = 3;
                        switchMenuButtons();
                        widgetController.gameSettings.mapPath = "maps/arena2.tmx";
                        break;
                    case 3:
                        //Choose map3
                        menuLvl = 3;
                        switchMenuButtons();
                        widgetController.gameSettings.mapPath = "maps/arena4.tmx";
                        break;
                }
                break;
            case 3:                                             //Difficulty menu
                switch (buttonNumber) {
                    case 1:
                        //start game with EASY
                        widgetController.gameSettings.difficultyLevel = 0.5f;
                        widgetController.addScreen(new GameScreen(widgetController.factionsManager, widgetController.gameSettings));
                        break;
                    case 2:
                        //start game with NORMAL
                        widgetController.gameSettings.difficultyLevel = 1f;
                        widgetController.addScreen(new GameScreen(widgetController.factionsManager, widgetController.gameSettings));
                        break;
                    case 3:
                        //start game with HARD
                        widgetController.gameSettings.difficultyLevel = 2f;
                        widgetController.addScreen(new GameScreen(widgetController.factionsManager, widgetController.gameSettings));
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
