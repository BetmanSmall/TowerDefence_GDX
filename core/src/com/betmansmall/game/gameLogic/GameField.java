package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.GameSettings;
import com.betmansmall.game.GameType;
import com.betmansmall.game.gameLogic.mapLoader.Map;
import com.betmansmall.game.gameLogic.mapLoader.MapLoader;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.PathFinder.Node;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.PathFinder.PathFinder;
import com.betmansmall.game.gameLogic.playerTemplates.FactionsManager;
import com.betmansmall.game.gameLogic.playerTemplates.ShellAttackType;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;
import com.betmansmall.game.gameLogic.playerTemplates.TowerAttackType;
import com.betmansmall.game.gameLogic.playerTemplates.ShellEffectType;

import java.util.ArrayDeque;
import java.util.Iterator;

/**
 * Created by betmansmall on 08.02.2016.
 */
public class GameField {
    public FactionsManager factionsManager;
    public WaveManager waveManager; // ALL public for all || we are friendly :)
    public TowersManager towersManager;
    public static UnitsManager unitsManager; // For Bullet
    public GameSettings gameSettings;
    public Map map;
    private Cell[][] field;
    private PathFinder pathFinder;

    // CameraController1 && map
//    public int map.width, map.height;
//    public static int sizeCellX, sizeCellY;
//    private int halfSizeCellX, halfSizeCellY;
    // CameraController2
//    public int isDrawableGrid = 1;
//    public static int isDrawableUnits = 1; // Bad! need make not static!
//    public static int isDrawableTowers = 1; // Bad! need make not static!
//    public int isDrawableBackground = 1;
//    public int isDrawableGround = 1;
//    public int isDrawableForeground = 1;
//    public int isDrawableGridNav = 1;
//    public int isDrawableRoutes = 1;
//    public int drawOrder = 8;
//    private WhichCell whichCell;
//    // CameraController3

    private UnderConstruction underConstruction;
    private Texture greenCheckmark;
    private Texture redCross;

    // GAME INTERFACE ZONE1
//    float timeOfGame;
    public float gameSpeed;
    public boolean gamePaused;
    public static int gamerGold; // For Bullet
    // GAME INTERFACE ZONE2
//    Cell cellSpawnHero;
//    Cell cellExitHero;
////    boolean isometric;
//    public int maxOfMissedUnitsForComputer0;
//    public int missedUnitsForComputer0;
//    public int maxOfMissedUnitsForPlayer1;
//    public int missedUnitsForPlayer1;
    // GAME INTERFACE ZONE3

    public GameField(String mapName, FactionsManager factionsManager, GameSettings gameSettings) {
        Gdx.app.log("GameField::GameField()", "-- mapName:" + mapName);
        Gdx.app.log("GameField::GameField()", "-- factionsManager:" + factionsManager);
        Gdx.app.log("GameField::GameField()", "-- gameSettings:" + gameSettings);
        this.factionsManager = factionsManager;
        waveManager = new WaveManager();
        towersManager = new TowersManager();
        unitsManager = new UnitsManager();
        this.gameSettings = gameSettings;

        map = new MapLoader(waveManager).load(mapName);

//        map.width = map.getProperties().get("width", Integer.class);
//        map.height = map.getProperties().get("height", Integer.class);
//        sizeCellX = map.getProperties().get("tilewidth", Integer.class);
//        sizeCellY = map.getProperties().get("tileheight", Integer.class);
//        halfSizeCellX = sizeCellX / 2;
//        halfSizeCellY = sizeCellY / 2;

        underConstruction = null;
        greenCheckmark = new Texture(Gdx.files.internal("maps/textures/green_checkmark.png"));
        redCross = new Texture(Gdx.files.internal("maps/textures/red_cross.png"));
        if (greenCheckmark == null || redCross == null) {
            Gdx.app.error("GameField::GameField()", "-- Achtung fuck. NOT FOUND 'maps/textures/green_checkmark.png' & 'maps/textures/red_cross.png' YEBAK");
        }

        createField();
        flipY();
        pathFinder = new PathFinder();
        pathFinder.loadCharMatrix(getCharMatrix());
        Gdx.app.log("GameField::GameField()", "-- pathFinder:" + pathFinder);

        MapProperties mapProperties = map.getProperties();
        Gdx.app.log("GameField::GameField()", "-- mapProperties:" + mapProperties);
        Gdx.app.log("GameField::GameField()", "-- gameSettings.gameType:" + gameSettings.gameType);
        if (gameSettings.gameType == GameType.LittleGame) {
            gameSettings.maxOfMissedUnitsForPlayer1 = 1;
            int randomEnemyCount = gameSettings.enemyCount;
            for (int k = 0; k < randomEnemyCount; k++) {
                int randomX = (int)(Math.random()*map.width);
                int randomY = (int)(Math.random()*map.height);
                Gdx.app.log("GameField::GameField()", "-- randomX:" + randomX);
                Gdx.app.log("GameField::GameField()", "-- randomY:" + randomY);
                if (getCell(randomX, randomY).isEmpty()) {
                    spawnCompUnitToRandomExit(randomX, randomY);
                } else {
                    k--;
                }
            }
//            spawnHeroInSpawnPoint();
        } else if (gameSettings.gameType == GameType.TowerDefence) {
            waveManager.validationPoints(field);
            if (waveManager.waves.size == 0) {
                for (int w = 0; w < 10; w++) {
                    GridPoint2 spawnPoint = new GridPoint2((int) (Math.random() * map.width), (int) (Math.random() * map.height));
                    GridPoint2 exitPoint = new GridPoint2((int) (Math.random() * map.width), (int) (Math.random() * map.height));
                    Cell spawnCell = getCell(spawnPoint.x, spawnPoint.y);
                    Cell exitCell = getCell(exitPoint.x, exitPoint.y);
                    if (spawnCell != null && spawnCell.isEmpty()) {
                        if (exitCell != null && exitCell.isEmpty()) {
                            Wave wave = new Wave(spawnPoint, exitPoint, 0f);
                            for (int k = 0; k < 10; k++) {
                                wave.addAction("interval=" + 1);
                                wave.addAction(factionsManager.getRandomTemplateForUnitFromFirstFaction().templateName);
                            }
                            waveManager.addWave(wave);
                        }
                    }
                }

            }
            waveManager.checkRoutes(pathFinder);
//            gamerGold = Integer.valueOf(mapProperties.get("gamerGold", "10000", String.class)); // HARD GAME | one gold = one unit for computer!!!
            gamerGold = 100000;
            gameSettings.maxOfMissedUnitsForComputer0 = mapProperties.get("maxOfMissedUnitsForComputer0", gamerGold, Integer.class); // Игрок может сразу выиграть если у него не будет голды. так как @ref2
//            gameSettings.maxOfMissedUnitsForComputer0 = Integer.valueOf(mapProperties.get("maxOfMissedUnitsForComputer0", String.valueOf(gamerGold), String.class));
            gameSettings.missedUnitsForComputer0 = 0;
            if (gameSettings.maxOfMissedUnitsForPlayer1 == 0) {
                gameSettings.maxOfMissedUnitsForPlayer1 = mapProperties.get("maxOfMissedUnitsForPlayer1", waveManager.getNumberOfActions() / 8, Integer.class); // it is not true | need implement getNumberOfUnits()
//                gameSettings.maxOfMissedUnitsForPlayer1 = Integer.valueOf(mapProperties.get("maxOfMissedUnitsForPlayer1", String.valueOf(waveManager.getNumberOfActions()/4), String.class)); // it is not true | need implement getNumberOfUnits()
            }
            gameSettings.missedUnitsForPlayer1 = 0;
            Gdx.app.log("GameField::GameField()", "-- gamerGold:" + gamerGold);
            Gdx.app.log("GameField::GameField()", "-- gameSettings.maxOfMissedUnitsForComputer0:" + gameSettings.maxOfMissedUnitsForComputer0);
            Gdx.app.log("GameField::GameField()", "-- gameSettings.maxOfMissedUnitsForPlayer1:" + gameSettings.maxOfMissedUnitsForPlayer1);
//        } else {
//            Gdx.app.log("GameField::GameField()", "-- gameSettings.gameType:" + gameSettings.gameType);
        }

        gameSpeed = 1.0f;
        gamePaused = false;
    }

    private void createField() {
        if (map.getProperties().containsKey("orientation")) {
            if (map.getProperties().get("orientation").equals("isometric")) {
                gameSettings.isometric = true;
            }
        }
//        Gdx.app.log("GameField::createField()", "-1- field:" + field);
        if (field == null) {
            field = new Cell[map.width][map.height];
            for (int y = 0; y < map.width; y++) {
                for (int x = 0; x < map.height; x++) {
                    Cell cell = field[x][y] = new Cell();
                    cell.setGraphicCoordinates(x, y, map.tileWidth/2, map.tileHeight/2);
                    for (MapLayer mapLayer : map.getLayers()) {
                        if (mapLayer instanceof TiledMapTileLayer) {
                            TiledMapTileLayer layer = (TiledMapTileLayer) mapLayer;
                            TiledMapTileLayer.Cell tileLayerCell = layer.getCell(x, y);
                            if (tileLayerCell != null) {
                                TiledMapTile tiledMapTile = tileLayerCell.getTile();
                                if (tiledMapTile != null) {
                                    String layerName = layer.getName();
                                    if (layerName != null) {
                                        if ( layerName.equals("background") ) {
                                            cell.backgroundTiles.add(tiledMapTile);
                                        } else if ( layerName.equals("ground") || layerName.equals("entity") ) {
                                            cell.setTerrain(tiledMapTile);
                                        } else if ( layerName.equals("towers") ) {
                                            cell.removeTerrain(true);
                                            this.createTower(x, y, factionsManager.getRandomTemplateForTowerFromAllFaction(), 0);
                                        } else {
                                            cell.foregroundTiles.add(tiledMapTile);
                                        }
                                    }
//                                if (!tiledMapTile.getProperties().) {
//                                    qDebug() << "GameField::createField(); -- layerName:" << layerName;
//                                    qDebug() << "GameField::createField(); -- tile->getId():" << tile->getId();
//                                    qDebug() << "GameField::createField(); -- tile->getProperties()->size():" << tile->getProperties()->size();
//                                    qDebug() << "GameField::createField(); -- keys:" << tile->getProperties()->keys();
//                                    qDebug() << "GameField::createField(); -- values:" << tile->getProperties()->values();
//                                }
//                                    if(!layer.getProperties().containsKey("background")) {
//                                        field[x][y].foregroundTiles.add(tiledMapTile);
//                                    } else {
//                                        field[x][y].backgroundTiles.add(tiledMapTile);
//                                    }
//                                    if (tiledMapTile.getProperties().get("terrain") != null) {
//                                        field[x][y].setTerrain();
//                                    } else
                                    if (tiledMapTile.getProperties().get("spawnCell") != null) {
                                        gameSettings.cellSpawnHero = cell;
                                        gameSettings.cellSpawnHero.spawn = true;
//                                        waveManager.spawnPoints.add(new GridPoint2(x, y));
                                        Gdx.app.log("GameField::createField()", "-- Set gameSettings.cellSpawnHero:" + gameSettings.cellSpawnHero);
                                    } else if (tiledMapTile.getProperties().get("exitCell") != null) {
                                        gameSettings.cellExitHero = cell;
                                        gameSettings.cellExitHero.exit = true;
//                                        waveManager.exitPoints.add(new GridPoint2(x, y));
                                        Gdx.app.log("GameField::createField()", "-- Set gameSettings.cellExitHero:" + gameSettings.cellExitHero);
                                    }
//                                    // task 6. отрисовка деревьев полностью
//                                    if(tiledMapTile.getProperties().get("treeName") != null) {
//                                        String treeName = tiledMapTile.getProperties().get("treeName", String.class);
//                                        int treeWidth = Integer.parseInt(tiledMapTile.getProperties().get("treeWidth", "1", String.class));
//                                        int treeHeight = Integer.parseInt(tiledMapTile.getProperties().get("treeHeight", "1", String.class));
//                                        Gdx.app.log("GameField::createField()", "-- New Tree:" + treeName + "[" + treeWidth + "," + treeHeight + "]:{" + x + "," + y + "}");
//                                        float regionX = tiledMapTile.getTextureRegion().getRegionX();
//                                        float regionY = tiledMapTile.getTextureRegion().getRegionY();
//                                        float regionWidth = tiledMapTile.getTextureRegion().getRegionWidth();
//                                        float regionHeight = tiledMapTile.getTextureRegion().getRegionWidth();
//                                        Gdx.app.log("GameField::createField()", "-- regionX:" + regionX + " regionY:" + regionY + " regionWidth:" + regionWidth + " regionHeight:" + regionHeight);
//                                        TextureRegion textureRegion = new TextureRegion(tiledMapTile.getTextureRegion());
//                                        textureRegion.setRegion(regionX - ((treeWidth>2) ? (treeWidth-2)*regionWidth : 0), regionY - ((treeHeight>1) ? (treeHeight-1)*regionHeight : 0), treeWidth*regionWidth, treeHeight*regionHeight);
////                                        Cell.Tree tree = new Cell.Tree(textureRegion, treeWidth, treeHeight);
//                                    }
                                }
                            }
                        } else {
                            Gdx.app.log("GameField::createField()", "-- Не смог преобразовать MapLayer в TiledMapTileLayer");
                        }
                    }
                }
            }
        }
        Gdx.app.log("GameField::createField()", "-2- field:" + field);
    }

