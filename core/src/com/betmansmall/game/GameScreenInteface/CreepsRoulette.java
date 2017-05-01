package com.betmansmall.game.GameScreenInteface;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.gameLogic.GameField;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Дима Цыкунов on 20.02.2016.
 */
public class CreepsRoulette extends Roulette {
    public GameField gameField;
    public BitmapFont bitmapFont;
    public Stage stage;

    private Array<TemplateForUnit> templateForUnits;
    private Group creepsButtonGroup;
    private boolean showCreepsSelector;
    private ImageButton creepsSelectorButton;
    private ImageButton playButton;
    private ImageButton pauseButton;
    private static volatile Boolean IS_PAUSE = true; // Prosto pizdec ANDREY!!!!!!!!!

    public CreepsRoulette(final GameField gameField, BitmapFont bitmapFont, Stage stage) {
        this.gameField = gameField;
        this.bitmapFont = bitmapFont;
        this.stage = stage;

        this.templateForUnits = gameField.getAllTemplateForUnits();
        this.creepsButtonGroup = new Group();
        creepsButtonGroup.setZIndex(1); // блять это гавно не пашет
        creepsButtonGroup.setDebug(true, true);

        showCreepsSelector = false;
        creepsSelectorButton = new ImageButton(new Image(new Texture(Gdx.files.internal("img/creep_roulette_main.png"))).getDrawable());
        creepsSelectorButton.setSize(getLocalWidth(ROULETTE_RADIUS),getLocalHeight(ROULETTE_RADIUS));
        creepsSelectorButton.setPosition(0, 0);
        creepsSelectorButton.setZIndex(5); // и это тоже!=(
        stage.addActor(creepsSelectorButton);

        playButton = new ImageButton(new Image(new Texture(Gdx.files.internal("img/playbutton.png"))).getDrawable());
        playButton.setSize(getLocalWidth(ROULETTE_RADIUS),getLocalHeight(ROULETTE_RADIUS));
        playButton.setPosition(getLocalWidth(ROULETTE_RADIUS), 0);
        playButton.setZIndex(1);
        stage.addActor(playButton);

        pauseButton = new ImageButton(new Image(new Texture(Gdx.files.internal("img/pausebutton.png"))).getDrawable());
        pauseButton.setSize(getLocalWidth(ROULETTE_RADIUS),getLocalHeight(ROULETTE_RADIUS));
        pauseButton.setPosition(getLocalWidth(ROULETTE_RADIUS), 0);
        playButton.setZIndex(0);
        stage.addActor(pauseButton);

        TextureRegion textureRegionFrame = new TextureRegion(new Texture(Gdx.files.internal("img/build_frame.png")));
        textureRegionFrame.flip(true, false);
        Image imageFrame = new Image(textureRegionFrame);
        for(int unitIndex = 0; unitIndex < templateForUnits.size; unitIndex++) {
            ImageButton creepFrameButton = new ImageButton(imageFrame.getDrawable());
            creepFrameButton.setSize(getLocalWidth(ROULETTE_RADIUS), getLocalHeight(ROULETTE_RADIUS)/1.5f); // 4to za pizdec? AndreY??? NAXUI
            creepFrameButton.setPosition(creepsSelectorButton.getX(), creepsSelectorButton.getY()
                    + (getLocalWidth(ROULETTE_RADIUS) + unitIndex * getLocalWidth(ROULETTE_RADIUS)/1.5f)); // 4to za pizdec? AndreY??? NAXUI
//            creepFrameButton.addListener(new ClickListener() {
//                @Override
//                public void clicked(InputEvent event, float x, float y) {
//                    Gdx.app.log("CreepsRoulette::ClickListener::clicked(" + event + "," + x + "," + y + ")", "-- creepFrameButton");
//                }
//            });
            TemplateForUnit templateForUnit = templateForUnits.get(unitIndex);

            String nameUnit = templateForUnit.name;
            String hpUnit = templateForUnit.healthPoints.toString();
            String speedUnit = templateForUnit.speed.toString();
            String costUnit = templateForUnit.cost.toString();

            Label nameUnitLabel = new Label(nameUnit, new Label.LabelStyle(bitmapFont, Color.WHITE));
            Label hpUnitLabel = new Label(hpUnit, new Label.LabelStyle(bitmapFont, Color.RED));
            Label speedUnitLabel = new Label(speedUnit, new Label.LabelStyle(bitmapFont, Color.GREEN));
            Label costUnitLabel = new Label(costUnit, new Label.LabelStyle(bitmapFont, Color.YELLOW));
            float textX = 1.0f;
            float textY = 0;
            nameUnitLabel.setPosition(textX+45, textY); // Magic number
            hpUnitLabel.setPosition(textX, textY + 65); // tyt tak zavedeno
            speedUnitLabel.setPosition(textX, textY + 33); // need make layouts
            costUnitLabel.setPosition(textX, textY + 7);

            nameUnitLabel.setName("nameUnitLabel");
            hpUnitLabel.setName("hpUnitLabel");
            speedUnitLabel.setName("speedUnitLabel");
            costUnitLabel.setName("costUnitLabel");
            creepFrameButton.addActor(nameUnitLabel);
            creepFrameButton.addActor(hpUnitLabel);
            creepFrameButton.addActor(speedUnitLabel);
            creepFrameButton.addActor(costUnitLabel);

            ImageButton templateButton = new ImageButton(new Image(templateForUnit.animations.values().toArray().get(6).getTextureRegion()).getDrawable());
            templateButton.setSize(creepFrameButton.getWidth()/1.6f, creepFrameButton.getHeight());
            templateButton.setPosition(45f, 0f);

            templateButton.setName("templateButton");
            creepFrameButton.setName("creepFrameButton");
            creepFrameButton.addActor(templateButton);
            creepFrameButton.setVisible(false);
            creepFrameButton.setUserObject(unitIndex);
//            creepFrameButton.setZIndex(1); // да ну впзду
            creepsButtonGroup.addActor(creepFrameButton);
            creepsButtonGroup.setHeight(creepsButtonGroup.getHeight()+creepFrameButton.getHeight());

            Gdx.app.log("CreepsRoulette::init()", "-- button pos:(" + creepFrameButton.getX() + "," + creepFrameButton.getY() + "):" + nameUnit);
        }
        int buttonsSize = creepsButtonGroup.getChildren().size;
        for(Actor actor : creepsButtonGroup.getChildren()) {
            Gdx.app.log("CreepsRoulette::init()", "-- actor:" + actor/* + " actor.getName():" + actor.getName() + " actor.getUserObject():" + actor.getUserObject()*/);
        }
        stage.addActor(creepsButtonGroup);
        Gdx.app.log("CreepsRoulette::init()", "-- buttonsSize:" + buttonsSize);
        Gdx.app.log("CreepsRoulette::init()", "-- templateForUnits.size:" + templateForUnits.size);
    }

