package com.betmansmall.game.GameScreenInteface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.TowerDefence;
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

    private Label actionsHistoryLabel;
    private Array<String> arrayActionsHistory;
    private float deleteActionThrough, actionInHistoryTime;

    public TowersRoulette towersRoulette;
    public CreepsRoulette creepsRoulette;

    private Texture winTexture, loseTexture;
    private float currentTextureTime, maxTextureTime;

    public GameInterface(GameField gameField) {
        Gdx.app.log("GameInterface::GameInterface(" + gameField + ")", "-- Called!");
        bitmapFont = new BitmapFont();
        stage = new Stage();
        gamerGoldLabel = new Label("gamerGold:", new Label.LabelStyle(bitmapFont, Color.YELLOW));
        gamerGoldLabel.setPosition(Gdx.graphics.getWidth()*0.60f, 15.0f);
        gamerGoldLabel.setFontScale(2f);
        stage.addActor(gamerGoldLabel);
        missedAndLimit = new Label("10/100", new Label.LabelStyle(bitmapFont, Color.PINK));
        missedAndLimit.setPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()-20f);
        missedAndLimit.setFontScale(2f);
        stage.addActor(missedAndLimit);
        fpsLabel = new Label("000", new Label.LabelStyle(bitmapFont, Color.WHITE));
        fpsLabel.setPosition(0.0f, Gdx.graphics.getHeight() - 18.0f);
        stage.addActor(fpsLabel);
        actionsHistoryLabel = new Label("actionsHistory1\nactionsHistory2\nactionsHistory3", new Label.LabelStyle(bitmapFont, Color.WHITE));
        actionsHistoryLabel.setPosition(0.0f, Gdx.graphics.getHeight()/2);
        stage.addActor(actionsHistoryLabel);
        arrayActionsHistory = new Array<String>();
        deleteActionThrough = 0f;
        actionInHistoryTime = 1f;

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

        winTexture = new Texture(Gdx.files.internal("img/victory.jpg"));
        loseTexture = new Texture(Gdx.files.internal("img/defeat.jpg"));
        currentTextureTime = 0f;
        maxTextureTime = 1f;
    }

    public void addActionToHistory(String action) {
        arrayActionsHistory.add(action);
    }

    public void render(float delta) {
        if(arrayActionsHistory.size > 0) {
            deleteActionThrough += delta;
            if (deleteActionThrough > actionInHistoryTime) {
                arrayActionsHistory.removeIndex(0);
                deleteActionThrough = 0f;
            }
            StringBuilder sb = new StringBuilder();
            for(String str : arrayActionsHistory) {
                sb.append("\n" + str);
            }
            actionsHistoryLabel.setText(sb.toString());
        }
        stage.act(delta);
        stage.draw();
    }

    public void renderEndGame(float delta, String gameState) {
        currentTextureTime += delta;
        if (currentTextureTime > maxTextureTime) {
//            this.dispose();
            TowerDefence.getInstance().nextGameLevel();
            return; // It'is really need???
        }
        Batch batch = stage.getBatch(); // Need have own batch. mb get from GameScreen
        batch.begin();
        if(gameState.equals("Win")) {
            batch.draw(winTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        } else if(gameState.equals("Lose")) {
            batch.draw(loseTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        batch.end();
    }

    public void dispose() {
        Gdx.app.log("GameInterface::dispose()", "-- Called!");
        bitmapFont.dispose();
        stage.dispose();
//        towersRoulette.dispose();
//        creepsRoulette.dispose();
        winTexture.dispose();
        loseTexture.dispose();
    }
}
