package com.betmansmall.maps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.betmansmall.game.gameLogic.playerTemplates.Direction;
import com.betmansmall.maps.jsons.TileSet;
import com.betmansmall.maps.xmls.Tile;
import com.betmansmall.utils.logging.Logger;
import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.betmansmall.maps.AutoTiler.TILE_BITS.*;

public class AutoTiler implements Runnable {
    private static final byte MATCH_ANY = 127;

    public enum TILE_BITS {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }

    public int mapWidth = 32;
    public int mapHeight = 16;
    private Random random;

    private TileSet tileSet;
    private TiledMapTileSet tiledMapTiles;
    private TmxMap map;
    private TiledMapTileLayer mapLayer;
    private StaticTiledMapTile nullTile;

    private Thread thread = null;
    private int timeSleep = 2;
    private int order = 28;

    public AutoTiler(FileHandle tilesetFile) {
        tileSet = TsxLoader.loadTileSet(tilesetFile);
        tiledMapTiles = TsxLoader.loadTiledMapTiles(tilesetFile, tileSet);
        map = new TmxMap(new TiledMap(), "");
        init(mapWidth, mapHeight);
    }

    private void init(int mapWidth, int mapHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.random = new Random();
        map.width = mapWidth;
        map.height = mapHeight;
        map.tileWidth = Integer.parseInt(tileSet.tilewidth);
        map.tileHeight = Integer.parseInt(tileSet.tileheight);

        nullTile = new StaticTiledMapTile(new TextureRegion(new Texture(Gdx.files.internal("maps/textures/redTexture.png"))));
        nullTile.getTextureRegion().setRegionWidth(map.tileWidth);
        nullTile.getTextureRegion().setRegionHeight(map.tileHeight);
//        nullTile.getTextureRegion().getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        nullTile.setId(-1);
        Logger.logFuncEnd("this:" + this);
    }

    public void setTimeSleep(boolean b) {
        if (timeSleep > 0 && timeSleep < 1000) {
            if (timeSleep <= 100) {
                if (timeSleep <= 10) {
                    if (!b) {
                        timeSleep -= 2;
                    } else {
                        timeSleep += 2;
                    }
                } else {
                    if (!b) {
                        timeSleep -= 10;
                    } else {
                        timeSleep += 10;
                    }
                }
            } else {
                if (!b) {
                    timeSleep -= 100;
                } else {
                    timeSleep += 100;
                }
            }
        } else {
            timeSleep = 500;
        }
        Logger.logFuncStart("timeSleep:" + timeSleep);
    }

    public TmxMap generateMap(FileHandle fileHandle) {
        Logger.logFuncStart("fileHandle:" + fileHandle);
        tileSet = TsxLoader.loadTileSet(fileHandle);
        if (tileSet.terrainTypes != null) {
            tiledMapTiles = TsxLoader.loadTiledMapTiles(fileHandle, tileSet);
            map.tileWidth = Integer.parseInt(tileSet.tilewidth);
            map.tileHeight = Integer.parseInt(tileSet.tileheight);

            map.isometric = false;
            if (tileSet.grid != null) {
                if (tileSet.grid.orientation.equals("isometric")) {
                    map.isometric = true;
                }
            }
            return generateMap();
        }
        return null;
    }

    public TmxMap generateMap() {
        Logger.logWithTime("currentThread:" + Thread.currentThread().toString());
        Logger.logDebug("thread:" + thread);
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
        Logger.logDebug("order:" + order);
        Logger.logFuncEnd(Thread.currentThread().toString());
        return map;
    }

