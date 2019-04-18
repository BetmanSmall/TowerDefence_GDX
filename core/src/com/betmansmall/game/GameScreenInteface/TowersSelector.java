package com.betmansmall.game.GameScreenInteface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.gameLogic.GameField;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;

public class TowersSelector extends Table { // implements GestureDetector.GestureListener {
//    public float sectionWidth, sectionHeight;
    public float parentWidth, parentHeight;
    public float selectorPrefWidth, selectorPrefHeight;
    public float selectorBorderVertical;
    public float selectorBorderHorizontal;

    public boolean flinging;
    public float flingVelocityX, flingVelocityY;

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
    public GameInterface gameInterface; // NOT GOOD MB

    public TowersSelector(GameField gameField, BitmapFont bitmapFont, Skin skin, GameInterface gameInterface) {
        this.gameField = gameField;
        this.bitmapFont = bitmapFont;
        this.gameInterface = gameInterface; // NOT GOOD MB

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
            button.setName(nameTower);
            button.setUserObject(towerIndex);
            Cell<Button> cellButton = this.add(button);//.expand();//.fill();
            if (gameField.gameSettings.verticalSelector) {
                cellButton.row();//.minHeight(Gdx.graphics.getHeight()*0.2f).row();
            }
        }
//        this.clearChildren();
        this.setDebug(true);
//        this.addListener(new EventListener() {
//            @Override
//            public boolean handle(Event event) {
//                return true;
//            }
//        });
    }

    public void dispose() {
        Gdx.app.log("TowersSelector::dispose()", "--");
    }

//    @Override
    public void resize(int width, int height) {
        Gdx.app.log("TowersSelector::resize()", "-- width:" + width + " height:" + height);
        Group groupParent = getParent(); // mb it is not good!
        parentWidth = groupParent.getWidth(); // mb need set simple // parentWidth = width;
        parentHeight = groupParent.getHeight(); // mb need set simple // parentHeight = height;
        Gdx.app.log("TowersSelector::resize()", "-- parentWidth:" + parentWidth + " parentHeight:" + parentHeight);
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
        Gdx.app.log("TowersSelector::resize()", "-- getX:" + getX());
        Gdx.app.log("TowersSelector::resize()", "-- getY:" + getY());

        selectorPrefWidth = getPrefWidth();
        selectorPrefHeight = getPrefHeight();
        Gdx.app.log("TowersSelector::resize()", "-- selectorPrefWidth:" + selectorPrefWidth);
        Gdx.app.log("TowersSelector::resize()", "-- selectorPrefHeight:" + selectorPrefHeight);
//        setStopSection(calculateCurrentSection() - 1);

        // need change row in table cells! row or not row!
        if (gameField.gameSettings.verticalSelector) {
            coordinateY = parentHeight;
            if (gameField.gameSettings.topBottomLeftRightSelector) {
                selectorBorderVertical = width - selectorPrefWidth;
                if (open) {
                    coordinateX = selectorBorderVertical;
                } else {
                    coordinateX = width;
                }
            } else {
                selectorBorderVertical = selectorPrefWidth;
                if (open) {
                    coordinateX = 0;
                } else {
                    coordinateX = 0 - selectorPrefWidth;
                }
            }
        } else {
            if (gameField.gameSettings.topBottomLeftRightSelector) {
                selectorBorderHorizontal = height - selectorPrefHeight;
                if (open) {
                    coordinateY = height;
                } else {
                    coordinateY = height + selectorPrefHeight;
                }
            } else {
                selectorBorderHorizontal = selectorPrefHeight;
                if (open) {
                    coordinateY = selectorBorderHorizontal;
                } else {
                    coordinateY = 0;
                }
            }
        }
        Gdx.app.log("TowersSelector::resize()", "-- selectorBorderVertical:" + selectorBorderVertical);
        Gdx.app.log("TowersSelector::resize()", "-- selectorBorderHorizontal:" + selectorBorderHorizontal);
        Gdx.app.log("TowersSelector::resize()", "-- coordinateX:" + coordinateX);
        Gdx.app.log("TowersSelector::resize()", "-- coordinateY:" + coordinateY);
    }

