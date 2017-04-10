package com.betmansmall.game.GameScreenInteface;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.gameLogic.GameField;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;

import java.util.Arrays;
import java.util.List;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class TowersRoulette extends Roulette {
    private Group circleGroup;
    private Group buttonGroup;
    private ImageButton rouletteButton;
    private ImageButton rouletteCircle;
    private Array<ImageButton> towerButtonsArray;
    private Array<ImageButton> towerFrames;
    private static volatile Boolean IS_HIDE_TOWERS = true;
    private GameField gameField;
    private Array<TemplateForTower> templateForTowers;

    public TowersRoulette(GameField gameField) {
        this.gameField = gameField;
        init();
    }

    private void init() {
        //TEMPORARY VARIANT OF TAKING TOWERS
        templateForTowers = gameField.getAllTowers();

        buttonGroup = new Group();
        towerButtonsArray = new Array<ImageButton>();

        towerFrames = new Array<ImageButton>();

        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            Gdx.app.log("TowersRoulette::init()", "-- ApplicationType.Android");
            circleGroup = new Group();
            rouletteButton = new ImageButton(new Image(new Texture(Gdx.files.internal("img/tower_roulette_main.png"))).getDrawable());
            rouletteButton.setName("rouletteButton");
            rouletteButton.setSize(getLocalWidth(ROULETTE_RADIUS), getLocalHeight(ROULETTE_RADIUS));
            rouletteButton.setPosition(Gdx.graphics.getWidth() - rouletteButton.getWidth(), 0);
            rouletteButton.setOrigin(Gdx.graphics.getWidth(), 0);
            buttonGroup.addActor(rouletteButton);
            buttonGroup.setOrigin(Gdx.graphics.getWidth(), 0);

            rouletteCircle = new ImageButton(new Image(new Texture(Gdx.files.internal("img/golden_ring.png"))).getDrawable());
            rouletteCircle.setSize(getLocalWidth(RING_RADIUS) * 2, getLocalHeight(RING_RADIUS) * 2);
            rouletteCircle.setPosition(Gdx.graphics.getWidth() - rouletteCircle.getWidth() / 2, 0 - rouletteCircle.getHeight() / 2);
            rouletteCircle.setVisible(false);
            circleGroup.addActor(rouletteCircle);
            circleGroup.setOrigin(Gdx.graphics.getWidth(), 0);
        } else if(Gdx.app.getType() == Application.ApplicationType.Desktop) {
            Gdx.app.log("TowersRoulette::init()", "-- ApplicationType.Desktop");
            rouletteButton = new ImageButton(new Image(new Texture(Gdx.files.internal("img/tower_button.png"))).getDrawable());
            rouletteButton.setName("rouletteButton");
            rouletteButton.setSize(getLocalWidth(ROULETTE_RADIUS), getLocalHeight(ROULETTE_RADIUS));
            rouletteButton.setPosition(Gdx.graphics.getWidth() - rouletteButton.getWidth(), 0);
            rouletteButton.setOrigin(Gdx.graphics.getWidth(), 0);
            buttonGroup.addActor(rouletteButton);
            buttonGroup.setOrigin(Gdx.graphics.getWidth(), 0);
            int towersNumber;
            for(towersNumber = 0; towersNumber < templateForTowers.size; towersNumber++) {
                TemplateForTower templateForTower = templateForTowers.get(towersNumber);
                ImageButton templateButton = new ImageButton(new Image(templateForTower.idleTile.getTextureRegion()).getDrawable());
                ImageButton templateFrame = new ImageButton(new Image(new Texture(Gdx.files.internal("img/build_frame.png"))).getDrawable());

//                String attackTower = templateForTower.damage.toString();
//                String radiusTower = templateForTower.radius.toString();
//                String costTower = templateForTower.cost.toString();
//                String nameTower = templateForTower.name;

                towerButtonsArray.add(templateButton);
                towerFrames.add(templateFrame);

                towerButtonsArray.get(towersNumber).setSize(getLocalWidth(ROULETTE_RADIUS)/1.5f, getLocalHeight(ROULETTE_RADIUS)/1.5f);
                towerButtonsArray.get(towersNumber).setPosition(
                        rouletteButton.getX(),
                        rouletteButton.getY()
                                + (getLocalWidth(ROULETTE_RADIUS) + towersNumber * getLocalWidth(ROULETTE_RADIUS)/1.5f));
                towerFrames.get(towersNumber).setSize(getLocalWidth(ROULETTE_RADIUS), getLocalHeight(ROULETTE_RADIUS)/1.5f);
                towerFrames.get(towersNumber).setPosition(
                        rouletteButton.getX(),
                        rouletteButton.getY()
                                + (getLocalWidth(ROULETTE_RADIUS) + towersNumber * getLocalWidth(ROULETTE_RADIUS)/1.5f));
                Gdx.app.log("TowersRoulette::init()", "-- button position(" + templateButton.getX() + "," + templateButton.getY() + ")");
                towerButtonsArray.get(towersNumber).setVisible(false);
                towerFrames.get(towersNumber).setVisible(false);
                buttonGroup.addActor(towerButtonsArray.get(towersNumber));
                buttonGroup.addActor(towerFrames.get(towersNumber));
            }
            Gdx.app.log("TowersRoulette::init()", "-- towersNumber:" + towersNumber);
        } else {
            Gdx.app.log("TowersRoulette::init()", "-- Device is not recognized");
        }
    }

    private void buttonClick() {
        IS_HIDE_TOWERS = !IS_HIDE_TOWERS;
        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            Gdx.app.log("TowersRoulette::buttonClick()", "-- ApplicationType.Android");
            rouletteCircle.setVisible(!IS_HIDE_TOWERS);
            rouletteButton.setPosition(Gdx.graphics.getWidth() - rouletteButton.getWidth(), 0);
        } else if(Gdx.app.getType() == Application.ApplicationType.Desktop) {
            Gdx.app.log("TowersRoulette::buttonClick()", "-- ApplicationType.Desktop");
            for(int towersNumber = 0; towersNumber < templateForTowers.size; towersNumber++ ) {
                towerButtonsArray.get(towersNumber).setVisible(!IS_HIDE_TOWERS);
                towerFrames.get(towersNumber).setVisible(!IS_HIDE_TOWERS);
            }
        }
        if(IS_HIDE_TOWERS) {
            gameField.cancelUnderConstruction();
        }
    }

    private void ringClick(){
//        Gdx.app.log("TowersRoulette::ringClick()", "-- Tower is selected");
        rouletteButton.setSize(getLocalWidth(ROULETTE_RADIUS), getLocalHeight(ROULETTE_RADIUS));
        rouletteButton.setPosition(Gdx.graphics.getWidth() - rouletteButton.getWidth(), 0);
        float trash = circleGroup.getRotation() % 90; //TODO rename trash variable
        if(trash  > 45 ) {
            circleGroup.addAction(rotateBy(90f - trash, 0.5f));
        } else {
            circleGroup.addAction(rotateBy(-trash, 0.5f));
        }
        //TODO implement neccessary part just workaround
        chooseTowerAndroid(trash);
    }

    public boolean makeRotation(float x, float y, float deltaX, float deltaY) {
        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            Gdx.app.log("TowersRoulette::makeRotation()", "-- ApplicationType.Android");
            x = Gdx.graphics.getWidth() - x;
            y = Gdx.graphics.getHeight() - y;
            if ((x * x + y * y) <= (getLocalWidth(RING_RADIUS) * getLocalWidth(RING_RADIUS))
                    && x <= getLocalWidth(RING_RADIUS) && y <= getLocalWidth(RING_RADIUS) && !IS_HIDE_TOWERS) {
                if (!((x * x + y * y) <= getLocalWidth(ROULETTE_RADIUS) * getLocalWidth(ROULETTE_RADIUS)
                        && x <= getLocalWidth(ROULETTE_RADIUS) && y <= getLocalWidth(ROULETTE_RADIUS))) {
                    float rotation = -((deltaX < 0) ? -1f : 1f) * ((deltaY < 0) ? deltaY * (-1f) : deltaY);
                    circleGroup.rotateBy(rotation);
                    return true;
                }
            }
        }
        return false;
    }

    public void chooseTowerAndroid(float isGreatedRound) {
        TemplateForTower localTemplate = templateForTowers.get(0);
        float tmp;
        if(isGreatedRound  > 45 ) {
            tmp = 90f - isGreatedRound + circleGroup.getRotation();
        } else {
            tmp = - isGreatedRound + circleGroup.getRotation();
        }

        int towerId = (int)(tmp % (90 * templateForTowers.size)) / 90;
        if(towerId < templateForTowers.size) {
            localTemplate = templateForTowers.get(Math.abs(towerId));
        }
        Gdx.app.log("TowersRoulette::chooseTowerAndroid(" + isGreatedRound + ")", "User choose tower:" + localTemplate.name);
        gameField.createdUnderConstruction(localTemplate);
    }

    private void chooseTowerDesktop(float x, float y) {
        Gdx.app.log("TowersRoulette::chooseTowerDesktop(" + x + ", " + y + ")", "-- templateForTowers.size:" + templateForTowers.size + " towerFrames.size:" + towerFrames.size);
        TemplateForTower localTemplate;
        for(int towerNumber = 0; towerNumber < towerFrames.size; towerNumber++) {
            Gdx.app.log("TowersRoulette::chooseTowerDesktop()", "-- towerNumber:" + towerNumber);
            if (towerFrames.get(towerNumber).isPressed()) {
                Gdx.app.log("TowersRoulette::chooseTowerDesktop()", "-- towerNumber.isPressed()");
                localTemplate = templateForTowers.get(towerNumber);
                gameField.createdUnderConstruction(localTemplate);
                break;
            }
        }
    }

    public boolean isButtonTouched(float x, float y) {
        boolean isTouched = false;
        x = Gdx.graphics.getWidth()  - x;
        y = Gdx.graphics.getHeight() - y;
        Gdx.app.log("TowersRoulette::isButtonTouched()", "-- x:" + x + " y:" + y);

        //RING PRESS DETECTION
        if((x*x + y*y) <= (getLocalWidth(RING_RADIUS) * getLocalWidth(RING_RADIUS))
                && x <= getLocalWidth(RING_RADIUS)
                && y <= getLocalWidth(RING_RADIUS) && !IS_HIDE_TOWERS
                && Gdx.app.getType() == Application.ApplicationType.Android) {
            if (!((x*x + y*y) <= getLocalWidth(ROULETTE_RADIUS) * getLocalWidth(ROULETTE_RADIUS)
                    && x <= getLocalWidth(ROULETTE_RADIUS)
                    && y <= getLocalWidth(ROULETTE_RADIUS))) {
                Gdx.app.log("TowersRoulette::isButtonTouched()", "-- RING PRESS DETECTION");
                isTouched = true;
                if(isTouched) ringClick();
                return isTouched;
            }
        }

        //BUTTON PRESS DETECTION ANDROID
        if ((x * x + y * y) <= getLocalWidth(ROULETTE_RADIUS) * getLocalWidth(ROULETTE_RADIUS)
                && x <= getLocalWidth(ROULETTE_RADIUS)
                && y <= getLocalWidth(ROULETTE_RADIUS)
                && Gdx.app.getType() == Application.ApplicationType.Android) {
            Gdx.app.log("TowersRoulette::isButtonTouched()", "-- BUTTON PRESS DETECTION ANDROID");
            isTouched = true;
            if (isTouched) buttonClick();
            return isTouched;
        }
        //BUTTON PRESS DETECTION DESKTOP
        if ((x * y) <= getLocalWidth(ROULETTE_RADIUS) * getLocalWidth(ROULETTE_RADIUS)
                && x <= getLocalWidth(ROULETTE_RADIUS)
                && y <= getLocalWidth(ROULETTE_RADIUS)) {
            Gdx.app.log("TowersRoulette::isButtonTouched()", "-- BUTTON PRESS DETECTION DESKTOP");
            isTouched = true;
            if (isTouched) buttonClick();
            return isTouched;
        }
        //TOWER_BUTTON PRESS DETECTION
        if(        x <= getLocalWidth(ROULETTE_RADIUS)
                && y <= (templateForTowers.size) * getLocalWidth(ROULETTE_RADIUS)/1.5f + getLocalWidth(ROULETTE_RADIUS)
                && y >= getLocalHeight(ROULETTE_RADIUS)/1.5f){
            Gdx.app.log("TowersRoulette::isButtonTouched()", "-- TOWER_BUTTON PRESS DETECTION");
            chooseTowerDesktop(x, y);
            isTouched = true;
            return isTouched;
        }
        return isTouched;
    }

    @Override
    public List<Group> getGroup() {
        if (Gdx.app.getType() == Application.ApplicationType.Android){
            return Arrays.asList(buttonGroup, circleGroup);
        } else {
            return Arrays.asList(buttonGroup);
        }
    }
}
