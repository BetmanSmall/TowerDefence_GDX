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
            int towerSize = templateForTower.size;
            int deltaX = 0, deltaY=0;
            if(towerSize != 1) {
                if(towerSize%2 == 0) {
                    deltaX = towerSize/2;
                    deltaY = (towerSize/2)-1;
                } else {
                    deltaX = towerSize/2;
                    deltaY = towerSize/2;
                }
            }
            int tmpX = startX-deltaX;
            int tmpY = startY-deltaY;
            if(endY == tmpY || (endY < (tmpY+towerSize) && endY > tmpY)) {
                if(endX >= tmpX) {
                    for(int currX = tmpX+towerSize; currX <= endX; currX+=towerSize) {
                        this.coorsX.add(currX+deltaX);
                        this.coorsY.add(tmpY+deltaY);
                    }
                } else {
                    for(int currX = tmpX-towerSize; currX > endX-towerSize; currX-=towerSize) {
                        this.coorsX.add(currX+deltaX);
                        this.coorsY.add(tmpY+deltaY);
                    }
                }
            } else if(endX == tmpX || endX < (tmpX+towerSize) && endX > tmpX) {
                if(endY >= tmpY) {
                    for(int currY = tmpY+towerSize; currY <= endY; currY+=towerSize) {
                        this.coorsX.add(tmpX+deltaX);
                        this.coorsY.add(currY+deltaY);
                    }
                } else {
                    for(int currY = tmpY-towerSize; currY > endY-towerSize; currY-=towerSize) {
                        this.coorsX.add(tmpX+deltaX);
                        this.coorsY.add(currY+deltaY);
                    }
                }
            }
            return true;
        }
        return false;
    }

    public boolean clearStartCoors() {
        this.state = 0;
        this.coorsX.clear();
        this.coorsY.clear();
        return true;
    }

    public void dispose() {
        clearStartCoors();
        templateForTower = null;
        coorsX = null;
        coorsY = null;
    }
}
