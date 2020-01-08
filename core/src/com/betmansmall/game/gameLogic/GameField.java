package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.enums.GameState;
import com.betmansmall.game.Player;
import com.betmansmall.screens.client.GameScreen;
import com.betmansmall.game.GameSettings;
import com.betmansmall.enums.GameType;
import com.betmansmall.maps.TmxMap;
import com.betmansmall.maps.MapLoader;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.PathFinder.PathFinder;
import com.betmansmall.game.gameLogic.playerTemplates.FactionsManager;
import com.betmansmall.game.gameLogic.playerTemplates.TowerShellType;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;
import com.betmansmall.game.gameLogic.playerTemplates.TowerAttackType;
import com.betmansmall.server.data.CreateUnitData;
import com.betmansmall.server.data.GameFieldVariablesData;
import com.betmansmall.server.data.UnitInstanceData;
import com.betmansmall.server.data.UnitsManagerData;
import com.betmansmall.util.logging.Logger;

import java.util.ArrayDeque;
import java.util.Random;

/**
 * Created by betmansmall on 08.02.2016.
 */
public class GameField {
    public GameScreen gameScreen;
    public GameSettings gameSettings;
    public FactionsManager factionsManager;
    public WaveManager waveManager; // ALL public for all || we are friendly :)
    public TowersManager towersManager;
    public UnitsManager unitsManager; // For Bullet
    public TmxMap tmxMap;
    private Random random;
    private Cell[][] field;
    private PathFinder pathFinder;
    private UnderConstruction underConstruction;

    // GAME INTERFACE ZONE1
    public float timeOfGame;
    public float gameSpeed;
    public boolean gamePaused;
    public boolean unitsSpawn;

