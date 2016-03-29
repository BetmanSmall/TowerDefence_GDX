package com.betmansmall.game.GameScreenInteface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.RotateToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.betmansmall.game.gameLogic.GameField;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by Дима Цыкунов on 20.02.2016.
 */
public class TowersRoulette extends Roulette {
    private Group group;
    private ImageButton rouletteButton;
    private ImageButton rouletteCircle;
    private static volatile Boolean IS_HIDE_TOWERS = true;
    private GameField gameField;
    private RotateToAction rotateToAction;

    public TowersRoulette(GameField gameField) {
        this.gameField = gameField;
        init();
    }

    private void init() {
        group = new Group();

        rouletteButton = new ImageButton(new Image(new Texture(Gdx.files.internal("img/tower_roulette_main.png"))).getDrawable());
        rouletteButton.setName("rouletteButton");
        rouletteButton.setSize(getLocalWidth(ROULETTE_RADIUS), getLocalHeight(ROULETTE_RADIUS));
        rouletteButton.setPosition(Gdx.graphics.getWidth() - rouletteButton.getWidth(), 0);

        rouletteCircle = new ImageButton(new Image(new Texture(Gdx.files.internal("img/golden_ring.png"))).getDrawable());
        rouletteCircle.setSize(getLocalWidth(RING_RADIUS) * 2, getLocalHeight(RING_RADIUS) * 2);
        rouletteCircle.setPosition(Gdx.graphics.getWidth() - rouletteCircle.getWidth() / 2, 0 - rouletteCircle.getHeight() / 2);
        rouletteCircle.setVisible(false);

        rouletteButton.addAction(rotateBy(90f, 5f));

        group.addActor(rouletteCircle);
        group.addActor(rouletteButton);
        group.setOrigin(Gdx.graphics.getWidth(), 0);
        group.addAction(rotateBy(90f, 5f));
    }

    private void buttonClick() {
        IS_HIDE_TOWERS = !IS_HIDE_TOWERS;
        rouletteCircle.setVisible(!IS_HIDE_TOWERS);
        rouletteButton.setPosition(Gdx.graphics.getWidth() - rouletteButton.getWidth(), 0);
    }

    private void ringClick(){
        Gdx.app.log("TAG", "Tower is selected");
        rouletteButton.setSize(getLocalWidth(ROULETTE_RADIUS), getLocalHeight(ROULETTE_RADIUS));
        rouletteButton.setPosition(Gdx.graphics.getWidth() - rouletteButton.getWidth(), 0);
    }

    public boolean isButtonTouched(float x, float y) {
        boolean isTouched = false;
        x = Gdx.graphics.getWidth()  - x;
        y = Gdx.graphics.getHeight() - y;

        //RING PRESS DETECTION
        if((x*x + y*y) <= (getLocalWidth(RING_RADIUS) * getLocalWidth(RING_RADIUS))
                && x <= getLocalWidth(RING_RADIUS) && y <= getLocalWidth(RING_RADIUS) && !IS_HIDE_TOWERS) {
            if (!((x*x + y*y) <= getLocalWidth(ROULETTE_RADIUS) * getLocalWidth(ROULETTE_RADIUS)
                    && x <= getLocalWidth(ROULETTE_RADIUS) && y <= getLocalWidth(ROULETTE_RADIUS))) {
                isTouched = true;
                Gdx.app.log("TAG", "RING");
                if(isTouched) ringClick();
                return isTouched;
            }
        }

        //BUTTON PRESS DETECTION
        if ((x*x + y*y) <= getLocalWidth(ROULETTE_RADIUS) * getLocalWidth(ROULETTE_RADIUS)
                && x <= getLocalWidth(ROULETTE_RADIUS) && y <= getLocalWidth(ROULETTE_RADIUS)) {
            isTouched = true;
            Gdx.app.log("TAG", "ROULETTE");
            // return isTouched;
            if(isTouched) buttonClick();
            return isTouched;
        }
        Gdx.app.log("TAG", "NOTHING");
        return false;
    }

    @Override
    public Group getGroup() {
        return group;
    }
}