    public void turnRight() {
        if(map.width == map.height) {
            Cell[][] newCells = new Cell[map.width][map.height];
            for(int y = 0; y < map.height; y++) {
                for(int x = 0; x < map.width; x++) {
                    newCells[map.width-y-1][x] = field[x][y];
                    newCells[map.width-y-1][x].setGraphicCoordinates(map.width-y-1, x, map.tileWidth/2, map.tileHeight/2);
                }
            }
            field = newCells;
        } else {
            Gdx.app.log("GameField::turnRight()", "-- Not work || Work, but mb not Good!");
            int oldWidth = map.width;
            int oldHeight = map.height;
            map.width = map.height;
            map.height = oldWidth;
            Cell[][] newCells = new Cell[map.width][map.height];
            for(int y = 0; y < oldHeight; y++) {
                for(int x = 0; x < oldWidth; x++) {
                    newCells[map.width-y-1][x] = field[x][y];
                    newCells[map.width-y-1][x].setGraphicCoordinates(map.width-y-1, x, map.tileWidth/2, map.tileHeight/2);
                }
            }
            field = newCells;
        }
    }

    public void turnLeft() {
        if(map.width == map.height) {
            Cell[][] newCells = new Cell[map.width][map.height];
            for(int y = 0; y < map.height; y++) {
                for(int x = 0; x < map.width; x++) {
                    newCells[y][map.height-x-1] = field[x][y];
                    newCells[y][map.height-x-1].setGraphicCoordinates(y, map.height-x-1, map.tileWidth/2, map.tileHeight/2);
                }
            }
            field = newCells;
        } else {
            Gdx.app.log("GameField::turnLeft()", "-- Not work || Work, but mb not Good!");
            int oldWidth = map.width;
            int oldHeight = map.height;
            map.width = map.height;
            map.height = oldWidth;
            Cell[][] newCells = new Cell[map.width][map.height];
            for(int y = 0; y < oldHeight; y++) {
                for(int x = 0; x < oldWidth; x++) {
                    newCells[y][map.height-x-1] = field[x][y];
                    newCells[y][map.height-x-1].setGraphicCoordinates(y, map.height-x-1, map.tileWidth/2, map.tileHeight/2);
                }
            }
            field = newCells;
        }
    }

    public void flipX() {
        Cell[][] newCells = new Cell[map.width][map.height];
        for (int y = 0; y < map.height; y++) {
            for (int x = 0; x < map.width; x++) {
                newCells[map.width-x-1][y] = field[x][y];
                newCells[map.width-x-1][y].setGraphicCoordinates(map.width-x-1, y, map.tileWidth/2, map.tileHeight/2);
            }
        }
        field = newCells;
    }

    public void flipY() {
        Cell[][] newCells = new Cell[map.width][map.height];
        for(int y = 0; y < map.height; y++) {
            for(int x = 0; x < map.width; x++) {
                newCells[x][map.height-y-1] = field[x][y];
                newCells[x][map.height-y-1].setGraphicCoordinates(x, map.height-y-1, map.tileWidth/2, map.tileHeight/2);
            }
        }
        field = newCells;
    }

