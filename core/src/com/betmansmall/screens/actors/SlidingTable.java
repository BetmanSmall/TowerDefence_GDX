package com.betmansmall.screens.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.utils.logging.Logger;

public class SlidingTable extends Table implements GestureDetector.GestureListener {
    private Texture naviPassive, naviActive;

    // Контейнер для секций
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
//    private Rectangle cullingArea = new Rectangle();
//    private Actor touchFocusedChild;
//    private ActorGestureListener actorGestureListener;
    private Array<Image> helpImages;

    private int LINE_MENU_ITEM_COUNT = 6;

    public SlidingTable() {
//        Gdx.app.log("SlidingTable::SlidingTable()", "-- ");

        helpImages = new Array<Image>();
        FileHandle imagesDir = Gdx.files.internal("helpImages");
        FileHandle[] fileHandles = imagesDir.list();
        for (FileHandle fileHandle : fileHandles) {
            if (fileHandle.extension().equals("png")) {
                Image image = new Image(new Texture(fileHandle));
//                image.setFillParent(true);
                helpImages.add(image);
            }
        }

        naviPassive = new Texture(Gdx.files.internal("buttons/naviPassive.png"));
        naviActive  = new Texture(Gdx.files.internal("buttons/naviActive.png"));

//        sectionWidth  = Gdx.app.getGraphics().getWidth();
//        sectionHeight = Gdx.app.getGraphics().getHeight();

        // Считаем ширину иконки уровня исходя из желаемого количества иконок по ширине экрана
        // Ширина = Ширина экрана / желаемое количество - (отступ слева + отступ справа)
        float itemWidth = sectionWidth / LINE_MENU_ITEM_COUNT - 40;

        // Создаем 4 секции с иконками выбора уровня
        // В каждой секции будет 2 строки иконок по 6 в каждой
        // Расставляем иконки по сетке с помощью виджета Table
        for(int section = 0; section < helpImages.size; section++) {
            Image image = helpImages.get(section);
            Table sectionTable = new Table();
            sectionTable.add(image).center().expand();
//            Table table = new Table();
//            for(int i=0; i<2; i++) {
//                table.row();
//                for(int j = 0; j < 6; j++ ) {
//                    // (20,20,60,20) - отступы сверху, слева, снизу, справа
//                    table.add( new MenuItem( itemWidth, itemWidth ) ).pad(20,20,60,20).expand();
//                }
//            }
//            sectionTable.addActor(table);
            // Добавляем секцию в наш контейнер
            addActor(sectionTable);
        }
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        Gdx.app.log("SlidingTable::isPanning()", "-- x:" + x + " y:" + y + " deltaX:" + deltaX + " deltaY:" + deltaY);
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
        Gdx.app.log("SlidingTable::panStop()", "-- x:" + x + " y:" + y);
        isPanning = false;
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        Gdx.app.log("SlidingTable::fling()", "-- velocityX:" + velocityX + " velocityY:" + velocityY);
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
//        if ( event.getTarget().getClass() == LevelIcon.class ) {
//            touchFocusedChild = event.getTarget();
//        }
        Logger.logDebug("x:" + x, "y:" + y, "actor:" + hit(x, y, true));
        return false;
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

        for (int i=1; i<= getSectionsCount(); i++) {
            if ( i == calculateCurrentSection() ) {
                batch.draw( naviActive, Gdx.app.getGraphics().getWidth()/2 - getSectionsCount()*20/2 + i*20 , 50);
            } else {
                batch.draw( naviPassive, Gdx.app.getGraphics().getWidth()/2 - getSectionsCount()*20/2 + i*20 , 50);
            }
        }
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
        sectionWidth = width;
        sectionHeight = height;
        Array<Actor> array = getChildren();
        for (int a = 0; a < array.size; a++) {
            Actor actor = array.get(a);
            actor.setWidth(sectionWidth);
            actor.setHeight(sectionHeight);
            actor.setX(a * sectionWidth);
            if (actor instanceof Table) {
                Table table = (Table)actor;
                Array<Actor> children = table.getChildren();
                for (Actor child : children) {
                    if (child instanceof Table) {
                        child.setWidth(sectionWidth);
                        child.setHeight(sectionHeight);
                    }
                }
            }
        }
        setStopSection(calculateCurrentSection() - 1);
    }
}
