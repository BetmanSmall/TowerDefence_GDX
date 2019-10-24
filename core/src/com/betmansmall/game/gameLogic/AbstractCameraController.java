package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.betmansmall.util.logging.Logger;

public abstract class AbstractCameraController implements GestureDetector.GestureListener, InputProcessor {
    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        Logger.logFuncStart("-- x:" + x + " y:" + y + " pointer:" + pointer + " button:" + button);
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        Logger.logFuncStart("-- x:" + x + " y:" + y + " count:" + count + " button:" + button);
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY/*, int buttons*/) {
        Logger.logFuncStart("-- x:" + x + " y:" + y + " deltaX:" + deltaX + " deltaY:" + deltaY);
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        Logger.logFuncStart("-- x:" + x + " y:" + y + " pointer:" + pointer + " button:" + button);
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        Logger.logFuncStart("-- initialPointer1:" + initialPointer1 + " initialPointer2:" + initialPointer2 + " pointer1:" + pointer1 + " pointer2:" + pointer2);
        return false;
    }

    @Override
    public void pinchStop() {
        Logger.logFuncStart("--");
    }

    @Override
    public boolean keyDown(int keycode) {
        Logger.logFuncStart("-- keycode:" + keycode);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        Logger.logFuncStart("-- keycode:" + keycode);
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        Logger.logFuncStart("-- character:" + character);
        return false;
    }
}
