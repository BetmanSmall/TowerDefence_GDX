package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by betma on 04.12.2018.
 */

public class OptionMenuScreen implements Screen {
    private TowerDefence towerDefence;

    private Stage stage;

    private CheckBox checkBoxSound;
    private Slider sliderSound;
    private Slider sliderEnemyCount;
    private Slider sliderTowerCount;
    private Slider sliderDifficultyLevel;

    private CheckBox panLeftMouseButton;
    private CheckBox panMidMouseButton;
    private CheckBox panRightMouseButton;
    private TextButton backButton;

    public OptionMenuScreen(final TowerDefence towerDefence) {
        this.towerDefence = towerDefence;

        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        stage = new Stage(new ScreenViewport());
        stage.addActor(towerDefence.backgroundImages.get(2));
        stage.setDebugAll(true);

        Table rootTable = new Table(skin);
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        Table rightTable = new Table(skin);

        panLeftMouseButton = new CheckBox("panLeftMouseButton", skin);
        panLeftMouseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
//                Gdx.app.log("OptionMenuScreen::panLeftMouseButton::changed()", "-- event:" + event);
//                Gdx.app.log("OptionMenuScreen::panLeftMouseButton::changed()", "-- actor:" + actor);
                Gdx.app.log("OptionMenuScreen::sliderDifficultyLevel::changed()", "-- sliderDifficultyLevel.getValue():" + sliderDifficultyLevel.getValue());
            }
        });
        rightTable.add(panLeftMouseButton).left().colspan(2).row();

        panMidMouseButton = new CheckBox("panMidMouseButton", skin);
        panMidMouseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("OptionMenuScreen::panMidMouseButton::changed()", "-- event:" + event);
                Gdx.app.log("OptionMenuScreen::panMidMouseButton::changed()", "-- actor:" + actor);
            }
        });
        rightTable.add(panMidMouseButton).left().colspan(2).row();

        panRightMouseButton = new CheckBox("panRightMouseButton", skin);
        panRightMouseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("OptionMenuScreen::panRightMouseButton::changed()", "-- event:" + event);
                Gdx.app.log("OptionMenuScreen::panRightMouseButton::changed()", "-- actor:" + actor);
            }
        });
        rightTable.add(panRightMouseButton).left().colspan(2).row();

        checkBoxSound = new CheckBox("sound On/Off:", skin);
        checkBoxSound.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("OptionMenuScreen::checkBoxSound::changed()", "-- event:" + event);
                Gdx.app.log("OptionMenuScreen::checkBoxSound::changed()", "-- actor:" + actor);
            }
        });
        rightTable.add(checkBoxSound).right();

        sliderSound = new Slider(0f, 100f, 1f, false, skin);
        sliderSound.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("OptionMenuScreen::sliderSound::changed()", "-- event:" + event);
                Gdx.app.log("OptionMenuScreen::sliderSound::changed()", "-- actor:" + actor);
            }
        });
        rightTable.add(sliderSound).row();

        Label enemyCountLabel = new Label("enemyCount:", skin);
        rightTable.add(enemyCountLabel).right();

        sliderEnemyCount = new Slider(0f, 100f, 1f, false, skin);
        sliderEnemyCount.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
//                Gdx.app.log("OptionMenuScreen::sliderEnemyCount::changed()", "-- event:" + event);
//                Gdx.app.log("OptionMenuScreen::sliderEnemyCount::changed()", "-- actor:" + actor);
                Gdx.app.log("OptionMenuScreen::sliderEnemyCount::changed()", "-- sliderEnemyCount.getValue():" + sliderEnemyCount.getValue());
            }
        });
        rightTable.add(sliderEnemyCount).row();

        Label difficultyLevelLabel = new Label("difficultyLevel:", skin);
        rightTable.add(difficultyLevelLabel).right();

        sliderDifficultyLevel = new Slider(0f, 100f, 1f, false, skin);
        sliderDifficultyLevel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
//                Gdx.app.log("OptionMenuScreen::sliderDifficultyLevel::changed()", "-- event:" + event);
//                Gdx.app.log("OptionMenuScreen::sliderDifficultyLevel::changed()", "-- actor:" + actor);
                Gdx.app.log("OptionMenuScreen::sliderDifficultyLevel::changed()", "-- sliderDifficultyLevel.getValue():" + sliderDifficultyLevel.getValue());
            }
        });
        rightTable.add(sliderDifficultyLevel).row();

        Label towerCountLabel = new Label("towerCount:", skin);
        rightTable.add(towerCountLabel).right();

        sliderTowerCount = new Slider(0f, 100f, 1f, false, skin);
        sliderTowerCount.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
//                Gdx.app.log("OptionMenuScreen::sliderTowerCount::changed()", "-- event:" + event);
//                Gdx.app.log("OptionMenuScreen::sliderTowerCount::changed()", "-- actor:" + actor);
                Gdx.app.log("OptionMenuScreen::sliderTowerCount::changed()", "-- sliderTowerCount.getValue():" + sliderTowerCount.getValue());
            }
        });
        rightTable.add(sliderTowerCount).row();

        backButton = new TextButton("BACK", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("OptionMenuScreen::backButton::changed()", "-- event:" + event);
                Gdx.app.log("OptionMenuScreen::backButton::changed()", "-- actor:" + actor);
                towerDefence.removeTopScreen();
            }
        });
//        backButton.setScaleX(200f);
//        backButton.setScale(2f);
        rightTable.add(backButton).colspan(2).fillX();
        rootTable.add(rightTable).right().bottom().expand();
    }

    @Override
    public void show() {
        Gdx.app.log("OptionMenuScreen::show()", "-- Called!");
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
//      Gdx.app.log("OptionMenuScreen::render()", "-- delta:" + delta + " FPS:" + Gdx.graphics.getFramesPerSecond());
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
        Gdx.app.log("OptionMenuScreen::resize(" + width + ", " + height + ")", "--");
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        Gdx.app.log("OptionMenuScreen::pause()", "--");
    }

    @Override
    public void resume() {
        Gdx.app.log("OptionMenuScreen::resume()", "--");
    }

    @Override
    public void hide() {
        Gdx.app.log("OptionMenuScreen::hide()", "--");
    }

    @Override
    public void dispose() {
        Gdx.app.log("OptionMenuScreen::dispose()", "--");
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
    }

    private void inputHandler(float delta) {
//        Gdx.app.log("OptionMenuScreen::inputHandler(" + delta + ");");
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            Gdx.app.log("OptionMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.BACK || Input.Keys.BACKSPACE);");
            towerDefence.removeTopScreen();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            Gdx.app.log("OptionMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUMPAD_1 || Input.Keys.NUM_1);");
//            clickAnalyzer((short)1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            Gdx.app.log("OptionMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUMPAD_2 || Input.Keys.NUM_2);");
//            clickAnalyzer((short)2);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_3) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            Gdx.app.log("OptionMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUMPAD_3 || Input.Keys.NUM_3);");
//            clickAnalyzer((short)3);
//        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
//            Gdx.app.log("OptionMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.ENTER);");
//            Gdx.app.log("OptionMenuScreen::inputHandler()", "-- Campaign levels:" + towerDefence.gameLevelMaps.toString());
//            towerDefence.nextGameLevel();
        }
    }
}