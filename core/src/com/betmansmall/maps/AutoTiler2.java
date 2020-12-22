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
import com.betmansmall.maps.jsons.TileSet;
import com.betmansmall.utils.logging.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

import static com.betmansmall.maps.AutoTiler.TILE_BITS.BOTTOM_LEFT;
import static com.betmansmall.maps.AutoTiler.TILE_BITS.BOTTOM_RIGHT;
import static com.betmansmall.maps.AutoTiler.TILE_BITS.TOP_LEFT;
import static com.betmansmall.maps.AutoTiler.TILE_BITS.TOP_RIGHT;

public class AutoTiler2 {
    private static final byte MATCH_ANY = 127;
    private static final int TERRAINS_PER_ROW = 2;
    private static final int TILES_PER_TERRAIN = 16;

    public enum TILE_BITS {
        TOP_LEFT(0), TOP_RIGHT(1), BOTTOM_LEFT(2), BOTTOM_RIGHT(3);

        private final int value;

        TILE_BITS(int value) {
            this.value = value;
        }

        public int id() {
            return value;
        }
    }

    public static class TerrainType {
        private final byte id;
        private final TreeSet<Byte> transitions;

        public TerrainType(byte id) {
            this.id = id;
            this.transitions = new TreeSet<>();
        }

        public byte getId() {
            return id;
        }

        public TreeSet<Byte> getTransitions() {
            return transitions;
        }
    }

    private final int mapWidth;
    private final int mapHeight;
    private final Random random;

    private int tileWidth;
    private int tileHeight;
    private List<List<Byte>> tileRowTerrains;
    private Map<Byte, TerrainType> terrainTypes;
    private int maxTransitions;
    private Texture tilesTexture;
    private TiledMapTileSet tileSet;
    private TiledMap map;
    private TiledMapTileLayer mapLayer;

    public TileSet tileSet2;

    public AutoTiler2(final int mapWidth,
                      final int mapHeight,
                      FileHandle tileSetFileHandle) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.random = new Random();
        tileSet2 = TsxLoader.loadTileSet(tileSetFileHandle);
        tileSet = TsxLoader.loadTiledMapTiles(tileSetFileHandle, tileSet2);
//        init(tilesetConfigFile);
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public TiledMap generateMap() {
        for (int row = 0; row < mapHeight; row++) {
            for (int col = 0; col < mapWidth; col++) {
                // Pick next tile
                final int tileId = pickTile(col, row);
                final TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(tileSet.getTile(tileId));
                mapLayer.setCell(col, row, cell);
            }
        }
        return map;
    }

    private int pickTile(final int col, final int row) {
        final byte[] matchMask = new byte[]{MATCH_ANY, MATCH_ANY, MATCH_ANY, MATCH_ANY};
        updateMatchMaskForTile(matchMask,
                col - 1, row,
                TOP_LEFT.id(), TOP_RIGHT.id(),
                BOTTOM_LEFT.id(), BOTTOM_RIGHT.id());
        updateMatchMaskForTile(matchMask,
                col, row - 1,
                BOTTOM_LEFT.id(), TOP_LEFT.id(),
                BOTTOM_RIGHT.id(), TOP_RIGHT.id());
        final int tileId = getTileId(col + 1, row - 1);
        if (tileId >= 0) {
            final byte tileCorner = getTerrainCodes(tileId)[TOP_RIGHT.id()];
            final byte maskCorner = matchMask[TOP_LEFT.id()];
            if (maskCorner != tileCorner) {
                final TreeSet<Byte> validTransitions = terrainTypes.get(tileCorner).getTransitions();
                if (validTransitions.size() < maxTransitions) {
                    matchMask[TOP_RIGHT.id()] = validTransitions.first();
                }
            }
        }
        final List<Integer> matchingTiles = findMatchingTiles(matchMask);
        final int selectedTile = random.nextInt(matchingTiles.size());
        return matchingTiles.get(selectedTile);
    }

    private void updateMatchMaskForTile(final byte[] mask,
                                        final int col, final int row,
                                        final int mask_corner0, final int tile_corner0,
                                        final int mask_corner1, final int tile_corner1) {
        final int tileId = getTileId(col, row);
        if (tileId >= 0) {
            final byte[] tileCodes = getTerrainCodes(tileId);
            mask[mask_corner0] = tileCodes[tile_corner0];
            mask[mask_corner1] = tileCodes[tile_corner1];
        }
    }

