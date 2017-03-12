package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Intersector; // AlexGor
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.WhichCell;
import com.betmansmall.game.gameLogic.mapLoader.MapLoader;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.PathFinder.Node;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.PathFinder.PathFinder;
import com.betmansmall.game.gameLogic.playerTemplates.FactionsManager;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;
import com.betmansmall.game.gameLogic.playerTemplates.TowerAttackType;
import com.betmansmall.game.gameLogic.playerTemplates.ShellEffectType;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.TreeMap;
import java.lang.Object;

//import com.badlogic.gdx.maps.tiled.TmxMapLoader;

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
    private static int sizeCellX, sizeCellY;

    public int getSizeFieldX() {
        return sizeFieldX;
    }

    public int getSizeFieldY() {
        return sizeFieldY;
    }

    public static int getSizeCellX() {
        return sizeCellX;
    }

    public static int getSizeCellY() {
        return sizeCellY;
    }

    public int isDrawableBackground = 1;
    public int isDrawableForeground = 1;
    public int isDrawableGrid = 0;
    public static int isDrawableCreeps = 1;
    public static int isDrawableTowers = 1;
//    public boolean isDrawableRoutes = true;
    public int isDrawableGridNav = 0;
    public int drawOrder = 8;

    private int halfSizeCellX;
    private int halfSizeCellY;

    private Cell[][] field;
    private PathFinder pathFinder;

    private WaveManager waveManager;
    static public CreepsManager creepsManager; // For Shell
    private TowersManager towersManager;
    private FactionsManager factionsManager;

    private UnderConstruction underConstruction;
    private Texture greenCheckmark;
    private Texture redCross;

    // GAME INTERFACE ZONE1
    private WhichCell whichCell;
    private boolean gamePaused;
    public float gameSpeed;
    private int maxOfMissedCreeps;
    private int missedCreeps;
    public static int gamerGold;
    // GAME INTERFACE ZONE2

    //TEST ZONE1
    private Map<Integer, Object> priorityMap = new TreeMap<Integer, Object>();
    private Animation animation;
    private float stateTime;
    //TEST ZONE2

    public GameField(String mapName) {
        waveManager = new WaveManager();
        creepsManager = new CreepsManager();
        towersManager = new TowersManager();
        factionsManager = new FactionsManager();
        factionsManager.loadFactions();

        map = new MapLoader(waveManager).load(mapName);
        renderer = new IsometricTiledMapRenderer(map, spriteBatch);

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
            Gdx.app.error("GameField::GameField()", " -- Achtung fuck. NOT FOUND 'maps/textures/green_checkmark.png' & 'maps/textures/red_cross.png' YEBAK");
        }

        createField(sizeFieldX, sizeFieldY, map.getLayers());
        waveManager.validationPoints(field);
        if (waveManager.waves.size == 0) {
            GridPoint2 spawnPoint = new GridPoint2(-1, -1);
            for (int y = 0; y < sizeFieldY; y++) {
                for (int x = 0; x < sizeFieldX; x++) {
                    if (cellIsEmpty(x, y)) {
                        spawnPoint.set(x, y);
                    }
                }
            }
            GridPoint2 exitPoint = new GridPoint2(-1, -1);
            for (int y = sizeFieldY - 1; y >= 0; y--) {
                for (int x = sizeFieldX-1; x >= 0; x--) {
                    if (cellIsEmpty(x, y)) {
                        exitPoint.set(x, y);
                    }
                }
            }
            Wave wave = new Wave(spawnPoint, exitPoint, 0f);
//            wave.spawnInterval = 1f;
            for (int k = 0; k < 10; k++) {
                wave.addAction("interval=" + 1);
                wave.addAction(factionsManager.getRandomTemplateForUnitFromFirstFaction().getTemplateName());
            }
            waveManager.addWave(wave);
        }

        // GAME INTERFACE ZONE1
        whichCell = new WhichCell(sizeFieldX, sizeFieldY, sizeCellX, sizeCellY);
        gamePaused = true;
        gameSpeed = 1.0f;
        maxOfMissedCreeps = 1000;
        missedCreeps = 0;
        gamerGold = 10000;
        // GAME INTERFACE ZONE2
    }

    private void createField(int sizeFieldX, int sizeFieldY, MapLayers mapLayers) {
        Gdx.app.log("GameField::createField(" + sizeFieldX + "," + sizeFieldY + "," + mapLayers + ");", " -- field:" + field);
        if (field == null) {
            field = new Cell[sizeFieldX][sizeFieldY];
            for (int y = 0; y < sizeFieldY; y++) {
                for (int x = 0; x < sizeFieldX; x++) {
                    field[x][y] = new Cell();
                    for (MapLayer mapLayer : mapLayers) {
                        if (mapLayer instanceof TiledMapTileLayer) {
                            TiledMapTileLayer layer = (TiledMapTileLayer) mapLayer;
                            TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                            if (cell != null) {
                                TiledMapTile tiledMapTile = cell.getTile();
                                if (tiledMapTile != null) {
                                    if(layer.getProperties().get("background") == null) {
                                        field[x][y].foregroundTiles.add(tiledMapTile);
                                    } else {
                                        field[x][y].backgroundTiles.add(tiledMapTile);
                                    }
                                    if (tiledMapTile.getProperties().get("terrain") != null) {
                                        field[x][y].setTerrain();
                                    } else if (tiledMapTile.getProperties().get("spawnPoint") != null) {
//                                    spawnPoint = new GridPoint2(x, y);
//                                        waveManager.spawnPoints.add(new GridPoint2(x, y));
//                                    field[x][y].setTerrain();
                                        Gdx.app.log("GameField::GameField()", "-- Set spawnPoint: (" + x + ", " + y + ")");
                                    } else if (tiledMapTile.getProperties().get("exitPoint") != null) {
//                                    exitPoint = new GridPoint2(x, y);
//                                        waveManager.exitPoints.add(new GridPoint2(x, y));
//                                    field[x][y].setTerrain();
                                        Gdx.app.log("GameField::GameField()", "-- Set exitPoint: (" + x + ", " + y + ")");
                                    }
                                    if(tiledMapTile.getProperties().get("treeName") != null) {
                                        String treeName = tiledMapTile.getProperties().get("treeName", String.class);
                                        int treeWidth = Integer.parseInt(tiledMapTile.getProperties().get("treeWidth", "1", String.class));
                                        int treeHeight = Integer.parseInt(tiledMapTile.getProperties().get("treeHeight", "1", String.class));
                                        Gdx.app.log("GameField::createField();", " -- New Tree:" + treeName + "[" + treeWidth + "," + treeHeight + "]:{" + x + "," + y + "}");
                                        float regionX = tiledMapTile.getTextureRegion().getRegionX();
                                        float regionY = tiledMapTile.getTextureRegion().getRegionY();
                                        float regionWidth = tiledMapTile.getTextureRegion().getRegionWidth();
                                        float regionHeight = tiledMapTile.getTextureRegion().getRegionWidth();
                                        Gdx.app.log("GameField::createField();", " -- regionX:" + regionX + " regionY:" + regionY + " regionWidth:" + regionWidth + " regionHeight:" + regionHeight);
                                        TextureRegion textureRegion = new TextureRegion(tiledMapTile.getTextureRegion());
                                        textureRegion.setRegion(regionX - ((treeWidth>2) ? (treeWidth-2)*regionWidth : 0), regionY - ((treeHeight>1) ? (treeHeight-1)*regionHeight : 0), treeWidth*regionWidth, treeHeight*regionHeight);
//                                        Cell.Tree tree = new Cell.Tree(textureRegion, treeWidth, treeHeight);
                                    }
                                }
                            }
                        } else {
                            Gdx.app.log("GameField::createField()", " -- Не смог преобразовать MapLayer в TiledMapTileLayer");
                        }
                    }
                }
            }
        }
//        turnRight();
        flipY();
//        turnRight();
        Gdx.app.log("GameField::createField();", " -- pathFinder:" + pathFinder);
        pathFinder = new PathFinder();
        pathFinder.loadCharMatrix(getCharMatrix());
    }

    public void turnRight() {
        if(sizeFieldX == sizeFieldY) {
            Cell[][] newCells = new Cell[sizeFieldX][sizeFieldY];
            for(int y = 0; y < sizeFieldY; y++) {
                for(int x = 0; x < sizeFieldX; x++) {
                    newCells[sizeFieldX-y-1][x] = field[x][y];
                }
            }
            field = newCells;
        } else {
            Gdx.app.log("GameField::turnRight();", " -- Not work || Work but mb not Good!");
            int oldWidth = sizeFieldX;
            int oldHeight = sizeFieldY;
            sizeFieldX = sizeFieldY;
            sizeFieldY = oldWidth;
            Cell[][] newCells = new Cell[sizeFieldX][sizeFieldY];
            for(int y = 0; y < oldHeight; y++) {
                for(int x = 0; x < oldWidth; x++) {
                    newCells[sizeFieldX-y-1][x] = field[x][y];
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
                }
            }
            field = newCells;
        } else {
            Gdx.app.log("GameField::turnLeft();", " -- Not work || Work but mb not Good!");
            int oldWidth = sizeFieldX;
            int oldHeight = sizeFieldY;
            sizeFieldX = sizeFieldY;
            sizeFieldY = oldWidth;
            Cell[][] newCells = new Cell[sizeFieldX][sizeFieldY];
            for(int y = 0; y < oldHeight; y++) {
                for(int x = 0; x < oldWidth; x++) {
                    newCells[y][sizeFieldY-x-1] = field[x][y];
                }
            }
            field = newCells;
        }
    }

    public void flipX() {
        Cell[][] newCells = new Cell[sizeFieldX][sizeFieldY];
        for (int y = 0; y < sizeFieldY; y++) {
            for (int x = 0; x < sizeFieldX; x++) {
                newCells[sizeFieldX - x - 1][y] = field[x][y];
            }
        }
        field = newCells;
    }

    public void flipY() {
        Cell[][] newCells = new Cell[sizeFieldX][sizeFieldY];
        for(int y = 0; y < sizeFieldY; y++) {
            for(int x = 0; x < sizeFieldX; x++) {
                newCells[x][sizeFieldY-y-1] = field[x][y];
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
                        if (field[x][y].getTower() != null && field[x][y].getTower().getTemplateForTower().towerAttackType == TowerAttackType.Pit) {
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
        Gdx.app.log("GameField::dispose()", " -- Called!");
        shapeRenderer.dispose();
        spriteBatch.dispose();
        bitmapFont.dispose();
        map.dispose();
        renderer.dispose();
        greenCheckmark.dispose();
        redCross.dispose();
    }

    public void render(float delta, OrthographicCamera camera) {
        delta = delta * gameSpeed;
        if (!gamePaused) {
            spawnCreeps(delta);
            stepAllCreep(delta);
            shotAllTowers(delta);
            moveAllShells(delta);
        }

//        if (isDrawableTerrain) {
//            renderer.setView(camera);
//            renderer.render();
//            renderer.getBatch().begin();
//            for (MapLayer mapLayer : map.getLayers()) {
//                if (mapLayer instanceof TiledMapTileLayer) {
//                    TiledMapTileLayer layer = (TiledMapTileLayer) mapLayer;
//                    String background = layer.getProperties().get("background", String.class);
//                    if(background != null) {
//                        renderer.renderTileLayer(layer);
//                    }
//                }
//            }
//            renderer.getBatch().end();
//        }
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        if(isDrawableBackground > 0) {
            drawBackGrounds(spriteBatch);
        }
        if(isDrawableForeground > 0) {
            drawForeGroundsWithCreepsAndTowers(spriteBatch);
        }
        spriteBatch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawCreepsBars(shapeRenderer);
        shapeRenderer.end();

        if (isDrawableGrid > 0)
            drawGrid(camera);
//        // just workaround
//        if (isDrawableCreeps && isDrawableTowers) {
//            drawCreepsAndTowers(camera);
//        } else {
//            if (isDrawableTowers)
//                drawTowers(camera);
//            if (isDrawableCreeps)
//                drawCreeps(camera);
//        }
        if (isDrawableGridNav > 0) {
            drawRoutes(camera);
            drawGridNav(camera);
        }
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        drawShells(spriteBatch);
        drawTowersUnderConstruction(spriteBatch);
        spriteBatch.end();

        shapeRenderer.setColor(Color.FIREBRICK);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(0f, 0f, 1.5f);
        shapeRenderer.end();
        if (animation != null) {
            stateTime += delta;
            TextureRegion currentFrame = animation.getKeyFrame(stateTime, true); // #16
            spriteBatch.begin();
            spriteBatch.draw(currentFrame, 0, 700, 700, 700); // #17
//            bitmapFont.draw(spriteBatch, getGamerGold(), Gdx.graphics.getWidth()/2-10, Gdx.graphics.getHeight())
//            bitmapFont.draw(spriteBatch, String.valueOf(getGamerGold()), Gdx.graphics.getWidth()/2-10, Gdx.graphics.getHeight()-10);
            spriteBatch.end();
        }
        spriteBatch.begin();
        bitmapFont.setColor(Color.YELLOW);
        bitmapFont.draw(spriteBatch, String.valueOf("0, 0"), 0, 0);
        bitmapFont.getData().setScale(4);
        bitmapFont.draw(spriteBatch, String.valueOf("Gold amount: " + gamerGold), Gdx.graphics.getWidth() / 2 - 150, Gdx.graphics.getHeight() - 10);
        spriteBatch.end();
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
            int x = 0, y = 0;
            int length = (sizeFieldX > sizeFieldY) ? sizeFieldX : sizeFieldY;
            while (x < length) {
                if(x < sizeFieldX && y < sizeFieldY) {
                    if (x == length - 1 && y == length - 1) {
                        drawBackGroundCell(spriteBatch, x, y);
//                        Gdx.app.log("GameField::drawBackGrounds();", " -- хуй");
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
        Vector2 pos = new Vector2();
        Array<TiledMapTile> tiledMapTiles = field[cellX][cellY].backgroundTiles;
        for (TiledMapTile tiledMapTile : tiledMapTiles) {
            TextureRegion textureRegion = tiledMapTile.getTextureRegion();
            if (isDrawableBackground == 1 || isDrawableBackground == 5) {
                pos.set(getGraphicCoordinates(cellX, cellY, 1)).add(-halfSizeCellX, -halfSizeCellY);
                spriteBatch.draw(textureRegion, pos.x, pos.y);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
            if (isDrawableBackground == 2 || isDrawableBackground == 5) {
                pos.set(getGraphicCoordinates(cellX, cellY, 2)).add(-halfSizeCellX, -halfSizeCellY);
                spriteBatch.draw(textureRegion, pos.x, pos.y);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
            if (isDrawableBackground == 3 || isDrawableBackground == 5) {
                pos.set(getGraphicCoordinates(cellX, cellY, 3)).add(-halfSizeCellX, -halfSizeCellY);
                spriteBatch.draw(textureRegion, pos.x, pos.y);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
            if (isDrawableBackground == 4 || isDrawableBackground == 5) {
                pos.set(getGraphicCoordinates(cellX, cellY, 4)).add(-halfSizeCellX, -halfSizeCellY);
                spriteBatch.draw(textureRegion, pos.x, pos.y);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
        }
    }

    private void drawForeGroundsWithCreepsAndTowers(SpriteBatch spriteBatch) {
        if(drawOrder == 0) {
            for (int y = 0; y < sizeFieldY; y++) {
                for (int x = 0; x < sizeFieldX; x++) {
                    drawForeGroundCellWithCreepsAndTower(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 1) {
            for (int x = 0; x < sizeFieldX; x++) {
                for (int y = 0; y < sizeFieldY; y++) {
                    drawForeGroundCellWithCreepsAndTower(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 2) {
            for (int y = sizeFieldY-1; y >= 0; y--) {
                for (int x = sizeFieldX-1; x >= 0; x--) {
                    drawForeGroundCellWithCreepsAndTower(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 3) {
            for (int x = sizeFieldX-1; x >= 0; x--) {
                for (int y = sizeFieldY-1; y >= 0; y--) {
                    drawForeGroundCellWithCreepsAndTower(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 4) {
            for (int y = sizeFieldY-1; y >= 0; y--) {
                for (int x = 0; x < sizeFieldX; x++) {
                    drawForeGroundCellWithCreepsAndTower(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 5) {
            for (int x = 0; x < sizeFieldX; x++) {
                for (int y = sizeFieldY-1; y >= 0; y--) {
                    drawForeGroundCellWithCreepsAndTower(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 6) {
            for (int y = 0; y < sizeFieldY; y++) {
                for (int x = sizeFieldX-1; x >= 0; x--) {
                    drawForeGroundCellWithCreepsAndTower(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 7) {
            for (int x = sizeFieldX-1; x >= 0; x--) {
                for (int y = 0; y < sizeFieldY; y++) {
                    drawForeGroundCellWithCreepsAndTower(spriteBatch, x, y);
                }
            }
        } else if(drawOrder == 8) {
            int x = 0, y = 0;
            int length = (sizeFieldX > sizeFieldY) ? sizeFieldX : sizeFieldY;
            while (x < length) {
                if(x < sizeFieldX && y < sizeFieldY) {
                    if (x == length - 1 && y == length - 1) {
                        drawForeGroundCellWithCreepsAndTower(spriteBatch, x, y);
//                        Gdx.app.log("GameField::drawForeGroundsWithCreepsAndTowers();", " -- хуй");
//                        break;
                    } else {
                        drawForeGroundCellWithCreepsAndTower(spriteBatch, x, y);
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

    private void drawForeGroundCellWithCreepsAndTower(SpriteBatch spriteBatch, int cellX, int cellY) {
        Vector2 pos = new Vector2();
        Array<TiledMapTile> tiledMapTiles = field[cellX][cellY].foregroundTiles;
        for (TiledMapTile tiledMapTile : tiledMapTiles) {
            TextureRegion textureRegion = tiledMapTile.getTextureRegion();
            if(isDrawableForeground == 1 || isDrawableForeground == 5) {
                pos.set(getGraphicCoordinates(cellX, cellY, 1)).add(-halfSizeCellX, -halfSizeCellY);
                spriteBatch.draw(textureRegion, pos.x, pos.y);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
            if(isDrawableForeground == 2 || isDrawableForeground == 5) {
                pos.set(getGraphicCoordinates(cellX, cellY, 2)).add(-halfSizeCellX, -halfSizeCellY);
                spriteBatch.draw(textureRegion, pos.x, pos.y);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
            if(isDrawableForeground == 3 || isDrawableForeground == 5) {
                pos.set(getGraphicCoordinates(cellX, cellY, 3)).add(-halfSizeCellX, -halfSizeCellY);
                spriteBatch.draw(textureRegion, pos.x, pos.y);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
            if(isDrawableForeground == 4 || isDrawableForeground == 5) {
                pos.set(getGraphicCoordinates(cellX, cellY, 4)).add(-halfSizeCellX, -halfSizeCellY);
                spriteBatch.draw(textureRegion, pos.x, pos.y);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
            }
        }
        Array<Creep> creeps = field[cellX][cellY].getCreeps();
        if(creeps != null) {
            for (Creep creep : creeps) {
                drawCreep(creep, spriteBatch);
            }
        }
        Tower tower = field[cellX][cellY].getTower();
        if(tower != null) {
            drawTower(tower, spriteBatch);
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
//        Gdx.app.log("GameField::drawGrid(camera);", " -- widthForTop:" + widthForTop + " heightForTop:" + heightForTop + " widthForBottom:" + widthForBottom + " heightForBottom:" + heightForBottom);

        if(isDrawableGrid == 1 || isDrawableGrid == 5) {
            for (int x = 0; x <= sizeFieldX; x++)
                shapeRenderer.line( (halfSizeCellX*x),-(halfSizeCellY*x)+halfSizeCellY,-(widthForTop)+(halfSizeCellX*x),   -(heightForTop)-(x*halfSizeCellY)+halfSizeCellY);
            for (int y = 0; y <= sizeFieldY; y++)
                shapeRenderer.line(-(halfSizeCellX*y),-(halfSizeCellY*y)+halfSizeCellY, (widthForBottom)-(halfSizeCellX*y),-(heightForBottom)-(halfSizeCellY*y)+halfSizeCellY);
        }
        if(isDrawableGrid == 2 || isDrawableGrid == 5) {
            for (int x = 0; x <= sizeFieldX; x++)
                shapeRenderer.line((halfSizeCellX*x),-(halfSizeCellY*x)+halfSizeCellY,(widthForTop)+(halfSizeCellX*x),    (heightForTop)-(x*halfSizeCellY)+halfSizeCellY);
            for (int y = 0; y <= sizeFieldY; y++)
                shapeRenderer.line((halfSizeCellX*y), (halfSizeCellY*y)+halfSizeCellY,(widthForBottom)+(halfSizeCellX*y),-(heightForBottom)+(halfSizeCellY*y)+halfSizeCellY);
        }
        if(isDrawableGrid == 3 || isDrawableGrid == 5) {
            for (int x = 0; x <= sizeFieldY; x++) // WHT??? sizeFieldY check groundDraw
                shapeRenderer.line(-(halfSizeCellX*x),(halfSizeCellY*x)+halfSizeCellY, (widthForBottom)-(halfSizeCellX*x),(heightForBottom)+(x*halfSizeCellY)+halfSizeCellY);
            for (int y = 0; y <= sizeFieldX; y++) // WHT??? sizeFieldX check groundDraw
                shapeRenderer.line( (halfSizeCellX*y),(halfSizeCellY*y)+halfSizeCellY,-(widthForTop)+(halfSizeCellX*y),   (heightForTop)+(halfSizeCellY*y)+halfSizeCellY);
        }
        if(isDrawableGrid == 4 || isDrawableGrid == 5) {
            for (int x = 0; x <= sizeFieldY; x++) // WHT??? sizeFieldY check groundDraw
                shapeRenderer.line(-(halfSizeCellX*x), (halfSizeCellY*x)+halfSizeCellY,-(widthForBottom)-(halfSizeCellX*x),   -(heightForBottom)+(x*halfSizeCellY)+halfSizeCellY);
            for (int y = 0; y <= sizeFieldX; y++) // WHT??? sizeFieldX check groundDraw
                shapeRenderer.line(-(halfSizeCellX*y),-(halfSizeCellY*y)+halfSizeCellY,-(widthForTop)-(halfSizeCellX*y),(heightForTop)-(halfSizeCellY*y)+halfSizeCellY);
        }
        shapeRenderer.end();
    }

//    private void drawCreepsAndTowers(SpriteBatch spriteBatch) {
//        getPriorityMap();
//        for (Object obj : priorityMap.values()) {
//            if (obj instanceof Tower) {
//                drawTower((Tower) obj, spriteBatch);
//            } else {
//                for (Creep creep : (List<Creep>) obj) {
//                    drawCreep(creep, spriteBatch);
//                }
//            }
//        }
//    }
//
//    private void getPriorityMap() {
//        priorityMap.clear();
//        for (Tower tower : towersManager.getAllTowers()) {
//            priorityMap.put(tower.getPosition().x * 1000 - tower.getPosition().y, tower);
//        }
//        for (Creep creep : creepsManager.getAllCreeps()) {
//            List list;
//            Integer key = creep.getNewPosition().getX() * 1000 - creep.getNewPosition().getY();
//            if (priorityMap.containsKey(key) && (priorityMap.get(key) instanceof List)) {
//                list = (List) priorityMap.get(key);
//                list.add(creep);
//                priorityMap.put(key, list);
//            } else {
//                list = new ArrayList<Object>();
//                list.add(creep);
//                priorityMap.put(creep.getNewPosition().getX() * 1000 - creep.getNewPosition().getY(), list);
//            }
//        }
//    }

//    private void drawCreeps(SpriteBatch spriteBatch) {
//        for (Creep creep : creepsManager.getAllCreeps()) {
//            drawCreep(creep, spriteBatch);
//        }
//    }

    private void drawCreep(Creep creep, SpriteBatch spriteBatch) { //TODO Need to refactor this
//        Gdx.app.log("GameField::drawCreep(" + creep + "," + spriteBatch + ");", " -- Start!");
        TextureRegion currentFrame;
        if (creep.isAlive()) {
            currentFrame = creep.getCurentFrame();
        } else {
            currentFrame = creep.getCurrentDeathFrame();
        }
//        int deltaX = (currentFrame.getRegionWidth()) / 2;
//        int deltaY = (currentFrame.getRegionHeight()) / 2;
        int deltaX = (sizeCellX) / 2;
        int deltaY = (sizeCellY) / 2;

        float fVx = 0f, fVy = 0f;
        if(isDrawableCreeps == 1 || isDrawableCreeps == 5) {
            fVx = creep.circle1.x - deltaX;
            fVy = creep.circle1.y - deltaY;
            spriteBatch.draw(currentFrame, fVx, fVy, sizeCellX, sizeCellY*2);
        }
        if(isDrawableCreeps == 2 || isDrawableCreeps == 5) {
            fVx = creep.circle2.x - deltaX;
            fVy = creep.circle2.y - deltaY;
            spriteBatch.draw(currentFrame, fVx, fVy, sizeCellX, sizeCellY*2);
        }
        if(isDrawableCreeps == 3 || isDrawableCreeps == 5) {
            fVx = creep.circle3.x - deltaX;
            fVy = creep.circle3.y - deltaY;
            spriteBatch.draw(currentFrame, fVx, fVy, sizeCellX, sizeCellY*2);
        }
        if(isDrawableCreeps == 4 || isDrawableCreeps == 5) {
            fVx = creep.circle4.x - deltaX;
            fVy = creep.circle4.y - deltaY;
            spriteBatch.draw(currentFrame, fVx, fVy, sizeCellX, sizeCellY*2);
        }
//        drawCreepBar(shapeRenderer, creep, currentFrame, fVx, fVy);
    }

    private void drawCreepsBars(ShapeRenderer shapeRenderer) {
        for (Creep creep : creepsManager.getAllCreeps()) {
            if(isDrawableCreeps == 1 || isDrawableCreeps == 5) {
                drawCreepBar(shapeRenderer, creep, creep.circle1.x, creep.circle1.y);
            }
            if(isDrawableCreeps == 2 || isDrawableCreeps == 5) {
                drawCreepBar(shapeRenderer, creep, creep.circle2.x, creep.circle2.y);
            }
            if(isDrawableCreeps == 3 || isDrawableCreeps == 5) {
                drawCreepBar(shapeRenderer, creep, creep.circle3.x, creep.circle3.y);
            }
            if(isDrawableCreeps == 4 || isDrawableCreeps == 5) {
                drawCreepBar(shapeRenderer, creep, creep.circle4.x, creep.circle4.y);
            }
        }
    }

    private void drawCreepBar(ShapeRenderer shapeRenderer, Creep creep, float fVx, float fVy) {
        if (creep.isAlive()) {
            TextureRegion currentFrame = creep.getCurentFrame();
            fVx -= sizeCellX/2;
            fVy -= sizeCellY/2;
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
            int maxHP = creep.getTemplateForUnit().healthPoints;
            int hp = creep.getHp();
            hpBarHPWidth = hpBarHPWidth / maxHP * hp;
            shapeRenderer.rect(fVx + hpBarWidthIndent + hpBarSpace, fVy + currentFrameHeight - hpBarTopIndent + hpBarSpace, hpBarHPWidth - (hpBarSpace * 2), hpBarHeight - (hpBarSpace * 2));

            float allTime = 0f;
            for (ShellEffectType shellEffectType : creep.shellEffectTypes)
                allTime += shellEffectType.time;

            float effectWidth = effectBarWidth / allTime;
            float efX = fVx + hpBarWidthIndent + effectBarWidthSpace;
            float efY = fVy + currentFrameHeight - hpBarTopIndent + effectBarHeightSpace;
            float effectBlockWidth = effectBarWidth / creep.shellEffectTypes.size;
            for (int effectIndex = 0; effectIndex < creep.shellEffectTypes.size; effectIndex++) {
                ShellEffectType shellEffectType = creep.shellEffectTypes.get(effectIndex);
                if (shellEffectType.shellEffectEnum == ShellEffectType.ShellEffectEnum.FireEffect) {
                    shapeRenderer.setColor(Color.RED);
                } else if (shellEffectType.shellEffectEnum == ShellEffectType.ShellEffectEnum.FreezeEffect) {
                    shapeRenderer.setColor(Color.ROYAL);
                }
                float efWidth = effectBlockWidth - effectWidth * shellEffectType.elapsedTime;
                shapeRenderer.rect(efX, efY, efWidth, effectBarHeight);
                efX += effectBlockWidth;
//                Gdx.app.log("GameField::drawCreep();", " -- efX:" + efX + " efWidth:" + efWidth + ":" + effectIndex);
            }
        }
    }

    private void drawRoutes(OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BROWN); // (100, 60, 21, 1f);

        Vector2 pos = new Vector2();
        float gridNavRadius = sizeCellX/12f;
        for (Creep creep : creepsManager.getAllCreeps()) {
            ArrayDeque<Node> route = creep.getRoute();
            if (route != null) {
                for (Node coor : route) {
                    int x = coor.getX();
                    int y = coor.getY();
                    if(isDrawableGridNav == 1 || isDrawableGridNav == 5) {
                        pos.set(getGraphicCoordinates(x, y, 1));
                        shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                    }
                    if(isDrawableGridNav == 2 || isDrawableGridNav == 5) {
                        pos.set(getGraphicCoordinates(x, y, 2));
                        shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                    }
                    if(isDrawableGridNav == 3 || isDrawableGridNav == 5) {
                        pos.set(getGraphicCoordinates(x, y, 3));
                        shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                    }
                    if(isDrawableGridNav == 4 || isDrawableGridNav == 5) {
                        pos.set(getGraphicCoordinates(x, y, 4));
                        shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                    }
                }
            }
        }
        shapeRenderer.end();
    }

//    private void drawTowers(SpriteBatch spriteBatch) {
//        for (Tower tower : towersManager.getAllTowers()) {
//            drawTower(tower, spriteBatch);
//        }
//    }

    private void drawTower(Tower tower, SpriteBatch spriteBatch) {
//        Gdx.app.log("GameField", "drawTower(" + tower + ", " + camera + ");");
        int x = tower.getPosition().x;
        int y = tower.getPosition().y;
        int towerSize = tower.getTemplateForTower().size;
        GridPoint2 mainCellCoord = tower.getPosition();
        TextureRegion currentFrame = tower.getCurentFrame();
        Vector2 towerPos = new Vector2();
        if(isDrawableTowers == 5) {
            for(int m = 1; m < isDrawableTowers; m++) {
                towerPos.set(getGraphicCoordinates(mainCellCoord.x, mainCellCoord.y, m));
                towerPos.set(getCorrectGraphicTowerCoord(towerPos, towerSize, m));
                spriteBatch.draw(currentFrame, towerPos.x, towerPos.y, sizeCellX * towerSize, (sizeCellY * 2) * towerSize);
            }
        } else {
            towerPos.set(getGraphicCoordinates(mainCellCoord.x, mainCellCoord.y, isDrawableTowers));
            towerPos.set(getCorrectGraphicTowerCoord(towerPos, towerSize, isDrawableTowers));
            spriteBatch.draw(currentFrame, towerPos.x, towerPos.y, sizeCellX * towerSize, (sizeCellY * 2) * towerSize);
        }
    }

    private void drawGridNav(OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        Vector2 pos = new Vector2();
        float gridNavRadius = sizeCellX/20f;
        for (int y = 0; y < sizeFieldY; y++) {
            for (int x = 0; x < sizeFieldX; x++) {
                if (!field[x][y].isEmpty()) {
                    if (field[x][y].isTerrain()) {
                        shapeRenderer.setColor(Color.RED);
                    } else if (field[x][y].getCreep() != null) {
                        shapeRenderer.setColor(Color.GREEN);
                    } else if (field[x][y].getTower() != null) {
                        shapeRenderer.setColor(new Color(225f, 224f, 0f, 255f));
                    }
                    if(isDrawableGridNav == 1 || isDrawableGridNav == 5) {
                        pos.set(getGraphicCoordinates(x, y, 1));
                        shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                    }
                    if(isDrawableGridNav == 2 || isDrawableGridNav == 5) {
                        pos.set(getGraphicCoordinates(x, y, 2));
                        shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                    }
                    if(isDrawableGridNav == 3 || isDrawableGridNav == 5) {
                        pos.set(getGraphicCoordinates(x, y, 3));
                        shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                    }
                    if(isDrawableGridNav == 4 || isDrawableGridNav == 5) {
                        pos.set(getGraphicCoordinates(x, y, 4));
                        shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                    }
                }
            }
        }

        Array<GridPoint2> spawnPoints = waveManager.getAllSpawnPoint();
        shapeRenderer.setColor(new Color(0f, 255f, 204f, 255f));
        for (GridPoint2 spawnPoint : spawnPoints) {
            if(isDrawableGridNav == 1 || isDrawableGridNav == 5) {
                pos.set(getGraphicCoordinates(spawnPoint.x, spawnPoint.y, 1));
                shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
            }
            if(isDrawableGridNav == 2 || isDrawableGridNav == 5) {
                pos.set(getGraphicCoordinates(spawnPoint.x, spawnPoint.y, 2));
                shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
            }
            if(isDrawableGridNav == 3 || isDrawableGridNav == 5) {
                pos.set(getGraphicCoordinates(spawnPoint.x, spawnPoint.y, 3));
                shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
            }
            if(isDrawableGridNav == 4 || isDrawableGridNav == 5) {
                pos.set(getGraphicCoordinates(spawnPoint.x, spawnPoint.y, 4));
                shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
            }
        }

        Array<GridPoint2> exitPoints = waveManager.getAllExitPoint();
        shapeRenderer.setColor(new Color(255f, 0f, 102f, 255f));
        for (GridPoint2 exitPoint : exitPoints) {
//            Gdx.app.log("GameField::drawGridNav();", " -- exitPoint.x:" + exitPoint.x + " exitPoint.y:" + exitPoint.y + " isDrawableGridNav:" + isDrawableGridNav);
            if(isDrawableGridNav == 1 || isDrawableGridNav == 5) {
                pos.set(getGraphicCoordinates(exitPoint.x, exitPoint.y, 1));
                shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
            }
            if(isDrawableGridNav == 2 || isDrawableGridNav == 5) {
                pos.set(getGraphicCoordinates(exitPoint.x, exitPoint.y, 2));
                shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
            }
            if(isDrawableGridNav == 3 || isDrawableGridNav == 5) {
                pos.set(getGraphicCoordinates(exitPoint.x, exitPoint.y, 3));
                shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
            }
            if(isDrawableGridNav == 4 || isDrawableGridNav == 5) {
                pos.set(getGraphicCoordinates(exitPoint.x, exitPoint.y, 4));
                shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
            }
        }

        // Trush
//        shapeRenderer.setColor(Color.BLUE);
//        for (Creep creep : creepsManager.getAllCreeps()) {
//            shapeRenderer.circle(creep.circle1.x, creep.circle1.y, 1f);
//            shapeRenderer.circle(creep.circle2.x, creep.circle2.y, 1f);
//            shapeRenderer.circle(creep.circle3.x, creep.circle3.y, 1f);
//            shapeRenderer.circle(creep.circle4.x, creep.circle4.y, 1f);
//        }
//        shapeRenderer.setColor(Color.LIME);
//        for (Creep creep : creepsManager.getAllCreeps()) {
//            shapeRenderer.circle(creep.circle1.x, creep.circle1.y, 2f);
//            shapeRenderer.circle(creep.circle2.x, creep.circle2.y, 2f);
//            shapeRenderer.circle(creep.circle3.x, creep.circle3.y, 2f);
//            shapeRenderer.circle(creep.circle4.x, creep.circle4.y, 2f);
//        }
//        shapeRenderer.setColor(Color.PINK);
//        for (Tower tower : towersManager.getAllTowers()) {
//            for (Shell shell : tower.shells) {
//                if (null != shell.endPoint) {
//                    shapeRenderer.circle(shell.endPoint.x, shell.endPoint.y, shell.endPoint.radius);
//                }
//            }
//        }
        // ???

        shapeRenderer.setColor(Color.ORANGE);
        for (Tower tower : towersManager.getAllTowers()) {
            for (Shell shell : tower.shells) {
                shapeRenderer.rectLine(shell.currentPoint.x, shell.currentPoint.y, shell.endPoint.x, shell.endPoint.y, sizeCellX/40f);
                if (null != shell.circle) {
                    shapeRenderer.circle(shell.circle.x, shell.circle.y, shell.circle.radius);
                }
            }
        }

        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        for (Creep creep : creepsManager.getAllCreeps()) {
            if(isDrawableCreeps == 1 || isDrawableCreeps == 5)
                shapeRenderer.circle(creep.circle1.x, creep.circle1.y, creep.circle1.radius);
            if(isDrawableCreeps == 2 || isDrawableCreeps == 5)
                shapeRenderer.circle(creep.circle2.x, creep.circle2.y, creep.circle2.radius);
            if(isDrawableCreeps == 3 || isDrawableCreeps == 5)
                shapeRenderer.circle(creep.circle3.x, creep.circle3.y, creep.circle3.radius);
            if(isDrawableCreeps == 4 || isDrawableCreeps == 5)
                shapeRenderer.circle(creep.circle4.x, creep.circle4.y, creep.circle4.radius);
        }

        shapeRenderer.setColor(new Color(153f, 255f, 51f, 255f));
        Vector2 towerPos = new Vector2();
        for (Tower tower : towersManager.getAllTowers()) { // Draw white towers radius! -- radiusDetectionСircle
            if(isDrawableGridNav == 5) {
                if(isDrawableTowers == 5) {
                    for (int m = 1; m <= isDrawableTowers; m++) {
                        towerPos.set(tower.getCenterGraphicCoord(m)); // Need recoding this func!
                        shapeRenderer.circle(towerPos.x, towerPos.y, tower.radiusDetectionСircle.radius);
                    }
                } else {
                    towerPos.set(tower.getCenterGraphicCoord(isDrawableTowers));
                    shapeRenderer.circle(towerPos.x, towerPos.y, tower.radiusDetectionСircle.radius);
                }
            } else {
                if(isDrawableGridNav == isDrawableTowers) {
                    towerPos.set(tower.getCenterGraphicCoord(isDrawableTowers));
                    shapeRenderer.circle(towerPos.x, towerPos.y, tower.radiusDetectionСircle.radius);
                }
            }
        }

        shapeRenderer.setColor(Color.FIREBRICK);
        for (Tower tower : towersManager.getAllTowers()) { // Draw FIREBRICK towers radius! -- radiusFlyShellСircle
            if (tower.radiusFlyShellСircle != null) {
                if(isDrawableGridNav == 5) {
                    if(isDrawableTowers == 5) {
                        for (int m = 1; m <= isDrawableTowers; m++) {
                            towerPos.set(tower.getCenterGraphicCoord(m)); // Need recoding this func!
                            shapeRenderer.circle(towerPos.x, towerPos.y, tower.radiusFlyShellСircle.radius);
                        }
                    } else {
                        towerPos.set(tower.getCenterGraphicCoord(isDrawableTowers));
                        shapeRenderer.circle(towerPos.x, towerPos.y, tower.radiusFlyShellСircle.radius);
                    }
                } else {
                    if(isDrawableGridNav == isDrawableTowers) {
                        towerPos.set(tower.getCenterGraphicCoord(isDrawableTowers));
                        shapeRenderer.circle(towerPos.x, towerPos.y, tower.radiusFlyShellСircle.radius);
                    }
                }
            }
        }
        shapeRenderer.end();

        spriteBatch.begin();
        bitmapFont.setColor(Color.YELLOW);
        bitmapFont.getData().setScale(0.7f);
        for (Tower tower : towersManager.getAllTowers()) { // Draw pit capacity value
            if (tower.getTemplateForTower().towerAttackType == TowerAttackType.Pit) {
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
        for (Tower tower : towersManager.getAllTowers()) {
            for (Shell shell : tower.shells) {
                TextureRegion textureRegion = shell.textureRegion;
//                float width = textureRegion.getRegionWidth() * shell.ammoSize;
//                float height = textureRegion.getRegionHeight() * shell.ammoSize;
//                spriteBatch.draw(textureRegion, shell.currentPoint.x, shell.currentPoint.y, width, height);
                spriteBatch.draw(textureRegion, shell.currentPoint.x - shell.circle.radius, shell.currentPoint.y - shell.circle.radius, shell.circle.radius * 2, shell.circle.radius * 2);
//                Gdx.app.log("GameField", "drawProjecTiles(); -- Draw shell:" + shell.currentPoint);
            }
        }
    }

    private void drawTowersUnderConstruction(SpriteBatch spriteBatch) {
        if (underConstruction != null) {
            if (underConstruction.state == 0) {
                drawTowerUnderConstruction(spriteBatch, underConstruction.endX, underConstruction.endY, underConstruction.templateForTower);
            } else if (underConstruction.state == 1) {
                drawTowerUnderConstruction(spriteBatch, underConstruction.startX, underConstruction.startY, underConstruction.templateForTower);

                for (int k = 0; k < underConstruction.coorsX.size; k++) {
                    drawTowerUnderConstruction(spriteBatch, underConstruction.coorsX.get(k), underConstruction.coorsY.get(k), underConstruction.templateForTower);
                }
            }
        }
    }

    private void drawTowerUnderConstruction(SpriteBatch spriteBatch, int buildX, int buildY, TemplateForTower templateForTower) {
//        Gdx.app.log("GameField::drawTowerUnderConstruction()", " -- buildX:" + buildX + " buildY:" + buildY + " templateForTower:" + templateForTower);
        int sizeCellX = getSizeCellX();
        int sizeCellY = getSizeCellY();
        float halfSizeCellX = sizeCellX / 2;
        float halfSizeCellY = sizeCellY / 2;
        int towerSize = templateForTower.size;
        TextureRegion textureRegion = templateForTower.idleTile.getTextureRegion();

        Color oldColor = spriteBatch.getColor();

        boolean drawFull = true;
        boolean canBuild = true;
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
        for (int x = startX; x <= finishX; x++) {
            for (int y = startY; y <= finishY; y++) {
//                float pxlsX = halfSizeCellX*(buildY) + (buildX)*halfSizeCellX;
//                float pxlsY = (halfSizeCellY*(buildY) - (buildX)*halfSizeCellY) - halfSizeCellY*(towerSize-1);
                if (!cellIsEmpty(buildX + x, buildY + y)) {
                    if (drawFull) {
                        canBuild = false;
//                    } else {
//                        spriteBatch.setColor(1f, 0, 0, 0.55f);
//                        spriteBatch.draw(smallTextureRegions[x][y], pxlsX + (sizeCellX * y), pxlsY + ((sizeCellY * 2) * (towerSize - x - 1)), sizeCellX, sizeCellY * 2);
                    }
//                } else {
//                    spriteBatch.setColor(0, 1f, 0, 0.55f);
//                    spriteBatch.draw(smallTextureRegions[x][y], pxlsX + (sizeCellX * y), pxlsY + ((sizeCellY * 2) * (towerSize - x - 1)), sizeCellX, sizeCellY * 2);
                }
            }
        }
        if (drawFull) {
            if (canBuild)
                spriteBatch.setColor(0, 1f, 0, 0.55f);
            else
                spriteBatch.setColor(1f, 0, 0, 0.55f);

            Vector2 towerPos = new Vector2();
            Vector2 markPos = new Vector2();
            if(isDrawableTowers == 1 || isDrawableTowers == 5) {
                towerPos.set(getGraphicCoordinates(buildX, buildY, 1));
                towerPos.set(getCorrectGraphicTowerCoord(towerPos, towerSize, 1));
                spriteBatch.draw(textureRegion, towerPos.x, towerPos.y, sizeCellX * towerSize, (sizeCellY * 2) * towerSize);
                for (int x = startX; x <= finishX; x++) {
                    for (int y = startY; y <= finishY; y++) {
                        markPos.set(getGraphicCoordinates(buildX + x, buildY + y, 1));
                        markPos.add(-(halfSizeCellX), -(halfSizeCellY));
                        if (cellIsEmpty(buildX + x, buildY + y)) {
                            if (greenCheckmark != null)
                                spriteBatch.draw(greenCheckmark, markPos.x, markPos.y, sizeCellX, sizeCellY * 2);
                        } else {
                            if (redCross != null)
                                spriteBatch.draw(redCross, markPos.x, markPos.y, sizeCellX, sizeCellY * 2);
                        }
                    }
                }
            }
            if(isDrawableTowers == 2 || isDrawableTowers == 5) {
                towerPos.set(getGraphicCoordinates(buildX, buildY, 2));
                towerPos.set(getCorrectGraphicTowerCoord(towerPos, towerSize, 2));
                spriteBatch.draw(textureRegion, towerPos.x, towerPos.y, sizeCellX * towerSize, (sizeCellY * 2) * towerSize);
                for (int x = startX; x <= finishX; x++) {
                    for (int y = startY; y <= finishY; y++) {
                        markPos.set(getGraphicCoordinates(buildX + x, buildY + y, 2));
                        markPos.add(-(halfSizeCellX), -(halfSizeCellY));
                        if (cellIsEmpty(buildX + x, buildY + y)) {
                            if (greenCheckmark != null)
                                spriteBatch.draw(greenCheckmark, markPos.x, markPos.y, sizeCellX, sizeCellY * 2);
                        } else {
                            if (redCross != null)
                                spriteBatch.draw(redCross, markPos.x, markPos.y, sizeCellX, sizeCellY * 2);
                        }
                    }
                }
            }
            if(isDrawableTowers == 3 || isDrawableTowers == 5) {
                towerPos.set(getGraphicCoordinates(buildX, buildY, 3));
                towerPos.set(getCorrectGraphicTowerCoord(towerPos, towerSize, 3));
                spriteBatch.draw(textureRegion, towerPos.x, towerPos.y, sizeCellX * towerSize, (sizeCellY * 2) * towerSize);
                for (int x = startX; x <= finishX; x++) {
                    for (int y = startY; y <= finishY; y++) {
                        markPos.set(getGraphicCoordinates(buildX + x, buildY + y, 3));
                        markPos.add(-(halfSizeCellX), -(halfSizeCellY));
                        if (cellIsEmpty(buildX + x, buildY + y)) {
                            if (greenCheckmark != null)
                                spriteBatch.draw(greenCheckmark, markPos.x, markPos.y, sizeCellX, sizeCellY * 2);
                        } else {
                            if (redCross != null)
                                spriteBatch.draw(redCross, markPos.x, markPos.y, sizeCellX, sizeCellY * 2);
                        }
                    }
                }
            }
            if(isDrawableTowers == 4 || isDrawableTowers == 5) {
                towerPos.set(getGraphicCoordinates(buildX, buildY, 4));
                towerPos.set(getCorrectGraphicTowerCoord(towerPos, towerSize, 4));
                spriteBatch.draw(textureRegion, towerPos.x, towerPos.y, sizeCellX * towerSize, (sizeCellY * 2) * towerSize);
                for (int x = startX; x <= finishX; x++) {
                    for (int y = startY; y <= finishY; y++) {
                        markPos.set(getGraphicCoordinates(buildX + x, buildY + y, 4));
                        markPos.add(-(halfSizeCellX), -(halfSizeCellY));
                        if (cellIsEmpty(buildX + x, buildY + y)) {
                            if (greenCheckmark != null)
                                spriteBatch.draw(greenCheckmark, markPos.x, markPos.y, sizeCellX, sizeCellY * 2);
                        } else {
                            if (redCross != null)
                                spriteBatch.draw(redCross, markPos.x, markPos.y, sizeCellX, sizeCellY * 2);
                        }
                    }
                }
            }
            spriteBatch.setColor(oldColor);
        }
    }

    public void setSpawnPoint(int x, int y) {
        createCreep(new GridPoint2(x, y), factionsManager.getRandomTemplateForUnitFromFirstFaction(), null);
//        spawnPoint = new GridPoint2(x, y);
//        waveManager.spawnPoints.set(0, new GridPoint2(x, y));
    }

    public void setExitPoint(int x, int y) {
        waveManager.setExitPoint(new GridPoint2(x, y));
        rerouteForAllCreeps(new GridPoint2(x, y));
    }

    private void spawnCreeps(float delta) {
        Array<WaveManager.TemplateNameAndPoints> allCreepsForSpawn = waveManager.getAllCreepsForSpawn(delta);
        for (WaveManager.TemplateNameAndPoints templateNameAndPoints : allCreepsForSpawn) {
            spawnCreep(templateNameAndPoints);
        }
    }

    private void spawnCreep(WaveManager.TemplateNameAndPoints templateNameAndPoints) {
        if (templateNameAndPoints != null) {
            TemplateForUnit templateForUnit = factionsManager.getTemplateForUnitByName(templateNameAndPoints.templateName);
            if (templateForUnit != null) {
                createCreep(templateNameAndPoints.spawnPoint, templateForUnit, templateNameAndPoints.exitPoint);
            } else {
                Gdx.app.error("GameField", "spawnCreep(); -- templateForUnit == null | templateName:" + templateNameAndPoints.templateName);
            }
        }
    }

    public void createCreep(int x, int y) {
        createCreep(new GridPoint2(x, y), factionsManager.getRandomTemplateForUnitFromFirstFaction(), null);
    }

    private void createCreep(GridPoint2 spawnPoint, TemplateForUnit templateForUnit, GridPoint2 exitPoint) {
        Gdx.app.log("GameField::createCreep(" + spawnPoint + "," + templateForUnit + ", " + exitPoint + ");", " -- Start");
        if (exitPoint == null) {
            exitPoint = waveManager.lastExitPoint;
        }
        if (spawnPoint != null && exitPoint != null && pathFinder != null) {
            pathFinder.loadCharMatrix(getCharMatrix());
            ArrayDeque<Node> route = pathFinder.route(spawnPoint.x, spawnPoint.y, exitPoint.x, exitPoint.y);
            if (route != null) {
                Creep creep = creepsManager.createCreep(route, templateForUnit);
                field[spawnPoint.x][spawnPoint.y].setCreep(creep); // TODO field maybe out array
//            Gdx.app.log("GameField::createCreep()", " -- x:" + x + " y:" + y + " eX:" + waveManager.exitPoints.first().x + " eY:" + waveManager.exitPoints.first().y);
//            Gdx.app.log("GameField::createCreep()", " -- route:" + route);
            } else {

            }
        } else {
            Gdx.app.log("GameField::createCreep(" + spawnPoint + "," + templateForUnit + ", " + exitPoint + ");", " -- exitPoint:" + exitPoint + " pathFinder:" + pathFinder);
        }
    }

    public void buildTowersWithUnderConstruction(int x, int y) {
        if (underConstruction != null) {
            underConstruction.setEndCoors(x, y);
            createTower(underConstruction.startX, underConstruction.startY, underConstruction.templateForTower);
            for (int k = 0; k < underConstruction.coorsX.size; k++) {
//            for(int k = underConstruction.coorsX.size-1; k >= 0; k--) {
                createTower(underConstruction.coorsX.get(k), underConstruction.coorsY.get(k), underConstruction.templateForTower);
            }
            underConstruction.clearStartCoors();
            rerouteForAllCreeps();
        }
    }

    public void towerActions(int x, int y) {
        if (field[x][y].isEmpty()) {
            createTower(x, y, factionsManager.getRandomTemplateForTowerFromAllFaction());
            rerouteForAllCreeps();
        } else if (field[x][y].getTower() != null) {
            removeTower(x, y);
        }
    }

    public boolean createTower(int buildX, int buildY, TemplateForTower templateForTower) {
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
                    finishX = towerSize / 2;
                    finishY = towerSize / 2;
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
//                    finishX = towerSize / 2;
//                    finishY = towerSize / 2;
//                }
            }
            for (int tmpX = startX; tmpX <= finishX; tmpX++)
                for (int tmpY = startY; tmpY <= finishY; tmpY++)
                    if (!cellIsEmpty(buildX + tmpX, buildY + tmpY))
                        return false;

            // GOVNO CODE
            GridPoint2 position = new GridPoint2(buildX, buildY);
            Tower tower = towersManager.createTower(position, templateForTower);
            Gdx.app.log("GameField", "createTower(); -- templateForTower.towerAttackType:" + templateForTower.towerAttackType);
            if (templateForTower.towerAttackType != TowerAttackType.Pit) {
                for (int tmpX = startX; tmpX <= finishX; tmpX++)
                    for (int tmpY = startY; tmpY <= finishY; tmpY++)
                        field[buildX + tmpX][buildY + tmpY].setTower(tower);
            }
            // GOVNO CODE

//            rerouteForAllCreeps();
            gamerGold -= templateForTower.cost;
            Gdx.app.log("GameField::createTower()", " -- GamerGold:" + gamerGold);
            return true;
        } else {
            return false;
        }
    }

    public void removeTower(int touchX, int touchY) {
        Tower tower = field[touchX][touchY].getTower();
        if (tower != null) {
            int x = tower.getPosition().x;
            int y = tower.getPosition().y;
            int towerSize = tower.getTemplateForTower().size;
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
                }
            }
            towersManager.removeTower(tower);
            rerouteForAllCreeps();
//            gamerGold += (int) tower.getTemplateForTower().cost*0.5;
        }
    }

    private void rerouteForAllCreeps() {
        rerouteForAllCreeps(null);
    }

    private void rerouteForAllCreeps(GridPoint2 exitPoint) {
        if (pathFinder != null) {
            long start1 = System.nanoTime();
            Gdx.app.log("GameField", "rerouteForAllCreeps(); -- Start:" + start1);
            pathFinder.loadCharMatrix(getCharMatrix());
            for (Creep creep : creepsManager.getAllCreeps()) {
                ArrayDeque<Node> route;
                if (exitPoint == null) {
                    Node node = creep.getRoute().getLast();
                    GridPoint2 localExitPoint = new GridPoint2(node.getX(), node.getY());
                    route = pathFinder.route(creep.getNewPosition().getX(), creep.getNewPosition().getY(), localExitPoint.x, localExitPoint.y); // TODO BAGA!
                } else {
                    route = pathFinder.route(creep.getNewPosition().getX(), creep.getNewPosition().getY(), exitPoint.x, exitPoint.y); // TODO BAGA!
                }
                if (route != null) {
                    route.removeFirst();
                    creep.setRoute(route);
                }
//                    long end2 = System.nanoTime();
//                    Gdx.app.log("GameField", "rerouteForAllCreeps(); -- Thread End:" + (end2-start2));
//                }
//            }.init(creep, outExitPoint)).start();
            }
            long end1 = System.nanoTime();
            Gdx.app.log("GameField", "rerouteForAllCreeps(); -- Time End:" + (end1 - start1));
        } else {
            Gdx.app.log("GameField::rerouteForAllCreeps(" + exitPoint + ");", " -- pathFinder:" + pathFinder);
        }
    }

    private void stepAllCreep(float delta) {
        for (int i = 0; i < creepsManager.amountCreeps(); i++) {
            Creep creep = creepsManager.getCreep(i);
            Node oldPosition = creep.getNewPosition();
            if (creep.isAlive()) {
                Node newPosition = creep.move(delta);
                if (newPosition != null) {
                    if (!newPosition.equals(oldPosition)) {
                        field[oldPosition.getX()][oldPosition.getY()].removeCreep(creep);
                        field[newPosition.getX()][newPosition.getY()].setCreep(creep);
//                    Gdx.app.log("GameField::stepAllCreep()", "-- Creep move to X:" + newPosition.getX() + " Y:" + newPosition.getY());
                    }
                } else {
                    field[oldPosition.getX()][oldPosition.getY()].removeCreep(creep);
                    creepsManager.removeCreep(creep);
                    missedCreeps++;
//                Gdx.app.log("GameField::stepAllCreep()", "-- Creep finished!");
                }
            } else {
                if (!creep.changeDeathFrame(delta)) {
                    field[oldPosition.getX()][oldPosition.getY()].removeCreep(creep);
                    creepsManager.removeCreep(creep);
//                Gdx.app.log("GameField::stepAllCreep()", "-- Creep death! and delete!");
                }
            }
        }
    }

    private void shotAllTowers(float delta) { // AlexGor
        for (Tower tower : towersManager.getAllTowers()) {
            TowerAttackType towerAttackType = tower.getTemplateForTower().towerAttackType;
            if (towerAttackType == TowerAttackType.Pit) {
                Creep creep = field[tower.getPosition().x][tower.getPosition().y].getCreep();
                if (creep != null) {
                    Gdx.app.log("GameField", "shotAllTowers(); -- tower.capacity:" + tower.capacity + " creep.getHp:" + creep.getHp());
//                    creep.die(creep.getHp());
                    creepsManager.removeCreep(creep);
                    field[tower.getPosition().x][tower.getPosition().y].removeCreep(creep);
                    tower.capacity--;
                    if (tower.capacity <= 0) {
                        towersManager.removeTower(tower);
                    }
                }
//                Gdx.app.log("GameField::shotAllTowers(" + delta + ");", " -- towerAttackType.pit -- creep:" + creep);
            } else if (towerAttackType == TowerAttackType.Melee) {
                int radius = tower.getRadiusDetection();
                for (int tmpX = -radius; tmpX <= radius; tmpX++) {
                    for (int tmpY = -radius; tmpY <= radius; tmpY++) {
                        GridPoint2 position = tower.getPosition();
                        if (cellHasCreep(tmpX + position.x, tmpY + position.y)) {
                            Creep creep = field[tmpX + position.x][tmpY + position.y].getCreep();
//                            tower.shoot(creep);
                            creep.die(tower.getDamage(), tower.getTemplateForTower().shellEffectType);
                            return;
                        }
                    }
                }
            } else if (towerAttackType == TowerAttackType.Range) {
                if (tower.recharge(delta)) {
                    for (Creep creep : creepsManager.getAllCreeps()) {
                        if (Intersector.overlaps(tower.getRadiusDetectionСircle(), creep.circle1)) {
//                            Gdx.app.log("GameField", "shotAllTowers(); -- Intersector.overlaps(" + tower.toString() + ", " + creep.toString());
                            if (tower.shoot(creep)) {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private void moveAllShells(float delta) {
        for (Tower tower : towersManager.getAllTowers()) {
            tower.moveAllShells(delta);
        }
    } // AlexGor

//    public Array<TemplateForTower> getFirstTemplateForTowers() {
//        factionsManager.ge
//    }

    // GAME INTERFACE ZONE1
    public GridPoint2 whichCell(GridPoint2 grafCoordinate) {
        return whichCell.whichCell(grafCoordinate);
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
        if (missedCreeps >= maxOfMissedCreeps) {
//            Gdx.app.log("GameField::getGameState()", " -- LOSE!!");
            return "Lose";
        } else {
            if (waveManager.getNumberOfCreeps() == 0 && creepsManager.amountCreeps() == 0) {
//                Gdx.app.log("GameField::getGameState()", " -- WIN!!");
                return "Win";
            }
        }
//        Gdx.app.log("GameField::getGameState()", " -- IN PROGRESS!!");
        return "In progress";
    }

    public int getGamerGold() {
        return gamerGold;
    }

    public Array<TemplateForTower> getAllFirstTowersFromFirstFaction() {
        return factionsManager.getAllFirstTowersFromFirstFaction();
    }

    public Array<TemplateForTower> getAllTowers() {
        return factionsManager.getAllTowers();
    }

    public boolean createdUnderConstruction(TemplateForTower templateForTower) {
        if (underConstruction != null) {
            underConstruction.dispose();
        }
        underConstruction = new UnderConstruction(templateForTower);
        return true;
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

    private boolean cellIsEmpty(int x, int y) {
        if (x >= 0 && y >= 0) {
            if (x < sizeFieldX && y < sizeFieldY) {
                return field[x][y].isEmpty();
            }
        }
        return false;
    }

    private boolean cellHasCreep(int x, int y) {
        if (x >= 0 && y >= 0) {
            if (x < sizeFieldX && y < sizeFieldY) {
                return field[x][y].getCreep() != null;
            }
        }
        return false;
    }

    public Vector2 getGraphicCoordinates(int cellX, int cellY, int map) {
        float pxlsX = 0f, pxlsY = 0f;
        if(map == 1) { // Нижняя карта
            pxlsX = (-(halfSizeCellX * cellY) + (cellX * halfSizeCellX));
            pxlsY = (-(halfSizeCellY * cellY) - (cellX * halfSizeCellY));
        } else if(map == 2) { // Правая карта
            pxlsX = ( (halfSizeCellX * cellY) + (cellX * halfSizeCellX)) + halfSizeCellX;
            pxlsY = ( (halfSizeCellY * cellY) - (cellX * halfSizeCellY)) + halfSizeCellY;
        } else if(map == 3) { // Верхняя карта
            pxlsX = (-(halfSizeCellX * cellY) + (cellX * halfSizeCellX));
            pxlsY = ( (halfSizeCellY * cellY) + (cellX * halfSizeCellY)) + halfSizeCellY*2;
        } else if(map == 4) {// Левая карта
            pxlsX = (-(halfSizeCellX * cellY) - (cellX * halfSizeCellX)) - halfSizeCellX;
            pxlsY = ( (halfSizeCellY * cellY) - (cellX * halfSizeCellY)) + halfSizeCellY;
        }
//        Gdx.app.log("GameField::getGraphicCoordinates(" + cellX + "," + cellY + "," + map + ");", " -- pxlsX:" + pxlsX + " pxlsY:" + pxlsY);
        return new Vector2(pxlsX, pxlsY);
    }

    public Vector2 getCorrectGraphicTowerCoord(Vector2 towerPos, int towerSize, int map) {
        if(map == 1) {
            towerPos.add(-(halfSizeCellX * towerSize), -(halfSizeCellY * (towerSize - ((towerSize % 2 != 0) ? 0 : 1))));
        } else if(map == 2) {
            towerPos.add(-(halfSizeCellX*((towerSize % 2 != 0) ? towerSize : towerSize+1)), -(halfSizeCellY*towerSize));
        } else if(map == 3) {
            towerPos.add(-(halfSizeCellX*towerSize), -(halfSizeCellY * ((towerSize % 2 != 0) ? towerSize : towerSize+1)));
        } else if(map == 4) {
            towerPos.add(-(halfSizeCellX*(towerSize - ((towerSize % 2 != 0) ? 0 : 1))), -(halfSizeCellY*towerSize));
        } else {
            Gdx.app.log("GameField::getCorrectGraphicTowerCoord(" + towerPos + ", " + towerSize + ", " + map + ");", " -- Bad map[1-4] value:" + map);
        }
        return towerPos;
    }
}
