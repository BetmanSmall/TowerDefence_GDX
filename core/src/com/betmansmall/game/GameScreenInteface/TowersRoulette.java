package com.betmansmall.game.GameScreenInteface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.betmansmall.game.gameLogic.GameField;

/**
 * Created by Дима Цыкунов on 20.02.2016.
 */
public class TowersRoulette extends Roulette {
    private Group group;
    private ImageButton rouletteButton;
    private ImageButton rouletteCircle;
    private static volatile Boolean IS_HIDE_TOWERS = false;
    private GameField gameField;

    public TowersRoulette(GameField gameField) {
        this.gameField = gameField;
        init();
    }

    private void init() {
        group = new Group();

        rouletteButton = new ImageButton(new Image(new Texture(Gdx.files.internal("img/tower_roulette_main.png"))).getDrawable());
        rouletteButton.setSize(getLocalWidth(ROULETTE_WIDTH), getLocalHeight(ROULETTE_HEIGHT));
        rouletteButton.setPosition(Gdx.graphics.getWidth() - rouletteButton.getWidth(), 0);

        rouletteCircle = new ImageButton(new Image(new Texture(Gdx.files.internal("img/golden_ring.png"))).getDrawable());
        rouletteCircle.setSize(getLocalWidth(RING_WIDTH), getLocalHeight(RING_HEIGHT));
        rouletteCircle.setPosition(Gdx.graphics.getWidth() - rouletteCircle.getWidth() / 2, 0 - rouletteCircle.getHeight() / 2);
        rouletteCircle.setVisible(false);

        group.addActor(rouletteCircle);
        group.addActor(rouletteButton);
    }

    @Override
    public Group getGroup() {
        return group;
    }
}
