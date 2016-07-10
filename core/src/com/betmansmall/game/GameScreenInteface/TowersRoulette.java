package com.betmansmall.game.GameScreenInteface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.gameLogic.GameField;
import com.betmansmall.game.gameLogic.playerTemplates.Faction;
import com.betmansmall.game.gameLogic.playerTemplates.FactionsManager;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class TowersRoulette extends Roulette {
    private Group circleGroup;
    private Group buttonGroup;
    private ImageButton rouletteButton;
    private ImageButton rouletteCircle;
    private static volatile Boolean IS_HIDE_TOWERS = true;
    private GameField gameField;
    private RotateToAction rotateToAction;
    private TemplateForTower templateForTower;
    private  FactionsManager factionsManager;
    private Faction faction;
    private float scale;

    public TowersRoulette(GameField gameField) {
        this.gameField = gameField;
        init();
    }

    private void init() {
        circleGroup  = new Group();
        buttonGroup = new Group();

        scale = (((float) Gdx.graphics.getWidth()) / 1980);

        rouletteButton = new ImageButton(new Image(new Texture(Gdx.files.internal("img/tower_roulette_main.png"))).getDrawable());
        rouletteButton.setName("rouletteButton");
        rouletteButton.setSize(getLocalWidth(ROULETTE_RADIUS)*scale, getLocalHeight(ROULETTE_RADIUS)*scale);
        rouletteButton.setPosition(Gdx.graphics.getWidth() - rouletteButton.getWidth(), 0);
        rouletteButton.setOrigin(Gdx.graphics.getWidth(), 0);
        buttonGroup.addActor(rouletteButton);
        buttonGroup.setOrigin(Gdx.graphics.getWidth(), 0);

        rouletteCircle = new ImageButton(new Image(new Texture(Gdx.files.internal("img/golden_ring.png"))).getDrawable());
        rouletteCircle.setSize(getLocalWidth(RING_RADIUS) * 2*scale, getLocalHeight(RING_RADIUS) * 2*scale);
        rouletteCircle.setPosition(Gdx.graphics.getWidth() - rouletteCircle.getWidth() / 2, 0 - rouletteCircle.getHeight() / 2);
        rouletteCircle.setVisible(false);
        circleGroup.addActor(rouletteCircle);
        circleGroup.setOrigin(Gdx.graphics.getWidth(), 0);

    }

    private void buttonClick() {
        IS_HIDE_TOWERS = !IS_HIDE_TOWERS;
        rouletteCircle.setVisible(!IS_HIDE_TOWERS);
        rouletteButton.setPosition(Gdx.graphics.getWidth() - rouletteButton.getWidth(), 0);
        if(IS_HIDE_TOWERS)
            gameField.cancelUnderConstruction();
    }

    private void ringClick(){
//        Gdx.app.log("TAG", "Tower is selected");
        rouletteButton.setSize(getLocalWidth(ROULETTE_RADIUS)*scale, getLocalHeight(ROULETTE_RADIUS)*scale);
        rouletteButton.setPosition(Gdx.graphics.getWidth() - rouletteButton.getWidth(), 0);
        float trash = circleGroup.getRotation() % 90; //TODO rename trash variable
        if(trash  > 45 ) {
            circleGroup.addAction(rotateBy(90f - trash, 0.5f));
        } else {
            circleGroup.addAction(rotateBy(-trash, 0.5f));
        }
        //TODO implement neccessary part just workaround
        chooseTower(trash);
    }

    public boolean makeRotation(float x, float y, float deltaX, float deltaY) {
        x = Gdx.graphics.getWidth()  - x;
        y = Gdx.graphics.getHeight() - y;
        if((x*x + y*y) <= (getLocalWidth(RING_RADIUS) * getLocalWidth(RING_RADIUS))
                && x <= getLocalWidth(RING_RADIUS) && y <= getLocalWidth(RING_RADIUS) && !IS_HIDE_TOWERS) {
            if (!((x*x + y*y) <= getLocalWidth(ROULETTE_RADIUS) * getLocalWidth(ROULETTE_RADIUS)
                    && x <= getLocalWidth(ROULETTE_RADIUS) && y <= getLocalWidth(ROULETTE_RADIUS))) {
                float rotation = -((deltaX < 0)? -1f : 1f) * ((deltaY < 0)? deltaY * (-1f) : deltaY);
                circleGroup.rotateBy(rotation);
                return true;
            }
        }
        return false;
    }

    public void chooseTower(float isGreatedRound) {
        Array<TemplateForTower> templateForTowers = gameField.getAllFirstTowersFromFirstFaction();
        TemplateForTower localTemplate = templateForTowers.get(0);
        float tmp;
        if(isGreatedRound  > 45 ) {
            tmp = 90f - isGreatedRound + circleGroup.getRotation();
        } else {
            tmp = - isGreatedRound + circleGroup.getRotation();
        }

        int towerId = (int)(tmp % (90 * templateForTowers.size)) / 90;
        if(towerId < templateForTowers.size)
            localTemplate = templateForTowers.get(Math.abs(towerId));
        Gdx.app.log("tag", "sette :" + localTemplate.name);
        gameField.createdUnderConstruction(localTemplate);
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
//            Gdx.app.log("TAG", "ROULETTE");
            // return isTouched;
            if(isTouched) buttonClick();
            return isTouched;
        }
//        Gdx.app.log("TAG", "NOTHING");
        return false;
    }

    @Override
    public List<Group> getGroup() {
        return Arrays.asList(buttonGroup, circleGroup);
    }
}
