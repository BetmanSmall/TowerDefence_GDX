package com.betmansmall.screens.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Created by betma on 06.12.2018.
 */

public class SlidingGroup extends Table implements GestureDetector.GestureListener {
    private Texture bg, naviPassive, naviActive;

    // Контейнер для секций
//    private Group sections;
    private float sectionWidth;
//    private float sectionHeight;

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
//    private Rectangle cullingArea = new Rectangle();
//    private Actor touchFocusedChild;
//    private ActorGestureListener actorGestureListener;

    private int LINE_MENU_ITEM_COUNT = 4;

    public SlidingGroup() {

        Gdx.app.log("SlidingGroup::SlidingGroup()", "-- amountX:" + amountX);
//        super();
        bg          = new Texture(Gdx.files.internal("buttons/bg.png"));
        naviPassive = new Texture(Gdx.files.internal("buttons/naviPassive.png"));
        naviActive  = new Texture(Gdx.files.internal("buttons/naviActive.png"));

//        sections = new Group();
//        this.addActor( sections );

        sectionWidth  = Gdx.app.getGraphics().getWidth();
//        sectionHeight = Gdx.app.getGraphics().getHeight();

//        actorGestureListener = new ActorGestureListener() {
//        };

//        this.addListener(actorGestureListener);

        // Считаем ширину иконки уровня исходя из желаемого количества иконок по ширине экрана
        // Ширина = Ширина экрана / желаемое количество - (отступ слева + отступ справа)
        float itemWidth = Gdx.app.getGraphics().getWidth() / LINE_MENU_ITEM_COUNT;

        // Создаем 4 секции с иконками выбора уровня
        // В каждой секции будет 2 строки иконок по 6 в каждой
        // Расставляем иконки по сетке с помощью виджета Table
        for(int section=0; section< LINE_MENU_ITEM_COUNT; section++) {
            Table table = new Table();
            for(int i=0; i<1; i++) {
                table.row();
                for(int j = 0; j < 3; j++ ) {
                    // (20,20,60,20) - отступы сверху, слева, снизу, справа
                    table.add( new MenuItem( itemWidth + itemWidth/2, itemWidth ) ).pad(20,itemWidth / LINE_MENU_ITEM_COUNT,60,itemWidth / LINE_MENU_ITEM_COUNT);
                }
            }
            // Добавляем секцию в наш контейнер
//            table.setFillParent(true);
            add(table);
        }
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        Gdx.app.log("SlidingGroup::pan()", "-- x:" + x + " y:" + y + " deltaX:" + deltaX + " deltaY:" + deltaY);
        if ( amountX < -overscrollDistance ) {
            return false;
        }
        if ( amountX > (getChildren().size - 1) * sectionWidth + overscrollDistance) {
            return false;
        }

        isPanning = true;
        amountX -= deltaX;
//        cancelTouchFocusedChild();
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        Gdx.app.log("SlidingGroup::panStop()", "-- x:" + x + " y:" + y);
        isPanning = false;
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        Gdx.app.log("SlidingGroup::fling()", "-- velocityX:" + velocityX + " velocityY:" + velocityY);
        if ( Math.abs(velocityX) > flingSpeed ) {
            if ( velocityX > 0 ) {
                setStopSection(currentSection - 2);
            } else {
                setStopSection(currentSection);
            }
        }
//        cancelTouchFocusedChild();
        return false;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
//                if ( event.getTarget().getClass() == LevelIcon.class ) {
//        touchFocusedChild = event.getTarget();
//                }
        return false;
    }

//    public void addWidget(Actor widget) {
//        widget.setX( this.sections.getChildren().size * sectionWidth);
//        widget.setY( 0 );
//        widget.setWidth( sectionWidth );
//        widget.setHeight( Gdx.graphics.getHeight() );
//        sections.addActor( widget );
//    }

    // Вычисление текущей секции на основании смещения контейнера sections
    public int calculateCurrentSection() {
//        Gdx.app.log("SlidingGroup::calculateCurrentSection()", "-- amountX:" + amountX);
//        Gdx.app.log("SlidingGroup::calculateCurrentSection()", "-- sectionWidth:" + sectionWidth);
        // Текущая секция = (Текущее смещение / длинну секции) + 1, т.к наши секции нумеруются с 1
        int section = Math.round( amountX / sectionWidth ) + 1;
        //Проверяем адекватность полученного значения, вхождение в интервал [1, количество секций]
        if ( section > getChildren().size ) return getChildren().size;
        if ( section < 1 ) return 1;
        return section;
    }

    public int getSectionsCount() {
        return getChildren().size;
    }

    public void setStopSection(int stoplineSection) {
        Gdx.app.log("SlidingGroup::setStopSection()", "-- stoplineSection:" + stoplineSection);
//        Gdx.app.log("SlidingGroup::setStopSection()", "-- getSectionsCount():" + getSectionsCount());
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
//        Gdx.app.log("SlidingGroup::move()", "-- amountX:" + amountX);
//        Gdx.app.log("SlidingGroup::move()", "-- stopSection:" + stopSection);
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
        setX( -amountX );

//        cullingArea.set( -sections.getX() + 50, sections.getY(), sectionWidth - 100, sections.getHeight() );
//        sections.setCullingArea(cullingArea);

        // Если водим пальцем по экрану
        if (this.isPanning) {
//        if ( actorGestureListener.getGestureDetector().isPanning() ) {
            // Устанавливаем границу, к которой будем анимировать движение
//             граница = номер предыдущей секции
            setStopSection(calculateCurrentSection() - 1);
        } else {
            // Если палец далеко от экрана - анимируем движение в заданную точку
            move( delta );
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

//        Gdx.app.log("::draw()", "-- section.getX()" + sections.getX());
//        Gdx.app.log("::draw()", "-- section.getY()" + sections.getY());
//        Gdx.app.log("::draw()", "-- section.getWidth()" + sections.getWidth());
//        Gdx.app.log("::draw()", "-- section.getHeight()" + sections.getHeight());
//
////        // Рисуем фон
//        batch.draw(bg, sections.getX(), sections.getY(), sectionWidth, this.getHeight() );

        // Рисуем указатель текущей секции
        for (int i=1; i<= getSectionsCount(); i++) {
            if ( i == calculateCurrentSection() ) {
                batch.draw( naviActive, Gdx.app.getGraphics().getWidth()/2 - getSectionsCount()*20/2 + i*20 , 50);
            } else {
                batch.draw( naviPassive, Gdx.app.getGraphics().getWidth()/2 - getSectionsCount()*20/2 + i*20 , 50);
            }
        }
    }

//    void cancelTouchFocusedChild () {
////        Gdx
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

    public void setSpeed( float _speed ) {
        speed = _speed;
    }
}
