package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.math.Vector3;
import com.betmansmall.enums.GameType;
import com.betmansmall.screens.client.GameScreen;

public class GameScreenCameraController extends CameraController {
    public GameScreenCameraController(GameScreen gameScreen) {
        super(gameScreen);
        this.gameScreen = gameScreen;
        this.gameField = gameScreen.gameField;
        this.gameInterface = gameScreen.gameInterface;
    }

    @Override
    public boolean longPress(float x, float y) {
        super.longPress(x, y);
//        if (!gameInterface.interfaceTouched) {
        Vector3 touch = new Vector3(x, y, 0.0f);
        whichCell(touch, isDrawableTowers);
        if (random.nextBoolean()) {
            gameScreen.createTower((int) touch.x, (int) touch.y);
        } else {
            if (random.nextInt(5) == 0 && gameField.gameSettings.gameType == GameType.LittleGame) {
                gameField.spawnLocalHero((int) touch.x, (int) touch.y);
            } else {
                gameField.spawnServerUnitToRandomExit((int) touch.x, (int) touch.y);
            }
        }
//        }
        return false;
    }
}
