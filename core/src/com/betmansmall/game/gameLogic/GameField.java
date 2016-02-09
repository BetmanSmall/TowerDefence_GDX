package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.betmansmall.game.CollisionDetection;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by betmansmall on 08.02.2016.
 */
public class GameField {
    ShapeRenderer sr = new ShapeRenderer();
    SpriteBatch batch = new SpriteBatch();
    BitmapFont font = new BitmapFont();

    TiledMap map;
    IsometricTiledMapRenderer renderer;
    int sizeFieldX, sizeFieldY;
    int sizeCellX, sizeCellY;

    GridPoint2 spawnPoint, exitPoint;
    static TiledMapTileLayer layerBackGround, layerForeGround;

//    Creeps creeps;

    TiledMapTile defaultTileForCreeps;
     static Array<Creep> creeps;
    Array<Tower> towers;

    Array<Integer> stepsForWaveAlgorithm;
//    int currentFinishedCreeps = 0, gameOverLimitCreeps = 20;
    int defaultNumCreateCreeps = 3;

    float intervalForTimerCreeps = 1f;
    Timer.Task timerForCreeps;

    HashMap<String, TiledMapTile> creepTiles, towerTiles;

    public GameField(String mapName) {
        map = new TmxMapLoader().load(mapName);
        renderer = new IsometricTiledMapRenderer(map);

        sizeFieldX = map.getProperties().get("width", Integer.class);
        sizeFieldY = map.getProperties().get("height", Integer.class);
        sizeCellX = map.getProperties().get("tilewidth", Integer.class);
        sizeCellY = map.getProperties().get("tileheight", Integer.class);

        layerBackGround = (TiledMapTileLayer) map.getLayers().get("background");
        layerForeGround = (TiledMapTileLayer) map.getLayers().get("foreground");

		for(int x = 0; x < sizeFieldX; x++) {
			for(int y = 0; y < sizeFieldY; y++){
				TiledMapTileLayer.Cell cell = layerBackGround.getCell(x, y);
				if(cell.getTile().getProperties().get("spawnPoint") != null && cell.getTile().getProperties().get("spawnPoint").equals("1")) {
                    Gdx.app.log("spawnPoint", "set! " + new GridPoint2(x, y));
                    spawnPoint = new GridPoint2(x, y);
				}
				if(cell.getTile().getProperties().get("exitPoint") != null && cell.getTile().getProperties().get("exitPoint").equals("1")) {
                    Gdx.app.log("exitPoint", "set! " + new GridPoint2(x, y));
					exitPoint = new GridPoint2(x, y);
				}
			}
		}

//        creeps = new Creeps(1);

        creeps = new Array<Creep>();
        towers = new Array<Tower>();

        stepsForWaveAlgorithm = new Array<Integer>(sizeFieldX*sizeFieldY);

		TiledMapTileSet tileset = map.getTileSets().getTileSet("creep");
        creepTiles = new HashMap<String,TiledMapTile>();
		towerTiles = new HashMap<String,TiledMapTile>();

//		//Search in tileset objects with property "creep" and put them in waterTiles
		for(TiledMapTile tile:tileset){
			Object property = tile.getProperties().get("creep");
			if(property != null) {
                creepTiles.put((String) property, tile);
                defaultTileForCreeps = tile;
			}
		}
		tileset =  map.getTileSets().getTileSet("tower");
		for(TiledMapTile tile:tileset){
			Object property = tile.getProperties().get("tower");
			if(property != null) {
				towerTiles.put((String) property, tile);
			}
		}
    }

    public void dispose() {
		map.dispose();
		map = null;
		renderer.dispose();
		renderer = null;
    }

    public void render(float delta, OrthographicCamera camera) {
        renderer.setView(camera);
        renderer.render();

        drawGrid(camera);
        drawStepsAndMouse(camera);

//        batch.begin();
//        font.draw(batch, "test", 50, 50);
//        batch.end();
    }

