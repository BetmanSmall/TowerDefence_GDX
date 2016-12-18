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
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.WhichCell;
import com.betmansmall.game.gameLogic.mapLoader.MapLoader;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.PathFinder.Node;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.PathFinder.PathFinder;
import com.betmansmall.game.gameLogic.playerTemplates.FactionsManager;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;
import com.badlogic.gdx.math.Intersector; // AlexGor

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
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

    public boolean isDrawableTerrain = true;
    public boolean isDrawableGrid = true;
    public boolean isDrawableCreeps = true;
    public boolean isDrawableTowers = true;
    public boolean isDrawableRoutes = true;
    public boolean isDrawableGridNav = true;

    private MyBox2dContactListener myBox2dContactListener;
    private Box2DDebugRenderer debugRenderer;
    public static World world;

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

    public class MyBox2dContactListener implements ContactListener {
        @Override
        public void endContact(Contact contact) {
            Gdx.app.log("MyBox2dContactListener", "endContact(" + contact + ");");
        }

        @Override
        public void beginContact(Contact contact) {
            Gdx.app.log("MyBox2dContactListener", "beginContact(" + contact + ");");
            Creep creep = null;
            Tower tower = null;
            Object object1 = contact.getFixtureA().getUserData();
            Object object2 = contact.getFixtureB().getUserData();
            if (object1 instanceof Creep) {
                creep = (Creep) object1;
            } else if (object2 instanceof Creep) {
                creep = (Creep) object2;
            }
            if (object1 instanceof Tower) {
                tower = (Tower) object1;
            } else if (object2 instanceof Tower) {
                tower = (Tower) object2;
            }
            if (creep != null && tower != null) {
                Gdx.app.log("MyBox2dContactListener", "beginContact(); -- Creep:" + creep.getTemplateForUnit().name);
                Gdx.app.log("MyBox2dContactListener", "beginContact(); -- Tower:" + tower.getTemplateForTower().name);
            }
        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {
//            Gdx.app.log("MyBox2dContactListener", "preSolve(" + contact + ", " + oldManifold + ");");
        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {
//            Gdx.app.log("MyBox2dContactListener", "postSolve(" + contact + ", " + impulse+ ");");
        }
    }

    ;

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

//        myBox2dContactListener = new MyBox2dContactListener();
//        world = new World(new Vector2(0f, 0f), true);
//        world.setContactListener(myBox2dContactListener);
//        debugRenderer = new Box2DDebugRenderer();

        createField(sizeFieldX, sizeFieldY, map.getLayers());

//        TiledMapTileSets tileSets = map.getTileSets();
//        for (TiledMapTileSet tileSet : tileSets) {
//            String tileSetName = tileSet.getName();
//            Gdx.app.log("GameField::GameField()", "-- TileSet:" + tileSetName);
//            if (tileSetName.contains("unit")) {
//                TemplateForUnit templateForUnit = new TemplateForUnit(tileSet);
//                factionsManager.addUnitToFaction(templateForUnit);
//                if (animation == null) {
//                    AnimatedTiledMapTile animatedTiledMapTile = templateForUnit.animations.get("death_" + Direction.DOWN);
//                    StaticTiledMapTile[] staticTiledMapTiles = animatedTiledMapTile.getFrameTiles();
//                    Array<TextureRegion> textureRegions = new Array<TextureRegion>(staticTiledMapTiles.length);
////                    Gdx.app.log("GameField::GameField()", " -- textureRegion.size:" + staticTiledMapTiles.length);
//                    for (int k = 0; k < staticTiledMapTiles.length; k++) {
//                        TextureRegion textureRegion = staticTiledMapTiles[k].getTextureRegion();
//                        textureRegions.add(textureRegion);
//                    }
//                    stateTime = 0f;
//                    animation = new Animation(0.25f, textureRegions);
//                }
//            } else if (tileSetName.contains("tower")) {
//                TemplateForTower templateForTower = new TemplateForTower(tileSet);
//                factionsManager.addTowerToFaction(templateForTower);
//            }
//        }

        // GAME INTERFACE ZONE1
        whichCell = new WhichCell(sizeFieldX, sizeFieldY, sizeCellX, sizeCellY);
        gamePaused = true;
        gameSpeed = 1.0f;
        maxOfMissedCreeps = 7;
        missedCreeps = 0;
        gamerGold = 10000;
        // GAME INTERFACE ZONE2
    }

    private void createField(int sizeFieldX, int sizeFieldY, MapLayers mapLayers) {
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
                                    field[x][y].addTiledMapTile(tiledMapTile);
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
                                }
                            }
                        } else {
                            Gdx.app.log("GameField::createField()", " -- Не смог преобразовать MapLayer в TiledMapTileLayer");
                        }
                    }
                }
            }
            pathFinder = new PathFinder();
            pathFinder.loadCharMatrix(getCharMatrix());
        }
    }

    public char[][] getCharMatrix() {
        if (field != null) {
            char[][] charMatrix = new char[sizeFieldY][sizeFieldX];
            for (int y = 0; y < sizeFieldY; y++) {
                for (int x = 0; x < sizeFieldX; x++) {
                    if (field[x][y].isTerrain() || field[x][y].getTower() != null) {
                        if(field[x][y].getTower() != null && field[x][y].getTower().getTemplateForTower().type.equals("towerTerrain")) {
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
//        renderer.dispose();
//        renderer = null;
    }

    public void render(float delta, OrthographicCamera camera) {
        delta = delta*gameSpeed;
        if (!gamePaused) {
            spawnCreep(delta);
            stepAllCreep(delta);
            shotAllTowers(delta);
            moveAllShells(delta);
        }

        if (isDrawableTerrain) {
//            int halfSizeCellX = sizeCellX / 2;
//            int halfSizeCellY = sizeCellY / 2;
//            spriteBatch.setProjectionMatrix(camera.combined);
//            spriteBatch.begin();
//            for (int y = sizeFieldY - 1; y >= 0; y--) {
////                for(int x = sizeFieldX-1; x >= 0; x--) {
////                for(int y = 0; y < sizeFieldY; y++) {
//                for (int x = 0; x < sizeFieldX; x++) {
//                    Array<TiledMapTile> tiledMapTiles = field[x][y].getTiledMapTiles();
//                    for (TiledMapTile tiledMapTile : tiledMapTiles) {
//                        TextureRegion textureRegion = tiledMapTile.getTextureRegion();
//                        float pxlsX = (halfSizeCellX * y + x * halfSizeCellX);
//                        float pxlsY = (halfSizeCellY * y - x * halfSizeCellY);
//
//                        spriteBatch.draw(textureRegion, pxlsX, pxlsY);//, sizeCellX, sizeCellY*2); TODO NEED FIX!
//                    }
//                }
//            }
//            spriteBatch.end();
            renderer.setView(camera);
            renderer.render();
//            renderer.getBatch().begin();
//            for (MapLayer mapLayer : map.getLayers()) {
//                if (mapLayer instanceof TiledMapTileLayer) {
//                    TiledMapTileLayer layer = (TiledMapTileLayer) mapLayer;
//                    String background = layer.getProperties().get("background", String.class);
////                    if(background != null) {
//                        renderer.renderTileLayer(layer);
////                    }
//                }
//            }
//            renderer.getBatch().end();
        }

        if (isDrawableGrid)
            drawGrid(camera);
        //just workaround
        if (isDrawableCreeps && isDrawableTowers) {
            drawCreepsAndTowers(camera);
        } else {
            if (isDrawableTowers)
                drawTowers(camera);
            if (isDrawableCreeps)
                drawCreeps(camera);
        }
        if (isDrawableRoutes)
            drawRoutes(camera);
        if (isDrawableGridNav)
            drawGridNav(camera);
        drawShells(camera);
        drawTowersUnderConstruction(camera);

//        if (isDrawableTerrain) {
//            renderer.getBatch().begin();
//            for (MapLayer mapLayer : map.getLayers()) {
//                if (mapLayer instanceof TiledMapTileLayer) {
//                    TiledMapTileLayer layer = (TiledMapTileLayer) mapLayer;
//                    String background = layer.getProperties().get("background", String.class);
//                    if (background == null) {
//                        renderer.renderTileLayer(layer);
//                    }
//                }
//            }
//            renderer.getBatch().end();
//        }

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
        bitmapFont.getData().setScale(4);
        bitmapFont.setColor(Color.YELLOW);
        bitmapFont.draw(spriteBatch, String.valueOf("Gold amount: " + gamerGold), Gdx.graphics.getWidth() / 2 - 150, Gdx.graphics.getHeight() - 10);
        spriteBatch.end();

//        debugRenderer.render(world, camera.combined);
//        world.step(1 / 60f, 6, 2);
    }

    private void getPriorityMap() {
        priorityMap.clear();
        for (Tower tower : towersManager.getAllTowers()) {
            priorityMap.put(tower.getPosition().x * 1000 - tower.getPosition().y, tower);
        }
        for (Creep creep : creepsManager.getAllCreeps()) {
            List list;
            Integer key = creep.getNewPosition().getX() * 1000 - creep.getNewPosition().getY();
            if(priorityMap.containsKey(key) && (priorityMap.get(key) instanceof List)) {
                list = (List) priorityMap.get(key);
                list.add(creep);
                priorityMap.put(key, list);
            } else {
                list = new ArrayList<Object>();
                list.add(creep);
                priorityMap.put(creep.getNewPosition().getX() * 1000 - creep.getNewPosition().getY(), list);
            }
        }
    }

    private void drawCreepsAndTowers(OrthographicCamera camera) {
        getPriorityMap();
        for (Object obj : priorityMap.values()) {
            if (obj instanceof Tower) {
                drawTower((Tower) obj, camera);
            } else {
                for(Creep creep : (List<Creep>) obj) {
                    drawCreep(creep, camera);
                }
            }
        }
    }
    private void drawGrid(OrthographicCamera camera) {
        int widthForTop = sizeFieldY * halfSizeCellX; // A - B
        int heightForTop = sizeFieldY * halfSizeCellY; // B - Top
        int widthForBottom = sizeFieldX * halfSizeCellX; // A - C
        int heightForBottom = sizeFieldX * halfSizeCellY; // C - Bottom

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BROWN); // (100, 60, 21, 1f);

        for (int x = 0; x <= sizeFieldX; x++)
            shapeRenderer.line(x * halfSizeCellX, halfSizeCellY - x * halfSizeCellY, widthForTop + x * halfSizeCellX, halfSizeCellY + heightForTop - x * halfSizeCellY);
        for (int y = 0; y <= sizeFieldY; y++)
            shapeRenderer.line(y * halfSizeCellX, halfSizeCellY + y * halfSizeCellY, widthForBottom + y * halfSizeCellX, halfSizeCellY - heightForBottom + y * halfSizeCellY);

        shapeRenderer.end();
    }

    private void drawCreep(Creep creep, OrthographicCamera camera) { //TODO Need to refactor this
        int oldX = creep.getOldPosition().getX(), oldY = creep.getOldPosition().getY();
        int newX = creep.getNewPosition().getX(), newY = creep.getNewPosition().getY();
        float fVx = halfSizeCellX * newY + newX * halfSizeCellX;
        float fVy = halfSizeCellY * newY - newX * halfSizeCellY;

        float elapsedTime = creep.getElapsedTime(), speed = creep.getSpeed();
        if (newX < oldX && newY > oldY) {
            fVy -= (sizeCellY / speed) * (speed - elapsedTime);
        } else if (newX == oldX && newY > oldY) {
            fVx -= (sizeCellX / 2 / speed) * (speed - elapsedTime);
            fVy -= (sizeCellY / 2 / speed) * (speed - elapsedTime);
        } else if (newX > oldX && newY > oldY) {
            fVx -= (sizeCellX / speed) * (speed - elapsedTime);
        } else if (newX > oldX && newY == oldY) {
            fVx -= (sizeCellX / 2 / speed) * (speed - elapsedTime);
            fVy += (sizeCellY / 2 / speed) * (speed - elapsedTime);
        } else if (newX > oldX && newY < oldY) {
            fVy += (sizeCellY / speed) * (speed - elapsedTime);
        } else if (newX == oldX && newY < oldY) {
            fVx += (sizeCellX / 2 / speed) * (speed - elapsedTime);
            fVy += (sizeCellY / 2 / speed) * (speed - elapsedTime);
        } else if (newX < oldX && newY < oldY) {
            fVx += (sizeCellX / speed) * (speed - elapsedTime);
        } else if (newX < oldX && newY == oldY) {
            fVx += (sizeCellX / 2 / speed) * (speed - elapsedTime);
            fVy -= (sizeCellY / 2 / speed) * (speed - elapsedTime);
        }

//        int centerX = creep.getNewPosition().getX(), centerY = creep.getNewPosition().getY();
//        float centerVx = halfSizeCellX * centerY + centerX * halfSizeCellX;
//        float centerVy = halfSizeCellY * centerY - centerX * halfSizeCellY;
        creep.setGraphicalCoordinates(fVx + halfSizeCellX, fVy + halfSizeCellY);

        TextureRegion currentFrame;
        if (creep.isAlive()) {
            currentFrame = creep.getCurentFrame();
        } else {
            currentFrame = creep.getCurrentDeathFrame();
        }
        int deltaX = (currentFrame.getRegionWidth() - sizeCellX) / 2;
        int deltaY = (currentFrame.getRegionHeight() - sizeCellY) / 2;
        fVx -= deltaX;
        fVy -= deltaY;

        if (creep.isAlive()) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.BLACK);
            float spaceInHpBar = 1;
            float hpBarWidth = 30;
            float hpBarHeight = 7;
            float hpBarWidthSpace = (currentFrame.getRegionWidth() - hpBarWidth) / 2;
            float hpBarTopSpace = hpBarHeight;
            shapeRenderer.rect(fVx + hpBarWidthSpace, fVy + currentFrame.getRegionHeight() - hpBarTopSpace, hpBarWidth, hpBarHeight);
            shapeRenderer.setColor(Color.GREEN);
            int maxHP = creep.getTemplateForUnit().healthPoints;
            int hp = creep.getHp();
            hpBarWidth = hpBarWidth / maxHP * hp;
            shapeRenderer.rect(fVx + hpBarWidthSpace + spaceInHpBar, fVy + currentFrame.getRegionHeight() - hpBarTopSpace + spaceInHpBar, hpBarWidth - (spaceInHpBar * 2), hpBarHeight - (spaceInHpBar * 2));
//            shapeRenderer.setColor(Color.BLUE);
//            shapeRenderer.circle(fVx, fVy, 1f);
            shapeRenderer.end();
        }

//        creep.setGraphicalCoordinates(fVx, fVy);
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        spriteBatch.draw(currentFrame, fVx, fVy);
        spriteBatch.end();
    }

    private void drawCreeps(OrthographicCamera camera) {
        for (Creep creep : creepsManager.getAllCreeps()) {
            drawCreep(creep, camera);
        }
    }

    private void drawRoutes(OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BROWN); // (100, 60, 21, 1f);

        for (Creep creep : creepsManager.getAllCreeps()) {
            ArrayDeque<Node> route = creep.getRoute();

            if (route != null) {
                for (Node coor : route) {
                    int vX = coor.getX();
                    int vY = coor.getY() + 1; // LibGDX some problems. Have offset (0,0) coor.
                    float fVx = halfSizeCellX * vY + vX * halfSizeCellX;
                    float fVy = halfSizeCellY * vY - vX * halfSizeCellY;

                    shapeRenderer.circle(fVx, fVy, 5);
                }
            }
        }
        shapeRenderer.end();
    }

    private void drawTower(Tower tower, OrthographicCamera camera) {
//        Gdx.app.log("GameField", "drawTower(" + tower + ", " + camera + ");");
        spriteBatch.setProjectionMatrix(camera.combined);
        int x = tower.getPosition().x;
        int y = tower.getPosition().y;
        int towerSize = tower.getTemplateForTower().size;
        float pxlsX = (halfSizeCellX * y + x * halfSizeCellX) - halfSizeCellX * (towerSize - 1);
        float pxlsY = (halfSizeCellY * y - x * halfSizeCellY) - halfSizeCellY * (towerSize - ((towerSize % 2 != 0) ? 1 : 2));
        TextureRegion currentFrame = tower.getCurentFrame();
        spriteBatch.begin();
        spriteBatch.draw(currentFrame, pxlsX, pxlsY, sizeCellX * towerSize, (sizeCellY * 2) * towerSize);
        spriteBatch.end();
    }

    private void drawTowers(OrthographicCamera camera) {
        for (Tower tower : towersManager.getAllTowers()) {
            drawTower(tower, camera);
        }
    }

    private void drawGridNav(OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(Color.RED); // (100, 60, 21, 1f);

        for (int y = 0; y < sizeFieldY; y++) {
            for (int x = 0; x < sizeFieldX; x++) {
                float fVx = halfSizeCellX * (y + 1) + x * halfSizeCellX;
                float fVy = halfSizeCellY * (y + 1) - x * halfSizeCellY;
                if (!field[x][y].isEmpty()) {
                    if (field[x][y].isTerrain()) {
                        shapeRenderer.setColor(Color.RED);
                    } else if (field[x][y].getCreep() != null) {
                        shapeRenderer.setColor(Color.GREEN);
                    } else if (field[x][y].getTower() != null) {
                        shapeRenderer.setColor(Color.ORANGE);
                    }
                    shapeRenderer.circle(fVx, fVy, 3f);
                }
            }
        }

        shapeRenderer.setColor(Color.ORANGE);
        for(Tower tower: towersManager.getAllTowers()) {
            for(Shell shell : tower.shells) {
                shapeRenderer.rectLine(shell.currentPoint.x, shell.currentPoint.y, shell.endPoint.x, shell.endPoint.y, 1.5f);
                if(null != shell.circle) {
                    shapeRenderer.circle(shell.circle.x, shell.circle.y, shell.circle.radius);
                }
            }
        }
        float xPoint, yPoint;
        GridPoint2 spawnPoint = waveManager.getSpawnPoint();
        GridPoint2 exitPoint = waveManager.getExitPoint();

        if (spawnPoint != null) {
            shapeRenderer.setColor(Color.CYAN);
            xPoint = halfSizeCellX * (spawnPoint.y + 1) + spawnPoint.x * halfSizeCellX;
            yPoint = halfSizeCellY * (spawnPoint.y + 1) - spawnPoint.x * halfSizeCellY;
            shapeRenderer.circle(xPoint, yPoint, 3);
        }

        if (exitPoint != null) {
            shapeRenderer.setColor(Color.ORANGE);
            xPoint = halfSizeCellX * (exitPoint.y + 1) + exitPoint.x * halfSizeCellX;
            yPoint = halfSizeCellY * (exitPoint.y + 1) - exitPoint.x * halfSizeCellY;
            shapeRenderer.circle(xPoint, yPoint, 3);
        }

        shapeRenderer.setColor(Color.BLUE);
        for(Creep creep : creepsManager.getAllCreeps()) {
            shapeRenderer.circle(creep.graphicalCoordinateX, creep.graphicalCoordinateY, 1f);
        }

        shapeRenderer.setColor(Color.LIME);
        for(Creep creep : creepsManager.getAllCreeps()) {
            Circle rectangle = creep.getRect();
            shapeRenderer.circle(rectangle.x, rectangle.y, 2f);
        }

        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.setColor(Color.RED);
        for(Creep creep : creepsManager.getAllCreeps()) {
            Circle rectangle = creep.getRect();
//            shapeRenderer.box(rectangle.x, rectangle.y, 0, rectangle.width, rectangle.height, 0);
            shapeRenderer.circle(rectangle.x, rectangle.y, rectangle.radius);
        }

        shapeRenderer.setColor(Color.GREEN);
        for(Tower tower: towersManager.getAllTowers()) {
            shapeRenderer.setColor(Color.GREEN);
            Circle circle = tower.getCircle();
            shapeRenderer.circle(circle.x, circle.y, circle.radius);
//            for(Shell shell: tower.shells) {
//                shapeRenderer.setColor(Color.WHITE);
//                shapeRenderer.rectLine(shell.currentPoint.x, shell.currentPoint.y, shell.endPoint.x, shell.endPoint.y, 1.5f);
//                shapeRenderer.circle(shell.circle.x, shell.circle.y, shell.circle.radius);
//                shapeRenderer.setColor(Color.RED);
//                if(null != shell.circle) {
//                    shapeRenderer.circle(shell.circle.x, shell.circle.y, shell.circle.radius);
//                }
//            }
        }

        shapeRenderer.end();

        spriteBatch.begin();
        bitmapFont.setColor(Color.YELLOW);
        bitmapFont.getData().setScale(0.7f);
        shapeRenderer.setColor(Color.GREEN);
        for(Tower tower: towersManager.getAllTowers()) {
            if(tower.getTemplateForTower().type.equals("towerTerrain")) {
                bitmapFont.draw(spriteBatch, String.valueOf(tower.capacity), tower.getGraphCorX(), tower.getGraphCorY());
            }
        }
        spriteBatch.end();
    }

    private void drawShells(OrthographicCamera camera) {
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        for (Tower tower : towersManager.getAllTowers()) {
            for (Shell shell : tower.shells) {
                TextureRegion textureRegion = shell.textureRegion;
//                float width = textureRegion.getRegionWidth() * shell.ammoSize;
//                float height = textureRegion.getRegionHeight() * shell.ammoSize;
//                spriteBatch.draw(textureRegion, shell.currentPoint.x, shell.currentPoint.y, width, height);
                spriteBatch.draw(textureRegion, shell.currentPoint.x - shell.circle.radius, shell.currentPoint.y - shell.circle.radius, shell.circle.radius*2, shell.circle.radius*2);
//                Gdx.app.log("GameField", "drawProjecTiles(); -- Draw shell:" + shell.currentPoint);
            }
        }
        spriteBatch.end();
    }

    private void drawTowersUnderConstruction(OrthographicCamera camera) {
        if (underConstruction != null) {
            if (underConstruction.state == 0) {
                drawTowerUnderConstruction(camera, underConstruction.endX, underConstruction.endY, underConstruction.templateForTower);
            } else if (underConstruction.state == 1) {
                drawTowerUnderConstruction(camera, underConstruction.startX, underConstruction.startY, underConstruction.templateForTower);

                for (int k = 0; k < underConstruction.coorsX.size; k++) {
                    drawTowerUnderConstruction(camera, underConstruction.coorsX.get(k), underConstruction.coorsY.get(k), underConstruction.templateForTower);
                }
            }
        }
    }

    private void drawTowerUnderConstruction(OrthographicCamera camera, int buildX, int buildY, TemplateForTower templateForTower) {
//        Gdx.app.log("GameField::drawTowerUnderConstruction()", " -- buildX:" + buildX + " buildY:" + buildY + " templateForTower:" + templateForTower);
        int sizeCellX = getSizeCellX();
        int sizeCellY = getSizeCellY();
        float halfSizeCellX = sizeCellX / 2;
        float halfSizeCellY = sizeCellY / 2;
//        buildY = buildY+1;

        int towerSize = templateForTower.size;
        TextureRegion textureRegion = templateForTower.idleTile.getTextureRegion();
//        int pixSizeCellX = textureRegion.getRegionWidth() / towerSize;
//        int pixSizeCellY = textureRegion.getRegionHeight() / towerSize;
//        TextureRegion[][] smallTextureRegions = textureRegion.split(pixSizeCellX, pixSizeCellY);

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        Color oldColor = spriteBatch.getColor();

        boolean drawFull = true;
        boolean canBuild = true;
        int startX = 0, startY = 0, finishX = 0, finishY = 0;
        if (towerSize != 1) {
            if (towerSize % 2 == 0) {
                startX = -(towerSize / 2);
                startY = -((towerSize / 2) - 1);
                finishX = ((towerSize / 2) - 1);
                finishY = (towerSize / 2);
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
            float pxlsX = (halfSizeCellX * (buildY) + (buildX) * halfSizeCellX) - halfSizeCellX * (towerSize - 1);
            float pxlsY = (halfSizeCellY * (buildY) - (buildX) * halfSizeCellY) - halfSizeCellY * (towerSize - ((towerSize % 2 != 0) ? 1 : 2));

            if (canBuild)
                spriteBatch.setColor(0, 1f, 0, 0.55f);
            else
                spriteBatch.setColor(1f, 0, 0, 0.55f);

            spriteBatch.draw(textureRegion, pxlsX, pxlsY, sizeCellX * towerSize, (sizeCellY * 2) * towerSize);
            spriteBatch.setColor(oldColor);
            for (int x = startX; x <= finishX; x++) {
                for (int y = startY; y <= finishY; y++) {
                    pxlsX = halfSizeCellX * (buildY + y) + (buildX + x) * halfSizeCellX;
                    pxlsY = halfSizeCellY * (buildY + y) - (buildX + x) * halfSizeCellY;
                    if (cellIsEmpty(buildX + x, buildY + y)) {
                        if (greenCheckmark != null)
                            spriteBatch.draw(greenCheckmark, pxlsX, pxlsY, sizeCellX, sizeCellY * 2);
                    } else {
                        if (redCross != null)
                            spriteBatch.draw(redCross, pxlsX, pxlsY, sizeCellX, sizeCellY * 2);
                    }
                }
            }
        }
        spriteBatch.end();
    }

    public void setSpawnPoint(int x, int y) {
//        spawnPoint = new GridPoint2(x, y);
//        waveManager.spawnPoints.set(0, new GridPoint2(x, y));
    }

    public void setExitPoint(int x, int y) {
//        exitPoint = new GridPoint2(x, y);
//        waveManager.exitPoints.set(0, new GridPoint2(x, y));
        rerouteForAllCreeps();
    }

    private void spawnCreep(float delta) {
        String templateName = waveManager.getNextNameTemplateForUnitForSpawnCreep(delta);
        if (templateName != null) {
            GridPoint2 spawnPoint = waveManager.getSpawnPoint();
            GridPoint2 exitPoint = waveManager.getExitPoint();
            if (spawnPoint == null || !field[spawnPoint.x][spawnPoint.y].isEmpty()) {
                Gdx.app.log("GameField::spawnCreep()", " spawnPoint bad!");
                return;
            }
            if (exitPoint == null || !field[exitPoint.x][exitPoint.y].isEmpty()) {
                Gdx.app.log("GameField::spawnCreep()", " exitPoint bad!");
                return;
            }
            if (spawnPoint != null && exitPoint != null) {
                TemplateForUnit templateForUnit = factionsManager.getTemplateForUnitByName(templateName);
                if(templateForUnit != null) {
                    createCreep(spawnPoint.x, spawnPoint.y, templateForUnit);
                } else {
                    Gdx.app.error("GameField", "spawnCreep(); -- templateForUnit == null | templateName:" + templateName);
                }
            }
        }
    }

    public void createCreep(int x, int y) {
        createCreep(x, y, factionsManager.getRandomTemplateForUnitFromFirstFaction());
    }

    private void createCreep(int x, int y, TemplateForUnit templateForUnit) {
        GridPoint2 exitPoint = waveManager.getExitPoint();
        if (exitPoint != null) {
            pathFinder.loadCharMatrix(getCharMatrix());
            ArrayDeque<Node> route = pathFinder.route(x, y, exitPoint.x, exitPoint.y);

            if (route != null) {
                Creep creep = creepsManager.createCreep(route, templateForUnit);
                field[x][y].setCreep(creep); // TODO field maybe out array
//            Gdx.app.log("GameField::createCreep()", " -- x:" + x + " y:" + y + " eX:" + waveManager.exitPoints.first().x + " eY:" + waveManager.exitPoints.first().y);
//            Gdx.app.log("GameField::createCreep()", " -- route:" + route);
            }
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
            createTower(x, y, factionsManager.getRandomTemplateForTowerFromFirstFaction());
        } else if (field[x][y].getTower() != null) {
            removeTower(x, y);
        }
    }

    public boolean createTower(int buildX, int buildY, TemplateForTower templateForTower) {
        if (gamerGold >= templateForTower.cost) {
            int towerSize = templateForTower.size;
            int startX = 0, startY = 0, finishX = 0, finishY = 0;
            if (towerSize != 1) {
                if (towerSize % 2 == 0) {
                    startX = -(towerSize / 2);
                    startY = -((towerSize / 2) - 1);
                    finishX = ((towerSize / 2) - 1);
                    finishY = (towerSize / 2);
                } else {
                    startX = -(towerSize / 2);
                    startY = -(towerSize / 2);
                    finishX = towerSize / 2;
                    finishY = towerSize / 2;
                }
            }
            for (int tmpX = startX; tmpX <= finishX; tmpX++)
                for (int tmpY = startY; tmpY <= finishY; tmpY++)
                    if (!cellIsEmpty(buildX + tmpX, buildY + tmpY))
                        return false;

            GridPoint2 position = new GridPoint2(buildX, buildY);
            Tower tower = towersManager.createTower(position, templateForTower);
            Gdx.app.log("GameField", "createTower(); -- " + templateForTower.type);
            if(!templateForTower.type.equals("towerTerrain")) {
                for (int tmpX = startX; tmpX <= finishX; tmpX++)
                    for (int tmpY = startY; tmpY <= finishY; tmpY++)
                        field[buildX + tmpX][buildY + tmpY].setTower(tower);
            }

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
                    startY = -((towerSize / 2) - 1);
                    finishX = ((towerSize / 2) - 1);
                    finishY = (towerSize / 2);
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
        pathFinder.loadCharMatrix(getCharMatrix());
        for (Creep creep : creepsManager.getAllCreeps()) {
            ArrayDeque<Node> route = pathFinder.route(creep.getNewPosition().getX(), creep.getNewPosition().getY(), creep.getRoute().getLast().getX(), creep.getRoute().getLast().getY()); // TODO BAGA!
            if (route != null) {
                route.removeFirst();
                creep.setRoute(route);
            }
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
            if(!tower.getTemplateForTower().type.equals("towerTerrain")) {
                if (tower.recharge(delta)) {
                    for (Creep creep : creepsManager.getAllCreeps()) {
                        if (Intersector.overlaps(tower.getCircle(), creep.getRect())) {
//                            Gdx.app.log("GameField", "shotAllTowers(); -- Intersector.overlaps(" + tower.toString() + ", " + creep.toString());
                            if (tower.shoot(creep)) {
                                break;
                            }
                        }
                    }
                }
            } else {
                Creep creep = field[tower.getPosition().x][tower.getPosition().y].getCreep();
                if(creep != null) {
                    Gdx.app.log("GameField", "shotAllTowers(); -- tower.capacity:" + tower.capacity + " creep.getHp:" + creep.getHp());
//                    creep.die(creep.getHp());
                    creepsManager.removeCreep(creep);
                    field[tower.getPosition().x][tower.getPosition().y].removeCreep(creep);
                    tower.capacity--;
                    if(tower.capacity <= 0) {
                        towersManager.removeTower(tower);
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
}