    public void changeGameState() {
        Gdx.app.log("CreepsRoulette::changeGameState()", "--");
        IS_PAUSE = !IS_PAUSE;
        if(IS_PAUSE) {
            pauseButton.setZIndex(1);
            playButton.setZIndex(0);
        } else {
            pauseButton.setZIndex(0);
            playButton.setZIndex(1);
        }
        gameField.setGamePause(IS_PAUSE);
    }

    public boolean pan(float x, float y, float deltaX, float deltaY) {
//        Gdx.app.log("CreepsRoulette::pan(" + x + "," + y + "," + deltaX + "," + deltaY + ")", "--");
        if (showCreepsSelector && 0 < x && x < creepsSelectorButton.getX()+creepsSelectorButton.getWidth()) {
//            Gdx.app.log("CreepsRoulette::pan()", "-- creepsButtonGroup.getY():" + creepsButtonGroup.getY() + " creepsButtonGroup.getOriginY():" + creepsButtonGroup.getOriginY());
//            Gdx.app.log("CreepsRoulette::pan()", "-- creepsButtonGroup.getHeight()():" + creepsButtonGroup.getHeight());
//            Gdx.app.log("CreepsRoulette::pan()", "-- creepsSelectorButton.getHeight():" + creepsSelectorButton.getHeight());
            if(deltaY < 0f && creepsButtonGroup.getY() <= 0) {
                creepsButtonGroup.moveBy(0, +Math.abs(deltaY));
            } else if(deltaY > 0f && creepsButtonGroup.getY()+creepsButtonGroup.getHeight() >= creepsButtonGroup.getOriginY()+creepsSelectorButton.getHeight()*4) { // Magic number!!
                creepsButtonGroup.moveBy(0, -Math.abs(deltaY));
            }
            return true;
        }
        return false;
    }

