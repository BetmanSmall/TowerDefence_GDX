package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Created by Transet on 07.02.2016.
 * This class provides elements which placed on game screen.
 * TODO implement more interface options
 */
public class GameInterface {

    public enum GameInterfaceElements {
        RETURN_BUTTON,
        START_WAVE_BUTTON
    }

    private Image returnButton;
    private Image startWaveButton;
    private Batch spriteBatch;

    private boolean isReturnButtonTouched = false;
    private boolean isStartWaveButtonTouched = false;

    public GameInterface() {
        init();
    }

    private void init() {
        spriteBatch = new SpriteBatch();

        returnButton = new Image(new Texture(Gdx.files.internal("img/return.png")));
        returnButton.setSize(55, 55);
        returnButton.setPosition(0, Gdx.graphics.getHeight() - returnButton.getHeight());

        startWaveButton = new Image(new Texture(Gdx.files.internal("img/startgamebutton.PNG")));
        startWaveButton.setSize(140, 42);
        startWaveButton.setPosition((Gdx.graphics.getWidth() / 2) - (startWaveButton.getWidth() / 2), Gdx.graphics.getHeight() - startWaveButton.getHeight());
    }

    private float getNormalCordX(float x) {
        return x;
    }

    private float getNormalCordY(float y) {
        return (float) Gdx.graphics.getHeight() - y;
    }

    public void setVisible(boolean visible, GameInterfaceElements element) {
        getElement(element).setVisible(visible);
    }

    private Actor getElement(GameInterfaceElements element) {
        switch (element) {
            case RETURN_BUTTON:
                return returnButton;
            case START_WAVE_BUTTON:
                return startWaveButton;
        }
        return null;
    }

    public boolean isTouched(GameInterfaceElements element) {
        switch (element) {
            case RETURN_BUTTON:
                if(isReturnButtonTouched) {
                    isReturnButtonTouched = false;
                    return true;
                }
                break;
            case START_WAVE_BUTTON:
                if(isStartWaveButtonTouched) {
                    isStartWaveButtonTouched = false;
                    return true;
                }
                break;
        }
        return false;
    }

    public boolean update(float x, float y)
    {
        if (isActorTouched(returnButton,x,y)) {
            isReturnButtonTouched = true;
            return true;
        }

        if (isActorTouched(startWaveButton,x,y)) {
            isStartWaveButtonTouched = true;
            return true;
        }
        return false;
    }

    private boolean isActorTouched(Actor actor,float x,float y) {
        if (actor.getX() < getNormalCordX(x) && getNormalCordX(x) < actor.getX() + actor.getWidth() &&
                actor.getY() < getNormalCordY(y) && getNormalCordY(y) < actor.getY() + actor.getHeight()) {
            return true;
        }
        return false;
    }

    public void draw() {
        spriteBatch.begin();
        if(returnButton.isVisible()) {
            returnButton.draw(spriteBatch, 1);
        }
        if(startWaveButton.isVisible()) {
            startWaveButton.draw(spriteBatch, 1);
        }
        spriteBatch.end();
    }

}
