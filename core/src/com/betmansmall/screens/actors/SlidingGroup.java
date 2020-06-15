package com.betmansmall.screens.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.betmansmall.GameMaster;
import com.betmansmall.utils.logging.Logger;

public class SlidingGroup extends Table implements GestureDetector.GestureListener {
//    private Texture bg = new Texture(Gdx.files.internal("buttons/bg.png"));
    private Texture naviPassive = new Texture(Gdx.files.internal("buttons/naviPassive.png"));
    private Texture naviActive  = new Texture(Gdx.files.internal("buttons/naviActive.png"));

    private float sectionWidth;
    private float amountX = 0;
    private int transmission   = 0;
    private float stopSection  = 0;
    private float speed        = 1500;

    private int currentSection = 1;
    private float flingSpeed   = 1000;
    private float overscrollDistance = 500;
    private boolean isPanning;
    private int itemsCountInSection = 3;

    public SlidingGroup(GameMaster gameMaster) {
        Gdx.app.log("SlidingGroup::SlidingGroup()", "-- amountX:" + amountX);

        sectionWidth  = Gdx.app.getGraphics().getWidth();
        float itemWidth = sectionWidth / itemsCountInSection;
        int sectionsCount = gameMaster.allMaps.size / itemsCountInSection;
        for (int section = 0; section < sectionsCount; section++) {
            Table table = new Table();
            for (int i = 0; i < itemsCountInSection; i++) {
                MenuItem menuItem = new MenuItem( itemWidth + itemWidth/2, itemWidth, (section*itemsCountInSection)+i);
                menuItem.setUserObject(gameMaster.allMaps.get((section*itemsCountInSection)+i));
                table.add(menuItem).pad(20,itemWidth / itemsCountInSection,60,itemWidth / itemsCountInSection);
            }
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
        return false;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        Actor actor = hit(x, y, false);
        if (actor != null) {
            Logger.logInfo("x:" + x, "y:" + y, "v:" + actor.getUserObject());
//            switch (intV) {
//                case 0: {
//                    Logger.logDebug(x+"",y+"", intV+"");
//                }
//                default:
//            }
        }
        return false;
    }

    public int calculateCurrentSection() {
        int section = Math.round( amountX / sectionWidth ) + 1;
        if ( section > getChildren().size ) return getChildren().size;
        if ( section < 1 ) return 1;
        return section;
    }

    public int getSectionsCount() {
        return getChildren().size;
    }

    public void setStopSection(int stoplineSection) {
        Gdx.app.log("SlidingGroup::setStopSection()", "-- stoplineSection:" + stoplineSection);
        if ( stoplineSection < 0 ) {
            stoplineSection = 0;
        }
        if ( stoplineSection > this.getSectionsCount() - 1 ) {
            stoplineSection = this.getSectionsCount() - 1;
        }
        stopSection = stoplineSection * sectionWidth;
        if ( amountX < stopSection) {
            transmission = 1;
        } else {
            transmission = -1;
        }
    }

    private void move(float delta) {
        if ( amountX < stopSection) {
            if ( transmission == -1 ) {
                amountX = stopSection;
                currentSection = calculateCurrentSection();
                return;
            }
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
        setX( -amountX );
        if (this.isPanning) {
            setStopSection(calculateCurrentSection() - 1);
        } else {
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

    public void setSpeed( float _speed ) {
        speed = _speed;
    }
}