    @Override
    public void run() {
        Logger.logWithTime(Thread.currentThread().toString());
        try {
            if (map.getLayers().size() != 0) {
                map.getLayers().remove(0); // .remove(mapLayer);
            }
            if (map.isometric) {
                mapLayer = new TiledMapTileLayer(mapWidth, mapHeight, map.tileWidth, map.tileHeight / 2);
            } else {
                mapLayer = new TiledMapTileLayer(mapWidth, mapHeight, map.tileWidth, map.tileHeight);
            }
            map.getLayers().add(mapLayer);
            switch (order) {
                case 0: {
                    for (int row = 0; row < mapHeight; row++) {
                        for (int col = 0; col < mapWidth; col++) {
                            if (!makeCell(col, row)) {
                                col -= 2;
                            }
                        }
                    }
                    break;
                }
                case 1: {
                    for (int row = mapHeight - 1; row >= 0; row--) {
                        for (int col = 0; col < mapWidth; col++) {
                            if (!makeCell(col, row)) {
                                col -= 2;
                            }
                        }
                    }
                    break;
                }
                case 2: {
                    for (int row = 0; row < mapHeight; row++) {
                        for (int col = mapWidth - 1; col >= 0; col--) {
                            if (!makeCell(col, row)) {
                                col += 2;
                            }
                        }
                    }
                    break;
                }
                case 3: {
                    for (int row = mapHeight - 1; row >= 0; row--) {
                        for (int col = mapWidth - 1; col >= 0; col--) {
                            if (!makeCell(col, row)) {
                                col += 2;
                            }
                        }
                    }
                    break;
                }
                case 4: {
                    for (int col = 0; col < mapWidth; col++) {
                        for (int row = 0; row < mapHeight; row++) {
                            if (!makeCell(col, row)) {
                                row -= 2;
                            }
                        }
                    }
                    break;
                }
                case 5: {
                    for (int col = 0; col < mapWidth; col++) {
                        for (int row = mapHeight - 1; row >= 0; row--) {
                            if (!makeCell(col, row)) {
                                row += 2;
                            }
                        }
                    }
                    break;
                }
                case 6: {
                    for (int col = mapWidth - 1; col >= 0; col--) {
                        for (int row = 0; row < mapHeight; row++) {
                            if (!makeCell(col, row)) {
                                row -= 2;
                            }
                        }
                    }
                    break;
                }
                case 7: {
                    for (int col = mapWidth - 1; col >= 0; col--) {
                        for (int row = mapHeight - 1; row >= 0; row--) {
                            if (!makeCell(col, row)) {
                                row += 2;
                            }
                        }
                    }
                    break;
                }
                case 13:
                case 12:
                case 11:
                case 10:
                case 9:
                case 8: {
                    diagonalOrder11();
                    break;
                }
                case 19:
                case 18:
                case 17:
                case 16:
                case 15:
                case 14: {
                    diagonalOrder12();
                    break;
                }
                case 20: {
//                    diagonalUp0();
                    zigZag0();
                    break;
                }
                case 21: {
                    zigZag1();
                    break;
                }
                case 22: {
                    zigZag2();
                    break;
                }
                case 23: {
                    zigZag3();
                    break;
                }
                case 24: {
                    zigZag4();
                    break;
                }
                case 25: {
                    zigZag5();
                    break;
                }
                case 26: {
                    zigZag6();
                    break;
                }
                case 27: {
                    zigZag7();
                    break;
                }
                case 28: {
                    circular();
                    break;
                }
                default:
            }
            order++;
            if (order > 28) {
                order = 0;
            }
        } catch (Exception exception) {
            Logger.logError("exception:" + exception);
            exception.printStackTrace();
        }
        thread.interrupt();
        thread = null;
        Logger.logFuncEnd(Thread.currentThread().toString());
    }

