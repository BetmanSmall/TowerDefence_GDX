package com.betmansmall.game.GameScreenInteface;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Created by Transet on 07.02.2016.
 * This class provides elements which placed on game screen.
 * TODO implement more interface options
 */
public class GameInterface {

    public enum GameInterfaceElements {
        TOWERS_ROULETTE,
        CREEPS_ROULETTE
    }

    private TowersRoulette towersRoulette;
    private CreepsRoulette creepsRoulette;
    private Stage stage;

    public GameInterface() {
        init();
    }

    private void init() {
        stage = new Stage();
        towersRoulette = new TowersRoulette();
        creepsRoulette = new CreepsRoulette();

        stage.addActor(towersRoulette.getGroup());
        stage.addActor(creepsRoulette.getGroup());
    }

    public InputMultiplexer setCommonInputHandler(InputProcessor inputProcessor) {
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(inputProcessor);
        inputMultiplexer.addProcessor(stage);
        return inputMultiplexer;
    }

    public void act(float delta) {
        stage.act(delta);
    }

    public void draw() {
        stage.draw();
    }

}
