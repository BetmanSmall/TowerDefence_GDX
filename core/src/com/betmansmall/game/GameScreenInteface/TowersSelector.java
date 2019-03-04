package com.betmansmall.game.GameScreenInteface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.gameLogic.GameField;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;

public class TowersSelector extends Table /*implements GestureDetector.GestureListener*/ {
    private float sectionWidth;
    private float sectionHeight;

    // Смещение контейнера sections
    private float amountX = 0;
    // Направление движения секций
    private int transmission   = 0;
    private float stopSection  = 0;
    private float speed        = 1500;

    private int currentSection = 1;
    // Скорость пиксель/секунда после которой считаем, что пользователь хочет перейти к следующей секции
    private float flingSpeed   = 1000;
    private float overscrollDistance = 500;
    private boolean isPanning;

    public GameField gameField;
    public BitmapFont bitmapFont;
    public Array<TemplateForTower> templateForTowers;

    public TowersSelector(GameField gameField, BitmapFont bitmapFont, Skin skin) {
        this.gameField = gameField;
        this.bitmapFont = bitmapFont;
//        this.table = table;

        sectionWidth = Gdx.app.getGraphics().getHeight() * 0.2f;
        sectionHeight = sectionWidth;

        templateForTowers = gameField.factionsManager.getAllTemplateForTowers();
        Gdx.app.log("TowersSelector::TowersSelector()", "-- templateForTowers:" + templateForTowers);

        for (int towerIndex = 0; towerIndex < templateForTowers.size; towerIndex++) {
            TemplateForTower templateForTower = templateForTowers.get(towerIndex);
            String nameTower = templateForTower.name;
            String attackTower = templateForTower.damage.toString();
            String radiusDetectionTower = templateForTower.radiusDetection.toString();
            String costTower = templateForTower.cost.toString();
            Label nameTowerLabel = new Label(nameTower, new Label.LabelStyle(bitmapFont, Color.WHITE));
            Label attackTowerLabel = new Label(attackTower, new Label.LabelStyle(bitmapFont, Color.RED));
            Label radiusDetectionTowerLabel = new Label(radiusDetectionTower, new Label.LabelStyle(bitmapFont, Color.GREEN));
            Label costTowerLabel = new Label(costTower, new Label.LabelStyle(bitmapFont, Color.YELLOW));
            nameTowerLabel.setName("nameTowerLabel");
            attackTowerLabel.setName("attackTowerLabel");
            radiusDetectionTowerLabel.setName("radiusDetectionTowerLabel");
            costTowerLabel.setName("costTowerLabel");

            Table towerTable = new Table();

            towerTable.add(nameTowerLabel).colspan(2).row();
            Image templateButton = new Image(templateForTower.idleTile.getTextureRegion());
            towerTable.add(templateButton).expand();

            Table tableWithCharacteristics = new Table();
            tableWithCharacteristics.add(attackTowerLabel).row();
            tableWithCharacteristics.add(radiusDetectionTowerLabel).row();
            tableWithCharacteristics.add(costTowerLabel).row();
            towerTable.add(tableWithCharacteristics).expandY().right();

            Button button = new Button(towerTable, skin);
            button.setUserObject(towerIndex);
            add(button).expand().fill().minHeight(Gdx.graphics.getHeight()*0.3f).row();
        }
    }

//    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        Gdx.app.log("TowersSelector::pan(" + x + "," + y + "," + deltaX + "," + deltaY + ")", "--");
        float groupX = getX();
        float groupY = getY();
        float groupWidth = getWidth();
        float groupHeight = getHeight();
        float groupPrefWidth = getPrefWidth();
        float groupPrefHeight = getPrefHeight();
        float tableWidth = getParent().getWidth();
        float tableHeight = getParent().getHeight();
//        Gdx.app.log("TowersSelector::isPanning()", "-- groupX:" + groupX + " groupY:" + groupY + " groupWidth:" + groupWidth + " groupHeight:" + groupHeight);
//        Gdx.app.log("TowersSelector::isPanning()", "-- groupPrefWidth:" + groupPrefWidth + " groupPrefHeight:" + groupPrefHeight + " tableWidth:" + tableWidth + " tableHeight:" + tableHeight);
//        Gdx.app.log("TowersSelector::isPanning()", "-- Gdx.graphics.getWidth():" + Gdx.graphics.getWidth() + " Gdx.graphics.getHeight():" + Gdx.graphics.getHeight());
//        Gdx.app.log("TowersSelector::isPanning()", "-- table.getStage().getViewport().getScreenWidth():" + table.getStage().getViewport().getScreenWidth());
//        Gdx.app.log("TowersSelector::isPanning()", "-- table.getStage().getViewport().getScreenHeight():" + table.getStage().getViewport().getScreenHeight());
//        Gdx.app.log("TowersSelector::isPanning()", "-- table.getStage().getViewport().getWorldWidth():" + table.getStage().getViewport().getWorldWidth());
//        Gdx.app.log("TowersSelector::isPanning()", "-- table.getStage().getViewport().getWorldHeight():" + table.getStage().getViewport().getWorldHeight());
//        Gdx.app.log("TowersSelector::isPanning()", "-- table.getWidth():" + table.getWidth() + " table.getHeight():" + table.getHeight());
        if (Math.abs(deltaX) > Math.abs(deltaY) && !isPanning) {
            if (x >= (tableWidth-groupWidth/**2f*/) && deltaX > 0) {
                moveBy(deltaX, 0);
                if(getX() > tableWidth) {
                    setX(tableWidth);
                    gameField.cancelUnderConstruction();
                }
                isPanning = false;
                return true;
            } else if (x >= (tableWidth-groupWidth) && deltaX < 0) {
                moveBy(deltaX, 0);
                if(getX() < tableWidth) {
                    setX(tableWidth-groupWidth);
                }
                isPanning = true;
                return true;
            }
        } else if (x >= (tableWidth-groupWidth) || isPanning) {
//            isPanning = true;
            if (deltaX < 0) {
                moveBy(0, -deltaY);
            } else if (deltaX > 0) {
                moveBy(0, -deltaY);
            }
            if (getY() > 0) {
                setY(0);
            } else if(getY()+ getHeight() < tableHeight) {
                setY( (0-(getHeight()-tableHeight)) );
            }
            return true;
        }

//        Gdx.app.log("SlidingTable::isPanning()", "-- x:" + x + " y:" + y + " deltaX:" + deltaX + " deltaY:" + deltaY);
//        if ( amountX < -overscrollDistance ) {
//            return false;
//        }
//        if ( amountX > (getChildren().size - 1) * sectionWidth + overscrollDistance) {
//            return false;
//        }
//
//        isPanning = true;
//        amountX -= deltaX;
//        cancelTouchFocusedChild();
        return false;
    }

