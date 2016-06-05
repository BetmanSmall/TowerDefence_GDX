package com.betmansmall.game.gameLogic.pathfinderAlgorithms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;
/**
 * Created by betmansmall on 18.02.2016.
 */
public class WaveAlgorithm {
    private boolean CIRCLET8 = true;

    private volatile Array<Integer> mapWithSteps;
    //    private Array<Integer> outMap;
    private int sizeX, sizeY;
    private int exitPointX, exitPointY;
    private TiledMapTileLayer layer;
    private boolean found;
    private SearchThread searchThread = new SearchThread();

    private static volatile int threads = 0;

    private class SearchThread extends Thread {

        private int arg1; // X
        private int arg2; // Y
        private int arg3; // third arg
        private boolean isSearchInterrupted = false;
        private boolean startSearch = false;
        private ThreadGroup threadGroup = new ThreadGroup("subThread 1");

        public void interruptSearch() {
            isSearchInterrupted = true;
        }

        public boolean isSearchInterrupted() {
            return isSearchInterrupted;
        }

        public void setSearchArgs(int arg1, int arg2, int arg3) { // args by default : (x,y,1)
            synchronized (SearchThread.class) {
                this.arg1 = arg1;
                this.arg2 = arg2;
                this.arg3 = arg3;
                startSearch = true;
            }
        }

        private class WaveStepRunnable implements Runnable {
            private int x,y,nextStep;
            public void init(int x,int y,int nextStep) {
                this.x = x;this.y=y;this.nextStep=nextStep;
            }

            @Override
            public void run() {
                waveStep(x , y, nextStep);
            }
        }

        private void waveStep(int x, int y, int step) {
//        Gdx.app.log("WaveAlgorithm::waveStep()", "-- heap:" + Gdx.app.getJavaHeap() + " Step:" + step);
//        if(Thread.currentThread().isInterrupted()) {
//            Gdx.app.log("WaveAlgorithm::waveStep()-isInterrupted", "-- Thread work:" + Thread.currentThread().toString() + " Step:" + step);
//            return;
//        }
            if (this.isSearchInterrupted) {
                return;
            }
            //------------3*3----------------
            if (CIRCLET8) {
                boolean mass[][] = new boolean[3][3];
                int nextStep = step + 1;

                for (int tmpY = -1; tmpY < 2; tmpY++)
                    for (int tmpX = -1; tmpX < 2; tmpX++)
                        mass[tmpX + 1][tmpY + 1] = setNumOfCell(x + tmpX, y + tmpY, nextStep);


                    for (int tmpY = -1; tmpY < 2; tmpY++)
                        for (int tmpX = -1; tmpX < 2; tmpX++)
                            if (mass[tmpX + 1][tmpY + 1]) {
                                if(getStepCellWithOutIfs(x + tmpX, y+tmpY) <= step && getStepCellWithOutIfs(x + tmpX, y+tmpY) != 0) {
                                    continue;
                                }
                                WaveStepRunnable newRunnable = new WaveStepRunnable();
                                newRunnable.init(x + tmpX, y + tmpY, nextStep);
                                new Thread(threadGroup, newRunnable, "thread " + threads).start();
                                threads++;
                            }

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

        @Override
        public void run() {
            while (!this.isInterrupted()) {
                if (this.startSearch) {
                    synchronized (SearchThread.class) {
                        this.startSearch = false;
                        threadGroup.interrupt();
                        waveStep(arg1, arg2, arg3);
                        found = true;
                        this.isSearchInterrupted = false;
                    }
                }
            }
        }
    }

    public WaveAlgorithm(int sizeX, int sizeY, int exitPointX, int exitPointY, TiledMapTileLayer layer) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.exitPointX = exitPointX;
        this.exitPointY = exitPointY;
        this.layer = layer;
        this.mapWithSteps = new Array<Integer>(sizeX * sizeY);
        searchThread.start();
//        this.outMap = new Array<Integer>(sizeX*sizeY);
        clearStepsOnWaveAlgorithm();
    }

    public boolean isFound() {
        return found;
    }

    public int getNumStep(int x, int y) {
        if (found) {
            if (x >= 0 && x < sizeX) {
                if (y >= 0 && y < sizeY) {
                    if (cellIsEmpty(x, y)) {
                        return getStepCellWithOutIfs(x, y);
                    }
                    return 0;
                }
            }
        }
        return -1;
    }

    public void searh() {
        Gdx.app.log("WaveAlgorithm::searh()", "-- Searh start!");
        researh(exitPointX, exitPointY);
        Gdx.app.log("WaveAlgorithm::searh()", "-- Searh stop!");
    }

    public void researh(final int x, final int y) {
        clearStepsOnWaveAlgorithm();
        setNumOfCell(x, y, 1);

        if (searchThread.isSearchInterrupted()) {
            searchThread.interruptSearch();
        }
        searchThread.setSearchArgs(x, y, 1);

    }

    private boolean setNumOfCell(int x, int y, int step) {
        if (x >= 0 && x < sizeX) {
            if (y >= 0 && y < sizeY) {
                if (cellIsEmpty(x, y)) {
                    if (getStepCellWithOutIfs(x, y) > step || getStepCellWithOutIfs(x, y) == 0) {
                        setStepCell(x, y, step);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private int getStepCellWithOutIfs(int x, int y) {
        return mapWithSteps.get(sizeX * y + x);
    }

    private void setStepCell(int x, int y, int step) {
//        Gdx.app.log("WaveAlgorithm::setStepCell()", "-- x:" + x + " y:" + y + " step:" + step + " sum:" + (sizeX*y + x));
        mapWithSteps.set(sizeX * y + x, step);
    }

    private void clearStepsOnWaveAlgorithm() {
        found = false;
        mapWithSteps.clear();
        for (int tmpX = 0; tmpX < sizeX; tmpX++) {
            for (int tmpY = 0; tmpY < sizeY; tmpY++) {
                mapWithSteps.add(0); // КОСТЫЛЬ МАТЬ ЕГО!!!!!
            }
        }
    }

    private boolean cellIsEmpty(int x, int y) {
        if (layer.getCell(x, y) != null && layer.getCell(x, y).getTile() != null) {
            Object property = layer.getCell(x, y).getTile().getProperties().get("busy");
            if (property != null) {
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
