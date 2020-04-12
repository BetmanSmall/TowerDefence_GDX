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
    private boolean buildType;

    public UnderConstruction(TemplateForTower templateForTower, boolean buildType) {
        Gdx.app.log("UnderConstruction::UnderConstruction()", "-- templateForTower:" + templateForTower);
        this.state = -1;
        this.init(templateForTower, buildType);
    }

    public UnderConstruction(int startX, int startY, TemplateForTower templateForTower) {
        Gdx.app.log("UnderConstruction::UnderConstruction()", "-- startX:" + startX + " startY:" + startY + " templateForTower:" + templateForTower);
        this.state = 1;
        this.startX = startX;
        this.startY = startY;
        this.init(templateForTower, true);
    }

    public void init(TemplateForTower templateForTower, boolean buildType) {
        this.templateForTower = templateForTower;
        this.buildType = buildType;
        this.coorsX = new Array<>();
        this.coorsY = new Array<>();
    }

    public void setBuildType(boolean buildType) {
        this.buildType = buildType;
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
            int towerSize = templateForTower.size;
            int startX0 = startX;
            int startY0 = startY;
            if (buildType == true) {
                int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
                if (towerSize != 1) {
                    if (towerSize % 2 == 0) {
                        x1 = -(towerSize / 2);
                        y1 = -(towerSize / 2);
                        x2 = (towerSize / 2) - 1;
                        y2 = (towerSize / 2) - 1;
                    } else {
                        x1 = -(towerSize / 2);
                        y1 = -(towerSize / 2);
                        x2 = (towerSize / 2);
                        y2 = (towerSize / 2);
                    }
                }
                startX0 -= x1;
                startY0 -= y1;
                if (endY == startY0 || (endY < (startY0 + towerSize) && endY > startY0)) {
                    if (endX >= startX0) {
                        for (int currX = startX0 + towerSize; currX <= endX; currX += towerSize) {
                            this.coorsX.add(currX + x1);
                            this.coorsY.add(startY0 + y1);
                        }
                    } else {
                        for (int currX = startX0 - towerSize; currX > endX - towerSize; currX -= towerSize) {
                            this.coorsX.add(currX + x1);
                            this.coorsY.add(startY0 + y1);
                        }
                    }
                } else if (endX == startX0 || endX < (startX0 + towerSize) && endX > startX0) {
                    if (endY >= startY0) {
                        for (int currY = startY0 + towerSize; currY <= endY; currY += towerSize) {
                            this.coorsX.add(startX0 + x1);
                            this.coorsY.add(currY + y1);
                        }
                    } else {
                        for (int currY = startY0 - towerSize; currY > endY - towerSize; currY -= towerSize) {
                            this.coorsX.add(startX0 + x1);
                            this.coorsY.add(currY + y1);
                        }
                    }
                }
            } else {
                int x, y, dx, dy, incx, incy;
                dx = (endX - startX0) / towerSize;
                incx = sign(dx) * towerSize;
                dy = (endY - startY0) / towerSize;
                incy = sign(dy) * towerSize;

                if (dx < 0) dx = -dx;
                if (dy < 0) dy = -dy;

                int pdx, pdy, es, el, err;
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
                x = startX0;
                y = startY0;
                err = el / 2;//(towerSize + 1);
//                this.coorsX.add(x);
//                this.coorsY.add(y);
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
