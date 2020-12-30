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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.betmansmall.utils.logging.Logger;
import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static com.betmansmall.maps.AutoTiler.TILE_BITS.*;

public class AutoTiler implements Runnable {
    private static final byte MATCH_ANY = 127;
    private static final int TERRAINS_PER_ROW = 2;
    private static final int TILES_PER_TERRAIN = 16;

    public enum TILE_BITS {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }

//    public static class TerrainType {
//        private final byte id;
//        private final TreeSet<Byte> transitions;
//
//        public TerrainType(byte id) {
//            this.id = id;
//            this.transitions = new TreeSet<>();
//        }
//
//        public byte getId() {
//            return id;
//        }
//
//        public TreeSet<Byte> getTransitions() {
//            return transitions;
//        }
//
//        @Override
//        public String toString() {
//            return MoreObjects.toStringHelper(this)
//                    .add("id", id)
//                    .add("transitions", transitions)
//                    .toString();
//        }
//    }

    public int mapWidth;
    public int mapHeight;
    private final Random random;

    public int tileWidth;
    public int tileHeight;
    private List<List<Byte>> tileRowTerrains;
//    private Map<Byte, TerrainType> terrainTypes;
//    private int maxTransitions;
    private Texture tilesTexture;
    private TiledMapTileSet tileSet;
    private TmxMap map;
    private TiledMapTileLayer mapLayer;
    private final StaticTiledMapTile nullTile;

    private Thread thread = null;
    private int timeSleep = 0;
    private int order = 22;

