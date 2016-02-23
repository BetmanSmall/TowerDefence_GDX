package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.betmansmall.game.WhichCell;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.WaveAlgorithm;
import com.betmansmall.game.gameLogic.playerTemplates.FactionsManager;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;

import java.util.ArrayList;

/**
 * Created by betmansmall on 08.02.2016.
 */
public class GameField {
    private ShapeRenderer sr = new ShapeRenderer();
    private SpriteBatch batch = new SpriteBatch();
    private BitmapFont font = new BitmapFont();

    public boolean isDrawableGrid = true;
    public boolean isDrawableSteps = true;

    private TiledMap map;
    private IsometricTiledMapRenderer renderer;
    private int sizeFieldX, sizeFieldY;
    private int sizeCellX, sizeCellY;

    private static TiledMapTileLayer layerBackGround, layerForeGround;
    private GridPoint2 spawnPoint, exitPoint;
    public WaveAlgorithm waveAlgorithm;

//    private int currentFinishedCreeps = 0, gameOverLimitCreeps = 20;
    private int defaultNumCreateCreeps = 10;
    private CreepsManager creepsManager;
    public TowersManager towersManager;
    private FactionsManager factionsManager;

    private float intervalForTimerCreeps = 1f;
    private Timer.Task timerForCreeps;

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
			for(int y = 0; y < sizeFieldY; y++) {
				TiledMapTileLayer.Cell cell = layerBackGround.getCell(x, y);
                if(cell != null) {
                    if(cell.getTile().getProperties().get("spawnPoint") != null && cell.getTile().getProperties().get("spawnPoint").equals("1")) {
                        spawnPoint = new GridPoint2(x, y);
                        Gdx.app.log("GameField::GameField()", "-- Set spawnPoint:" + spawnPoint);
                    }
                    if(cell.getTile().getProperties().get("exitPoint") != null && cell.getTile().getProperties().get("exitPoint").equals("1")) {
                        exitPoint = new GridPoint2(x, y);
                        Gdx.app.log("GameField::GameField()", "-- Set exitPoint:" + exitPoint);
                    }
                }
			}
		}
        waveAlgorithm = new WaveAlgorithm(sizeFieldX, sizeFieldY, exitPoint.x, exitPoint.y, layerForeGround);

        creepsManager = new CreepsManager(defaultNumCreateCreeps);
        towersManager = new TowersManager();
        factionsManager = new FactionsManager();

		TiledMapTileSets tileSets = map.getTileSets();
		for(TiledMapTileSet tileSet:tileSets) {
            String tileSetName = tileSet.getName();
            Gdx.app.log("GameField::GameField()", "-- TileSet:" + tileSetName);
            if(tileSetName.contains("unit")) {
                TemplateForUnit unit = new TemplateForUnit(tileSet);
                factionsManager.addUnitToFaction(unit);
            } else if(tileSetName.contains("tower")) {
                TemplateForTower tower = new TemplateForTower(tileSet);
                factionsManager.addTowerToFaction(tower);
            }
		}
    }

    public void dispose() {
        stopTimerForCreeps();
		map.dispose();
		map = null;
		renderer.dispose();
		renderer = null;
    }

    public void render(float delta, OrthographicCamera camera) {
        renderer.setView(camera);
        renderer.render();

        if(isDrawableGrid)
            drawGrid(camera);
        if(isDrawableSteps)
            drawStepsAndMouse(camera);
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
        if(waveAlgorithm.isFound()) {
            int halfSizeCellX = sizeCellX / 2;
            int halfSizeCellY = sizeCellY / 2;

            int isometricCoorX = 0;
            int isometricCoorY = 0;

            batch.setProjectionMatrix(camera.combined);
            batch.begin();

            for (int y = 0; y < sizeFieldY; y++) {
                for (int x = 0; x < sizeFieldX; x++) {
                    float x1 = isometricCoorX + (halfSizeCellX-5) + x * halfSizeCellX;
                    float y1 = isometricCoorY + (halfSizeCellY+5) - x * halfSizeCellY;
                    String str = String.valueOf(waveAlgorithm.getNumStep(x, y));
//                    Gdx.app.log("GameField::drawStepsAndMouse()", "-- x1:" + x1 + " y1:" + y1 + " step:" + str);
                    font.draw(batch, str, x1, y1);
                }
                isometricCoorX = halfSizeCellX * (y + 1);
                isometricCoorY = halfSizeCellY * (y + 1);
            }

            batch.end();
        }
    }

    public void createTimerForCreeps(){
        if(timerForCreeps == null) {
            timerForCreeps = Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
//                    Gdx.app.log("GameField::createTimerForCreeps()", "-- Timer for Creeps! Delta:" + timerForCreeps.getExecuteTimeMillis());
                    if(spawnPoint != null) {
                        if(creepsManager.amountCreeps() < defaultNumCreateCreeps) {
                            creepsManager.createCreep(spawnPoint, layerForeGround, factionsManager.getRandomTemplateForUnitFromFirstFaction());
                        }
                    }
                    stepAllCreeps();
                }
            }, 0, intervalForTimerCreeps);
        }
    }

    private void stopTimerForCreeps() {
        Gdx.app.log("GameField::stopTimerForCreeps()", "Stop timer for creeps!");
        timerForCreeps.cancel();
        timerForCreeps = null;
    }

    public GridPoint2 whichCell(GridPoint2 gameCoor) {
        int sizeFieldX = getSizeFieldX();
        int sizeFieldY = getSizeFieldY();
        int sizeCellX = getSizeCellX();
        int sizeCellY = getSizeCellY();
        int halfSizeCellX = sizeCellX/2;
        int halfSizeCellY = sizeCellY/2;

        for(int tileX = 0; tileX < sizeFieldX; tileX++) {
            for(int tileY = 0; tileY < sizeFieldY; tileY++) {
                float posX = (tileX*halfSizeCellX) + (tileY*halfSizeCellX);
                float posY = -(tileX*halfSizeCellY) + (tileY*halfSizeCellY) + halfSizeCellY;

                ArrayList<Vector2> tilePoints = new ArrayList<Vector2>();
                tilePoints.add(new Vector2(posX, posY));
                tilePoints.add(new Vector2(posX + halfSizeCellX, posY + halfSizeCellY));
                tilePoints.add(new Vector2(posX + sizeCellX, posY));
                tilePoints.add(new Vector2(posX + halfSizeCellX, posY - halfSizeCellY));
                if(WhichCell.estimation(tilePoints, gameCoor)) {
//                    Gdx.app.log("GameField::whichCell()", "-- posX:" + posX + " posY:" + posY);
//                    Gdx.app.log("GameField::whichCell()", "-- tilePoints:" + tilePoints.toString());
//                    Gdx.app.log("GameField::whichCell()", "-- x:" + tileX + " y:" + tileY);
                    return new GridPoint2(tileX, tileY);
                }
            }
        }
        return null;
    }

    public void createTower(GridPoint2 position) {
        towersManager.createTower(position, layerForeGround, factionsManager.getRandomTemplateForTowerFromFirstFaction());
    }
