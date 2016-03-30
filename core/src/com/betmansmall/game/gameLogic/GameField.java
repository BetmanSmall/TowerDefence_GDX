package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.WhichCell;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.GridNav.GridNav;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.GridNav.Vertex;
import com.betmansmall.game.gameLogic.playerTemplates.Direction;
import com.betmansmall.game.gameLogic.playerTemplates.FactionsManager;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;

import java.util.ArrayDeque;

/**
 * Created by betmansmall on 08.02.2016.
 */
public class GameField {
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private SpriteBatch spriteBatch = new SpriteBatch();
    private BitmapFont bitmapFont = new BitmapFont();

    private TiledMap map;
    private IsometricTiledMapRenderer renderer;
    private int sizeFieldX, sizeFieldY;
    private int sizeCellX, sizeCellY;
    public int getSizeFieldX() {
        return sizeFieldX;
    } public int getSizeFieldY() {
        return sizeFieldY;
    }
    public int getSizeCellX() {
        return sizeCellX;
    } public int getSizeCellY() {
        return sizeCellY;
    }
//    private TiledMapTileLayer foreground, backbround;

    public boolean isDrawableGrid = true;
    public boolean isDrawableCreeps = true;
    public boolean isDrawableTowers = true;
    public boolean isDrawableRoutes = true;
    public boolean isDrawableGridNav = false;

    private Cell[][] field;
    private GridNav gridNav;
//    private GridPoint2 spawnPoint, exitPoint;

    private WaveManager waveManager;
//    private int defaultNumCreateCreeps = 100;
    private CreepsManager creepsManager;
    private TowersManager towersManager;
    private FactionsManager factionsManager;

//    private float intervalForSpawnCreeps = 1f;
//    private float elapsedTimeForSpawn = 0f;
    // GAME INTERFACE ZONE1
    private WhichCell whichCell;
    private boolean gamePaused;
    private int maxOfMissedCreeps;
    private int missedCreeps;
    private int gamerGold;
    // GAME INTERFACE ZONE2

    //TEST ZONE1
    private Animation animation;
    private float stateTime;
    //TEST ZONE2

    public GameField(String mapName) {
        map = new TmxMapLoader().load(mapName);
        renderer = new IsometricTiledMapRenderer(map, spriteBatch);

        sizeFieldX = map.getProperties().get("width", Integer.class);
        sizeFieldY = map.getProperties().get("height", Integer.class);
        sizeCellX = map.getProperties().get("tilewidth", Integer.class);
        sizeCellY = map.getProperties().get("tileheight", Integer.class);

        waveManager = new WaveManager();
        creepsManager = new CreepsManager();
        towersManager = new TowersManager();
        factionsManager = new FactionsManager();

        createField(sizeFieldX, sizeFieldY, map.getLayers());

		TiledMapTileSets tileSets = map.getTileSets();
		for(TiledMapTileSet tileSet:tileSets) {
            String tileSetName = tileSet.getName();
            Gdx.app.log("GameField::GameField()", "-- TileSet:" + tileSetName);
            if(tileSetName.contains("unit")) {
                TemplateForUnit unit = new TemplateForUnit(tileSet);
                factionsManager.addUnitToFaction(unit);
                if(animation == null) {
                    AnimatedTiledMapTile animatedTiledMapTile = unit.animations.get("idle_" + Direction.UP);
                    StaticTiledMapTile[] staticTiledMapTiles = animatedTiledMapTile.getFrameTiles();
                    Array<TextureRegion> textureRegions = new Array<TextureRegion>(staticTiledMapTiles.length);
//                    Gdx.app.log("GameField::GameField()", " -- textureRegion.size:" + staticTiledMapTiles.length);
                    for(int k = 0; k < staticTiledMapTiles.length; k++) {
                        TextureRegion textureRegion = staticTiledMapTiles[k].getTextureRegion();
                        textureRegions.add(textureRegion);
                    }
                    stateTime = 0f;
                    animation = new Animation(0.25f, textureRegions);
                }
            } else if(tileSetName.contains("tower")) {
                TemplateForTower tower = new TemplateForTower(tileSet);
                factionsManager.addTowerToFaction(tower);
            }
		}

        // GAME INTERFACE ZONE1
        whichCell = new WhichCell(sizeFieldX, sizeFieldY, sizeCellX, sizeCellY);
        gamePaused = true;
        maxOfMissedCreeps = 7;
        missedCreeps = 0;
        gamerGold = 50;
        // GAME INTERFACE ZONE2
    }

