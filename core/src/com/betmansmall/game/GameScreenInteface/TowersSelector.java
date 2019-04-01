package com.betmansmall.game.GameScreenInteface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.gameLogic.GameField;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;

public class TowersSelector extends Table implements GestureDetector.GestureListener {
    private float sectionWidth;
    private float sectionHeight;
    private float selectorBorderVertical;
    private float selectorBorderHorizontal;

    // Смещение контейнера sections
    private boolean open = true;
    public float coordinateX = 0;
    public float coordinateY = 0;
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
            Cell<Button> cellButton = this.add(button);//.expand().fill();
            if (gameField.gameSettings.verticalSelector) {
                cellButton.row();//.minHeight(Gdx.graphics.getHeight()*0.2f).row();
            }
        }
        sectionWidth = Gdx.graphics.getHeight()*0.2f;
        sectionHeight = Gdx.graphics.getHeight()*0.2f;
    }

    public void dispose() {
        Gdx.app.log("TowersSelector::dispose()", "--");
    }

//    @Override
    public void resize(int width, int height) {
        Gdx.app.log("TowersSelector::resize()", "-- width:" + width + " height:" + height);
//        setWidth(width);
//        setHeight(height);
        Gdx.app.log("TowersSelector::resize()", "-- getMaxWidth():" + getMaxWidth());
        Gdx.app.log("TowersSelector::resize()", "-- getMaxHeight():" + getMaxHeight());
        Gdx.app.log("TowersSelector::resize()", "-- getWidth():" + getWidth());
        Gdx.app.log("TowersSelector::resize()", "-- getHeight():" + getHeight());
        Gdx.app.log("TowersSelector::resize()", "-- getMinWidth():" + getMinWidth());
        Gdx.app.log("TowersSelector::resize()", "-- getMinHeight():" + getMinHeight());
        Gdx.app.log("TowersSelector::resize()", "-- getPrefWidth():" + getPrefWidth());
        Gdx.app.log("TowersSelector::resize()", "-- getPrefHeight():" + getPrefHeight());
        Gdx.app.log("TowersSelector::resize()", "-- getColumnWidth():" + getColumnWidth(0));
        Gdx.app.log("TowersSelector::resize()", "-- getRowHeight():" + getRowHeight(0));
        sectionWidth = height*0.2f;
        sectionHeight = height*0.2f;
        Gdx.app.log("TowersSelector::resize()", "-- sectionWidth:" + sectionWidth);
        Gdx.app.log("TowersSelector::resize()", "-- sectionHeight:" + sectionHeight);
        if (gameField.gameSettings.verticalSelector) {
            setWidth(sectionWidth);
        } else {
            setHeight(sectionHeight);
        }
        float maxWidthSection = sectionWidth;
        float maxHeightSection = sectionHeight;
        Array<Actor> array = getChildren();
        for (int a = 0; a < array.size; a++) {
            Actor actor = array.get(a);
            if (actor instanceof Button) {
                Button button = (Button) actor;
                Gdx.app.log("TowersSelector::resize()", "-- button:" + button);
                Gdx.app.log("TowersSelector::resize()", "-- button.getMinWidth():" + button.getMinWidth());
                Gdx.app.log("TowersSelector::resize()", "-- button.getMinHeight():" + button.getMinHeight());
                float minButtonWidth = button.getMinWidth();
                float minButtonHeight = button.getMinHeight();
                if (minButtonWidth > maxWidthSection) {
                    maxWidthSection = minButtonWidth;
                }
                if (minButtonHeight > maxHeightSection) {
                    maxHeightSection = minButtonHeight;
                }
            }
        }
        sectionWidth = maxWidthSection;
        sectionHeight = maxHeightSection;
//        for (int a = 0; a < array.size; a++) {
//            Actor actor = array.get(a);
//            if (actor instanceof Button) {
//                Button button = (Button) actor;
//                button.setWidth(sectionWidth);
//                button.setHeight(sectionHeight);
////                actor.setX(a * sectionWidth);
//            }
//        }
        Gdx.app.log("TowersSelector::resize()", "-2- sectionWidth:" + sectionWidth);
        Gdx.app.log("TowersSelector::resize()", "-2- sectionHeight:" + sectionHeight);
//        setStopSection(calculateCurrentSection() - 1);

        selectorBorderVertical = width - sectionWidth;
        selectorBorderHorizontal = height - sectionHeight;

//        Gdx.app.log("TowersSelector::resize()", "-- gameField.gameSettings.topBottomLeftRightSelector:" + gameField.gameSettings.topBottomLeftRightSelector);
//        Gdx.app.log("TowersSelector::resize()", "-- gameField.gameSettings.verticalSelector:" + gameField.gameSettings.verticalSelector);
        // need change row in table cells! row or not row!
        if (gameField.gameSettings.verticalSelector) {
            if (gameField.gameSettings.topBottomLeftRightSelector) {
                selectorBorderVertical = width - sectionWidth;
                if (open) {
                    coordinateX = selectorBorderVertical;
                } else {
                    coordinateX = width;
                }
            } else {
                selectorBorderVertical = sectionWidth;
                if (open) {
                    coordinateX = 0;
                } else {
                    coordinateX = 0 - sectionWidth;
                }
            }
        } else {
            if (gameField.gameSettings.topBottomLeftRightSelector) {
                selectorBorderHorizontal = height - sectionHeight;
                if (open) {
                    coordinateY = selectorBorderHorizontal;
                } else {
                    coordinateY = height;
                }
            } else {
                selectorBorderHorizontal = sectionHeight;
                if (open) {
                    coordinateY = 0;
                } else {
                    coordinateY = 0 - sectionHeight;
                }
            }
        }
        Gdx.app.log("TowersSelector::resize()", "-- selectorBorderVertical:" + selectorBorderVertical);
        Gdx.app.log("TowersSelector::resize()", "-- selectorBorderHorizontal:" + selectorBorderHorizontal);
        Gdx.app.log("TowersSelector::resize()", "-- coordinateX:" + coordinateX);
        Gdx.app.log("TowersSelector::resize()", "-- coordinateY:" + coordinateY);

//        Gdx.app.log("TowersSelector::resize()", "-- getParent:" + getParent());
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        Gdx.app.log("TowersSelector::pan(" + x + "," + y + "," + deltaX + "," + deltaY + ")", "--");
//        float groupX = getX();
//        float groupY = getY();
        float selectorWidth = getWidth();
        float selectorHeight = getHeight();
        float groupPrefWidth = getPrefWidth();
        float groupPrefHeight = getPrefHeight();
        float parentWidth = getParent().getWidth();
        float parentHeight = getParent().getHeight();
        Gdx.app.log("TowersSelector::isPanning()", "-- coordinateX:" + coordinateX + " coordinateY:" + coordinateY + " selectorWidth:" + selectorWidth + " selectorHeight:" + selectorHeight);
        Gdx.app.log("TowersSelector::isPanning()", "-- groupPrefWidth:" + groupPrefWidth + " groupPrefHeight:" + groupPrefHeight + " parentWidth:" + parentWidth + " parentHeight:" + parentHeight);
        Gdx.app.log("TowersSelector::isPanning()", "-- selectorWidth:" + selectorWidth + " selectorHeight:" + selectorHeight);
//        Gdx.app.log("TowersSelector::isPanning()", "-- Gdx.graphics.getWidth():" + Gdx.graphics.getWidth() + " Gdx.graphics.getHeight():" + Gdx.graphics.getHeight());
//        Gdx.app.log("TowersSelector::isPanning()", "-- table.getStage().getViewport().getScreenWidth():" + table.getStage().getViewport().getScreenWidth());
//        Gdx.app.log("TowersSelector::isPanning()", "-- table.getStage().getViewport().getScreenHeight():" + table.getStage().getViewport().getScreenHeight());
//        Gdx.app.log("TowersSelector::isPanning()", "-- table.getStage().getViewport().getWorldWidth():" + table.getStage().getViewport().getWorldWidth());
//        Gdx.app.log("TowersSelector::isPanning()", "-- table.getStage().getViewport().getWorldHeight():" + table.getStage().getViewport().getWorldHeight());
//        Gdx.app.log("TowersSelector::isPanning()", "-- table.getWidth():" + table.getWidth() + " table.getHeight():" + table.getHeight());
        float deltaXabs = Math.abs(deltaX);
        float deltaYabs = Math.abs(deltaY);
        if (deltaXabs > deltaYabs && !isPanning) { // select direction
//            coordinateX += deltaX;
            if (gameField.gameSettings.verticalSelector) {
                if (deltaX > 0) {
                    if (gameField.gameSettings.topBottomLeftRightSelector && /*coordinateX < parentWidth &&*/ x >= selectorBorderVertical) {
                        coordinateX += deltaX;
                        if (coordinateX > parentWidth) {
                            coordinateX = parentWidth;
                            gameField.cancelUnderConstruction();
                        }
//                        isPanning = false;
                        return true;
                    } else if (!gameField.gameSettings.topBottomLeftRightSelector && x <= selectorBorderVertical) {
                        coordinateX += deltaX;
                        if (coordinateX > 0) {
                            coordinateX = 0;
                        }
//                        isPanning = true;
                        return true;
                    }
                } else {
                    if (gameField.gameSettings.topBottomLeftRightSelector && /*coordinateX < parentWidth &&*/ x >= selectorBorderVertical) {
                        coordinateX += deltaX;
                        if (coordinateX < selectorBorderVertical) {
                            coordinateX = selectorBorderVertical;
                        }
//                        isPanning = true;
                        return true;
                    } else if (!gameField.gameSettings.topBottomLeftRightSelector && x <= selectorBorderVertical) {
                        coordinateX += deltaX;
                        if (coordinateX < 0 - selectorWidth) {
                            coordinateX = 0 - selectorWidth;
                            gameField.cancelUnderConstruction();
                        }
//                        isPanning = true;
                        return true;
                    }
                }
            }
        } else {
//            coordinateY += deltaY;
            if (gameField.gameSettings.verticalSelector) {
                if (deltaY > 0) {
                    if (gameField.gameSettings.topBottomLeftRightSelector && /*coordinateY < parentHeight &&*/ x >= selectorBorderVertical) {
                        coordinateY += deltaY;
//                        if (coordinateY > parentHeight) {
//                            coordinateY = parentHeight;
//                            gameField.cancelUnderConstruction();
//                        }
//                        isPanning = false;
                        return true;
                    } else if (!gameField.gameSettings.topBottomLeftRightSelector && x <= selectorBorderVertical) {
                        coordinateY += deltaY;
//                        if (coordinateY > 0) {
//                            coordinateY = 0;
//                        }
//                        isPanning = true;
                        return true;
                    }
                } else {
                    if (gameField.gameSettings.topBottomLeftRightSelector && /*coordinateY < parentHeight &&*/ x >= selectorBorderVertical) {
                        coordinateY += deltaY;
//                        if (coordinateY < selectorBorderHorizontal) {
//                            coordinateY = selectorBorderHorizontal;
//                        }
//                        isPanning = true;
                        return true;
                    } else if (!gameField.gameSettings.topBottomLeftRightSelector && x <= selectorBorderVertical) {
                        coordinateY += deltaY;
//                        if (coordinateY < 0 - selectorHeight) {
//                            coordinateY = 0 - selectorHeight;
//                            gameField.cancelUnderConstruction();
//                        }
//                        isPanning = true;
                        return true;
                    }
                }
            }
//            isPanning = true;
//            if ( (gameField.gameSettings.verticalSelector && gameField.gameSettings.topBottomLeftRightSelector && (x>=selectorBorderVertical)) || isPanning) {
//                coordinateY += deltaY;
//            } else if ( (gameField.gameSettings.verticalSelector && !gameField.gameSettings.topBottomLeftRightSelector && (x<=selectorBorderVertical)) || isPanning) {
//                coordinateY += deltaY;
//            if (getY() > 0) {
//                setY(0);
//            } else if(getY()+ getHeight() < parentHeight) {
//                setY( (0-(getHeight()-parentHeight)) );
//            }
//            if (coordinateY > 0) {
//                coordinateY = 0;
//            } else if (coordinateY + selectorHeight < parentHeight) {
//                coordinateY = 0 - selectorHeight - parentHeight;
//            }
//                return true;
//            }
        }

//        Gdx.app.log("SlidingTable::isPanning()", "-- x:" + x + " y:" + y + " deltaX:" + deltaX + " deltaY:" + deltaY);
//        if ( coordinateX < -overscrollDistance ) {
//            return false;
//        }
//        if ( coordinateX > (getChildren().size - 1) * sectionWidth + overscrollDistance) {
//            return false;
//        }
//
//        isPanning = true;
//        coordinateX -= deltaX;
//        cancelTouchFocusedChild();
        return false;
    }