    public boolean isButtonTouched(float x, float y) {
        Gdx.app.log("CreepsRoulette::isButtonTouched(" + x + "," + y + ")", "--");
        y = Gdx.graphics.getHeight() - y;
        if(playButton.getX() <= x && x <= playButton.getX()+playButton.getWidth() && playButton.getY() <= y && y <= playButton.getY()+playButton.getHeight()) {
            changeGameState();
            return true;
        }
//        Gdx.app.log("CreepsRoulette::isButtonTouched()", "-- creepsSelectorButton.isTransform()" + creepsSelectorButton.isTransform());
//        Gdx.app.log("CreepsRoulette::isButtonTouched()", "-- creepsSelectorButton.isTouchable()" + creepsSelectorButton.isTouchable());
//        Gdx.app.log("CreepsRoulette::isButtonTouched()", "-- creepsSelectorButton.isDisabled()" + creepsSelectorButton.isDisabled());
//        Gdx.app.log("CreepsRoulette::isButtonTouched()", "-- creepsSelectorButton.isVisible()" + creepsSelectorButton.isVisible());
//        Gdx.app.log("CreepsRoulette::isButtonTouched()", "-- creepsSelectorButton.isChecked()" + creepsSelectorButton.isChecked());
//        Gdx.app.log("CreepsRoulette::isButtonTouched()", "-- creepsSelectorButton.isPressed()" + creepsSelectorButton.isPressed());
//        Gdx.app.log("CreepsRoulette::isButtonTouched()", "-- creepsSelectorButton.isOver()" + creepsSelectorButton.isOver());
        if(creepsSelectorButton.isPressed() || (Gdx.app.getType() == Application.ApplicationType.Android && creepsSelectorButton.isChecked())) {
            Gdx.app.log("CreepsRoulette::isButtonTouched()", "-- creepsSelectorButton.isPressed()");
            showCreepsSelector = !showCreepsSelector;
            for (Actor actor : creepsButtonGroup.getChildren()) {
                actor.setVisible(showCreepsSelector);
            }
            return true;
        }
        for (Actor actor : creepsButtonGroup.getChildren()) {
//            Gdx.app.log("CreepsRoulette::isButtonTouched()", "-- actor:" + actor);
            if (actor instanceof ImageButton) {
                ImageButton imageButton = (ImageButton)actor;
//                Gdx.app.log("CreepsRoulette::isButtonTouched()", "-- imageButton.isPressed():" + imageButton.isPressed());
                if(imageButton.isPressed()) {
                    Integer unitIndex = (Integer) imageButton.getUserObject();
                    if (unitIndex != null) {
                        Gdx.app.log("CreepsRoulette::isButtonTouched()", "-- unitIndex:" + unitIndex);
                        gameField.spawnCreepFromUser(templateForUnits.get(unitIndex));
                    }
                    return true;
                }
            }
        }
//        Actor actor = creepsButtonGroup.hit(x, y, true);
//        if(actor != null) {
//            Gdx.app.log("CreepsRoulette::isButtonTouched()", "-- actor:" + actor);
//            Actor parentActor = actor.getParent();
//            if(parentActor != null) {
//                Gdx.app.log("CreepsRoulette::isButtonTouched()", "-- parentActor:" + parentActor);
//                Integer unitIndex = (Integer) parentActor.getUserObject();
//                if (unitIndex != null) {
//                    Gdx.app.log("CreepsRoulette::isButtonTouched()", "-- unitIndex:" + unitIndex);
//                    gameField.spawnCreepFromUser(templateForUnits.get(unitIndex));
//                }
//            }
//            return true;
//        }
        return false;
    }

    @Override
    public List<Group> getGroup() {
        return Arrays.asList(creepsButtonGroup);
    }
}