//    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        Gdx.app.log("TowersSelector::pan(" + x + "," + y + "," + deltaX + "," + deltaY + ")", "--");
        Gdx.app.log("TowersSelector::isPanning()", "-- coordinateX:" + coordinateX + " coordinateY:" + coordinateY);
        Gdx.app.log("TowersSelector::isPanning()", "-- selectorPrefWidth:" + selectorPrefWidth + " selectorPrefHeight:" + selectorPrefHeight);
        Gdx.app.log("TowersSelector::isPanning()", "-- selectorPrefWidth:" + selectorPrefWidth + " selectorPrefHeight:" + selectorPrefHeight + " parentWidth:" + parentWidth + " parentHeight:" + parentHeight);

        float deltaXabs = Math.abs(deltaX);
        float deltaYabs = Math.abs(deltaY);
        if (deltaXabs > deltaYabs) {// && !isPanning) { // select direction
//            coordinateX += deltaX;
            if (gameField.gameSettings.verticalSelector) {
                if (deltaX > 0) {
                    if (gameField.gameSettings.topBottomLeftRightSelector && x >= coordinateX) {
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
                    if (gameField.gameSettings.topBottomLeftRightSelector && x >= selectorBorderVertical) {
                        coordinateX += deltaX;
                        if (coordinateX < selectorBorderVertical) {
                            coordinateX = selectorBorderVertical;
                        }
//                        isPanning = true;
                        return true;
                    } else if (!gameField.gameSettings.topBottomLeftRightSelector && x <= coordinateX+ selectorPrefWidth) {
                        coordinateX += deltaX;
                        if (coordinateX < 0 - selectorPrefWidth) {
                            coordinateX = 0 - selectorPrefWidth;
                            gameField.cancelUnderConstruction();
                        }
//                        isPanning = true;
                        return true;
                    }
                }
            } else {
                if (deltaX > 0) {
                    if (gameField.gameSettings.topBottomLeftRightSelector && y >= coordinateY- selectorPrefHeight) {
                        coordinateX += deltaX;
                        if (coordinateX > 0) {
                            coordinateX = 0;
                        }
//                        isPanning = ???;
                        return true;
                    } else if (!gameField.gameSettings.topBottomLeftRightSelector && y <= coordinateY){
                        coordinateX += deltaX;
                        if (coordinateX > 0) {
                            coordinateX = 0;
                        }
//                        isPanning = ???;
                        return true;
                    }
                } else {
                    if (gameField.gameSettings.topBottomLeftRightSelector && y >= coordinateY- selectorPrefHeight) {
                        coordinateX += deltaX;
                        if (coordinateX+ selectorPrefWidth < parentWidth) {
                            coordinateX = parentWidth- selectorPrefWidth;
                        }
//                        isPanning = true;
                        return true;
                    } else if (!gameField.gameSettings.topBottomLeftRightSelector && y <= coordinateY) {
                        coordinateX += deltaX;
                        if (coordinateX+ selectorPrefWidth < parentWidth) {
                            coordinateX = parentWidth- selectorPrefWidth;
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
                    if (gameField.gameSettings.topBottomLeftRightSelector && x >= coordinateX) {
                        coordinateY += deltaY;
                        if (coordinateY- selectorPrefHeight > 0) {
                            coordinateY = selectorPrefHeight;
                        }
//                        isPanning = false;
                        return true;
                    } else if (!gameField.gameSettings.topBottomLeftRightSelector && x <= coordinateX+ selectorPrefWidth) {
                        coordinateY += deltaY;
                        if (coordinateY- selectorPrefHeight > 0) {
                            coordinateY = selectorPrefHeight;
                        }
//                        isPanning = true;
                        return true;
                    }
                } else {
                    if (gameField.gameSettings.topBottomLeftRightSelector && x >= coordinateX) {
                        coordinateY += deltaY;
                        if (coordinateY < parentHeight) {
                            coordinateY = parentHeight;
                        }
//                        isPanning = true;
                        return true;
                    } else if (!gameField.gameSettings.topBottomLeftRightSelector && x <= coordinateX+ selectorPrefWidth) {
                        coordinateY += deltaY;
                        if (coordinateY < parentHeight) {
                            coordinateY = parentHeight;
                        }
//                        isPanning = true;
                        return true;
                    }
                }
            } else {
                if (deltaY > 0) {
                    if (gameField.gameSettings.topBottomLeftRightSelector && y >= coordinateY- selectorPrefHeight) {
                        coordinateY += deltaY;
                        if (coordinateY- selectorPrefHeight > parentHeight) {
                            coordinateY = parentHeight+ selectorPrefHeight;
                            gameField.cancelUnderConstruction();
                        }
//                        isPanning = ???;
                        return true;
                    } else if (!gameField.gameSettings.topBottomLeftRightSelector && y <= selectorBorderHorizontal){
                        coordinateY += deltaY;
                        if (coordinateY > selectorBorderHorizontal) {
                            coordinateY = selectorBorderHorizontal;
                        }
//                        isPanning = ???;
                        return true;
                    }
                } else {
                    if (gameField.gameSettings.topBottomLeftRightSelector && y >= selectorBorderHorizontal) {
                        coordinateY += deltaY;
                        if (coordinateY- selectorPrefHeight < selectorBorderHorizontal) {
                            coordinateY = parentHeight;
                        }
//                        isPanning = true;
                        return true;
                    } else if (!gameField.gameSettings.topBottomLeftRightSelector && y <= coordinateY) {
                        coordinateY += deltaY;
                        if (coordinateY < 0) {
                            coordinateY = 0;
                            gameField.cancelUnderConstruction();
                        }
//                        isPanning = true;
                        return true;
                    }
                }
            }
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
        if (gameField.gameSettings.verticalSelector) {
            if (gameField.gameSettings.topBottomLeftRightSelector) {
                if (coordinateX >= selectorBorderVertical + (selectorPrefWidth / 2) ) {
                    coordinateX = getParent().getWidth();
                    gameField.cancelUnderConstruction();
                } else {
                    coordinateX = selectorBorderVertical;
                }
            } else {
                if (coordinateX+ selectorPrefWidth >= selectorBorderVertical - (selectorPrefWidth / 2) ) {
                    coordinateX = 0;
                } else {
                    coordinateX = -selectorPrefWidth;
                    gameField.cancelUnderConstruction();
                }
            }
        } else {
            if (gameField.gameSettings.topBottomLeftRightSelector) {
                if (coordinateY- selectorPrefHeight >= selectorBorderHorizontal + (selectorPrefHeight / 2) ) {
                    coordinateY = parentHeight + selectorPrefHeight; // or sectionHeight???
                    gameField.cancelUnderConstruction();
                } else {
                    coordinateY = parentHeight;
                }
            } else {
                if (coordinateY >= selectorBorderHorizontal - (selectorPrefHeight / 2) ) {
                    coordinateY = selectorBorderHorizontal;
                } else {
                    coordinateY = 0;
                    gameField.cancelUnderConstruction();
                }
            }
        }
        isPanning = false;
        return false;
    }

//    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        Gdx.app.log("TowersSelector::fling()", "-- velocityX:" + velocityX + " velocityY:" + velocityY);
        flinging = false;
        if (gameField.gameSettings.verticalSelector) {
            if (gameField.gameSettings.topBottomLeftRightSelector) {
                if (gameInterface.prevMouseX > selectorBorderVertical) {
                    flinging = true;
                }
            } else {
                if (gameInterface.prevMouseX < selectorBorderVertical) {
                    flinging = true;
                }
            }
        } else {
            if (gameField.gameSettings.topBottomLeftRightSelector) {
                if (gameInterface.prevMouseY > selectorBorderHorizontal) {
                    flinging = true;
                }
            } else {
                if (gameInterface.prevMouseY < selectorBorderHorizontal) {
                    flinging = true;
                }
            }
        }
        if (gameField.gameSettings.smoothFlingSelector && flinging) { // smoothFlingSelector - плавное движение селектора
            flingVelocityX = velocityX * 0.5f;
            flingVelocityY = velocityY * 0.5f;
            return true;
        } else { // если smoothFlingSelector=false значит нужно рывками сдвигать по секциям. как в help SlidingTable
            flinging = false; // TODO work need
        }
//        if ( < selectorBorderVertical) {
//            if (Math.abs(velocityX) > flingSpeed) {
//                if (velocityX > 0) {
//                    coordinateX = getWidth();
//                closeSelector();
//                setStopSection(currentSection - 2);
//                } else {
//                    coordinateX = getWidth() - sectionWidth * 2f; // todo need fix why *2f?
//                openSelector()
//                setStopSection(currentSection);
//                }
//            }
//            if (Math.abs(velocityY) > flingSpeed) {
//                if (velocityY > 0) {
//                    setStopSection(currentSection - 2);
//                } else {
//                    setStopSection(currentSection);
//                }
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

//    public int getSectionsCount() {
//        return templateForTowers.size;
////        return getChildren().size;
//    }

//    // Вычисление текущей секции на основании смещения контейнера sections
//    public int calculateCurrentSection() {
//        // Текущая секция = (Текущее смещение / длинну секции) + 1, т.к наши секции нумеруются с 1
//        int section = Math.round( getY() / sectionHeight ) + 1;
//        //Проверяем адекватность полученного значения, вхождение в интервал [1, количество секций]
//        if ( section > getChildren().size ) return getChildren().size;
//        if ( section < 1 ) return 1;
//        return section;
//    }

//    public void setStopSection(int stoplineSection) {
//        if ( stoplineSection < 0 ) {
//            stoplineSection = 0;
//        }
//        if ( stoplineSection > this.getSectionsCount() - 1 ) {
//            stoplineSection = this.getSectionsCount() - 1;
//        }
//
//        stopSection = stoplineSection * sectionHeight;
//
//        // Определяем направление движения
//        // transmission ==  1 - вправо
//        // transmission == -1 - влево
//        if ( getY() < stopSection) {
//            transmission = 1;
//        } else {
//            transmission = -1;
//        }
//    }

//    private void move(float delta) {
//        // Определяем направление смещения
//        if ( getY() < stopSection) {
////             Двигаемся вправо
////             Если попали сюда, а при этом должны были двигаться влево
////             значит пора остановиться
//            if ( transmission == -1 ) {
//                setY(stopSection);
////                 Фиксируем номер текущей секции
//                currentSection = calculateCurrentSection();
//                return;
//            }
////             Смещаем
//            setY(getY()+ speed * delta);
//        } else if( getY() > stopSection) {
//            if ( transmission == 1 ) {
//                setY(stopSection);
//                currentSection = calculateCurrentSection();
//                return;
//            }
//            setY(getY() - speed * delta);
//        }
//    }

    @Override
    public void act(float delta) {
        // Смещаем контейнер с секциями
        if (flinging) {
            flingVelocityX *= 0.98f;
            flingVelocityY *= 0.98f;
            float newX = coordinateX + (flingVelocityX * delta);
            float newY = coordinateY + (flingVelocityY * delta);
            if (gameField.gameSettings.verticalSelector) {
                if (newY > parentHeight && newY - selectorPrefHeight < 0) {
                    coordinateY = newY;
                }
            } else {
                if (newX < 0 && newX + selectorPrefWidth > parentWidth) {
                    coordinateX = newX;
                }
            }
            if (Math.abs(flingVelocityX) < 0.01) flingVelocityX = 0.0f;
            if (Math.abs(flingVelocityY) < 0.01) flingVelocityY = 0.0f;
            if (flingVelocityX == 0.0 && flingVelocityY == 0.0) {
                flinging = false;
            }
//                    Gdx.app.log("CameraController::update()", "-- velX:" + velX + " velY:" + velY);
//                    Gdx.app.log("CameraController::update()", "-- newCameraX:" + newCameraX + " newCameraY:" + newCameraY);
        }
        setX(coordinateX);
        setY( -(coordinateY-parentHeight) ); // pizdec libgdx draw ui from leftDown but mouse Coord from leftUp

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
