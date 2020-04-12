package com.betmansmall.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.betmansmall.GameMaster;
import com.betmansmall.game.GameSettings;
import com.betmansmall.screens.client.GameScreen;
import com.betmansmall.util.logging.Logger;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

/**
 * Created by betma on 04.12.2018.
 */

public class OptionMenuScreen implements Screen {
    private GameMaster gameMaster;

    private Stage stage;

    private VisCheckBox topBottomLeftRightSelector;
    private VisCheckBox verticalSelector;
    private VisCheckBox smoothFlingSelector;
    private VisCheckBox checkBoxSound;
    private Slider sliderSound;
    private Slider sliderEnemyCount;
    private Slider sliderTowerCount;
    private Slider sliderDifficultyLevel;
    private Slider sliderLandscapePercent;

    private VisCheckBox panLeftMouseButton;
    private VisCheckBox panMidMouseButton;
    private VisCheckBox towerMoveAlgorithm;
    private VisCheckBox panRightMouseButton;
    private TextButton backButton;

    public OptionMenuScreen(final GameMaster gameMaster, final GameSettings gameSettings) {
        this.gameMaster = gameMaster;

        stage = new Stage(new ScreenViewport());
        stage.addActor(gameMaster.backgroundImages.get(2));

        stage.setDebugUnderMouse(true);
        stage.setDebugParentUnderMouse(true);
        stage.setDebugTableUnderMouse(true);
        stage.setDebugInvisible(true);

        Table rootTable = new VisTable();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        TextButton playButton = new VisTextButton("PLAY");
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MainMenuScreen::playButton::clicked()", "-- event:" + event);
                super.clicked(event, x, y);
                gameSettings.setGameTypeByMap("maps/randomMap.tmx");
                gameMaster.setScreen(new GameScreen(gameMaster));
            }
        });
        rootTable.add(playButton).colspan(2).expand().fill().row();

        Table table1 = new VisTable();

        topBottomLeftRightSelector = new VisCheckBox("topBottomLeftRightSelector");
        topBottomLeftRightSelector.getLabel().setColor(Color.BLACK);
        topBottomLeftRightSelector.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("OptionMenuScreen::topBottomLeftRightSelector::changed()", "-- topBottomLeftRightSelector.isChecked():" + topBottomLeftRightSelector.isChecked());
                gameSettings.topBottomLeftRightSelector = topBottomLeftRightSelector.isChecked();
            }
        });
        topBottomLeftRightSelector.setChecked(gameSettings.topBottomLeftRightSelector);
        table1.add(topBottomLeftRightSelector).row();

        verticalSelector = new VisCheckBox("verticalSelector");
        verticalSelector.getLabel().setColor(Color.BLACK);
        verticalSelector.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("OptionMenuScreen::verticalSelector::changed()", "-- verticalSelector.isChecked():" + verticalSelector.isChecked());
                gameSettings.verticalSelector = verticalSelector.isChecked();
            }
        });
        verticalSelector.setChecked(gameSettings.verticalSelector);
        table1.add(verticalSelector).row();

        smoothFlingSelector = new VisCheckBox("smoothFlingSelector");
        smoothFlingSelector.getLabel().setColor(Color.BLACK);
        smoothFlingSelector.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("OptionMenuScreen::smoothFlingSelector::changed()", "-- smoothFlingSelector.isChecked():" + smoothFlingSelector.isChecked());
                gameSettings.smoothFlingSelector = smoothFlingSelector.isChecked();
            }
        });
        smoothFlingSelector.setChecked(gameSettings.smoothFlingSelector);
        table1.add(smoothFlingSelector).row();

        rootTable.add(table1);
        Table table2 = new VisTable();

        panLeftMouseButton = new VisCheckBox("panLeftMouseButton");
        panLeftMouseButton.getLabel().setColor(Color.BLACK);
        panLeftMouseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("OptionMenuScreen::panLeftMouseButton::changed()", "-- panLeftMouseButton.isChecked():" + panLeftMouseButton.isChecked());
            }
        });
        panLeftMouseButton.setChecked(gameSettings.panLeftMouseButton);
        table2.add(panLeftMouseButton).row();

        panMidMouseButton = new VisCheckBox("panMidMouseButton");
        panMidMouseButton.getLabel().setColor(Color.BLACK);
        panMidMouseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("OptionMenuScreen::panMidMouseButton::changed()", "-- panMidMouseButton.isChecked():" + panMidMouseButton.isChecked());
            }
        });
        panMidMouseButton.setChecked(gameSettings.panMidMouseButton);
        table2.add(panMidMouseButton).row();

        panRightMouseButton = new VisCheckBox("panRightMouseButton");
        panRightMouseButton.getLabel().setColor(Color.BLACK);
        panRightMouseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("OptionMenuScreen::panRightMouseButton::changed()", "-- panRightMouseButton.isChecked():" + panRightMouseButton.isChecked());
            }
        });
        panRightMouseButton.setChecked(gameSettings.panRightMouseButton);
        table2.add(panRightMouseButton).row();

        towerMoveAlgorithm = new VisCheckBox("towerMoveAlgorithm");
        towerMoveAlgorithm.getLabel().setColor(Color.BLACK);
        towerMoveAlgorithm.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("OptionMenuScreen::towerMoveAlgorithm::changed()", "-- towerMoveAlgorithm.isChecked():" + towerMoveAlgorithm.isChecked());
                gameSettings.towerMoveAlgorithm = towerMoveAlgorithm.isChecked();
            }
        });
        towerMoveAlgorithm.setChecked(gameSettings.towerMoveAlgorithm);
        table2.add(towerMoveAlgorithm).row();

        rootTable.add(table2).row();
        Table table3 = new VisTable();

        checkBoxSound = new VisCheckBox("sound On/Off:");
        checkBoxSound.getLabel().setColor(Color.BLACK);
        checkBoxSound.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("OptionMenuScreen::checkBoxSound::changed()", "-- checkBoxSound.isChecked():" + checkBoxSound.isChecked());
            }
        });
        checkBoxSound.setChecked(gameSettings.panLeftMouseButton);
        table3.add(checkBoxSound);

        sliderSound = new VisSlider(0f, 100f, 1f, false);
        sliderSound.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("OptionMenuScreen::sliderSound::changed()", "-- sliderSound.getValue():" + sliderSound.getValue());
            }
        });
        table3.add(sliderSound).row();

        final Label enemyCountLabel = new VisLabel("enemyCount:");
        enemyCountLabel.setColor(Color.BLACK);
        table3.add(enemyCountLabel);

        sliderEnemyCount = new VisSlider(0f, 32f, 1f, false);
        sliderEnemyCount.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("OptionMenuScreen::sliderEnemyCount::changed()", "-- sliderEnemyCount.getValue():" + sliderEnemyCount.getValue());
                enemyCountLabel.setText("enemyCount:" + sliderEnemyCount.getValue());
                gameSettings.enemyCount = (int)sliderEnemyCount.getValue();
            }
        });
        sliderEnemyCount.setValue(gameSettings.enemyCount);
        table3.add(sliderEnemyCount).row();

        final Label towerCountLabel = new VisLabel("towerCount:");
        towerCountLabel.setColor(Color.BLACK);
        table3.add(towerCountLabel);

        sliderTowerCount = new VisSlider(0f, 32f, 1f, false);
        sliderTowerCount.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("OptionMenuScreen::sliderTowerCount::changed()", "-- sliderTowerCount.getValue():" + sliderTowerCount.getValue());
                towerCountLabel.setText("towerCount:" + sliderTowerCount.getValue());
                gameSettings.towersCount = (int)sliderTowerCount.getValue();
            }
        });
        sliderTowerCount.setValue(gameSettings.towersCount);
        table3.add(sliderTowerCount).row();

        final Label difficultyLevelLabel = new VisLabel("difficultyLevel:");
        difficultyLevelLabel.setColor(Color.BLACK);
        table3.add(difficultyLevelLabel);

        sliderDifficultyLevel = new VisSlider(0f, 4f, 1f, false);
        sliderDifficultyLevel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("OptionMenuScreen::sliderDifficultyLevel::changed()", "-- sliderDifficultyLevel.getValue():" + sliderDifficultyLevel.getValue());
                difficultyLevelLabel.setText("difficultyLevel:" + sliderDifficultyLevel.getValue());
                gameSettings.difficultyLevel = (int)sliderDifficultyLevel.getValue();
            }
        });
        sliderDifficultyLevel.setValue(gameSettings.difficultyLevel);
        table3.add(sliderDifficultyLevel).row();

        final Label landscapePercentLabel = new VisLabel("landscapePercent:");
        landscapePercentLabel.setColor(Color.BLACK);
        table3.add(landscapePercentLabel);

        sliderLandscapePercent = new VisSlider(0f, 100f, 1f, false);
        sliderLandscapePercent.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Logger.logDebug("-- sliderLandscapePercent.getValue():" + sliderLandscapePercent.getValue());
                landscapePercentLabel.setText("landscapePercent:" + sliderLandscapePercent.getValue());
                gameSettings.landscapePercent = (int)sliderLandscapePercent.getValue();
            }
        });
        sliderLandscapePercent.setValue(gameSettings.landscapePercent);
        table3.add(sliderLandscapePercent).row();

        rootTable.add(table3).colspan(2).row();

        backButton = new VisTextButton("BACK");
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("OptionMenuScreen::backButton::changed()", "-- backButton.isChecked():" + backButton.isChecked());
                gameMaster.removeTopScreen();
            }
        });
        rootTable.add(backButton).colspan(2).expand().fill().row();
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.log("OptionMenuScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.BACK || Input.Keys.ESCAPE);");
            gameMaster.removeTopScreen();
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
//            Gdx.app.log("OptionMenuScreen::inputHandler()", "-- Campaign levels:" + gameMaster.gameLevelMaps.toString());
//            gameMaster.nextGameLevel();
        }
    }
}
