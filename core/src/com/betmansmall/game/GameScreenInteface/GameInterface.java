package com.betmansmall.game.GameScreenInteface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.betmansmall.game.gameLogic.GameField;

/**
 * Created by Transet/AndeyA on 07.02.2016. (GovnoDoderbI)
 * This class provides elements which placed on game screen.
 * TODO implement more interface options
 */
public class GameInterface {
    public BitmapFont bitmapFont;
    public Stage stage;
    public Label gamerGoldLabel, missedAndLimit, fpsLabel;

    public TowersRoulette towersRoulette;
    public CreepsRoulette creepsRoulette;

    public GameInterface(GameField gameField) {
        Gdx.app.log("GameInterface::GameInterface(" + gameField + ")", "-- Called!");
        bitmapFont = new BitmapFont();
        stage = new Stage();
        gamerGoldLabel = new Label("gamerGold:", new Label.LabelStyle(bitmapFont, Color.YELLOW));
        gamerGoldLabel.setPosition(Gdx.graphics.getWidth()*0.60f, 15.0f);
        gamerGoldLabel.setFontScale(2f);
        missedAndLimit = new Label("10/100", new Label.LabelStyle(bitmapFont, Color.PINK));
        missedAndLimit.setPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()-20f);
        missedAndLimit.setFontScale(2f);
        fpsLabel = new Label("000", new Label.LabelStyle(bitmapFont, Color.WHITE));
        fpsLabel.setPosition(0.0f, Gdx.graphics.getHeight() - 18.0f);
        stage.addActor(gamerGoldLabel);
        stage.addActor(missedAndLimit);
        stage.addActor(fpsLabel);

        towersRoulette = new TowersRoulette(gameField, bitmapFont, stage);
        creepsRoulette = new CreepsRoulette(gameField);
        for(Actor actor : creepsRoulette.getGroup()) {
            stage.addActor(actor);
        }
        try {
            for (Actor actor : towersRoulette.getGroup()) {
                stage.addActor(actor);
            }
        } catch(Error error) {
            Gdx.app.log("GameInterface::GameInterface()", "-- no circle(???) group");
        }
    }

    public void dispose() {
        Gdx.app.log("GameInterface::dispose()", "-- Called!");
        stage.dispose();
//        towersRoulette.dispose();
//        creepsRoulette.dispose();
        bitmapFont.dispose();
    }
}
