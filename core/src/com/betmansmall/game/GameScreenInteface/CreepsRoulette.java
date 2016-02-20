package com.betmansmall.game.GameScreenInteface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created by Дима Цыкунов on 20.02.2016.
 */
public class CreepsRoulette extends Roulette {

    private Group group;
    private ImageButton rouletteButton;
    private ImageButton rouletteCircle;


    public CreepsRoulette() {
        init();
    }

    private void init() {
        group = new Group();

        rouletteButton = new ImageButton(new Image(new Texture(Gdx.files.internal("img/creep_roulette_main.png"))).getDrawable());
        rouletteButton.setSize(getLocalWidth(ROULETTE_WIDTH), getLocalHeight(ROULETTE_HEIGHT));
        rouletteButton.setPosition(0, 0);
        rouletteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("TAG", "setVisible = " + !rouletteCircle.isVisible());
                rouletteCircle.setVisible(!rouletteCircle.isVisible());
            }
        });

        rouletteCircle = new ImageButton(new Image(new Texture(Gdx.files.internal("img/golden_ring.png"))).getDrawable());
        rouletteCircle.setSize(getLocalWidth(RING_WIDTH), getLocalHeight(RING_HEIGHT));
        rouletteCircle.setPosition(0 - rouletteCircle.getWidth() / 2, 0 - rouletteCircle.getHeight() / 2);
        rouletteCircle.setVisible(false);

        group.addActor(rouletteButton);
        group.addActor(rouletteCircle);
    }

    @Override
    public Group getGroup() {
        return group;
    }

}