//    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        Gdx.app.log("TowersSelector::panStop()", "-- x:" + x + " y:" + y + " pointer:" + pointer + " button:" + button);
        isPanning = false;
        return false;
    }

//    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        Gdx.app.log("SlidingTable::fling()", "-- velocityX:" + velocityX + " velocityY:" + velocityY);
//        if ( Math.abs(velocityX) > flingSpeed ) {
//            if ( velocityX > 0 ) {
//                setStopSection(currentSection - 2);
//            } else {
//                setStopSection(currentSection);
//            }
//        }
//        cancelTouchFocusedChild();
        return false;
    }

//    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        Gdx.app.log("TowersSelector::touchDown()", "-- x:" + x + " y:" + y + " pointer:" + pointer + " button:" + button);
//        if ( event.getTarget().getClass() == LevelIcon.class ) {
//            touchFocusedChild = event.getTarget();
//        }

        for (Actor actor : getChildren()) {
//            Gdx.app.log("TowersSelector::touchDown()", "-- actor:" + actor);
            if (actor instanceof Button) {
                Button buttonActor = (Button)actor;
//                Gdx.app.log("TowersSelector::touchDown()", "-- buttonActor.isPressed():" + buttonActor.isPressed());
                if(buttonActor.isPressed()) {
                    Integer towerIndex = (Integer) buttonActor.getUserObject();
                    if (towerIndex != null) {
                        Gdx.app.log("TowersSelector::touchDown()", "-- towerIndex:" + towerIndex);
                        gameField.createdUnderConstruction(templateForTowers.get(towerIndex));
                    }
//                    Gdx.app.log("TowersSelector::touchDown()", "-- return true");
                    return true;
                }
            }
        }
//        Gdx.app.log("TowersSelector::touchDown()", "-- return false");
        return false;
    }

//    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

//    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

//    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

//    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

