package com.betmansmall.maps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
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
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

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
    private int timeSleep = 2;
    private int order = 4;

    public AutoTiler(int mapWidth, int mapHeight, FileHandle tilesetConfigFile) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.random = new Random();
        init(tilesetConfigFile);
        nullTile = new StaticTiledMapTile(new TextureRegion(new Texture(Gdx.files.internal("maps/textures/redTexture.png"))));
        nullTile.getTextureRegion().setRegionWidth(tileWidth);
        nullTile.getTextureRegion().setRegionHeight(tileHeight);
//        nullTile.getTextureRegion().getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
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
            }
            order++;
            if (order > 7) {
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
        return mapLayer.getCell(col, row).getTile().getId();
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