    public GameField(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.gameSettings = gameScreen.game.sessionSettings.gameSettings;
        this.factionsManager = gameScreen.game.factionsManager;
        this.waveManager = new WaveManager(gameSettings.mapPath);
        this.towersManager = new TowersManager();
        this.unitsManager = new UnitsManager();

        tmxMap = (TmxMap)new MapLoader().load(gameSettings.mapPath);
        Gdx.app.log("GameField::GameField()", "-- tmxMap:" + tmxMap);
        random = new Random();

        createField();
        if (tmxMap.isometric) {
            flipY();
        }
        landscapeGenerator(tmxMap.mapPath);
        pathFinder = new PathFinder(this);
        pathFinder.loadCharMatrix(getCharMatrix(false));
        Gdx.app.log("GameField::GameField()", "-- pathFinder:" + pathFinder);

        underConstruction = null;

        timeOfGame = 0.0f;
        gameSpeed = 1.0f;
        gamePaused = false;
        unitsSpawn = false;

        Gdx.app.log("GameField::GameField()", "-- gameSettings.gameType:" + gameSettings.gameType);
        if (gameSettings.gameType == GameType.LittleGame) {
            int randomEnemyCount = gameSettings.enemyCount;
            Gdx.app.log("GameField::GameField()", "-- randomEnemyCount:" + randomEnemyCount);
            for (int k = 0; k < randomEnemyCount; k++) {
                int randomX = random.nextInt(tmxMap.width);
                int randomY = random.nextInt(tmxMap.height);
                Gdx.app.log("GameField::GameField()", "-- k:" + k);
                Gdx.app.log("GameField::GameField()", "-- randomX:" + randomX);
                Gdx.app.log("GameField::GameField()", "-- randomY:" + randomY);
                if (getCell(randomX, randomY).isEmpty()) {
                    if (spawnServerUnitToRandomExit(randomX, randomY) == null) {
                        k--;
                    }
                } else {
                    k--;
                }
            }
            int randomTowerCount = gameSettings.towersCount;
            Gdx.app.log("GameField::GameField()", "-- randomTowerCount:" + randomTowerCount);
            for (int k = 0; k < randomTowerCount; k++) {
                int randomX = random.nextInt(tmxMap.width);
                int randomY = random.nextInt(tmxMap.height);
                Gdx.app.log("GameField::GameField()", "-- k:" + k);
                Gdx.app.log("GameField::GameField()", "-- randomX:" + randomX);
                Gdx.app.log("GameField::GameField()", "-- randomY:" + randomY);
                if (getCell(randomX, randomY).isEmpty()) {
                    if (createTower(randomX, randomY, factionsManager.getRandomTemplateForTowerFromAllFaction(), null) == null) {
                        k--;
                    }
                } else {
                    k--;
                }
            }
            spawnHeroInSpawnPoint();
        } else if (gameSettings.gameType == GameType.TowerDefence) {
            waveManager.validationPoints(field, tmxMap.width, tmxMap.height);
            if (waveManager.waves.size == 0) {
                for (int w = 0; w < 10; w++) {
                    GridPoint2 spawnPoint = new GridPoint2(random.nextInt(tmxMap.width), random.nextInt(tmxMap.height));
                    GridPoint2 exitPoint = new GridPoint2(random.nextInt(tmxMap.width), random.nextInt(tmxMap.height));
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
        } else {
            Gdx.app.log("GameField::GameField()", "-- gameSettings.gameType:" + gameSettings.gameType);
        }
        Gdx.app.log("GameField::GameField()", "-end-");
    }

    public void dispose() {
        Gdx.app.log("GameField::dispose()", "-- Called!");
//        gameSettings.dispose();
//        factionsManager.dispose();
        waveManager.dispose();
        towersManager.dispose();
        unitsManager.dispose();
        tmxMap.dispose();
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
    }

    private void createField() {
        Gdx.app.log("GameField::createField()", "-START- field:" + field);
        if (field == null) {
            field = new Cell[tmxMap.width][tmxMap.height];
            for (int y = 0; y < tmxMap.height; y++) {
                for (int x = 0; x < tmxMap.width; x++) {
                    Cell cell = field[x][y] = new Cell();
                    cell.setGraphicCoordinates(x, y, tmxMap.tileWidth, tmxMap.tileHeight, tmxMap.isometric);
                    for (MapLayer mapLayer : tmxMap.getLayers()) {
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
                                            this.createTower(x, y, factionsManager.getRandomTemplateForTowerFromAllFaction(), null);
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
        if (mapPath.contains("randomMap")) {
            for (int x = 0; x < tmxMap.width; x++) {
                for (int y = 0; y < tmxMap.height; y++) {
                    if(random.nextInt(10) < 3) {
                        if (getCellNoCheck(x, y).isEmpty()) {
                            TiledMapTileSet tiledMapTileSet = tmxMap.getTileSets().getTileSet(1);
                            int firstgid = tiledMapTileSet.getProperties().get("firstgid", Integer.class);

                            int randNumber = (firstgid + 43 + random.nextInt(4)); // bricks from TileObjectsRubbleWalls.tsx
                            TiledMapTile tile = tiledMapTileSet.getTile(randNumber);
                            Logger.logDebug("tile:" + tile);
                            getCellNoCheck(x, y).setTerrain(tile);
                        }
                    }
                }
            }
        }
        return true;
    }

    public Cell getCell(int x, int y) {
        return x >= 0 && x < tmxMap.width && y >= 0 && y < tmxMap.height ? field[x][y] : null;
    }

    public Cell getCellNoCheck(int x, int y) {
        return field[x][y];
    }

    public void updateCellsGraphicCoordinates(float sizeCellX, float sizeCellY) {
        for (int cellX = 0; cellX < tmxMap.width; cellX++) {
            for (int cellY = 0; cellY < tmxMap.height; cellY++) {
                field[cellX][cellY].setGraphicCoordinates(cellX, cellY, sizeCellX, sizeCellY, tmxMap.isometric);
            }
        }
    }

    public void updateTowersGraphicCoordinates(CameraController cameraController) {
        for (Tower tower : towersManager.towers) {
            tower.updateCenterGraphicCoordinates(cameraController);
        }
    }

    public void update(float deltaTime, CameraController cameraController) {
        deltaTime = deltaTime * gameSpeed;
        if (!gamePaused) {
            timeOfGame += deltaTime;
            if (gameScreen.spawnUnitFromServerScreenByWaves()) {
                spawnUnits(deltaTime);
            }
            stepAllUnits(deltaTime, cameraController);
            shotAllTowers(deltaTime, cameraController);
            moveAllShells(deltaTime, cameraController);
        }
    }

    public Unit spawnUnitFromUser(TemplateForUnit templateForUnit) {
        Gdx.app.log("GameField::spawnUnitFromUser()", "-- templateForUnit:" + templateForUnit);
        if (gameScreen.playersManager.getLocalPlayer().gold >= templateForUnit.cost) {
            gameScreen.playersManager.getLocalPlayer().gold -= templateForUnit.cost;
            for (Wave wave : waveManager.wavesForUser) {
                Cell spawnCell = getCell(wave.spawnPoint.x, wave.spawnPoint.y);
                Cell destExitCell = getCell(wave.exitPoint.x, wave.exitPoint.y);
                return gameScreen.createUnit(spawnCell, destExitCell, templateForUnit, destExitCell, gameScreen.playersManager.getLocalPlayer()); // create Player1 Unit
            }
        }
        return null;
    }

    private void spawnUnits(float delta) {
        if (unitsSpawn) {
            if (waveManager.allTogether) {
                Array<WaveManager.TemplateNameAndPoints> allUnitsForSpawn = waveManager.getAllUnitsForSpawn(delta);
                for (WaveManager.TemplateNameAndPoints templateNameAndPoints : allUnitsForSpawn) {
                    spawnUnitByWave(templateNameAndPoints);
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
                        spawnUnitByWave(templateNameAndPoints);
                    }
                }
            }
        }
    }

    private Unit spawnUnitByWave(WaveManager.TemplateNameAndPoints templateNameAndPoints) {
        if (templateNameAndPoints != null) {
            TemplateForUnit templateForUnit = factionsManager.getTemplateForUnitByName(templateNameAndPoints.templateName);
            if (templateForUnit != null) {
                Cell spawnCell = getCell(templateNameAndPoints.spawnPoint.x, templateNameAndPoints.spawnPoint.y);
                Cell destExitCell = getCell(templateNameAndPoints.exitPoint.x, templateNameAndPoints.exitPoint.y);
                return gameScreen.createUnit(spawnCell, destExitCell, templateForUnit, destExitCell, gameScreen.playersManager.getLocalServer()); // create Computer0 Unit
            } else {
                Gdx.app.error("GameField::spawnUnitByWave()", "-- templateForUnit == null | templateName:" + templateNameAndPoints.templateName);
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
            TemplateForUnit templateForUnit = factionsManager.getTemplateForUnitByName("unit3_footman");
            return createUnit(gameSettings.cellSpawnHero, gameSettings.cellExitHero, templateForUnit, gameSettings.cellExitHero, gameScreen.playersManager.getLocalPlayer()); // player1 = hero
        }
        return null;
    }

    public Unit spawnLocalHero(int cellX, int cellY) {
        Gdx.app.log("GameField::spawnLocalHero()", "-- cellX:" + cellX + " cellY:" + cellY);
        Gdx.app.log("GameField::spawnLocalHero()", "-- gameSettings.cellExitHero:" + gameSettings.cellExitHero);
        if (gameSettings.cellExitHero != null) {
            Cell cell = getCell(cellX, cellY);
            if (cell != null) {
                cell.removeTerrain(true);
                removeTower(cell.cellX, cell.cellY);
                return createUnit(cell, cell, factionsManager.getTemplateForUnitByName("unit3_footman"), gameSettings.cellExitHero, gameScreen.playersManager.getLocalPlayer()); // player1 = hero
            }
        } else {
            int randomX = (int)(Math.random() * tmxMap.width);
            int randomY = (int)(Math.random() * tmxMap.height);
            gameSettings.cellExitHero = getCell(randomX, randomY);
            Unit hero = spawnLocalHero(cellX, cellY);
            if (hero == null) {
                gameSettings.cellExitHero = null;
            }
        }
        return null;
    }

    public Unit spawnServerUnitToRandomExit(int x, int y) {
        Gdx.app.log("GameField::spawnServerUnitToRandomExit()", "-- x:" + x + " y:" + y);
        int randomX = random.nextInt(tmxMap.width);
        int randomY = random.nextInt(tmxMap.height);
        Gdx.app.log("GameField::spawnServerUnitToRandomExit()", "-- randomX:" + randomX + " randomY:" + randomY);
//        return gameScreen.createUnit(getCell(x, y), getCell(randomX, randomY), factionsManager.getRandomTemplateForUnitFromSecondFaction(), null, gameScreen.playersManager.getLocalServer());
        return createUnit(getCell(x, y), getCell(randomX, randomY), factionsManager.getRandomTemplateForUnitFromSecondFaction(), null, gameScreen.playersManager.getLocalServer());
    }

//    private Unit createUnit(Cell spawnCell, Cell destCell, TemplateForUnit templateForUnit, Cell exitCell) {
//        return gameScreen.createUnit(spawnCell, destCell, templateForUnit, exitCell, gameScreen.playersManager.getLocalServer());
//    }
    public Unit createUnit(CreateUnitData createUnitData) {
        Cell spawnCell = getCell(createUnitData.spawnCell.x, createUnitData.spawnCell.y);
        Cell destCell = getCell(createUnitData.destCell.x, createUnitData.destCell.y);
        TemplateForUnit templateForUnit = factionsManager.getTemplateForUnitByName(createUnitData.templateForUnit);
        Cell exitCell = getCell(createUnitData.exitCell.x, createUnitData.exitCell.y);
        Player player = gameScreen.playersManager.getPlayer(createUnitData.player.playerID);
        return createUnit(spawnCell, destCell, templateForUnit, exitCell, player);
    }

    public Unit createUnit(Cell spawnCell, Cell destCell, TemplateForUnit templateForUnit, Cell exitCell, Player player) {
        Unit unit = null;
        if (spawnCell != null && destCell != null && pathFinder != null) {
            pathFinder.loadCharMatrix(getCharMatrix(true));
            ArrayDeque<Cell> route = pathFinder.route(spawnCell.cellX, spawnCell.cellY, destCell.cellX, destCell.cellY);
            if (route == null) {
                pathFinder.loadCharMatrix(getCharMatrix(false));
                route = pathFinder.route(spawnCell.cellX, spawnCell.cellY, destCell.cellX, destCell.cellY);
            }
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

    public Unit createUnit(UnitInstanceData unitInstanceData) {
        Logger.logFuncStart("unitInstanceData:" + unitInstanceData.toString(true));
        GridPoint2 spawnGP2 = unitInstanceData.route.get(0);
        GridPoint2 destGP2 = unitInstanceData.route.get(unitInstanceData.route.size()-1);
        Cell spawnCell = getCell(spawnGP2.x, spawnGP2.y);
        Cell destCell = getCell(destGP2.x, destGP2.y);
        if (spawnCell != null && destCell != null && pathFinder != null) {
            ArrayDeque<Cell> route = unitInstanceData.getRoute(this);
            if (route != null) {
                TemplateForUnit templateForUnit = gameScreen.game.factionsManager.getTemplateForUnitByName(unitInstanceData.templateForUnit);
                Player player = gameScreen.playersManager.getPlayer(unitInstanceData.playerInfoData.playerID);
                Cell exitCell = null;
                if (unitInstanceData.exitCell != null) {
                    exitCell = getCell(unitInstanceData.exitCell.x, unitInstanceData.exitCell.y);
                }
                Unit unit = unitsManager.createUnit(route, templateForUnit, player, exitCell);
                spawnCell.setUnit(unit);
                unit.updateData(unitInstanceData);
                Gdx.app.log("GameField::createUnit()", "-- unit:" + unit);
                return unit;
            } else {
                Gdx.app.log("GameField::createUnit()", "-- Not found route for createUnit!");
                return null;
            }
        } else {
            Gdx.app.log("GameField::createUnit()", "-- Bad spawnCell:" + spawnCell + " || destCell:" + destCell + " || pathFinder:" + pathFinder);
            return null;
        }
    }

    public void updateUnitsManager(UnitsManagerData unitsManagerData) {
        for (UnitInstanceData unitInstanceData : unitsManagerData.units) {
            ArrayDeque<Cell> route = unitInstanceData.getRoute(this);
            if (route != null) {
                if (!unitsManager.updateUnit(unitInstanceData, route)) {
                    this.createUnit(unitInstanceData);
                }
            } else {
                Logger.logError("route:null unitInstanceData:" + unitInstanceData);
            }
        }
    }

    public UnderConstruction createdRandomUnderConstruction() {
        return createdUnderConstruction(factionsManager.getRandomTemplateForTowerFromAllFaction());
    }

    public UnderConstruction createdUnderConstruction(TemplateForTower templateForTower) {
        if (templateForTower != null) {
            if (underConstruction != null) {
                underConstruction.dispose();
            }
            return underConstruction = new UnderConstruction(templateForTower);
        }
        return null;
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

    public void buildTowersWithUnderConstruction(int buildX, int buildY) {
        Logger.logFuncStart("buildX:" + buildX, "buildY:" + buildY);
        if (underConstruction != null) {
            underConstruction.setEndCoors(buildX, buildY);
            gameScreen.createTowerWithGoldCheck(underConstruction.startX, underConstruction.startY, underConstruction.templateForTower);
            for (int k = 0; k < underConstruction.coorsX.size; k++) {
//            for(int k = underConstruction.coorsX.size-1; k >= 0; k--) {
                gameScreen.createTowerWithGoldCheck(underConstruction.coorsX.get(k), underConstruction.coorsY.get(k), underConstruction.templateForTower);
            }
            underConstruction.clearStartCoors();
            rerouteAllUnits();
        }
    }

    public Tower createTowerWithGoldCheck(int buildX, int buildY, TemplateForTower templateForTower) {
        Logger.logFuncStart("buildX:" + buildX, "buildY:" + buildY, "templateForTower:" + templateForTower);
        return createTowerWithGoldCheck(buildX, buildY, templateForTower, gameScreen.playersManager.getLocalPlayer());
    }

    public Tower createTowerWithGoldCheck(int buildX, int buildY, TemplateForTower templateForTower, Player player) {
        Logger.logFuncStart("buildX:" + buildX, "buildY:" + buildY, "templateForTower:" + templateForTower, "player:" + player);
        if (player.gold >= templateForTower.cost) {
            Tower tower = createTower(buildX, buildY, templateForTower, player);
            if (tower != null) {
                player.gold -= templateForTower.cost;
                Logger.logDebug("player:" + player + ", Now player.gold:" + player.gold);
                return tower;
            }
        }
        return null;
    }

    public Tower createTower(int buildX, int buildY, TemplateForTower templateForTower) {
        Logger.logFuncStart("buildX:" + buildX, "buildY:" + buildY, "templateForTower:" + templateForTower);
        Player localPlayer = gameScreen.playersManager.getLocalPlayer();
        return createTower(buildX, buildY, templateForTower, localPlayer);
    }

    public Tower createTower(int buildX, int buildY, TemplateForTower templateForTower, Player player) {
        Logger.logFuncStart("buildX:" + buildX, "buildY:" + buildY, "templateForTower:" + templateForTower, "player:" + player);
        if (player == null) {
            player = gameScreen.playersManager.getLocalServer(); // ComputerPlayer0 inst;
        }
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

    public boolean removeTowerWithGold(int cellX, int cellY, Player player) {
        int towerCost = removeTower(cellX, cellY);
        if (towerCost > 0) {
//            rerouteForAllUnits();
            player.gold += towerCost; // *0.5;
            Gdx.app.log("GameField::removeTowerWithGold()", "-- Now gamerGold:" + player.gold);
            return true;
        }
        return false;
    }

    public int removeTower(int cellX, int cellY) { // TODO if tower.cost zero mb bug
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
        pathFinder.loadCharMatrix(getCharMatrix(false));
        Gdx.app.log("GameField::updatePathFinderWalls()", "-end- pathFinder.walls.size():" + pathFinder.nodeMatrix.length);
    }

    public char[][] getCharMatrix(boolean towers) {
        if (field != null) {
            char[][] charMatrix = new char[tmxMap.height][tmxMap.width];
            for (int y = 0; y < tmxMap.height; y++) {
                for (int x = 0; x < tmxMap.width; x++) {
                    if (towers) {
                        if (field[x][y].isTerrain() || field[x][y].getTower() != null) {
                            if (field[x][y].getTower() != null && field[x][y].getTower().templateForTower.towerAttackType == TowerAttackType.Pit) {
                                charMatrix[y][x] = '.';
                            } else {
                                charMatrix[y][x] = 'T';
                            }
                        } else {
                            charMatrix[y][x] = '.';
                        }
                    } else {
                        charMatrix[y][x] = '.';
                        if (field[x][y].isTerrain()) {
                            charMatrix[y][x] = 'T';
                        }
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
//            for (Unit unit : unitsManager.units) { // GdxRuntimeException: #iterator() cannot be used nested.
            for (int u = 0; u < unitsManager.units.size; u++ ) {
                Unit unit = unitsManager.units.get(u);
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
                            if (unit.player == gameScreen.playersManager.getLocalServer()) {
                                unit.player.missedUnits++;
                            } else if (unit.player == gameScreen.playersManager.getLocalPlayer()) {
                                if (unit.exitCell == nextCurrentCell) { // ?? unit.exitCell.equals(cell) ?? // hueta! change plz Nikita!
                                    unit.player.missedUnits++;
                                    if (cell != null) {
                                        cell.removeUnit(unit);
                                    }
                                    unitsManager.removeUnit(unit);
                                    Gdx.app.log("GameField::stepAllUnits()", "-- unitsManager.removeUnit(tower):");
                                } else {
                                    if (unit.route == null || unit.route.isEmpty()) {
                                        int randomX = random.nextInt(tmxMap.width);
                                        int randomY = random.nextInt(tmxMap.height);
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
//                    removeTowerWithGold(tower.cell.cellX, tower.cell.cellY);
                    removeTower(tower.cell.cellX, tower.cell.cellY);
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
                            tower.player.gold += unit.templateForUnit.bounty;
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

    public void updateGameFieldVariables(GameFieldVariablesData gameFieldVariablesData) {
        this.timeOfGame = gameFieldVariablesData.timeOfGame;
        this.gameSpeed = gameFieldVariablesData.gameSpeed;
        this.gamePaused = gameFieldVariablesData.gamePaused;
        this.unitsSpawn = gameFieldVariablesData.unitsSpawn;
    }

    public GameState getGameState() {
//        Logger.logDebug("missedUnitsForPlayer1:" + gameScreen.playersManager.getLocalPlayer().missedUnits);
//        Logger.logDebug("maxOfMissedUnitsForPlayer1:" + gameScreen.playersManager.getLocalPlayer().maxOfMissedUnits);
//        Logger.logDebug("missedUnitsForComputer0:" + gameScreen.playersManager.getLocalServer().missedUnits);
//        Logger.logDebug("maxOfMissedUnitsForComputer0:" + gameScreen.playersManager.getLocalServer().maxOfMissedUnits);
//        Logger.logDebug("waveManager.getNumberOfActions():" + waveManager.getNumberOfActions());
//        Logger.logDebug("unitsManager.units.size:" + unitsManager.units.size);
//        Logger.logDebug("gameSettings.gameType:" + gameSettings.gameType);
        if (gameSettings.gameType == GameType.LittleGame) {
            for (Unit hero : unitsManager.hero) {
                Cell pos = hero.nextCell;
                if (pos != null) {
                    if (pos.cellX == hero.exitCell.cellX && pos.cellY == hero.exitCell.cellY) {
                        Logger.logDebug("hero.nextCell:" + hero.nextCell);
                        Logger.logDebug("hero.exitCell:" + hero.exitCell);
                        return GameState.WIN;//"LittleGame_Win";
                    }
                } else {
                    return GameState.LOSE;//"LittleGame_WTF";
                }
            }
        } else if (gameSettings.gameType == GameType.TowerDefence) {
            if (gameScreen.playersManager.getPlayers().size > 2) {
                if (gameScreen.playersManager.getLocalServer() != null && gameScreen.playersManager.getLocalPlayer() != null) {
                    if (gameScreen.playersManager.getLocalPlayer().missedUnits >= gameScreen.playersManager.getLocalPlayer().maxOfMissedUnits) {
                        return GameState.LOSE;
                    } else {
                        if (gameScreen.playersManager.getLocalServer().missedUnits >= gameScreen.playersManager.getLocalServer().maxOfMissedUnits) { // При инициализации если в карте не было голды игроку. и у игрока изначально было 0 голды. то он сразу же выиграет
                            return GameState.WIN;
                        }
                        if (waveManager.getNumberOfActions() == 0 && unitsManager.units.size == 0) {
                            return GameState.WIN;
                        }
                    }
                }
            }
        } else {
            Gdx.app.log("GameField::getGameState()", "-bad- gameSettings.gameType:" + gameSettings.gameType);
        }
//        Logger.logDebug("IN PROGRESS!!");
        return GameState.IN_PROGRESS;
    }

    public void turnRight() {
        tmxMap.setSize(tmxMap.height, tmxMap.width); // flip size
        Cell[][] newCells = new Cell[field[0].length][field.length]; // flip array size
        int y2 = tmxMap.width - 1;
        for(int y = 0; y < tmxMap.width; y++) {
            for(int x = 0; x < tmxMap.height; x++) {
                newCells[y2][x] = field[x][y];
                newCells[y2][x].setGraphicCoordinates(y2, x, tmxMap.tileWidth, tmxMap.tileHeight, tmxMap.isometric);
            }
            y2--;
        }
        // delete field; // TODO need this make or not?
        field = newCells;
        tmxMap.turnedMap = !tmxMap.turnedMap;
    }

    public void turnLeft() {
        tmxMap.setSize(tmxMap.height, tmxMap.width); // flip size
        Cell[][] newCells = new Cell[field[0].length][field.length]; // flip array size
        int x2 = tmxMap.height - 1;
        for(int x = 0; x < tmxMap.height; x++) {
            for(int y = 0; y < tmxMap.width; y++) {
                newCells[y][x2] = field[x][y];
                newCells[y][x2].setGraphicCoordinates(y, x2, tmxMap.tileWidth, tmxMap.tileHeight, tmxMap.isometric);
            }
            x2--;
        }
        // delete field;
        field = newCells;
        tmxMap.turnedMap = !tmxMap.turnedMap;
    }

    /**
     * Flips cells array by X axis.
     */
    public void flipX() {
        Cell[][] newCells = new Cell[tmxMap.width][tmxMap.height];
        int x2 = tmxMap.width - 1;
        for (int x = 0; x < tmxMap.width; x++) {
            for (int y = 0; y < tmxMap.height; y++) {
                newCells[x][y] = field[x2][y];
                newCells[x][y].setGraphicCoordinates(x, y, tmxMap.tileWidth, tmxMap.tileHeight, tmxMap.isometric);
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
        Cell[][] newCells = new Cell[tmxMap.width][tmxMap.height];
        int y2 = tmxMap.height - 1;
        for(int y = 0; y < tmxMap.height; y++) {
            for(int x = 0; x < tmxMap.width; x++) {
                newCells[x][y] = field[x][y2];
                newCells[x][y].setGraphicCoordinates(x, y, tmxMap.tileWidth, tmxMap.tileHeight, tmxMap.isometric);
            }
            y2--;
        }
        // delete field;
        field = newCells;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("GameField[");
        sb.append("unitsSpawn:" + unitsSpawn);
        sb.append(",gamePaused:" + gamePaused);
        sb.append(",gameSpeed:" + gameSpeed);
        sb.append(",timeOfGame:" + timeOfGame);
        if (full) {
            sb.append(",underConstruction:" + underConstruction);
            sb.append(",pathFinder:" + pathFinder);
            sb.append(",field.length:" + ( (field!=null) ? field.length : "null"));
            sb.append(",tmxMap:" + tmxMap);
            sb.append(",unitsManager:" + unitsManager);
            sb.append(",towersManager:" + towersManager);
            sb.append(",waveManager:" + waveManager);
            sb.append(",factionsManager:" + factionsManager);
            sb.append(",gameSettings:" + gameSettings);
        }
        sb.append("]");
        return sb.toString();
    }
}
