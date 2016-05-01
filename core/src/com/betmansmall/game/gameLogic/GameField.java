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
import com.badlogic.gdx.math.Vector3;
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
    private static int sizeCellX, sizeCellY;
    public int getSizeFieldX() {
        return sizeFieldX;
    } public int getSizeFieldY() {
        return sizeFieldY;
    }
    public static int getSizeCellX() {
        return sizeCellX;
    } public static int getSizeCellY() {
        return sizeCellY;
    }

    public boolean isDrawableGrid = true;
    public boolean isDrawableCreeps = true;
    public boolean isDrawableTowers = true;
    public boolean isDrawableRoutes = true;
    public boolean isDrawableGridNav = false;

    private Cell[][] field;
    private GridNav gridNav;

    private WaveManager waveManager;
    private CreepsManager creepsManager;
    private TowersManager towersManager;
    private FactionsManager factionsManager;

    private UnderConstruction underConstruction;
    // GAME INTERFACE ZONE1
    private WhichCell whichCell;
    private boolean gamePaused;
    private int maxOfMissedCreeps;
    private int missedCreeps;
    public static int gamerGold;
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

        underConstruction = null;

        createField(sizeFieldX, sizeFieldY, map.getLayers());

		TiledMapTileSets tileSets = map.getTileSets();
		for(TiledMapTileSet tileSet:tileSets) {
            String tileSetName = tileSet.getName();
            Gdx.app.log("GameField::GameField()", "-- TileSet:" + tileSetName);
            if(tileSetName.contains("unit")) {
                TemplateForUnit templateForUnit = new TemplateForUnit(tileSet);
                factionsManager.addUnitToFaction(templateForUnit);
                if(animation == null) {
                    AnimatedTiledMapTile animatedTiledMapTile = templateForUnit.animations.get("death_" + Direction.DOWN);
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
                TemplateForTower templateForTower = new TemplateForTower(tileSet);
                factionsManager.addTowerToFaction(templateForTower);
            }
		}

        // GAME INTERFACE ZONE1
        whichCell = new WhichCell(sizeFieldX, sizeFieldY, sizeCellX, sizeCellY);
        gamePaused = true;
        maxOfMissedCreeps = 7;
        missedCreeps = 0;
        gamerGold = 100;
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
		renderer.dispose();
		renderer = null;
    }

    public void render(float delta, OrthographicCamera camera) {
        int x = Gdx.input.getX();
        int y = Gdx.input.getY();
        Vector3 touch = new Vector3(x, y, 0);
        camera.unproject(touch);
        GridPoint2 grafCoordinate = new GridPoint2((int) touch.x, (int) touch.y);
        GridPoint2 cellCoordinate = whichCell(grafCoordinate);
        if(cellCoordinate != null) {
            if(underConstruction != null) {
                underConstruction.setEndCoors(cellCoordinate.x, cellCoordinate.y);
            }
            Gdx.app.log("GameField::render()", " -- x:" + cellCoordinate.x + " y:" + cellCoordinate.y);
        }

        renderer.setView(camera);
        renderer.render();

        if(!gamePaused) {
            spawnCreep(delta);
            stepAllCreep(delta);
            shotAllTowers(delta);
            moveAllProjecTiles(delta);
        }

        if(isDrawableGrid)
            drawGrid(camera);
        if(isDrawableTowers)
            drawTowers(camera);
        if(isDrawableCreeps)
            drawCreeps(camera);
        if(isDrawableRoutes)
            drawRoutes(camera);
        if(isDrawableGridNav)
            drawGridNav(camera);
        drawProjecTiles(camera);
        drawTowersUnderConstruction(camera);

        if(animation != null) {
            stateTime += delta;
            TextureRegion currentFrame = animation.getKeyFrame(stateTime, true); // #16
            spriteBatch.begin();
            spriteBatch.draw(currentFrame, 50, 300, 700, 700); // #17
//            bitmapFont.draw(spriteBatch, getGamerGold(), Gdx.graphics.getWidth()/2-10, Gdx.graphics.getHeight())
//            bitmapFont.draw(spriteBatch, String.valueOf(getGamerGold()), Gdx.graphics.getWidth()/2-10, Gdx.graphics.getHeight()-10);
            spriteBatch.end();
        }
        spriteBatch.begin();
        bitmapFont.getData().setScale(4);
        bitmapFont.setColor(Color.YELLOW);
        bitmapFont.draw(spriteBatch, String.valueOf("Gold amount: " + gamerGold), Gdx.graphics.getWidth() / 2 - 150, Gdx.graphics.getHeight() - 10);
        spriteBatch.end();
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

//        for(Creep creep: creepsManager.getAllCreeps()) {
        for(int k = creepsManager.getAllCreeps().size-1; k >= 0; k--) {
            Creep creep = creepsManager.getCreep(k);
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

            TextureRegion curentFrame;
            if(creep.isAlive()) {
                curentFrame = creep.getCurentFrame();
            } else {
                curentFrame = creep.getCurrentDeathFrame();
            }
            int deltaX = (curentFrame.getRegionWidth()-sizeCellX)/2;
            int deltaY = (curentFrame.getRegionHeight()-sizeCellY)/2;
            fVx -= deltaX;
            fVy -= deltaY;

            if(creep.isAlive()) {
                shapeRenderer.setProjectionMatrix(camera.combined);
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(Color.BLACK);
                float spaceInHpBar = 1;
                float hpBarWidth = 30;
                float hpBarHeight = 7;
                float hpBarWidthSpace = (curentFrame.getRegionWidth() - hpBarWidth) / 2;
                float hpBarTopSpace = hpBarHeight;
                shapeRenderer.rect(fVx + hpBarWidthSpace, fVy + curentFrame.getRegionHeight() - hpBarTopSpace, hpBarWidth, hpBarHeight);
                shapeRenderer.setColor(Color.GREEN);
                int maxHP = creep.getTemplateForUnit().healthPoints;
                int hp = creep.getHp();
                hpBarWidth = hpBarWidth / maxHP * hp;
                shapeRenderer.rect(fVx + hpBarWidthSpace + spaceInHpBar, fVy + curentFrame.getRegionHeight() - hpBarTopSpace + spaceInHpBar, hpBarWidth - (spaceInHpBar * 2), hpBarHeight - (spaceInHpBar * 2));
//                shapeRenderer.setColor(Color.BLUE); // (100, 60, 21, 1f);
//                shapeRenderer.circle(fVx, fVy, 1);
                shapeRenderer.end();
            }

            spriteBatch.setProjectionMatrix(camera.combined);
            spriteBatch.begin();
            spriteBatch.draw(curentFrame, fVx, fVy);
            spriteBatch.end();

            creep.setGraphicalCoordinates(fVx, fVy); // TODO GAVNO KODE

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

    private void drawProjecTiles(OrthographicCamera camera) {
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        for(Tower tower: towersManager.getAllTowers()) {
            for(ProjecTile projecTile: tower.projecTiles) {
                TextureRegion textureRegion = projecTile.textureRegion;
                float width = textureRegion.getRegionWidth()*projecTile.ammoSize;
                float height = textureRegion.getRegionHeight()*projecTile.ammoSize;
                spriteBatch.draw(textureRegion, projecTile.x, projecTile.y, width, height);
            }
        }
        spriteBatch.end();
    }

    private void drawTowersUnderConstruction(OrthographicCamera camera) {
        if(underConstruction != null) {
            if(underConstruction.state == 0) {
                drawTowerUnderConstruction(camera, underConstruction.endX, underConstruction.endY, underConstruction.templateForTower);
            } else if(underConstruction.state == 1) {
                drawTowerUnderConstruction(camera, underConstruction.startX, underConstruction.startY, underConstruction.templateForTower);

                for(int k = 0; k < underConstruction.coorsX.size; k++) {
                    drawTowerUnderConstruction(camera, underConstruction.coorsX.get(k), underConstruction.coorsY.get(k), underConstruction.templateForTower);
                }
            }
        }
    }

    private void drawTowerUnderConstruction(OrthographicCamera camera, int buildX, int buildY, TemplateForTower templateForTower) {
        Gdx.app.log("GameField::drawTowerUnderConstruction()", " -- buildX:" + buildX + " buildY:" + buildY + " templateForTower:" + templateForTower);
        int sizeCellX = getSizeCellX();
        int sizeCellY = getSizeCellY();
        float halfSizeCellX = sizeCellX/2;
        float halfSizeCellY = sizeCellY/2;
//        buildY = buildY+1;

        int towerSize = templateForTower.size;
        TextureRegion textureRegion = templateForTower.idleTile.getTextureRegion();
        int pixSizeCellX = textureRegion.getRegionWidth() / towerSize;
        int pixSizeCellY = textureRegion.getRegionHeight() / towerSize;
        TextureRegion[][] smallTextureRegions = textureRegion.split(pixSizeCellX, pixSizeCellY);

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        Color oldColor = spriteBatch.getColor();
        for(int x = 0; x < towerSize; x++) {
            for(int y = 0; y < towerSize; y++) {
                float pxlsX = halfSizeCellX*(buildY) + (buildX)*halfSizeCellX;
                float pxlsY = (halfSizeCellY*(buildY) - (buildX)*halfSizeCellY) - halfSizeCellY*(towerSize-1);

                if(cellIsEmpty(buildX+x, buildY+y)) {
//                        p.setOpacity(0.5);
//                        p.drawPixmap(pxlsX - sizeCellX/2, pxlsY + sizeCellY - (sizeCellY*2)*height, sizeCellX, (sizeCellY*2)*height, pix);
                    spriteBatch.setColor(0, 1f, 0, 0.55f);
                    spriteBatch.draw(smallTextureRegions[x][y], pxlsX+(sizeCellX*y), pxlsY+((sizeCellY*2)*(towerSize-x-1)), sizeCellX, sizeCellY*2);
//                    p.setOpacity(1);
                } else {
                    spriteBatch.setColor(1f, 0, 0, 0.55f);
                    spriteBatch.draw(smallTextureRegions[x][y], pxlsX+(sizeCellX*y), pxlsY+((sizeCellY*2)*(towerSize-x-1)), sizeCellX, sizeCellY*2);
//                        p.setOpacity(0.5);
//                        p.drawPixmap(pxlsX - sizeCellX/2, pxlsY + sizeCellY - (sizeCellY*2)*height, sizeCellX, (sizeCellY*2)*height, pix);
//
//                        QPainter painter(&pix);
//                        painter.setCompositionMode(QPainter::CompositionMode_SourceIn);
//                        painter.fillRect(pix.rect(), cRed);
//                        painter.end();
//                        p.drawPixmap(pxlsX - sizeCellX/2, pxlsY + sizeCellY - (sizeCellY*2)*height, sizeCellX, (sizeCellY*2)*height, pix);
                }
            }
        }
//        float pxlsX = halfSizeCellX*(buildY) + (buildX)*halfSizeCellX;
//        float pxlsY = (halfSizeCellY*(buildY) - (buildX)*halfSizeCellY) - halfSizeCellY*(towerSize-1);
//        spriteBatch.draw(smallTextureRegions[0][0], pxlsX,              pxlsY+sizeCellY*2, sizeCellX, sizeCellY*2);
//        spriteBatch.draw(smallTextureRegions[0][1], pxlsX+sizeCellX,    pxlsY+sizeCellY*2, sizeCellX, sizeCellY*2);
//        spriteBatch.draw(smallTextureRegions[1][0], pxlsX,              pxlsY, sizeCellX, sizeCellY*2);
//        spriteBatch.draw(smallTextureRegions[1][1], pxlsX+sizeCellX,    pxlsY, sizeCellX, sizeCellY*2);

//        spriteBatch.draw(textureRegion.getTexture(), pxlsX,             pxlsY+sizeCellY*2,  sizeCellX, sizeCellY*2, 0, 0, (int)pixSizeCellX, (int)pixSizeCellY, false, false);
//        spriteBatch.draw(textureRegion.getTexture(), pxlsX,             pxlsY+sizeCellY*2,  sizeCellX, sizeCellY*2, pixSizeCellX, -sizeCellY, 0f, 0f);
//        spriteBatch.draw(textureRegion.getTexture(), pxlsX+sizeCellX,   pxlsY+sizeCellY*2,  sizeCellX, sizeCellY*2, 32, 0,  (int)pixSizeCell, (int)pixSizeCell, false, false);
//        spriteBatch.draw(textureRegion.getTexture(), pxlsX,             pxlsY,              sizeCellX, sizeCellY*2, 0,  32, (int)pixSizeCell, (int)pixSizeCell, false, false);
//        spriteBatch.draw(textureRegion.getTexture(), pxlsX+sizeCellX,   pxlsY,              sizeCellX, sizeCellY*2, 32, 32, (int)pixSizeCell, (int)pixSizeCell, false, false);

//        spriteBatch.draw(smallTextureRegions[0][0], pxlsX,                      pxlsY+sizeCellY*2+sizeCellY*2, sizeCellX, sizeCellY*2);
//        spriteBatch.draw(smallTextureRegions[0][1], pxlsX+sizeCellX,            pxlsY+sizeCellY*2+sizeCellY*2, sizeCellX, sizeCellY*2);
//        spriteBatch.draw(smallTextureRegions[0][2], pxlsX+sizeCellX+sizeCellX,  pxlsY+sizeCellY*2+sizeCellY*2, sizeCellX, sizeCellY*2);
//        spriteBatch.draw(smallTextureRegions[1][0], pxlsX,                      pxlsY+sizeCellY*2, sizeCellX, sizeCellY*2);
//        spriteBatch.draw(smallTextureRegions[1][1], pxlsX+sizeCellX,            pxlsY+sizeCellY*2, sizeCellX, sizeCellY*2);
//        spriteBatch.draw(smallTextureRegions[1][2], pxlsX+sizeCellX+sizeCellX,  pxlsY+sizeCellY*2, sizeCellX, sizeCellY*2);
//        spriteBatch.draw(smallTextureRegions[2][0], pxlsX,                      pxlsY, sizeCellX, sizeCellY*2);
//        spriteBatch.draw(smallTextureRegions[2][1], pxlsX+sizeCellX,            pxlsY, sizeCellX, sizeCellY*2);
//        spriteBatch.draw(smallTextureRegions[2][2], pxlsX+sizeCellX+sizeCellX,  pxlsY, sizeCellX, sizeCellY*2);

//        spriteBatch.draw(textureRegion.getTexture(), pxlsX,                     pxlsY+sizeCellY*2+sizeCellY*2,  sizeCellX, sizeCellY*2, 0, 0, (int)pixSizeCell, (int)pixSizeCell, false, false);
//        spriteBatch.draw(textureRegion.getTexture(), pxlsX+sizeCellX,           pxlsY+sizeCellY*2+sizeCellY*2,  sizeCellX, sizeCellY*2, 22, 0,  (int)pixSizeCell, (int)pixSizeCell, false, false);
//        spriteBatch.draw(textureRegion.getTexture(), pxlsX+sizeCellX+sizeCellX, pxlsY+sizeCellY*2+sizeCellY*2,  sizeCellX, sizeCellY*2, 43, 0,  (int)pixSizeCell, (int)pixSizeCell, false, false);
//        spriteBatch.draw(textureRegion.getTexture(), pxlsX,                     pxlsY+sizeCellY*2,              sizeCellX, sizeCellY*2, 0,  22, (int)pixSizeCell, (int)pixSizeCell, false, false);
//        spriteBatch.draw(textureRegion.getTexture(), pxlsX+sizeCellX,           pxlsY+sizeCellY*2,              sizeCellX, sizeCellY*2, 22, 22, (int)pixSizeCell, (int)pixSizeCell, false, false);
//        spriteBatch.draw(textureRegion.getTexture(), pxlsX+sizeCellX+sizeCellX, pxlsY+sizeCellY*2,              sizeCellX, sizeCellY*2, 43, 22,  (int)pixSizeCell, (int)pixSizeCell, false, false);
//        spriteBatch.draw(textureRegion.getTexture(), pxlsX,                     pxlsY,                          sizeCellX, sizeCellY*2, 0,  43, (int)pixSizeCell, (int)pixSizeCell, false, false);
//        spriteBatch.draw(textureRegion.getTexture(), pxlsX+sizeCellX,           pxlsY,                          sizeCellX, sizeCellY*2, 22, 43, (int)pixSizeCell, (int)pixSizeCell, false, false);
//        spriteBatch.draw(textureRegion.getTexture(), pxlsX+sizeCellX+sizeCellX, pxlsY,                          sizeCellX, sizeCellY*2, 43, 43,  (int)pixSizeCell, (int)pixSizeCell, false, false);

        spriteBatch.setColor(oldColor);
        spriteBatch.end();
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
        Integer templateIndex = waveManager.getNextIndexTemplateForUnitForSpawnCreep(delta);
        if(templateIndex != null) {
            if(templateIndex >= 0) {
                GridPoint2 spawnPoint = waveManager.spawnPoints.first();
                if(spawnPoint != null) {
                    createCreep(spawnPoint.x, spawnPoint.y, factionsManager.getTemplateForUnitFromFirstFactionByIndex(templateIndex));
                }
            }
        }
    }

    public void createCreep(int x, int y) {
        createCreep(x, y, factionsManager.getRandomTemplateForUnitFromFirstFaction());
    }

    private void createCreep(int x, int y, TemplateForUnit templateForUnit) {
        gridNav.loadCharMatrix(getCharMatrix());
        ArrayDeque<Vertex> route = gridNav.route(x, y, waveManager.exitPoints.first().x, waveManager.exitPoints.first().y);

        if(route != null) {
            Creep creep = creepsManager.createCreep(route, templateForUnit);
            field[x][y].setCreep(creep); // TODO field maybe out array
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
        TemplateForTower templateForTower = factionsManager.getRandomTemplateForTowerFromFirstFaction();
        if(gamerGold >= templateForTower.cost) {
            int towerSize = templateForTower.size;
            for(int tmpX = 0; tmpX < towerSize; tmpX++)
                for(int tmpY = 0; tmpY < towerSize; tmpY++)
                    if(!cellIsEmpty(tmpX+x, tmpY+y))
                        return false;

            GridPoint2 position = new GridPoint2(x, y);
            Tower tower = towersManager.createTower(position, templateForTower);
            for(int tmpX = 0; tmpX < towerSize; tmpX++)
                for(int tmpY = 0; tmpY < towerSize; tmpY++)
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
        for(int i = 0; i < creepsManager.amountCreeps(); i++) {
            Creep creep = creepsManager.getCreep(i);
            Vertex oldPosition = creep.getNewPosition();
            if(creep.isAlive()) {
                Vertex newPosition = creep.move(delta);
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
                if(!creep.changeDeathFrame(delta)) {
                    field[oldPosition.getX()][oldPosition.getY()].removeCreep(creep);
                    creepsManager.removeCreep(creep);
//                Gdx.app.log("GameField::stepAllCreep()", "-- Creep death! and delete!");
                }
            }
        }
    }

    private void shotAllTowers(float delta) {
        for(Tower tower: towersManager.getAllTowers()) {
            if(tower.recharge(delta)) {
                int radius = tower.getRadius();
                for(int tmpX = -radius; tmpX <= radius; tmpX++) {
                    for(int tmpY = -radius; tmpY <= radius; tmpY++) {
                        GridPoint2 position = tower.getPosition();
                        if(cellHasCreep(tmpX+position.x, tmpY+position.y)) {
                            Creep creep = field[tmpX+position.x][tmpY+position.y].getCreep();
                            tower.shoot(creep);
                            return;
                        }
                    }
                }
            }
        }
    }

    private void moveAllProjecTiles(float delta) {
        for(Tower tower: towersManager.getAllTowers()) {
            tower.moveAllProjecTiles(delta);
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
        if(missedCreeps >= maxOfMissedCreeps) {
//            Gdx.app.log("GameField::getGameState()", " -- LOSE!!");
            return "Lose";
        } else {
            if(waveManager.getNumberOfCreeps() == 0 && creepsManager.amountCreeps() == 0) {
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

    public boolean createdUnderConstruction(TemplateForTower templateForTower) {
        if(underConstruction != null) {
            underConstruction.dispose();
        }
        underConstruction = new UnderConstruction(templateForTower);
        return true;
    }
    // GAME INTERFACE ZONE2

    private boolean cellIsEmpty(int x, int y) {
        if(x >= 0 && y >= 0) {
            if(x < sizeFieldX && y < sizeFieldY) {
                return field[x][y].isEmpty();
            }
        }
        return false;
    }

    private boolean cellHasCreep(int x, int y) {
        if(x >= 0 && y >= 0) {
            if(x < sizeFieldX && y < sizeFieldY) {
                return field[x][y].getCreep() != null;
            }
        }
        return false;
    }
}
