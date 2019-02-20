package com.betmansmall.game.GameScreenInteface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
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

    private Skin skin;
    public Stage stage;
    public Table tableBack, tableFront;

    // Console need
    public Array<String> arrayActionsHistory;
    private float deleteActionThrough, actionInHistoryTime;
    private Label actionsHistoryLabel;

    public TextButton startAndPauseButton;
    public Label mapNameLabel, fpsLabel, gamerCursorCoordCell, underConstructionLabel,
            missedAndMaxForPlayer1, gamerGoldLabel, missedAndMaxForComputer0, nextUnitSpawnLabel,
            unitsManagerSize, towersManagerSize, unitsSpawn, gamePaused;

    public boolean interfaceTouched;
    public TowersSelector towersSelector;
    public UnitsSelector unitsSelector;

    private Texture winTexture, loseTexture;
    private float currentTextureTime, maxTextureTime;

    public GameInterface(final GameField gameField, BitmapFont bitmapFont) {
        Gdx.app.log("GameInterface::GameInterface(" + gameField + "," + bitmapFont + ")", "-- Called!");
        this.gameField = gameField;
//        this.shapeRenderer = shapeRenderer;
//        this.spriteBatch = spriteBatch;
        this.bitmapFont = bitmapFont;

        this.skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        this.stage = new Stage(new ScreenViewport());
//        stage.getViewport().update(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2, true);
//        stage = new Stage(new ExtendViewport(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
//        stage.setDebugAll(true);

        this.tableBack = new Table(skin);
//        tableBack.setDebug(true);
        stage.addActor(tableBack);
        tableBack.setFillParent(true);

        arrayActionsHistory = new Array<String>();
        deleteActionThrough = 0f;
        actionInHistoryTime = 1f;
//        actionsHistoryLabel = new Label("actionsHistory1\nactionsHistory2\nactionsHistory3", new Label.LabelStyle(bitmapFont, Color.WHITE));
        actionsHistoryLabel = new Label("", new Label.LabelStyle(bitmapFont, Color.WHITE));
        tableBack.add(actionsHistoryLabel).expand().left();

        startAndPauseButton = new TextButton((!gameField.gamePaused)? "PAUSE" : "PLAY", skin, "default");
//        startAndPauseButton.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                Gdx.app.log("GameInterface::ClickListener::clicked(" + event + "," + x + "," + y + ")", "-- startAndPauseButton");
//                gameField.gamePaused = !gameField.gamePaused;
//                startAndPauseButton.setText((!gameField.gamePaused)? "PAUSE" : "PLAY");
//                interfaceTouched = true;
//            }
//        });
        tableBack.add(startAndPauseButton).size(startAndPauseButton.getWidth()*3f, startAndPauseButton.getHeight()*1.5f).bottom();

        VerticalGroup tablo = new VerticalGroup();
//        tablo.space(1f);
        tablo.align(Align.left);
        tableBack.add(tablo).top().expand();

        mapNameLabel = new Label("MapName:arena0tmx", new Label.LabelStyle(bitmapFont, Color.WHITE));
        tablo.addActor(mapNameLabel);
        fpsLabel = new Label("FPS:000", new Label.LabelStyle(bitmapFont, Color.WHITE));
        tablo.addActor(fpsLabel);
        gamerCursorCoordCell = new Label("CoordCell:(0,0)", new Label.LabelStyle(bitmapFont, Color.WHITE));
        tablo.addActor(gamerCursorCoordCell);
        underConstructionLabel = new Label("UnderConstrTemplateName:tower1", new Label.LabelStyle(bitmapFont, Color.WHITE));
        tablo.addActor(underConstructionLabel);
        missedAndMaxForPlayer1 = new Label("UnitsLimitPL1:10/100", new Label.LabelStyle(bitmapFont, Color.GREEN));
        tablo.addActor(missedAndMaxForPlayer1);
        gamerGoldLabel = new Label("GamerGold:000", new Label.LabelStyle(bitmapFont, Color.YELLOW));
        tablo.addActor(gamerGoldLabel);
        missedAndMaxForComputer0 = new Label("UnitsLimitComp0:10/100", new Label.LabelStyle(bitmapFont, Color.RED));
        tablo.addActor(missedAndMaxForComputer0);
        nextUnitSpawnLabel = new Label("NextUnitSpawnAfter:0.12sec", new Label.LabelStyle(bitmapFont, Color.ORANGE));
        tablo.addActor(nextUnitSpawnLabel);
        unitsManagerSize = new Label("unitsManagerSize:", new Label.LabelStyle(bitmapFont, Color.GREEN));
        tablo.addActor(unitsManagerSize);
        towersManagerSize = new Label("towersManagerSize:", new Label.LabelStyle(bitmapFont, Color.YELLOW));
        tablo.addActor(towersManagerSize);
        unitsSpawn = new Label("unitsSpawn:", new Label.LabelStyle(bitmapFont, Color.RED));
        tablo.addActor(unitsSpawn);
        gamePaused = new Label("gamePaused:", new Label.LabelStyle(bitmapFont, Color.GREEN));
        tablo.addActor(gamePaused);

        this.tableFront = new Table(skin); // WTF??? почему нельзя селекторы на одну таблицу со всем остальным??
////        table.setDebug(true);
        stage.addActor(tableFront);
        tableFront.setFillParent(true);
        interfaceTouched = false;
        if (gameField.waveManager.wavesForUser.size > 0) {
            unitsSelector = new UnitsSelector(gameField, bitmapFont, tableFront);
        }

        towersSelector = new TowersSelector(gameField, bitmapFont, tableFront);

        winTexture = new Texture(Gdx.files.internal("concepts/littlegame-concept-2-1.jpg"));
        loseTexture = new Texture(Gdx.files.internal("concepts/2018-12-03_19-43-03.png"));
        currentTextureTime = 0f;
        maxTextureTime = 5f;
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
            underConstructionLabel.setText("UnderConstrTemplateName:" + underConstruction.templateForTower.name);
            underConstructionLabel.setColor(Color.GREEN);
        } else {
            gamerCursorCoordCell.setText("CoordCell:(WTF,WTF)");
            underConstructionLabel.setText("UnderConstrTemplateName:NULL");
            underConstructionLabel.setColor(Color.RED);
        }
        missedAndMaxForPlayer1.setText("UnitsLimitPL1:" + gameField.gameSettings.missedUnitsForPlayer1 + "/" + gameField.gameSettings.maxOfMissedUnitsForPlayer1);
        gamerGoldLabel.setText("GamerGold:" + gameField.gamerGold);
        missedAndMaxForComputer0.setText("UnitsLimitComp0:" + gameField.gameSettings.missedUnitsForComputer0 + "/" + gameField.gameSettings.maxOfMissedUnitsForComputer0);
        nextUnitSpawnLabel.setText("NextUnitSpawnAfter:" + ((gameField.waveManager.waitForNextSpawnUnit > 0f) ? String.format("%.2f", gameField.waveManager.waitForNextSpawnUnit) + "sec" : "PRESS_PLAY_BUTTON"));
        missedAndMaxForComputer0.setText("UnitsLimitComp0:" + gameField.gameSettings.missedUnitsForComputer0 + "/" + gameField.gameSettings.maxOfMissedUnitsForComputer0);
        missedAndMaxForComputer0.setText("UnitsLimitComp0:" + gameField.gameSettings.missedUnitsForComputer0 + "/" + gameField.gameSettings.maxOfMissedUnitsForComputer0);
        unitsManagerSize.setText("unitsManagerSize:" + gameField.unitsManager.units.size);
        towersManagerSize.setText("towersManagerSize:" + gameField.towersManager.towers.size);
        unitsSpawn.setText("unitsSpawn:" + gameField.unitsSpawn);
        gamePaused.setText("gamePaused:" + gameField.gamePaused);
        startAndPauseButton.setText( (gameField.gamePaused) ? "PLAY" : (gameField.unitsSpawn) ? "PAUSE" : (GameField.unitsManager.units.size>0) ? "PAUSE" : "START NEXT WAVE");
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
            batch.draw(winTexture, 0, 0, stage.getWidth(), stage.getHeight());
        } else if(gameState.equals("Lose")) {
            batch.draw(loseTexture, 0, 0, stage.getWidth(), stage.getHeight());
        }
        batch.end();
    }

    public void dispose() {
        Gdx.app.log("GameInterface::dispose()", "-- Called!");
        bitmapFont.dispose();
        stage.dispose();
//        towersSelector.dispose();
//        unitsSelector.dispose();
        winTexture.dispose();
        loseTexture.dispose();
    }

    public boolean touchDown(float screenX, float screenY, int pointer, int button) {
        Gdx.app.log("GameInterface::touchDown()", "-- prevMouseX:" + screenX + " prevMouseY:" + screenY + " pointer:" + pointer + " button:" + button);
        if (startAndPauseButton.isPressed()) {
            Gdx.app.log("GameInterface::touchDown()", "-- startAndPauseButton.isPressed()");
            gameField.gamePaused = !gameField.gamePaused;
            if (!gameField.unitsSpawn && GameField.unitsManager.units.size == 0) {
                gameField.unitsSpawn = true;
                gameField.gamePaused = false;
            }
//            startAndPauseButton.setText( (!gameField.gamePaused) ? "PAUSE" : "PLAY");
            interfaceTouched = true;
        }
        if(unitsSelector != null) {
            if(unitsSelector.touchDown(screenX, screenY, pointer, button)) {
                interfaceTouched = true;
                Gdx.app.log("GameInterface::touchDown()", "-- return true");
                return true;
            }
        }
        if(towersSelector != null) {
            if (towersSelector.touchDown(screenX, screenY, pointer, button)) {
                interfaceTouched = true;
                return true;
            }
        }
        Gdx.app.log("GameInterface::touchDown()", "-- return false");
        return false;
    }

    public boolean pan(float x, float y, float deltaX, float deltaY) {
//        Gdx.app.log("GameInterface::pan()", "-- x:" + x + " y:" + y + " deltaX:" + deltaX + " deltaY:" + deltaY);
        if(unitsSelector != null) {
            if(unitsSelector.pan(x, y, deltaX, deltaY)) {
//                interfaceTouched = true;
                return true;
            }
        }
        if(towersSelector != null) {
            if (towersSelector.pan(x, y, deltaX, deltaY)) {
//                interfaceTouched = true;
                return true;
            }
        }
        return false;
    }

    public boolean panStop(float x, float y, int pointer, int button) {
        Gdx.app.log("GameInterface::panStop()", "-- x:" + x + " y:" + y + " pointer:" + pointer + " button:" + button);
        if(unitsSelector != null) {
            if (unitsSelector.panStop(x, y, pointer, button)) {
//                return true;
            }
        }
        if(towersSelector != null) {
            if(towersSelector.panStop(x, y, pointer, button)) {
//                return true;
            }
        }
        return false;
    }

    public boolean scrolled(int amount) {
        Gdx.app.log("GameInterface::scrolled()", "-- amount:" + amount);
        if(unitsSelector != null) {
            if (unitsSelector.scrolled(amount)) {
                return true;
            }
        }
        if(towersSelector != null) {
            if(towersSelector.scrolled(amount)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("GameInterface[");
        sb.append("tableBack:" + tableBack);
        sb.append("]");
        return sb.toString();
    }
}