    private void createField(int sizeFieldX, int sizeFieldY, MapLayers mapLayers) {
        if(field == null) {
            field = new Cell[sizeFieldX][sizeFieldY];
            for(int y = 0; y < sizeFieldY; y++) {
                for(int x = 0; x < sizeFieldX; x++) {
                    field[x][y] = new Cell();
                    for(MapLayer mapLayer: mapLayers) {
                        if(mapLayer instanceof TiledMapTileLayer) {
                            TiledMapTileLayer layer = (TiledMapTileLayer) mapLayer;
                            TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                            if(cell != null) {
                                if(cell.getTile().getProperties().get("busy") != null) {
                                    field[x][y].setTerrain();
                                } else if(cell.getTile().getProperties().get("spawnPoint") != null && cell.getTile().getProperties().get("spawnPoint").equals("1")) {
//                                    spawnPoint = new GridPoint2(x, y);
                                    waveManager.spawnPoints.add(new GridPoint2(x, y));
//                                    field[x][y].setTerrain();
                                    Gdx.app.log("GameField::GameField()", "-- Set spawnPoint: (" + x + ", " + y + ")");
                                } else if (cell.getTile().getProperties().get("exitPoint") != null && cell.getTile().getProperties().get("exitPoint").equals("1")) {
//                                    exitPoint = new GridPoint2(x, y);
                                    waveManager.exitPoints.add(new GridPoint2(x, y));
//                                    field[x][y].setTerrain();
                                    Gdx.app.log("GameField::GameField()", "-- Set exitPoint: (" + x + ", " + y + ")");
                                }
                            }
                        } else {
                            Gdx.app.log("GameField::createField()", " -- Не смог преобразовать MapLayer в TiledMapTileLayer");
                        }
                    }
                }
            }
            gridNav = new GridNav();
            gridNav.loadCharMatrix(getCharMatrix());
        }
    }

    public char[][] getCharMatrix() {
        if(field != null) {
            char[][] charMatrix = new char[sizeFieldY][sizeFieldX];
            for(int y = 0; y < sizeFieldY; y++) {
                for(int x = 0; x < sizeFieldX; x++) {
                    if(field[x][y].isTerrain() || field[x][y].getTower() != null) {
                        charMatrix[y][x] = 'T';
                    } else {
                        charMatrix[y][x] = '.';
                    }
//                    System.out.print(charMatrix[y][x]);
                }
//                System.out.print("\n");
            }
            return charMatrix;
        }
        return null;
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

        if(!gamePaused) {
            spawnCreep(delta);
            stepAllCreep(delta);
            attackCreeps(delta);
        }

        if(isDrawableGrid)
            drawGrid(camera);
        if(isDrawableCreeps)
            drawCreeps(camera);
        if(isDrawableTowers)
            drawTowers(camera);
        if(isDrawableRoutes)
            drawRoutes(camera);
        if(isDrawableGridNav)
            drawGridNav(camera);

        if(animation != null) {
            stateTime += delta;
            TextureRegion currentFrame = animation.getKeyFrame(stateTime, true); // #16
            spriteBatch.begin();
            spriteBatch.draw(currentFrame, 50, 300, 300, 300); // #17
            spriteBatch.end();
        }
    }

