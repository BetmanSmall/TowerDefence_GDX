package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.WhichCell;
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
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private SpriteBatch spriteBatch = new SpriteBatch();
    private BitmapFont bitmapFont = new BitmapFont();

    public FactionsManager factionsManager;
    public WaveManager waveManager; // ALL public for all || we are friendly :)
    public TowersManager towersManager;
    public static UnitsManager unitsManager; // For Bullet
    private TiledMap map;
    private Cell[][] field;
    private PathFinder pathFinder;

    // CameraController1 && map
    public int sizeFieldX, sizeFieldY;
    public static int sizeCellX, sizeCellY;
    private int halfSizeCellX, halfSizeCellY;
    // CameraController2
    public int isDrawableGrid = 1;
    public static int isDrawableUnits = 1; // Bad! need make not static!
    public static int isDrawableTowers = 1; // Bad! need make not static!
    public int isDrawableBackground = 1;
    public int isDrawableGround = 1;
    public int isDrawableForeground = 1;
    public int isDrawableGridNav = 1;
    public int isDrawableRoutes = 1;
    public int drawOrder = 8;
    private WhichCell whichCell;
    // CameraController3

    private UnderConstruction underConstruction;
    private Texture greenCheckmark;
    private Texture redCross;

    boolean isometric;
//    QPixmap global_pixmap;
//    QPixmap global_pixmap_PathPoint;
//    QPixmap global_pixmap_EnemyPathPoint;
//    QPixmap global_pixmap_DestinationPoint;
//    QPixmap global_pixmap_ExitPoint;

    // GAME INTERFACE ZONE1
