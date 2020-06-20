package com.betmansmall.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.betmansmall.GameMaster;
import com.betmansmall.screens.actors.SlidingGroup;

public class LevelSelectorScreen implements Screen, GestureDetector.GestureListener {
    private GameMaster gameMaster;
    private Stage stage;
    private Table rootTable;
    private SlidingGroup slidingGroup;
    private boolean pan = false;

    public LevelSelectorScreen(final GameMaster gameMaster) {
        this.gameMaster = gameMaster;
        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        stage = new Stage(new ScreenViewport());
        stage.addActor(gameMaster.backgroundImages.get(1));
        stage.setDebugAll(true);

        rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        slidingGroup = new SlidingGroup(gameMaster);
        rootTable.add(slidingGroup).center().expand().fill().row();

//        helpImagesHorGroup = new HorizontalGroup();
//        FileHandle imagesDir = Gdx.files.internal("helpImages");
//        FileHandle[] fileHandles = imagesDir.list();
//        helpImages = new Array<Image>();
//        for (FileHandle fileHandle : fileHandles) {
//            if (fileHandle.extension().equals("png")) {
//                Image image = new Image(new Texture(fileHandle));
////                image.setFillParent(true);
//                helpImages.add(image);
//                helpImagesHorGroup.addActor(image);
//            }
//        }
//        rootTable.add(helpImagesHorGroup).row();

        TextButton backButton = new TextButton("BACK", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameMaster.removeTopScreen();
            }
        });
        rootTable.add(backButton).bottom().fillX();
    }

    @Override
    public void show() {
        Gdx.app.log("LevelSelectorScreen::show()", "--");
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
//        inputMultiplexer.addProcessor(new GestureDetector(this));
//        inputMultiplexer.addProcessor(slidingGroup.getStage());
        inputMultiplexer.addProcessor(new GestureDetector(slidingGroup));
        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
//      Gdx.app.log("LevelSelectorScreen::render()", "-- delta:" + delta + " FPS:" + Gdx.graphics.getFramesPerSecond());
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        inputHandler(delta);

        if (stage != null) {
            stage.act(delta);
            stage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("LevelSelectorScreen::resize(" + width + ", " + height + ")", "--");
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        Gdx.app.log("LevelSelectorScreen::pause()", "--");
    }

    @Override
    public void resume() {
        Gdx.app.log("LevelSelectorScreen::resume()", "--");
    }

    @Override
    public void hide() {
        Gdx.app.log("LevelSelectorScreen::hide()", "--");
    }

    @Override
    public void dispose() {
        Gdx.app.log("LevelSelectorScreen::dispose()", "--");
    }

    private void inputHandler(float delta) {
//        Gdx.app.log("LevelSelectorScreen::inputHandler(" + delta + ");");
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            Gdx.app.log("LevelSelectorScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.BACK || Input.Keys.BACKSPACE);");
            gameMaster.removeTopScreen();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            Gdx.app.log("LevelSelectorScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUMPAD_1 || Input.Keys.NUM_1);");
//            clickAnalyzer((short)1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            Gdx.app.log("LevelSelectorScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUMPAD_2 || Input.Keys.NUM_2);");
//            clickAnalyzer((short)2);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_3) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            Gdx.app.log("LevelSelectorScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUMPAD_3 || Input.Keys.NUM_3);");
//            clickAnalyzer((short)3);
//        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
//            Gdx.app.log("LevelSelectorScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.ENTER);");
//            Gdx.app.log("LevelSelectorScreen::inputHandler()", "-- Campaign levels:" + towerDefence.gameLevelMaps.toString());
//            towerDefence.nextGameLevel();
        }
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    public boolean pan(float x, float y, float deltaX, float deltaY) {
        Gdx.app.log("LevelSelectorScreen::pan(" + x + "," + y + "," + deltaX + "," + deltaY + ")", "--");
//        float groupX = helpImagesHorGroup.getX();
//        float groupY = helpImagesHorGroup.getY();
//        float groupWidth = helpImagesHorGroup.getWidth();
//        float groupHeight = helpImagesHorGroup.getHeight();
//        float groupPrefWidth = helpImagesHorGroup.getPrefWidth();
//        float groupPrefHeight = helpImagesHorGroup.getPrefHeight();
//        float tableWidth = rootTable.getWidth();
//        float tableHeight = rootTable.getHeight();
////        Gdx.app.log("LevelSelectorScreen::pan()", "-- groupX:" + groupX + " groupY:" + groupY + " groupWidth:" + groupWidth + " groupHeight:" + groupHeight);
////        Gdx.app.log("LevelSelectorScreen::pan()", "-- groupPrefWidth:" + groupPrefWidth + " groupPrefHeight:" + groupPrefHeight + " tableWidth:" + tableWidth + " tableHeight:" + tableHeight);
////        Gdx.app.log("LevelSelectorScreen::pan()", "-- Gdx.graphics.getWidth():" + Gdx.graphics.getWidth() + " Gdx.graphics.getHeight():" + Gdx.graphics.getHeight());
////        Gdx.app.log("LevelSelectorScreen::pan()", "-- table.getStage().getViewport().getScreenWidth():" + table.getStage().getViewport().getScreenWidth());
////        Gdx.app.log("LevelSelectorScreen::pan()", "-- table.getStage().getViewport().getScreenHeight():" + table.getStage().getViewport().getScreenHeight());
////        Gdx.app.log("LevelSelectorScreen::pan()", "-- table.getStage().getViewport().getWorldWidth():" + table.getStage().getViewport().getWorldWidth());
////        Gdx.app.log("LevelSelectorScreen::pan()", "-- table.getStage().getViewport().getWorldHeight():" + table.getStage().getViewport().getWorldHeight());
////        Gdx.app.log("LevelSelectorScreen::pan()", "-- table.getWidth():" + table.getWidth() + " table.getHeight():" + table.getHeight());
//        if (Math.abs(deltaX) > Math.abs(deltaY) && !pan) {
//            if (x >= (tableWidth-groupWidth/**2f*/) && deltaX > 0) {
//                helpImagesHorGroup.moveBy(deltaX, 0);
//                if(helpImagesHorGroup.getX() > tableWidth) {
//                    helpImagesHorGroup.setX(tableWidth);
//                }
//                pan = false;
//                return true;
//            } else if (x >= (tableWidth-groupWidth) && deltaX < 0) {
//                helpImagesHorGroup.moveBy(deltaX, 0);
//                if(helpImagesHorGroup.getX() < tableWidth) {
//                    helpImagesHorGroup.setX(tableWidth-groupWidth);
//                }
//                pan = true;
//                return true;
//            }
//        } else if (x >= (tableWidth-groupWidth) || pan) {
////            pan = true;
//            if (deltaX < 0) {
//                helpImagesHorGroup.moveBy(0, -deltaY);
//            } else if (deltaX > 0) {
//                helpImagesHorGroup.moveBy(0, -deltaY);
//            }
//            if (helpImagesHorGroup.getY() > 0) {
//                helpImagesHorGroup.setY(0);
//            } else if(helpImagesHorGroup.getY()+ helpImagesHorGroup.getHeight() < tableHeight) {
//                helpImagesHorGroup.setY( (0-(helpImagesHorGroup.getHeight()-tableHeight)) );
//            }
//            return true;
//        }
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        Gdx.app.log("LevelSelectorScreen::panStop()", "-- x:" + x + " y:" + y + " pointer:" + pointer + " button:" + button);
        pan = false;
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }
}
