package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.TTW;
import com.betmansmall.enums.GameState;
import com.betmansmall.game.GameSettings;
import com.betmansmall.game.GameType;
import com.betmansmall.game.gameLogic.mapLoader.MapLayer;
import com.betmansmall.game.gameLogic.mapLoader.MapLoader;
import com.betmansmall.game.gameLogic.mapLoader.TiledMap;
import com.betmansmall.game.gameLogic.mapLoader.TiledMapTile;
import com.betmansmall.game.gameLogic.mapLoader.TiledMapTileLayer;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.PathFinder.Node;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.PathFinder.PathFinder;
import com.betmansmall.game.gameLogic.playerTemplates.FactionsManager;
import com.betmansmall.game.gameLogic.playerTemplates.TowerShellEffect;
import com.betmansmall.game.gameLogic.playerTemplates.TowerShellType;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;
import com.betmansmall.game.gameLogic.playerTemplates.TowerAttackType;

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
    public TiledMap map;
    public boolean turnedMap = false; // костыль. нужно допиливать поворт карты. если этот функционал дествительно нужен.
    private Cell[][] field;
    private PathFinder pathFinder;

    private UnderConstruction underConstruction;
    private Texture greenCheckmark;
    private Texture redCross;

    // GAME INTERFACE ZONE1
    public float timeOfGame;
    public float gameSpeed;
    public boolean gamePaused;
    public boolean unitsSpawn;
    public int gamerGold;

    public GameField() {
        this.factionsManager = TTW.game.factionsManager;
        this.waveManager = new WaveManager();
        this.towersManager = new TowersManager();
        this.unitsManager = new UnitsManager();
        this.gameSettings = TTW.game.sessionSettings.gameSettings;

        map = new MapLoader(waveManager).load(gameSettings.mapPath);
        Gdx.app.log("GameField::GameField()", "-- map:" + map);

        underConstruction = null;
        greenCheckmark = new Texture(Gdx.files.internal("maps/textures/green_checkmark.png"));
        redCross = new Texture(Gdx.files.internal("maps/textures/red_cross.png"));
        if (greenCheckmark == null || redCross == null) {
            Gdx.app.error("GameField::GameField()", "-- Achtung! NOT FOUND 'maps/textures/green_checkmark.png' || 'maps/textures/red_cross.png'");
        }

        createField();
        if (gameSettings.isometric) {
            flipY();
        }
        pathFinder = new PathFinder(this);
        pathFinder.loadCharMatrix(getCharMatrix());
        Gdx.app.log("GameField::GameField()", "-- pathFinder:" + pathFinder);

        gamerGold = 100000;
        timeOfGame = 0.0f;
        gameSpeed = 1.0f;
        gamePaused = false;
        unitsSpawn = false;

        Gdx.app.log("GameField::GameField()", "-- gameSettings.gameType:" + gameSettings.gameType);
        if (gameSettings.gameType == GameType.LittleGame) {
            int randomEnemyCount = gameSettings.enemyCount;
            Gdx.app.log("GameField::GameField()", "-- randomEnemyCount:" + randomEnemyCount);
            for (int k = 0; k < randomEnemyCount; k++) {
                int randomX = (int)(Math.random()*map.width);
                int randomY = (int)(Math.random()*map.height);
                Gdx.app.log("GameField::GameField()", "-- k:" + k);
                Gdx.app.log("GameField::GameField()", "-- randomX:" + randomX);
                Gdx.app.log("GameField::GameField()", "-- randomY:" + randomY);
                if (getCell(randomX, randomY).isEmpty()) {
                    if (spawnCompUnitToRandomExit(randomX, randomY) == null) {
                        k--;
                    }
                } else {
                    k--;
                }
            }
            int randomTowerCount = gameSettings.towersCount;
            Gdx.app.log("GameField::GameField()", "-- randomTowerCount:" + randomTowerCount);
            for (int k = 0; k < randomTowerCount; k++) {
                int randomX = (int)(Math.random()*map.width);
                int randomY = (int)(Math.random()*map.height);
                Gdx.app.log("GameField::GameField()", "-- k:" + k);
                Gdx.app.log("GameField::GameField()", "-- randomX:" + randomX);
                Gdx.app.log("GameField::GameField()", "-- randomY:" + randomY);
                if (getCell(randomX, randomY).isEmpty()) {
                    if (createTower(randomX, randomY, factionsManager.getRandomTemplateForTowerFromAllFaction(), 0) == null) {
                        k--;
                    }
                } else {
                    k--;
                }
            }
            spawnHeroInSpawnPoint();
        } else if (gameSettings.gameType == GameType.TowerDefence) {
            waveManager.validationPoints(field, map.width, map.height);
            if (waveManager.waves.size == 0) {
                for (int w = 0; w < 10; w++) {
                    GridPoint2 spawnPoint = new GridPoint2((int) (Math.random() * map.width), (int) (Math.random() * map.height));
                    GridPoint2 exitPoint = new GridPoint2((int) (Math.random() * map.width), (int) (Math.random() * map.height));
                    Cell spawnCell = getCell(spawnPoint.x, spawnPoint.y);
                    Cell exitCell = getCell(exitPoint.x, exitPoint.y);
                    if (spawnCell != null && spawnCell.isEmpty()) {
                        if (exitCell != null && exitCell.isEmpty()) {
                            Wave wave = new Wave(spawnPoint, exitPoint);
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
            MapProperties mapProperties = map.getProperties();
            Gdx.app.log("GameField::GameField()", "-- mapProperties:" + mapProperties);
            if (mapProperties.containsKey("gamerGold")) {
//                gamerGold = Integer.parseInt(mapProperties.get("gamerGold").toString()); // HARD GAME | one gold = one unit for computer!!!
            }
            gameSettings.maxOfMissedUnitsForComputer0 = mapProperties.get("maxOfMissedUnitsForComputer0", gamerGold, Integer.class); // Игрок может сразу выиграть если у него не будет голды. так как @ref2
            gameSettings.missedUnitsForComputer0 = 0;
            if (gameSettings.maxOfMissedUnitsForPlayer1 == 0) {
                gameSettings.maxOfMissedUnitsForPlayer1 = mapProperties.get("maxOfMissedUnitsForPlayer1", waveManager.getNumberOfActions() / 8, Integer.class); // it is not true | need implement getNumberOfUnits()
            }
            gameSettings.missedUnitsForPlayer1 = 0;
            Gdx.app.log("GameField::GameField()", "-- gamerGold:" + gamerGold);
            Gdx.app.log("GameField::GameField()", "-- gameSettings.maxOfMissedUnitsForComputer0:" + gameSettings.maxOfMissedUnitsForComputer0);
            Gdx.app.log("GameField::GameField()", "-- gameSettings.maxOfMissedUnitsForPlayer1:" + gameSettings.maxOfMissedUnitsForPlayer1);
        } else {
            Gdx.app.log("GameField::GameField()", "-- gameSettings.gameType:" + gameSettings.gameType);
        }
        Gdx.app.log("GameField::GameField()", "-end-");
    }

    public void dispose() {
        Gdx.app.log("GameField::dispose()", "-- Called!");
//        factionsManager.dispose();
        waveManager.dispose();
        towersManager.dispose();
        unitsManager.dispose();
        map.dispose();
//        for (Cell[] cellArr : field) { // memory leak
//            for (Cell cell : cellArr) {
//                cell.dispose();
//                cell = null;
//            }
//        }
        field = null;
//        pathFinder.dispose();

        if (underConstruction != null) {
            underConstruction.dispose();
        }
        greenCheckmark.dispose();
        redCross.dispose();
    }

    private void createField() {
        if (map.getProperties().containsKey("orientation")) {
            if (map.getProperties().get("orientation").equals("isometric")) {
                gameSettings.isometric = true;
            }
        }
        Gdx.app.log("GameField::createField()", "-START- field:" + field);
        if (field == null) {
            field = new Cell[map.width][map.height];
            for (int y = 0; y < map.height; y++) {
                for (int x = 0; x < map.width; x++) {
                    Cell cell = field[x][y] = new Cell();
                    cell.setGraphicCoordinates(x, y, map.tileWidth, map.tileHeight, gameSettings.isometric);
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
                                            cell.setTerrain(tiledMapTile, false, false);
                                        } else if ( layerName.equals("towers") ) {
                                            cell.removeTerrain(true);
                                            this.createTower(x, y, factionsManager.getRandomTemplateForTowerFromAllFaction(), 0);
                                        } else {
                                            cell.foregroundTiles.add(tiledMapTile);
                                        }
                                    }
                                    if (tiledMapTile.getProperties().containsKey("spawnPoint")) {
                                        gameSettings.cellSpawnHero = cell;
                                        gameSettings.cellSpawnHero.spawn = true;
                                        Gdx.app.log("GameField::createField()", "-- Set gameSettings.cellSpawnHero:" + gameSettings.cellSpawnHero);
                                    } else if (tiledMapTile.getProperties().containsKey("exitPoint")) {
                                        gameSettings.cellExitHero = cell;
                                        gameSettings.cellExitHero.exit = true;
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
        Gdx.app.log("GameField::createField()", "-END- field:" + (field != null) );
    }

    public boolean landscapeGenerator(String mapPath) {
        Gdx.app.log("GameField::landscapeGenerator()", "-- mapPath:" + mapPath);
        return true;
    }

    public Cell getCell(int x, int y) {
        if (x >= 0 && x < map.width) {
            if (y >= 0 && y < map.height) {
                return field[x][y];
            }
        }
//        Gdx.app.log("GameField::getCell()", "-NotGood- x:" + x + " y:" + y + " map.width:" + map.width + " map.height:" + map.height);
        return null;
    }

    public void updateCellsGraphicCoordinates(float sizeCellX, float sizeCellY) {
        for (int cellX = 0; cellX < map.width; cellX++) {
            for (int cellY = 0; cellY < map.height; cellY++) {
                field[cellX][cellY].setGraphicCoordinates(cellX, cellY, sizeCellX, sizeCellY, gameSettings.isometric);
            }
        }
    }

    public void updateTowersGraphicCoordinates(CameraController cameraController) {
        for (Tower tower : towersManager.towers) {
            tower.updateCenterGraphicCoordinates(cameraController);
        }
    }

    public void render(float deltaTime, CameraController cameraController) {
        deltaTime = deltaTime * gameSpeed;
        if (!gamePaused) {
            timeOfGame += deltaTime;
            spawnUnits(deltaTime);
            stepAllUnits(deltaTime, cameraController);
            shotAllTowers(deltaTime, cameraController);
            moveAllShells(deltaTime, cameraController);
        }

//        drawFullField(cameraController);
        cameraController.spriteBatch.setProjectionMatrix(cameraController.camera.combined);
        cameraController.spriteBatch.begin();
        cameraController.shapeRenderer.setProjectionMatrix(cameraController.camera.combined);
        cameraController.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
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
        cameraController.shapeRenderer.end();
        cameraController.spriteBatch.end();

        cameraController.shapeRenderer.setProjectionMatrix(cameraController.camera.combined);
        cameraController.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawUnitsBars(cameraController);
        drawTowersBars(cameraController);
        cameraController.shapeRenderer.end();

        if (cameraController.isDrawableGrid > 0)
            drawGrid(cameraController);
        if (cameraController.isDrawableGridNav > 0)
            drawGridNav(cameraController);
//            drawGridNavs(cameraController);
        if (cameraController.isDrawableRoutes > 0) {
            drawRoutes(cameraController);
//            drawWavesRoutes(camera);
        }

        cameraController.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        cameraController.spriteBatch.begin();
        drawBullets(cameraController);
        drawTowersUnderConstruction(cameraController);
        cameraController.spriteBatch.end();
        cameraController.shapeRenderer.end();

        cameraController.shapeRenderer.setColor(Color.RED);
        cameraController.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        cameraController.shapeRenderer.circle(0f, 0f, 5);
        cameraController.shapeRenderer.end();
    }

//void GameField::drawFullField(CameraController* cameraController) {
////    qDebug() << "GameField::drawFullField(); -- map:" << map;
////    qDebug() << "GameField::drawFullField(); -- map->tiledMapTileSets:size" << map->tiledMapTileSets.size();
////    qDebug() << "GameField::drawFullField(); -- map->tiledMapTileSets.getTiledMapTile(85):" << map->tiledMapTileSets.getTiledMapTile(85);
////    qDebug() << "GameField::drawFullField(); -- map->tiledMapTileSets.getTiledMapTile(85)->getPixmap():" << map->tiledMapTileSets.getTiledMapTile(85)->getPixmap();
//    if(gameSettings->isometric) {
//        QPixmap pixmap = map->tiledMapTileSets.getTiledMapTile(85)->getPixmap(); // draw water2
//        int sizeX = 30;//width()/sizeCellX)+1;
//        int sizeY = 30;//(height()/sizeCellY)*2+2;
//        int isometricSpaceX = 0;
//        int isometricSpaceY = -(cameraController->sizeCellY/2);
//        for (int y = 0; y <= sizeY; y++) {
//            for (int x = 0; x <= sizeX; x++) {
//                cameraController->painter->drawPixmap(isometricSpaceX - cameraController->sizeCellX/2 + x*cameraController->sizeCellX, isometricSpaceY - cameraController->sizeCellY, sizeCellX, sizeCellY, pixmap);
//            }
//            isometricSpaceY += cameraController->sizeCellY/2;
//            isometricSpaceX = isometricSpaceX != 0 ? 0 : cameraController->sizeCellX/2;
//        }
//    }
//}

    private void drawGrid(CameraController cameraController) {
        cameraController.shapeRenderer.setProjectionMatrix(cameraController.camera.combined);
        cameraController.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        cameraController.shapeRenderer.setColor(Color.BROWN); // (100, 60, 21, 1f);
        if (!gameSettings.isometric) {
            float sizeCellX = cameraController.sizeCellX;
//            float sizeCellY = cameraController.sizeCellY;
            if (cameraController.isDrawableGrid == 1 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x < map.width+1; x++)
                    cameraController.shapeRenderer.line(-(x*sizeCellX), 0, -(x*sizeCellX), -(sizeCellX*map.height));
                for (int y = 0; y < map.height+1; y++)
                    cameraController.shapeRenderer.line(0, -(y*sizeCellX), -(sizeCellX*map.width), -(y*sizeCellX));
            }
            if (cameraController.isDrawableGrid == 2 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x < map.width+1; x++)
                    cameraController.shapeRenderer.line(x*sizeCellX, 0, x*sizeCellX, -(sizeCellX*map.height));
                for (int y = 0; y < map.height+1; y++)
                    cameraController.shapeRenderer.line(0, -(y*sizeCellX), sizeCellX*map.width, -(y*sizeCellX));
            }
            if (cameraController.isDrawableGrid == 3 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x < map.width+1; x++)
                    cameraController.shapeRenderer.line(x*sizeCellX, 0, x*sizeCellX, sizeCellX*map.height);
                for (int y = 0; y < map.height+1; y++)
                    cameraController.shapeRenderer.line(0, y*sizeCellX, sizeCellX*map.width, y*sizeCellX);
            }
            if (cameraController.isDrawableGrid == 4 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x < map.width+1; x++)
                    cameraController.shapeRenderer.line(-(x*sizeCellX), 0, -(x*sizeCellX), sizeCellX*map.height);
                for (int y = 0; y < map.height+1; y++)
                    cameraController.shapeRenderer.line(0, y*sizeCellX, -(sizeCellX*map.width), y*sizeCellX);
            }
        } else {
            float halfSizeCellX = cameraController.halfSizeCellX;
            float halfSizeCellY = cameraController.halfSizeCellY;
            float widthForTop = map.height * halfSizeCellX; // A - B
            float heightForTop = map.height * halfSizeCellY; // B - Top
            float widthForBottom = map.width * halfSizeCellX; // A - C
            float heightForBottom = map.width * halfSizeCellY; // C - Bottom
//        Gdx.app.log("GameField::drawGrid(camera)", "-- widthForTop:" + widthForTop + " heightForTop:" + heightForTop + " widthForBottom:" + widthForBottom + " heightForBottom:" + heightForBottom);
            if (cameraController.isDrawableGrid == 1 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x <= map.width; x++)
                    cameraController.shapeRenderer.line((halfSizeCellX*x),-(halfSizeCellY*x),-(widthForTop)+(halfSizeCellX*x),-(heightForTop)-(x*halfSizeCellY));
                for (int y = 0; y <= map.height; y++)
                    cameraController.shapeRenderer.line(-(halfSizeCellX*y),-(halfSizeCellY*y),(widthForBottom)-(halfSizeCellX*y),-(heightForBottom)-(halfSizeCellY*y));
            }
            if (cameraController.isDrawableGrid == 2 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x <= map.width; x++)
                    cameraController.shapeRenderer.line((halfSizeCellX*x),-(halfSizeCellY*x),(widthForTop)+(halfSizeCellX*x),(heightForTop)-(x*halfSizeCellY));
                for (int y = 0; y <= map.height; y++)
                    cameraController.shapeRenderer.line((halfSizeCellX*y),(halfSizeCellY*y),(widthForBottom)+(halfSizeCellX*y),-(heightForBottom)+(halfSizeCellY*y));
            }
            if (cameraController.isDrawableGrid == 3 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x <= map.height; x++) // WHT??? map.height check groundDraw
                    cameraController.shapeRenderer.line(-(halfSizeCellX*x),(halfSizeCellY*x),(widthForBottom)-(halfSizeCellX*x),(heightForBottom)+(x*halfSizeCellY));
                for (int y = 0; y <= map.width; y++) // WHT??? map.width check groundDraw
                    cameraController.shapeRenderer.line((halfSizeCellX*y),(halfSizeCellY*y),-(widthForTop)+(halfSizeCellX*y),(heightForTop)+(halfSizeCellY*y));
            }
            if (cameraController.isDrawableGrid == 4 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x <= map.height; x++) // WHT??? map.height check groundDraw
                    cameraController.shapeRenderer.line(-(halfSizeCellX*x),(halfSizeCellY*x),-(widthForBottom)-(halfSizeCellX*x),-(heightForBottom)+(x*halfSizeCellY));
                for (int y = 0; y <= map.width; y++) // WHT??? map.width check groundDraw
                    cameraController.shapeRenderer.line(-(halfSizeCellX*y),-(halfSizeCellY*y),-(widthForTop)-(halfSizeCellX*y),(heightForTop)-(halfSizeCellY*y));
            }
        }
        cameraController.shapeRenderer.end();
    }

    private void drawBackGrounds(CameraController cameraController) {
        if (cameraController.drawOrder == 0) {
            for (int y = 0; y < map.height; y++) {
                for (int x = 0; x < map.width; x++) {
                    drawBackGroundCell(cameraController, x, y);
                }
            }
        } else if (cameraController.drawOrder == 1) {
            for (int x = 0; x < map.width; x++) {
                for (int y = 0; y < map.height; y++) {
                    drawBackGroundCell(cameraController, x, y);
                }
            }
        } else if (cameraController.drawOrder == 2) {
            for (int y = map.height - 1; y >= 0; y--) {
                for (int x = map.width - 1; x >= 0; x--) {
                    drawBackGroundCell(cameraController, x, y);
                }
            }
        } else if (cameraController.drawOrder == 3) {
            for (int x = map.width - 1; x >= 0; x--) {
                for (int y = map.height - 1; y >= 0; y--) {
                    drawBackGroundCell(cameraController, x, y);
                }
            }
        } else if (cameraController.drawOrder == 4) {
            for (int y = map.height - 1; y >= 0; y--) {
                for (int x = 0; x < map.width; x++) {
                    drawBackGroundCell(cameraController, x, y);
                }
            }
        } else if (cameraController.drawOrder == 5) {
            for (int x = 0; x < map.width; x++) {
                for (int y = map.height - 1; y >= 0; y--) {
                    drawBackGroundCell(cameraController, x, y);
                }
            }
        } else if (cameraController.drawOrder == 6) {
            for (int y = 0; y < map.height; y++) {
                for (int x = map.width - 1; x >= 0; x--) {
                    drawBackGroundCell(cameraController, x, y);
                }
            }
        } else if (cameraController.drawOrder == 7) {
            for (int x = map.width - 1; x >= 0; x--) {
                for (int y = 0; y < map.height; y++) {
                    drawBackGroundCell(cameraController, x, y);
                }
            }
        } else if (cameraController.drawOrder == 8) {
            int x = 0, y = 0;
            int length = (map.width > map.height) ? map.width : map.height;
            while (x < length) {
                if (x < map.width && y < map.height) {
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
        float sizeCellX = cameraController.sizeCellX;
        float sizeCellY = cameraController.sizeCellY*2;
        float deltaX = cameraController.halfSizeCellX;
        float deltaY = cameraController.halfSizeCellY;
        if (!gameSettings.isometric) {
            sizeCellY = cameraController.sizeCellY;
//            deltaY = cameraController.sizeCellY;
        }
        Cell cell = field[cellX][cellY];
        Array<TiledMapTile> tiledMapTiles = cell.backgroundTiles;
        for (TiledMapTile tiledMapTile : tiledMapTiles) {
            TextureRegion textureRegion = tiledMapTile.getTextureRegion();
//            if (cameraController.isDrawableBackground == 1 || cameraController.isDrawableBackground == 5) {
//                cameraController.spriteBatch.draw(textureRegion, cell.graphicCoordinates1.x-deltaX, cell.graphicCoordinates1.y-deltaY, sizeCellX, sizeCellY);
//            }
//            if (cameraController.isDrawableBackground == 2 || cameraController.isDrawableBackground == 5) {
//                cameraController.spriteBatch.draw(textureRegion, cell.graphicCoordinates2.x-deltaX, cell.graphicCoordinates2.y-deltaY, sizeCellX, sizeCellY);
//            }
//            if (cameraController.isDrawableBackground == 3 || cameraController.isDrawableBackground == 5) {
//                cameraController.spriteBatch.draw(textureRegion, cell.graphicCoordinates3.x-deltaX, cell.graphicCoordinates3.y-deltaY, sizeCellX, sizeCellY);
//            }
//            if (cameraController.isDrawableBackground == 4 || cameraController.isDrawableBackground == 5) {
//                cameraController.spriteBatch.draw(textureRegion, cell.graphicCoordinates4.x-deltaX, cell.graphicCoordinates4.y-deltaY, sizeCellX, sizeCellY);
//            }
            Vector2 cellCoord = new Vector2();
            if (cameraController.isDrawableBackground == 5) {
                for (int m = 1; m < cameraController.isDrawableBackground; m++) {
                    cellCoord.set(cell.getGraphicCoordinates(m));
                    cameraController.spriteBatch.draw(textureRegion, cellCoord.x-deltaX, cellCoord.y-deltaY, sizeCellX, sizeCellY);
                }
            } else if (cameraController.isDrawableBackground != 0) {
                cellCoord.set(cell.getGraphicCoordinates(cameraController.isDrawableBackground));
                cameraController.spriteBatch.draw(textureRegion, cellCoord.x-deltaX, cellCoord.y-deltaY, sizeCellX, sizeCellY);
            }
        }
    }

    private void drawGroundsWithUnitsAndTowers(CameraController cameraController) {
        if (cameraController.drawOrder == 0) {
            for (int y = 0; y < map.height; y++) {
                for (int x = 0; x < map.width; x++) {
                    drawGroundCellWithUnitsAndTower(cameraController, x, y);
                }
            }
        } else if (cameraController.drawOrder == 1) {
            for (int x = 0; x < map.width; x++) {
                for (int y = 0; y < map.height; y++) {
                    drawGroundCellWithUnitsAndTower(cameraController, x, y);
                }
            }
        } else if (cameraController.drawOrder == 2) {
            for (int y = map.height - 1; y >= 0; y--) {
                for (int x = map.width - 1; x >= 0; x--) {
                    drawGroundCellWithUnitsAndTower(cameraController, x, y);
                }
            }
        } else if (cameraController.drawOrder == 3) {
            for (int x = map.width - 1; x >= 0; x--) {
                for (int y = map.height - 1; y >= 0; y--) {
                    drawGroundCellWithUnitsAndTower(cameraController, x, y);
                }
            }
        } else if (cameraController.drawOrder == 4) {
            for (int y = map.height - 1; y >= 0; y--) {
                for (int x = 0; x < map.width; x++) {
                    drawGroundCellWithUnitsAndTower(cameraController, x, y);
                }
            }
        } else if (cameraController.drawOrder == 5) {
            for (int x = 0; x < map.width; x++) {
                for (int y = map.height - 1; y >= 0; y--) {
                    drawGroundCellWithUnitsAndTower(cameraController, x, y);
                }
            }
        } else if (cameraController.drawOrder == 6) {
            for (int y = 0; y < map.height; y++) {
                for (int x = map.width - 1; x >= 0; x--) {
                    drawGroundCellWithUnitsAndTower(cameraController, x, y);
                }
            }
        } else if (cameraController.drawOrder == 7) {
            for (int x = map.width - 1; x >= 0; x--) {
                for (int y = 0; y < map.height; y++) {
                    drawGroundCellWithUnitsAndTower(cameraController, x, y);
                }
            }
        } else if (cameraController.drawOrder == 8) {
            int x = 0, y = 0;
            int length = (map.width > map.height) ? map.width : map.height;
            while (x < length) {
                if (x < map.width && y < map.height) {
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
        float sizeCellX = cameraController.sizeCellX;
        float sizeCellY = cameraController.sizeCellY*2;
        float deltaX = cameraController.halfSizeCellX;
        float deltaY = cameraController.halfSizeCellY;
        if (!gameSettings.isometric) {
            sizeCellY = cameraController.sizeCellY;
//            deltaY = cameraController.sizeCellY;
        }
        Cell cell = field[cellX][cellY];
        Array<TiledMapTile> tiledMapTiles = cell.groundTiles;
        for (TiledMapTile tiledMapTile : tiledMapTiles) {
            TextureRegion textureRegion = tiledMapTile.getTextureRegion();
//            if (cameraController.isDrawableGround == 1 || cameraController.isDrawableGround == 5) {
//                cameraController.spriteBatch.draw(textureRegion, cell.graphicCoordinates1.x-deltaX, cell.graphicCoordinates1.y-deltaY, sizeCellX, sizeCellY);
//            }
//            if (cameraController.isDrawableGround == 2 || cameraController.isDrawableGround == 5) {
//                cameraController.spriteBatch.draw(textureRegion, cell.graphicCoordinates2.x-deltaX, cell.graphicCoordinates2.y-deltaY, sizeCellX, sizeCellY);
//            }
//            if (cameraController.isDrawableGround == 3 || cameraController.isDrawableGround == 5) {
//                cameraController.spriteBatch.draw(textureRegion, cell.graphicCoordinates3.x-deltaX, cell.graphicCoordinates3.y-deltaY, sizeCellX, sizeCellY);
//            }
//            if (cameraController.isDrawableGround == 4 || cameraController.isDrawableGround == 5) {
//                cameraController.spriteBatch.draw(textureRegion, cell.graphicCoordinates4.x-deltaX, cell.graphicCoordinates4.y-deltaY, sizeCellX, sizeCellY);
//            }
            Vector2 cellCoord = new Vector2();
            if (cameraController.isDrawableGround == 5) {
                for (int m = 1; m < cameraController.isDrawableGround; m++) {
                    cellCoord.set(cell.getGraphicCoordinates(m));
                    cameraController.spriteBatch.draw(textureRegion, cellCoord.x-deltaX, cellCoord.y-deltaY, sizeCellX, sizeCellY);
                }
            } else if (cameraController.isDrawableGround != 0) {
                cellCoord.set(cell.getGraphicCoordinates(cameraController.isDrawableGround));
                cameraController.spriteBatch.draw(textureRegion, cellCoord.x-deltaX, cellCoord.y-deltaY, sizeCellX, sizeCellY);
            }
        }
        Array<Unit> units = cell.getUnits();
        if(units != null) {
            Color oldColorSB = cameraController.spriteBatch.getColor();
            for (Unit unit : units) {
                drawUnit(cameraController, unit);
            }
            cameraController.spriteBatch.setColor(oldColorSB);
        }
        Tower tower = cell.getTower();
        if(tower != null) {
            drawTower(cameraController, tower);
        }
    }

    private void drawForeGrounds(CameraController cameraController) {
        if (cameraController.drawOrder == 0) {
            for (int y = 0; y < map.height; y++) {
                for (int x = 0; x < map.width; x++) {
                    drawForeGroundCell(cameraController, x, y);
                }
            }
        } else if (cameraController.drawOrder == 1) {
            for (int x = 0; x < map.width; x++) {
                for (int y = 0; y < map.height; y++) {
                    drawForeGroundCell(cameraController, x, y);
                }
            }
        } else if (cameraController.drawOrder == 2) {
            for (int y = map.height - 1; y >= 0; y--) {
                for (int x = map.width - 1; x >= 0; x--) {
                    drawForeGroundCell(cameraController, x, y);
                }
            }
        } else if (cameraController.drawOrder == 3) {
            for (int x = map.width - 1; x >= 0; x--) {
                for (int y = map.height - 1; y >= 0; y--) {
                    drawForeGroundCell(cameraController, x, y);
                }
            }
        } else if (cameraController.drawOrder == 4) {
            for (int y = map.height - 1; y >= 0; y--) {
                for (int x = 0; x < map.width; x++) {
                    drawForeGroundCell(cameraController, x, y);
                }
            }
        } else if (cameraController.drawOrder == 5) {
            for (int x = 0; x < map.width; x++) {
                for (int y = map.height - 1; y >= 0; y--) {
                    drawForeGroundCell(cameraController, x, y);
                }
            }
        } else if (cameraController.drawOrder == 6) {
            for (int y = 0; y < map.height; y++) {
                for (int x = map.width - 1; x >= 0; x--) {
                    drawForeGroundCell(cameraController, x, y);
                }
            }
        } else if (cameraController.drawOrder == 7) {
            for (int x = map.width - 1; x >= 0; x--) {
                for (int y = 0; y < map.height; y++) {
                    drawForeGroundCell(cameraController, x, y);
                }
            }
        } else if (cameraController.drawOrder == 8) {
            int x = 0, y = 0;
            int length = (map.width > map.height) ? map.width : map.height;
            while (x < length) {
                if (x < map.width && y < map.height) {
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
        float sizeCellX = cameraController.sizeCellX;
        float sizeCellY = cameraController.sizeCellY*2;
        float deltaX = cameraController.halfSizeCellX;
        float deltaY = cameraController.halfSizeCellY;
        if (!gameSettings.isometric) {
            sizeCellY = cameraController.sizeCellY;
//            deltaY = cameraController.sizeCellY;
        }
        Cell cell = field[cellX][cellY];
        Array<TiledMapTile> tiledMapTiles = cell.foregroundTiles;
        for (TiledMapTile tiledMapTile : tiledMapTiles) {
            TextureRegion textureRegion = tiledMapTile.getTextureRegion();
//            if (cameraController.isDrawableForeground == 1 || cameraController.isDrawableForeground == 5) {
//                cameraController.spriteBatch.draw(textureRegion, cell.graphicCoordinates1.x-deltaX, cell.graphicCoordinates1.y-deltaY, sizeCellX, sizeCellY);
//            }
//            if (cameraController.isDrawableForeground == 2 || cameraController.isDrawableForeground == 5) {
//                cameraController.spriteBatch.draw(textureRegion, cell.graphicCoordinates2.x-deltaX, cell.graphicCoordinates2.y-deltaY, sizeCellX, sizeCellY);
//            }
//            if (cameraController.isDrawableForeground == 3 || cameraController.isDrawableForeground == 5) {
//                cameraController.spriteBatch.draw(textureRegion, cell.graphicCoordinates3.x-deltaX, cell.graphicCoordinates3.y-deltaY, sizeCellX, sizeCellY);
//            }
//            if (cameraController.isDrawableForeground == 4 || cameraController.isDrawableForeground == 5) {
//                cameraController.spriteBatch.draw(textureRegion, cell.graphicCoordinates4.x-deltaX, cell.graphicCoordinates4.y-deltaY, sizeCellX, sizeCellY);
//            }
            Vector2 cellCoord = new Vector2();
            if (cameraController.isDrawableForeground == 5) {
                for (int m = 1; m < cameraController.isDrawableForeground; m++) {
                    cellCoord.set(cell.getGraphicCoordinates(m));
                    cameraController.spriteBatch.draw(textureRegion, cellCoord.x-deltaX, cellCoord.y-deltaY, sizeCellX, sizeCellY);
                }
            } else if (cameraController.isDrawableForeground != 0) {
                cellCoord.set(cell.getGraphicCoordinates(cameraController.isDrawableForeground));
                cameraController.spriteBatch.draw(textureRegion, cellCoord.x-deltaX, cellCoord.y-deltaY, sizeCellX, sizeCellY);
            }
        }
    }

    private void drawUnit(CameraController cameraController, Unit unit) { //TODO Need to refactor this
//        Gdx.app.log("GameField::drawUnit(" + unit + "," + spriteBatch + ")", "-- Start!");
//        for (TowerShellEffect shellAttackType : unit.shellEffectTypes) {
//            if(shellAttackType.shellEffectEnum == TowerShellEffect.ShellEffectEnum.FreezeEffect) {
//                cameraController.spriteBatch.setColor(0.0f, 0.0f, 1.0f, 0.9f);
//                // Gdx.app.log("GameField::drawUnit(" + unit + "," + spriteBatch + ")", "-- FreezeEffect!");
//            }
//            if(shellAttackType.shellEffectEnum == TowerShellEffect.ShellEffectEnum.FireEffect) {
//                cameraController.spriteBatch.setColor(1.0f, 0.0f, 0.0f, 0.9f);
//                // Gdx.app.log("GameField::drawUnit(" + unit + "," + spriteBatch + ")", "-- FireEffect!");
//            }
//        }
        TextureRegion currentFrame = null;
        if (unit.isAlive()) {
            currentFrame = unit.getCurrentAttackFrame();
            if (currentFrame == null) {
                currentFrame = unit.getCurrentFrame();
            }
        } else {
            currentFrame = unit.getCurrentDeathFrame();
        }

        float sizeCellX = cameraController.sizeCellX;
        float sizeCellY = cameraController.sizeCellY*2;
        float deltaX = cameraController.halfSizeCellX;
        float deltaY = cameraController.sizeCellY;
        if (!gameSettings.isometric) {
            sizeCellY = cameraController.sizeCellY;
            deltaY = cameraController.halfSizeCellY;
        }
        float fVx = 0f, fVy = 0f;
//        if (cameraController.isDrawableUnits == 1 || cameraController.isDrawableUnits == 5) {
//            fVx = unit.circle1.x - deltaX;
//            fVy = unit.circle1.y - deltaY;
//            cameraController.spriteBatch.draw(currentFrame, fVx, fVy, sizeCellX, sizeCellY);
//        }
//        if (cameraController.isDrawableUnits == 2 || cameraController.isDrawableUnits == 5) {
//            fVx = unit.circle2.x - deltaX;
//            fVy = unit.circle2.y - deltaY;
//            cameraController.spriteBatch.draw(currentFrame, fVx, fVy, sizeCellX, sizeCellY);
//        }
//        if (cameraController.isDrawableUnits == 3 || cameraController.isDrawableUnits == 5) {
//            fVx = unit.circle3.x - deltaX;
//            fVy = unit.circle3.y - deltaY;
//            cameraController.spriteBatch.draw(currentFrame, fVx, fVy, sizeCellX, sizeCellY);
//        }
//        if (cameraController.isDrawableUnits == 4 || cameraController.isDrawableUnits == 5) {
//            fVx = unit.circle4.x - deltaX;
//            fVy = unit.circle4.y - deltaY;
//            cameraController.spriteBatch.draw(currentFrame, fVx, fVy, sizeCellX, sizeCellY);
//        }
        if (cameraController.isDrawableUnits == 5) {
            for (int m = 1; m < cameraController.isDrawableUnits; m++) {
                Circle circle = unit.getCircle(m);
                fVx = circle.x - deltaX;
                fVy = circle.y - deltaY;
                cameraController.spriteBatch.draw(currentFrame, fVx, fVy, sizeCellX, sizeCellY);
            }
        } else if (cameraController.isDrawableUnits != 0) {
            Circle circle = unit.getCircle(cameraController.isDrawableUnits);
            fVx = circle.x - deltaX;
            fVy = circle.y - deltaY;
            cameraController.spriteBatch.draw(currentFrame, fVx, fVy, sizeCellX, sizeCellY);
        }
//        drawUnitBar(shapeRenderer, unit, currentFrame, fVx, fVy);
    }

    private void drawUnitsBars(CameraController cameraController) {
        for (Unit unit : unitsManager.units) {
            if (unit.isAlive()) {
//                if (cameraController.isDrawableUnits == 1 || cameraController.isDrawableUnits == 5) {
//                    drawUnitBar(cameraController, unit, unit.circle1.x, unit.circle1.y);
//                }
//                if (cameraController.isDrawableUnits == 2 || cameraController.isDrawableUnits == 5) {
//                    drawUnitBar(cameraController, unit, unit.circle2.x, unit.circle2.y);
//                }
//                if (cameraController.isDrawableUnits == 3 || cameraController.isDrawableUnits == 5) {
//                    drawUnitBar(cameraController, unit, unit.circle3.x, unit.circle3.y);
//                }
//                if (cameraController.isDrawableUnits == 4 || cameraController.isDrawableUnits == 5) {
//                    drawUnitBar(cameraController, unit, unit.circle4.x, unit.circle4.y);
//                }
                if (cameraController.isDrawableUnits == 5) {
                    for (int m = 1; m < cameraController.isDrawableUnits; m++) {
                        Circle circle = unit.getCircle(m);
                        drawUnitBar(cameraController, unit, circle.x, circle.y);
                    }
                } else if (cameraController.isDrawableUnits != 0) {
                    Circle circle = unit.getCircle(cameraController.isDrawableUnits);
                    drawUnitBar(cameraController, unit, circle.x, circle.y);
                }
            }
        }
    }

    private void drawUnitBar(CameraController cameraController, Unit unit, float fVx, float fVy) {
        float maxHP = unit.templateForUnit.healthPoints;
        float hp = unit.hp;
        if (maxHP != hp) {
            TextureRegion currentFrame = unit.getCurrentFrame();
            fVx -= cameraController.sizeCellX/2;
            fVy -= cameraController.sizeCellY;
            float currentFrameWidth = currentFrame.getRegionWidth();
            float currentFrameHeight = currentFrame.getRegionHeight();
            float hpBarSpace = 0.8f;
            float hpBarHPWidth = 30f;
            float hpBarHeight = 7f;
            float hpBarWidthIndent = (currentFrameWidth - hpBarHPWidth) / 2;
            float hpBarTopIndent = hpBarHeight;

            cameraController.shapeRenderer.setColor(Color.BLACK);
            cameraController.shapeRenderer.rect(fVx + hpBarWidthIndent, fVy + currentFrameHeight - hpBarTopIndent, hpBarHPWidth, hpBarHeight);
            cameraController.shapeRenderer.setColor(Color.GREEN);

            hpBarHPWidth = hpBarHPWidth / maxHP * hp;
            cameraController.shapeRenderer.rect(fVx + hpBarWidthIndent + hpBarSpace, fVy + currentFrameHeight - hpBarTopIndent + hpBarSpace, hpBarHPWidth - (hpBarSpace * 2), hpBarHeight - (hpBarSpace * 2));

            float allTime = 0f;
            for (TowerShellEffect towerShellEffect : unit.shellEffectTypes) {
                allTime += towerShellEffect.time;
            }

            if (allTime != 0.0) {
                float effectBarWidthSpace = hpBarSpace * 2;
                float effectBarHeightSpace = hpBarSpace * 2;
                float effectBarWidth = hpBarHPWidth - effectBarWidthSpace * 2;
                float effectBarHeight = hpBarHeight - (effectBarHeightSpace * 2);
                float effectWidth = effectBarWidth / allTime;
                float efX = fVx + hpBarWidthIndent + effectBarWidthSpace;
                float efY = fVy + currentFrameHeight - hpBarTopIndent + effectBarHeightSpace;
                float effectBlockWidth = effectBarWidth / unit.shellEffectTypes.size;
                for (int effectIndex = 0; effectIndex < unit.shellEffectTypes.size; effectIndex++) {
                    TowerShellEffect towerShellEffect = unit.shellEffectTypes.get(effectIndex);
                    if (towerShellEffect.shellEffectEnum == TowerShellEffect.ShellEffectEnum.FireEffect) {
                        cameraController.shapeRenderer.setColor(Color.RED);
                    } else if (towerShellEffect.shellEffectEnum == TowerShellEffect.ShellEffectEnum.FreezeEffect) {
                        cameraController.shapeRenderer.setColor(Color.ROYAL);
                    }
                    float efWidth = effectBlockWidth - effectWidth * towerShellEffect.elapsedTime;
                    cameraController.shapeRenderer.rect(efX, efY, efWidth, effectBarHeight);
                    efX += effectBlockWidth;
//                    Gdx.app.log("GameField::drawUnit()", "-- efX:" + efX + " efWidth:" + efWidth + ":" + effectIndex);
                }
            }
        }
    }

    private void drawTower(CameraController cameraController, Tower tower) {
        Cell cell = tower.cell;
        int towerSize = tower.templateForTower.size;
//        Vector2 towerPos = new Vector2(cell.getGraphicCoordinates(cameraController.isDrawableTowers));
//        cameraController.shapeRenderer.circle(towerPos.x, towerPos.y, 3);
        float sizeCellX = cameraController.sizeCellX;
        float sizeCellY = cameraController.sizeCellY*2;

        TextureRegion burningFrame = null;
        TextureRegion currentFrame = null;
        if (tower.isNotDestroyed()) {
            currentFrame = tower.templateForTower.idleTile.getTextureRegion();
            if (tower.burningAnimation != null) {
                burningFrame = tower.getCurrentBurningFrame();
            }
        } else {
            sizeCellY = cameraController.sizeCellY*1.5f;
            currentFrame = tower.getCurrentDestroyFrame();
        }
        if (!gameSettings.isometric) {
            sizeCellY = cameraController.sizeCellY;
        }
        Vector2 towerPos = new Vector2();
        if (cameraController.isDrawableTowers == 5) {
            for (int m = 1; m < cameraController.isDrawableTowers; m++) {
                towerPos.set(cell.getGraphicCoordinates(m));
                cameraController.getCorrectGraphicTowerCoord(towerPos, towerSize, m);
                cameraController.spriteBatch.draw(currentFrame, towerPos.x, towerPos.y, sizeCellX * towerSize, sizeCellY * towerSize);
                if (burningFrame != null) {
                    cameraController.spriteBatch.draw(burningFrame, towerPos.x, towerPos.y, sizeCellX * towerSize, sizeCellY * towerSize);
                }
//                cameraController.shapeRenderer.circle(towerPos.x, towerPos.y, tower.radiusDetectionCircle.radius/2);
//                cameraController.shapeRenderer.circle(tower.radiusDetectionCircle.x, tower.radiusDetectionCircle.y, tower.radiusDetectionCircle.radius);
            }
        } else if (cameraController.isDrawableTowers != 0) {
            towerPos.set(cell.getGraphicCoordinates(cameraController.isDrawableTowers));
            cameraController.getCorrectGraphicTowerCoord(towerPos, towerSize, cameraController.isDrawableTowers);
            cameraController.spriteBatch.draw(currentFrame, towerPos.x, towerPos.y, sizeCellX * towerSize, sizeCellY * towerSize);
            if (burningFrame != null) {
                cameraController.spriteBatch.draw(burningFrame, towerPos.x, towerPos.y, sizeCellX * towerSize, sizeCellY * towerSize);
            }
//            cameraController.shapeRenderer.circle(towerPos.x, towerPos.y, tower.radiusDetectionCircle.radius/2);
//            cameraController.shapeRenderer.circle(tower.radiusDetectionCircle.x, tower.radiusDetectionCircle.y, tower.radiusDetectionCircle.radius);
        }
        // todo fix this shit 1 || this fix bug with transparent when select tower
        cameraController.spriteBatch.end();
        cameraController.shapeRenderer.end();
        cameraController.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        Color oldColor = cameraController.shapeRenderer.getColor();
        cameraController.shapeRenderer.setColor(Color.WHITE);
        if (cameraController.prevCellX == cell.cellX && cameraController.prevCellY == cell.cellY) {
            cameraController.shapeRenderer.circle(tower.radiusDetectionCircle.x, tower.radiusDetectionCircle.y, tower.radiusDetectionCircle.radius);
        }
        cameraController.shapeRenderer.setColor(oldColor);

        cameraController.shapeRenderer.end();
        cameraController.spriteBatch.begin();
        // todo fix this shit 2
        towerPos = null; // delete towerPos;
    }


    private void drawTowersBars(CameraController cameraController) {
        for (Tower tower : towersManager.towers) {
            if (tower.isNotDestroyed()) {
//                if (cameraController.isDrawableTowers == 1 || cameraController.isDrawableTowers == 5) {
//                    drawTowerBar(cameraController, tower, tower.circle1.x, tower.circle1.y);
//                }
//                if (cameraController.isDrawableTowers == 2 || cameraController.isDrawableTowers == 5) {
//                    drawUnitBar(cameraController, tower, tower.circle2.x, tower.circle2.y);
//                }
//                if (cameraController.isDrawableTowers == 3 || cameraController.isDrawableTowers == 5) {
//                    drawUnitBar(cameraController, tower, tower.circle3.x, tower.circle3.y);
//                }
//                if (cameraController.isDrawableTowers == 4 || cameraController.isDrawableTowers == 5) {
//                    drawUnitBar(cameraController, tower, tower.circle4.x, tower.circle4.y);
//                }
                if (cameraController.isDrawableTowers == 5) {
                    for (int m = 1; m < cameraController.isDrawableTowers; m++) {
                        Circle circle = tower.getCircle(m);
                        drawTowerBar(cameraController, tower, circle.x, circle.y);
                    }
                } else if (cameraController.isDrawableTowers != 0) {
                    Circle circle = tower.getCircle(cameraController.isDrawableTowers);
                    drawTowerBar(cameraController, tower, circle.x, circle.y);
                }
            }
        }
    }

    private void drawTowerBar(CameraController cameraController, Tower tower, float fVx, float fVy) {
        float maxHP = tower.templateForTower.healthPoints;
        float hp = tower.hp;
        if (maxHP != hp) {
            TextureRegion currentFrame = tower.templateForTower.idleTile.getTextureRegion();
            fVx -= cameraController.sizeCellX/2;
            fVy -= cameraController.sizeCellY;
            float currentFrameWidth = currentFrame.getRegionWidth();
            float currentFrameHeight = currentFrame.getRegionHeight();
            float hpBarSpace = 0.8f;
            float hpBarHPWidth = 30f;
            float hpBarHeight = 7f;
            float hpBarWidthIndent = (currentFrameWidth - hpBarHPWidth) / 2;
            float hpBarTopIndent = hpBarHeight;

            cameraController.shapeRenderer.setColor(Color.BLACK);
            cameraController.shapeRenderer.rect(fVx + hpBarWidthIndent, fVy + currentFrameHeight - hpBarTopIndent, hpBarHPWidth, hpBarHeight);
            cameraController.shapeRenderer.setColor(Color.GREEN);

            hpBarHPWidth = hpBarHPWidth / maxHP * hp;
            cameraController.shapeRenderer.rect(fVx + hpBarWidthIndent + hpBarSpace, fVy + currentFrameHeight - hpBarTopIndent + hpBarSpace, hpBarHPWidth - (hpBarSpace * 2), hpBarHeight - (hpBarSpace * 2));
        }
    }

    private void drawBullets(CameraController cameraController) {
        for (Tower tower : towersManager.towers) {
            for (Bullet bullet : tower.bullets) {
                TextureRegion textureRegion = bullet.textureRegion;
                if (textureRegion != null) {
//                float width = textureRegion.getRegionWidth() * bullet.ammoSize;
//                float height = textureRegion.getRegionHeight() * bullet.ammoSize;
//                spriteBatch.draw(textureRegion, bullet.currentPoint.x, bullet.currentPoint.y, width, height);
                    cameraController.spriteBatch.draw(textureRegion, bullet.currentPoint.x - bullet.currCircle.radius, bullet.currentPoint.y - bullet.currCircle.radius, bullet.currCircle.radius * 2, bullet.currCircle.radius * 2);
//                Gdx.app.log("GameField", "drawProjecTiles(); -- Draw bullet:" + bullet.currentPoint);
                }
            }
        }
        for (Unit unit : unitsManager.units) {
            for (UnitBullet bullet : unit.bullets) {
                TextureRegion textureRegion = bullet.textureRegion;
//                Gdx.app.log("GameField::drawBullets()", "-- textureRegion:" + textureRegion);
//                Gdx.app.log("GameField::drawBullets()", "-- Draw bullet:" + bullet.currentPoint);
                if (textureRegion != null) {
                    cameraController.spriteBatch.draw(textureRegion, bullet.currentPoint.x - bullet.currCircle.radius, bullet.currentPoint.y - bullet.currCircle.radius, bullet.currCircle.radius * 2, bullet.currCircle.radius * 2);
                }
            }
        }
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
                        if (cell.removableTerrain) {
                            cameraController.shapeRenderer.getColor().set(255, 0, 0, 100);
                        }
                    } else if (cell.getUnit() != null) {
                        cameraController.shapeRenderer.setColor(Color.GREEN);
                    } else if (cell.getTower() != null) {
                        cameraController.shapeRenderer.setColor(Color.YELLOW);
                    }
//                    if(cameraController.isDrawableGridNav == 1 || cameraController.isDrawableGridNav == 5) {
//                        pos.set(cell.getGraphicCoordinates(1));
//                        cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
//                    }
//                    if(cameraController.isDrawableGridNav == 2 || cameraController.isDrawableGridNav == 5) {
//                        pos.set(cell.getGraphicCoordinates(2));
//                        cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
//                    }
//                    if(cameraController.isDrawableGridNav == 3 || cameraController.isDrawableGridNav == 5) {
//                        pos.set(cell.getGraphicCoordinates(3));
//                        cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
//                    }
//                    if(cameraController.isDrawableGridNav == 4 || cameraController.isDrawableGridNav == 5) {
//                        pos.set(cell.getGraphicCoordinates(4));
//                        cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
//                    }
                    if (cameraController.isDrawableGridNav == 5) {
                        for (int m = 1; m < cameraController.isDrawableGridNav; m++) {
                            pos.set(cell.getGraphicCoordinates(m));
                            cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                        }
                    } else if (cameraController.isDrawableGridNav != 0) {
                        pos.set(cell.getGraphicCoordinates(cameraController.isDrawableGridNav));
                        cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                    }
                }
            }
        }

        Array<GridPoint2> spawnPoints = waveManager.getAllSpawnPoint();
        cameraController.shapeRenderer.setColor(new Color(0f, 255f, 204f, 255f));
        if (!turnedMap) {
            for (GridPoint2 spawnPoint : spawnPoints) {
                Cell cell = field[spawnPoint.x][spawnPoint.y]; // ArrayIndexOutOfBoundsException x==31 because turn x==16 && y==31 // turnedMap kostbIl
                if (cell != null) { // TODO need in turnAndFlip() convert. and this point and other
//                    if (cameraController.isDrawableGridNav == 1 || cameraController.isDrawableGridNav == 5) {
//                        pos.set(cell.getGraphicCoordinates(1));
//                        cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
//                    }
//                    if (cameraController.isDrawableGridNav == 2 || cameraController.isDrawableGridNav == 5) {
//                        pos.set(cell.getGraphicCoordinates(2));
//                        cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
//                    }
//                    if (cameraController.isDrawableGridNav == 3 || cameraController.isDrawableGridNav == 5) {
//                        pos.set(cell.getGraphicCoordinates(3));
//                        cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
//                    }
//                    if (cameraController.isDrawableGridNav == 4 || cameraController.isDrawableGridNav == 5) {
//                        pos.set(cell.getGraphicCoordinates(4));
//                        cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
//                    }
                    if (cameraController.isDrawableGridNav == 5) {
                        for (int m = 1; m < cameraController.isDrawableGridNav; m++) {
                            pos.set(cell.getGraphicCoordinates(m));
                            cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                        }
                    } else if (cameraController.isDrawableGridNav != 0) {
                        pos.set(cell.getGraphicCoordinates(cameraController.isDrawableGridNav));
                        cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                    }
                } else {
                    Gdx.app.log("GameField::drawGridNav()", "-- cell:" + cell + " spawnPoint.x:" + spawnPoint.x + " spawnPoint.y:" + spawnPoint.y);
                }
            }
        }

        Array<GridPoint2> exitPoints = waveManager.getAllExitPoint();
        cameraController.shapeRenderer.setColor(new Color(255f, 0f, 102f, 255f));
        if (!turnedMap) {
            for (GridPoint2 exitPoint : exitPoints) {
//            Gdx.app.log("GameField::drawGridNav()", "-- exitCell.cellX:" + exitCell.cellX + " exitCell.y:" + exitCell.y + " cameraController.isDrawableGridNav:" + cameraController.isDrawableGridNav);
                Cell cell = field[exitPoint.x][exitPoint.y];
//                if (cameraController.isDrawableGridNav == 1 || cameraController.isDrawableGridNav == 5) {
//                    pos.set(cell.getGraphicCoordinates(1));
//                    cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
//                }
//                if (cameraController.isDrawableGridNav == 2 || cameraController.isDrawableGridNav == 5) {
//                    pos.set(cell.getGraphicCoordinates(2));
//                    cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
//                }
//                if (cameraController.isDrawableGridNav == 3 || cameraController.isDrawableGridNav == 5) {
//                    pos.set(cell.getGraphicCoordinates(3));
//                    cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
//                }
//                if (cameraController.isDrawableGridNav == 4 || cameraController.isDrawableGridNav == 5) {
//                    pos.set(cell.getGraphicCoordinates(4));
//                    cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
//                }
                if (cameraController.isDrawableGridNav == 5) {
                    for (int m = 1; m < cameraController.isDrawableGridNav; m++) {
                        pos.set(cell.getGraphicCoordinates(m));
                        cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                    }
                } else if (cameraController.isDrawableGridNav != 0) {
                    pos.set(cell.getGraphicCoordinates(cameraController.isDrawableGridNav));
                    cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                }
            }
        }

        cameraController.shapeRenderer.setColor(Color.ORANGE);
        for (Tower tower : towersManager.towers) {
            for (Bullet bullet : tower.bullets) {
                cameraController.shapeRenderer.rectLine(bullet.currentPoint.x, bullet.currentPoint.y, bullet.endPoint.x, bullet.endPoint.y, cameraController.sizeCellX/40f);
                if (null != bullet.currCircle) {
                    if (bullet.animation == null) {
                        cameraController.shapeRenderer.circle(bullet.currCircle.x, bullet.currCircle.y, bullet.currCircle.radius);
                    }
                }
            }
        }
        for (Unit unit : unitsManager.units) {
            for (UnitBullet bullet : unit.bullets) {
                cameraController.shapeRenderer.rectLine(bullet.currentPoint.x, bullet.currentPoint.y, bullet.endPoint.x, bullet.endPoint.y, cameraController.sizeCellX/40f);
                if (null != bullet.currCircle) {
                    if (bullet.animation == null) {
                        cameraController.shapeRenderer.circle(bullet.currCircle.x, bullet.currCircle.y, bullet.currCircle.radius);
                    }
                }
            }
        }
        cameraController.shapeRenderer.end();

        cameraController.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        cameraController.shapeRenderer.setColor(Color.ORANGE);
        for (Tower tower : towersManager.towers) { // Draw Orange tower.bullets! -- bullet.currCircle
            for (Bullet bullet : tower.bullets) {
                if (null != bullet.currCircle) {
                    if (bullet.animation != null) {
                        cameraController.shapeRenderer.circle(bullet.currCircle.x, bullet.currCircle.y, bullet.currCircle.radius);
                    }
                }
            }
        }

        cameraController.shapeRenderer.setColor(Color.RED);
        for (Unit unit : unitsManager.units) {
//            if(cameraController.isDrawableUnits == 1 || cameraController.isDrawableUnits == 5) {
//                cameraController.shapeRenderer.circle(unit.circle1.x, unit.circle1.y, unit.circle1.radius);
//            }
//            if(cameraController.isDrawableUnits == 2 || cameraController.isDrawableUnits == 5) {
//                cameraController.shapeRenderer.circle(unit.circle2.x, unit.circle2.y, unit.circle2.radius);
//            }
//            if(cameraController.isDrawableUnits == 3 || cameraController.isDrawableUnits == 5) {
//                cameraController.shapeRenderer.circle(unit.circle3.x, unit.circle3.y, unit.circle3.radius);
//            }
//            if(cameraController.isDrawableUnits == 4 || cameraController.isDrawableUnits == 5) {
//                cameraController.shapeRenderer.circle(unit.circle4.x, unit.circle4.y, unit.circle4.radius);
//            }
            if (cameraController.isDrawableUnits == 5) {
                for (int m = 1; m < cameraController.isDrawableUnits; m++) {
                    Circle circle = unit.getCircle(m);
                    cameraController.shapeRenderer.circle(circle.x, circle.y, circle.radius);
                }
            } else if (cameraController.isDrawableUnits != 0) {
                Circle circle = unit.getCircle(cameraController.isDrawableUnits);
                cameraController.shapeRenderer.circle(circle.x, circle.y, circle.radius);
            }
            if (unit.unitAttack != null && unit.unitAttack.circle != null) {
                cameraController.shapeRenderer.circle(unit.unitAttack.circle.x, unit.unitAttack.circle.y, unit.unitAttack.circle.radius);
            }
        }

        cameraController.shapeRenderer.setColor(Color.WHITE);
        for (Tower tower : towersManager.towers) { // Draw white towers radius! -- radiusDetectionCircle
            if (tower.radiusDetectionCircle != null) {
                if (cameraController.isDrawableGridNav == 5) {
                    if (cameraController.isDrawableTowers == 5) {
                        for (int m = 1; m < cameraController.isDrawableTowers; m++) {
                            cameraController.shapeRenderer.circle(tower.radiusDetectionCircle.x, tower.radiusDetectionCircle.y, tower.radiusDetectionCircle.radius);
                        }
                    } else if (cameraController.isDrawableTowers != 0) {
                        cameraController.shapeRenderer.circle(tower.radiusDetectionCircle.x, tower.radiusDetectionCircle.y, tower.radiusDetectionCircle.radius);
                    }
                } else if(cameraController.isDrawableGridNav != 0) {
                    if (cameraController.isDrawableGridNav == cameraController.isDrawableTowers) {
                        cameraController.shapeRenderer.circle(tower.radiusDetectionCircle.x, tower.radiusDetectionCircle.y, tower.radiusDetectionCircle.radius);
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
                            cameraController.shapeRenderer.circle(tower.radiusFlyShellCircle.x, tower.radiusFlyShellCircle.y, tower.radiusFlyShellCircle.radius);
                        }
                    } else if(cameraController.isDrawableTowers != 0) {
                        cameraController.shapeRenderer.circle(tower.radiusFlyShellCircle.x, tower.radiusFlyShellCircle.y, tower.radiusFlyShellCircle.radius);
                    }
                } else if(cameraController.isDrawableGridNav != 0) {
                    if(cameraController.isDrawableGridNav == cameraController.isDrawableTowers) {
                        cameraController.shapeRenderer.circle(tower.radiusFlyShellCircle.x, tower.radiusFlyShellCircle.y, tower.radiusFlyShellCircle.radius);
                    }
                }
            }
        }

        cameraController.shapeRenderer.setColor(Color.YELLOW);
        for (Tower tower : towersManager.towers) { // Draw YELLOW towers overlaps circle!
            if (tower.circles.size != 0) {
                if(cameraController.isDrawableGridNav == 5) {
                    if(cameraController.isDrawableTowers == 5) {
                        for (int m = 1; m <= cameraController.isDrawableTowers; m++) {
                            Circle towerCircle = tower.getCircle(m);
                            cameraController.shapeRenderer.circle(towerCircle.x, towerCircle.y, towerCircle.radius);
                        }
                    } else if(cameraController.isDrawableTowers != 0) {
                        Circle towerCircle = tower.getCircle(cameraController.isDrawableTowers);
                        cameraController.shapeRenderer.circle(towerCircle.x, towerCircle.y, towerCircle.radius);
                    }
                } else if(cameraController.isDrawableGridNav != 0) {
                    if(cameraController.isDrawableGridNav == cameraController.isDrawableTowers) {
                        Circle towerCircle = tower.getCircle(cameraController.isDrawableGridNav);
                        cameraController.shapeRenderer.circle(towerCircle.x, towerCircle.y, towerCircle.radius);
                    }
                }
            }
        }
        cameraController.shapeRenderer.end();

        cameraController.spriteBatch.begin();
        for (Tower tower : towersManager.towers) { // Draw pit capacity value || players ID
            if (tower.templateForTower.towerAttackType == TowerAttackType.Pit) {
                cameraController.bitmapFont.setColor(Color.YELLOW);
                cameraController.bitmapFont.getData().setScale(0.7f);
                if(cameraController.isDrawableGridNav == 5) {
                    if(cameraController.isDrawableTowers == 5) {
                        for (int m = 1; m <= cameraController.isDrawableTowers; m++) {
                            cameraController.bitmapFont.draw(cameraController.spriteBatch, String.valueOf(tower.capacity), tower.centerGraphicCoordinates.x, tower.centerGraphicCoordinates.y);
                        }
                    } else if(cameraController.isDrawableTowers != 0) {
                        cameraController.bitmapFont.draw(cameraController.spriteBatch, String.valueOf(tower.capacity), tower.centerGraphicCoordinates.x, tower.centerGraphicCoordinates.y);
                    }
                } else if(cameraController.isDrawableGridNav != 0) {
                    if(cameraController.isDrawableGridNav == cameraController.isDrawableTowers) {
                        cameraController.bitmapFont.draw(cameraController.spriteBatch, String.valueOf(tower.capacity), tower.centerGraphicCoordinates.x, tower.centerGraphicCoordinates.y);
                    }
                }
            }
            cameraController.bitmapFont.getData().setScale(0.9f);
            if (tower.player == 0) {
                cameraController.bitmapFont.setColor(Color.RED);
            } else if (tower.player == 1) {
                cameraController.bitmapFont.setColor(Color.BLUE);
            }
            if(cameraController.isDrawableGridNav == 5) {
                if(cameraController.isDrawableTowers == 5) {
                    for (int m = 1; m <= cameraController.isDrawableTowers; m++) {
                        cameraController.bitmapFont.draw(cameraController.spriteBatch, String.valueOf(tower.player), tower.centerGraphicCoordinates.x, tower.centerGraphicCoordinates.y);
                    }
                } else if(cameraController.isDrawableTowers != 0) {
                    cameraController.bitmapFont.draw(cameraController.spriteBatch, String.valueOf(tower.player), tower.centerGraphicCoordinates.x, tower.centerGraphicCoordinates.y);
                }
            } else if(cameraController.isDrawableGridNav != 0) {
                if(cameraController.isDrawableGridNav == cameraController.isDrawableTowers) {
                    cameraController.bitmapFont.draw(cameraController.spriteBatch, String.valueOf(tower.player), tower.centerGraphicCoordinates.x, tower.centerGraphicCoordinates.y);
                }
            }
        }
        cameraController.spriteBatch.end();
    }

    private void drawRoutes(CameraController cameraController) {
        cameraController.shapeRenderer.setProjectionMatrix(cameraController.camera.combined);
        cameraController.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float gridNavRadius = cameraController.sizeCellX/22f;
        for (Unit unit : unitsManager.units) {
            if (unit.player == 1) {
                cameraController.shapeRenderer.setColor(Color.WHITE);
            } else {
                cameraController.shapeRenderer.setColor(Color.BROWN); // (100, 60, 21, 1f);
            }
            ArrayDeque<Cell> unitRoute = unit.route;
            if (unitRoute != null && !unitRoute.isEmpty()) {
                for (Cell cell : unitRoute) {
//                    Cell cell = getCell(coor.getX(), coor.getY());
//                    Cell cell = field[coor.getX()][coor.getY()];
                    if (cell != null) {
//                        if (cameraController.isDrawableRoutes == 1 || cameraController.isDrawableRoutes == 5) {
//                            cameraController.shapeRenderer.circle(cell.graphicCoordinates1.x, cell.graphicCoordinates1.y, gridNavRadius);
//                        }
//                        if (cameraController.isDrawableRoutes == 2 || cameraController.isDrawableRoutes == 5) {
//                            cameraController.shapeRenderer.circle(cell.graphicCoordinates2.x, cell.graphicCoordinates2.y, gridNavRadius);
//                        }
//                        if (cameraController.isDrawableRoutes == 3 || cameraController.isDrawableRoutes == 5) {
//                            cameraController.shapeRenderer.circle(cell.graphicCoordinates3.x, cell.graphicCoordinates3.y, gridNavRadius);
//                        }
//                        if (cameraController.isDrawableRoutes == 4 || cameraController.isDrawableRoutes == 5) {
//                            cameraController.shapeRenderer.circle(cell.graphicCoordinates4.x, cell.graphicCoordinates4.y, gridNavRadius);
//                        }
                        Vector2 cellCoord = new Vector2();
                        if (cameraController.isDrawableRoutes == 5) {
                            for (int m = 1; m < cameraController.isDrawableRoutes; m++) {
                                cellCoord.set(cell.getGraphicCoordinates(m));
                                cameraController.shapeRenderer.circle(cellCoord.x, cellCoord.y, gridNavRadius);
                            }
                        } else if (cameraController.isDrawableRoutes != 0) {
                            cellCoord.set(cell.getGraphicCoordinates(cameraController.isDrawableRoutes));
                            cameraController.shapeRenderer.circle(cellCoord.x, cellCoord.y, gridNavRadius);
                        }
                    }
                }
                cameraController.shapeRenderer.setColor(0.756f, 0.329f, 0.756f, 1f);
                Cell cell = unitRoute.getLast();
//                Cell cell = getCell(destinationPoint.getX(), destinationPoint.getY());
                if (cell != null) {
//                    if (cameraController.isDrawableRoutes == 1 || cameraController.isDrawableRoutes == 5) {
//                        cameraController.shapeRenderer.circle(cell.graphicCoordinates1.x, cell.graphicCoordinates1.y, gridNavRadius * 0.7f);
//                    }
//                    if (cameraController.isDrawableRoutes == 2 || cameraController.isDrawableRoutes == 5) {
//                        cameraController.shapeRenderer.circle(cell.graphicCoordinates2.x, cell.graphicCoordinates2.y, gridNavRadius * 0.7f);
//                    }
//                    if (cameraController.isDrawableRoutes == 3 || cameraController.isDrawableRoutes == 5) {
//                        cameraController.shapeRenderer.circle(cell.graphicCoordinates3.x, cell.graphicCoordinates3.y, gridNavRadius * 0.7f);
//                    }
//                    if (cameraController.isDrawableRoutes == 4 || cameraController.isDrawableRoutes == 5) {
//                        cameraController.shapeRenderer.circle(cell.graphicCoordinates4.x, cell.graphicCoordinates4.y, gridNavRadius * 0.7f);
//                    }
                    Vector2 cellCoord = new Vector2();
                    if (cameraController.isDrawableRoutes == 5) {
                        for (int m = 1; m < cameraController.isDrawableRoutes; m++) {
                            cellCoord.set(cell.getGraphicCoordinates(m));
                            cameraController.shapeRenderer.circle(cellCoord.x, cellCoord.y, gridNavRadius * 0.7f);
                        }
                    } else if (cameraController.isDrawableRoutes != 0) {
                        cellCoord.set(cell.getGraphicCoordinates(cameraController.isDrawableRoutes));
                        cameraController.shapeRenderer.circle(cellCoord.x, cellCoord.y, gridNavRadius * 0.7f);
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

    private void drawWave(CameraController cameraController, Wave wave) {
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
//                if(cameraController.isDrawableRoutes == 1 || cameraController.isDrawableRoutes == 5) {
//                    cameraController.shapeRenderer.rectLine(startCell.graphicCoordinates1, endCell.graphicCoordinates1, linesWidth);
//                }
//                if(cameraController.isDrawableRoutes == 2 || cameraController.isDrawableRoutes == 5) {
//                    cameraController.shapeRenderer.rectLine(startCell.graphicCoordinates2, endCell.graphicCoordinates2, linesWidth);
//                }
//                if(cameraController.isDrawableRoutes == 3 || cameraController.isDrawableRoutes == 5) {
//                    cameraController.shapeRenderer.rectLine(startCell.graphicCoordinates3, endCell.graphicCoordinates3, linesWidth);
//                }
//                if(cameraController.isDrawableRoutes == 4 || cameraController.isDrawableRoutes == 5) {
//                    cameraController.shapeRenderer.rectLine(startCell.graphicCoordinates4, endCell.graphicCoordinates4, linesWidth);
//                }
                Vector2 startCellCoord = new Vector2();
                Vector2 endCellCoord = new Vector2();
                if (cameraController.isDrawableRoutes == 5) {
                    for (int m = 1; m < cameraController.isDrawableRoutes; m++) {
                        startCellCoord.set(startCell.getGraphicCoordinates(m));
                        endCellCoord.set(endCell.getGraphicCoordinates(m));
                        cameraController.shapeRenderer.rectLine(startCellCoord, endCellCoord, linesWidth);
                    }
                } else if (cameraController.isDrawableRoutes != 0) {
                    startCellCoord.set(startCell.getGraphicCoordinates(cameraController.isDrawableRoutes));
                    endCellCoord.set(endCell.getGraphicCoordinates(cameraController.isDrawableRoutes));
                    cameraController.shapeRenderer.rectLine(startCellCoord, endCellCoord, linesWidth);
                }
                startNode = endNode;
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
        }
        GridPoint2 startDrawCell = new GridPoint2(startX, startY);
        GridPoint2 finishDrawCell = new GridPoint2(finishX, finishY);
        for (int x = startX; x <= finishX; x++) {
            for (int y = startY; y <= finishY; y++) {
                Cell cell = getCell(buildX + x, buildY + y);
                if(cell == null || !cell.isEmpty()) {
                    if (drawFull) {
                        canBuild = false;
                    }
                }
            }
        }
        if (drawFull) {
            Cell mainCell = getCell(buildX, buildY);
            if(mainCell != null) {
//                Color oldColorSB = cameraController.spriteBatch.getColor();
//                Color oldColorSR = cameraController.shapeRenderer.getColor();
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
                cameraController.spriteBatch.setColor(Color.WHITE);
                cameraController.shapeRenderer.setColor(Color.WHITE);
            }
        }
    }

    private void drawTowerUnderConstructionAndMarks(CameraController cameraController, int map, TemplateForTower templateForTower, Cell mainCell, GridPoint2 startDrawCell, GridPoint2 finishDrawCell) {
//        Gdx.app.log("GameField::drawTowerUnderConstructionAndMarks()", "-- spriteBatch:" + /*spriteBatch +*/ " shapeRenderer:" + /*shapeRenderer +*/ " map:" + map + " templateForTower:" + templateForTower + " mainCell:" + mainCell + " startDrawCell:" + startDrawCell + " finishDrawCell:" + finishDrawCell);
        float sizeCellX = cameraController.sizeCellX;
        float sizeCellY = cameraController.sizeCellY*2;
        if (!gameSettings.isometric) {
            sizeCellY = cameraController.sizeCellY;
        }
        TextureRegion textureRegion = templateForTower.idleTile.getTextureRegion();
        int towerSize = templateForTower.size;
        Vector2 towerPos = new Vector2(mainCell.getGraphicCoordinates(map));
        if (templateForTower.radiusDetection != null) {
            cameraController.shapeRenderer.circle(towerPos.x, towerPos.y, templateForTower.radiusDetection);
        }
        cameraController.getCorrectGraphicTowerCoord(towerPos, towerSize, map);
        cameraController.spriteBatch.draw(textureRegion, towerPos.x, towerPos.y, sizeCellX * towerSize, sizeCellY * towerSize);
//        cameraController.shapeRenderer.circle(towerPos.x, towerPos.y, templateForTower.radiusDetection/4);
        if (greenCheckmark != null && redCross != null) {
            Vector2 markPos = new Vector2();
            for (int x = startDrawCell.x; x <= finishDrawCell.x; x++) {
                for (int y = startDrawCell.y; y <= finishDrawCell.y; y++) {
                    Cell markCell = getCell(mainCell.cellX + x, mainCell.cellY + y);
                    if (markCell != null) {
                        markPos.set(markCell.getGraphicCoordinates(map));
                        markPos.add(-(cameraController.halfSizeCellX), -(cameraController.halfSizeCellY));
                        if(markCell.isEmpty()) {
                            cameraController.spriteBatch.draw(greenCheckmark, markPos.x, markPos.y, sizeCellX, sizeCellY);
                        } else {
                            cameraController.spriteBatch.draw(redCross, markPos.x, markPos.y, sizeCellX, sizeCellY);
                        }
                    }
                }
            }
            markPos = null; // delete markPos;
        }
        towerPos = null; // delete towerPos;
    }

    public Unit spawnUnitFromUser(TemplateForUnit templateForUnit) {
        Gdx.app.log("GameField::spawnUnitFromUser()", "-- templateForUnit:" + templateForUnit);
        if (gamerGold >= templateForUnit.cost) {
            gamerGold -= templateForUnit.cost;
            for (Wave wave : waveManager.wavesForUser) {
                Cell spawnCell = getCell(wave.spawnPoint.x, wave.spawnPoint.y);
                Cell destExitCell = getCell(wave.exitPoint.x, wave.exitPoint.y);
                return createUnit(spawnCell, destExitCell, templateForUnit, 1, destExitCell); // create Player1 Unit
            }
        }
        return null;
    }

    private void spawnUnits(float delta) {
        if (unitsSpawn) {
            if (waveManager.allTogether) {
                Array<WaveManager.TemplateNameAndPoints> allUnitsForSpawn = waveManager.getAllUnitsForSpawn(delta);
                for (WaveManager.TemplateNameAndPoints templateNameAndPoints : allUnitsForSpawn) {
                    spawnUnit(templateNameAndPoints);
                }
            } else {
                if (waveManager.currentWave == null) {
                    if (unitsManager.units.size == 0) {
                        if (!waveManager.updateCurrentWave()) {
                            unitsSpawn = false;
                        }
                    } else {
                        unitsSpawn = false;
                    }
                } else {
                    WaveManager.TemplateNameAndPoints templateNameAndPoints = waveManager.getUnitForSpawn(delta);
                    if (templateNameAndPoints != null) {
                        spawnUnit(templateNameAndPoints);
                    }
                }
            }
        }
    }

    private Unit spawnUnit(WaveManager.TemplateNameAndPoints templateNameAndPoints) {
        if (templateNameAndPoints != null) {
            TemplateForUnit templateForUnit = factionsManager.getTemplateForUnitByName(templateNameAndPoints.templateName);
            if (templateForUnit != null) {
                Cell spawnCell = getCell(templateNameAndPoints.spawnPoint.x, templateNameAndPoints.spawnPoint.y);
                Cell destExitCell = getCell(templateNameAndPoints.exitPoint.x, templateNameAndPoints.exitPoint.y);
                return createUnit(spawnCell, destExitCell, templateForUnit, 0, destExitCell); // create Computer0 Unit
            } else {
                Gdx.app.error("GameField::spawnUnit()", "-- templateForUnit == null | templateName:" + templateNameAndPoints.templateName);
            }
        }
        return null;
    }

    private Unit spawnHeroInSpawnPoint() {
        Gdx.app.log("GameField::spawnHeroInSpawnPoint()", "-- gameSettings.cellExitHero:" + gameSettings.cellExitHero + " gameSettings.cellSpawnHero:" + gameSettings.cellSpawnHero);
        if (gameSettings.cellSpawnHero != null && gameSettings.cellExitHero != null) {
            gameSettings.cellSpawnHero.removeTerrain(true);
            gameSettings.cellExitHero.removeTerrain(true);
            removeTower(gameSettings.cellSpawnHero.cellX, gameSettings.cellSpawnHero.cellY);
            removeTower(gameSettings.cellExitHero.cellX, gameSettings.cellExitHero.cellY);
            return createUnit(gameSettings.cellSpawnHero, gameSettings.cellExitHero, factionsManager.getTemplateForUnitByName("unit3_footman"), 1, gameSettings.cellExitHero); // player1 = hero
        }
        return null;
    }

    public Unit spawnHero(int cellX, int cellY) {
        Gdx.app.log("GameField::spawnHero()", "-- cellX:" + cellX + " cellY:" + cellY);
        Gdx.app.log("GameField::spawnHero()", "-- gameSettings.cellExitHero:" + gameSettings.cellExitHero);
        if (gameSettings.cellExitHero != null) {
            Cell cell = getCell(cellX, cellY);
            if (cell != null) {
                cell.removeTerrain(true);
                removeTower(cell.cellX, cell.cellY);
                return createUnit(cell, cell, factionsManager.getTemplateForUnitByName("unit3_footman"), 1, gameSettings.cellExitHero); // player1 = hero
            }
        } else {
            int randomX = (int)(Math.random()*map.width);
            int randomY = (int)(Math.random()*map.height);
            gameSettings.cellExitHero = getCell(randomX, randomY);
            Unit hero = spawnHero(cellX, cellY);
            if (hero == null) {
                gameSettings.cellExitHero = null;
            }
        }
        return null;
    }

    public Unit spawnCompUnitToRandomExit(int x, int y) {
        Gdx.app.log("GameField::spawnCompUnitToRandomExit()", "-- x:" + x + " y:" + y);
        int randomX = (int)(Math.random()*map.width);
        int randomY = (int)(Math.random()*map.height);
        Gdx.app.log("GameField::spawnCompUnitToRandomExit()", "-- randomX:" + randomX + " randomY:" + randomY);
        return createUnit(getCell(x, y), getCell(randomX, randomY), factionsManager.getRandomTemplateForUnitFromSecondFaction(), 0, null);
    }

    private Unit createUnit(Cell spawnCell, Cell destCell, TemplateForUnit templateForUnit, int player, Cell exitCell) {
//        Gdx.app.log("GameField::createUnit()", "-- spawnCell:" + spawnCell);
//        Gdx.app.log("GameField::createUnit()", "-- destCell:" + destCell);
//        Gdx.app.log("GameField::createUnit()", "-- templateForUnit:" + templateForUnit.toString());
//        Gdx.app.log("GameField::createUnit()", "-- player:" + player);
//        Gdx.app.log("GameField::createUnit()", "-- exitCell:" + exitCell);
//        if (destCell == null) {
//            destCell = waveManager.lastExitCell;
//        }
        Unit unit = null;
        if (spawnCell != null && destCell != null && pathFinder != null) {
//            pathFinder.loadCharMatrix(getCharMatrix());
            ArrayDeque<Cell> route = pathFinder.route(spawnCell.cellX, spawnCell.cellY, destCell.cellX, destCell.cellY);
            if (route != null) {
                unit = unitsManager.createUnit(route, templateForUnit, player, exitCell);
                spawnCell.setUnit(unit);
                Gdx.app.log("GameField::createUnit()", "-- unit:" + unit);
            } else {
                Gdx.app.log("GameField::createUnit()", "-- Not found route for createUnit!");
//                if(towersManager.towers.size > 0) {
//                    Gdx.app.log("GameField::createUnit()", "-- Remove one last tower! And retry call createUnit()");
//                    removeLastTower();
//                    unit = createUnit(spawnCell, destCell, templateForUnit, player, exitCell);
//                } else {
                return null;
//                }
            }
            return unit;
        } else {
            Gdx.app.log("GameField::createUnit()", "-- Bad spawnCell:" + spawnCell + " || destCell:" + destCell + " || pathFinder:" + pathFinder);
            return null;
        }
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

    public boolean towerActions(int x, int y) {
        Cell cell = getCell(x, y);
        if (cell != null) {
            if (cell.isEmpty()) {
                createTowerWithGoldCheck(x, y, factionsManager.getRandomTemplateForTowerFromAllFaction(), 1);
                rerouteAllUnits();
                return true;
            } else if (cell.getTower() != null) {
                removeTowerWithGold(x, y);
                return true;
            }
        }
        return false;
    }

    public void buildTowersWithUnderConstruction(int x, int y) {
        if (underConstruction != null) {
            underConstruction.setEndCoors(x, y);
            createTowerWithGoldCheck(underConstruction.startX, underConstruction.startY, underConstruction.templateForTower, 1);
            for (int k = 0; k < underConstruction.coorsX.size; k++) {
//            for(int k = underConstruction.coorsX.size-1; k >= 0; k--) {
                createTowerWithGoldCheck(underConstruction.coorsX.get(k), underConstruction.coorsY.get(k), underConstruction.templateForTower, 1);
            }
            underConstruction.clearStartCoors();
            rerouteAllUnits();
        }
    }

    public Tower createTowerWithGoldCheck(int buildX, int buildY, TemplateForTower templateForTower, int player) {
        if (gamerGold >= templateForTower.cost) {
            Tower tower = createTower(buildX, buildY, templateForTower, player);
//            rerouteForAllUnits();
            gamerGold -= templateForTower.cost;
            Gdx.app.log("GameField::createTowerWithGoldCheck()", "-- Now gamerGold:" + gamerGold);
            return tower;
        } else {
            return null;
        }
    }

    public Tower createTower(int buildX, int buildY, TemplateForTower templateForTower, int player) {
        if (templateForTower != null) {
            int towerSize = templateForTower.size;
            int startX = 0, startY = 0, finishX = 0, finishY = 0;
            if (towerSize != 1) {
                if (towerSize % 2 == 0) {
                    startX = -(towerSize / 2);
                    startY = -(towerSize / 2);
                    finishX = (towerSize / 2) - 1;
                    finishY = (towerSize / 2) - 1;
                } else {
                    startX = -(towerSize / 2);
                    startY = -(towerSize / 2);
                    finishX = (towerSize / 2);
                    finishY = (towerSize / 2);
                }
            }
            for (int tmpX = startX; tmpX <= finishX; tmpX++) {
                for (int tmpY = startY; tmpY <= finishY; tmpY++) {
                    Cell cell = getCell(buildX + tmpX, buildY + tmpY);
                    if (cell == null || !cell.isEmpty()) {
                        return null;
                    }
                }
            }

            Cell cell = getCell(buildX, buildY);
            Tower tower = towersManager.createTower(cell, templateForTower, player);
            if (templateForTower.towerAttackType != TowerAttackType.Pit) {
                for (int tmpX = startX; tmpX <= finishX; tmpX++) {
                    for (int tmpY = startY; tmpY <= finishY; tmpY++) {
                        field[buildX + tmpX][buildY + tmpY].setTower(tower);
                        if (pathFinder != null) {
                            pathFinder.nodeMatrix[buildY + tmpY][buildX + tmpX].setKey('T');
                        }
                    }
                }
            }
            Gdx.app.log("GameField::createTower()", "-- tower:" + tower.toString(true));
            return tower;
        }
        return null;
    }

    public boolean removeLastTower() {
        Tower tower = towersManager.getTower(); // towersManager.towers.size - 1);
        if (tower != null) {
            Cell pos = tower.cell;
            return ( (removeTower(pos.cellX, pos.cellY)==0)?false:true );
        }
        return false;
    }

    public boolean removeTowerWithGold(int cellX, int cellY) {
        int towerCost = removeTower(cellX, cellY);
        if (towerCost > 0) {
//            rerouteForAllUnits();
            gamerGold += towerCost; // *0.5;
            Gdx.app.log("GameField::removeTowerWithGold()", "-- Now gamerGold:" + gamerGold);
            return true;
        }
        return false;
    }

    public int removeTower(int cellX, int cellY) {
        Tower tower = field[cellX][cellY].getTower();
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
                    if (pathFinder != null) {
                        pathFinder.getNodeMatrix()[y + tmpY][x + tmpX].setKey('.');
                    }
                }
            }
            for (Unit unit : tower.whoAttackMe) { // need change i think
                if (unit != null) {
                    unit.towerAttack = null;
                    if (unit.unitAttack != null) {
                        unit.unitAttack.attacked = false;
                    }
                }
            }
            int towerCost = tower.templateForTower.cost;
            towersManager.removeTower(tower);
            return towerCost;
        }
        return 0;
    }

//    public void updateHeroDestinationPoint() {
//        Gdx.app.log("GameField::updateHeroDestinationPoint()", "-- ");
////    Unit* hero = unitsManager.hero;
//        for (Unit hero : unitsManager.hero) {
//            Gdx.app.log("GameField::updateHeroDestinationPoint()", "-- hero:" + hero);
//            if (hero != null && !hero.route.isEmpty()) {
//                updateHeroDestinationPoint(hero.route.getFirst().getX(), hero.route.getFirst().getY());
//            } else {
////                updatePathFinderWalls();
//            }
//        }
//    }
//
//    public void updateHeroDestinationPoint(int x, int y) {
//        updatePathFinderWalls();
////    Unit* hero = unitsManager.hero;
//        for (Unit hero : unitsManager.hero) {
//            Node old = hero.currentCell;
//            Node pos = hero.nextCell;
//            Gdx.app.log("GameField::updateHeroDestinationPoint()", "-- old:" + old);
//            Gdx.app.log("GameField::updateHeroDestinationPoint()", "-- pos:" + pos);
//            if (pos != null) {
//                hero.route = pathFinder.route(hero.nextCell.getX(), hero.nextCell.getY(), x, y);
//                Gdx.app.log("GameField::updateHeroDestinationPoint()", "-- hero.route:" + hero.route);
//                if (hero.route != null && !hero.route.isEmpty()) {
//                    hero.route.removeFirst();
//                }
//            }
//        }
//    }

    public void updatePathFinderWalls() {
        Gdx.app.log("GameField::updatePathFinderWalls()", "-start- pathFinder.walls.size():" + pathFinder.nodeMatrix.length);
//        pathFinder.clearCollisions();
        pathFinder.loadCharMatrix(getCharMatrix());
        Gdx.app.log("GameField::updatePathFinderWalls()", "-end- pathFinder.walls.size():" + pathFinder.nodeMatrix.length);
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

//void GameField::updateHeroDestinationPoint() {
//    qDebug() << "GameField::updateHeroDestinationPoint(); -- ";
////    Unit* hero = unitsManager->hero;
//    foreach (Unit* hero, unitsManager->hero) {
//        qDebug() << "GameField::updateHeroDestinationPoint(); -- hero:" << hero;
//        if (hero != NULL && !hero->route.empty()) {
//            updateHeroDestinationPoint(hero->route.front().x, hero->route.front().y);
//        } else {
//            updatePathFinderWalls();
//        }
//    }
//}

    public void rerouteHero() {
        rerouteHero(-1, -1);
    }

    public void rerouteHero(int x, int y) {
        if (x == -1 && y == -1) {
            for (Unit unit : unitsManager.hero) {
                ArrayDeque<Cell> route = unit.route;
                if (route != null && !route.isEmpty()) {
                    Cell node = route.getLast();
                    rerouteUnitPath(unit, node.cellX, node.cellY);
                }
            }
        } else {
            for (Unit unit : unitsManager.hero) {
                rerouteUnitPath(unit, x, y);
            }
        }
    }

    public void rerouteAllUnits() {
        rerouteAllUnits(-1, -1);
    }

    public void rerouteAllUnits(int x, int y) {
        if (x == -1 && y == -1) {
            for (Unit unit : unitsManager.units) {
                ArrayDeque<Cell> route = unit.route;
                if (route != null && !route.isEmpty()) {
                    Cell node = route.getLast();
                    rerouteUnitPath(unit, node.cellX, node.cellY);
                }
            }
        } else {
            for (Unit unit : unitsManager.units) {
                rerouteUnitPath(unit, x, y);
            }
        }
    }

    private void rerouteUnitPath(Unit unit, int x, int y) {
        ArrayDeque<Cell> route = pathFinder.route(unit.nextCell.cellX, unit.nextCell.cellY, x, y);
        if (route != null && !route.isEmpty()) {
//            if (route.front().equals({x, y})) {
            route.removeFirst();
            unit.route = route;
//            } else {
//                unit->route.clear();
//            }
        }
    }

//    public void rerouteForAllUnits() {
//        rerouteForAllUnits(null);
//    }
//
//    public void rerouteForAllUnits(GridPoint2 exitPoint) {
//        if (pathFinder != null) {
//            long start = System.nanoTime();
//            Gdx.app.log("GameField::rerouteForAllUnits()", "-- Start:" + start);
////            pathFinder.loadCharMatrix(getCharMatrix());
//            for (Unit unit : unitsManager.units) {
//                ArrayDeque<Node> route;
//                if (exitPoint == null) {
//                    route = unit.route;
//                    if(route != null && route.size() > 0) {
//                        Node node = unit.route.getLast();
//                        GridPoint2 localExitPoint = new GridPoint2(node.getX(), node.getY());
//                        route = pathFinder.route(unit.nextCell.getX(), unit.nextCell.getY(), localExitPoint.x, localExitPoint.y); // TODO BAGA!
//                    }
//                } else {
//                    route = pathFinder.route(unit.nextCell.getX(), unit.nextCell.getY(), exitPoint.x, exitPoint.y); // TODO BAGA!
//                }
//                if (route != null && !route.isEmpty()) {
//                    route.removeFirst();
//                    unit.route = route;
//                }
////                    long end2 = System.nanoTime();
////                    Gdx.app.log("GameField", "rerouteForAllUnits()', "-- Thread End:" + (end2-start2));
////                }
////            }.init(unit, outExitPoint)).start();
//            }
//            long end = System.nanoTime();
//            Gdx.app.log("GameField::rerouteForAllUnits()", "-- End:" + end + " Delta time:" + (end-start));
//        } else {
//            Gdx.app.log("GameField::rerouteForAllUnits(" + exitPoint + ")", "-- pathFinder:" + pathFinder);
//        }
//    }

    private void stepAllUnits(float delta, CameraController cameraController) {
        for (Unit unit : unitsManager.units) {
            Cell oldCurrentCell = unit.currentCell;
            Cell nextCurrentCell = unit.nextCell;
            if (unit.isAlive()) {
                unit.shellEffectsMove(delta);

                if (!unit.tryAttackTower(delta, cameraController)) {
//                    if (!unit.unitAttack.attacked && unit.towerAttack == null) {
//                    if (unit.unitAttack == null || (!unit.unitAttack.attacked && unit.towerAttack == null) ) {
                        Cell currentCell = unit.move(delta, cameraController);
                        if (currentCell != null) {
                            if (!currentCell.equals(oldCurrentCell)) { // ?? currentCell == oldCurrentCell ??
                                if (oldCurrentCell != null && currentCell != null) {
                                    oldCurrentCell.removeUnit(unit);
                                    currentCell.setUnit(unit);
//                                Gdx.app.log("GameField::stepAllUnits()", "-- Unit move to X:" + nextCell.getX() + " Y:" + nextCell.getY());
                                } else {
                                    Gdx.app.error("GameField::stepAllUnits()", "-- Unit bad! Cells:oldCurrentCell:" + oldCurrentCell + " currentCell:" + currentCell + " nextCurrentCell:" + nextCurrentCell);
                                }
                            }
                        } else {
                            Cell cell = oldCurrentCell;
                            if (unit.player == 1) {
                                gameSettings.missedUnitsForComputer0++;
                            } else if (unit.player == 0) {
                                if (unit.exitCell == nextCurrentCell) { // ?? unit.exitCell.equals(cell) ?? // hueta! change plz Nikita!
                                    gameSettings.missedUnitsForPlayer1++;
                                    if (cell != null) {
                                        cell.removeUnit(unit);
                                    }
                                    unitsManager.removeUnit(unit);
                                    Gdx.app.log("GameField::stepAllUnits()", "-- unitsManager.removeUnit(tower):");
                                } else {
                                    if (unit.route == null || unit.route.isEmpty()) {
                                        int randomX = (int) (Math.random() * map.width);
                                        int randomY = (int) (Math.random() * map.height);
                                        unit.route = pathFinder.route(nextCurrentCell.cellX, nextCurrentCell.cellY, randomX, randomY); // nextCurrentCell -?- currentCell
                                        if (unit.route != null && !unit.route.isEmpty()) {
                                            unit.route.removeFirst();
//                                        unit.route.removeLast();
                                        }
//                                    Gdx.app.log("GameField::stepAllUnits()", "-- new unit.route:" + tower.route);
                                    }
                                }
//                            Cell* cell = getCell(currentCell.x, currentCell.y);
//                            if (cell->isTerrain()) {
//                                cell->removeTerrain(true);
//                                updatePathFinderWalls();
//                            }
                            }
                        }
//                    }
                }
            } else {
//            if (!unit.isAlive()) {
                if (!unit.changeDeathFrame(delta)) {
                    oldCurrentCell.removeUnit(unit);
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
//        updateTowersGraphicCoordinates(cameraController);
        for (Tower tower : towersManager.towers) {
            tower.updateCenterGraphicCoordinates(cameraController);
            tower.moveAnimations(delta);
            if (tower.isNotDestroyed()) {
                TowerAttackType towerAttackType = tower.templateForTower.towerAttackType;
                if (towerAttackType == TowerAttackType.Pit) {
                    checkPitTower(tower);
                } else if (towerAttackType == TowerAttackType.Melee) {
                    shotMeleeTower(tower);
                } else if (towerAttackType == TowerAttackType.Range || towerAttackType == TowerAttackType.RangeFly) {
                    if (tower.recharge(delta)) {
                        for (Unit unit : unitsManager.units) {
                            if (unit != null && unit.isAlive() && unit.player != tower.player) {
                                if ((unit.templateForUnit.type.equals("fly") && towerAttackType == TowerAttackType.RangeFly) ||
                                        (!unit.templateForUnit.type.equals("fly") && towerAttackType == TowerAttackType.Range)) {
//                                    Gdx.app.log("GameField::shotAllTowers()", "-- tower.radiusDetectionCircle:" + tower.radiusDetectionCircle);
                                    if (tower.radiusDetectionCircle != null) {
                                        Circle unitCircle = unit.getCircle(cameraController.isDrawableUnits);
                                        if (unitCircle != null) {
                                            if (tower.radiusDetectionCircle.overlaps(unitCircle)) {
                                                if (tower.shoot(unit, cameraController)) {
                                                    if (tower.templateForTower.towerShellType != TowerShellType.MassAddEffect) {
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (towerAttackType == TowerAttackType.FireBall) {
                    if (tower.recharge(delta)) {
                        tower.shotFireBall(cameraController);
                    }
                }
            } else {
                if (!tower.changeDestroyFrame(delta)) {
                    removeTowerWithGold(tower.cell.cellX, tower.cell.cellY);
                    continue;
                }
            }
        }
    }

    private boolean checkPitTower(Tower tower) {
        Unit unit = field[tower.cell.cellX][tower.cell.cellY].getUnit();
        if (unit != null && !unit.templateForUnit.type.equals("fly") && unit.player != tower.player) {
            Gdx.app.log("GameField::shotAllTowers()", "-- tower.capacity:" + tower.capacity + " unit.getHp:" + unit.hp);
//            unit.die(unit.getHp());
            unitsManager.removeUnit(unit);
            field[tower.cell.cellX][tower.cell.cellY].removeUnit(unit);
            tower.capacity--;
            if (tower.capacity <= 0) {
                towersManager.removeTower(tower);
            }
            return true;
        }
        return false;
    }

    private boolean shotMeleeTower(Tower tower) {
        boolean attack = false;
        Cell towerCell = tower.cell;
        int radius = Math.round(tower.templateForTower.radiusDetection);
        for (int tmpX = -radius; tmpX <= radius; tmpX++) {
            for (int tmpY = -radius; tmpY <= radius; tmpY++) {
                Cell cell = getCell(tmpX + towerCell.cellX, tmpY + towerCell.cellY);
                if (cell != null && cell.getUnit() != null) {
                    attack = true;
                    Unit unit = cell.getUnit();
                    if (unit != null && !unit.templateForUnit.type.equals("fly") && unit.player != tower.player) {
                        if (unit.die(tower.templateForTower.damage, tower.templateForTower.towerShellEffect)) {
                            gamerGold += unit.templateForUnit.bounty;
                        }
                        if (tower.templateForTower.towerShellType == TowerShellType.SingleTarget) {
                            return true;
                        }
                    }
                }
            }
        }
        return attack;
    }

    private void moveAllShells(float delta, CameraController cameraController) {
        for (Tower tower : towersManager.towers) {
            tower.moveAllShells(delta, cameraController);
        }
        for (Unit unit : unitsManager.units) {
            unit.moveAllShells(delta, cameraController);
        }
    }

    public int getNumberOfUnits() {
        return waveManager.getNumberOfActions() + unitsManager.units.size;
    }

    public GameState getGameState() {
//        Gdx.app.log("GameField::getGameState()", "-- missedUnitsForPlayer1:" + gameSettings.missedUnitsForPlayer1);
//        Gdx.app.log("GameField::getGameState()", "-- maxOfMissedUnitsForPlayer1:" + gameSettings.maxOfMissedUnitsForPlayer1);
//        Gdx.app.log("GameField::getGameState()", "-- missedUnitsForComputer0:" + gameSettings.missedUnitsForComputer0);
//        Gdx.app.log("GameField::getGameState()", "-- maxOfMissedUnitsForComputer0:" + gameSettings.maxOfMissedUnitsForComputer0);
//        Gdx.app.log("GameField::getGameState()", "-- waveManager.getNumberOfActions():" + waveManager.getNumberOfActions());
//        Gdx.app.log("GameField::getGameState()", "-- unitsManager.units.size:" + unitsManager.units.size);
//        Gdx.app.log("GameField::getGameState()", "-- gameSettings.gameType:" + gameSettings.gameType);
        if (gameSettings.gameType == GameType.LittleGame) {
            for (Unit hero : unitsManager.hero) {
                Cell pos = hero.nextCell;
                if (pos != null) {
                    if (pos.cellX == hero.exitCell.cellX && pos.cellY == hero.exitCell.cellY) {
                        Gdx.app.log("GameField::getGameState()", "-- hero.nextCell:" + hero.nextCell);
                        Gdx.app.log("GameField::getGameState()", "-- hero.exitCell:" + hero.exitCell);
                        return GameState.WIN;//"LittleGame_Win";
                    }
                } else {
                    return GameState.LOSE;//"LittleGame_WTF";
                }
            }
        } else if (gameSettings.gameType == GameType.TowerDefence) {
            if (gameSettings.missedUnitsForPlayer1 >= gameSettings.maxOfMissedUnitsForPlayer1) {
//                Gdx.app.log("GameField::getGameState()", "-- LOSE!!");
                return GameState.LOSE;
            } else {
                if (gameSettings.missedUnitsForComputer0 >= gameSettings.maxOfMissedUnitsForComputer0) { // При инициализации если в карте не было голды игроку. и у игрока изначально было 0 голды. то он сразу же выиграет
//                    Gdx.app.log("GameField::getGameState()", "-- WIN!!");
                    return GameState.WIN;
                }
                if (waveManager.getNumberOfActions() == 0 && unitsManager.units.size == 0) {
//                    Gdx.app.log("GameField::getGameState()", "-- WIN!!");
                    return GameState.WIN;
                }
            }
        } else {
            Gdx.app.log("GameField::getGameState()", "-bad- gameSettings.gameType:" + gameSettings.gameType);
        }
//        Gdx.app.log("GameField::getGameState()", "-- IN PROGRESS!!");
        return GameState.IN_PROGRESS;
    }

    public void turnRight() {
        map.setSize(map.height, map.width); // flip size
        Cell[][] newCells = new Cell[field[0].length][field.length]; // flip array size
        int y2 = map.width - 1;
        for(int y = 0; y < map.width; y++) {
            for(int x = 0; x < map.height; x++) {
                newCells[y2][x] = field[x][y];
                newCells[y2][x].setGraphicCoordinates(y2, x, map.tileWidth, map.tileHeight, gameSettings.isometric);
            }
            y2--;
        }
        // delete field; // TODO need this make or not?
        field = newCells;
        turnedMap = !turnedMap;
    }

    public void turnLeft() {
        map.setSize(map.height, map.width); // flip size
        Cell[][] newCells = new Cell[field[0].length][field.length]; // flip array size
        int x2 = map.height - 1;
        for(int x = 0; x < map.height; x++) {
            for(int y = 0; y < map.width; y++) {
                newCells[y][x2] = field[x][y];
                newCells[y][x2].setGraphicCoordinates(y, x2, map.tileWidth, map.tileHeight, gameSettings.isometric);
            }
            x2--;
        }
        // delete field;
        field = newCells;
        turnedMap = !turnedMap;
    }

    /**
     * Flips cells array by X axis.
     */
    public void flipX() {
        Cell[][] newCells = new Cell[map.width][map.height];
        int x2 = map.width - 1;
        for (int x = 0; x < map.width; x++) {
            for (int y = 0; y < map.height; y++) {
                newCells[x][y] = field[x2][y];
                newCells[x][y].setGraphicCoordinates(x, y, map.tileWidth, map.tileHeight, gameSettings.isometric);
            }
            x2--;
        }
        // delete field;
        field = newCells;
    }

    /**
     * Flips cells array by Y axis.
     */
    public void flipY() {
        Cell[][] newCells = new Cell[map.width][map.height];
        int y2 = map.height - 1;
        for(int y = 0; y < map.height; y++) {
            for(int x = 0; x < map.width; x++) {
                newCells[x][y] = field[x][y2];
                newCells[x][y].setGraphicCoordinates(x, y, map.tileWidth, map.tileHeight, gameSettings.isometric);
            }
            y2--;
        }
        // delete field;
        field = newCells;
    }

    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("GameField[");
        sb.append("gamerGold:" + gamerGold);
        sb.append(",gamePaused:" + gamePaused);
        sb.append(",gameSpeed:" + gameSpeed);
        sb.append(",timeOfGame:" + timeOfGame);
        if (full) {
            sb.append(",underConstruction:" + underConstruction);
            sb.append(",pathFinder:" + pathFinder);
            sb.append(",field.length:" + ( (field!=null) ? field.length : "null"));
            sb.append(",map:" + map);
            sb.append(",gameSettings:" + gameSettings);
            sb.append(",unitsManager:" + unitsManager);
            sb.append(",towersManager:" + towersManager);
            sb.append(",waveManager:" + waveManager);
            sb.append(",factionsManager:" + factionsManager);
        }
        sb.append("]");
        return sb.toString();
    }
}
