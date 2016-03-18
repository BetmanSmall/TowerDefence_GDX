package com.betmansmall.game.GameScreenInteface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.RotateToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Pool;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.rotateBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by Дима Цыкунов on 20.02.2016.
 */
public class TowersRoulette extends Roulette {

    private Group group;
    private ImageButton rouletteButton;
    private ImageButton rouletteCircle;
    private static volatile Boolean IS_HIDE_TOWERS = false;


    public TowersRoulette() {
        init();
    }



    private void init() {
        group = new Group();

        rouletteButton = new ImageButton(new Image(new Texture(Gdx.files.internal("img/tower_roulette_main.png"))).getDrawable());
        rouletteButton.setSize(getLocalWidth(ROULETTE_WIDTH), getLocalHeight(ROULETTE_HEIGHT));
        rouletteButton.setPosition(Gdx.graphics.getWidth() - rouletteButton.getWidth(), 0);
//        rouletteButton.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                Gdx.app.log("TAG", "setVisible = " + !rouletteCircle.isVisible());
//                IS_HIDE_TOWERS = !IS_HIDE_TOWERS;
//                rouletteCircle.setVisible(IS_HIDE_TOWERS);
//
//            }
//        });


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
