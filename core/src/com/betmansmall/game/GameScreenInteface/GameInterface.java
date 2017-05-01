package com.betmansmall.game.GameScreenInteface;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.TowerDefence;
import com.betmansmall.game.gameLogic.GameField;
import com.betmansmall.game.gameLogic.UnderConstruction;

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
    public Label fpsLabel, gamerCursorCoordCell, missedAndMaxForPlayer1, gamerGoldLabel, missedAndMaxForComputer0, nextCreepSpawnLabel;

    // Console need
    public Array<String> arrayActionsHistory;
    private float deleteActionThrough, actionInHistoryTime;
    private Label actionsHistoryLabel;

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

        this.table = new Table();
        stage.addActor(table);
        table.setFillParent(true);
//        table.setBounds(1, 0, stage.getWidth()-1, stage.getHeight()-1);
        Gdx.app.log("GameInterface::GameInterface()", "-- table.getWidth():" + table.getWidth() + " table.getHeight():" + table.getHeight());

        arrayActionsHistory = new Array<String>();
        deleteActionThrough = 0f;
        actionInHistoryTime = 1f;
        actionsHistoryLabel = new Label("actionsHistory1\nactionsHistory2\nactionsHistory3", new Label.LabelStyle(bitmapFont, Color.WHITE));
        table.add(actionsHistoryLabel).expand().left();

//        Table infoTable = new Table();
//        infoTable.setSize(100f, 50f);
//        infoTable.align(Align.top);
//        table.addActor(infoTable);
//        infoTable.setPosition(50f, 50f);
//        infoTable.setBounds(10, 10, 100, 50);
//        infoTable.setFillParent(true);

//        Table table1 = new Table();
//        table1.setFillParent(true);
//        infoTable.add(table1);

        VerticalGroup infoGroup = new VerticalGroup();
        infoGroup.left();
        table.add(infoGroup).expand().top().right();

        fpsLabel = new Label("FPS:000", new Label.LabelStyle(bitmapFont, Color.WHITE));
        gamerCursorCoordCell = new Label("CoordCell:(0,0)", new Label.LabelStyle(bitmapFont, Color.WHITE));
        missedAndMaxForPlayer1 = new Label("CreepsLimitPL1:10/100", new Label.LabelStyle(bitmapFont, Color.GREEN));
        gamerGoldLabel = new Label("GamerGold:000", new Label.LabelStyle(bitmapFont, Color.YELLOW));
        missedAndMaxForComputer0 = new Label("CreepsLimitComp0:10/100", new Label.LabelStyle(bitmapFont, Color.RED));
        nextCreepSpawnLabel = new Label("NextCreepSpawnAfter:0.12sec", new Label.LabelStyle(bitmapFont, Color.ORANGE));
        infoGroup.addActor(fpsLabel);
        infoGroup.addActor(gamerCursorCoordCell);
        infoGroup.addActor(missedAndMaxForPlayer1);
        infoGroup.addActor(gamerGoldLabel);
        infoGroup.addActor(missedAndMaxForComputer0);
        infoGroup.addActor(nextCreepSpawnLabel);

        towersRoulette = new TowersRoulette(gameField, bitmapFont, stage);
        creepsRoulette = new CreepsRoulette(gameField, bitmapFont, stage);

        winTexture = new Texture(Gdx.files.internal("img/victory.jpg"));
        loseTexture = new Texture(Gdx.files.internal("img/defeat.jpg"));
        currentTextureTime = 0f;
        maxTextureTime = 1f;
    }

    public void addActionToHistory(String action) {
        if(arrayActionsHistory != null) {
            arrayActionsHistory.add(action);
        }
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
        fpsLabel.setText("FPS:" + String.valueOf(Gdx.graphics.getFramesPerSecond()));
        UnderConstruction underConstruction = gameField.getUnderConstruction();
        if(underConstruction != null) {
            gamerCursorCoordCell.setText("CoordCell:(" + underConstruction.endX + "," + underConstruction.endY + ")");
        } else {
            gamerCursorCoordCell.setText("CoordCell:(WTF,WTF)");
        }
        missedAndMaxForPlayer1.setText("CreepsLimitPL1:" + gameField.missedCreepsForPlayer1 + "/" + gameField.maxOfMissedCreepsForPlayer1);
        gamerGoldLabel.setText("GamerGold:" + gameField.getGamerGold());
        missedAndMaxForComputer0.setText("CreepsLimitComp0:" + gameField.missedCreepsForComputer0 + "/" + gameField.maxOfMissedCreepsForComputer0);
        nextCreepSpawnLabel.setText("NextCreepSpawnAfter:" + ((gameField.waveManager.waitForNextSpawnCreep > 0f) ? String.format("%.2f", gameField.waveManager.waitForNextSpawnCreep) + "sec" : "PRESS_PLAY_BUTTON"));
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