    public AutoTiler(int mapWidth, int mapHeight, FileHandle tilesetConfigFile) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.random = new Random();
        init(tilesetConfigFile);
        nullTile = new StaticTiledMapTile(new TextureRegion(new Texture(Gdx.files.internal("maps/textures/redTexture.png"))));
        nullTile.getTextureRegion().setRegionWidth(tileWidth);
        nullTile.getTextureRegion().setRegionHeight(tileHeight);
//        nullTile.getTextureRegion().getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        map.width = mapWidth;
        map.height = mapHeight;
        map.tileWidth = tileWidth;
        map.tileHeight = tileHeight;
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
            map.getLayers().remove(0);
            mapLayer = new TiledMapTileLayer(mapWidth, mapHeight, tileWidth, tileHeight);
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
                default:
            }
            order++;
            if (order > 23) {
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
        int rows = map.width;
        int cols = map.height;
        int x = 0;
        int y = 0;
        makeCell(x, y);
        while (x <= rows && y <= cols) {
            if (y < cols - 1) {
                y++;
            } else {
                x++;
            }
            while (x < rows && y >= 0) {
                if (makeCell(x, y)) {
                    x++;
                    y--;
                } else {
                    y--;
                }
            }
            x--;
            y++;
            if (x < rows - 1) {
                x++;
            } else {
                y++;
            }
            while (x >= 0 && y < cols) {
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
        int rows = map.width;
        int cols = map.height;
        int x = 0;
        int y = 0;
        makeCell(x, y);
        while (x <= rows && y <= cols) {
            if (x < rows - 1) {
                x++;
            } else {
                y++;
            }
            while (x >= 0 && y < cols) {
                if (makeCell(x, y)) {
                    x--;
                    y++;
                } else {
                    y--;
                }
            }
            x++;
            y--;
            if (y < cols - 1) {
                y++;
            } else {
                x++;
            }
            while (x < rows && y >= 0) {
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
        int rows = map.width;
        int cols = map.height;
        int x = map.width-1;
        int y = map.height-1;
        makeCell(x, y);
        while (x >= 0 && y >= 0) {
            if (x > 0) {
                x--;
            } else {
                y--;
            }
            while (x < rows && y >= 0) {
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
            while (x >= 0 && y < cols) {
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
        int rows = map.width;
        int cols = map.height;
        int x = map.width-1;
        int y = map.height-1;
        makeCell(x, y);
        while (x >= 0 && y >= 0) {
            if (y > 0) {
                y--;
            } else {
                x--;
            }
            while (x >= 0 && y < cols) {
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
            while (x < rows && y >= 0) {
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

    private boolean makeCell(int col, int row) throws Exception {
        Thread.sleep(timeSleep);
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        int tileId = pickTile(col, row);
        if (tileId >= 0) {
            cell.setTile(tileSet.getTile(tileId));
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
//            default: {
//                updateMatchMaskForTile(matchMask, col - 1, row, TOP_LEFT, BOTTOM_LEFT, TOP_RIGHT, BOTTOM_RIGHT);
//                updateMatchMaskForTile(matchMask, col, row - 1, BOTTOM_LEFT, BOTTOM_RIGHT, TOP_LEFT, TOP_RIGHT);
//                updateMatchMaskForTile(matchMask, col - 1, row, BOTTOM_LEFT, TOP_LEFT, BOTTOM_RIGHT, TOP_RIGHT);
//                updateMatchMaskForTile(matchMask, col, row + 1, BOTTOM_LEFT, BOTTOM_RIGHT, TOP_LEFT, TOP_RIGHT);
//                updateMatchMaskForTile(matchMask, col + 1, row, BOTTOM_RIGHT, TOP_RIGHT, BOTTOM_LEFT, TOP_LEFT);
//                updateMatchMaskForTile(matchMask, col, row - 1, BOTTOM_LEFT, BOTTOM_RIGHT, TOP_LEFT, TOP_RIGHT);
//                updateMatchMaskForTile(matchMask, col + 1, row, BOTTOM_RIGHT, TOP_RIGHT, BOTTOM_LEFT, TOP_LEFT);
//                updateMatchMaskForTile(matchMask, col, row + 1, BOTTOM_LEFT, BOTTOM_RIGHT, TOP_LEFT, TOP_RIGHT);
//                break;
//            }
            default:
            case 4:
            case 0: {
                updateMatchMaskForTile(matchMask, col-1, row, TOP_LEFT, BOTTOM_LEFT, TOP_RIGHT, BOTTOM_RIGHT);
                updateMatchMaskForTile(matchMask, col, row-1, BOTTOM_LEFT, BOTTOM_RIGHT, TOP_LEFT, TOP_RIGHT);
                break;
            }
            case 5:
            case 1: {
                updateMatchMaskForTile(matchMask, col-1, row, BOTTOM_LEFT, TOP_LEFT, BOTTOM_RIGHT, TOP_RIGHT);
                updateMatchMaskForTile(matchMask, col, row+1, BOTTOM_LEFT, BOTTOM_RIGHT, TOP_LEFT, TOP_RIGHT);
                break;
            }
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
                updateMatchMaskForTile(matchMask, col, row+1, BOTTOM_LEFT, BOTTOM_RIGHT, TOP_LEFT, TOP_RIGHT);
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
        int numTiles = tileSet.size();
        for (int i = 0; i < numTiles; i++) {
            byte[] bits = getTerrainCodes(i);
            int j = 0;
            for (; j < maskLength; j++) {
                if (mask[j] != MATCH_ANY && mask[j] != bits[j]) {
                    break;
                }
            }
            if (j == maskLength) {
                matchingTiles.add(i);
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
//        byte[] values = new byte[] {
//                (byte) (tileId & 0x1),
//                (byte) ((tileId & 0x2) >> 1),
//                (byte) ((tileId & 0x4) >> 2),
//                (byte) ((tileId & 0x8) >> 3)
//        };
        switch (order) {
            default:
            case 6:
            case 4:
            case 2:
            case 0: {
                values[0] = (byte) (tileId & 0x1);
                values[1] = (byte) ((tileId & 0x2) >> 1);
                values[2] = (byte) ((tileId & 0x4) >> 2);
                values[3] = (byte) ((tileId & 0x8) >> 3);
                break;
            }
            case 23:
            case 22:
            case 7:
            case 5:
            case 3:
            case 1: {
                values[0] = (byte) ((tileId & 0x4) >> 2);
                values[1] = (byte) ((tileId & 0x8) >> 3);
                values[2] = (byte) (tileId & 0x1);
                values[3] = (byte) ((tileId & 0x2) >> 1);
                break;
            }
        }
        int tilesRowIndex = tileId / TILES_PER_TERRAIN;
        List<Byte> terrainRow = tileRowTerrains.get(tilesRowIndex);
        for (int i = 0; i < values.length; i++) {
            values[i] = terrainRow.get(values[i]);
        }
        return values;
    }

    private void init(FileHandle tilesetConfigFile) {
        Json json = new Json();
        TilesetConfig conf = json.fromJson(TilesetConfig.class, tilesetConfigFile);
        Logger.logDebug("conf:" + conf);
        FileHandle tilesTextureHandle = Gdx.files.internal(conf.getTexturePath());
        if (!tilesTextureHandle.exists() || tilesTextureHandle.isDirectory()) {
            throw new IllegalArgumentException("Invalid Tile-set texture path");
        }
        tileWidth = conf.getTileWidth();
        if (tileWidth <= 0 || tileWidth > 128) {
            throw new IllegalArgumentException("Invalid tile width");
        }
        tileHeight = conf.getTileHeight();
        if (tileHeight <= 0 || tileHeight > 128) {
            throw new IllegalArgumentException("Invalid tile height");
        }
        loadTerrainDefinitions(conf);
        tilesTexture = new Texture(conf.getTexturePath());
        try {
            initMap();
        } catch (Exception e) {
            tilesTexture.dispose();
            throw e;
        }
    }

    private void loadTerrainDefinitions(TilesetConfig config) {
        Array<Array<String>> terrainDefs = config.getTerrainDefs();
        HashMap<String, Byte> nameToIdMap = new HashMap<>();
//        terrainTypes = new HashMap<>();
        tileRowTerrains = new ArrayList<>();
        byte currentTerrainId = 0;
        for (Array<String> terrainDefsRow : terrainDefs) {
            if (terrainDefsRow.size != TERRAINS_PER_ROW) {
                throw new IllegalArgumentException(
                        "Each terrain_defs row must contain exactly " + TERRAINS_PER_ROW + " terrain types");
            }
            List<Byte> terrainRow = new ArrayList<>(TERRAINS_PER_ROW);
            for (String terrainName : terrainDefsRow) {
//                TerrainType terrainType;
                Byte id = nameToIdMap.get(terrainName);
                if (id == null) {
                    id = currentTerrainId++;
                    nameToIdMap.put(terrainName, id);
//                    terrainType = new TerrainType(id);
//                    terrainTypes.put(id, terrainType);
                }
                terrainRow.add(id);
            }
            this.tileRowTerrains.add(terrainRow);
//            byte firstTerrainId = terrainRow.get(0);
//            byte secondTerrainId = terrainRow.get(1);
//            terrainTypes.get(firstTerrainId).getTransitions().add(secondTerrainId);
//            terrainTypes.get(secondTerrainId).getTransitions().add(firstTerrainId);
        }
//        maxTransitions = terrainTypes.size() - 1;
    }

    private void initMap() {
        TextureRegion[][] splitTiles = TextureRegion.split(tilesTexture, tileWidth, tileHeight);
        int numRows = splitTiles.length;
        if (numRows != tileRowTerrains.size()) {
            throw new IllegalArgumentException("Tileset rows do not match terrain definitions");
        }
        for (TextureRegion[] splitTile : splitTiles) {
            if (splitTile.length != TILES_PER_TERRAIN) {
                throw new IllegalArgumentException("Each tileset row must have exactly " + TILES_PER_TERRAIN + " tiles");
            }
        }
        tileSet = new TiledMapTileSet();
        int tid = 0;
        for (int i = 0; i < splitTiles.length; i++) {
            for (int j = 0; j < splitTiles[i].length; j++) {
                StaticTiledMapTile tile = new StaticTiledMapTile(splitTiles[i][j]);
                tile.setId(tid++);
                tileSet.putTile(tile.getId(), tile);
            }
        }
        map = new TmxMap(new TiledMap(), "");
        mapLayer = new TiledMapTileLayer(mapWidth, mapHeight, tileWidth, tileHeight);
        map.getLayers().add(mapLayer);
        Array<Texture> textures = Array.with(tilesTexture);
        map.setOwnedResources(textures);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("mapWidth", mapWidth)
                .add("mapHeight", mapHeight)
                .add("random", random)
                .add("tileWidth", tileWidth)
                .add("tileHeight", tileHeight)
                .add("tileRowTerrains", tileRowTerrains)
//                .add("terrainTypes", terrainTypes)
//                .add("maxTransitions", maxTransitions)
                .add("tilesTexture", tilesTexture)
//                .add("tileSet", tileSet)
//                .add("map", map)
//                .add("mapLayer", mapLayer)
//                .add("nullTile", nullTile)
//                .add("thread", thread)
//                .add("timeSleep", timeSleep)
                .toString();
    }
}
