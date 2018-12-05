package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by betma on 05.12.2018.
 */

public class HelpMenuScreen implements Screen {
    private TowerDefence towerDefence;

    private Stage stage;
    private Array<Image> helpImages;

    public HelpMenuScreen(final TowerDefence towerDefence) {
        this.towerDefence = towerDefence;

        stage = new Stage(new ScreenViewport());
        stage.addActor(towerDefence.backgroundImages.get(1));
        stage.setDebugAll(true);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        FileHandle imagesDir = Gdx.files.internal("helpImages");
        FileHandle[] fileHandles = imagesDir.list();
        helpImages = new Array<Image>();
        for (FileHandle fileHandle : fileHandles) {
            if (fileHandle.extension().equals("png")) {
                Image image = new Image(new Texture(fileHandle));
//                image.setFillParent(true);
                helpImages.add(image);
            }
        }
        table.add(helpImages.random()).expand();

//        HorizontalGroup horizontalGroup = new HorizontalGroup();

    }

    @Override
    public void show() {
        Gdx.app.log("HelpMenuScreen::show()", "--");
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
//      Gdx.app.log("HelpMenuScreen::render()", "-- delta:" + delta + " FPS:" + Gdx.graphics.getFramesPerSecond());
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
        Gdx.app.log("HelpMenuScreen::resize(" + width + ", " + height + ")", "--");
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        Gdx.app.log("HelpMenuScreen::pause()", "--");
    }

    @Override
    public void resume() {
        Gdx.app.log("HelpMenuScreen::resume()", "--");
    }

    @Override
    public void hide() {
        Gdx.app.log("HelpMenuScreen::hide()", "--");
    }

    @Override
    public void dispose() {
        Gdx.app.log("HelpMenuScreen::dispose()", "--");
    }

    private void inputHandler(float delta) {
//        Gdx.app.log("HelpMenuScreen::inputHandler(" + delta + ");");
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            Gdx.app.log("HelpMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.BACK || Input.Keys.BACKSPACE);");
            towerDefence.removeTopScreen();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            Gdx.app.log("HelpMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUMPAD_1 || Input.Keys.NUM_1);");
//            clickAnalyzer((short)1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            Gdx.app.log("HelpMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUMPAD_2 || Input.Keys.NUM_2);");
//            clickAnalyzer((short)2);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_3) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            Gdx.app.log("HelpMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUMPAD_3 || Input.Keys.NUM_3);");
//            clickAnalyzer((short)3);
//        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
//            Gdx.app.log("HelpMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.ENTER);");
//            Gdx.app.log("HelpMenuScreen::inputHandler()", "-- Campaign levels:" + towerDefence.gameLevelMaps.toString());
//            towerDefence.nextGameLevel();
        }
    }
}
