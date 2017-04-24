package com.betmansmall.game.GameScreenInteface;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.betmansmall.game.TowerDefence;
import com.betmansmall.game.gameLogic.GameField;

/**
 * Created by Transet/AndeyA on 07.02.2016. (GovnoDoderbI)
 * This class provides elements which placed on game screen.
 * TODO implement more interface options
 */
public class GameInterface {
    private GameField gameField;
//    private ShapeRenderer shapeRenderer;
//    private SpriteBatch spriteBatch;
    private BitmapFont bitmapFont;

//    private Skin skin;
    public Stage stage;
    public Table table;
    public Label gamerGoldLabel, missedAndLimit, fpsLabel;

    // Console need
    private Label actionsHistoryLabel;
    public Array<String> arrayActionsHistory;
    private float deleteActionThrough, actionInHistoryTime;

    public TowersRoulette towersRoulette;
    public CreepsRoulette creepsRoulette;

    private Texture winTexture, loseTexture;
    private float currentTextureTime, maxTextureTime;

    public GameInterface(GameField gameField, BitmapFont bitmapFont) {
        Gdx.app.log("GameInterface::GameInterface(" + gameField + "," + bitmapFont + ")", "-- Called!");
        this.gameField = gameField;
//        this.shapeRenderer = shapeRenderer;
//        this.spriteBatch = spriteBatch;
        this.bitmapFont = bitmapFont;

//        this.skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        this.stage = new Stage(/*new ScreenViewport()*/);
        stage.setDebugAll(true);
        Gdx.app.log("GameInterface::GameInterface()", "-- stage.getWidth():" + stage.getWidth() + " stage.getHeight():" + stage.getHeight());
        this.table = new Table();
        stage.addActor(table);
        Gdx.app.log("GameInterface::GameInterface()", "-- table:" + table);
        Gdx.app.log("GameInterface::GameInterface()", "-- table.getWidth():" + table.getWidth() + " table.getHeight():" + table.getHeight());
        table.setFillParent(true);
        Gdx.app.log("GameInterface::GameInterface()", "-- table.getWidth():" + table.getWidth() + " table.getHeight():" + table.getHeight());
        table.setBounds(1, 0, table.getWidth()-2, table.getHeight()-2);
        Gdx.app.log("GameInterface::GameInterface()", "-- table.getWidth():" + table.getWidth() + " table.getHeight():" + table.getHeight());

        Table infoTable = new Table();
//        infoTable.setPosition(50f, 50f);
//        infoTable.setBounds(10, 10, 100, 50);
        infoTable.setFillParent(true);

//        Table table1 = new Table();
//        table1.setFillParent(true);
//        infoTable.add(table1);

//        gamerGoldLabel = new Label("gamerGold:000", new Label.LabelStyle(bitmapFont, Color.YELLOW));
////        gamerGoldLabel.setPosition(Gdx.graphics.getWidth()*0.60f, 15.0f);
////        gamerGoldLabel.setFontScale(2f);
//        infoTable.add(gamerGoldLabel).row();
//        infoTable.row();
//        missedAndLimit = new Label("10/100", new Label.LabelStyle(bitmapFont, Color.PINK));
////        missedAndLimit.setPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()-20f);
////        missedAndLimit.setFontScale(2f);
//        infoTable.add(missedAndLimit).row();
//        infoTable.row();
//        fpsLabel = new Label("000", new Label.LabelStyle(bitmapFont, Color.WHITE));
////        fpsLabel.setPosition(0.0f, Gdx.graphics.getHeight() - 18.0f);
//        infoTable.add(fpsLabel);
////        infoTable.align(Align.center);
////        infoTable.center();
        table.add(infoTable);

//        actionsHistoryLabel = new Label("actionsHistory1\nactionsHistory2\nactionsHistory3", new Label.LabelStyle(bitmapFont, Color.WHITE));
//        actionsHistoryLabel.setPosition(0.0f, Gdx.graphics.getHeight()/2);
//        stage.addActor(actionsHistoryLabel);
//        arrayActionsHistory = new Array<String>();
//        deleteActionThrough = 0f;
//        actionInHistoryTime = 1f;

//        towersRoulette = new TowersRoulette(gameField, bitmapFont, stage);
//        creepsRoulette = new CreepsRoulette(gameField, bitmapFont, stage);


        winTexture = new Texture(Gdx.files.internal("img/victory.jpg"));
        loseTexture = new Texture(Gdx.files.internal("img/defeat.jpg"));
        currentTextureTime = 0f;
        maxTextureTime = 1f;
    }

    public void addActionToHistory(String action) {
        arrayActionsHistory.add(action);
    }

    public void render(float delta) {
//        if(arrayActionsHistory.size > 0) {
//            deleteActionThrough += delta;
//            if (deleteActionThrough > actionInHistoryTime) {
//                arrayActionsHistory.removeIndex(0);
//                deleteActionThrough = 0f;
//            }
//            StringBuilder sb = new StringBuilder();
//            for(String str : arrayActionsHistory) {
//                sb.append("\n" + str);
//            }
//            actionsHistoryLabel.setText(sb.toString());
//        }
//        gamerGoldLabel.setText("gamerGold:" + gameField.getGamerGold());
//        missedAndLimit.setText(gameField.missedCreeps + "/" + gameField.maxOfMissedCreeps);
//        fpsLabel.setText(String.valueOf(Gdx.graphics.getFramesPerSecond()));
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

    public boolean pan(float x, float y, float deltaX, float deltaY) {
        if(creepsRoulette != null) {
            if(creepsRoulette.pan(x, y, deltaX, deltaY)) {
                return true;
            }
        }
        if(towersRoulette != null) {
            if (towersRoulette.makeRotation(x, y, deltaX, deltaY) && Gdx.app.getType() == Application.ApplicationType.Android) {
                return true;
            }
        }
        return false;
    }
}
