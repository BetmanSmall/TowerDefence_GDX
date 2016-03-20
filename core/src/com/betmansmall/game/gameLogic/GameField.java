package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.betmansmall.game.WhichCell;
import com.betmansmall.game.gameLogic.GridNav.GridNav;
import com.betmansmall.game.gameLogic.GridNav.Options;
import com.betmansmall.game.gameLogic.GridNav.Vertex;
import com.betmansmall.game.gameLogic.playerTemplates.factionsManager;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by betmansmall on 08.02.2016.
 */
public class GameField {
    private ShapeRenderer sr = new ShapeRenderer();

    public boolean isDrawableGrid = true;
    public boolean isDrawableSteps = true;

    private TiledMap map;
    private IsometricTiledMapRenderer renderer;
    private int sizeFieldX, sizeFieldY;
    private int sizeCellX, sizeCellY;

    private static TiledMapTileLayer layerBackGround, layerForeGround;
    private GridPoint2 spawnPoint, exitPoint;
    public GridNav gridNav;

    private int defaultNumCreateCreeps = 100;
    private CreepsManager creepsManager;
    public TowersManager towersManager;
    private com.betmansmall.game.gameLogic.playerTemplates.factionsManager factionsManager;
    private CellManager cellManager;

    private float intervalForSpawnCreeps = 1f;
    private float elapsedTimeForSpawn = 0f;
    private Timer.Task timerSpawnCreeps;
    private boolean gamePaused;

    public GameField(String mapName) {
        map = new TmxMapLoader().load(mapName);
        renderer = new IsometricTiledMapRenderer(map);

        sizeFieldX = map.getProperties().get("width", Integer.class);
        sizeFieldY = map.getProperties().get("height", Integer.class);
        sizeCellX = map.getProperties().get("tilewidth", Integer.class);
        sizeCellY = map.getProperties().get("tileheight", Integer.class);

        layerBackGround = (TiledMapTileLayer) map.getLayers().get("background");
        layerForeGround = (TiledMapTileLayer) map.getLayers().get("foreground");

        cellManager = new CellManager(sizeFieldX, sizeFieldY);
		for(int x = 0; x < sizeFieldX; x++) {
			for(int y = 0; y < sizeFieldY; y++) {
                cellManager.setCell(x, y, new Cell());
				TiledMapTileLayer.Cell cell = layerBackGround.getCell(x, y);
                TiledMapTileLayer.Cell cellFore = layerForeGround.getCell(x, y);
                if(cell != null) {
                    if (cell.getTile().getProperties().get("spawnPoint") != null && cell.getTile().getProperties().get("spawnPoint").equals("1")) {
                        spawnPoint = new GridPoint2(x, y);
                        cellManager.getCell(x, y).setSpawnPoint(true);
                        Gdx.app.log("GameField::GameField()", "-- Set spawnPoint:" + spawnPoint);
                    }
                    if (cell.getTile().getProperties().get("exitPoint") != null && cell.getTile().getProperties().get("exitPoint").equals("1")) {
                        exitPoint = new GridPoint2(x, y);
                        cellManager.getCell(x, y).setExitPoint(true);
                        Gdx.app.log("GameField::GameField()", "-- Set exitPoint:" + exitPoint);
                    }
                }
                if(cellFore != null && cellFore.getTile().getProperties().get("busy") != null) {
                    cellManager.getCell(x, y).setPathFinder('T');
                }
                else {
                    cellManager.getCell(x, y).setPathFinder('.');
                }
			}
		}
        gridNav = new GridNav();
        gridNav.loadCharMatrix(cellManager.getCharMatrix());
        creepsManager = new CreepsManager(defaultNumCreateCreeps);
        towersManager = new TowersManager();
        factionsManager = new factionsManager();

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
        gamePaused = true;
    }

    public void dispose() {
//        stopSpawnTimerForCreeps();
		map.dispose();
		map = null;
		renderer.dispose();
		renderer = null;
    }