	private void drawGrid(OrthographicCamera camera) {
        int widthForTop = sizeFieldY * (sizeCellX/2);
        int heightForTop = sizeFieldY * (sizeCellY/2);
        int widthForBottom = sizeFieldX * (sizeCellX/2);
        int heightForBottom = sizeFieldX * (sizeCellY/2);

		int halfSizeCellX = sizeCellX/2;
		int halfSizeCellY = sizeCellY/2;

		shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(Color.BROWN); // (100, 60, 21, 1f);

		for(int x = 0; x <= sizeFieldX; x++)
			shapeRenderer.line(x*halfSizeCellX, halfSizeCellY - x*halfSizeCellY, widthForTop + x*halfSizeCellX, halfSizeCellY + heightForTop - x*halfSizeCellY);
		for(int y = 0; y <= sizeFieldY; y++)
			shapeRenderer.line(y*halfSizeCellX, halfSizeCellY + y*halfSizeCellY, widthForBottom + y*halfSizeCellX, halfSizeCellY - heightForBottom + y*halfSizeCellY);

		shapeRenderer.end();
	}

    private void drawCreeps(OrthographicCamera camera) {
        int halfSizeCellX = sizeCellX / 2;
        int halfSizeCellY = sizeCellY / 2;

//        spriteBatch.setProjectionMatrix(camera.combined);
//        spriteBatch.begin();

        for(Creep creep: creepsManager.getAllCreeps()) {
            int oldX = creep.getOldPosition().getX(), oldY = creep.getOldPosition().getY();
            int newX = creep.getNewPosition().getX(), newY = creep.getNewPosition().getY();
            float fVx = halfSizeCellX*newY + newX*halfSizeCellX;
            float fVy = halfSizeCellY*newY - newX*halfSizeCellY;

            float elapsedTime = creep.getElapsedTime(), speed = creep.getSpeed();
            if(newX < oldX && newY > oldY) {
                fVy -= (sizeCellY/speed)*(speed-elapsedTime);
            } else if(newX == oldX && newY > oldY) {
                fVx -= (sizeCellX/2/speed)*(speed-elapsedTime);
                fVy -= (sizeCellY/2/speed)*(speed-elapsedTime);
            } else if(newX > oldX && newY > oldY) {
                fVx -= (sizeCellX/speed)*(speed-elapsedTime);
            } else if(newX > oldX && newY == oldY) {
                fVx -= (sizeCellX/2/speed)*(speed-elapsedTime);
                fVy += (sizeCellY/2/speed)*(speed-elapsedTime);
            } else if(newX > oldX && newY < oldY) {
                fVy += (sizeCellY/speed)*(speed-elapsedTime);
            } else if(newX == oldX && newY < oldY) {
                fVx += (sizeCellX/2/speed)*(speed-elapsedTime);
                fVy += (sizeCellY/2/speed)*(speed-elapsedTime);
            } else if(newX < oldX && newY < oldY) {
                fVx += (sizeCellX/speed)*(speed-elapsedTime);
            } else if(newX < oldX && newY == oldY) {
                fVx += (sizeCellX/2/speed)*(speed-elapsedTime);
                fVy -= (sizeCellY/2/speed)*(speed-elapsedTime);
            }


            TextureRegion curentFrame = creep.getCurentFrame();
            int deltaX = (curentFrame.getRegionWidth()-sizeCellX)/2;
            int deltaY = (curentFrame.getRegionHeight()-sizeCellY)/2;
            fVx -= deltaX;
            fVy -= deltaY;

            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.BLACK);
            float spaceInHpBar = 1;
            float hpBarWidth = 30;
            float hpBarHeight = 7;
            float hpBarWidthSpace = (curentFrame.getRegionWidth()-hpBarWidth)/2;
            float hpBarTopSpace = hpBarHeight;
            shapeRenderer.rect(fVx + hpBarWidthSpace, fVy + curentFrame.getRegionHeight() - hpBarTopSpace, hpBarWidth, hpBarHeight);
            shapeRenderer.setColor(Color.GREEN);
            int maxHP = creep.getTemplateForUnit().healthPoints;
            int hp = creep.getHp();
            hpBarWidth = hpBarWidth/maxHP * hp;
            shapeRenderer.rect(fVx + hpBarWidthSpace+spaceInHpBar, fVy + curentFrame.getRegionHeight() - hpBarTopSpace + spaceInHpBar, hpBarWidth - (spaceInHpBar*2), hpBarHeight - (spaceInHpBar*2));
//            shapeRenderer.setColor(Color.BLUE); // (100, 60, 21, 1f);
//            shapeRenderer.circle(fVx, fVy, 1);
            shapeRenderer.end();

            spriteBatch.setProjectionMatrix(camera.combined);
            spriteBatch.begin();
            spriteBatch.draw(curentFrame, fVx, fVy);
            spriteBatch.end();

//            Gdx.app.log("GameField::drawCreeps()", " -- x:" + x + " y:" + y + " x1:" + x1 + " y1:" + y1);
//            Gdx.app.log("GameField::drawCreeps()", " -- sizeTexReg:" + creep.getCurentFrame().getRegionWidth() + "x" + creep.getCurentFrame().getRegionHeight());
        }
//        spriteBatch.end();
    }

    private void drawRoutes(OrthographicCamera camera) {
        int halfSizeCellX = sizeCellX / 2;
        int halfSizeCellY = sizeCellY / 2;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BROWN); // (100, 60, 21, 1f);

        for(Creep creep: creepsManager.getAllCreeps()) {
            ArrayDeque<Vertex> route = creep.getRoute();

            if(route != null) {
                for(Vertex coor : route) {
                    int vX = coor.getX();
                    int vY = coor.getY()+1; // LibGDX some problems. Have offset (0,0) coor.
                    float fVx = halfSizeCellX * vY + vX * halfSizeCellX;
                    float fVy = halfSizeCellY * vY - vX * halfSizeCellY;

                    shapeRenderer.circle(fVx, fVy, 5);
                }
            }
        }
        shapeRenderer.end();
    }

    private void drawTowers(OrthographicCamera camera) {
        int halfSizeCellX = sizeCellX / 2;
        int halfSizeCellY = sizeCellY / 2;

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        float fix = 1f;

        for(Tower tower: towersManager.getAllTowers()) {
            int x = tower.getPosition().x;
            int y = tower.getPosition().y+1;
            float x1 = halfSizeCellX*y + x*halfSizeCellX;
            float y1 = halfSizeCellY*y - x*halfSizeCellY;

            int towerSize = tower.getTemplateForTower().size;
            TextureRegion curentFrame = tower.getCurentFrame();
            float deltaX = (sizeCellX/2)*fix;//(sizeCellX*towerSize)/sizeCellX;
            float deltaY = ((sizeCellY/2)*towerSize)*fix;
            spriteBatch.draw(curentFrame, x1 - deltaX, y1 - deltaY, (sizeCellX * towerSize) * fix, ((sizeCellY*2)*towerSize)*fix);

//            Gdx.app.log("GameField::drawCreeps()", " -- x:" + x + " y:" + y + " x1:" + x1 + " y1:" + y1);
//            Gdx.app.log("GameField::drawCreeps()", " -- deltaX:" + deltaX + " deltaY:" + deltaY + " towerSize:" + towerSize);
//            Gdx.app.log("GameField::drawCreeps()", " -- sizeTexReg:" + tower.getCurentFrame().getRegionWidth() + "x" + tower.getCurentFrame().getRegionHeight());
        }
        spriteBatch.end();
    }

    private void drawGridNav(OrthographicCamera camera) {
        int halfSizeCellX = sizeCellX / 2;
        int halfSizeCellY = sizeCellY / 2;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(Color.RED); // (100, 60, 21, 1f);

        for(int y = 0; y < sizeFieldY; y++) {
            for(int x = 0; x < sizeFieldX; x++) {
                float fVx = halfSizeCellX * (y+1) + x * halfSizeCellX;
                float fVy = halfSizeCellY * (y+1) - x * halfSizeCellY;
                if(!field[x][y].isEmpty()) {
                    if(field[x][y].isTerrain()) {
                        shapeRenderer.setColor(Color.RED);
                    } else if(field[x][y].getCreep() != null) {
                        shapeRenderer.setColor(Color.GREEN);
                    } else if(field[x][y].getTower() != null) {
                        shapeRenderer.setColor(Color.BLACK);
                    }
                    shapeRenderer.circle(fVx, fVy, 3);
                }
            }
        }
        shapeRenderer.end();
    }

    public void setSpawnPoint(int x, int y) {
//        spawnPoint = new GridPoint2(x, y);
        waveManager.spawnPoints.set(0, new GridPoint2(x, y));
    }
    public void setExitPoint(int x, int y) {
//        exitPoint = new GridPoint2(x, y);
        waveManager.exitPoints.set(0, new GridPoint2(x, y));
        rerouteForAllCreeps();
    }

    private void spawnCreep(float delta) {
        Integer templateIndex = waveManager.getNextCreepTemplateIndexForSpawn(delta);
        if(templateIndex != null) {
            if(templateIndex >= 0) {
                GridPoint2 spawnPoint = waveManager.spawnPoints.first();
                if(spawnPoint != null) {
                    createCreep(spawnPoint.x, spawnPoint.y, factionsManager.getTemplateForUnitFromFirstFaction(templateIndex));
                }
            }
        }
//        elapsedTimeForSpawn += delta;
//        if(elapsedTimeForSpawn > intervalForSpawnCreeps) {
//            elapsedTimeForSpawn = 0f;
//            if(creepsManager.amountCreeps() < defaultNumCreateCreeps) {
//                if(spawnPoint != null) {
//                    createCreep(spawnPoint.x, spawnPoint.y);
//                }
//            }
//        }
    }

    public void createCreep(int x, int y) {
        createCreep(x, y, factionsManager.getRandomTemplateForUnitFromFirstFaction());
    }

    private void createCreep(int x, int y, TemplateForUnit templateForUnit) {
        gridNav.loadCharMatrix(getCharMatrix());
        ArrayDeque<Vertex> route = gridNav.route(x, y, waveManager.exitPoints.first().x, waveManager.exitPoints.first().y);

        if(route != null) {
            Creep creep = creepsManager.createCreep(route, templateForUnit);
            field[x][y].setCreep(creep);
//            Gdx.app.log("GameField::createCreep()", " -- x:" + x + " y:" + y + " eX:" + waveManager.exitPoints.first().x + " eY:" + waveManager.exitPoints.first().y);
//            Gdx.app.log("GameField::createCreep()", " -- route:" + route);
        }
    }

    public void towerActions(int x, int y) {
        if(field[x][y].isEmpty()) {
            createTower(x, y);
        } else if(field[x][y].getTower() != null) {
            removeTower(x, y);
        }
    }

    public boolean createTower(int x, int y) {
        TemplateForTower templateForTower = factionsManager.getCurrentTemplateTower();
        if(gamerGold >= templateForTower.cost) {
            int towerSize = templateForTower.size;
            for (int tmpX = 0; tmpX < towerSize; tmpX++)
                for (int tmpY = 0; tmpY < towerSize; tmpY++)
                    if (!field[tmpX + x][tmpY + y].isEmpty()) // HAVE BUGS! Can out of array!
                        return false;

            GridPoint2 position = new GridPoint2(x, y);
            Tower tower = towersManager.createTower(position, templateForTower);
            for (int tmpX = 0; tmpX < towerSize; tmpX++)
                for (int tmpY = 0; tmpY < towerSize; tmpY++)
                    field[tmpX + x][tmpY + y].setTower(tower);

            rerouteForAllCreeps();
            gamerGold -= templateForTower.cost;
            Gdx.app.log("GameField::createTower()", " -- GamerGold:" + gamerGold);
            return true;
        } else {
            return false;
        }
    }

    public void removeTower(int touchX, int touchY) {
        Tower tower = field[touchX][touchY].getTower();
        if(tower != null) {
            int x = tower.getPosition().x;
            int y = tower.getPosition().y;
            int towerSize = tower.getTemplateForTower().size;

            for(int tmpX = 0; tmpX < towerSize; tmpX++) {
                for(int tmpY = 0; tmpY < towerSize; tmpY++) {
                    field[x+tmpX][y+tmpY].removeTower();
                }
            }
            towersManager.removeTower(tower);
            rerouteForAllCreeps();
//            gamerGold += (int) tower.getTemplateForTower().cost*0.5;
        }
    }

    private void rerouteForAllCreeps() {
        gridNav.loadCharMatrix(getCharMatrix());
        for(Creep creep: creepsManager.getAllCreeps()) {
            ArrayDeque<Vertex> route = gridNav.route(creep.getNewPosition().getX(), creep.getNewPosition().getY(), waveManager.exitPoints.first().x, waveManager.exitPoints.first().y);
            if(route != null) {
                route.removeFirst();
                creep.setRoute(route);
            }
        }
    }

    private void stepAllCreep(float delta) {
        for(int i=0; i<creepsManager.amountCreeps(); i++) {
            Creep creep = creepsManager.getCreep(i);
            Vertex oldPosition = creep.getNewPosition();
//            field[oldPosition.getX()][oldPosition.getY()].removeCreep(creep);
//            float elTime = creep.getElapsedReloadTime()+delta;
//            if(elTime >= creep.getSpeed()) {
//                creep.setElapsedReloadTime(0);
//                stepOneCreep(creep);
//            }
//            else creep.setElapsedReloadTime(elTime);
            Vertex newPosition = creep.move(delta);
            if(newPosition != null) {
                if(!newPosition.equals(oldPosition)) {
                    field[oldPosition.getX()][oldPosition.getY()].removeCreep(creep);
                    field[newPosition.getX()][newPosition.getY()].setCreep(creep);
//                    Gdx.app.log("GameField::stepAllCreep()", "-- Creep move to X:" + newPosition.getX() + " Y:" + newPosition.getY());
                }
            } else {
                field[oldPosition.getX()][oldPosition.getY()].removeCreep(creep);
                creepsManager.removeCreep(creep);
//                Gdx.app.log("GameField::stepAllCreep()", "-- Creep finished!");
            }
        }
    }

    private void attackCreeps(float delta) {
        for(int i=0;i<towersManager.amountTowers();i++) {
            Tower tower = towersManager.getTower(i);
            float elTime = tower.getElapsedReloadTime()+delta;
            if(elTime >= tower.getReloadTime()) {
                tower.setElapsedReloadTime(0);
                attackCreep(tower);
            }
            else tower.setElapsedReloadTime(elTime);
        }
    }

    private void attackCreep(Tower tower) {
        int radius = tower.getRadius();
        for(int i=-radius;i<=radius;i++) {
            for(int j=-radius;j<=radius;j++) {
                GridPoint2 position = tower.getPosition();
                if(field[position.x + i][position.y + j].getCreep() != null) {
                    Creep creep = field[position.x + i][position.y + j].getCreeps().first();
                    int hp = creep.getHp() - tower.getDamage();
                    if(hp <= 0) {
                        field[position.x+i][position.y+j].removeCreep(creep);
                        creepsManager.removeCreep(creep);
                        gamerGold += creep.getTemplateForUnit().bounty;
                        Gdx.app.log("Creep::attackCreep()", " -- ");
                    }
                    else {
                        creep.setHp(hp);
                    }
//                    Gdx.app.log("Creep healthPoints", creep.getHp()+"");
                }
            }
        }
    }

//    public Array<TemplateForTower> getFirstTemplateForTowers() {
//        factionsManager.ge
//    }

    // GAME INTERFACE ZONE1
    public GridPoint2 whichCell(GridPoint2 gameCoordinate) {
        return whichCell.whichCell(gameCoordinate);
    }

    public void setGamePause(boolean gamePaused) {
        this.gamePaused = gamePaused;
    }
    public boolean getGamePaused() {
        return gamePaused;
    }

    public int getNumberOfCreeps() {
        return waveManager.getNumberOfCreeps() + creepsManager.amountCreeps();
    }

    public String getGameState() {
        if(missedCreeps > maxOfMissedCreeps) {
            return "Lose";
        } else {
            if(waveManager.getNumberOfCreeps() == 0 && creepsManager.amountCreeps() == 0) {
                return "Win";
            }
        }
        return "In progress";
    }

    public int getGamerGold() {
        return gamerGold;
    }
    // GAME INTERFACE ZONE2
}