    private List<Integer> findMatchingTiles(final byte[] mask) {
        final List<Integer> matchingTiles = new ArrayList<>();
        final int maskLength = mask.length;
        final int numTiles = tileSet.size();
        for (int i = 0; i < numTiles; i++) {
            final byte[] bits = getTerrainCodes(i);
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

    private int getTileId(final int col, final int row) {
        if (col < 0 || row < 0 || col >= mapWidth || row >= mapHeight) {
            return -1;
        }
        return mapLayer.getCell(col, row).getTile().getId();
    }

    private byte[] getTerrainCodes(final int tileId) {
        byte[] values = new byte[]{
                (byte) (tileId & 0x1),
                (byte) ((tileId & 0x2) >> 1),
                (byte) ((tileId & 0x4) >> 2),
                (byte) ((tileId & 0x8) >> 3)
        };
        final int tilesRowIndex = tileId / TILES_PER_TERRAIN;
        final List<Byte> terrainRow = tileRowTerrains.get(tilesRowIndex);
        for (int i = 0; i < values.length; i++) {
            values[i] = terrainRow.get(values[i]);
        }
        return values;
    }

    private void init(FileHandle tilesetConfigFile) {
        final Json json = new Json();
        final TilesetConfig conf = json.fromJson(TilesetConfig.class, tilesetConfigFile);
        Logger.logDebug("conf:" + conf);
        final FileHandle tilesTextureHandle = Gdx.files.internal(conf.getTexturePath());
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
        final Array<Array<String>> terrainDefs = config.getTerrainDefs();
        final HashMap<String, Byte> nameToIdMap = new HashMap<>();
        terrainTypes = new HashMap<>();
        tileRowTerrains = new ArrayList<>();
        byte currentTerrainId = 0;
        for (final Array<String> terrainDefsRow : terrainDefs) {
            if (terrainDefsRow.size != TERRAINS_PER_ROW) {
                throw new IllegalArgumentException(
                        "Each terrain_defs row must contain exactly " + TERRAINS_PER_ROW + " terrain types");
            }
            final List<Byte> terrainRow = new ArrayList<>(TERRAINS_PER_ROW);
            for (final String terrainName : terrainDefsRow) {
                TerrainType terrainType;
                Byte id = nameToIdMap.get(terrainName);
                if (id == null) {
                    id = currentTerrainId++;
                    nameToIdMap.put(terrainName, id);
                    terrainType = new TerrainType(id);
                    terrainTypes.put(id, terrainType);
                }
                terrainRow.add(id);
            }
            this.tileRowTerrains.add(terrainRow);
            final byte firstTerrainId = terrainRow.get(0);
            final byte secondTerrainId = terrainRow.get(1);
            terrainTypes.get(firstTerrainId).getTransitions().add(secondTerrainId);
            terrainTypes.get(secondTerrainId).getTransitions().add(firstTerrainId);
        }
        maxTransitions = terrainTypes.size() - 1;
    }

    private void initMap() {
        final TextureRegion[][] splitTiles = TextureRegion.split(tilesTexture, tileWidth, tileHeight);
        final int numRows = splitTiles.length;
        if (numRows != tileRowTerrains.size()) {
            throw new IllegalArgumentException("Tileset rows do not match terrain definitions");
        }
        for (final TextureRegion[] splitTile : splitTiles) {
            if (splitTile.length != TILES_PER_TERRAIN) {
                throw new IllegalArgumentException("Each tileset row must have exactly " + TILES_PER_TERRAIN + " tiles");
            }
        }
        tileSet = new TiledMapTileSet();
        int tid = 0;
        for (int i = 0; i < splitTiles.length; i++) {
            for (int j = 0; j < splitTiles[i].length; j++) {
                final StaticTiledMapTile tile = new StaticTiledMapTile(splitTiles[i][j]);
                tile.setId(tid++);
                tileSet.putTile(tile.getId(), tile);
            }
        }
        map = new TiledMap();
        mapLayer = new TiledMapTileLayer(mapWidth, mapHeight, tileWidth, tileHeight);
        map.getLayers().add(mapLayer);
        final Array<Texture> textures = Array.with(tilesTexture);
        map.setOwnedResources(textures);
    }
}
