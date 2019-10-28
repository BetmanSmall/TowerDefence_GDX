package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.betmansmall.util.logging.Logger;

public abstract class AbstractCameraController implements GestureDetector.GestureListener, InputProcessor, Disposable {
    @Override
    public boolean keyDown(int keycode) {
        Logger.logFuncStart("keycode:" + keycode);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        Logger.logFuncStart("keycode:" + keycode);
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        Logger.logFuncStart("character:" + character);
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Logger.logFuncStart("screenX:" + screenX + " screenY:" + screenY + " pointer:" + pointer + " button:" + button);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Logger.logFuncStart("screenX:" + screenX + " screenY:" + screenY + " pointer:" + pointer + " button:" + button);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Logger.logFuncStart("screenX:" + screenX + " screenY:" + screenY + " pointer:" + pointer);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Logger.logFuncStart("screenX:" + screenX + " screenY:" + screenY);
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        Logger.logFuncStart("-- amount:" + amount);
        return false;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        Logger.logFuncStart("x:" + x + " y:" + y + " pointer:" + pointer + " button:" + button);
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        Logger.logFuncStart("x:" + x + " y:" + y + " count:" + count + " button:" + button);
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        Logger.logFuncStart("x:" + x + " y:" + y);
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        Logger.logFuncStart("velocityX:" + velocityX + " velocityY:" + velocityY + " button:" + button);
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        Logger.logFuncStart("x:" + x + " y:" + y + " deltaX:" + deltaX + " deltaY:" + deltaY);
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        Logger.logFuncStart("x:" + x + " y:" + y + " pointer:" + pointer + " button:" + button);
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        Logger.logFuncStart("initialDistance:" + initialDistance + " distance:" + distance);
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        Logger.logFuncStart("initialPointer1:" + initialPointer1 + " initialPointer2:" + initialPointer2 + " pointer1:" + pointer1 + " pointer2:" + pointer2);
        return false;
    }

    @Override
    public void pinchStop() {
        Logger.logFuncStart();
    }
}