    public char[][] getCharMatrix() {
        if (field != null) {
            char[][] charMatrix = new char[map.height][map.width];
            for (int y = 0; y < map.height; y++) {
                for (int x = 0; x < map.width; x++) {
                    if (field[x][y].isTerrain() || field[x][y].getTower() != null) {
                        if (field[x][y].getTower() != null && field[x][y].getTower().templateForTower.towerAttackType == TowerAttackType.Pit) {
                            charMatrix[y][x] = '.';
                        } else {
                            charMatrix[y][x] = 'T';
                        }
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
        Gdx.app.log("GameField::dispose()", "-- Called!");
//        shapeRenderer.dispose();
//        spriteBatch.dispose();
//        bitmapFont.dispose();
        map.dispose();
        greenCheckmark.dispose();
        redCross.dispose();
    }

    public void render(float delta, CameraController cameraController) {
        delta = delta * gameSpeed;
        if (!gamePaused) {
            spawnUnits(delta);
            stepAllUnits(delta, cameraController);
            shotAllTowers(delta, cameraController);
            moveAllShells(delta);
        }

        cameraController.spriteBatch.setProjectionMatrix(cameraController.camera.combined);
        cameraController.spriteBatch.begin();
        if(cameraController.isDrawableBackground > 0) {
            drawBackGrounds(cameraController);
        }
        if(cameraController.isDrawableGround > 0 || cameraController.isDrawableUnits > 0 || cameraController.isDrawableTowers > 0) {
            drawGroundsWithUnitsAndTowers(cameraController);
//            drawTowersUnderConstruction(cameraController);
        }
        if (cameraController.isDrawableForeground > 0) {
            drawForeGrounds(cameraController);
        }
        cameraController.spriteBatch.end();

        cameraController.shapeRenderer.setProjectionMatrix(cameraController.camera.combined);
        cameraController.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawUnitsBars(cameraController);
        cameraController.shapeRenderer.end();

        if (cameraController.isDrawableGrid > 0)
            drawGrid(cameraController);

        if (cameraController.isDrawableGridNav > 0) {
            drawRoutes(cameraController);
//            drawWavesRoutes(camera);
            drawGridNav(cameraController);
        }

        cameraController.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        cameraController.spriteBatch.begin();
        drawShells(cameraController);
        drawTowersUnderConstruction(cameraController);
        cameraController.spriteBatch.end();
        cameraController.shapeRenderer.end();

        cameraController.shapeRenderer.setColor(Color.RED);
        cameraController.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        cameraController.shapeRenderer.circle(0f, 0f, 5);
        cameraController.shapeRenderer.end();
    }

    private void drawBackGrounds(CameraController cameraController) {
        if(cameraController.drawOrder == 0) {
            for (int y = 0; y < map.height; y++) {
                for (int x = 0; x < map.width; x++) {
                    drawBackGroundCell(cameraController, x, y);
                }
            }
        } else if(cameraController.drawOrder == 1) {
            for (int x = 0; x < map.width; x++) {
                for (int y = 0; y < map.height; y++) {
                    drawBackGroundCell(cameraController, x, y);
                }
            }
        } else if(cameraController.drawOrder == 2) {
            for (int y = map.height-1; y >= 0; y--) {
                for (int x = map.width-1; x >= 0; x--) {
                    drawBackGroundCell(cameraController, x, y);
                }
            }
        } else if(cameraController.drawOrder == 3) {
            for (int x = map.width-1; x >= 0; x--) {
                for (int y = map.height-1; y >= 0; y--) {
                    drawBackGroundCell(cameraController, x, y);
                }
            }
        } else if(cameraController.drawOrder == 4) {
            for (int y = map.height-1; y >= 0; y--) {
                for (int x = 0; x < map.width; x++) {
                    drawBackGroundCell(cameraController, x, y);
                }
            }
        } else if(cameraController.drawOrder == 5) {
            for (int x = 0; x < map.width; x++) {
                for (int y = map.height-1; y >= 0; y--) {
                    drawBackGroundCell(cameraController, x, y);
                }
            }
        } else if(cameraController.drawOrder == 6) {
            for (int y = 0; y < map.height; y++) {
                for (int x = map.width-1; x >= 0; x--) {
                    drawBackGroundCell(cameraController, x, y);
                }
            }
        } else if(cameraController.drawOrder == 7) {
            for (int x = map.width-1; x >= 0; x--) {
                for (int y = 0; y < map.height; y++) {
                    drawBackGroundCell(cameraController, x, y);
                }
            }
        } else if(cameraController.drawOrder == 8) {
            int x = 0, y = 0;
            int length = (map.width > map.height) ? map.width : map.height;
            while (x < length) {
                if(x < map.width && y < map.height) {
                    if (x == length - 1 && y == length - 1) {
                        drawBackGroundCell(cameraController, x, y);
                    } else {
                        drawBackGroundCell(cameraController, x, y);
                    }
                }
                if (x == length - 1) {
                    x = y + 1;
                    y = length - 1;
                } else if (y == 0) {
                    y = x + 1;
                    x = 0;
                } else {
                    x++;
                    y--;
                }
            }
        }
    }

    private void drawBackGroundCell(CameraController cameraController, int cellX, int cellY) {
        float deltaX = cameraController.halfSizeCellX;
        float deltaY = cameraController.halfSizeCellY;
        Cell cell = field[cellX][cellY];
        Array<TiledMapTile> tiledMapTiles = cell.backgroundTiles;
        for (TiledMapTile tiledMapTile : tiledMapTiles) {
            TextureRegion textureRegion = tiledMapTile.getTextureRegion();
            if (cameraController.isDrawableBackground == 1 || cameraController.isDrawableBackground == 5) {
                cameraController.spriteBatch.draw(textureRegion, cell.graphicCoordinates1.x-deltaX, cell.graphicCoordinates1.y-deltaY);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
            if (cameraController.isDrawableBackground == 2 || cameraController.isDrawableBackground == 5) {
                cameraController.spriteBatch.draw(textureRegion, cell.graphicCoordinates2.x-deltaX, cell.graphicCoordinates2.y-deltaY);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
            if (cameraController.isDrawableBackground == 3 || cameraController.isDrawableBackground == 5) {
                cameraController.spriteBatch.draw(textureRegion, cell.graphicCoordinates3.x-deltaX, cell.graphicCoordinates3.y-deltaY);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
            if (cameraController.isDrawableBackground == 4 || cameraController.isDrawableBackground == 5) {
                cameraController.spriteBatch.draw(textureRegion, cell.graphicCoordinates4.x-deltaX, cell.graphicCoordinates4.y-deltaY);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
        }
    }

    private void drawGroundsWithUnitsAndTowers(CameraController cameraController) {
        if(cameraController.drawOrder == 0) {
            for (int y = 0; y < map.height; y++) {
                for (int x = 0; x < map.width; x++) {
                    drawGroundCellWithUnitsAndTower(cameraController, x, y);
                }
            }
        } else if(cameraController.drawOrder == 1) {
            for (int x = 0; x < map.width; x++) {
                for (int y = 0; y < map.height; y++) {
                    drawGroundCellWithUnitsAndTower(cameraController, x, y);
                }
            }
        } else if(cameraController.drawOrder == 2) {
            for (int y = map.height-1; y >= 0; y--) {
                for (int x = map.width-1; x >= 0; x--) {
                    drawGroundCellWithUnitsAndTower(cameraController, x, y);
                }
            }
        } else if(cameraController.drawOrder == 3) {
            for (int x = map.width-1; x >= 0; x--) {
                for (int y = map.height-1; y >= 0; y--) {
                    drawGroundCellWithUnitsAndTower(cameraController, x, y);
                }
            }
        } else if(cameraController.drawOrder == 4) {
            for (int y = map.height-1; y >= 0; y--) {
                for (int x = 0; x < map.width; x++) {
                    drawGroundCellWithUnitsAndTower(cameraController, x, y);
                }
            }
        } else if(cameraController.drawOrder == 5) {
            for (int x = 0; x < map.width; x++) {
                for (int y = map.height-1; y >= 0; y--) {
                    drawGroundCellWithUnitsAndTower(cameraController, x, y);
                }
            }
        } else if(cameraController.drawOrder == 6) {
            for (int y = 0; y < map.height; y++) {
                for (int x = map.width-1; x >= 0; x--) {
                    drawGroundCellWithUnitsAndTower(cameraController, x, y);
                }
            }
        } else if(cameraController.drawOrder == 7) {
            for (int x = map.width-1; x >= 0; x--) {
                for (int y = 0; y < map.height; y++) {
                    drawGroundCellWithUnitsAndTower(cameraController, x, y);
                }
            }
        } else if(cameraController.drawOrder == 8) {
            int x = 0, y = 0;
            int length = (map.width > map.height) ? map.width : map.height;
            while (x < length) {
                if(x < map.width && y < map.height) {
                    if (x == length - 1 && y == length - 1) {
                        drawGroundCellWithUnitsAndTower(cameraController, x, y);
                    } else {
                        drawGroundCellWithUnitsAndTower(cameraController, x, y);
                    }
                }
                if (x == length - 1) {
                    x = y + 1;
                    y = length - 1;
                } else if (y == 0) {
                    y = x + 1;
                    x = 0;
                } else {
                    x++;
                    y--;
                }
            }
        }
    }

    private void drawGroundCellWithUnitsAndTower(CameraController cameraController, int cellX, int cellY) {
        float deltaX = cameraController.halfSizeCellX;
        float deltaY = cameraController.halfSizeCellY;
        Cell cell = field[cellX][cellY];
        Array<TiledMapTile> tiledMapTiles = cell.groundTiles;
        for (TiledMapTile tiledMapTile : tiledMapTiles) {
            TextureRegion textureRegion = tiledMapTile.getTextureRegion();
            if(cameraController.isDrawableGround == 1 || cameraController.isDrawableGround == 5) {
                cameraController.spriteBatch.draw(textureRegion, cell.graphicCoordinates1.x-deltaX, cell.graphicCoordinates1.y-deltaY);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
            if(cameraController.isDrawableGround == 2 || cameraController.isDrawableGround == 5) {
                cameraController.spriteBatch.draw(textureRegion, cell.graphicCoordinates2.x-deltaX, cell.graphicCoordinates2.y-deltaY);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
            if(cameraController.isDrawableGround == 3 || cameraController.isDrawableGround == 5) {
                cameraController.spriteBatch.draw(textureRegion, cell.graphicCoordinates3.x-deltaX, cell.graphicCoordinates3.y-deltaY);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
            if(cameraController.isDrawableGround == 4 || cameraController.isDrawableGround == 5) {
                cameraController.spriteBatch.draw(textureRegion, cell.graphicCoordinates4.x-deltaX, cell.graphicCoordinates4.y-deltaY);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
        }
        Array<Unit> units = field[cellX][cellY].getUnits();
        if(units != null) {
            Color oldColorSB = cameraController.spriteBatch.getColor();
            for (Unit unit : units) {
                drawUnit(cameraController, unit);
            }
            cameraController.spriteBatch.setColor(oldColorSB);
        }
        Tower tower = field[cellX][cellY].getTower();
        if(tower != null) {
            drawTower(cameraController, tower);
        }
    }

    private void drawForeGrounds(CameraController cameraController) {
        if(cameraController.drawOrder == 0) {
            for (int y = 0; y < map.height; y++) {
                for (int x = 0; x < map.width; x++) {
                    drawForeGroundCell(cameraController, x, y);
                }
            }
        } else if(cameraController.drawOrder == 1) {
            for (int x = 0; x < map.width; x++) {
                for (int y = 0; y < map.height; y++) {
                    drawForeGroundCell(cameraController, x, y);
                }
            }
        } else if(cameraController.drawOrder == 2) {
            for (int y = map.height-1; y >= 0; y--) {
                for (int x = map.width-1; x >= 0; x--) {
                    drawForeGroundCell(cameraController, x, y);
                }
            }
        } else if(cameraController.drawOrder == 3) {
            for (int x = map.width-1; x >= 0; x--) {
                for (int y = map.height-1; y >= 0; y--) {
                    drawForeGroundCell(cameraController, x, y);
                }
            }
        } else if(cameraController.drawOrder == 4) {
            for (int y = map.height-1; y >= 0; y--) {
                for (int x = 0; x < map.width; x++) {
                    drawForeGroundCell(cameraController, x, y);
                }
            }
        } else if(cameraController.drawOrder == 5) {
            for (int x = 0; x < map.width; x++) {
                for (int y = map.height-1; y >= 0; y--) {
                    drawForeGroundCell(cameraController, x, y);
                }
            }
        } else if(cameraController.drawOrder == 6) {
            for (int y = 0; y < map.height; y++) {
                for (int x = map.width-1; x >= 0; x--) {
                    drawForeGroundCell(cameraController, x, y);
                }
            }
        } else if(cameraController.drawOrder == 7) {
            for (int x = map.width-1; x >= 0; x--) {
                for (int y = 0; y < map.height; y++) {
                    drawForeGroundCell(cameraController, x, y);
                }
            }
        } else if(cameraController.drawOrder == 8) {
            int x = 0, y = 0;
            int length = (map.width > map.height) ? map.width : map.height;
            while (x < length) {
                if(x < map.width && y < map.height) {
                    if (x == length - 1 && y == length - 1) {
                        drawForeGroundCell(cameraController, x, y);
                    } else {
                        drawForeGroundCell(cameraController, x, y);
                    }
                }
                if (x == length - 1) {
                    x = y + 1;
                    y = length - 1;
                } else if (y == 0) {
                    y = x + 1;
                    x = 0;
                } else {
                    x++;
                    y--;
                }
            }
        }
    }

    private void drawForeGroundCell(CameraController cameraController, int cellX, int cellY) {
        float deltaX = cameraController.halfSizeCellX;
        float deltaY = cameraController.halfSizeCellY;
        Cell cell = field[cellX][cellY];
        Array<TiledMapTile> tiledMapTiles = cell.foregroundTiles;
        for (TiledMapTile tiledMapTile : tiledMapTiles) {
            TextureRegion textureRegion = tiledMapTile.getTextureRegion();
            if (cameraController.isDrawableForeground == 1 || cameraController.isDrawableForeground == 5) {
                cameraController.spriteBatch.draw(textureRegion, cell.graphicCoordinates1.x-deltaX, cell.graphicCoordinates1.y-deltaY);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
            if (cameraController.isDrawableForeground == 2 || cameraController.isDrawableForeground == 5) {
                cameraController.spriteBatch.draw(textureRegion, cell.graphicCoordinates2.x-deltaX, cell.graphicCoordinates2.y-deltaY);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
            if (cameraController.isDrawableForeground == 3 || cameraController.isDrawableForeground == 5) {
                cameraController.spriteBatch.draw(textureRegion, cell.graphicCoordinates3.x-deltaX, cell.graphicCoordinates3.y-deltaY);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
            if (cameraController.isDrawableForeground == 4 || cameraController.isDrawableForeground == 5) {
                cameraController.spriteBatch.draw(textureRegion, cell.graphicCoordinates4.x-deltaX, cell.graphicCoordinates4.y-deltaY);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
        }
    }

    private void drawGrid(CameraController cameraController) {
        cameraController.shapeRenderer.setProjectionMatrix(cameraController.camera.combined);
        cameraController.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        cameraController.shapeRenderer.setColor(Color.BROWN); // (100, 60, 21, 1f);

        float halfSizeCellX = cameraController.halfSizeCellX;
        float halfSizeCellY = cameraController.halfSizeCellY;
        float widthForTop = map.height * halfSizeCellX; // A - B
        float heightForTop = map.height * halfSizeCellY; // B - Top
        float widthForBottom = map.width * halfSizeCellX; // A - C
        float heightForBottom = map.width * halfSizeCellY; // C - Bottom
//        Gdx.app.log("GameField::drawGrid(camera)", "-- widthForTop:" + widthForTop + " heightForTop:" + heightForTop + " widthForBottom:" + widthForBottom + " heightForBottom:" + heightForBottom);

        if(cameraController.isDrawableGrid == 1 || cameraController.isDrawableGrid == 5) {
            for (int x = 0; x <= map.width; x++)
                cameraController.shapeRenderer.line( (halfSizeCellX*x),-(halfSizeCellY*x),-(widthForTop)+(halfSizeCellX*x),   -(heightForTop)-(x*halfSizeCellY));
            for (int y = 0; y <= map.height; y++)
                cameraController.shapeRenderer.line(-(halfSizeCellX*y),-(halfSizeCellY*y), (widthForBottom)-(halfSizeCellX*y),-(heightForBottom)-(halfSizeCellY*y));
        }
        if(cameraController.isDrawableGrid == 2 || cameraController.isDrawableGrid == 5) {
            for (int x = 0; x <= map.width; x++)
                cameraController.shapeRenderer.line((halfSizeCellX*x),-(halfSizeCellY*x),(widthForTop)+(halfSizeCellX*x),    (heightForTop)-(x*halfSizeCellY));
            for (int y = 0; y <= map.height; y++)
                cameraController.shapeRenderer.line((halfSizeCellX*y), (halfSizeCellY*y),(widthForBottom)+(halfSizeCellX*y),-(heightForBottom)+(halfSizeCellY*y));
        }
        if(cameraController.isDrawableGrid == 3 || cameraController.isDrawableGrid == 5) {
            for (int x = 0; x <= map.height; x++) // WHT??? map.height check groundDraw
                cameraController.shapeRenderer.line(-(halfSizeCellX*x),(halfSizeCellY*x), (widthForBottom)-(halfSizeCellX*x),(heightForBottom)+(x*halfSizeCellY));
            for (int y = 0; y <= map.width; y++) // WHT??? map.width check groundDraw
                cameraController.shapeRenderer.line( (halfSizeCellX*y),(halfSizeCellY*y),-(widthForTop)+(halfSizeCellX*y),   (heightForTop)+(halfSizeCellY*y));
        }
        if(cameraController.isDrawableGrid == 4 || cameraController.isDrawableGrid == 5) {
            for (int x = 0; x <= map.height; x++) // WHT??? map.height check groundDraw
                cameraController.shapeRenderer.line(-(halfSizeCellX*x), (halfSizeCellY*x),-(widthForBottom)-(halfSizeCellX*x),   -(heightForBottom)+(x*halfSizeCellY));
            for (int y = 0; y <= map.width; y++) // WHT??? map.width check groundDraw
                cameraController.shapeRenderer.line(-(halfSizeCellX*y),-(halfSizeCellY*y),-(widthForTop)-(halfSizeCellX*y),(heightForTop)-(halfSizeCellY*y));
        }
        cameraController.shapeRenderer.end();
    }

//    private void drawUnitsAndTowers(CameraController cameraController) {
//        getPriorityMap();
//        for (Object obj : priorityMap.values()) {
//            if (obj instanceof Tower) {
//                drawTower((Tower) obj, spriteBatch);
//            } else {
//                for (Unit unit : (List<Unit>) obj) {
//                    drawUnit(unit, spriteBatch);
//                }
//            }
//        }
//    }
//
//    private void getPriorityMap() {
//        priorityMap.clear();
//        for (Tower tower : towersManager.getAllTemplateForTowers()) {
//            priorityMap.put(tower.getPosition().x * 1000 - tower.getPosition().y, tower);
//        }
//        for (Unit unit : unitsManager.units) {
//            List list;
//            Integer key = unit.getNewPosition().getX() * 1000 - unit.getNewPosition().getY();
//            if (priorityMap.containsKey(key) && (priorityMap.get(key) instanceof List)) {
//                list = (List) priorityMap.get(key);
//                list.add(unit);
//                priorityMap.put(key, list);
//            } else {
//                list = new ArrayList<Object>();
//                list.add(unit);
//                priorityMap.put(unit.getNewPosition().getX() * 1000 - unit.getNewPosition().getY(), list);
//            }
//        }
//    }

//    private void drawUnits(CameraController cameraController) {
//        for (Unit unit : unitsManager.units) {
//            drawUnit(unit, spriteBatch);
//        }
//    }

    private void drawUnit(CameraController cameraController, Unit unit) { //TODO Need to refactor this
//        Gdx.app.log("GameField::drawUnit(" + unit + "," + spriteBatch + ")", "-- Start!");
        for (ShellEffectType shellAttackType : unit.shellEffectTypes) {
            if(shellAttackType.shellEffectEnum == ShellEffectType.ShellEffectEnum.FreezeEffect) {
                cameraController.spriteBatch.setColor(0.0f, 0.0f, 1.0f, 0.9f);
                // Gdx.app.log("GameField::drawUnit(" + unit + "," + spriteBatch + ")", "-- FreezeEffect!");
            }
            if(shellAttackType.shellEffectEnum == ShellEffectType.ShellEffectEnum.FireEffect) {
                cameraController.spriteBatch.setColor(1.0f, 0.0f, 0.0f, 0.9f);
                // Gdx.app.log("GameField::drawUnit(" + unit + "," + spriteBatch + ")", "-- FireEffect!");
            }
        }
        TextureRegion currentFrame;
        if (unit.isAlive()) {
            currentFrame = unit.getCurrentFrame();
        } else {
            currentFrame = unit.getCurrentDeathFrame();
        }
        float deltaX = cameraController.halfSizeCellX;
        float deltaY = cameraController.sizeCellY;
        float sizeCellX = cameraController.sizeCellX;
        float sizeCellY = cameraController.sizeCellY;

        float fVx = 0f, fVy = 0f;
        if(cameraController.isDrawableUnits == 1 || cameraController.isDrawableUnits == 5) {
            fVx = unit.circle1.x - deltaX;
            fVy = unit.circle1.y - deltaY;
            cameraController.spriteBatch.draw(currentFrame, fVx, fVy, sizeCellX, sizeCellY*2);
        }
        if(cameraController.isDrawableUnits == 2 || cameraController.isDrawableUnits == 5) {
            fVx = unit.circle2.x - deltaX;
            fVy = unit.circle2.y - deltaY;
            cameraController.spriteBatch.draw(currentFrame, fVx, fVy, sizeCellX, sizeCellY*2);
        }
        if(cameraController.isDrawableUnits == 3 || cameraController.isDrawableUnits == 5) {
            fVx = unit.circle3.x - deltaX;
            fVy = unit.circle3.y - deltaY;
            cameraController.spriteBatch.draw(currentFrame, fVx, fVy, sizeCellX, sizeCellY*2);
        }
        if(cameraController.isDrawableUnits == 4 || cameraController.isDrawableUnits == 5) {
            fVx = unit.circle4.x - deltaX;
            fVy = unit.circle4.y - deltaY;
            cameraController.spriteBatch.draw(currentFrame, fVx, fVy, sizeCellX, sizeCellY*2);
        }
//        drawUnitBar(shapeRenderer, unit, currentFrame, fVx, fVy);
    }

    private void drawUnitsBars(CameraController cameraController) {
        for (Unit unit : unitsManager.units) {
            if(cameraController.isDrawableUnits == 1 || cameraController.isDrawableUnits == 5) {
                drawUnitBar(cameraController, unit, unit.circle1.x, unit.circle1.y);
            }
            if(cameraController.isDrawableUnits == 2 || cameraController.isDrawableUnits == 5) {
                drawUnitBar(cameraController, unit, unit.circle2.x, unit.circle2.y);
            }
            if(cameraController.isDrawableUnits == 3 || cameraController.isDrawableUnits == 5) {
                drawUnitBar(cameraController, unit, unit.circle3.x, unit.circle3.y);
            }
            if(cameraController.isDrawableUnits == 4 || cameraController.isDrawableUnits == 5) {
                drawUnitBar(cameraController, unit, unit.circle4.x, unit.circle4.y);
            }
        }
    }

    private void drawUnitBar(CameraController cameraController, Unit unit, float fVx, float fVy) {
        if (unit.isAlive()) {
            TextureRegion currentFrame = unit.getCurrentFrame();
            fVx -= cameraController.sizeCellX/2;
            fVy -= cameraController.sizeCellY;
            float currentFrameWidth = currentFrame.getRegionWidth();
            float currentFrameHeight = currentFrame.getRegionHeight();
            float hpBarSpace = 0.8f;
            float effectBarWidthSpace = hpBarSpace * 2;
            float effectBarHeightSpace = hpBarSpace * 2;
            float hpBarHPWidth = 30f;
            float effectBarWidth = hpBarHPWidth - effectBarWidthSpace * 2;
            float hpBarHeight = 7f;
            float effectBarHeight = hpBarHeight - (effectBarHeightSpace * 2);
            float hpBarWidthIndent = (currentFrameWidth - hpBarHPWidth) / 2;
            float hpBarTopIndent = hpBarHeight;

            cameraController.shapeRenderer.setColor(Color.BLACK);
            cameraController.shapeRenderer.rect(fVx + hpBarWidthIndent, fVy + currentFrameHeight - hpBarTopIndent, hpBarHPWidth, hpBarHeight);
            cameraController.shapeRenderer.setColor(Color.GREEN);
            float maxHP = unit.templateForUnit.healthPoints;
            float hp = unit.hp;
            hpBarHPWidth = hpBarHPWidth / maxHP * hp;
            cameraController.shapeRenderer.rect(fVx + hpBarWidthIndent + hpBarSpace, fVy + currentFrameHeight - hpBarTopIndent + hpBarSpace, hpBarHPWidth - (hpBarSpace * 2), hpBarHeight - (hpBarSpace * 2));

            float allTime = 0f;
            for (ShellEffectType shellEffectType : unit.shellEffectTypes)
                allTime += shellEffectType.time;

            float effectWidth = effectBarWidth / allTime;
            float efX = fVx + hpBarWidthIndent + effectBarWidthSpace;
            float efY = fVy + currentFrameHeight - hpBarTopIndent + effectBarHeightSpace;
            float effectBlockWidth = effectBarWidth / unit.shellEffectTypes.size;
            for (int effectIndex = 0; effectIndex < unit.shellEffectTypes.size; effectIndex++) {
                ShellEffectType shellEffectType = unit.shellEffectTypes.get(effectIndex);
                if (shellEffectType.shellEffectEnum == ShellEffectType.ShellEffectEnum.FireEffect) {
                    cameraController.shapeRenderer.setColor(Color.RED);
                } else if (shellEffectType.shellEffectEnum == ShellEffectType.ShellEffectEnum.FreezeEffect) {
                    cameraController.shapeRenderer.setColor(Color.ROYAL);
                }
                float efWidth = effectBlockWidth - effectWidth * shellEffectType.elapsedTime;
                cameraController.shapeRenderer.rect(efX, efY, efWidth, effectBarHeight);
                efX += effectBlockWidth;
//                Gdx.app.log("GameField::drawUnit()", "-- efX:" + efX + " efWidth:" + efWidth + ":" + effectIndex);
            }
        }
    }

    private void drawRoutes(CameraController cameraController) {
        cameraController.shapeRenderer.setProjectionMatrix(cameraController.camera.combined);
        cameraController.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        cameraController.shapeRenderer.setColor(Color.BROWN); // (100, 60, 21, 1f);

        float gridNavRadius = cameraController.sizeCellX/15f;
        for (Unit unit : unitsManager.units) {
            ArrayDeque<Node> route = unit.route;
            if (route != null) {
                for (Node coor : route) {
                    Cell cell = field[coor.getX()][coor.getY()];
                    if(cameraController.isDrawableGridNav == 1 || cameraController.isDrawableGridNav == 5) {
                        cameraController.shapeRenderer.circle(cell.graphicCoordinates1.x, cell.graphicCoordinates1.y, gridNavRadius);
                    }
                    if(cameraController.isDrawableGridNav == 2 || cameraController.isDrawableGridNav == 5) {
                        cameraController.shapeRenderer.circle(cell.graphicCoordinates2.x, cell.graphicCoordinates2.y, gridNavRadius);
                    }
                    if(cameraController.isDrawableGridNav == 3 || cameraController.isDrawableGridNav == 5) {
                        cameraController.shapeRenderer.circle(cell.graphicCoordinates3.x, cell.graphicCoordinates3.y, gridNavRadius);
                    }
                    if(cameraController.isDrawableGridNav == 4 || cameraController.isDrawableGridNav == 5) {
                        cameraController.shapeRenderer.circle(cell.graphicCoordinates4.x, cell.graphicCoordinates4.y, gridNavRadius);
                    }
                }
            }
        }
        cameraController.shapeRenderer.end();
    }

    private void drawWavesRoutes(CameraController cameraController) {
        cameraController.shapeRenderer.setProjectionMatrix(cameraController.camera.combined);
        cameraController.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        cameraController.shapeRenderer.setColor(Color.BROWN);
        for (Wave wave : waveManager.waves) {
            drawWave(cameraController, wave);
        }
        cameraController.shapeRenderer.setColor(Color.BLUE);
        for (Wave wave : waveManager.wavesForUser) {
            drawWave(cameraController, wave);
        }
        cameraController.shapeRenderer.end();
    }

    public void drawWave(CameraController cameraController, Wave wave) {
//        Gdx.app.log("GameField::drawWave(" + wave + ")", "--");
        float linesWidth = cameraController.sizeCellX/15f;
        ArrayDeque<Node> route = wave.route;
        if (route != null && !route.isEmpty()) {
            Iterator<Node> nodeIterator = route.iterator();
            Node startNode = nodeIterator.next();
            Node endNode = null;
            while (nodeIterator.hasNext()) {
                endNode = nodeIterator.next();
                Cell startCell = field[startNode.getX()][startNode.getY()];
                Cell endCell = field[endNode.getX()][endNode.getY()];
                if(cameraController.isDrawableGridNav == 1 || cameraController.isDrawableGridNav == 5) {
                    cameraController.shapeRenderer.rectLine(startCell.graphicCoordinates1, endCell.graphicCoordinates1, linesWidth);
                }
                if(cameraController.isDrawableGridNav == 2 || cameraController.isDrawableGridNav == 5) {
                    cameraController.shapeRenderer.rectLine(startCell.graphicCoordinates2, endCell.graphicCoordinates2, linesWidth);
                }
                if(cameraController.isDrawableGridNav == 3 || cameraController.isDrawableGridNav == 5) {
                    cameraController.shapeRenderer.rectLine(startCell.graphicCoordinates3, endCell.graphicCoordinates3, linesWidth);
                }
                if(cameraController.isDrawableGridNav == 4 || cameraController.isDrawableGridNav == 5) {
                    cameraController.shapeRenderer.rectLine(startCell.graphicCoordinates4, endCell.graphicCoordinates4, linesWidth);
                }
                startNode = endNode;
            }
        }
    }

//    private void drawTowers(CameraController cameraController) {
//        for (Tower tower : towersManager.getAllTemplateForTowers()) {
//            drawTower(tower, spriteBatch);
//        }
//    }

    private void drawTower(CameraController cameraController, Tower tower) {
        Cell cell = tower.cell;
        int towerSize = tower.templateForTower.size;
//        Vector2 towerPos = cell.getGraphicCoordinates(cameraController.isDrawableTowers);
//        cameraController.shapeRender.circle(towerPos)
        Vector2 towerPos = new Vector2();
        TextureRegion currentFrame = tower.templateForTower.idleTile.getTextureRegion();
        float sizeCellX = cameraController.sizeCellX;
        float sizeCellY = cameraController.sizeCellY;
        if (cameraController.isDrawableTowers == 5) {
            for (int m = 1; m < cameraController.isDrawableTowers; m++) {
                towerPos.set(cell.getGraphicCoordinates(m));
                cameraController.getCorrectGraphicTowerCoord(towerPos, towerSize, m);
                cameraController.spriteBatch.draw(currentFrame, towerPos.x, towerPos.y, sizeCellX * towerSize, (sizeCellY * 2) * towerSize);
            }
        } else if (cameraController.isDrawableTowers != 0) {
            towerPos.set(cell.getGraphicCoordinates(cameraController.isDrawableTowers));
            cameraController.getCorrectGraphicTowerCoord(towerPos, towerSize, cameraController.isDrawableTowers);
            cameraController.spriteBatch.draw(currentFrame, towerPos.x, towerPos.y, sizeCellX * towerSize, (sizeCellY * 2) * towerSize);
        }
        towerPos = null; // delete towerPos;
    }

    private void drawGridNav(CameraController cameraController) {
        cameraController.shapeRenderer.setProjectionMatrix(cameraController.camera.combined);
        cameraController.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        Vector2 pos = new Vector2();
        float gridNavRadius = cameraController.sizeCellX/20f;
        for (int y = 0; y < map.height; y++) {
            for (int x = 0; x < map.width; x++) {
                Cell cell = field[x][y];
                if (cell != null && !cell.isEmpty()) {
                    if (cell.isTerrain()) {
                        cameraController.shapeRenderer.setColor(Color.RED);
                    } else if (cell.getUnit() != null) {
                        cameraController.shapeRenderer.setColor(Color.GREEN);
                    } else if (cell.getTower() != null) {
//                        cameraController.shapeRenderer.setColor(new Color(225f, 224f, 0f, 255f));
                        cameraController.shapeRenderer.setColor(Color.YELLOW);
                    }
                    if(cameraController.isDrawableGridNav == 1 || cameraController.isDrawableGridNav == 5) {
                        pos.set(cell.getGraphicCoordinates(1));
                        cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                    }
                    if(cameraController.isDrawableGridNav == 2 || cameraController.isDrawableGridNav == 5) {
                        pos.set(cell.getGraphicCoordinates(2));
                        cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                    }
                    if(cameraController.isDrawableGridNav == 3 || cameraController.isDrawableGridNav == 5) {
                        pos.set(cell.getGraphicCoordinates(3));
                        cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                    }
                    if(cameraController.isDrawableGridNav == 4 || cameraController.isDrawableGridNav == 5) {
                        pos.set(cell.getGraphicCoordinates(4));
                        cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                    }
                }
            }
        }

        Array<GridPoint2> spawnPoints = waveManager.getAllSpawnPoint();
        cameraController.shapeRenderer.setColor(new Color(0f, 255f, 204f, 255f));
        for (GridPoint2 spawnPoint : spawnPoints) {
            Cell cell = field[spawnPoint.x][spawnPoint.y];
            if(cameraController.isDrawableGridNav == 1 || cameraController.isDrawableGridNav == 5) {
                pos.set(cell.getGraphicCoordinates(1));
                cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
            }
            if(cameraController.isDrawableGridNav == 2 || cameraController.isDrawableGridNav == 5) {
                pos.set(cell.getGraphicCoordinates(2));
                cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
            }
            if(cameraController.isDrawableGridNav == 3 || cameraController.isDrawableGridNav == 5) {
                pos.set(cell.getGraphicCoordinates(3));
                cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
            }
            if(cameraController.isDrawableGridNav == 4 || cameraController.isDrawableGridNav == 5) {
                pos.set(cell.getGraphicCoordinates(4));
                cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
            }
        }

        Array<GridPoint2> exitPoints = waveManager.getAllExitPoint();
        cameraController.shapeRenderer.setColor(new Color(255f, 0f, 102f, 255f));
        for (GridPoint2 exitPoint : exitPoints) {
//            Gdx.app.log("GameField::drawGridNav()", "-- exitCell.cellX:" + exitCell.cellX + " exitCell.y:" + exitCell.y + " cameraController.isDrawableGridNav:" + cameraController.isDrawableGridNav);
            Cell cell = field[exitPoint.x][exitPoint.y];
            if(cameraController.isDrawableGridNav == 1 || cameraController.isDrawableGridNav == 5) {
                pos.set(cell.getGraphicCoordinates(1));
                cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
            }
            if(cameraController.isDrawableGridNav == 2 || cameraController.isDrawableGridNav == 5) {
                pos.set(cell.getGraphicCoordinates(2));
                cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
            }
            if(cameraController.isDrawableGridNav == 3 || cameraController.isDrawableGridNav == 5) {
                pos.set(cell.getGraphicCoordinates(3));
                cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
            }
            if(cameraController.isDrawableGridNav == 4 || cameraController.isDrawableGridNav == 5) {
                pos.set(cell.getGraphicCoordinates(4));
                cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
            }
        }

        cameraController.shapeRenderer.setColor(Color.ORANGE);
        for (Tower tower : towersManager.towers) {
            for (Bullet bullet : tower.bullets) {
                cameraController.shapeRenderer.rectLine(bullet.currentPoint.x, bullet.currentPoint.y, bullet.endPoint.x, bullet.endPoint.y, cameraController.sizeCellX/40f);
                if (null != bullet.circle) {
                    cameraController.shapeRenderer.circle(bullet.circle.x, bullet.circle.y, bullet.circle.radius);
                }
            }
        }
        cameraController.shapeRenderer.end();

        cameraController.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        cameraController.shapeRenderer.setColor(Color.RED);
        for (Unit unit : unitsManager.units) {
            if(cameraController.isDrawableUnits == 1 || cameraController.isDrawableUnits == 5)
                cameraController.shapeRenderer.circle(unit.circle1.x, unit.circle1.y, unit.circle1.radius);
            if(cameraController.isDrawableUnits == 2 || cameraController.isDrawableUnits == 5)
                cameraController.shapeRenderer.circle(unit.circle2.x, unit.circle2.y, unit.circle2.radius);
            if(cameraController.isDrawableUnits == 3 || cameraController.isDrawableUnits == 5)
                cameraController.shapeRenderer.circle(unit.circle3.x, unit.circle3.y, unit.circle3.radius);
            if(cameraController.isDrawableUnits == 4 || cameraController.isDrawableUnits == 5)
                cameraController.shapeRenderer.circle(unit.circle4.x, unit.circle4.y, unit.circle4.radius);
        }

        cameraController.shapeRenderer.setColor(new Color(153f, 255f, 51f, 1f));
//        Vector2 towerPos = new Vector2();
        for (Tower tower : towersManager.towers) { // Draw white towers radius! -- radiusDetectionCircle
            if (tower.radiusDetectionCircle != null) {
                if (cameraController.isDrawableGridNav == 5) {
                    if (cameraController.isDrawableTowers == 5) {
                        for (int m = 1; m < cameraController.isDrawableTowers; m++) {
//                            towerPos.set(cameraController.getCenterGraphicCoord(tower.cell.x, tower.cell.y, m)); // Need recoding this func!
                            cameraController.shapeRenderer.circle(tower.centerGraphicCoord.x, tower.centerGraphicCoord.y, tower.radiusDetectionCircle.radius);
                        }
                    } else if (cameraController.isDrawableTowers != 0) {
//                        towerPos.set(cameraController.getCenterGraphicCoord(tower.cell.x, tower.cell.y, cameraController.isDrawableTowers));
                        cameraController.shapeRenderer.circle(tower.centerGraphicCoord.x, tower.centerGraphicCoord.y, tower.radiusDetectionCircle.radius);
                    }
                } else /*if(cameraController.isDrawableGridNav != 0)*/ {
                    if (cameraController.isDrawableGridNav == cameraController.isDrawableTowers) {
//                        towerPos.set(cameraController.getCenterGraphicCoord(tower.cell.x, tower.cell.y, cameraController.isDrawableTowers));
                        cameraController.shapeRenderer.circle(tower.centerGraphicCoord.x, tower.centerGraphicCoord.y, tower.radiusDetectionCircle.radius);
                    }
                }
            }
        }

        cameraController.shapeRenderer.setColor(Color.FIREBRICK);
        for (Tower tower : towersManager.towers) { // Draw FIREBRICK towers radius! -- radiusFlyShellCircle
            if (tower.radiusFlyShellCircle != null) {
                if(cameraController.isDrawableGridNav == 5) {
                    if(cameraController.isDrawableTowers == 5) {
                        for (int m = 1; m <= cameraController.isDrawableTowers; m++) {
//                            towerPos.set(cameraController.getCenterGraphicCoord(tower.cell.x, tower.cell.y, m)); // Need recoding this func!
                            cameraController.shapeRenderer.circle(tower.centerGraphicCoord.x, tower.centerGraphicCoord.y, tower.radiusFlyShellCircle.radius);
                        }
                    } else {
//                        towerPos.set(cameraController.getCenterGraphicCoord(tower.cell.x, tower.cell.y, cameraController.isDrawableTowers));
                        cameraController.shapeRenderer.circle(tower.centerGraphicCoord.x, tower.centerGraphicCoord.y, tower.radiusFlyShellCircle.radius);
                    }
                } else {
                    if(cameraController.isDrawableGridNav == cameraController.isDrawableTowers) {
//                        towerPos.set(cameraController.getCenterGraphicCoord(tower.cell.x, tower.cell.y, cameraController.isDrawableTowers));
                        cameraController.shapeRenderer.circle(tower.centerGraphicCoord.x, tower.centerGraphicCoord.y, tower.radiusFlyShellCircle.radius);
                    }
                }
            }
        }
        cameraController.shapeRenderer.end();

        cameraController.spriteBatch.begin();
        cameraController.bitmapFont.setColor(Color.YELLOW);
        cameraController.bitmapFont.getData().setScale(0.7f);
        for (Tower tower : towersManager.towers) { // Draw pit capacity value
            if (tower.templateForTower.towerAttackType == TowerAttackType.Pit) {
                if(cameraController.isDrawableGridNav == 5) {
                    if(cameraController.isDrawableTowers == 5) {
                        for (int m = 1; m <= cameraController.isDrawableTowers; m++) {
//                            towerPos.set(cameraController.getCenterGraphicCoord(tower.cell.x, tower.cell.y, m)); // Need recoding this func!
                            cameraController.bitmapFont.draw(cameraController.spriteBatch, String.valueOf(tower.capacity), tower.centerGraphicCoord.x, tower.centerGraphicCoord.y);
                        }
                    } else {
//                        towerPos.set(cameraController.getCenterGraphicCoord(tower.cell.x, tower.cell.y, cameraController.isDrawableTowers));
                        cameraController.bitmapFont.draw(cameraController.spriteBatch, String.valueOf(tower.capacity), tower.centerGraphicCoord.x, tower.centerGraphicCoord.y);
                    }
                } else {
                    if(cameraController.isDrawableGridNav == cameraController.isDrawableTowers) {
//                        towerPos.set(cameraController.getCenterGraphicCoord(tower.cell.x, tower.cell.y, cameraController.isDrawableTowers));
                        cameraController.bitmapFont.draw(cameraController.spriteBatch, String.valueOf(tower.capacity), tower.centerGraphicCoord.x, tower.centerGraphicCoord.y);
                    }
                }
            }
        }
        cameraController.spriteBatch.end();
    }

    private void drawShells(CameraController cameraController) {
        for (Tower tower : towersManager.towers) {
            for (Bullet bullet : tower.bullets) {
                TextureRegion textureRegion = bullet.textureRegion;
//                float width = textureRegion.getRegionWidth() * bullet.ammoSize;
//                float height = textureRegion.getRegionHeight() * bullet.ammoSize;
//                spriteBatch.draw(textureRegion, bullet.currentPoint.x, bullet.currentPoint.y, width, height);
                cameraController.spriteBatch.draw(textureRegion, bullet.currentPoint.x - bullet.circle.radius, bullet.currentPoint.y - bullet.circle.radius, bullet.circle.radius * 2, bullet.circle.radius * 2);
//                Gdx.app.log("GameField", "drawProjecTiles(); -- Draw bullet:" + bullet.currentPoint);
            }
        }
    }

    private void drawTowersUnderConstruction(CameraController cameraController) {
        if (underConstruction != null) {
            int goldNeed = underConstruction.templateForTower.cost;
            boolean enoughGold = (gamerGold >= goldNeed) ? true : false;
            if (underConstruction.state == 0) {
                drawTowerUnderConstruction(cameraController, underConstruction.endX, underConstruction.endY, underConstruction.templateForTower, enoughGold);
            } else if (underConstruction.state == 1) {
                drawTowerUnderConstruction(cameraController, underConstruction.startX, underConstruction.startY, underConstruction.templateForTower, enoughGold);
                for (int k = 0; k < underConstruction.coorsX.size; k++) {
                    goldNeed += underConstruction.templateForTower.cost;
                    enoughGold = (gamerGold >= goldNeed) ? true : false;
                    drawTowerUnderConstruction(cameraController, underConstruction.coorsX.get(k), underConstruction.coorsY.get(k), underConstruction.templateForTower, enoughGold);
                }
            }
        }
    }

    private void drawTowerUnderConstruction(CameraController cameraController, int buildX, int buildY, TemplateForTower templateForTower, boolean enoughGold) {
//        Gdx.app.log("GameField::drawTowerUnderConstruction()", "-- buildX:" + buildX + " buildY:" + buildY /*+ " templateForTower:" + templateForTower*/ + " enoughGold:" + enoughGold);
        boolean drawFull = true;
        boolean canBuild = true;
        int towerSize = templateForTower.size;
        int startX = 0, startY = 0, finishX = 0, finishY = 0;
        if (towerSize != 1) {
            // Нижняя карта
            if (towerSize % 2 == 0) {
                startX = -(towerSize / 2);
                startY = -(towerSize / 2);
                finishX = (towerSize / 2)-1;
                finishY = (towerSize / 2)-1;
            } else {
                startX = -(towerSize / 2);
                startY = -(towerSize / 2);
                finishX = (towerSize / 2);
                finishY = (towerSize / 2);
            }
            // Правая карта
//            if (towerSize % 2 == 0) {
//                startX = -(towerSize / 2);
//                startY = -((towerSize / 2) - 1);
//                finishX = ((towerSize / 2) - 1);
//                finishY = (towerSize / 2);
//            } else {
//                startX = -(towerSize / 2);
//                startY = -(towerSize / 2);
//                finishX = (towerSize / 2);
//                finishY = (towerSize / 2);
//            }
        }
        GridPoint2 startDrawCell = new GridPoint2(startX, startY);
        GridPoint2 finishDrawCell = new GridPoint2(finishX, finishY);
        for (int x = startX; x <= finishX; x++) {
            for (int y = startY; y <= finishY; y++) {
                Cell mainCell = getCell(buildX + x, buildY + y);
                if(mainCell == null || !mainCell.isEmpty()) {
                    if (drawFull) {
                        canBuild = false;
                    }
                }
            }
        }
        if (drawFull) {
            Cell mainCell = getCell(buildX, buildY);
            if(mainCell != null) {
                Color oldColorSB = cameraController.spriteBatch.getColor();
                Color oldColorSR = cameraController.shapeRenderer.getColor();
                if (enoughGold && canBuild) {
                    cameraController.spriteBatch.setColor(0, 1f, 0, 0.55f);
                    cameraController.shapeRenderer.setColor(0, 1f, 0, 0.55f);
                } else {
                    cameraController.spriteBatch.setColor(1f, 0, 0, 0.55f);
                    cameraController.shapeRenderer.setColor(1f, 0, 0, 0.55f);
                }
                if (cameraController.isDrawableTowers == 5) {
                    for (int map = 1; map < cameraController.isDrawableTowers; map++) {
                        drawTowerUnderConstructionAndMarks(cameraController, map, templateForTower, mainCell, startDrawCell, finishDrawCell);
                    }
                } else if (cameraController.isDrawableTowers != 0) {
                    drawTowerUnderConstructionAndMarks(cameraController, cameraController.isDrawableTowers, templateForTower, mainCell, startDrawCell, finishDrawCell);
                }
                cameraController.spriteBatch.setColor(oldColorSB);
                cameraController.shapeRenderer.setColor(oldColorSR);
            }
        }
    }

    private void drawTowerUnderConstructionAndMarks(CameraController cameraController, int map, TemplateForTower templateForTower, Cell mainCell, GridPoint2 startDrawCell, GridPoint2 finishDrawCell) {
//        Gdx.app.log("GameField::drawTowerUnderConstructionAndMarks()", "-- spriteBatch:" + /*spriteBatch +*/ " shapeRenderer:" + /*shapeRenderer +*/ " map:" + map + " templateForTower:" + templateForTower + " mainCell:" + mainCell + " startDrawCell:" + startDrawCell + " finishDrawCell:" + finishDrawCell);
        TextureRegion textureRegion = templateForTower.idleTile.getTextureRegion();
        int towerSize = templateForTower.size;
        Vector2 towerPos = new Vector2(mainCell.getGraphicCoordinates(map));
        if (templateForTower.radiusDetection != null) {
            cameraController.shapeRenderer.circle(towerPos.x, towerPos.y, templateForTower.radiusDetection);
        }
        cameraController.getCorrectGraphicTowerCoord(towerPos, towerSize, map);
        cameraController.spriteBatch.draw(textureRegion, towerPos.x, towerPos.y, cameraController.sizeCellX * towerSize, (cameraController.sizeCellY * 2) * towerSize);
        cameraController.shapeRenderer.circle(towerPos.x, towerPos.y, templateForTower.radiusDetection/4);
        if (greenCheckmark != null && redCross != null) {
            Vector2 markPos = new Vector2();
            for (int x = startDrawCell.x; x <= finishDrawCell.x; x++) {
                for (int y = startDrawCell.y; y <= finishDrawCell.y; y++) {
                    Cell markCell = getCell(mainCell.cellX + x, mainCell.cellY + y);
                    if (markCell != null) {
                        markPos.set(markCell.getGraphicCoordinates(map));
                        markPos.add(-(cameraController.halfSizeCellX), -(cameraController.halfSizeCellY));
                        if(markCell.isEmpty()) {
                            cameraController.spriteBatch.draw(greenCheckmark, markPos.x, markPos.y, cameraController.sizeCellX, cameraController.sizeCellY * 2);
                        } else {
                            cameraController.spriteBatch.draw(redCross, markPos.x, markPos.y, cameraController.sizeCellX, cameraController.sizeCellY * 2);
                        }
                    }
                }
            }
            markPos = null; // delete markPos;
        }
        towerPos = null; // delete towerPos;
    }

    public void setExitPoint(int x, int y) {
        waveManager.setExitPoint(new GridPoint2(x, y));
        rerouteForAllUnits(new GridPoint2(x, y));
    }

    public void spawnUnitFromUser(TemplateForUnit templateForUnit) {
        Gdx.app.log("GameField::spawnUnitFromUser()", "-- templateForUnit:" + templateForUnit);
        if (gamerGold >= templateForUnit.cost) {
            gamerGold -= templateForUnit.cost;
            for (Wave wave : waveManager.wavesForUser) {
                Cell spawnCell = getCell(wave.spawnPoint.x, wave.spawnPoint.y);
                Cell destExitCell = getCell(wave.exitPoint.x, wave.exitPoint.y);
                createUnit(spawnCell, destExitCell, templateForUnit, 1, destExitCell); // create Player1 Unit
            }
        }
    }

    private void spawnUnits(float delta) {
        Array<WaveManager.TemplateNameAndPoints> allUnitsForSpawn = waveManager.getAllUnitsForSpawn(delta);
        for (WaveManager.TemplateNameAndPoints templateNameAndPoints : allUnitsForSpawn) {
            spawnUnit(templateNameAndPoints);
        }
    }

    private void spawnUnit(WaveManager.TemplateNameAndPoints templateNameAndPoints) {
        if (templateNameAndPoints != null) {
            TemplateForUnit templateForUnit = factionsManager.getTemplateForUnitByName(templateNameAndPoints.templateName);
            if (templateForUnit != null) {
                Cell spawnCell = getCell(templateNameAndPoints.spawnPoint.x, templateNameAndPoints.spawnPoint.y);
                Cell destExitCell = getCell(templateNameAndPoints.exitPoint.x, templateNameAndPoints.exitPoint.y);
                createUnit(spawnCell, destExitCell, templateForUnit, 0, destExitCell); // create Computer0 Unit
            } else {
                Gdx.app.error("GameField::spawnUnit()", "-- templateForUnit == null | templateName:" + templateNameAndPoints.templateName);
            }
        }
    }

    private void spawnHeroInSpawnPoint() {
        gameSettings.cellSpawnHero.removeTerrain(true);
        removeTower(gameSettings.cellSpawnHero.cellX, gameSettings.cellSpawnHero.cellY);
        createUnit(gameSettings.cellSpawnHero.cellX, gameSettings.cellSpawnHero.cellY, gameSettings.cellExitHero.cellX, gameSettings.cellExitHero.cellY, 1); // player1 = hero
    }

    void spawnCompUnitToRandomExit(int x, int y) {
        Gdx.app.log("GameField::spawnCompUnitToRandomExit()", "-- x:" + x + " y:" + y);
        int randomX = (int)(Math.random()*map.width);
        int randomY = (int)(Math.random()*map.height);
        Gdx.app.log("GameField::spawnCompUnitToRandomExit()", "-- randomX:" + randomX + " randomY:" + randomY);
        createUnit(x, y, randomX, randomY, 0);
    }

    public void createUnit(int x, int y) {
        Cell spawnCell = getCell(x, y);
        Cell destExitCell = getCell(waveManager.lastExitPoint.x, waveManager.lastExitPoint.y);
        createUnit(spawnCell, destExitCell, factionsManager.getRandomTemplateForUnitFromFirstFaction(), 0, destExitCell); // create computer0 Unit
    }

    public void createUnit(int x, int y, int x2, int y2, int player) {
        if (player == 0) {
            createUnit(getCell(x, y), getCell(x2, y2), factionsManager.getRandomTemplateForUnitFromFirstFaction(), player, null);
        } else if (player == 1) {
            createUnit(getCell(x, y), getCell(x2, y2), factionsManager.getTemplateForUnitByName("unit3_footman"), player, gameSettings.cellExitHero);
//        updateHeroDestinationPoint(exitPointX, exitPointY);
        }
    }

    private void createUnit(Cell spawnCell, Cell destCell, TemplateForUnit templateForUnit, int player, Cell exitCell) {
//        Gdx.app.log("GameField::createUnit(" + spawnCell + ", " + templateForUnit.toString(true) + ", " + destCell + ", " + player + ")", "--");
//        if (destCell == null) {
//            destCell = waveManager.lastExitCell;
//        }
        if (spawnCell != null && destCell != null && pathFinder != null) {
//            pathFinder.loadCharMatrix(getCharMatrix());
            ArrayDeque<Node> route = pathFinder.route(spawnCell.cellX, spawnCell.cellY, destCell.cellX, destCell.cellY);
            if (route != null) {
                Unit unit = unitsManager.createUnit(route, templateForUnit, player, exitCell);
                field[spawnCell.cellX][spawnCell.cellY].setUnit(unit); // TODO field maybe out array | NO, we have WaveManager.validationPoints()
//                Gdx.app.log("GameField::createUnit()", "-- route:" + route);
            } else {
                Gdx.app.log("GameField::createUnit()", "-- Not found route for createUnit!");
                if(towersManager.towers.size > 0) {
                    Gdx.app.log("GameField::createUnit()", "-- Remove one last tower! And retry call createUnit()");
                    removeLastTower();
                    createUnit(spawnCell, destCell, templateForUnit, player, exitCell);
                }
            }
        } else {
            Gdx.app.log("GameField::createUnit()", "-- Bad spawnCell:" + spawnCell + " || destCell:" + destCell + " || pathFinder:" + pathFinder);
        }
    }

    public void buildTowersWithUnderConstruction(int x, int y) {
        if (underConstruction != null) {
            underConstruction.setEndCoors(x, y);
            createTower(underConstruction.startX, underConstruction.startY, underConstruction.templateForTower, 1);
            for (int k = 0; k < underConstruction.coorsX.size; k++) {
//            for(int k = underConstruction.coorsX.size-1; k >= 0; k--) {
                createTower(underConstruction.coorsX.get(k), underConstruction.coorsY.get(k), underConstruction.templateForTower, 1);
            }
            underConstruction.clearStartCoors();
            rerouteForAllUnits();
        }
    }

    public void towerActions(int x, int y) {
        if (field[x][y].isEmpty()) {
            createTower(x, y, factionsManager.getRandomTemplateForTowerFromAllFaction(), 1);
            rerouteForAllUnits();
        } else if (field[x][y].getTower() != null) {
            removeTower(x, y);
        }
    }

    public boolean createTower(int buildX, int buildY, TemplateForTower templateForTower, int player) {
        if (gamerGold >= templateForTower.cost) {
            int towerSize = templateForTower.size;
            int startX = 0, startY = 0, finishX = 0, finishY = 0;
            if (towerSize != 1) {
                // Нижняя карта
                if (towerSize % 2 == 0) {
                    startX = -(towerSize / 2);
                    startY = -(towerSize / 2);
                    finishX = (towerSize / 2)-1;
                    finishY = (towerSize / 2)-1;
                } else {
                    startX = -(towerSize / 2);
                    startY = -(towerSize / 2);
                    finishX = (towerSize / 2);
                    finishY = (towerSize / 2);
                }
                // Правая карта
//                if (towerSize % 2 == 0) {
//                    startX = -(towerSize / 2);
//                    startY = -((towerSize / 2) - 1);
//                    finishX = ((towerSize / 2) - 1);
//                    finishY = (towerSize / 2);
//                } else {
//                    startX = -(towerSize / 2);
//                    startY = -(towerSize / 2);
//                    finishX = (towerSize / 2);
//                    finishY = (towerSize / 2);
//                }
            }
            for (int tmpX = startX; tmpX <= finishX; tmpX++) {
                for (int tmpY = startY; tmpY <= finishY; tmpY++) {
                    Cell cell = getCell(buildX + tmpX, buildY + tmpY);
                    if (cell == null || !cell.isEmpty()) {
                        return false;
                    }
                }
            }

            Cell cell = getCell(buildX, buildY);
            Tower tower = towersManager.createTower(cell, templateForTower, player);
            Gdx.app.log("GameField::createTower()", "-- templateForTower.towerAttackType:" + templateForTower.towerAttackType);
            if (templateForTower.towerAttackType != TowerAttackType.Pit) {
                for (int tmpX = startX; tmpX <= finishX; tmpX++) {
                    for (int tmpY = startY; tmpY <= finishY; tmpY++) {
                        field[buildX + tmpX][buildY + tmpY].setTower(tower);
                        pathFinder.nodeMatrix[buildY + tmpY][buildX + tmpX].setKey('T');
                    }
                }
            }

//            rerouteForAllUnits();
            gamerGold -= templateForTower.cost;
            Gdx.app.log("GameField::createTower()", "-- Now gamerGold:" + gamerGold);
            return true;
        } else {
            return false;
        }
    }

    public void removeLastTower() {
//        if(towersManager.amountTowers() > 0) {
            Tower tower = towersManager.getTower(towersManager.towers.size - 1);
            Cell pos = tower.cell;
            removeTower(pos.cellX, pos.cellY);
//        }
    }

    public void removeTower(int touchX, int touchY) {
        Tower tower = field[touchX][touchY].getTower();
        if (tower != null) {
            int x = tower.cell.cellX;
            int y = tower.cell.cellY;
            int towerSize = tower.templateForTower.size;
            int startX = 0, startY = 0, finishX = 0, finishY = 0;
            if (towerSize != 1) {
                if (towerSize % 2 == 0) {
                    startX = -(towerSize / 2);
                    startY = -(towerSize / 2);
                    finishX = (towerSize / 2)-1;
                    finishY = (towerSize / 2)-1;
                } else {
                    startX = -(towerSize / 2);
                    startY = -(towerSize / 2);
                    finishX = towerSize / 2;
                    finishY = towerSize / 2;
                }
            }

            for (int tmpX = startX; tmpX <= finishX; tmpX++) {
                for (int tmpY = startY; tmpY <= finishY; tmpY++) {
                    field[x + tmpX][y + tmpY].removeTower();
                    pathFinder.getNodeMatrix()[y + tmpY][x + tmpX].setKey('.');
                }
            }
            towersManager.removeTower(tower);
            rerouteForAllUnits();
            gamerGold += tower.templateForTower.cost;//*0.5;
        }
    }

    private void rerouteForAllUnits() {
        rerouteForAllUnits(null);
    }

    private void rerouteForAllUnits(GridPoint2 exitPoint) {
        if (pathFinder != null) {
            long start = System.nanoTime();
            Gdx.app.log("GameField::rerouteForAllUnits()", "-- Start:" + start);
//            pathFinder.loadCharMatrix(getCharMatrix());
            for (Unit unit : unitsManager.units) {
                ArrayDeque<Node> route;
                if (exitPoint == null) {
                    route = unit.route;
                    if(route != null && route.size() > 0) {
                        Node node = unit.route.getLast();
                        GridPoint2 localExitPoint = new GridPoint2(node.getX(), node.getY());
                        route = pathFinder.route(unit.newPosition.getX(), unit.newPosition.getY(), localExitPoint.x, localExitPoint.y); // TODO BAGA!
                    }
                } else {
                    route = pathFinder.route(unit.newPosition.getX(), unit.newPosition.getY(), exitPoint.x, exitPoint.y); // TODO BAGA!
                }
                if (route != null && route.size() > 0) {
                    route.removeFirst();
                    unit.route = route;
                }
//                    long end2 = System.nanoTime();
//                    Gdx.app.log("GameField", "rerouteForAllUnits(); -- Thread End:" + (end2-start2));
//                }
//            }.init(unit, outExitPoint)).start();
            }
            long end = System.nanoTime();
            Gdx.app.log("GameField::rerouteForAllUnits()", "-- End:" + end + " Delta time:" + (end-start));
        } else {
            Gdx.app.log("GameField::rerouteForAllUnits(" + exitPoint + ")", "-- pathFinder:" + pathFinder);
        }
    }

    private void stepAllUnits(float delta, CameraController cameraController) {
//        Gdx.app.log("GameField::stepAllUnits()", "-- unitsManager.units:" + unitsManager.units.size);
        for (Unit unit : unitsManager.units) {
            Node oldPosition = unit.newPosition;
            if (unit.isAlive()) {
                Node newPosition = unit.move(delta, cameraController);
                if (newPosition != null) {
                    if (!newPosition.equals(oldPosition)) {
                        field[oldPosition.getX()][oldPosition.getY()].removeUnit(unit);
                        field[newPosition.getX()][newPosition.getY()].setUnit(unit);
//                    Gdx.app.log("GameField::stepAllUnits()", "-- Unit move to X:" + newPosition.getX() + " Y:" + newPosition.getY());
                    }
                } else {
                    Cell cell = getCell(oldPosition.getX(), oldPosition.getY());
//                    Gdx.app.log("GameField::stepAllUnits()", "-- cell:" + cell.toString());
                    Gdx.app.log("GameField::stepAllUnits()", "-- Unit finished!");
                    if (unit.player == 1) {
                        gameSettings.missedUnitsForComputer0++;
                    } else if (unit.player == 0) {
                        if (unit.exitCell == cell) {
                            gameSettings.missedUnitsForPlayer1++;
                            cell.removeUnit(unit);
                            unitsManager.removeUnit(unit);
                            Gdx.app.log("GameField::stepAllUnits()", "-- unitsManager.removeUnit(unit):");
                        } else {
                            if (unit.route == null || unit.route.isEmpty()) {
                                int randomX = (int)(Math.random()*map.width);
                                int randomY = (int)(Math.random()*map.height);
                                unit.route = pathFinder.route(oldPosition.getX(), oldPosition.getY(), randomX, randomY);
                                unit.route.removeFirst();
                                Gdx.app.log("GameField::stepAllUnits()", "-- new unit.route:" + unit.route);
                            }
                        }
                    }
                }
            } else {
//            if (!unit.isAlive()) {
                if (!unit.changeDeathFrame(delta)) {
                    field[oldPosition.getX()][oldPosition.getY()].removeUnit(unit);
//                    GameField.gamerGold += unit.templateForUnit.bounty;
                    unit.dispose();
                    unitsManager.removeUnit(unit);
                    Gdx.app.log("GameField::stepAllUnits()", "-- Unit death! and delete!");
                }
            }
//            Gdx.app.log("GameField::stepAllUnits()", "-- Unit:" + unit.toString());
        }
    }

    private void shotAllTowers(float delta, CameraController cameraController) {
        updateTowersGraphicCoordinates(cameraController);
        for (Tower tower : towersManager.towers) {
//            Gdx.app.log("GameField::shotAllTowers()", "-- tower.toString():" + tower.toString());
            TowerAttackType towerAttackType = tower.templateForTower.towerAttackType;
            if (towerAttackType == TowerAttackType.Pit) {
                Unit unit = field[tower.cell.cellX][tower.cell.cellY].getUnit();
                if (unit != null && !unit.templateForUnit.type.equals("fly") && unit.player != tower.player) {
                    Gdx.app.log("GameField", "shotAllTowers(); -- tower.capacity:" + tower.capacity + " unit.getHp:" + unit.hp);
//                    unit.die(unit.getHp());
                    unitsManager.removeUnit(unit);
                    field[tower.cell.cellX][tower.cell.cellY].removeUnit(unit);
                    tower.capacity--;
                    if (tower.capacity <= 0) {
                        towersManager.removeTower(tower);
                    }
                }
//                Gdx.app.log("GameField::shotAllTowers(" + delta + ")", "-- towerAttackType.pit -- unit:" + unit);
            } else if (towerAttackType == TowerAttackType.Melee) {
                shotMeleeTower(tower);
            } else if (towerAttackType == TowerAttackType.Range || towerAttackType == TowerAttackType.RangeFly) {
                if (tower.recharge(delta)) {
                    for (Unit unit : unitsManager.units) {
                        if (unit != null && unit.isAlive() && unit.player != tower.player) {
                            if ( (unit.templateForUnit.type.equals("fly") && towerAttackType == TowerAttackType.RangeFly) ||
                                    (!unit.templateForUnit.type.equals("fly") && towerAttackType == TowerAttackType.Range)) { // Тупо но работает, потом переделать need =)
                                Gdx.app.log("GameField::shotAllTowers()", "-- tower.radiusDetectionCircle:" + tower.radiusDetectionCircle);
                                Gdx.app.log("GameField::shotAllTowers()", "-- unit.circle1:" + unit.circle1);
//                                Gdx.app.log("GameField::shotAllTowers()", "-- unit.circle2:" + unit.circle2);
//                                Gdx.app.log("GameField::shotAllTowers()", "-- unit.circle3:" + unit.circle3);
//                                Gdx.app.log("GameField::shotAllTowers()", "-- unit.circle4:" + unit.circle4);
                                if (Intersector.overlaps(unit.circle1, tower.radiusDetectionCircle)) {
                                    Gdx.app.log("GameField::shotAllTowers()", "-- overlaps");
                                    if (tower.shoot(unit, cameraController)) {
                                        if(tower.templateForTower.shellAttackType != ShellAttackType.MassAddEffect) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
//                        Gdx.app.log("GameField::shotAllTowers()", "-- unit.toString():" + unit.toString());
                    }
                }
            } else if (towerAttackType == TowerAttackType.FireBall) {
                if (tower.recharge(delta)) {
//                    fireBallTowerAttack(delta, tower);
//                    tower.shoot();
                }
            }
        }
    }

    private boolean shotMeleeTower(Tower tower) {
        boolean attack = false;
        int radius = Math.round(tower.templateForTower.radiusDetection);
        for (int tmpX = -radius; tmpX <= radius; tmpX++) {
            for (int tmpY = -radius; tmpY <= radius; tmpY++) {
                Cell towerCell = tower.cell;
                Cell cell = getCell(tmpX + towerCell.cellX, tmpY + towerCell.cellY);
                if (cell != null && cell.getUnit() != null) {
                    attack = true;
                    Unit unit = cell.getUnit();
                    if (unit != null && !unit.templateForUnit.type.equals("fly") && unit.player != tower.player) {
                        if (unit.die(tower.templateForTower.damage, tower.templateForTower.shellEffectType)) {
                            gamerGold += unit.templateForUnit.bounty;
                        }
                        if (tower.templateForTower.shellAttackType == ShellAttackType.SingleTarget) {
                            return true;
                        }
                    }
                }
            }
        }
        return attack;
    }

//    boolean fireBallTowerAttack(float deltaTime, Tower fireBallTower) {
//            if (fireBallTower.recharge(deltaTime)) {
//    //            fireBallTower.createBullets(towersManager->difficultyLevel);
//            }
//            for (int b = 0; b < fireBallTower.bullets.size; b++) {
//                Bullet tmpBullet = fireBallTower.bullets.get(b);
//                int currX = tmpBullet.currCellX;
//                int currY = tmpBullet.currCellY;
//                if (currX < 0 || currX >= map->width || currY < 0 || currY >= map->height) {
//                    fireBallTower.bullets.erase(fireBallTower.bullets.begin()+b);
//                    delete tmpBullet;
//                    b--;
//                } else {
//                    if (getCell(currX, currY)->getHero() != NULL) {
//                        unitsManager->attackUnit(currX, currY, 9999);//, getCell(currX, currY)->getHero()); // Magic number 9999
//                    }
//                }
//                if(tmpBullet->animationCurrIter < tmpBullet->animationMaxIter) {
//                    tmpBullet->pixmap = tmpBullet->activePixmaps[tmpBullet->animationCurrIter++];
//                } else {
//                    int exitX = currX, exitY = currY;
//                    if (tmpBullet->direction == Direction::type::UP) {
//                        exitX = currX-1;
//                        exitY = currY-1;
//                    } else if (tmpBullet->direction == Direction::UP_RIGHT) {
//                        exitX = currX;
//                        exitY = currY-1;
//                    } else if (tmpBullet->direction == Direction::RIGHT) {
//                        exitX = currX+1;
//                        exitY = currY-1;
//                    } else if (tmpBullet->direction == Direction::UP_LEFT) {
//                        exitX = currX-1;
//                        exitY = currY;
//                    } else if (tmpBullet->direction == Direction::DOWN_RIGHT) {
//                        exitX = currX+1;
//                        exitY = currY;
//                    } else if (tmpBullet->direction == Direction::LEFT) {
//                        exitX = currX-1;
//                        exitY = currY+1;
//                    } else if (tmpBullet->direction == Direction::DOWN_LEFT) {
//                        exitX = currX;
//                        exitY = currY+1;
//                    } else if (tmpBullet->direction == Direction::DOWN) {
//                        exitX = currX+1;
//                        exitY = currY+1;
//                    }
//                    if(exitX != currX || exitY != currY) {
//                        tmpBullet->lastCellX = currX;
//                        tmpBullet->lastCellY = currY;
//                        tmpBullet->currCellX = exitX;
//                        tmpBullet->currCellY = exitY;
//                        if(isometric) {
//                            if(exitX < currX && exitY < currY) {
//                                tmpBullet->animationMaxIter = tmpBullet->defTower->bullet_fly_up.size();
//                                tmpBullet->activePixmaps = tmpBullet->defTower->bullet_fly_up;
//                                tmpBullet->direction = Direction::UP;
//                            } else if(exitX == currX && exitY < currY) {
//                                tmpBullet->animationMaxIter = tmpBullet->defTower->bullet_fly_up_right.size();
//                                tmpBullet->activePixmaps = tmpBullet->defTower->bullet_fly_up_right;
//                                tmpBullet->direction = Direction::UP_RIGHT;
//                            } else if(exitX > currX && exitY < currY) {
//                                tmpBullet->animationMaxIter = tmpBullet->defTower->bullet_fly_right.size();
//                                tmpBullet->activePixmaps = tmpBullet->defTower->bullet_fly_right;
//                                tmpBullet->direction = Direction::RIGHT;
//                            } else if(exitX < currX && exitY == currY) {
//                                tmpBullet->animationMaxIter = tmpBullet->defTower->bullet_fly_up_left.size();
//                                tmpBullet->activePixmaps = tmpBullet->defTower->bullet_fly_up_left;
//                                tmpBullet->direction = Direction::UP_LEFT;
//                            } else if(exitX > currX && exitY == currY) {
//                                tmpBullet->animationMaxIter = tmpBullet->defTower->bullet_fly_down_right.size();
//                                tmpBullet->activePixmaps = tmpBullet->defTower->bullet_fly_down_right;
//                                tmpBullet->direction = Direction::DOWN_RIGHT;
//                            } else if(exitX < currX && exitY > currY) {
//                                tmpBullet->animationMaxIter = tmpBullet->defTower->bullet_fly_left.size();
//                                tmpBullet->activePixmaps = tmpBullet->defTower->bullet_fly_left;
//                                tmpBullet->direction = Direction::LEFT;
//                            } else if(exitX == currX && exitY > currY) {
//                                tmpBullet->animationMaxIter = tmpBullet->defTower->bullet_fly_down_left.size();
//                                tmpBullet->activePixmaps = tmpBullet->defTower->bullet_fly_down_left;
//                                tmpBullet->direction = Direction::DOWN_LEFT;
//                            } else if(exitX > currX && exitY > currY) {
//                                tmpBullet->animationMaxIter = tmpBullet->defTower->bullet_fly_down.size();
//                                tmpBullet->activePixmaps = tmpBullet->defTower->bullet_fly_down;
//                                tmpBullet->direction = Direction::DOWN;
//                            }
//                        }
//                        if (tmpBullet->activePixmaps.empty() && !tmpBullet->defTower->bullet.empty()) {
//                            tmpBullet->animationMaxIter = tmpBullet->defTower->bullet.size();
//                            tmpBullet->activePixmaps = tmpBullet->defTower->bullet;
//                        }
//                        tmpBullet->pixmap = tmpBullet->activePixmaps[0];
//                        tmpBullet->animationCurrIter = 0;
//                    }
//                }
//            }
//        }
//        return true;
//    }

    private void moveAllShells(float delta) {
        for (Tower tower : towersManager.towers) {
            tower.moveAllShells(delta);
        }
    }

//    public Array<TemplateForTower> getFirstTemplateForTowers() {
//        factionsManager.ge
//    }

    // GAME INTERFACE ZONE1
//    public GridPoint2 whichCell(GridPoint2 grafCoordinate) {
//        return whichCell.whichCell(grafCoordinate);
//    }

//    public WhichCell getWhichCell() {
//        return whichCell;
//    }

    public void setGamePause(boolean gamePaused) {
        this.gamePaused = gamePaused;
    }

    public boolean getGamePaused() {
        return gamePaused;
    }

    public int getNumberOfUnits() {
        return waveManager.getNumberOfActions() + unitsManager.units.size;
    }

    public String getGameState() {
//        Gdx.app.log("GameField::getGameState()", "-- missedUnitsForPlayer1:" + missedUnitsForPlayer1);
//        Gdx.app.log("GameField::getGameState()", "-- maxOfMissedUnitsForPlayer1:" + maxOfMissedUnitsForPlayer1);
//        Gdx.app.log("GameField::getGameState()", "-- missedUnitsForComputer0:" + missedUnitsForComputer0);
//        Gdx.app.log("GameField::getGameState()", "-- maxOfMissedUnitsForComputer0:" + maxOfMissedUnitsForComputer0);
//        Gdx.app.log("GameField::getGameState()", "-- waveManager.getNumberOfActions():" + waveManager.getNumberOfActions());
//        Gdx.app.log("GameField::getGameState()", "-- unitsManager.units.size:" + unitsManager.units.size);
        if (gameSettings.missedUnitsForPlayer1 >= gameSettings.maxOfMissedUnitsForPlayer1) {
//            Gdx.app.log("GameField::getGameState()", "-- LOSE!!");
            return "Lose";
        } else {
            if(gameSettings.missedUnitsForComputer0 >= gameSettings.maxOfMissedUnitsForComputer0) { // При инициализации если в карте не было голды игроку. и у игрока изначально было 0 голды. то он сразу же выиграет
//                Gdx.app.log("GameField::getGameState()", "-- WIN!!");
                return "Win";
            }
            if (waveManager.getNumberOfActions() == 0 && unitsManager.units.size == 0) {
//                Gdx.app.log("GameField::getGameState()", "-- WIN!!");
                return "Win";
            }
        }
//        Gdx.app.log("GameField::getGameState()", "-- IN PROGRESS!!");
        return "In progress";
    }

    public int getGamerGold() {
        return gamerGold;
    }

    public Array<TemplateForTower> getAllFirstTowersFromFirstFaction() {
        return factionsManager.getAllFirstTowersFromFirstFaction();
    }

    public Array<TemplateForTower> getAllTemplateForTowers() {
        return factionsManager.getAllTemplateForTowers();
    }

    public Array<TemplateForUnit> getAllTemplateForUnits() {
        return factionsManager.getAllTemplateForUnits();
    }

    public UnderConstruction createdRandomUnderConstruction() {
        return createdUnderConstruction(factionsManager.getRandomTemplateForTowerFromAllFaction());
    }

    public UnderConstruction createdUnderConstruction(TemplateForTower templateForTower) {
        if (underConstruction != null) {
            underConstruction.dispose();
        }
        return underConstruction = new UnderConstruction(templateForTower);
    }

    public boolean cancelUnderConstruction() {
        if (underConstruction != null) {
            underConstruction.dispose();
            underConstruction = null;
            return true;
        }
        return false;
    }

    public UnderConstruction getUnderConstruction() {
        return underConstruction;
    }

    private Cell getCell(int x, int y) {
        if (x >= 0 && x < map.width) {
            if (y >= 0 && y < map.height) {
                return field[x][y];
            }
        }
        return null;
    }

    public void updateCellsGraphicCoordinates(float halfSizeCellX, float halfSizeCellY) {
        for (int cellX = 0; cellX < map.width; cellX++) {
            for (int cellY = 0; cellY < map.height; cellY++) {
                field[cellX][cellY].setGraphicCoordinates(cellX, cellY, halfSizeCellX, halfSizeCellY);
            }
        }
    }

    public void updateTowersGraphicCoordinates(CameraController cameraController) {
        for (Tower tower : towersManager.towers) {
            tower.updateCenterGraphicCoordinates(cameraController);
        }
    }
}