    private void diagonalOrder11() throws Exception {
        int returned = 0;
        int x = 0, y = 0;
        int length = Math.max(map.width, map.height);
        while (x < length) {
            if (x < map.width && y < map.height) {
                switch (order) {
                    case 8: {
                        makeCell(x, y);
                        break;
                    }
                    case 9: {
                        if (!makeCell(x, y)) {
                            x -= 1;
                            continue;
                        }
                        break;
                    }
                    case 10: {
                        if (!makeCell(x, y)) {
                            y -= 1;
                            continue;
                        }
                        break;
                    }
                    case 11: {
                        if (!makeCell(x, y)) {
                            returned = 1;
                            x -= 1;
                            continue;
                        } else if (returned == 1) {
                            returned = 0;
                            x += 1;
                            continue;
                        }
                        break;
                    }
                    case 12: {
                        if (!makeCell(x, y)) {
                            returned = 1;
                            y -= 1;
                            continue;
                        } else if (returned == 1) {
                            returned = 0;
                            y += 1;
                            continue;
                        }
                        break;
                    }
                    case 13: {
                        if (returned == 0) {
                            if (!makeCell(x, y)) {
                                returned = 1;
                                x -= 1;
                                continue;
                            }
                        } else if (returned == 1) {
                            if (!makeCell(x, y)) {
                                Logger.logError("x:" + x, "y:" + y);
                                returned = 0;
                                continue;
                            }
                            returned = 2;
                            continue;
                        } else if (returned == 2) {
                            y += 1;
                            returned = 0;
                            continue;
                        }
                        break;
                    }
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

    private void diagonalOrder12() throws Exception {
        int returned = 0;
        int x = 0, y = 0;
        int length = Math.max(map.width, map.height);
        while (y < length) {
            if (x < map.width && y < map.height) {
                switch (order) {
                    case 14: {
                        makeCell(x, y);
                        break;
                    }
                    case 15: {
                        if (!makeCell(x, y)) {
                            x -= 1;
                            continue;
                        }
                        break;
                    }
                    case 16: {
                        if (!makeCell(x, y)) {
                            y -= 1;
                            continue;
                        }
                        break;
                    }
                    case 17: {
                        if (!makeCell(x, y)) {
                            returned = 1;
                            x -= 1;
                            continue;
                        } else if (returned == 1) {
                            returned = 0;
                            x += 1;
                            continue;
                        }
                        break;
                    }
                    case 18: {
                        if (!makeCell(x, y)) {
                            returned = 1;
                            y -= 1;
                            continue;
                        } else if (returned == 1) {
                            returned = 0;
                            y += 1;
                            continue;
                        }
                        break;
                    }
                    case 19: {
                        if (returned == 0) {
                            if (!makeCell(x, y)) {
                                returned = 1;
                                x -= 1;
                                continue;
                            }
                        } else if (returned == 1) {
                            if (!makeCell(x, y)) {
                                Logger.logError("x:" + x, "y:" + y);
                                returned = 0;
                                continue;
                            }
                            returned = 2;
                            continue;
                        } else if (returned == 2) {
                            y += 1;
                            returned = 0;
                            continue;
                        }
                        break;
                    }
                }
            }
            if (y == length - 1) {
                y = x + 1;
                x = length - 1;
            } else if (x == 0) {
                x = y + 1;
                y = 0;
            } else {
                y++;
                x--;
            }
        }
    }

    private void diagonalUp0() throws Exception {
        for (int x1 = 0; x1 < map.width; x1++) {
            for (int y3 = 0, x2 = x1; y3 < map.height && x2 >= 0; y3++, x2--) {
//                Logger.logDebug("x2:" + x2, "y3:" + y3);
                if (!makeCell(x2, y3)) {
//                    Logger.logError("x2:" + x2, "y3:" + y3);
                    x1 -= 1;
                    x2 += 1;
                    y3 -= 2;
                }
            }
        }
        for (int y1 = 1; y1 < map.height; y1++) {
            for (int x3 = map.width - 1, y2 = y1; x3 >= 0 && y2 < map.height; x3--, y2++) {
                if (!makeCell(x3, y2)) {
//                    Logger.logError("x3:" + x3, "y2:" + y2);
                    y1 -= 1;
                    y2 -= 2;
                    x3 += 1;
                }
            }
        }
    }

    private void zigZag0() throws Exception {
        int width = map.width;
        int height = map.height;
        int x = 0;
        int y = 0;
        makeCell(x, y);
        while (x <= width && y <= height) {
            if (y < height - 1) {
                y++;
            } else {
                x++;
            }
            while (x < width && y >= 0) {
                if (makeCell(x, y)) {
                    x++;
                    y--;
                } else {
                    y--;
                }
            }
            x--;
            y++;
            if (x < width - 1) {
                x++;
            } else {
                y++;
            }
            while (x >= 0 && y < height) {
                if (makeCell(x, y)) {
                    x--;
                    y++;
                } else {
                    y--;
                }
            }
            x++;
            y--;
        }
    }

    private void zigZag1() throws Exception {
        int width = map.width;
        int height = map.height;
        int x = 0;
        int y = 0;
        makeCell(x, y);
        while (x <= width && y <= height) {
            if (x < width - 1) {
                x++;
            } else {
                y++;
            }
            while (x >= 0 && y < height) {
                if (makeCell(x, y)) {
                    x--;
                    y++;
                } else {
                    y--;
                }
            }
            x++;
            y--;
            if (y < height - 1) {
                y++;
            } else {
                x++;
            }
            while (x < width && y >= 0) {
                if (makeCell(x, y)) {
                    x++;
                    y--;
                } else {
                    y--;
                }
            }
            x--;
            y++;
        }
    }

    private void zigZag2() throws Exception {
        int width = map.width;
        int height = map.height;
        int x = map.width-1;
        int y = map.height-1;
        makeCell(x, y);
        while (x >= 0 && y >= 0) {
            if (x > 0) {
                x--;
            } else {
                y--;
            }
            while (x < width && y >= 0) {
                if (makeCell(x, y)) {
                    x++;
                    y--;
                } else {
                    y++;
                }
            }
            x--;
            y++;
            if (y > 0) {
                y--;
            } else {
                x--;
            }
            while (x >= 0 && y < height) {
                if (makeCell(x, y)) {
                    x--;
                    y++;
                } else {
                    y++;
                }
            }
            x++;
            y--;
        }
    }

    private void zigZag3() throws Exception {
        int width = map.width;
        int height = map.height;
        int x = map.width-1;
        int y = map.height-1;
        makeCell(x, y);
        while (x >= 0 && y >= 0) {
            if (y > 0) {
                y--;
            } else {
                x--;
            }
            while (x >= 0 && y < height) {
                if (makeCell(x, y)) {
                    x--;
                    y++;
                } else {
                    y++;
                }
            }
            x++;
            y--;
            if (x > 0) {
                x--;
            } else {
                y--;
            }
            while (x < width && y >= 0) {
                if (makeCell(x, y)) {
                    x++;
                    y--;
                } else {
                    y++;
                }
            }
            x--;
            y++;
        }
    }

    private void zigZag4() throws Exception {
        int width = map.width;
        int height = map.height;
        int x = map.width-1;
        int y = 0;
        makeCell(x, y);
        while (x >= 0 && y <= height) {
            if (y < height - 1) {
                y++;
            } else {
                x--;
            }
            while (x >= 0 && y >= 0) {
                if (makeCell(x, y)) {
                    x--;
                    y--;
                } else {
                    y--;
                }
            }
            x++;
            y++;
            if (x > 0) {
                x--;
            } else {
                y++;
            }
            while (x < width && y < height) {
                if (makeCell(x, y)) {
                    x++;
                    y++;
                } else {
                    y--;
                }
            }
            x--;
            y--;
        }
    }

    private void zigZag5() throws Exception {
        int width = map.width;
        int height = map.height;
        int x = map.width-1;
        int y = 0;
        makeCell(x, y);
        while (x >= 0 && y <= height) {
            if (x > 0) {
                x--;
            } else {
                y++;
            }
            while (x < width && y < height) {
                if (makeCell(x, y)) {
                    x++;
                    y++;
                } else {
                    y--;
                }
            }
            x--;
            y--;
            if (y < height - 1) {
                y++;
            } else {
                x--;
            }
            while (x >= 0 && y >= 0) {
                if (makeCell(x, y)) {
                    x--;
                    y--;
                } else {
                    y--;
                }
            }
            x++;
            y++;
        }
    }

    private void zigZag6() throws Exception {
        int width = map.width;
        int height = map.height;
        int x = 0;
        int y = map.height-1;
        makeCell(x, y);
        while (y >= 0 && x <= width) {
            if (y > 0) {
                y--;
            } else {
                x++;
            }
            while (y < height && x < width) {
                if (makeCell(x, y)) {
                    y++;
                    x++;
                } else {
                    y++;
                }
            }
            x--;
            y--;
            if (x < width - 1) {
                x++;
            } else {
                y--;
            }
            while (x >= 0 && y >= 0) {
                if (makeCell(x, y)) {
                    x--;
                    y--;
                } else {
                    y++;
                }
            }
            x++;
            y++;
        }
    }

    private void zigZag7() throws Exception {
        int width = map.width;
        int height = map.height;
        int x = 0;
        int y = map.height-1;
        makeCell(x, y);
        while (y >= 0 && x <= width) {
            if (x < width - 1) {
                x++;
            } else {
                y--;
            }
            while (x >= 0 && y >= 0) {
                if (makeCell(x, y)) {
                    x--;
                    y--;
                } else {
                    y++;
                }
            }
            x++;
            y++;
            if (y > 0) {
                y--;
            } else {
                x++;
            }
            while (y < height && x < width) {
                if (makeCell(x, y)) {
                    y++;
                    x++;
                } else {
                    y++;
                }
            }
            x--;
            y--;
        }
    }

    private void circular() throws Exception {
        int rows = map.width;
        int cols = map.height;
        Dot dot = new Dot(0, 0, rows - 1, cols - 1, 0, 0);
        Direction directionState = Direction.RIGHT; // initial direction
        int p = 1;
        makeCell(dot.row, dot.col);
        while (p < rows * cols) {
            switch (directionState) {
                case RIGHT:
                    if (dot.TryMoveRight()) {
                        if (!makeCell(dot.row, dot.col)) {
//                            Logger.logError("dot:" + dot);
//                            dot.row--;
//                        } else {
//                            p++;
                        }
                        p++;
                    } else {
                        directionState = Direction.DOWN;
                    }
                    break;
                case DOWN:
                    if (dot.TryMoveDown()) {
                        if (!makeCell(dot.row, dot.col)) {
//                            Logger.logError("dot:" + dot);
//                            dot.col++;
//                        } else {
//                            p++;
                        }
                        p++;
                    } else {
                        directionState = Direction.LEFT;
                    }
                    break;
                case LEFT:
                    if (dot.TryMoveLeft()) {
                        if (!makeCell(dot.row, dot.col)) {
//                            Logger.logError("dot:" + dot);
//                            dot.row++;
//                        } else {
//                            p++;
                        }
                        p++;
                    } else {
                        directionState = Direction.UP;
                    }
                    break;
                case UP:
                    if (dot.TryMoveUp()) {
                        if (!makeCell(dot.row, dot.col)) {
//                            Logger.logError("dot:" + dot);
//                            dot.col--;
//                        } else {
//                            p++;
                        }
                        p++;
                    } else {
                        directionState = Direction.RIGHT;
                    }
                    break;
            }
        }
    }

    private boolean makeCell(int col, int row) throws Exception {
        Thread.sleep(timeSleep);
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        int tileId = pickTile(col, row);
        if (tileId >= 0) {
            cell.setTile(tiledMapTiles.getTile(tileId));
            mapLayer.setCell(col, row, cell);
            return true;
        } else {
            cell.setTile(nullTile);
            mapLayer.setCell(col, row, cell);
            return false;
        }
    }

    private int pickTile(int col, int row) {
        byte[] matchMask = new byte[]{MATCH_ANY, MATCH_ANY, MATCH_ANY, MATCH_ANY};
        switch (order) {
            default: {
                updateMatchMaskForTile(matchMask, col - 1, row, TOP_LEFT, BOTTOM_LEFT, TOP_RIGHT, BOTTOM_RIGHT);
                updateMatchMaskForTile(matchMask, col, row - 1, BOTTOM_LEFT, BOTTOM_RIGHT, TOP_LEFT, TOP_RIGHT);
                updateMatchMaskForTile(matchMask, col + 1, row, BOTTOM_RIGHT, TOP_RIGHT, BOTTOM_LEFT, TOP_LEFT);
                updateMatchMaskForTile(matchMask, col, row + 1, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT);
                break;
            }
//            default:
            case 21:
            case 20:
            case 19:
            case 18:
            case 17:
            case 16:
            case 15:
//            case 14:
            case 13:
            case 12:
            case 11:
            case 10:
            case 9:
//            case 8:
            case 4:
            case 0: {
                updateMatchMaskForTile(matchMask, col-1, row, TOP_LEFT, BOTTOM_LEFT, TOP_RIGHT, BOTTOM_RIGHT);
                updateMatchMaskForTile(matchMask, col, row-1, BOTTOM_LEFT, BOTTOM_RIGHT, TOP_LEFT, TOP_RIGHT);
                break;
            }
            case 27:
            case 26:
            case 5:
            case 1: {
                updateMatchMaskForTile(matchMask, col-1, row, BOTTOM_LEFT, TOP_LEFT, BOTTOM_RIGHT, TOP_RIGHT);
                updateMatchMaskForTile(matchMask, col, row+1, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT);
                break;
            }
            case 25:
            case 24:
            case 6:
            case 2: {
                updateMatchMaskForTile(matchMask, col+1, row, BOTTOM_RIGHT, TOP_RIGHT, BOTTOM_LEFT, TOP_LEFT);
                updateMatchMaskForTile(matchMask, col, row-1, BOTTOM_LEFT, BOTTOM_RIGHT, TOP_LEFT, TOP_RIGHT);
                break;
            }
            case 23:
            case 22:
            case 7:
            case 3: {
                updateMatchMaskForTile(matchMask, col+1, row, BOTTOM_RIGHT, TOP_RIGHT, BOTTOM_LEFT, TOP_LEFT);
                updateMatchMaskForTile(matchMask, col, row+1, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT);
                break;
            }
        }
//        int tileId = getTileId(col+1, row-1);
//        if (tileId >= 0) {
//            byte tileCorner = getTerrainCodes(tileId)[TOP_RIGHT.ordinal()];
//            byte maskCorner = matchMask[TOP_LEFT.ordinal()];
//            if (maskCorner != tileCorner) {
//                TreeSet<Byte> validTransitions = terrainTypes.get(tileCorner).getTransitions();
//                if (validTransitions.size() < maxTransitions) {
//                    matchMask[TOP_RIGHT.ordinal()] = validTransitions.first();
//                }
//            }
//        }
        List<Integer> matchingTiles = findMatchingTiles(matchMask);
        if (matchingTiles.isEmpty()) {
            return -1;
        } else {
            int selectedTile = random.nextInt(matchingTiles.size());
            return matchingTiles.get(selectedTile);
        }
    }

    private void updateMatchMaskForTile(byte[] mask, int col, int row, TILE_BITS mask_corner0, TILE_BITS mask_corner1, TILE_BITS tile_corner0, TILE_BITS tile_corner1) {
        int tileId = getTileId(col, row);
        if (tileId >= 0) {
            byte[] tileCodes = getTerrainCodes(tileId);
            mask[mask_corner0.ordinal()] = tileCodes[tile_corner0.ordinal()];
            mask[mask_corner1.ordinal()] = tileCodes[tile_corner1.ordinal()];
        }
    }

    private List<Integer> findMatchingTiles(byte[] mask) {
        List<Integer> matchingTiles = new ArrayList<>();
        int maskLength = mask.length;
//        int numTiles = tiledMapTiles.size();
        for (TiledMapTile tiledMapTile : tiledMapTiles) {
//        for (int i = 0; i < numTiles; i++) {
            int id = tiledMapTile.getId();
            byte[] bits = getTerrainCodes(id);
            int j = 0;
            for (; j < maskLength; j++) {
                if (mask[j] != MATCH_ANY && mask[j] != bits[j]) {
                    break;
                }
            }
            if (j == maskLength) {
                matchingTiles.add(id);
            }
        }
        return matchingTiles;
    }

    private int getTileId(int col, int row) {
        if (col < 0 || row < 0 || col >= mapWidth || row >= mapHeight) {
            return -1;
        }
        TiledMapTileLayer.Cell cell = mapLayer.getCell(col, row);
        if (cell != null) {
            TiledMapTile tiledMapTile = cell.getTile();
            if (tiledMapTile != null) {
                return tiledMapTile.getId();
            }
        }
        return -1;
//        return mapLayer.getCell(col, row).getTile().getId();
    }

    private byte[] getTerrainCodes(int tileId) {
        byte[] values = new byte[4];
        Tile tile = tileSet.tileHashMap.get(tileId);
        if (tile != null) {
            String[] strings = tile.terrain.split(",");
            for (int k = 0; k < values.length; k++) {
                values[k] = Byte.parseByte(strings[k]);
            }
        }
        return values;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("mapWidth", mapWidth)
                .add("mapHeight", mapHeight)
                .add("random", random)
//                .add("tileSet", tileSet)
//                .add("tiledMapTiles", tiledMapTiles)
//                .add("map", map)
//                .add("mapLayer", mapLayer)
//                .add("nullTile", nullTile)
//                .add("thread", thread)
//                .add("timeSleep", timeSleep)
//                .add("order", order)
                .toString();
    }
}