    public void render(float delta, OrthographicCamera camera) {
        renderer.setView(camera);
        renderer.render();

        if(!gamePaused) {
            spawnCreep(delta);
            stepAllCreep(delta);
            attackCreeps(delta);
        }

        if(isDrawableGrid)
            drawGrid(camera);
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

    public void setGamePause(boolean gamePaused) {
        this.gamePaused = gamePaused;
    }

    public boolean getGamePaused() {
        return gamePaused;
    }

//    private void createSpawnTimerForCreeps(){
//        if(timerSpawnCreeps == null) {
//            timerSpawnCreeps = Timer.schedule(new Timer.Task() {
//                @Override
//                public void run() {
////                    Gdx.app.log("GameField::createTimerForCreeps()", "-- Timer for Creeps! Delta:" + timerForCreeps.getExecuteTimeMillis());
//                    spawnCreep();
//                }
//            }, 0, intervalForSpawnCreeps);
//        }
//    }
//
//    private void stopSpawnTimerForCreeps() {
//        if(timerSpawnCreeps != null) {
//            Gdx.app.log("GameField::stopTimerForCreeps()", "Stop timer for creeps!");
//            timerSpawnCreeps.cancel();
//            timerSpawnCreeps = null;
//        }
//    }

    private void spawnCreep(float delta) {
        elapsedTimeForSpawn += delta;
        if(elapsedTimeForSpawn > intervalForSpawnCreeps) {
            elapsedTimeForSpawn = 0f;
            if (creepsManager.amountCreeps() < defaultNumCreateCreeps) {
                if (spawnPoint != null) {
                    Creep creep = creepsManager.createCreep(spawnPoint, layerForeGround, factionsManager.getRandomTemplateForUnitFromFirstFaction());
                    cellManager.getCell(spawnPoint.x, spawnPoint.y).addCreep(creep);
                    creep.setRoute(gridNav, spawnPoint, exitPoint);
                }
            }
        }
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
                    return new GridPoint2(tileX, tileY);
                }
            }
        }
        return null;
    }

    public void towerActions(GridPoint2 position) {
        if(cellManager.getCell(position.x, position.y).isEmpty()) { //Set Tower
            cellManager.getCell(position.x, position.y).setPathFinder('T');
            gridNav.loadCharMatrix(cellManager.getCharMatrix());
            ArrayDeque<Vertex> adv = gridNav.route(new int[]{spawnPoint.x, spawnPoint.y},
                    new int[]{exitPoint.x, exitPoint.y}, Options.ASTAR, Options.EUCLIDEAN_HEURISTIC, true);
            if(creepsManager.setRouteForCreeps(gridNav, exitPoint) && adv != null) { //Set tower if there is route to exit point
                cellManager.getCell(position.x, position.y).setTower(
                        towersManager.createTower(position, layerForeGround, factionsManager.getRandomTemplateForTowerFromFirstFaction()));
            } else {
                Gdx.app.log("ERROR: Impossible to set tower in {"+position.x+", "+position.y+"}", "creep route not found");
                cellManager.getCell(position.x, position.y).setPathFinder('.');
                gridNav.loadCharMatrix(cellManager.getCharMatrix());
                creepsManager.setRouteForCreeps(gridNav, exitPoint);
            }
        } else if (cellManager.getCell(position.x, position.y).isTower()) { //Remove tower
            towersManager.removeTower(position, layerForeGround);
            cellManager.getCell(position.x, position.y).removeTower();
            cellManager.getCell(position.x, position.y).setPathFinder('.');
            gridNav.loadCharMatrix(cellManager.getCharMatrix());
            creepsManager.setRouteForCreeps(gridNav, exitPoint);
        }
    }

    private void stepAllCreep(float delta) {
        for(int i=0;i<creepsManager.amountCreeps();i++) {
            Creep creep = creepsManager.getCreep(i);
            float elTime = creep.getElapsedTime()+delta;
            if(elTime >= creep.getSpeed()) {
                creep.setElapsedTime(0);
                stepOneCreep(creep);
            }
            else creep.setElapsedTime(elTime);
        }
    }

    private int stepOneCreep(Creep tmpCreep) {
        int currX = tmpCreep.getPosition().x;
        int currY = tmpCreep.getPosition().y;

        int exitX = currX, exitY = currY;

        Vertex v = getNextStep(currX, currY);
        if (v != null) {
            exitX = v.getY();
            exitY = v.getX();

            Gdx.app.log("GameField::stepOneCreep()", "-- Creep move to X:" + exitX + " Y:" + exitY);
            cellManager.getCell(currX, currY).removeCreep(tmpCreep);
            tmpCreep.moveTo(new GridPoint2(exitX, exitY));
            cellManager.getCell(exitX, exitY).addCreep(tmpCreep);
        }
        return 0;
    }

    public Vertex getNextStep(int x, int y) {
        Creep creep = creepsManager.getCreep(new GridPoint2(x, y));
        ArrayDeque<Vertex> bestroute = creep.getRoute();
        Iterator it = bestroute.iterator();
        while(it.hasNext()) {
            Vertex v = (Vertex) it.next();
            if(v.getX() == exitPoint.y && v.getY() == exitPoint.x) {
                creepsManager.removeCreep(creep);
                layerForeGround.getCell(x, y).setTile(null);
                return null;
            }
            if (v.getX() == y && v.getY() == x) {
                return (Vertex) it.next();
            }
        }
        return null;
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

    private void attackCreeps(float delta) {
        for(int i=0;i<towersManager.amountTowers();i++) {
            Tower tower = towersManager.getTower(i);
            float elTime = tower.getElapsedTime()+delta;
            if(elTime >= tower.getAttackSpeed()) {
                tower.setElapsedTime(0);
                attackCreep(tower);
            }
            else tower.setElapsedTime(elTime);
        }
    }
    private void attackCreep(Tower tower) {
        int radius = tower.getRadius();
        for(int i=-radius;i<=radius;i++) {
            for(int j=-radius;j<=radius;j++) {
                GridPoint2 position = tower.getPosition();
                if(cellManager.getCell(position.x + i, position.y + j).isCreep()) {
                    Creep creep = cellManager.getCell(position.x + i, position.y + j).getCreeps().first();
                    int hp = creep.getHp() - tower.getDamage();
                    if (hp <= 0) {
                        cellManager.getCell(position.x+i, position.y+j).removeCreep(creep);
                        creepsManager.removeCreep(creep);
                        layerForeGround.getCell(position.x+i, position.y+j).setTile(null);
                    }
                    else {
                        creep.setHp(hp);
                    }
                    Gdx.app.log("Creep hp", creep.getHp()+"");
                }
            }
        }
    }
}
