package com.betmansmall.game.GameScreenInteface;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.gameLogic.GameField;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;

import java.util.Arrays;
import java.util.List;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class TowersRoulette extends Roulette {
    public GameField gameField;
    public BitmapFont bitmapFont;
//<<<<<<< HEAD
//<<<<<<< HEAD
//    public Table table;
//	public Stage stage;
//	float sizeButton = 128f;
//
//    private Array<TemplateForTower> templateForTowers;
//    private HorizontalGroup horizontalGroup;
//    public Group buttonGroup = new Group();
//    private Array<ImageButton> towerFrames;
//   private static volatile Boolean IS_HIDE_TOWERS = true; // Prosto pizdec ANDREY!!!!2!!!!!
//
//    public TowersRoulette(GameField gameField, BitmapFont bitmapFont, Table table, Stage stage) {
//        this.gameField = gameField;
//        this.bitmapFont = bitmapFont;
//        this.table = table;
//        this.stage = stage;
////=======
//=======
//>>>>>>> master2
    public Stage stage;

    private Group circleGroup;
    private Group buttonGroup;
    private ImageButton rouletteButton;
    private ImageButton rouletteCircle;
    private Array<ImageButton> towerFrames;
    private static volatile Boolean IS_HIDE_TOWERS = true;
    private Array<TemplateForTower> templateForTowers;

    public TowersRoulette(GameField gameField, BitmapFont bitmapFont, Stage stage) {
        this.gameField = gameField;
        this.bitmapFont = bitmapFont;
        this.stage = stage;
        init();
    }
//<<<<<<< HEAD
////>>>>>>> master2
//=======
//>>>>>>> master2

    private void init() { // Нужно тут все naxui переделать. Либо как в CreepsRoulette, либо вообще все по нормальному с Layouts || но пока да -- так=(
        //TEMPORARY VARIANT OF TAKING TOWERS
        templateForTowers = gameField.getAllTemplateForTowers();
//<<<<<<< HEAD
//<<<<<<< HEAD
//        horizontalGroup = new HorizontalGroup();
//        table.add(horizontalGroup).right();
//		towerFrames = new Array<ImageButton>();
//
//        ImageButton imageButton = new ImageButton(new Image(new Texture(Gdx.files.internal("img/tower_button.png"))).getDrawable());
//		imageButton.setSize(sizeButton, sizeButton);
//        imageButton.setScale(0.1f);
//	    imageButton.setVisible(false);
//        table.add(imageButton).bottom();
//        // stage.addActor(rouletteButton);
//        for(int towersNumber = 0; towersNumber < templateForTowers.size; towersNumber++) {
//           TemplateForTower templateForTower = templateForTowers.get(towersNumber);
//
//           ImageButton templateButton = new ImageButton(new Image(templateForTower.idleTile.getTextureRegion()).getDrawable());
//           ImageButton templateFrame = new ImageButton(new Image(new Texture(Gdx.files.internal("img/build_frame.png"))).getDrawable());
//           templateButton.setSize(sizeButton, sizeButton);
//           templateButton.setPosition(Gdx.graphics.getWidth()-190f, imageButton.getY() + (sizeButton + towersNumber * sizeButton/1.5f));
//           templateFrame.setSize(sizeButton, sizeButton/1.5f);
//           templateFrame.setPosition(Gdx.graphics.getWidth()-190f, imageButton.getY() + (sizeButton + towersNumber * sizeButton/1.5f));
//           templateButton.setVisible(true);
//           templateFrame.setVisible(true);
//
//           buttonGroup.addActor(templateButton);
//           buttonGroup.addActor(templateFrame);
//           towerFrames.add(templateFrame);
//
//           String nameTower = templateForTower.name;
//           String attackTower = templateForTower.damage.toString();
//           String radiusDetectionTower = templateForTower.radiusDetection.toString();
//           String costTower = templateForTower.cost.toString();
//
//           Label nameLabel = new Label(nameTower, new Label.LabelStyle(bitmapFont, Color.WHITE));
//           Label attackLabel = new Label(attackTower, new Label.LabelStyle(bitmapFont, Color.RED));
//           Label radiusDetectionLabel = new Label(radiusDetectionTower, new Label.LabelStyle(bitmapFont, Color.GREEN));
//           Label costLabel = new Label(costTower, new Label.LabelStyle(bitmapFont, Color.YELLOW));
//           float textX = Gdx.graphics.getWidth()-30f;
//           float textY = imageButton.getY() + (sizeButton + towersNumber * sizeButton/1.5f); // 4to za pizdec? AndreY??? NAXUI
//           nameLabel.setPosition(textX-160, textY+30); // Magic number
//           attackLabel.setPosition(textX, textY+60); // tyt tak zavedino
//           radiusDetectionLabel.setPosition(textX, textY+30);
//           costLabel.setPosition(textX, textY+10);
//           nameLabel.setVisible(true);
//           attackLabel.setVisible(true);
//           radiusDetectionLabel.setVisible(true);
//           costLabel.setVisible(true);
//
//           buttonGroup.addActor(nameLabel);
//           buttonGroup.addActor(attackLabel);
//           buttonGroup.addActor(radiusDetectionLabel);
//           buttonGroup.addActor(costLabel);
//
//           Gdx.app.log("TowersRoulette::init()", "-- button pos:(" + templateButton.getX() + "," + templateButton.getY() + "):" + nameTower);
//       }
//       stage.addActor(buttonGroup);
//        Gdx.app.log("TowersRoulette::init()", "-- templateForTowers.size:" + templateForTowers.size);
//    }
//
//   private void buttonClick() {
//       if(IS_HIDE_TOWERS) {
//           Gdx.app.log("TowersRoulette::changeGameState()", "-- ApplicationType.Desktop");
//           for (Actor actor : buttonGroup.getChildren()) {
//               actor.setVisible(!IS_HIDE_TOWERS);
//           }
//       } else {
//			gameField.cancelUnderConstruction();
//			IS_HIDE_TOWERS = !IS_HIDE_TOWERS;
//	   }
//   }
//
//    public boolean pan(float x, float y, float deltaX, float deltaY) {
//        Gdx.app.log("TowersRoulette::pan(" + x + "," + y + "," + deltaX + "," + deltaY + ")", "--");
////        if(Gdx.app.getType() == Application.ApplicationType.Android) {
////            Gdx.app.log("TowersRoulette::tap()", "-- ApplicationType.Android");
////            x = Gdx.graphics.getWidth() - x;
////            y = Gdx.graphics.getHeight() - y;
////            if ((x * x + y * y) <= (sizeButton * sizeButton)
////                    && x <= sizeButton && y <= sizeButton && !IS_HIDE_TOWERS) {
////                if (!((x * x + y * y) <= sizeButton * sizeButton
////                        && x <= sizeButton && y <= sizeButton)) {
////                    float rotation = -((deltaX < 0) ? -1f : 1f) * ((deltaY < 0) ? deltaY * (-1f) : deltaY);
////                    circleGroup.rotateBy(rotation);
////                    return true;
////                }
////            }
////        }
//=======
//=======
//>>>>>>> master2
        buttonGroup = new Group();
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
            stage.addActor(buttonGroup); // Naxer eto gavno!
            stage.addActor(circleGroup); // Naxer eto gavno!
        } else if(Gdx.app.getType() == Application.ApplicationType.Desktop) {
            Gdx.app.log("TowersRoulette::init()", "-- ApplicationType.Desktop");
            rouletteButton = new ImageButton(new Image(new Texture(Gdx.files.internal("img/tower_button.png"))).getDrawable());
            rouletteButton.setName("rouletteButton");
            rouletteButton.setSize(getLocalWidth(ROULETTE_RADIUS), getLocalHeight(ROULETTE_RADIUS));
            rouletteButton.setPosition(Gdx.graphics.getWidth() - rouletteButton.getWidth(), 0);
            rouletteButton.setOrigin(Gdx.graphics.getWidth(), 0);
            stage.addActor(rouletteButton);
            for(int towersNumber = 0; towersNumber < templateForTowers.size; towersNumber++) {
                TemplateForTower templateForTower = templateForTowers.get(towersNumber);

                ImageButton templateButton = new ImageButton(new Image(templateForTower.idleTile.getTextureRegion()).getDrawable());
                ImageButton templateFrame = new ImageButton(new Image(new Texture(Gdx.files.internal("img/build_frame.png"))).getDrawable());
                templateButton.setSize(getLocalWidth(ROULETTE_RADIUS)/1.5f, getLocalHeight(ROULETTE_RADIUS)/1.5f);
                templateButton.setPosition(rouletteButton.getX(), rouletteButton.getY()
                        + (getLocalWidth(ROULETTE_RADIUS) + towersNumber * getLocalWidth(ROULETTE_RADIUS)/1.5f));
                templateFrame.setSize(getLocalWidth(ROULETTE_RADIUS), getLocalHeight(ROULETTE_RADIUS)/1.5f);
                templateFrame.setPosition(rouletteButton.getX(), rouletteButton.getY()
                        + (getLocalWidth(ROULETTE_RADIUS) + towersNumber * getLocalWidth(ROULETTE_RADIUS)/1.5f));
                templateButton.setVisible(false);
                templateFrame.setVisible(false);

                buttonGroup.addActor(templateButton);
                buttonGroup.addActor(templateFrame);
                towerFrames.add(templateFrame);

                String nameTower = templateForTower.name;
                String attackTower = templateForTower.damage.toString();
                String radiusDetectionTower = templateForTower.radiusDetection.toString();
                String costTower = templateForTower.cost.toString();

                Label nameLabel = new Label(nameTower, new Label.LabelStyle(bitmapFont, Color.WHITE));
                Label attackLabel = new Label(attackTower, new Label.LabelStyle(bitmapFont, Color.RED));
                Label radiusDetectionLabel = new Label(radiusDetectionTower, new Label.LabelStyle(bitmapFont, Color.GREEN));
                Label costLabel = new Label(costTower, new Label.LabelStyle(bitmapFont, Color.YELLOW));
                float textX = Gdx.graphics.getWidth()-30f;
                float textY = rouletteButton.getY() + (getLocalWidth(ROULETTE_RADIUS) + towersNumber * getLocalWidth(ROULETTE_RADIUS)/1.5f); // 4to za pizdec? AndreY??? NAXUI
                nameLabel.setPosition(textX-160, textY+30); // Magic number
                attackLabel.setPosition(textX, textY+60); // tyt tak zavedino
                radiusDetectionLabel.setPosition(textX, textY+30);
                costLabel.setPosition(textX, textY+10);
                nameLabel.setVisible(false);
                attackLabel.setVisible(false);
                radiusDetectionLabel.setVisible(false);
                costLabel.setVisible(false);

                buttonGroup.addActor(nameLabel);
                buttonGroup.addActor(attackLabel);
                buttonGroup.addActor(radiusDetectionLabel);
                buttonGroup.addActor(costLabel);

                Gdx.app.log("TowersRoulette::init()", "-- button pos:(" + templateButton.getX() + "," + templateButton.getY() + "):" + nameTower);
            }
            stage.addActor(buttonGroup);
            Gdx.app.log("TowersRoulette::init()", "-- templateForTowers.size:" + templateForTowers.size);
        } else {
            Gdx.app.log("TowersRoulette::init()", "-- Device is not supported");
        }
    }

    private void buttonClick() {
        IS_HIDE_TOWERS = !IS_HIDE_TOWERS;
        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            Gdx.app.log("TowersRoulette::changeGameState()", "-- ApplicationType.Android");
            rouletteCircle.setVisible(!IS_HIDE_TOWERS);
            rouletteButton.setPosition(Gdx.graphics.getWidth() - rouletteButton.getWidth(), 0);
        } else if(Gdx.app.getType() == Application.ApplicationType.Desktop) {
            Gdx.app.log("TowersRoulette::changeGameState()", "-- ApplicationType.Desktop");
//            for(int towersNumber = 0; towersNumber < templateForTowers.size; towersNumber++ ) {
//                towerButtonsArray.get(towersNumber).setVisible(!IS_HIDE_TOWERS);
//                towerFrames.get(towersNumber).setVisible(!IS_HIDE_TOWERS);
//            }
            for (Actor actor : buttonGroup.getChildren()) {
                actor.setVisible(!IS_HIDE_TOWERS);
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
//<<<<<<< HEAD
////>>>>>>> master2
//=======
//>>>>>>> master2
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
        Gdx.app.log("TowersRoulette::chooseTowerAndroid(" + isGreatedRound + ")", "-- User choose tower:" + localTemplate.name);
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

//<<<<<<< HEAD
//<<<<<<< HEAD
//    public boolean tap(float x, float y, int count, int button) {
//        Gdx.app.log("TowersRoulette::tap(" + x + "," + y + "," + count + "," + button + ")", "--");
////        boolean isTouched = false;
////        x = Gdx.graphics.getWidth()  - x;
////        y = Gdx.graphics.getHeight() - y;
////        Gdx.app.log("TowersRoulette::tap(" + x + "," + y + "," + count + "," + button + ")", "--");
//        // return false;
//
////        //RING PRESS DETECTION
////        if((x*x + y*y) <= (sizeButton * sizeButton)
////                && x <= sizeButton
////                && y <= sizeButton && !IS_HIDE_TOWERS
////                && Gdx.app.getType() == Application.ApplicationType.Android) {
////            if (!((x*x + y*y) <= sizeButton * sizeButton
////                    && x <= sizeButton
////                    && y <= sizeButton)) {
////                Gdx.app.log("TowersRoulette::tap()", "-- RING PRESS DETECTION");
////                isTouched = true;
////                if(isTouched) ringClick();
////                return isTouched;
////            }
////        }
//
//       //BUTTON PRESS DETECTION ANDROID
//       if ((x * x + y * y) <= sizeButton * sizeButton
//               && x <= sizeButton
//               && y <= sizeButton
//               && Gdx.app.getType() == Application.ApplicationType.Android) {
//           Gdx.app.log("TowersRoulette::tap()", "-- BUTTON PRESS DETECTION ANDROID");
//           buttonClick();
//           return true;
//       }
//       //BUTTON PRESS DETECTION DESKTOP
//       if ((x * y) <= sizeButton * sizeButton
//               && x <= sizeButton
//               && y <= sizeButton) {
//           Gdx.app.log("TowersRoulette::tap()", "-- BUTTON PRESS DETECTION DESKTOP");
//           buttonClick();
//           return true;
//       }
//       //TOWER_BUTTON PRESS DETECTION
//       if(        x <= sizeButton
//               && y <= (templateForTowers.size) * sizeButton/1.5f + sizeButton
//               && y >= sizeButton/1.5f){
//           Gdx.app.log("TowersRoulette::tap()", "-- TOWER_BUTTON PRESS DETECTION");
//           chooseTowerDesktop(x, y);
//           return true;
//       }
//       return false;
//=======
//=======
//>>>>>>> master2
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
//<<<<<<< HEAD
////>>>>>>> master2
//=======
//>>>>>>> master2
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