	private void drawGrid(OrthographicCamera camera) {
        int mapWidth = sizeFieldX * sizeCellX;
        int mapHeight = sizeFieldY * sizeCellY;

		int halfSizeCellX = sizeCellX/2;
		int halfSizeCellY = sizeCellY/2;

		sr.setProjectionMatrix(camera.combined);
		sr.begin(ShapeRenderer.ShapeType.Line);
		sr.setColor(Color.BROWN); // (100, 60, 21, 1f);

		for(int x = 0; x <= sizeFieldX; x++)
			sr.line(x*halfSizeCellX, halfSizeCellY - x*halfSizeCellY, mapWidth/2 + x*halfSizeCellX, halfSizeCellY + mapHeight/2 - x*halfSizeCellY);
		for(int y = 0; y <= sizeFieldY; y++)
			sr.line(y*halfSizeCellX, halfSizeCellY + y*halfSizeCellY, mapWidth/2 + y*halfSizeCellX, halfSizeCellY -(mapHeight/2) + y*halfSizeCellY);

		sr.end();
	}

    private void drawStepsAndMouse(OrthographicCamera camera) {
        if(stepsForWaveAlgorithm.size != 0) {

            int halfSizeCellX = sizeCellX / 2;
            int halfSizeCellY = sizeCellY / 2;

            int isometricCoorX = 0;
            int isometricCoorY = halfSizeCellY;

            batch.setProjectionMatrix(camera.combined);
            batch.begin();

            for (int y = 0; y < sizeFieldY; y++) {
                for (int x = 0; x < sizeFieldX; x++) {
                    float x1 = isometricCoorX + x * (sizeCellX / 2);
                    float y1 = isometricCoorY + halfSizeCellY + x * halfSizeCellY;
                    String str = stepsForWaveAlgorithm.get(sizeFieldX * y + x).toString();
                    Gdx.app.log("drawStepsAndMouse", "x1=" + x1 + " y1=" + y1 + " step=" + str);
                    font.draw(batch, str, x1, y1);
//            sr.line(x * halfSizeCellX, halfSizeCellY - x * halfSizeCellY, mapWidth / 2 + x * halfSizeCellX, halfSizeCellY + mapHeight / 2 - x * halfSizeCellY);
//            sr.line(y * halfSizeCellX, halfSizeCellY + y * halfSizeCellY, mapWidth / 2 + y * halfSizeCellX, halfSizeCellY - (mapHeight / 2) + y * halfSizeCellY);
                }
                isometricCoorX = halfSizeCellX * (sizeFieldY - (y + 1));
                isometricCoorY = (halfSizeCellY / 2) * (y + 1);
            }

            batch.end();
        }
    }