//    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        Gdx.app.log("TowersSelector::panStop()", "-- x:" + x + " y:" + y + " pointer:" + pointer + " button:" + button);
        isPanning = false;
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        Gdx.app.log("TowersSelector::fling()", "-- velocityX:" + velocityX + " velocityY:" + velocityY);
//        if ( < selectorBorderVertical) {
            if (Math.abs(velocityX) > flingSpeed) {
                if (velocityX > 0) {
                    coordinateX = getWidth();
//                closeSelector();
//                setStopSection(currentSection - 2);
                } else {
                    coordinateX = getWidth() - sectionWidth * 2f; // todo need fix why *2f?
//                openSelector()
//                setStopSection(currentSection);
                }
            }
            if (Math.abs(velocityY) > flingSpeed) {
                if (velocityY > 0) {
                    setStopSection(currentSection - 2);
                } else {
                    setStopSection(currentSection);
                }
            }
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
        return templateForTowers.size;
//        return getChildren().size;
    }

    // Вычисление текущей секции на основании смещения контейнера sections
    public int calculateCurrentSection() {
        // Текущая секция = (Текущее смещение / длинну секции) + 1, т.к наши секции нумеруются с 1
        int section = Math.round( getY() / sectionHeight ) + 1;
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

        stopSection = stoplineSection * sectionHeight;

        // Определяем направление движения
        // transmission ==  1 - вправо
        // transmission == -1 - влево
        if ( getY() < stopSection) {
            transmission = 1;
        } else {
            transmission = -1;
        }
    }

    private void move(float delta) {
        // Определяем направление смещения
        if ( getY() < stopSection) {
//             Двигаемся вправо
//             Если попали сюда, а при этом должны были двигаться влево
//             значит пора остановиться
            if ( transmission == -1 ) {
                setY(stopSection);
//                 Фиксируем номер текущей секции
                currentSection = calculateCurrentSection();
                return;
            }
//             Смещаем
            setY(getY()+ speed * delta);
        } else if( getY() > stopSection) {
            if ( transmission == 1 ) {
                setY(stopSection);
                currentSection = calculateCurrentSection();
                return;
            }
            setY(getY() - speed * delta);
        }
    }

    @Override
    public void act (float delta) {
        // Смещаем контейнер с секциями
        setX(coordinateX);
        setY( -coordinateY);

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
