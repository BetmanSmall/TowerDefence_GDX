package com.betmansmall.game.GameScreenInteface;

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
    private Group buttonGroup;
    private ImageButton creepsSelectorButton;
    private ImageButton playButton;
    private ImageButton pauseButton;
    private static volatile Boolean IS_PAUSE = true; // Prosto pizdec ANDREY!!!!!!!!!
    private boolean showCreepsSelector;

    public CreepsRoulette(final GameField gameField, BitmapFont bitmapFont, Stage stage) {
        this.gameField = gameField;
        this.bitmapFont = bitmapFont;
        this.stage = stage;

        this.templateForUnits = gameField.getAllTemplateForUnits();
        this.buttonGroup = new Group();

        showCreepsSelector = false;
        creepsSelectorButton = new ImageButton(new Image(new Texture(Gdx.files.internal("img/creep_roulette_main.png"))).getDrawable());
        creepsSelectorButton.setSize(getLocalWidth(ROULETTE_RADIUS),getLocalHeight(ROULETTE_RADIUS));
        creepsSelectorButton.setPosition(0, 0);
//        creepsSelectorButton.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                Gdx.app.log("CreepsRoulette::ClickListener::clicked(" + event + "," + x + "," + y + ")", "-- creepsSelectorButton");
//                showCreepsSelector = !showCreepsSelector;
//                for (Actor actor : buttonGroup.getChildren()) {
//                    actor.setVisible(showCreepsSelector);
//                }
//            }
//        });
        stage.addActor(creepsSelectorButton);

        playButton = new ImageButton(new Image(new Texture(Gdx.files.internal("img/playbutton.png"))).getDrawable());
        playButton.setSize(getLocalWidth(ROULETTE_RADIUS),getLocalHeight(ROULETTE_RADIUS));
        playButton.setPosition(getLocalWidth(ROULETTE_RADIUS), 0);
//        playButton.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                Gdx.app.log("CreepsRoulette::ClickListener::clicked(" + event + "," + x + "," + y + ")", "-- playButton");
//            }
//        });
        stage.addActor(playButton);

        pauseButton = new ImageButton(new Image(new Texture(Gdx.files.internal("img/pausebutton.png"))).getDrawable());
        pauseButton.setSize(getLocalWidth(ROULETTE_RADIUS),getLocalHeight(ROULETTE_RADIUS));
        pauseButton.setPosition(getLocalWidth(ROULETTE_RADIUS), 0);
//        pauseButton.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                Gdx.app.log("CreepsRoulette::ClickListener::clicked(" + event + "," + x + "," + y + ")", "-- pauseButton");
//            }
//        });
        stage.addActor(pauseButton);

        TextureRegion textureRegionFrame = new TextureRegion(new Texture(Gdx.files.internal("img/build_frame.png")));
        textureRegionFrame.flip(true, false);
        Image imageFrame = new Image(textureRegionFrame);
        for(int unitIndex = 0; unitIndex < templateForUnits.size; unitIndex++) {
            TemplateForUnit templateForUnit = templateForUnits.get(unitIndex);

            String nameTower = templateForUnit.name;
            String attackTower = templateForUnit.healthPoints.toString();
            String radiusDetectionTower = templateForUnit.speed.toString();
            String costTower = templateForUnit.cost.toString();

            Label nameLabel = new Label(nameTower, new Label.LabelStyle(bitmapFont, Color.WHITE));
            Label attackLabel = new Label(attackTower, new Label.LabelStyle(bitmapFont, Color.RED));
            Label radiusDetectionLabel = new Label(radiusDetectionTower, new Label.LabelStyle(bitmapFont, Color.GREEN));
            Label costLabel = new Label(costTower, new Label.LabelStyle(bitmapFont, Color.YELLOW));
            float textX = 1.0f;
            float textY = creepsSelectorButton.getY() + (getLocalWidth(ROULETTE_RADIUS) + unitIndex * getLocalWidth(ROULETTE_RADIUS)/1.5f); // 4to za pizdec? AndreY??? NAXUI
            nameLabel.setPosition(textX+160, textY+30); // Magic number
            attackLabel.setPosition(textX, textY+60); // tyt tak zavedeno
            radiusDetectionLabel.setPosition(textX, textY+30);
            costLabel.setPosition(textX, textY+10);
            nameLabel.setVisible(false);
            attackLabel.setVisible(false);
            radiusDetectionLabel.setVisible(false);
            costLabel.setVisible(false);

            ImageButton templateFrame = new ImageButton(imageFrame.getDrawable());
            templateFrame.setSize(getLocalWidth(ROULETTE_RADIUS), getLocalHeight(ROULETTE_RADIUS)/1.5f);
            templateFrame.setPosition(creepsSelectorButton.getX(), creepsSelectorButton.getY()
                    + (getLocalWidth(ROULETTE_RADIUS) + unitIndex * getLocalWidth(ROULETTE_RADIUS)/1.5f));
            templateFrame.setVisible(false);
            templateFrame.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.log("CreepsRoulette::ClickListener::clicked(" + event + "," + x + "," + y + ")", "-- templateFrame");
                }
            });

            ImageButton templateButton = new ImageButton(new Image(templateForUnit.animations.values().toArray().get(6).getTextureRegion()).getDrawable());
            templateButton.setSize(getLocalWidth(ROULETTE_RADIUS)/1.5f, getLocalHeight(ROULETTE_RADIUS)/1.5f);
            templateButton.setPosition(creepsSelectorButton.getX() + 40f, creepsSelectorButton.getY()
                    + (getLocalWidth(ROULETTE_RADIUS) + unitIndex * getLocalWidth(ROULETTE_RADIUS)/1.5f));
            templateButton.setVisible(false);

            buttonGroup.addActor(templateButton);
            buttonGroup.addActor(templateFrame);

            buttonGroup.addActor(nameLabel);
            buttonGroup.addActor(attackLabel);
            buttonGroup.addActor(radiusDetectionLabel);
            buttonGroup.addActor(costLabel);

            Gdx.app.log("CreepsRoulette::init()", "-- button pos:(" + templateButton.getX() + "," + templateButton.getY() + "):" + nameTower);
        }
        Gdx.app.log("CreepsRoulette::init()", "-- templateForUnits.size:" + templateForUnits.size);
    }

    public void changeGameState() {
        Gdx.app.log("CreepsRoulette::changeGameState()", "--");
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
        Gdx.app.log("CreepsRoulette::isButtonTouched(" + x + "," + y + ")", "--");
        Gdx.app.log("CreepsRoulette::isButtonTouched()", "-- creepsSelectorButton.isTransform()" + creepsSelectorButton.isTransform());
        Gdx.app.log("CreepsRoulette::isButtonTouched()", "-- creepsSelectorButton.isTouchable()" + creepsSelectorButton.isTouchable());
        Gdx.app.log("CreepsRoulette::isButtonTouched()", "-- creepsSelectorButton.isDisabled()" + creepsSelectorButton.isDisabled());
        Gdx.app.log("CreepsRoulette::isButtonTouched()", "-- creepsSelectorButton.isVisible()" + creepsSelectorButton.isVisible());
        Gdx.app.log("CreepsRoulette::isButtonTouched()", "-- creepsSelectorButton.isChecked()" + creepsSelectorButton.isChecked());
        Gdx.app.log("CreepsRoulette::isButtonTouched()", "-- creepsSelectorButton.isPressed()" + creepsSelectorButton.isPressed());
        Gdx.app.log("CreepsRoulette::isButtonTouched()", "-- creepsSelectorButton.isOver()" + creepsSelectorButton.isOver());
        if(creepsSelectorButton.isPressed()) {
            Gdx.app.log("CreepsRoulette::isButtonTouched()", "-- creepsSelectorButton.isPressed()");
            showCreepsSelector = !showCreepsSelector;
            for (Actor actor1 : buttonGroup.getChildren()) {
                actor1.setVisible(showCreepsSelector);
            }
            return true;
        }
        Actor actor = buttonGroup.hit(x, y, true);
        if(actor != null) {
            Gdx.app.log("CreepsRoulette::isButtonTouched()", "-- actor:" + actor.toString());
            return true;
        }
        y = Gdx.graphics.getHeight() - y;
        if(playButton.getX() <= x && x <= playButton.getX()+playButton.getWidth() &&
                playButton.getY() <= y && y <= playButton.getY()+playButton.getHeight()) {
            changeGameState();
            return true;
        }
        return false;
    }

    @Override
    public List<Group> getGroup() {
        return Arrays.asList(buttonGroup);
    }
}