//    float timeOfGame;
    public float gameSpeed;
    public boolean gamePaused;
    public static int gamerGold; // For Bullet
    // GAME INTERFACE ZONE2
    Cell cellSpawnHero;
    Cell cellExitHero;
    public int maxOfMissedUnitsForComputer0;
    public int missedUnitsForComputer0;
    public int maxOfMissedUnitsForPlayer1;
    public int missedUnitsForPlayer1;
    // GAME INTERFACE ZONE3

    public GameField(String mapName, FactionsManager factionsManager, float levelOfDifficulty) {
        Gdx.app.log("GameField::GameField(" + mapName + ", " + levelOfDifficulty + ")", "--");
        this.factionsManager = factionsManager;
        waveManager = new WaveManager();
        towersManager = new TowersManager();
        unitsManager = new UnitsManager();

        map = new MapLoader(waveManager).load(mapName);

        sizeFieldX = map.getProperties().get("width", Integer.class);
        sizeFieldY = map.getProperties().get("height", Integer.class);
        sizeCellX = map.getProperties().get("tilewidth", Integer.class);
        sizeCellY = map.getProperties().get("tileheight", Integer.class);
        halfSizeCellX = sizeCellX / 2;
        halfSizeCellY = sizeCellY / 2;

        underConstruction = null;
        greenCheckmark = new Texture(Gdx.files.internal("maps/textures/green_checkmark.png"));
        redCross = new Texture(Gdx.files.internal("maps/textures/red_cross.png"));
        if (greenCheckmark == null || redCross == null) {
            Gdx.app.error("GameField::GameField()", "-- Achtung fuck. NOT FOUND 'maps/textures/green_checkmark.png' & 'maps/textures/red_cross.png' YEBAK");
        }

        createField(sizeFieldX, sizeFieldY, map.getLayers());
        if (mapName.contains("randomMap")) {
            maxOfMissedUnitsForPlayer1 = 5;
            int randomEnemyCount = 5;
            for (int k = 0; k < randomEnemyCount; k++) {
                int randomX = (int)(Math.random()*sizeFieldX);
                int randomY = (int)(Math.random()*sizeFieldY);
                Gdx.app.log("GameField::GameField()", "-- randomX:" + randomX);
                Gdx.app.log("GameField::GameField()", "-- randomY:" + randomY);
                if (getCell(randomX, randomY).isEmpty()) {
                    spawnCompUnitToRandomExit(randomX, randomY);
                } else {
                    k--;
                }
            }

        } else {
            waveManager.validationPoints(field);
            if (waveManager.waves.size == 0) {
                for (int w = 0; w < 10; w++) {
                    GridPoint2 spawnCell = new GridPoint2((int) (Math.random() * sizeFieldX), (int) (Math.random() * sizeFieldY));
                    GridPoint2 exitCell = new GridPoint2((int) (Math.random() * sizeFieldX), (int) (Math.random() * sizeFieldY));
//                    if (spawnCell != null && spawnCell.isEmpty()) {
//                        if (exitCell != null && exitCell.isEmpty()) {
                            Wave wave = new Wave(spawnCell, exitCell, 0f);
                            for (int k = 0; k < 10; k++) {
                                wave.addAction("interval=" + 1);
                                wave.addAction(factionsManager.getRandomTemplateForUnitFromFirstFaction().templateName);
                            }
                            waveManager.addWave(wave);
//                        }
//                    }
                }

            }
            waveManager.checkRoutes(pathFinder);
        }

        MapProperties mapProperties = map.getProperties();
        Gdx.app.log("GameField::GameField()", "-- mapProperties:" + mapProperties);
        // GAME INTERFACE ZONE1
        whichCell = new WhichCell(sizeFieldX, sizeFieldY, sizeCellX, sizeCellY);
        gameSpeed = 1.0f;
        gamePaused = false;
//        gamerGold = Integer.valueOf(mapProperties.get("gamerGold", "10000", String.class)); // HARD GAME | one gold = one unit for computer!!!
        gamerGold = 100000;
        maxOfMissedUnitsForComputer0 = mapProperties.get("maxOfMissedUnitsForComputer0", gamerGold, Integer.class); // Игрок может сразу выиграть если у него не будет голды. так как @ref2
//        maxOfMissedUnitsForComputer0 = Integer.valueOf(mapProperties.get("maxOfMissedUnitsForComputer0", String.valueOf(gamerGold), String.class));
        missedUnitsForComputer0 = 0;
        if (maxOfMissedUnitsForPlayer1 == 0) {
            maxOfMissedUnitsForPlayer1 = mapProperties.get("maxOfMissedUnitsForPlayer1", waveManager.getNumberOfActions() / 8, Integer.class); // it is not true | need implement getNumberOfUnits()
//        maxOfMissedUnitsForPlayer1 = Integer.valueOf(mapProperties.get("maxOfMissedUnitsForPlayer1", String.valueOf(waveManager.getNumberOfActions()/4), String.class)); // it is not true | need implement getNumberOfUnits()
        }
        missedUnitsForPlayer1 = 0;
        // GAME INTERFACE ZONE2
        Gdx.app.log("GameField::GameField()", "-- gamerGold:" + gamerGold + " maxOfMissedUnitsForComputer0:" + maxOfMissedUnitsForComputer0 + " maxOfMissedUnitsForPlayer1:" + maxOfMissedUnitsForPlayer1);
    }

    private void createField(int sizeFieldX, int sizeFieldY, MapLayers mapLayers) {
        Gdx.app.log("GameField::createField(" + sizeFieldX + "," + sizeFieldY + "," + mapLayers + ")", "-- field:" + field);
        if (field == null) {
            field = new Cell[sizeFieldX][sizeFieldY];
            for (int y = 0; y < sizeFieldY; y++) {
                for (int x = 0; x < sizeFieldX; x++) {
                    Cell cell = field[x][y] = new Cell();
                    cell.setGraphicCoordinates(x, y, halfSizeCellX, halfSizeCellY);
                    for (MapLayer mapLayer : mapLayers) {
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
                                        cellSpawnHero = cell;
                                        cellSpawnHero.spawn = true;
//                                        waveManager.spawnPoints.add(new GridPoint2(x, y));
                                        Gdx.app.log("GameField::createField()", "-- Set cellSpawnHero:" + cellSpawnHero);
                                    } else if (tiledMapTile.getProperties().get("exitCell") != null) {
                                        cellExitHero = cell;
                                        cellExitHero.exit = true;
//                                        waveManager.exitPoints.add(new GridPoint2(x, y));
                                        Gdx.app.log("GameField::createField()", "-- Set cellExitHero:" + cellExitHero);
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
//        turnRight();
        flipY();
//        turnRight();
        pathFinder = new PathFinder();
        pathFinder.loadCharMatrix(getCharMatrix());
        Gdx.app.log("GameField::createField()", "-- pathFinder:" + pathFinder);
    }

    public void turnRight() {
        if(sizeFieldX == sizeFieldY) {
            Cell[][] newCells = new Cell[sizeFieldX][sizeFieldY];
            for(int y = 0; y < sizeFieldY; y++) {
                for(int x = 0; x < sizeFieldX; x++) {
                    newCells[sizeFieldX-y-1][x] = field[x][y];
                    newCells[sizeFieldX-y-1][x].setGraphicCoordinates(sizeFieldX-y-1, x, halfSizeCellX, halfSizeCellY);
                }
            }
            field = newCells;
        } else {
            Gdx.app.log("GameField::turnRight()", "-- Not work || Work, but mb not Good!");
            int oldWidth = sizeFieldX;
            int oldHeight = sizeFieldY;
            sizeFieldX = sizeFieldY;
            sizeFieldY = oldWidth;
            Cell[][] newCells = new Cell[sizeFieldX][sizeFieldY];
            for(int y = 0; y < oldHeight; y++) {
                for(int x = 0; x < oldWidth; x++) {
                    newCells[sizeFieldX-y-1][x] = field[x][y];
                    newCells[sizeFieldX-y-1][x].setGraphicCoordinates(sizeFieldX-y-1, x, halfSizeCellX, halfSizeCellY);
                }
            }
            field = newCells;
        }
    }

    public void turnLeft() {
        if(sizeFieldX == sizeFieldY) {
            Cell[][] newCells = new Cell[sizeFieldX][sizeFieldY];
            for(int y = 0; y < sizeFieldY; y++) {
                for(int x = 0; x < sizeFieldX; x++) {
                    newCells[y][sizeFieldY-x-1] = field[x][y];
                    newCells[y][sizeFieldY-x-1].setGraphicCoordinates(y, sizeFieldY-x-1, halfSizeCellX, halfSizeCellY);
                }
            }
            field = newCells;
        } else {
            Gdx.app.log("GameField::turnLeft()", "-- Not work || Work, but mb not Good!");
            int oldWidth = sizeFieldX;
            int oldHeight = sizeFieldY;
            sizeFieldX = sizeFieldY;
            sizeFieldY = oldWidth;
            Cell[][] newCells = new Cell[sizeFieldX][sizeFieldY];
            for(int y = 0; y < oldHeight; y++) {
                for(int x = 0; x < oldWidth; x++) {
                    newCells[y][sizeFieldY-x-1] = field[x][y];
                    newCells[y][sizeFieldY-x-1].setGraphicCoordinates(y, sizeFieldY-x-1, halfSizeCellX, halfSizeCellY);
                }
            }
            field = newCells;
        }
    }

    public void flipX() {
        Cell[][] newCells = new Cell[sizeFieldX][sizeFieldY];
        for (int y = 0; y < sizeFieldY; y++) {
            for (int x = 0; x < sizeFieldX; x++) {
                newCells[sizeFieldX-x-1][y] = field[x][y];
                newCells[sizeFieldX-x-1][y].setGraphicCoordinates(sizeFieldX-x-1, y, halfSizeCellX, halfSizeCellY);
            }
        }
        field = newCells;
    }

    public void flipY() {
        Cell[][] newCells = new Cell[sizeFieldX][sizeFieldY];
        for(int y = 0; y < sizeFieldY; y++) {
            for(int x = 0; x < sizeFieldX; x++) {
                newCells[x][sizeFieldY-y-1] = field[x][y];
                newCells[x][sizeFieldY-y-1].setGraphicCoordinates(x, sizeFieldY-y-1, halfSizeCellX, halfSizeCellY);
            }
        }
        field = newCells;
    }

    public char[][] getCharMatrix() {
        if (field != null) {
            char[][] charMatrix = new char[sizeFieldY][sizeFieldX];
            for (int y = 0; y < sizeFieldY; y++) {
                for (int x = 0; x < sizeFieldX; x++) {
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
        shapeRenderer.dispose();
        spriteBatch.dispose();
        bitmapFont.dispose();
        map.dispose();
        greenCheckmark.dispose();
        redCross.dispose();
    }

    public void render(float delta, OrthographicCamera camera) {
        delta = delta * gameSpeed;
        if (!gamePaused) {
            spawnUnits(delta);
            stepAllUnits(delta);
            shotAllTowers(delta);
            moveAllShells(delta);
        }

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        if(isDrawableBackground > 0) {
            drawBackGrounds(spriteBatch);
        }
        if(isDrawableGround > 0 || isDrawableUnits > 0 || isDrawableTowers > 0) {
            drawGroundsWithUnitsAndTowers(spriteBatch);
//            drawTowersUnderConstruction(spriteBatch);
        }
        if (isDrawableForeground > 0) {
            drawForeGrounds(spriteBatch);
        }
        spriteBatch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawUnitsBars(shapeRenderer);
        shapeRenderer.end();

        if (isDrawableGrid > 0)
            drawGrid(camera);

        if (isDrawableGridNav > 0) {
            drawRoutes(camera);
//            drawWavesRoutes(camera);
            drawGridNav(camera);
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        spriteBatch.begin();
        drawShells(spriteBatch);
        drawTowersUnderConstruction(spriteBatch, shapeRenderer);
        spriteBatch.end();
        shapeRenderer.end();

        shapeRenderer.setColor(Color.RED);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(0f, 0f, 5);
        shapeRenderer.end();
    }

    private void drawBackGrounds(SpriteBatch spriteBatch) {
        if(drawOrder == 0) {
            for (int y = 0; y < sizeFieldY; y++) {
                for (int x = 0; x < sizeFieldX; x++) {
                    drawBackGroundCell(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 1) {
            for (int x = 0; x < sizeFieldX; x++) {
                for (int y = 0; y < sizeFieldY; y++) {
                    drawBackGroundCell(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 2) {
            for (int y = sizeFieldY-1; y >= 0; y--) {
                for (int x = sizeFieldX-1; x >= 0; x--) {
                    drawBackGroundCell(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 3) {
            for (int x = sizeFieldX-1; x >= 0; x--) {
                for (int y = sizeFieldY-1; y >= 0; y--) {
                    drawBackGroundCell(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 4) {
            for (int y = sizeFieldY-1; y >= 0; y--) {
                for (int x = 0; x < sizeFieldX; x++) {
                    drawBackGroundCell(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 5) {
            for (int x = 0; x < sizeFieldX; x++) {
                for (int y = sizeFieldY-1; y >= 0; y--) {
                    drawBackGroundCell(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 6) {
            for (int y = 0; y < sizeFieldY; y++) {
                for (int x = sizeFieldX-1; x >= 0; x--) {
                    drawBackGroundCell(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 7) {
            for (int x = sizeFieldX-1; x >= 0; x--) {
                for (int y = 0; y < sizeFieldY; y++) {
                    drawBackGroundCell(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 8) {
//            Gdx.app.log("GameField::drawBackGrounds()", "-- camera.position:" + camera.position);
            int x = 0, y = 0;
            int length = (sizeFieldX > sizeFieldY) ? sizeFieldX : sizeFieldY;
            while (x < length) {
                if(x < sizeFieldX && y < sizeFieldY) {
                    if (x == length - 1 && y == length - 1) {
                        drawBackGroundCell(spriteBatch, x, y);
//                        Gdx.app.log("GameField::drawBackGrounds()", "-- хуй");
//                        break;
                    } else {
                        drawBackGroundCell(spriteBatch, x, y);
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

    private void drawBackGroundCell(SpriteBatch spriteBatch, int cellX, int cellY) {
        Cell cell = field[cellX][cellY];
        Array<TiledMapTile> tiledMapTiles = cell.backgroundTiles;
        for (TiledMapTile tiledMapTile : tiledMapTiles) {
            TextureRegion textureRegion = tiledMapTile.getTextureRegion();
            if (isDrawableBackground == 1 || isDrawableBackground == 5) {
                spriteBatch.draw(textureRegion, cell.graphicCoordinates1.x-halfSizeCellX, cell.graphicCoordinates1.y-halfSizeCellY);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
            if (isDrawableBackground == 2 || isDrawableBackground == 5) {
                spriteBatch.draw(textureRegion, cell.graphicCoordinates2.x-halfSizeCellX, cell.graphicCoordinates2.y-halfSizeCellY);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
            if (isDrawableBackground == 3 || isDrawableBackground == 5) {
                spriteBatch.draw(textureRegion, cell.graphicCoordinates3.x-halfSizeCellX, cell.graphicCoordinates3.y-halfSizeCellY);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
            if (isDrawableBackground == 4 || isDrawableBackground == 5) {
                spriteBatch.draw(textureRegion, cell.graphicCoordinates4.x-halfSizeCellX, cell.graphicCoordinates4.y-halfSizeCellY);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
        }
    }

    private void drawGroundsWithUnitsAndTowers(SpriteBatch spriteBatch) {
        if(drawOrder == 0) {
            for (int y = 0; y < sizeFieldY; y++) {
                for (int x = 0; x < sizeFieldX; x++) {
                    drawGroundCellWithUnitsAndTower(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 1) {
            for (int x = 0; x < sizeFieldX; x++) {
                for (int y = 0; y < sizeFieldY; y++) {
                    drawGroundCellWithUnitsAndTower(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 2) {
            for (int y = sizeFieldY-1; y >= 0; y--) {
                for (int x = sizeFieldX-1; x >= 0; x--) {
                    drawGroundCellWithUnitsAndTower(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 3) {
            for (int x = sizeFieldX-1; x >= 0; x--) {
                for (int y = sizeFieldY-1; y >= 0; y--) {
                    drawGroundCellWithUnitsAndTower(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 4) {
            for (int y = sizeFieldY-1; y >= 0; y--) {
                for (int x = 0; x < sizeFieldX; x++) {
                    drawGroundCellWithUnitsAndTower(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 5) {
            for (int x = 0; x < sizeFieldX; x++) {
                for (int y = sizeFieldY-1; y >= 0; y--) {
                    drawGroundCellWithUnitsAndTower(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 6) {
            for (int y = 0; y < sizeFieldY; y++) {
                for (int x = sizeFieldX-1; x >= 0; x--) {
                    drawGroundCellWithUnitsAndTower(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 7) {
            for (int x = sizeFieldX-1; x >= 0; x--) {
                for (int y = 0; y < sizeFieldY; y++) {
                    drawGroundCellWithUnitsAndTower(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 8) {
            int x = 0, y = 0;
            int length = (sizeFieldX > sizeFieldY) ? sizeFieldX : sizeFieldY;
            while (x < length) {
                if(x < sizeFieldX && y < sizeFieldY) {
                    if (x == length - 1 && y == length - 1) {
                        drawGroundCellWithUnitsAndTower(spriteBatch, x, y);
//                        Gdx.app.log("GameField::drawForeGroundsWithUnitsAndTowers()", "-- хуй");
//                        break;
                    } else {
                        drawGroundCellWithUnitsAndTower(spriteBatch, x, y);
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

    private void drawGroundCellWithUnitsAndTower(SpriteBatch spriteBatch, int cellX, int cellY) {
        Cell cell = field[cellX][cellY];
        Array<TiledMapTile> tiledMapTiles = cell.groundTiles;
        for (TiledMapTile tiledMapTile : tiledMapTiles) {
            TextureRegion textureRegion = tiledMapTile.getTextureRegion();
            if(isDrawableGround == 1 || isDrawableGround == 5) {
                spriteBatch.draw(textureRegion, cell.graphicCoordinates1.x-halfSizeCellX, cell.graphicCoordinates1.y-halfSizeCellY);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
            if(isDrawableGround == 2 || isDrawableGround == 5) {
                spriteBatch.draw(textureRegion, cell.graphicCoordinates2.x-halfSizeCellX, cell.graphicCoordinates2.y-halfSizeCellY);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
            if(isDrawableGround == 3 || isDrawableGround == 5) {
                spriteBatch.draw(textureRegion, cell.graphicCoordinates3.x-halfSizeCellX, cell.graphicCoordinates3.y-halfSizeCellY);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
            if(isDrawableGround == 4 || isDrawableGround == 5) {
                spriteBatch.draw(textureRegion, cell.graphicCoordinates4.x-halfSizeCellX, cell.graphicCoordinates4.y-halfSizeCellY);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
        }
        Array<Unit> units = field[cellX][cellY].getUnits();
        if(units != null) {
            Color oldColorSB = spriteBatch.getColor();
            for (Unit unit : units) {
                drawUnit(unit, spriteBatch);
            }
            spriteBatch.setColor(oldColorSB);
        }
        Tower tower = field[cellX][cellY].getTower();
        if(tower != null) {
            drawTower(tower, spriteBatch);
        }
    }

    private void drawForeGrounds(SpriteBatch spriteBatch) {
        if(drawOrder == 0) {
            for (int y = 0; y < sizeFieldY; y++) {
                for (int x = 0; x < sizeFieldX; x++) {
                    drawForeGroundCell(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 1) {
            for (int x = 0; x < sizeFieldX; x++) {
                for (int y = 0; y < sizeFieldY; y++) {
                    drawForeGroundCell(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 2) {
            for (int y = sizeFieldY-1; y >= 0; y--) {
                for (int x = sizeFieldX-1; x >= 0; x--) {
                    drawForeGroundCell(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 3) {
            for (int x = sizeFieldX-1; x >= 0; x--) {
                for (int y = sizeFieldY-1; y >= 0; y--) {
                    drawForeGroundCell(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 4) {
            for (int y = sizeFieldY-1; y >= 0; y--) {
                for (int x = 0; x < sizeFieldX; x++) {
                    drawForeGroundCell(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 5) {
            for (int x = 0; x < sizeFieldX; x++) {
                for (int y = sizeFieldY-1; y >= 0; y--) {
                    drawForeGroundCell(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 6) {
            for (int y = 0; y < sizeFieldY; y++) {
                for (int x = sizeFieldX-1; x >= 0; x--) {
                    drawForeGroundCell(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 7) {
            for (int x = sizeFieldX-1; x >= 0; x--) {
                for (int y = 0; y < sizeFieldY; y++) {
                    drawForeGroundCell(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 8) {
//            Gdx.app.log("GameField::drawBackGrounds()", "-- camera.position:" + camera.position);
            int x = 0, y = 0;
            int length = (sizeFieldX > sizeFieldY) ? sizeFieldX : sizeFieldY;
            while (x < length) {
                if(x < sizeFieldX && y < sizeFieldY) {
                    if (x == length - 1 && y == length - 1) {
                        drawForeGroundCell(spriteBatch, x, y);
//                        Gdx.app.log("GameField::drawBackGrounds()", "-- хуй");
//                        break;
                    } else {
                        drawForeGroundCell(spriteBatch, x, y);
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

    private void drawForeGroundCell(SpriteBatch spriteBatch, int cellX, int cellY) {
        Cell cell = field[cellX][cellY];
        Array<TiledMapTile> tiledMapTiles = cell.foregroundTiles;
        for (TiledMapTile tiledMapTile : tiledMapTiles) {
            TextureRegion textureRegion = tiledMapTile.getTextureRegion();
            if (isDrawableForeground == 1 || isDrawableForeground == 5) {
                spriteBatch.draw(textureRegion, cell.graphicCoordinates1.x-halfSizeCellX, cell.graphicCoordinates1.y-halfSizeCellY);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
            if (isDrawableForeground == 2 || isDrawableForeground == 5) {
                spriteBatch.draw(textureRegion, cell.graphicCoordinates2.x-halfSizeCellX, cell.graphicCoordinates2.y-halfSizeCellY);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
            if (isDrawableForeground == 3 || isDrawableForeground == 5) {
                spriteBatch.draw(textureRegion, cell.graphicCoordinates3.x-halfSizeCellX, cell.graphicCoordinates3.y-halfSizeCellY);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
            if (isDrawableForeground == 4 || isDrawableForeground == 5) {
                spriteBatch.draw(textureRegion, cell.graphicCoordinates4.x-halfSizeCellX, cell.graphicCoordinates4.y-halfSizeCellY);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
        }
    }

    private void drawGrid(OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BROWN); // (100, 60, 21, 1f);

        int widthForTop = sizeFieldY * halfSizeCellX; // A - B
        int heightForTop = sizeFieldY * halfSizeCellY; // B - Top
        int widthForBottom = sizeFieldX * halfSizeCellX; // A - C
        int heightForBottom = sizeFieldX * halfSizeCellY; // C - Bottom
//        Gdx.app.log("GameField::drawGrid(camera)", "-- widthForTop:" + widthForTop + " heightForTop:" + heightForTop + " widthForBottom:" + widthForBottom + " heightForBottom:" + heightForBottom);

        if(isDrawableGrid == 1 || isDrawableGrid == 5) {
            for (int x = 0; x <= sizeFieldX; x++)
                shapeRenderer.line( (halfSizeCellX*x),-(halfSizeCellY*x),-(widthForTop)+(halfSizeCellX*x),   -(heightForTop)-(x*halfSizeCellY));
            for (int y = 0; y <= sizeFieldY; y++)
                shapeRenderer.line(-(halfSizeCellX*y),-(halfSizeCellY*y), (widthForBottom)-(halfSizeCellX*y),-(heightForBottom)-(halfSizeCellY*y));
        }
        if(isDrawableGrid == 2 || isDrawableGrid == 5) {
            for (int x = 0; x <= sizeFieldX; x++)
                shapeRenderer.line((halfSizeCellX*x),-(halfSizeCellY*x),(widthForTop)+(halfSizeCellX*x),    (heightForTop)-(x*halfSizeCellY));
            for (int y = 0; y <= sizeFieldY; y++)
                shapeRenderer.line((halfSizeCellX*y), (halfSizeCellY*y),(widthForBottom)+(halfSizeCellX*y),-(heightForBottom)+(halfSizeCellY*y));
        }
        if(isDrawableGrid == 3 || isDrawableGrid == 5) {
            for (int x = 0; x <= sizeFieldY; x++) // WHT??? sizeFieldY check groundDraw
                shapeRenderer.line(-(halfSizeCellX*x),(halfSizeCellY*x), (widthForBottom)-(halfSizeCellX*x),(heightForBottom)+(x*halfSizeCellY));
            for (int y = 0; y <= sizeFieldX; y++) // WHT??? sizeFieldX check groundDraw
                shapeRenderer.line( (halfSizeCellX*y),(halfSizeCellY*y),-(widthForTop)+(halfSizeCellX*y),   (heightForTop)+(halfSizeCellY*y));
        }
        if(isDrawableGrid == 4 || isDrawableGrid == 5) {
            for (int x = 0; x <= sizeFieldY; x++) // WHT??? sizeFieldY check groundDraw
                shapeRenderer.line(-(halfSizeCellX*x), (halfSizeCellY*x),-(widthForBottom)-(halfSizeCellX*x),   -(heightForBottom)+(x*halfSizeCellY));
            for (int y = 0; y <= sizeFieldX; y++) // WHT??? sizeFieldX check groundDraw
                shapeRenderer.line(-(halfSizeCellX*y),-(halfSizeCellY*y),-(widthForTop)-(halfSizeCellX*y),(heightForTop)-(halfSizeCellY*y));
        }
        shapeRenderer.end();
    }

//    private void drawUnitsAndTowers(SpriteBatch spriteBatch) {
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

//    private void drawUnits(SpriteBatch spriteBatch) {
//        for (Unit unit : unitsManager.units) {
//            drawUnit(unit, spriteBatch);
//        }
//    }

    private void drawUnit(Unit unit, SpriteBatch spriteBatch) { //TODO Need to refactor this
//        Gdx.app.log("GameField::drawUnit(" + unit + "," + spriteBatch + ")", "-- Start!");
        for (ShellEffectType shellAttackType : unit.shellEffectTypes) {
            if(shellAttackType.shellEffectEnum == ShellEffectType.ShellEffectEnum.FreezeEffect) {
                spriteBatch.setColor(0.0f, 0.0f, 1.0f, 0.9f);
                // Gdx.app.log("GameField::drawUnit(" + unit + "," + spriteBatch + ")", "-- FreezeEffect!");
            }
            if(shellAttackType.shellEffectEnum == ShellEffectType.ShellEffectEnum.FireEffect) {
                spriteBatch.setColor(1.0f, 0.0f, 0.0f, 0.9f);
                // Gdx.app.log("GameField::drawUnit(" + unit + "," + spriteBatch + ")", "-- FireEffect!");
            }
        }
        TextureRegion currentFrame;
        if (unit.isAlive()) {
            currentFrame = unit.getCurrentFrame();
        } else {
            currentFrame = unit.getCurrentDeathFrame();
        }
        int deltaX = halfSizeCellX;
        int deltaY = sizeCellY;

        float fVx = 0f, fVy = 0f;
        if(isDrawableUnits == 1 || isDrawableUnits == 5) {
            fVx = unit.circle1.x - deltaX;
            fVy = unit.circle1.y - deltaY;
            spriteBatch.draw(currentFrame, fVx, fVy, sizeCellX, sizeCellY*2);
        }
        if(isDrawableUnits == 2 || isDrawableUnits == 5) {
            fVx = unit.circle2.x - deltaX;
            fVy = unit.circle2.y - deltaY;
            spriteBatch.draw(currentFrame, fVx, fVy, sizeCellX, sizeCellY*2);
        }
        if(isDrawableUnits == 3 || isDrawableUnits == 5) {
            fVx = unit.circle3.x - deltaX;
            fVy = unit.circle3.y - deltaY;
            spriteBatch.draw(currentFrame, fVx, fVy, sizeCellX, sizeCellY*2);
        }
        if(isDrawableUnits == 4 || isDrawableUnits == 5) {
            fVx = unit.circle4.x - deltaX;
            fVy = unit.circle4.y - deltaY;
            spriteBatch.draw(currentFrame, fVx, fVy, sizeCellX, sizeCellY*2);
        }
//        drawUnitBar(shapeRenderer, unit, currentFrame, fVx, fVy);
    }

    private void drawUnitsBars(ShapeRenderer shapeRenderer) {
        for (Unit unit : unitsManager.units) {
            if(isDrawableUnits == 1 || isDrawableUnits == 5) {
                drawUnitBar(shapeRenderer, unit, unit.circle1.x, unit.circle1.y);
            }
            if(isDrawableUnits == 2 || isDrawableUnits == 5) {
                drawUnitBar(shapeRenderer, unit, unit.circle2.x, unit.circle2.y);
            }
            if(isDrawableUnits == 3 || isDrawableUnits == 5) {
                drawUnitBar(shapeRenderer, unit, unit.circle3.x, unit.circle3.y);
            }
            if(isDrawableUnits == 4 || isDrawableUnits == 5) {
                drawUnitBar(shapeRenderer, unit, unit.circle4.x, unit.circle4.y);
            }
        }
    }

    private void drawUnitBar(ShapeRenderer shapeRenderer, Unit unit, float fVx, float fVy) {
        if (unit.isAlive()) {
            TextureRegion currentFrame = unit.getCurrentFrame();
            fVx -= sizeCellX/2;
            fVy -= sizeCellY;
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

            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.rect(fVx + hpBarWidthIndent, fVy + currentFrameHeight - hpBarTopIndent, hpBarHPWidth, hpBarHeight);
            shapeRenderer.setColor(Color.GREEN);
            float maxHP = unit.templateForUnit.healthPoints;
            float hp = unit.hp;
            hpBarHPWidth = hpBarHPWidth / maxHP * hp;
            shapeRenderer.rect(fVx + hpBarWidthIndent + hpBarSpace, fVy + currentFrameHeight - hpBarTopIndent + hpBarSpace, hpBarHPWidth - (hpBarSpace * 2), hpBarHeight - (hpBarSpace * 2));

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
                    shapeRenderer.setColor(Color.RED);
                } else if (shellEffectType.shellEffectEnum == ShellEffectType.ShellEffectEnum.FreezeEffect) {
                    shapeRenderer.setColor(Color.ROYAL);
                }
                float efWidth = effectBlockWidth - effectWidth * shellEffectType.elapsedTime;
                shapeRenderer.rect(efX, efY, efWidth, effectBarHeight);
                efX += effectBlockWidth;
//                Gdx.app.log("GameField::drawUnit()", "-- efX:" + efX + " efWidth:" + efWidth + ":" + effectIndex);
            }
        }
    }

    private void drawRoutes(OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BROWN); // (100, 60, 21, 1f);

        float gridNavRadius = sizeCellX/12f;
        for (Unit unit : unitsManager.units) {
            ArrayDeque<Node> route = unit.route;
            if (route != null) {
                for (Node coor : route) {
                    Cell cell = field[coor.getX()][coor.getY()];
                    if(isDrawableGridNav == 1 || isDrawableGridNav == 5) {
                        shapeRenderer.circle(cell.graphicCoordinates1.x, cell.graphicCoordinates1.y, gridNavRadius);
                    }
                    if(isDrawableGridNav == 2 || isDrawableGridNav == 5) {
                        shapeRenderer.circle(cell.graphicCoordinates2.x, cell.graphicCoordinates2.y, gridNavRadius);
                    }
                    if(isDrawableGridNav == 3 || isDrawableGridNav == 5) {
                        shapeRenderer.circle(cell.graphicCoordinates3.x, cell.graphicCoordinates3.y, gridNavRadius);
                    }
                    if(isDrawableGridNav == 4 || isDrawableGridNav == 5) {
                        shapeRenderer.circle(cell.graphicCoordinates4.x, cell.graphicCoordinates4.y, gridNavRadius);
                    }
                }
            }
        }
        shapeRenderer.end();
    }

    private void drawWavesRoutes(OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(Color.BROWN);
        for (Wave wave : waveManager.waves) {
            drawWave(wave);
        }
        shapeRenderer.setColor(Color.BLUE);
        for (Wave wave : waveManager.wavesForUser) {
            drawWave(wave);
        }
        shapeRenderer.end();
    }

    public void drawWave(Wave wave) {
//        Gdx.app.log("GameField::drawWave(" + wave + ")", "--");
        float linesWidth = sizeCellX/15f;
        ArrayDeque<Node> route = wave.route;
        if (route != null && !route.isEmpty()) {
            Iterator<Node> nodeIterator = route.iterator();
            Node startNode = nodeIterator.next();
            Node endNode = null;
            while (nodeIterator.hasNext()) {
                endNode = nodeIterator.next();
                Cell startCell = field[startNode.getX()][startNode.getY()];
                Cell endCell = field[endNode.getX()][endNode.getY()];
                if(isDrawableGridNav == 1 || isDrawableGridNav == 5) {
                    shapeRenderer.rectLine(startCell.graphicCoordinates1, endCell.graphicCoordinates1, linesWidth);
                }
                if(isDrawableGridNav == 2 || isDrawableGridNav == 5) {
                    shapeRenderer.rectLine(startCell.graphicCoordinates2, endCell.graphicCoordinates2, linesWidth);
                }
                if(isDrawableGridNav == 3 || isDrawableGridNav == 5) {
                    shapeRenderer.rectLine(startCell.graphicCoordinates3, endCell.graphicCoordinates3, linesWidth);
                }
                if(isDrawableGridNav == 4 || isDrawableGridNav == 5) {
                    shapeRenderer.rectLine(startCell.graphicCoordinates4, endCell.graphicCoordinates4, linesWidth);
                }
                startNode = endNode;
            }
        }
    }

//    private void drawTowers(SpriteBatch spriteBatch) {
//        for (Tower tower : towersManager.getAllTemplateForTowers()) {
//            drawTower(tower, spriteBatch);
//        }
//    }

    private void drawTower(Tower tower, SpriteBatch spriteBatch) {
        GridPoint2 cellPosition = tower.position;
        int towerSize = tower.templateForTower.size;
        Cell cell = getCell(cellPosition.x, cellPosition.y);
//        Vector2 towerPos = cell.getGraphicCoordinates(isDrawableTowers);
//        cameraController.shapeRender.circle(towerPos)
        Vector2 towerPos = new Vector2();
        TextureRegion currentFrame = tower.templateForTower.idleTile.getTextureRegion();
        if (isDrawableTowers == 5) {
            for (int m = 1; m < isDrawableTowers; m++) {
                towerPos.set(cell.getGraphicCoordinates(m));
                getCorrectGraphicTowerCoord(towerPos, towerSize, m);
                spriteBatch.draw(currentFrame, towerPos.x, towerPos.y, sizeCellX * towerSize, (sizeCellY * 2) * towerSize);
            }
        } else if (isDrawableTowers != 0) {
            towerPos.set(cell.getGraphicCoordinates(isDrawableTowers));
            getCorrectGraphicTowerCoord(towerPos, towerSize, isDrawableTowers);
            spriteBatch.draw(currentFrame, towerPos.x, towerPos.y, sizeCellX * towerSize, (sizeCellY * 2) * towerSize);
        }
        towerPos = null; // delete towerPos;
    }

    private void drawGridNav(OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        Vector2 pos = new Vector2();
        float gridNavRadius = sizeCellX/20f;
        for (int y = 0; y < sizeFieldY; y++) {
            for (int x = 0; x < sizeFieldX; x++) {
                Cell cell = field[x][y];
                if (cell != null && !cell.isEmpty()) {
                    if (cell.isTerrain()) {
                        shapeRenderer.setColor(Color.RED);
                    } else if (cell.getUnit() != null) {
                        shapeRenderer.setColor(Color.GREEN);
                    } else if (cell.getTower() != null) {
//                        shapeRenderer.setColor(new Color(225f, 224f, 0f, 255f));
                        shapeRenderer.setColor(Color.YELLOW);
                    }
                    if(isDrawableGridNav == 1 || isDrawableGridNav == 5) {
                        pos.set(cell.getGraphicCoordinates(1));
                        shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                    }
                    if(isDrawableGridNav == 2 || isDrawableGridNav == 5) {
                        pos.set(cell.getGraphicCoordinates(2));
                        shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                    }
                    if(isDrawableGridNav == 3 || isDrawableGridNav == 5) {
                        pos.set(cell.getGraphicCoordinates(3));
                        shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                    }
                    if(isDrawableGridNav == 4 || isDrawableGridNav == 5) {
                        pos.set(cell.getGraphicCoordinates(4));
                        shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                    }
                }
            }
        }

        Array<GridPoint2> spawnPoints = waveManager.getAllSpawnCells();
        shapeRenderer.setColor(new Color(0f, 255f, 204f, 255f));
        for (GridPoint2 spawnPoint : spawnPoints) {
            Cell cell = field[spawnPoint.x][spawnPoint.y];
            if(isDrawableGridNav == 1 || isDrawableGridNav == 5) {
                pos.set(cell.getGraphicCoordinates(1));
                shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
            }
            if(isDrawableGridNav == 2 || isDrawableGridNav == 5) {
                pos.set(cell.getGraphicCoordinates(2));
                shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
            }
            if(isDrawableGridNav == 3 || isDrawableGridNav == 5) {
                pos.set(cell.getGraphicCoordinates(3));
                shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
            }
            if(isDrawableGridNav == 4 || isDrawableGridNav == 5) {
                pos.set(cell.getGraphicCoordinates(4));
                shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
            }
        }

        Array<GridPoint2> exitPoints = waveManager.getAllExitPoint();
        shapeRenderer.setColor(new Color(255f, 0f, 102f, 255f));
        for (GridPoint2 exitPoint : exitPoints) {
//            Gdx.app.log("GameField::drawGridNav()", "-- exitCell.cellX:" + exitCell.cellX + " exitCell.y:" + exitCell.y + " isDrawableGridNav:" + isDrawableGridNav);
            Cell cell = field[exitPoint.x][exitPoint.y];
            if(isDrawableGridNav == 1 || isDrawableGridNav == 5) {
                pos.set(cell.getGraphicCoordinates(1));
                shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
            }
            if(isDrawableGridNav == 2 || isDrawableGridNav == 5) {
                pos.set(cell.getGraphicCoordinates(2));
                shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
            }
            if(isDrawableGridNav == 3 || isDrawableGridNav == 5) {
                pos.set(cell.getGraphicCoordinates(3));
                shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
            }
            if(isDrawableGridNav == 4 || isDrawableGridNav == 5) {
                pos.set(cell.getGraphicCoordinates(4));
                shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
            }
        }

        shapeRenderer.setColor(Color.ORANGE);
        for (Tower tower : towersManager.towers) {
            for (Bullet bullet : tower.bullets) {
                shapeRenderer.rectLine(bullet.currentPoint.x, bullet.currentPoint.y, bullet.endPoint.x, bullet.endPoint.y, sizeCellX/40f);
                if (null != bullet.circle) {
                    shapeRenderer.circle(bullet.circle.x, bullet.circle.y, bullet.circle.radius);
                }
            }
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        for (Unit unit : unitsManager.units) {
            if(isDrawableUnits == 1 || isDrawableUnits == 5)
                shapeRenderer.circle(unit.circle1.x, unit.circle1.y, unit.circle1.radius);
            if(isDrawableUnits == 2 || isDrawableUnits == 5)
                shapeRenderer.circle(unit.circle2.x, unit.circle2.y, unit.circle2.radius);
            if(isDrawableUnits == 3 || isDrawableUnits == 5)
                shapeRenderer.circle(unit.circle3.x, unit.circle3.y, unit.circle3.radius);
            if(isDrawableUnits == 4 || isDrawableUnits == 5)
                shapeRenderer.circle(unit.circle4.x, unit.circle4.y, unit.circle4.radius);
        }

        shapeRenderer.setColor(new Color(153f, 255f, 51f, 1f));
        Vector2 towerPos = new Vector2();
        for (Tower tower : towersManager.towers) { // Draw white towers radius! -- radiusDetectionCircle
            if(isDrawableGridNav == 5) {
                if(isDrawableTowers == 5) {
                    for (int m = 1; m < isDrawableTowers; m++) {
                        towerPos.set(tower.getCenterGraphicCoord(m)); // Need recoding this func!
                        shapeRenderer.circle(towerPos.x, towerPos.y, tower.radiusDetectionCircle.radius);
                    }
                } else if(isDrawableTowers != 0) {
                    towerPos.set(tower.getCenterGraphicCoord(isDrawableTowers));
                    shapeRenderer.circle(towerPos.x, towerPos.y, tower.radiusDetectionCircle.radius);
                }
            } else /*if(isDrawableGridNav != 0)*/ {
                if(isDrawableGridNav == isDrawableTowers) {
                    towerPos.set(tower.getCenterGraphicCoord(isDrawableTowers));
                    shapeRenderer.circle(towerPos.x, towerPos.y, tower.radiusDetectionCircle.radius);
                }
            }
        }

        shapeRenderer.setColor(Color.FIREBRICK);
        for (Tower tower : towersManager.towers) { // Draw FIREBRICK towers radius! -- radiusFlyShellCircle
            if (tower.radiusFlyShellCircle != null) {
                if(isDrawableGridNav == 5) {
                    if(isDrawableTowers == 5) {
                        for (int m = 1; m <= isDrawableTowers; m++) {
                            towerPos.set(tower.getCenterGraphicCoord(m)); // Need recoding this func!
                            shapeRenderer.circle(towerPos.x, towerPos.y, tower.radiusFlyShellCircle.radius);
                        }
                    } else {
                        towerPos.set(tower.getCenterGraphicCoord(isDrawableTowers));
                        shapeRenderer.circle(towerPos.x, towerPos.y, tower.radiusFlyShellCircle.radius);
                    }
                } else {
                    if(isDrawableGridNav == isDrawableTowers) {
                        towerPos.set(tower.getCenterGraphicCoord(isDrawableTowers));
                        shapeRenderer.circle(towerPos.x, towerPos.y, tower.radiusFlyShellCircle.radius);
                    }
                }
            }
        }
        shapeRenderer.end();

        spriteBatch.begin();
        bitmapFont.setColor(Color.YELLOW);
        bitmapFont.getData().setScale(0.7f);
        for (Tower tower : towersManager.towers) { // Draw pit capacity value
            if (tower.templateForTower.towerAttackType == TowerAttackType.Pit) {
                if(isDrawableGridNav == 5) {
                    if(isDrawableTowers == 5) {
                        for (int m = 1; m <= isDrawableTowers; m++) {
                            towerPos.set(tower.getCenterGraphicCoord(m)); // Need recoding this func!
                            bitmapFont.draw(spriteBatch, String.valueOf(tower.capacity), towerPos.x, towerPos.y);
                        }
                    } else {
                        towerPos.set(tower.getCenterGraphicCoord(isDrawableTowers));
                        bitmapFont.draw(spriteBatch, String.valueOf(tower.capacity), towerPos.x, towerPos.y);
                    }
                } else {
                    if(isDrawableGridNav == isDrawableTowers) {
                        towerPos.set(tower.getCenterGraphicCoord(isDrawableTowers));
                        bitmapFont.draw(spriteBatch, String.valueOf(tower.capacity), towerPos.x, towerPos.y);
                    }
                }
            }
        }
        spriteBatch.end();
    }

    private void drawShells(SpriteBatch spriteBatch) {
        for (Tower tower : towersManager.towers) {
            for (Bullet bullet : tower.bullets) {
                TextureRegion textureRegion = bullet.textureRegion;
//                float width = textureRegion.getRegionWidth() * bullet.ammoSize;
//                float height = textureRegion.getRegionHeight() * bullet.ammoSize;
//                spriteBatch.draw(textureRegion, bullet.currentPoint.x, bullet.currentPoint.y, width, height);
                spriteBatch.draw(textureRegion, bullet.currentPoint.x - bullet.circle.radius, bullet.currentPoint.y - bullet.circle.radius, bullet.circle.radius * 2, bullet.circle.radius * 2);
//                Gdx.app.log("GameField", "drawProjecTiles(); -- Draw bullet:" + bullet.currentPoint);
            }
        }
    }

    private void drawTowersUnderConstruction(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        if (underConstruction != null) {
            int goldNeed = underConstruction.templateForTower.cost;
            boolean enoughGold = (gamerGold >= goldNeed) ? true : false;
            if (underConstruction.state == 0) {
                drawTowerUnderConstruction(spriteBatch, shapeRenderer, underConstruction.endX, underConstruction.endY, underConstruction.templateForTower, enoughGold);
            } else if (underConstruction.state == 1) {
                drawTowerUnderConstruction(spriteBatch, shapeRenderer, underConstruction.startX, underConstruction.startY, underConstruction.templateForTower, enoughGold);
                for (int k = 0; k < underConstruction.coorsX.size; k++) {
                    goldNeed += underConstruction.templateForTower.cost;
                    enoughGold = (gamerGold >= goldNeed) ? true : false;
                    drawTowerUnderConstruction(spriteBatch, shapeRenderer, underConstruction.coorsX.get(k), underConstruction.coorsY.get(k), underConstruction.templateForTower, enoughGold);
                }
            }
        }
    }

    private void drawTowerUnderConstruction(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, int buildX, int buildY, TemplateForTower templateForTower, boolean enoughGold) {
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
                if(mainCell != null) {
                    if (!mainCell.isEmpty()) {
                        if (drawFull) {
                            canBuild = false;
                        }
                    }
                }
            }
        }
        if (drawFull) {
            Cell mainCell = getCell(buildX, buildY);
            if(mainCell != null) {
                Color oldColorSB = spriteBatch.getColor();
                Color oldColorSR = shapeRenderer.getColor();
                if (enoughGold && canBuild) {
                    spriteBatch.setColor(0, 1f, 0, 0.55f);
                    shapeRenderer.setColor(0, 1f, 0, 0.55f);
                } else {
                    spriteBatch.setColor(1f, 0, 0, 0.55f);
                    shapeRenderer.setColor(1f, 0, 0, 0.55f);
                }
                if (isDrawableTowers == 5) {
                    for (int map = 1; map < isDrawableTowers; map++) {
                        drawTowerUnderConstructionAndMarks(spriteBatch, shapeRenderer, map, templateForTower, mainCell, startDrawCell, finishDrawCell);
                    }
                } else if (isDrawableTowers != 0) {
                    drawTowerUnderConstructionAndMarks(spriteBatch, shapeRenderer, isDrawableTowers, templateForTower, mainCell, startDrawCell, finishDrawCell);
                }
                spriteBatch.setColor(oldColorSB);
                shapeRenderer.setColor(oldColorSR);
            }
        }
    }

    private void drawTowerUnderConstructionAndMarks(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, int map, TemplateForTower templateForTower, Cell mainCell, GridPoint2 startDrawCell, GridPoint2 finishDrawCell) {
//        Gdx.app.log("GameField::drawTowerUnderConstructionAndMarks()", "-- spriteBatch:" + /*spriteBatch +*/ " shapeRenderer:" + /*shapeRenderer +*/ " map:" + map + " templateForTower:" + templateForTower + " mainCell:" + mainCell + " startDrawCell:" + startDrawCell + " finishDrawCell:" + finishDrawCell);
        TextureRegion textureRegion = templateForTower.idleTile.getTextureRegion();
        int towerSize = templateForTower.size;
        Vector2 towerPos = new Vector2(mainCell.getGraphicCoordinates(map));
        if (templateForTower.radiusDetection != null) {
            shapeRenderer.circle(towerPos.x, towerPos.y, templateForTower.radiusDetection);
        }
        getCorrectGraphicTowerCoord(towerPos, towerSize, map);
        spriteBatch.draw(textureRegion, towerPos.x, towerPos.y, sizeCellX * towerSize, (sizeCellY * 2) * towerSize);
        if (greenCheckmark != null && redCross != null) {
            Vector2 markPos = new Vector2();
            for (int x = startDrawCell.x; x <= finishDrawCell.x; x++) {
                for (int y = startDrawCell.y; y <= finishDrawCell.y; y++) {
                    Cell markCell = getCell(mainCell.cellX + x, mainCell.cellY + y);
                    if (markCell != null) {
                        markPos.set(markCell.getGraphicCoordinates(map));
                        markPos.add(-(halfSizeCellX), -(halfSizeCellY));
                        if(markCell.isEmpty()) {
                            spriteBatch.draw(greenCheckmark, markPos.x, markPos.y, sizeCellX, sizeCellY * 2);
                        } else {
                            spriteBatch.draw(redCross, markPos.x, markPos.y, sizeCellX, sizeCellY * 2);
                        }
                    }
                }
            }
            markPos = null; // delete markPos;
        }
        towerPos = null; // delete towerPos;
    }

    public void setExitPoint(int x, int y) {
        waveManager.setExitCell(new GridPoint2(x, y));
        rerouteForAllUnits(new GridPoint2(x, y));
    }

    public void spawnUnitFromUser(TemplateForUnit templateForUnit) {
        Gdx.app.log("GameField::spawnUnitFromUser()", "-- templateForUnit:" + templateForUnit);
        if (gamerGold >= templateForUnit.cost) {
            gamerGold -= templateForUnit.cost;
            for (Wave wave : waveManager.wavesForUser) {
                Cell spawnCell = getCell(wave.spawnCell.x, wave.spawnCell.y);
                Cell destExitCell = getCell(wave.exitCell.x, wave.exitCell.y);
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
                Cell spawnCell = getCell(templateNameAndPoints.spawnCell.x, templateNameAndPoints.spawnCell.y);
                Cell destExitCell = getCell(templateNameAndPoints.exitCell.x, templateNameAndPoints.exitCell.y);
                createUnit(spawnCell, destExitCell, templateForUnit, 0, destExitCell); // create Computer0 Unit
            } else {
                Gdx.app.error("GameField::spawnUnit()", "-- templateForUnit == null | templateName:" + templateNameAndPoints.templateName);
            }
        }
    }

    private void spawnHeroInSpawnPoint() {
        cellSpawnHero.removeTerrain(true);
        removeTower(cellSpawnHero.cellX, cellSpawnHero.cellY);
        createUnit(cellSpawnHero.cellX, cellSpawnHero.cellY, cellExitHero.cellX, cellExitHero.cellY, 1); // player1 = hero
    }

    void spawnCompUnitToRandomExit(int x, int y) {
        Gdx.app.log("GameField::spawnCompUnitToRandomExit()", "-- x:" + x + " y:" + y);
        int randomX = (int)(Math.random()*sizeFieldX);
        int randomY = (int)(Math.random()*sizeFieldY);
        Gdx.app.log("GameField::spawnCompUnitToRandomExit()", "-- randomX:" + randomX + " randomY:" + randomY);
        createUnit(x, y, randomX, randomY, 0);
    }

    public void createUnit(int x, int y) {
        Cell spawnCell = getCell(x, y);
        Cell destExitCell = getCell(waveManager.lastExitCell.x, waveManager.lastExitCell.y);
        createUnit(spawnCell, destExitCell, factionsManager.getRandomTemplateForUnitFromFirstFaction(), 0, destExitCell); // create computer0 Unit
    }

    public void createUnit(int x, int y, int x2, int y2, int player) {
        if (player == 0) {
            createUnit(getCell(x, y), getCell(x2, y2), factionsManager.getRandomTemplateForUnitFromFirstFaction(), player, null);
        } else if (player == 1) {
            createUnit(getCell(x, y), getCell(x2, y2), factionsManager.getTemplateForUnitByName("unit3_footman"), player, cellExitHero);
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
            for (int tmpX = startX; tmpX <= finishX; tmpX++)
                for (int tmpY = startY; tmpY <= finishY; tmpY++)
                    if (!getCell(buildX + tmpX, buildY + tmpY).isEmpty())
                        return false;

            // GOVNO CODE
            GridPoint2 position = new GridPoint2(buildX, buildY);
            Tower tower = towersManager.createTower(position, templateForTower, player);
            Gdx.app.log("GameField::createTower()", "-- templateForTower.towerAttackType:" + templateForTower.towerAttackType);
            if (templateForTower.towerAttackType != TowerAttackType.Pit) {
                for (int tmpX = startX; tmpX <= finishX; tmpX++) {
                    for (int tmpY = startY; tmpY <= finishY; tmpY++) {
                        field[buildX + tmpX][buildY + tmpY].setTower(tower);
                        pathFinder.nodeMatrix[buildY + tmpY][buildX + tmpX].setKey('T');
                    }
                }
            }
            // GOVNO CODE

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
            GridPoint2 pos = tower.position;
            removeTower(pos.x, pos.y);
//        }
    }

    public void removeTower(int touchX, int touchY) {
        Tower tower = field[touchX][touchY].getTower();
        if (tower != null) {
            int x = tower.position.x;
            int y = tower.position.y;
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

    private void stepAllUnits(float delta) {
//        Gdx.app.log("GameField::stepAllUnits()", "-- unitsManager.units:" + unitsManager.units.size);
        for (Unit unit : unitsManager.units) {
            Node oldPosition = unit.newPosition;
            if (unit.isAlive()) {
                Node newPosition = unit.move(delta);
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
                        missedUnitsForComputer0++;
                    } else if (unit.player == 0) {
                        if (unit.exitCell == cell) {
                            missedUnitsForPlayer1++;
                            cell.removeUnit(unit);
                            unitsManager.removeUnit(unit);
                            Gdx.app.log("GameField::stepAllUnits()", "-- unitsManager.removeUnit(unit):");
                        } else {
                            if (unit.route == null || unit.route.isEmpty()) {
                                int randomX = (int)(Math.random()*sizeFieldX);
                                int randomY = (int)(Math.random()*sizeFieldY);
                                unit.route = pathFinder.route(oldPosition.getX(), oldPosition.getY(), randomX, randomY);
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

    private void shotAllTowers(float delta) {
        for (Tower tower : towersManager.towers) {
            TowerAttackType towerAttackType = tower.templateForTower.towerAttackType;
            if (towerAttackType == TowerAttackType.Pit) {
                Unit unit = field[tower.position.x][tower.position.y].getUnit();
                if (unit != null && !unit.templateForUnit.type.equals("fly") && unit.player != tower.player) {
                    Gdx.app.log("GameField", "shotAllTowers(); -- tower.capacity:" + tower.capacity + " unit.getHp:" + unit.hp);
//                    unit.die(unit.getHp());
                    unitsManager.removeUnit(unit);
                    field[tower.position.x][tower.position.y].removeUnit(unit);
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
                                if (Intersector.overlaps(tower.radiusDetectionCircle, unit.circle1)) {
//                                    Gdx.app.log("GameField", "shotAllTowers(); -- Intersector.overlaps(" + tower.toString() + ", " + unit.toString());
                                    if (tower.shoot(unit)) {
                                        if(tower.templateForTower.shellAttackType != ShellAttackType.MassAddEffect) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
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
                GridPoint2 position = tower.position;
                Cell cell = getCell(tmpX + position.x, tmpY + position.y);
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

    public WhichCell getWhichCell() {
        return whichCell;
    }

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
        if (missedUnitsForPlayer1 >= maxOfMissedUnitsForPlayer1) {
//            Gdx.app.log("GameField::getGameState()", "-- LOSE!!");
            return "Lose";
        } else {
            if(missedUnitsForComputer0 >= maxOfMissedUnitsForComputer0) { // При инициализации если в карте не было голды игроку. и у игрока изначально было 0 голды. то он сразу же выиграет
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

    // GAME INTERFACE ZONE2

//    private boolean cellIsEmpty(int x, int y) {
//        if (x >= 0 && y >= 0) {
//            if (x < sizeFieldX && y < sizeFieldY) {
//                return field[x][y].isEmpty();
//            }
//        }
//        return false;
//    }
//
//    private boolean cellHasUnit(int x, int y) {
//        if (x >= 0 && y >= 0) {
//            if (x < sizeFieldX && y < sizeFieldY) {
//                return field[x][y].getUnit() != null;
//            }
//        }
//        return false;
//    }

    private Cell getCell(int x, int y) {
        if (x >= 0 && x < sizeFieldX) {
            if (y >= 0 && y < sizeFieldY) {
                return field[x][y];
            }
        }
        return null;
    }

//    public Vector2 getGraphicCoordinates(int cellX, int cellY, int map) {
//        float pxlsX = 0f, pxlsY = 0f;
//        if(map == 1) { // Нижняя карта
//            pxlsX = (-(halfSizeCellX * cellY) + (cellX * halfSizeCellX));
//            pxlsY = (-(halfSizeCellY * cellY) - (cellX * halfSizeCellY));
//        } else if(map == 2) { // Правая карта
//            pxlsX = ( (halfSizeCellX * cellY) + (cellX * halfSizeCellX)) + halfSizeCellX;
//            pxlsY = ( (halfSizeCellY * cellY) - (cellX * halfSizeCellY)) + halfSizeCellY;
//        } else if(map == 3) { // Верхняя карта
//            pxlsX = (-(halfSizeCellX * cellY) + (cellX * halfSizeCellX));
//            pxlsY = ( (halfSizeCellY * cellY) + (cellX * halfSizeCellY)) + halfSizeCellY*2;
//        } else if(map == 4) {// Левая карта
//            pxlsX = (-(halfSizeCellX * cellY) - (cellX * halfSizeCellX)) - halfSizeCellX;
//            pxlsY = ( (halfSizeCellY * cellY) - (cellX * halfSizeCellY)) + halfSizeCellY;
//        }
////        Gdx.app.log("GameField::getGraphicCoordinates(" + cellX + "," + cellY + "," + map + ")", "-- pxlsX:" + pxlsX + " pxlsY:" + pxlsY);
//        return new Vector2(pxlsX, pxlsY);
//    }

    public boolean getCorrectGraphicTowerCoord(Vector2 towerPos, int towerSize, int map) {
        if(map == 1) {
            towerPos.x += (-(halfSizeCellX * towerSize) );
            towerPos.y += (-(halfSizeCellY * (towerSize - ((towerSize % 2 != 0) ? 0 : 1))));
        } else if(map == 2) {
            towerPos.x += (-(halfSizeCellX * ((towerSize % 2 != 0) ? towerSize : towerSize+1)) );
            towerPos.y += (-(halfSizeCellY * towerSize));
        } else if(map == 3) {
            towerPos.x += (-(halfSizeCellX * towerSize) );
            towerPos.y += (-(halfSizeCellY * ((towerSize % 2 != 0) ? towerSize : towerSize+1)));
        } else if(map == 4) {
            towerPos.x += (-(halfSizeCellX * (towerSize - ((towerSize % 2 != 0) ? 0 : 1))) );
            towerPos.y += (-(halfSizeCellY * towerSize));
        } else {
            Gdx.app.log("GameField::getCorrectGraphicTowerCoord(" + towerPos + ", " + towerSize + ", " + map + ")", "-- Bad map[1-4] value:" + map);
            return false;
        }
        return true;
    }
}
