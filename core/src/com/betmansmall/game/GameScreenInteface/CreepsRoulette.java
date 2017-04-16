package com.betmansmall.game.GameScreenInteface;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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
    private Array<ImageButton> imageButtons;
    private ImageButton creepsSelectorButton;
    private ImageButton playButton;
    private ImageButton pauseButton;
    private static volatile Boolean IS_PAUSE = true;

    public CreepsRoulette(GameField gameField, BitmapFont bitmapFont, Stage stage) {
        this.gameField = gameField;
        this.bitmapFont = bitmapFont;
        this.stage = stage;
        init();
    }

    private void init() {
        templateForUnits = gameField.getAllTemplateForUnits();
        buttonGroup = new Group();
        imageButtons = new Array<ImageButton>();

        creepsSelectorButton = new ImageButton(new Image(new Texture(Gdx.files.internal("img/creep_roulette_main.png"))).getDrawable());
        creepsSelectorButton.setSize(getLocalWidth(ROULETTE_RADIUS),getLocalHeight(ROULETTE_RADIUS));
        creepsSelectorButton.setPosition(0, 0);
        stage.addActor(creepsSelectorButton);

        playButton = new ImageButton(new Image(new Texture(Gdx.files.internal("img/playbutton.png"))).getDrawable());
        playButton.setSize(getLocalWidth(ROULETTE_RADIUS),getLocalHeight(ROULETTE_RADIUS));
        playButton.setPosition(getLocalWidth(ROULETTE_RADIUS), 0);
        stage.addActor(playButton);

        pauseButton = new ImageButton(new Image(new Texture(Gdx.files.internal("img/pausebutton.png"))).getDrawable());
        pauseButton.setSize(getLocalWidth(ROULETTE_RADIUS),getLocalHeight(ROULETTE_RADIUS));
        pauseButton.setPosition(getLocalWidth(ROULETTE_RADIUS), 0);
        pauseButton.setVisible(true);
        stage.addActor(pauseButton);

        for(int unitIndex = 0; unitIndex < templateForUnits.size; unitIndex++) {
            TemplateForUnit templateForUnit = templateForUnits.get(unitIndex);

            ImageButton templateButton = new ImageButton(new Image(templateForUnit.animations.values().next().getTextureRegion()).getDrawable());
            ImageButton templateFrame = new ImageButton(new Image(new Texture(Gdx.files.internal("img/build_frame.png"))).getDrawable());
            templateButton.setSize(getLocalWidth(ROULETTE_RADIUS)/1.5f, getLocalHeight(ROULETTE_RADIUS)/1.5f);
            templateButton.setPosition(creepsSelectorButton.getX(), creepsSelectorButton.getY()
                    + (getLocalWidth(ROULETTE_RADIUS) + unitIndex * getLocalWidth(ROULETTE_RADIUS)/1.5f));
            templateFrame.setSize(getLocalWidth(ROULETTE_RADIUS), getLocalHeight(ROULETTE_RADIUS)/1.5f);
            templateFrame.setPosition(creepsSelectorButton.getX(), creepsSelectorButton.getY()
                    + (getLocalWidth(ROULETTE_RADIUS) + unitIndex * getLocalWidth(ROULETTE_RADIUS)/1.5f));
            templateButton.setVisible(false);
            templateFrame.setVisible(false);

            buttonGroup.addActor(templateButton);
            buttonGroup.addActor(templateFrame);
            imageButtons.add(templateButton);
            imageButtons.add(templateFrame);

            String nameTower = templateForUnit.name;
            String attackTower = templateForUnit.healthPoints.toString();
            String radiusDetectionTower = templateForUnit.speed.toString();
            String costTower = templateForUnit.cost.toString();

            Label nameLabel = new Label(nameTower, new Label.LabelStyle(bitmapFont, Color.WHITE));
            Label attackLabel = new Label(attackTower, new Label.LabelStyle(bitmapFont, Color.RED));
            Label radiusDetectionLabel = new Label(radiusDetectionTower, new Label.LabelStyle(bitmapFont, Color.GREEN));
            Label costLabel = new Label(costTower, new Label.LabelStyle(bitmapFont, Color.YELLOW));
            float textX = Gdx.graphics.getWidth()+30f;
            float textY = pauseButton.getY() + (getLocalWidth(ROULETTE_RADIUS) + unitIndex * getLocalWidth(ROULETTE_RADIUS)/1.5f); // 4to za pizdec? AndreY??? NAXUI
            nameLabel.setPosition(textX+160, textY+30); // Magic number
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

            Gdx.app.log("CreepsRoulette::init()", "-- button pos:(" + templateButton.getX() + "," + templateButton.getY() + "):" + nameTower);
        }
        Gdx.app.log("CreepsRoulette::init()", "-- templateForUnits.size:" + templateForUnits.size);
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
        for (Actor actor : buttonGroup.getChildren()) {
            actor.setVisible(!IS_PAUSE);
        }
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
        return Arrays.asList(buttonGroup);
    }
}