//    @Override
    public void pinchStop() {

    }

    public int getSectionsCount() {
        return getChildren().size;
    }

    // Вычисление текущей секции на основании смещения контейнера sections
    public int calculateCurrentSection() {
        // Текущая секция = (Текущее смещение / длинну секции) + 1, т.к наши секции нумеруются с 1
        int section = Math.round( amountX / sectionWidth ) + 1;
        //Проверяем адекватность полученного значения, вхождение в интервал [1, количество секций]
        if ( section > getChildren().size ) return getChildren().size;
        if ( section < 1 ) return 1;
        return section;
    }

    public void setStopSection(int stoplineSection) {
        if ( stoplineSection < 0 ) {
            stoplineSection = 0;
        }
        if ( stoplineSection > this.getSectionsCount() - 1 ) {
            stoplineSection = this.getSectionsCount() - 1;
        }

        stopSection = stoplineSection * sectionWidth;

        // Определяем направление движения
        // transmission ==  1 - вправо
        // transmission == -1 - влево
        if ( amountX < stopSection) {
            transmission = 1;
        } else {
            transmission = -1;
        }
    }

    private void move(float delta) {
        // Определяем направление смещения
        if ( amountX < stopSection) {
            // Двигаемся вправо
            // Если попали сюда, а при этом должны были двигаться влево
            // значит пора остановиться
            if ( transmission == -1 ) {
                amountX = stopSection;
                // Фиксируем номер текущей секции
                currentSection = calculateCurrentSection();
                return;
            }
            // Смещаем
            amountX += speed * delta;
        } else if( amountX > stopSection) {
            if ( transmission == 1 ) {
                amountX = stopSection;
                currentSection = calculateCurrentSection();
                return;
            }
            amountX -= speed * delta;
        }
    }

    @Override
    public void act (float delta) {
        // Смещаем контейнер с секциями
//        setX( -amountX );

//        cullingArea.set( -sections.getX() + 50, sections.getY(), sectionWidth - 100, sections.getHeight() );
//        sections.setCullingArea(cullingArea);

        // Если водим пальцем по экрану
//        if (this.isPanning) {
//        if ( actorGestureListener.getGestureDetector().isPanning() ) {
            // Устанавливаем границу, к которой будем анимировать движение
//             граница = номер предыдущей секции
//            setStopSection(calculateCurrentSection() - 1);
//        } else {
            // Если палец далеко от экрана - анимируем движение в заданную точку
//            move( delta );
//        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

//        for (int i=1; i<= getSectionsCount(); i++) {
//            if ( i == calculateCurrentSection() ) {
//                batch.draw( naviActive, Gdx.app.getGraphics().getWidth()/2 - getSectionsCount()*20/2 + i*20 , 50);
//            } else {
//                batch.draw( naviPassive, Gdx.app.getGraphics().getWidth()/2 - getSectionsCount()*20/2 + i*20 , 50);
//            }
//        }
    }

//    void cancelTouchFocusedChild () {
//        if (touchFocusedChild == null) {
//            return;
//        }
//        try {
//            this.getStage().cancelTouchFocus(touchFocusedChild);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        touchFocusedChild = null;
//    }

//    public void addWidget(Actor widget) {
//        widget.setX( this.sections.getChildren().size * sectionWidth);
//        widget.setY( 0 );
//        widget.setWidth( sectionWidth );
//        widget.setHeight( Gdx.graphics.getHeight() );
//        sections.addActor( widget );
//    }

//    @Override
    public void resize(float width, float height) {
        Gdx.app.log("TowersSelector::resize()", "-- width:" + width + " height:" + height);
//        sectionWidth = width;
//        sectionHeight = height;
//        Array<Actor> array = getChildren();
//        for (int a = 0; a < array.size; a++) {
//            Actor actor = array.get(a);
//            actor.setWidth(sectionWidth);
//            actor.setHeight(sectionHeight);
//            actor.setX(a * sectionWidth);
//            if (actor instanceof Table) {
//                Table table = (Table)actor;
//                Array<Actor> children = table.getChildren();
//                for (Actor child : children) {
//                    if (child instanceof Table) {
//                        child.setWidth(sectionWidth);
//                        child.setHeight(sectionHeight);
//                    }
//                }
//            }
//        }
//        setStopSection(calculateCurrentSection() - 1);
    }

    public boolean scrolled(int amount) {
        Gdx.app.log("TowersSelector::scrolled()", "-- amount:" + amount);
//        float groupWidth = getWidth();
//        float tableWidth = tablegetWidth();
//        if (Gdx.input.getX() >= (tableWidth-groupWidth)) {
//            moveBy(0, amount*10f);
//            return true;
//        }
        return false;
    }
}
