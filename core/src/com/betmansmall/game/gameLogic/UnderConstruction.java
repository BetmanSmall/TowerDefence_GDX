package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;

/**
 * Created by betma on 30.04.2016.
 */
public class UnderConstruction {
    public int state;
    public int startX, startY;
    public int endX, endY;
    public TemplateForTower templateForTower;
    public Array<Integer> coorsX;
    public Array<Integer> coorsY;
    public int x,y,dx, dy, incx, incy, pdx, pdy, es, el, err;

    public UnderConstruction(TemplateForTower templateForTower) {
        Gdx.app.log("UnderConstruction::UnderConstruction()", "-- templateForTower:" + templateForTower);
        this.state = -1;
        this.templateForTower = templateForTower;
        this.coorsX = new Array<Integer>();
        this.coorsY = new Array<Integer>();
    }

    public UnderConstruction(int startX, int startY, TemplateForTower templateForTower) {
        Gdx.app.log("UnderConstruction::UnderConstruction()", "-- startX:" + startX + " startY:" + startY + " templateForTower:" + templateForTower);
        this.state = 1;
        this.startX = startX;
        this.startY = startY;
        this.templateForTower = templateForTower;
        this.coorsX = new Array<Integer>();
        this.coorsY = new Array<Integer>();
    }

    public boolean setStartCoors(int startX, int startY) {
        Gdx.app.log("UnderConstruction::setStartCoors()", "-- startX:" + startX + " startY:" + startY);
        this.state = 1;
        this.startX = startX;
        this.startY = startY;
        return true;
    }

    public boolean setState(int state) {
        this.state = state;
        return true;
    }

    public int getState() {
        return state;
    }

    public boolean setEndCoors(int endX, int endY) {
//        Gdx.app.log("UnderConstruction::setEndCoors()", "-- endX:" + endX + " endY:" + endY);
        this.endX = endX;
        this.endY = endY;

        if(state == -1) {
            state = 0;
        }

        if(state == 1 && templateForTower != null) {
            coorsX.clear();
            coorsY.clear();
//            int towerSize = templateForTower.size;
//            int deltaX = 0, deltaY = 0;
//            if(towerSize != 1) {
//                if(towerSize%2 == 0) {
//                    deltaX = towerSize/2;
//                    deltaY = (towerSize/2)-1;
//                } else {
//                    deltaX = towerSize/2;
//                    deltaY = towerSize/2;
//                }
//            }
//            int tmpX = startX - deltaX;
//            int tmpY = startY - deltaY;
//            if(endY == tmpY || (endY < (tmpY+towerSize) && endY > tmpY)) {
//                if(endX >= tmpX) {
//                    for(int currX = tmpX+towerSize; currX <= endX; currX+=towerSize) {
//                        this.coorsX.add(currX+deltaX);
//                        this.coorsY.add(tmpY+deltaY);
//                    }
//                } else {
//                    for(int currX = tmpX-towerSize; currX > endX-towerSize; currX-=towerSize) {
//                        this.coorsX.add(currX+deltaX);
//                        this.coorsY.add(tmpY+deltaY);
//                    }
//                }
//            } else if(endX == tmpX || endX < (tmpX+towerSize) && endX > tmpX) {
//                if(endY >= tmpY) {
//                    for(int currY = tmpY+towerSize; currY <= endY; currY+=towerSize) {
//                        this.coorsX.add(tmpX+deltaX);
//                        this.coorsY.add(currY+deltaY);
//                    }
//                } else {
//                    for(int currY = tmpY-towerSize; currY > endY-towerSize; currY-=towerSize) {
//                        this.coorsX.add(tmpX+deltaX);
//                        this.coorsY.add(currY+deltaY);
//                    }
//                }
//            }
            dx = endX - startX;
            incx = sign(dx);
            dy = endY - startY;
            incy = sign(dy);

            if (dx < 0) dx = -dx;
            if (dy < 0) dy = -dy;

            if (dx > dy) {
                pdx = incx;
                pdy = 0;
                es = dy;
                el = dx;
            } else {
                pdx = 0;
                pdy = incy;
                es = dx;
                el = dy;
            }
            x = startX;
            y = startY;
            err = el / 2;
            this.coorsX.add(x);
            this.coorsY.add(y);
            for (int t = 0; t < el; t++) {
                err -= es;
                if (err < 0) {
                    err += el;
                    x += incx;
                    y += incy;
                } else {
                    x += pdx;
                    y += pdy;
                }
                this.coorsX.add(x);
                this.coorsY.add(y);
            }
            return true;
        }
        return false;
    }

    private int sign (int x) {
        return (x > 0) ? 1 : (x < 0) ? -1 : 0;
    }

    public boolean clearStartCoors() {
        this.state = 0;
        if (coorsX != null) {
            this.coorsX.clear();
        }
        if (coorsY != null) {
            this.coorsY.clear();
        }
        return true;
    }

    public void dispose() {
        clearStartCoors();
        templateForTower = null;
        coorsX = null;
        coorsY = null;
    }
}