    public void createTimerForCreeps(){
        if(timerForCreeps == null) {
            timerForCreeps = Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    Gdx.app.log("Timer", "for Creeps!");

                    if(spawnPoint != null) {
                        if(creeps.size < defaultNumCreateCreeps) {
                            creeps.add(new Creep(layerForeGround, defaultTileForCreeps, new GridPoint2(spawnPoint.x, spawnPoint.y)));
                        }
                    }
                    stepAllCreeps();
                }
            }, 0, intervalForTimerCreeps);
        }
    }

    private void stopTimerForCreeps() {
        timerForCreeps.cancel();
        timerForCreeps = null;
    }

    public void waveAlgorithm() {
        waveAlgorithm(-1, -1);
        Gdx.app.log("waveAlgorithm", "stop work!");
    }

    public void waveAlgorithm(int x, int y) {
        Gdx.app.log("WaveAlgorim", "x=" + x + ",y=" + y);
        if(x == -1 && y == -1) {
            if (exitPoint != null) {
                waveAlgorithm(exitPoint.x, exitPoint.y);
                return;
            }
        }


        for(int tmpX = 0; tmpX < sizeFieldX; tmpX++) {
            for(int tmpY = 0; tmpY < sizeFieldY; tmpY++) {
                stepsForWaveAlgorithm.add(0); // КОСТЫЛЬ МАТЬ ЕГО!!!!!
            }
        }
        Gdx.app.log("waveAlgorithm", "stepsForWaveAlgorithm: " + stepsForWaveAlgorithm.size);

        setStepCell(x, y, 1);

        waveStep(x, y, 1);
    }

    void waveStep(int x, int y, int step) {
        //------------3*3----------------
        boolean mass[][] = new boolean[3][3];
        int nextStep = step + 1;

        for (int tmpY = -1; tmpY < 2; tmpY++)
            for (int tmpX = -1; tmpX < 2; tmpX++)
                mass[tmpX + 1][tmpY + 1] = setNumOfCell(x + tmpX, y + tmpY, nextStep);

        for (int tmpY = -1; tmpY < 2; tmpY++)
            for (int tmpX = -1; tmpX < 2; tmpX++)
                if (mass[tmpX + 1][tmpY + 1])
                    waveStep(x + tmpX, y + tmpY, nextStep);

    }

    boolean setNumOfCell(int x, int y, int step) {
        if(x >= 0 && x < sizeFieldX) {
            if(y >= 0 && y < sizeFieldY) {
                if(CollisionDetection.cellIsEmpty(x, y, layerForeGround)) {
                    if(getStepCell(x, y) > step || getStepCell(x, y) == 0) {
                        setStepCell(x, y, step);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    int getStepCell(int x, int y) {
        return stepsForWaveAlgorithm.get(sizeFieldX*y + x);
    }

    void setStepCell(int x, int y, int step) {
//        Gdx.app.log("setStepCell", "x=" + x + " y=" + y + " step=" + step + " sum=" + (sizeFieldX*y + x));
        stepsForWaveAlgorithm.set(sizeFieldX*y + x, step);
    }

    void clearStepCell(int x, int y) {
        stepsForWaveAlgorithm.set(sizeFieldX*y + x, 0);
    }

    int stepAllCreeps() {
        boolean allDead = true;
        for(int k = 0; k < creeps.size; k++) {
            int result = stepOneCreep(k);
            if(result != -2)
                allDead = false;

            if(result == 1) {
//                currentFinishedCreeps++;
//                if(currentFinishedCreeps >= gameOverLimitCreeps)
//                    return 1;
            }
            else if(result == -1)
                return -1;
        }

        if(allDead)
            return 2;
        else
            return 0;
    }

    int stepOneCreep(int creepId) {
        com.betmansmall.game.gameLogic.Creep tmpCreep = creeps.get(creepId);
        if(tmpCreep.isAlive()) {
            int currX = tmpCreep.getPosition().x;
            int currY = tmpCreep.getPosition().y;

            int exitX = currX, exitY = currY;

            int min = getNumStep(currX,currY);

            if(min == 1)
                return 1;
            if(min == 0)
                return -1;

            int defaultStep = min;
            //--------------Looking specific cell-----------------------
            for(int tmpY = -1; tmpY < 2; tmpY++)
                for(int tmpX = -1; tmpX < 2; tmpX++)
                    if(!(tmpX == 0 && tmpY == 0)) {
                        int num = getNumStep(currX + tmpX, currY + tmpY);
//                            Log.d("TTW", "stepOneCreep() -- num: " + num);
                        if(num <= min && num != 0) {
                            if(num == min) {
                                if( ((int) (Math.random()*2)) == 1) {
                                    exitX = currX + tmpX;
                                    exitY = currY + tmpY;
                                }
                            } else if(num == defaultStep-1) {
                                exitX = currX + tmpX;
                                exitY = currY + tmpY;
                                min = num;
                            }
                        }
                    }
            //-----------------------------------------------------------

            if(exitX != currX || exitY != currY)
            {
                Gdx.app.log("Creep", "move to: x=" + exitX + " y=" + exitY);
                creeps.get(creepId).moveTo(new GridPoint2(exitX, exitY));
            } else {
                return 0;
            }
//            }
        }
        return 0;
    }

    int getNumStep(int x, int y) {
        if(x >= 0 && x < sizeFieldX) {
            if(y >= 0 && y < sizeFieldY) {
                if(CollisionDetection.cellIsEmpty(x, y, layerForeGround)) {
                    return getStepCell(x, y);
                }
            }
        }
        return 0;
    }

    public int getSizeFieldX() {
        return sizeFieldX;
    }

    public int getSizeFieldY() {
        return sizeFieldY;
    }

    public int getSizeCellX() {
        return sizeCellX;
    }

    public int getSizeCellY() {
        return sizeCellY;
    }

    public TiledMapTileLayer getLayerBackGround() {
        return layerBackGround;
    }

    public TiledMapTileLayer getLayerForeGround() {
        return layerForeGround;
    }

    public Array<Creep> getCreeps() {
        return creeps;
    }

    public Array<Tower> getTowers() {
        return towers;
    }

    public HashMap<String, TiledMapTile> getTowerTiles() {
        return towerTiles;
    }
    public static Creep getCreep(GridPoint2 position) {
        for(int i=0;i<creeps.size;i++) {
            if(creeps.get(i).getPosition().x == position.x &&
               creeps.get(i).getPosition().y == position.y) {
                return creeps.get(i);
            }
        }
        return null;
    }
    public static void attackCreep(GridPoint2 position) {
        if(CollisionDetection.cellIsCreep(position.x-1,position.y-1, layerForeGround)) {
            getCreep(new GridPoint2(position.x-1,position.y-1)).setHp(0);
            if(getCreep(new GridPoint2(position.x-1,position.y-1)).getHp() <= 0) {
                getCreep(new GridPoint2(position.x-1,position.y-1)).setAlive(false);
            }
        } else if(CollisionDetection.cellIsCreep(position.x,position.y-1, layerForeGround)) {
            getCreep(new GridPoint2(position.x,position.y-1)).setHp(0);
            if(getCreep(new GridPoint2(position.x,position.y-1)).getHp() <= 0) {
                getCreep(new GridPoint2(position.x,position.y-1)).setAlive(false);
            }

        } else if(CollisionDetection.cellIsCreep(position.x+1,position.y-1, layerForeGround)) {
            getCreep(new GridPoint2(position.x+1,position.y-1)).setHp(0);
            if(getCreep(new GridPoint2(position.x+1,position.y-1)).getHp() <= 0) {
                getCreep(new GridPoint2(position.x+1,position.y-1)).setAlive(false);
            }

        } else if(CollisionDetection.cellIsCreep(position.x+1,position.y, layerForeGround)) {
            getCreep(new GridPoint2(position.x+1,position.y)).setHp(0);
            if(getCreep(new GridPoint2(position.x+1,position.y)).getHp() <= 0) {
                getCreep(new GridPoint2(position.x+1,position.y)).setAlive(false);
            }

        } else if(CollisionDetection.cellIsCreep(position.x+1,position.y+1, layerForeGround)) {
            getCreep(new GridPoint2(position.x+1,position.y+1)).setHp(0);
            if(getCreep(new GridPoint2(position.x+1,position.y+1)).getHp() <= 0) {
                getCreep(new GridPoint2(position.x+1,position.y+1)).setAlive(false);
            }

        } else if(CollisionDetection.cellIsCreep(position.x,position.y+1, layerForeGround)) {
            getCreep(new GridPoint2(position.x,position.y+1)).setHp(0);
            if(getCreep(new GridPoint2(position.x,position.y+1)).getHp() <= 0) {
                getCreep(new GridPoint2(position.x,position.y+1)).setAlive(false);
            }

        } else if(CollisionDetection.cellIsCreep(position.x-1,position.y+1, layerForeGround)) {
            getCreep(new GridPoint2(position.x-1,position.y+1)).setHp(0);
            if(getCreep(new GridPoint2(position.x-1,position.y+1)).getHp() <= 0) {
                getCreep(new GridPoint2(position.x-1,position.y+1)).setAlive(false);
            }

        } else if(CollisionDetection.cellIsCreep(position.x-1,position.y, layerForeGround)) {
            getCreep(new GridPoint2(position.x-1,position.y)).setHp(0);
            if(getCreep(new GridPoint2(position.x-1,position.y)).getHp() <= 0) {
                getCreep(new GridPoint2(position.x-1,position.y)).setAlive(false);
            }
        }
    }
}
