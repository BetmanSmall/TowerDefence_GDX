package com.betmansmall.game.GameScreenInteface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.gameLogic.GameField;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;

/**
 * Created by Дима Цыкунов on 20.02.2016.
 */
public class CreepsRoulette {
    public GameField gameField;
    public BitmapFont bitmapFont;
    public Table tableFront;

    public Array<TemplateForUnit> templateForUnits;
    private VerticalGroup verticalGroupWithCreeps;
//    private boolean showCreepsSelector;
//    private ImageButton creepsSelectorButton;
//    private ImageButton playButton;
//    private ImageButton pauseButton;
//    private static volatile Boolean IS_PAUSE = true; // Prosto pizdec ANDREY!!!1!!!!!!

    public CreepsRoulette(GameField gameField, BitmapFont bitmapFont, Table tableFront) {
        this.gameField = gameField;
        this.bitmapFont = bitmapFont;
        this.tableFront = tableFront;

        this.templateForUnits = gameField.getAllTemplateForUnits();
//<<<<<<< HEAD
        Gdx.app.log("CreepsRoulette::CreepsRoulette()", "-- templateForUnits:" + templateForUnits);
        this.verticalGroupWithCreeps = new VerticalGroup();
        tableFront.add(verticalGroupWithCreeps).left().bottom().expand();
//        verticalGroupWithCreeps.addListener(new Pan)

//        showCreepsSelector = false;
//        creepsSelectorButton = new ImageButton(new Image(new Texture(Gdx.files.internal("img/creep_roulette_main.png"))).getDrawable());
////        creepsSelectorButton.setSize(getLocalWidth(ROULETTE_RADIUS),getLocalHeight(ROULETTE_RADIUS));
//        creepsSelectorButton.setPosition(0, 0);
//        creepsSelectorButton.setZIndex(5); // и это тоже!=(
//        stage.addActor(creepsSelectorButton);
//=======
//        this.creepsButtonGroup = new Group();
//        creepsButtonGroup.setZIndex(1); // блять это гавно не пашет
//        creepsButtonGroup.setDebug(true, true);
//
//        showCreepsSelector = false;
//        creepsSelectorButton = new ImageButton(new Image(new Texture(Gdx.files.internal("img/creep_roulette_main.png"))).getDrawable());
//       creepsSelectorButton.setSize(128f, 128f);
//        creepsSelectorButton.setPosition(0, 0);
//        creepsSelectorButton.setZIndex(5); // и это тоже!=(
//        stage.addActor(creepsSelectorButton);
//>>>>>>> origin/master

//        playButton = new ImageButton(new Image(new Texture(Gdx.files.internal("img/playbutton.png"))).getDrawable());
////        playButton.setSize(getLocalWidth(ROULETTE_RADIUS),getLocalHeight(ROULETTE_RADIUS));
////        playButton.setPosition(getLocalWidth(ROULETTE_RADIUS), 0);
//        playButton.setZIndex(1);
//        stage.addActor(playButton);
//
//        pauseButton = new ImageButton(new Image(new Texture(Gdx.files.internal("img/pausebutton.png"))).getDrawable());
////        pauseButton.setSize(getLocalWidth(ROULETTE_RADIUS),getLocalHeight(ROULETTE_RADIUS));
////        pauseButton.setPosition(getLocalWidth(ROULETTE_RADIUS), 0);
//        playButton.setZIndex(0);
//        stage.addActor(pauseButton);

//<<<<<<< HEAD
//        TextureRegion textureRegionFrame = new TextureRegion(new Texture(Gdx.files.internal("img/build_frame.png")));
//        textureRegionFrame.flip(true, false);
//        Image imageFrame = new Image(textureRegionFrame);
        for (int unitIndex = 0; unitIndex < templateForUnits.size; unitIndex++) {
//            ImageButton creepFrameButton = new ImageButton(imageFrame.getDrawable());
//            creepFrameButton.setSize(getLocalWidth(ROULETTE_RADIUS), getLocalHeight(ROULETTE_RADIUS)/1.5f); // 4to za pizdec? AndreY??? NAXUI
//            creepFrameButton.setPosition(creepsSelectorButton.getX(), creepsSelectorButton.getY()
//                    + (getLocalWidth(ROULETTE_RADIUS) + unitIndex * getLocalWidth(ROULETTE_RADIUS)/1.5f)); // 4to za pizdec? AndreY??? NAXUI
//            creepFrameButton.addListener(new ClickListener() {
//                @Override
//                public void clicked(InputEvent event, float x, float y) {
//                    Gdx.app.log("CreepsRoulette::ClickListener::clicked(" + event + "," + x + "," + y + ")", "-- creepFrameButton");
//                }
//            });
//=======
//        TextureRegion textureRegionFrame = new TextureRegion(new Texture(Gdx.files.internal("img/build_frame.png")));
//        textureRegionFrame.flip(true, false);
//        Image imageFrame = new Image(textureRegionFrame);
//        for(int unitIndex = 0; unitIndex < templateForUnits.size; unitIndex++) {
//            ImageButton creepFrameButton = new ImageButton(imageFrame.getDrawable());
//           creepFrameButton.setSize(128f, 128f/1.5f); // 4to za pizdec? AndreY??? NAXUI
//           creepFrameButton.setPosition(creepsSelectorButton.getX(), creepsSelectorButton.getY()
//                   + (128f + unitIndex * 128f/1.5f)); // 4to za pizdec? AndreY??? NAXUI
//           // creepFrameButton.addListener(new ClickListener() {
//               // @Override
//               // public void clicked(InputEvent event, float x, float y) {
//                   // Gdx.app.log("CreepsRoulette::ClickListener::clicked(" + event + "," + x + "," + y + ")", "-- creepFrameButton");
//               // }
//           // });
//>>>>>>> origin/master
            TemplateForUnit templateForUnit = templateForUnits.get(unitIndex);
            String nameUnit = templateForUnit.name;
            String hpUnit = templateForUnit.healthPoints.toString();
            String speedUnit = templateForUnit.speed.toString();
            String costUnit = templateForUnit.cost.toString();
            Label nameUnitLabel = new Label(nameUnit, new Label.LabelStyle(bitmapFont, Color.WHITE));
            Label hpUnitLabel = new Label(hpUnit, new Label.LabelStyle(bitmapFont, Color.RED));
            Label speedUnitLabel = new Label(speedUnit, new Label.LabelStyle(bitmapFont, Color.GREEN));
            Label costUnitLabel = new Label(costUnit, new Label.LabelStyle(bitmapFont, Color.YELLOW));
            nameUnitLabel.setName("nameUnitLabel");
            hpUnitLabel.setName("hpUnitLabel");
            speedUnitLabel.setName("speedUnitLabel");
            costUnitLabel.setName("costUnitLabel");

            Table table = new Table();
//            table.setDebug(true);

            table.add(nameUnitLabel).colspan(2).row();
            VerticalGroup verticalGroupHar = new VerticalGroup();
//            verticalGroupHar.left();
            verticalGroupHar.addActor(hpUnitLabel);
            verticalGroupHar.addActor(speedUnitLabel);
            verticalGroupHar.addActor(costUnitLabel);
            table.add(verticalGroupHar).expandY().left();

            Image templateButton = new Image(templateForUnit.animations.values().toArray().get(6).getTextureRegion());
            table.add(templateButton).expand();

            Button button = new Button(table, tableFront.getSkin());
//            button.addActor(table);
            button.setUserObject(unitIndex);
            verticalGroupWithCreeps.addActor(button);
//
//            templateButton.setName("templateButton");
//            creepFrameButton.setName("creepFrameButton");
//            creepFrameButton.addActor(templateButton);
////            creepFrameButton.setVisible(false);
////            creepFrameButton.setZIndex(1); // да ну впзду
//            verticalGroupWithCreeps.addActor(creepFrameButton);
////            verticalGroupWithCreeps.setHeight(verticalGroupWithCreeps.getHeight()+creepFrameButton.getHeight());
//
//            Gdx.app.log("CreepsRoulette::init()", "-- button pos:(" + creepFrameButton.getX() + "," + creepFrameButton.getY() + "):" + nameUnit);
        }
//        int buttonsSize = verticalGroupWithCreeps.getChildren().size;
//        for(Actor actor : verticalGroupWithCreeps.getChildren()) {
//            Gdx.app.log("CreepsRoulette::init()", "-- actor:" + actor/* + " actor.getName():" + actor.getName() + " actor.getUserObject():" + actor.getUserObject()*/);
//        }
//        Gdx.app.log("CreepsRoulette::init()", "-- buttonsSize:" + buttonsSize);
//        Gdx.app.log("CreepsRoulette::init()", "-- templateForUnits.size:" + templateForUnits.size);
    }

//    public void changeGameState() {
//        Gdx.app.log("CreepsRoulette::changeGameState()", "--");
//        IS_PAUSE = !IS_PAUSE;
//        if(IS_PAUSE) {
//            pauseButton.setZIndex(1);
//            playButton.setZIndex(0);
//        } else {
//            pauseButton.setZIndex(0);
//            playButton.setZIndex(1);
//        }
//        gameField.setGamePause(IS_PAUSE);
//    }

