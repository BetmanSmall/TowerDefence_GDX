package com.betmansmall.game.gameLogic.pathfinderAlgorithms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;

/**
 * Created by betmansmall on 18.02.2016.
 */
public class WaveAlgorithm {
    boolean CIRCLET8 = true;

    Array<Integer> mapWithSteps;
    int sizeX, sizeY;
    int exitPointX, exitPointY;
    TiledMapTileLayer layer;
    boolean found;

    public WaveAlgorithm(int sizeX, int sizeY, int exitPointX, int exitPointY, TiledMapTileLayer layer) {
        this.mapWithSteps = new Array<Integer>(sizeX*sizeY);
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.exitPointX = exitPointX;
        this.exitPointY = exitPointY;
        this.layer = layer;
        clearStepsOnWaveAlgorithm();
    }

    public boolean isFound() {
        return found;
    }

    public int getStepCell(int x, int y) {
        if(found) {
            if(x >= 0 && x < sizeX) {
                if (y >= 0 && y < sizeY) {
                    return mapWithSteps.get(sizeX * y + x);
                }
            }
        }
        return -1;
    }

    public void searh() {
        Gdx.app.log("WaveAlgorithm::searh()", "-- Start!");
        searh(exitPointX, exitPointY);
        Gdx.app.log("WaveAlgorithm::searh()", "-- Stop!");
    }

    public void searh(final int x, final int y) {
        clearStepsOnWaveAlgorithm();

        setNumOfCell(x, y, 1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Gdx.app.log("WaveAlgorithm::researh()", "-- Thread start!");
                waveStep(x, y, 1);
                found = true;
                Gdx.app.log("WaveAlgorithm::researh()", "-- Thread stop!");
            }
        }).start();
    }

    void waveStep(int x, int y, int step) {
//        Gdx.app.log("WaveAlgorithm::waveStep()", "-- (" + x + ", " + y + ", " + step + ");");
        //------------3*3----------------
        if(CIRCLET8) {
            boolean mass[][] = new boolean[3][3];
            int nextStep = step + 1;

            for (int tmpY = -1; tmpY < 2; tmpY++)
                for (int tmpX = -1; tmpX < 2; tmpX++)
                    mass[tmpX + 1][tmpY + 1] = setNumOfCell(x + tmpX, y + tmpY, nextStep);

            for (int tmpY = -1; tmpY < 2; tmpY++)
                for (int tmpX = -1; tmpX < 2; tmpX++)
                    if (mass[tmpX + 1][tmpY + 1])
                        waveStep(x + tmpX, y + tmpY, nextStep);
        } else {
            //------------2*2-----------------
            boolean mass[] = new boolean[4];
            int nextStep = step + 1;
            int x1 = x - 1, x2 = x, x3 = x + 1;
            int y1 = y - 1, y2 = y, y3 = y + 1;

            mass[0] = setNumOfCell(x1, y2, nextStep);
            mass[1] = setNumOfCell(x2, y1, nextStep);
            mass[2] = setNumOfCell(x2, y3, nextStep);
            mass[3] = setNumOfCell(x3, y2, nextStep);

            if (mass[0])
                waveStep(x1, y2, nextStep);
            if (mass[1])
                waveStep(x2, y1, nextStep);
            if (mass[2])
                waveStep(x2, y3, nextStep);
            if (mass[3])
                waveStep(x3, y2, nextStep);
        }
    }

    boolean setNumOfCell(int x, int y, int step) {
        if(x >= 0 && x < sizeX) {
            if(y >= 0 && y < sizeY) {
                if(cellIsEmpty(x, y)) {
                    if(getStepCellWithOutIfs(x, y) > step || getStepCellWithOutIfs(x, y) == 0) {
                        setStepCell(x, y, step);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    int getStepCellWithOutIfs(int x, int y) {
        return mapWithSteps.get(sizeX*y + x);
    }

    void setStepCell(int x, int y, int step) {
//        Gdx.app.log("WaveAlgorithm::setStepCell()", "-- x:" + x + " y:" + y + " step:" + step + " sum:" + (sizeX*y + x));
        mapWithSteps.set(sizeX*y + x, step);
    }

    void clearStepsOnWaveAlgorithm() {
        found = false;
        mapWithSteps.clear();
        for(int tmpX = 0; tmpX < sizeX; tmpX++) {
            for(int tmpY = 0; tmpY < sizeY; tmpY++) {
                mapWithSteps.add(0); // КОСТЫЛЬ МАТЬ ЕГО!!!!!
            }
        }
    }

    boolean cellIsEmpty(int x, int y) {
        if(layer.getCell(x, y) != null && layer.getCell(x, y).getTile() != null) {
            Object property = layer.getCell(x, y).getTile().getProperties().get("busy");
            if(property != null) {
                if (property.equals("tower") || property.equals("flora")) {
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }
}
