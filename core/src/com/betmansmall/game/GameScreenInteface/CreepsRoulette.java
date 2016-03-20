package com.betmansmall.game.GameScreenInteface;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.betmansmall.game.GameScreen;
import com.betmansmall.game.gameLogic.Creep;
import com.betmansmall.game.gameLogic.GameField;

/**
 * Created by Дима Цыкунов on 20.02.2016.
 */
public class CreepsRoulette extends Roulette {

    private CreepsRoulette cr;
    private Group group;
    private static int buttonSizeX = 200, buttonSizeY = 200;
    private ImageButton playButton;
    private ImageButton pauseButton;
    private static volatile Boolean IS_PAUSE = true;
    private static GameScreen gs;
    private GameField gameField;

    public CreepsRoulette(GameField gameField) {
        this.gameField = gameField;
        init();
    }

    private void init() {
        group = new Group();

        playButton = new ImageButton(new Image(new Texture(Gdx.files.internal("img/playbutton.png"))).getDrawable());
        playButton.setSize(buttonSizeX,buttonSizeY);
        playButton.setPosition(0, 0);

        pauseButton = new ImageButton(new Image(new Texture(Gdx.files.internal("img/pausebutton.png"))).getDrawable());
        pauseButton.setSize(buttonSizeX,buttonSizeY);
        pauseButton.setPosition(0, 0);
        pauseButton.setVisible(true);

        group.addActor(pauseButton);
        group.addActor(playButton);
    }

    public void buttonClick() {
        if(IS_PAUSE) {
            IS_PAUSE = !IS_PAUSE;
            pauseButton.setZIndex(1);
            playButton.setZIndex(0);
        } else {
            IS_PAUSE = !IS_PAUSE;
            pauseButton.setZIndex(0);
            playButton.setZIndex(1);
        }
        gameField.setGamePause(IS_PAUSE);
    }

    public float getButtonSizeX(){
        return buttonSizeX;
    }
    public float getButtonSizeY(){
        return buttonSizeY;
    }

    public boolean isButtonTouched(float x, float y) {
        boolean isTouched = false;
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            if(x <= getButtonSizeX() && y <= getButtonSizeY()){
                isTouched = true;
            }
        } else if( Gdx.app.getType() == Application.ApplicationType.Desktop) {
            if(x <= getButtonSizeX()&& y > Gdx.graphics.getHeight() - getButtonSizeY()){
                isTouched = true;
            }
        }
        if(isTouched) buttonClick();
        return isTouched;
    }

    @Override
    public Group getGroup() {
        return group;
    }

}