    public boolean pan(float x, float y, float deltaX, float deltaY) {
        Gdx.app.log("CreepsRoulette::pan(" + x + "," + y + "," + deltaX + "," + deltaY + ")", "--");
        if(x >= 0 && x <= verticalGroupWithCreeps.getWidth()) {
            if(deltaY < 0f && verticalGroupWithCreeps.getY() < 0) {
                verticalGroupWithCreeps.moveBy(0, +Math.abs(deltaY));
            } else if(deltaY > 0f && verticalGroupWithCreeps.getY() + verticalGroupWithCreeps.getHeight() > Gdx.graphics.getHeight()) {
                verticalGroupWithCreeps.moveBy(0, -Math.abs(deltaY));
            }
            return true;
        }
        return false;
    }

    public boolean tap(float x, float y, int count, int button) {
        Gdx.app.log("CreepsRoulette::tap(" + x + "," + y + "," + count + "," + button + ")", "--");
        y = Gdx.graphics.getHeight() - y;
//        if(playButton.getX() <= x && x <= playButton.getX()+playButton.getWidth() && playButton.getY() <= y && y <= playButton.getY()+playButton.getHeight()) {
//            changeGameState();
//            return true;
//        }
//        Gdx.app.log("CreepsRoulette::tap()", "-- creepsSelectorButton.isTransform()" + creepsSelectorButton.isTransform());
//        Gdx.app.log("CreepsRoulette::tap()", "-- creepsSelectorButton.isTouchable()" + creepsSelectorButton.isTouchable());
//        Gdx.app.log("CreepsRoulette::tap()", "-- creepsSelectorButton.isDisabled()" + creepsSelectorButton.isDisabled());
//        Gdx.app.log("CreepsRoulette::tap()", "-- creepsSelectorButton.isVisible()" + creepsSelectorButton.isVisible());
//        Gdx.app.log("CreepsRoulette::tap()", "-- creepsSelectorButton.isChecked()" + creepsSelectorButton.isChecked());
//        Gdx.app.log("CreepsRoulette::tap()", "-- creepsSelectorButton.isPressed()" + creepsSelectorButton.isPressed());
//        Gdx.app.log("CreepsRoulette::tap()", "-- creepsSelectorButton.isOver()" + creepsSelectorButton.isOver());
//        if(creepsSelectorButton.isPressed() || (Gdx.app.getType() == Application.ApplicationType.Android && creepsSelectorButton.isChecked())) {
//            Gdx.app.log("CreepsRoulette::tap()", "-- creepsSelectorButton.isPressed()");
//            showCreepsSelector = !showCreepsSelector;
//            for (Actor actor : verticalGroupWithCreeps.getChildren()) {
//                actor.setVisible(showCreepsSelector);
//            }
//            return true;
//        }
        for (Actor actor : verticalGroupWithCreeps.getChildren()) {
            Gdx.app.log("CreepsRoulette::tap()", "-- actor:" + actor);
            if (actor instanceof Button) {
                Button buttonActor = (Button)actor;
                Gdx.app.log("CreepsRoulette::tap()", "-- buttonActor.isPressed():" + buttonActor.isPressed());
                if(buttonActor.isPressed()) {
                    Integer unitIndex = (Integer) buttonActor.getUserObject();
                    if (unitIndex != null) {
                        Gdx.app.log("CreepsRoulette::tap()", "-- unitIndex:" + unitIndex);
                        gameField.spawnCreepFromUser(templateForUnits.get(unitIndex));
                    }
                    return true;
                }
            }
        }
//        Actor actor = verticalGroupWithCreeps.hit(x, y, true);
//        if(actor != null) {
//            Gdx.app.log("CreepsRoulette::tap()", "-- actor:" + actor);
//            Actor parentActor = actor.getParent();
//            if(parentActor != null) {
//                Gdx.app.log("CreepsRoulette::tap()", "-- parentActor:" + parentActor);
//                Integer unitIndex = (Integer) parentActor.getUserObject();
//                if (unitIndex != null) {
//                    Gdx.app.log("CreepsRoulette::tap()", "-- unitIndex:" + unitIndex);
//                    gameField.spawnCreepFromUser(templateForUnits.get(unitIndex));
//                }
//            }
//            return true;
//        }
        return false;
    }
}
