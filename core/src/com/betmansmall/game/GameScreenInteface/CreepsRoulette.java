package com.betmansmall.game.GameScreenInteface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.betmansmall.game.gameLogic.GameField;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Дима Цыкунов on 20.02.2016.
 */
public class CreepsRoulette extends Roulette {
    private Group group;
    private ImageButton playButton;
    private ImageButton pauseButton;
    private static volatile Boolean IS_PAUSE = true;
    private GameField gameField;

    public CreepsRoulette(GameField gameField) {
        this.gameField = gameField;
        init();
    }

    private void init() {
        group = new Group();

        playButton = new ImageButton(new Image(new Texture(Gdx.files.internal("img/playbutton.png"))).getDrawable());
        playButton.setSize(getLocalWidth(ROULETTE_RADIUS),getLocalHeight(ROULETTE_RADIUS));
        playButton.setPosition(0, 0);

        pauseButton = new ImageButton(new Image(new Texture(Gdx.files.internal("img/pausebutton.png"))).getDrawable());
        pauseButton.setSize(getLocalWidth(ROULETTE_RADIUS),getLocalHeight(ROULETTE_RADIUS));
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

    public boolean isButtonTouched(float x, float y) {
        boolean isTouched = false;
        if(x <= getLocalWidth(ROULETTE_RADIUS)&& y > Gdx.graphics.getHeight() - getLocalHeight(ROULETTE_RADIUS)){
            isTouched = true;
        }
        if(isTouched) buttonClick();
        return isTouched;
    }

    @Override
    public List<Group> getGroup() {
        return Arrays.asList(group);
    }
}