//    public void searhPath() {
//        waveAlgorithm.searh();
//    }

    /**
     * @brief Говорит всем криппам ходить
     * @return 2 - Все криппы мертвы
     * @return 1 - Eсли колличество криппов в точке @exitPoint превышено $gameOverLimitCreeps
     * @return 0 - Все криппы сходили успешно
     * @return -1 - Какому-либо криппу перекрыли путь до $exitPoint
     */
    private int stepAllCreeps() {
        boolean allDead = true;
        for(int k = 0; k < creepsManager.amountCreeps(); k++) {
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

    private int stepOneCreep(int creepId) {
        Creep tmpCreep = creepsManager.getCreep(creepId);
        if(tmpCreep.isAlive()) {
//            tmpCreep.getTemplate().
            int currX = tmpCreep.getPosition().x;
            int currY = tmpCreep.getPosition().y;

            int exitX = currX, exitY = currY;

            int min = waveAlgorithm.getNumStep(currX, currY);

            if(min == 1)
                return 1;
            if(min == 0)
                return -1;

            int defaultStep = min;
            //--------------Looking specific cell-----------------------
            for(int tmpY = -1; tmpY < 2; tmpY++)
                for(int tmpX = -1; tmpX < 2; tmpX++)
                    if(!(tmpX == 0 && tmpY == 0)) {
                        int num = waveAlgorithm.getNumStep(currX + tmpX, currY + tmpY);
//                        Gdx.app.log("GameField::stepOneCreep()", "-- num:" + num);
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
                Gdx.app.log("GameField::stepOneCreep()", "-- Creep move to X:" + exitX + " Y:" + exitY);
                creepsManager.getCreep(creepId).moveTo(new GridPoint2(exitX, exitY));
            } else {
                return 0;
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

    public boolean cellIsEmpty(int x, int y) {
        TiledMapTileLayer layer = layerForeGround;
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

    public static boolean cellIsCreep(int x, int y) {
        TiledMapTileLayer layer = layerForeGround;
        if(layer.getCell(x, y) != null && layer.getCell(x, y).getTile() != null) {
            Object property = layer.getCell(x, y).getTile().getProperties().get("busy");
            if(property != null) {
                if (property.equals("creep")) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    public static boolean cellIsTower(int x, int y) {
        TiledMapTileLayer layer = layerForeGround;
        if(layer.getCell(x, y) != null && layer.getCell(x, y).getTile() != null) {
            Object property = layer.getCell(x, y).getTile().getProperties().get("busy");
            if(property != null) {
                if (property.equals("tower")) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    public TiledMapTileLayer getLayerBackGround() {
        return layerBackGround;
    }

    public TiledMapTileLayer getLayerForeGround() {
        return layerForeGround;
    }

//    public void attackCreep(GridPoint2 position) {
//        for(int i=-1;i<=1;i++) {
//            for(int j=-1;j<=1;j++) {
//                if(cellIsCreep(position.x + i, position.y + j)) {
//                    creepsManager.getCreep(new GridPoint2(position.x + i, position.y + j)).setHp(0);
//                    if (creepsManager.getCreep(new GridPoint2(position.x + i, position.y + j)).getHp() <= 0) {
//                        creepsManager.getCreep(new GridPoint2(position.x + i, position.y + j)).setAlive(false);
//                    }
//                }
//            }
//        }
//    }
}
