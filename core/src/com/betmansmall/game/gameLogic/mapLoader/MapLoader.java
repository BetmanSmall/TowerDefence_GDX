package com.betmansmall.game.gameLogic.mapLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.ImageResolver;
import com.badlogic.gdx.maps.ImageResolver.DirectImageResolver;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.betmansmall.game.gameLogic.Wave;
import com.betmansmall.game.gameLogic.WaveManager;

import java.io.IOException;
import java.util.StringTokenizer;

public class MapLoader extends BaseTmxMapLoader<MapLoader.Parameters> {

    public static class Parameters extends BaseTmxMapLoader.Parameters {

    }

    WaveManager waveManager;

    public MapLoader(WaveManager waveManager) {
        super(new InternalFileHandleResolver());
        this.waveManager = waveManager;
    }

    public MapLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    public TiledMap load(String fileName) {
        return load(fileName, new MapLoader.Parameters());
    }

    public TiledMap load(String fileName, MapLoader.Parameters parameters) {
        try {
            this.convertObjectToTileSpace = parameters.convertObjectToTileSpace;
            this.flipY = parameters.flipY;
            FileHandle tmxFile = resolve(fileName);
            root = xml.parse(tmxFile);
            ObjectMap<String, Texture> textures = new ObjectMap<String, Texture>();
            Array<FileHandle> textureFiles = loadTilesets(root, tmxFile);
            textureFiles.addAll(loadImages(root, tmxFile));

            for (FileHandle textureFile : textureFiles) {
                Texture texture = new Texture(textureFile, parameters.generateMipMaps);
                texture.setFilter(parameters.textureMinFilter, parameters.textureMagFilter);
                textures.put(textureFile.path(), texture);
            }

            DirectImageResolver imageResolver = new DirectImageResolver(textures);
            TiledMap map = loadTilemap(root, tmxFile, imageResolver);
            map.setOwnedResources(textures.values().toArray());
            return map;
        } catch (IOException e) {
            throw new GdxRuntimeException("Couldn't load tilemap '" + fileName + "'", e);
        }
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle tmxFile, MapLoader.Parameters parameter) {
        map = null;

        if (parameter != null) {
            convertObjectToTileSpace = parameter.convertObjectToTileSpace;
            flipY = parameter.flipY;
        } else {
            convertObjectToTileSpace = false;
            flipY = true;
        }
        try {
            map = loadTilemap(root, tmxFile, new ImageResolver.AssetManagerImageResolver(manager));
        } catch (Exception e) {
            throw new GdxRuntimeException("Couldn't load tilemap '" + fileName + "'", e);
        }
    }

    @Override
    public TiledMap loadSync(AssetManager manager, String fileName, FileHandle file, MapLoader.Parameters parameter) {
        return map;
    }

    /**
     * Retrieves TiledMap resource dependencies
     *
     * @param fileName
     * @param parameter not used for now
     * @return dependencies for the given .tmx file
     */
    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle tmxFile, Parameters parameter) {
        Array<AssetDescriptor> dependencies = new Array<AssetDescriptor>();
        try {
            root = xml.parse(tmxFile);
            boolean generateMipMaps = (parameter != null ? parameter.generateMipMaps : false);
            TextureLoader.TextureParameter texParams = new TextureParameter();
            texParams.genMipMaps = generateMipMaps;
            if (parameter != null) {
                texParams.minFilter = parameter.textureMinFilter;
                texParams.magFilter = parameter.textureMagFilter;
            }
            for (FileHandle image : loadTilesets(root, tmxFile)) {
                dependencies.add(new AssetDescriptor(image, Texture.class, texParams));
            }
            for (FileHandle image : loadImages(root, tmxFile)) {
                dependencies.add(new AssetDescriptor(image, Texture.class, texParams));
            }
            return dependencies;
        } catch (IOException e) {
            throw new GdxRuntimeException("Couldn't load tilemap '" + fileName + "'", e);
        }
    }

    /**
     * Loads the map data, given the XML root element and an {@link ImageResolver} used to return the tileset Textures
     *
     * @param root          the XML root element
     * @param tmxFile       the Filehandle of the tmx file
     * @param imageResolver the {@link ImageResolver}
     * @return the {@link TiledMap}
     */
    protected TiledMap loadTilemap(Element root, FileHandle tmxFile, ImageResolver imageResolver) {
        TiledMap map = new TiledMap(tmxFile.path());

        String mapOrientation = root.getAttribute("orientation", null);
        int mapWidth = root.getIntAttribute("width", 0);
        int mapHeight = root.getIntAttribute("height", 0);
        int tileWidth = root.getIntAttribute("tilewidth", 0);
        int tileHeight = root.getIntAttribute("tileheight", 0);
        int hexSideLength = root.getIntAttribute("hexsidelength", 0);
        String staggerAxis = root.getAttribute("staggeraxis", null);
        String staggerIndex = root.getAttribute("staggerindex", null);
        String mapBackgroundColor = root.getAttribute("backgroundcolor", null);

        MapProperties mapProperties = map.getProperties();
        if (mapOrientation != null) {
            mapProperties.put("orientation", mapOrientation);
        }
        mapProperties.put("width", mapWidth);
        mapProperties.put("height", mapHeight);
        mapProperties.put("tilewidth", tileWidth);
        mapProperties.put("tileheight", tileHeight);
        mapProperties.put("hexsidelength", hexSideLength);
        if (staggerAxis != null) {
            mapProperties.put("staggeraxis", staggerAxis);
        }
        if (staggerIndex != null) {
            mapProperties.put("staggerindex", staggerIndex);
        }
        if (mapBackgroundColor != null) {
            mapProperties.put("backgroundcolor", mapBackgroundColor);
        }
        mapTileWidth = tileWidth;
        mapTileHeight = tileHeight;
        mapWidthInPixels = mapWidth * tileWidth;
        mapHeightInPixels = mapHeight * tileHeight;

        if (mapOrientation != null) {
            if ("staggered".equals(mapOrientation)) {
                if (mapHeight > 1) {
                    mapWidthInPixels += tileWidth / 2;
                    mapHeightInPixels = mapHeightInPixels / 2 + tileHeight / 2;
                }
            }
        }

        Element properties = root.getChildByName("properties");
        if (properties != null) {
            loadProperties(map.getProperties(), properties);
        }
        Array<Element> tilesets = root.getChildrenByName("tileset");
        for (Element element : tilesets) {
            loadTileSet(map, element, tmxFile, imageResolver);
            root.removeChild(element);
        }

        for (int i = 0, j = root.getChildCount(); i < j; i++) {
            Element element = root.getChild(i);
            String name = element.getName();
            if (name.equals("layer")) {
                loadTileLayer(map, element);
            } else if (name.equals("objectgroup")) {
                loadObjectGroup(map, element);
            } else if (name.equals("imagelayer")) {
                loadImageLayer(map, element, tmxFile, imageResolver);
            }
        }
        Element waves = root.getChildByName("waves");
        if(waves != null) {
//            String type = waves.getAttribute("type", null);
            String source = waves.getAttribute("source", null);
            if (source != null) {
                FileHandle tsx = getRelativeFileHandle(tmxFile, source);
//                try {
                    Element rootwaves = xml.parse(tsx);
                    wavesParser(rootwaves);
//                } catch (IOException e) { // todo need check how in MapLoader in gdx 1.9.9
//                    e.printStackTrace();
//                }
//            } else if(type != null/* && type == "empty"*/) { // LOL not WORK
//                System.out.println("type=" + type); // Хотел сделать пустую волну, не получилася=( мб как нить сделаем.
//                waveManager.addWave(new Wave(new GridPoint2(0, 0), new GridPoint2(0, 0), 10f));
            } else {
                wavesParser(waves);
            }
        } else {
            Gdx.app.log("MapLoader::loadTilemap()", "-- Not found waves block in map:" + tmxFile);
        }
        map.width = mapWidth;
        map.height = mapHeight;
        map.tileWidth = tileWidth;
        map.tileHeight = tileHeight;
        return map;
    }

    /**
     * Loads the tilesets
     *
     * @param root the root XML element
     * @return a list of filenames for helpImages containing tiles
     * @throws IOException
     */
    protected Array<FileHandle> loadTilesets(Element root, FileHandle tmxFile) throws IOException {
        Array<FileHandle> images = new Array<FileHandle>();
        for (Element tileset : root.getChildrenByName("tileset")) {
            String source = tileset.getAttribute("source", null);
            if (source != null) {
                FileHandle tsxFile = getRelativeFileHandle(tmxFile, source);
                tileset = xml.parse(tsxFile);
                Element imageElement = tileset.getChildByName("image");
                if (imageElement != null) {
                    String imageSource = tileset.getChildByName("image").getAttribute("source");
                    FileHandle image = getRelativeFileHandle(tsxFile, imageSource);
                    images.add(image);
                } else {
                    for (Element tile : tileset.getChildrenByName("tile")) {
                        String imageSource = tile.getChildByName("image").getAttribute("source");
                        FileHandle image = getRelativeFileHandle(tsxFile, imageSource);
                        images.add(image);
                    }
                }
            } else {
                Element imageElement = tileset.getChildByName("image");
                if (imageElement != null) {
                    String imageSource = tileset.getChildByName("image").getAttribute("source");
                    FileHandle image = getRelativeFileHandle(tmxFile, imageSource);
                    images.add(image);
                } else {
                    for (Element tile : tileset.getChildrenByName("tile")) {
                        String imageSource = tile.getChildByName("image").getAttribute("source");
                        FileHandle image = getRelativeFileHandle(tmxFile, imageSource);
                        images.add(image);
                    }
                }
            }
        }
        return images;
    }

    /**
     * Loads the helpImages in image layers
     *
     * @param root the root XML element
     * @return a list of filenames for helpImages inside image layers
     * @throws IOException
     */
    protected Array<FileHandle> loadImages(Element root, FileHandle tmxFile) throws IOException {
        Array<FileHandle> images = new Array<FileHandle>();

        for (Element imageLayer : root.getChildrenByName("imagelayer")) {
            Element image = imageLayer.getChildByName("image");
            String source = image.getAttribute("source", null);

            if (source != null) {
                FileHandle handle = getRelativeFileHandle(tmxFile, source);

                if (!images.contains(handle, false)) {
                    images.add(handle);
                }
            }
        }

        return images;
    }

    /**
     * Loads the specified tileset data, adding it to the collection of the specified map, given the XML element, the tmxFile and
     * an {@link ImageResolver} used to retrieve the tileset Textures.
     * <p/>
     * <p>
     * Default tileset's property keys that are loaded by default are:
     * </p>
     * <p/>
     * <ul>
     * <li><em>firstgid</em>, (int, defaults to 1) the first valid global id used for tile numbering</li>
     * <li><em>imagesource</em>, (String, defaults to empty string) the tileset source image filename</li>
     * <li><em>imagewidth</em>, (int, defaults to 0) the tileset source image width</li>
     * <li><em>imageheight</em>, (int, defaults to 0) the tileset source image height</li>
     * <li><em>tilewidth</em>, (int, defaults to 0) the tile width</li>
     * <li><em>tileheight</em>, (int, defaults to 0) the tile height</li>
     * <li><em>margin</em>, (int, defaults to 0) the tileset margin</li>
     * <li><em>spacing</em>, (int, defaults to 0) the tileset spacing</li>
     * </ul>
     * <p/>
     * <p>
     * The values are extracted from the specified Tmx file, if a value can't be found then the default is used.
     * </p>
     *
     * @param map           the Map whose tilesets collection will be populated
     * @param element       the XML element identifying the tileset to load
     * @param tmxFile       the Filehandle of the tmx file
     * @param imageResolver the {@link ImageResolver}
     */
    protected void loadTileSet(TiledMap map, Element element, FileHandle tmxFile, ImageResolver imageResolver) {
        if (element.getName().equals("tileset")) {
            String name = element.get("name", null);
            int firstgid = element.getIntAttribute("firstgid", 1);
            int tilewidth = element.getIntAttribute("tilewidth", 0);
            int tileheight = element.getIntAttribute("tileheight", 0);
            int spacing = element.getIntAttribute("spacing", 0);
            int margin = element.getIntAttribute("margin", 0);
            String source = element.getAttribute("source", null);

            int offsetX = 0;
            int offsetY = 0;

            String imageSource = "";
            int imageWidth = 0, imageHeight = 0;

            FileHandle image = null;
            if (source != null) {
                FileHandle tsx = getRelativeFileHandle(tmxFile, source);
//                try {
                    element = xml.parse(tsx);
                    name = element.get("name", null);
                    tilewidth = element.getIntAttribute("tilewidth", 0);
                    tileheight = element.getIntAttribute("tileheight", 0);
                    spacing = element.getIntAttribute("spacing", 0);
                    margin = element.getIntAttribute("margin", 0);
                    Element offset = element.getChildByName("tileoffset");
                    if (offset != null) {
                        offsetX = offset.getIntAttribute("x", 0);
                        offsetY = offset.getIntAttribute("y", 0);
                    }
                    Element imageElement = element.getChildByName("image");
                    if (imageElement != null) {
                        imageSource = imageElement.getAttribute("source");
                        imageWidth = imageElement.getIntAttribute("width", 0);
                        imageHeight = imageElement.getIntAttribute("height", 0);
                        image = getRelativeFileHandle(tsx, imageSource);
                    }
//                } catch (IOException e) {
//                    throw new GdxRuntimeException("Error parsing external tileset."); // todo need check how in MapLoader in gdx 1.9.9
//                }
            } else {
                Element offset = element.getChildByName("tileoffset");
                if (offset != null) {
                    offsetX = offset.getIntAttribute("x", 0);
                    offsetY = offset.getIntAttribute("y", 0);
                }
                Element imageElement = element.getChildByName("image");
                if (imageElement != null) {
                    imageSource = imageElement.getAttribute("source");
                    imageWidth = imageElement.getIntAttribute("width", 0);
                    imageHeight = imageElement.getIntAttribute("height", 0);
                    image = getRelativeFileHandle(tmxFile, imageSource);
                }
            }

            TiledMapTileSet tileset = new TiledMapTileSet();
            tileset.setName(name);
            tileset.getProperties().put("firstgid", firstgid);
            if (image != null) {
                TextureRegion texture = imageResolver.getImage(image.path());

                MapProperties props = tileset.getProperties();
                props.put("imagesource", imageSource);
                props.put("imagewidth", imageWidth);
                props.put("imageheight", imageHeight);
                props.put("tilewidth", tilewidth);
                props.put("tileheight", tileheight);
                props.put("margin", margin);
                props.put("spacing", spacing);

                int stopWidth = texture.getRegionWidth() - tilewidth;
                int stopHeight = texture.getRegionHeight() - tileheight;

                int id = firstgid;

                for (int y = margin; y <= stopHeight; y += tileheight + spacing) {
                    for (int x = margin; x <= stopWidth; x += tilewidth + spacing) {
                        TextureRegion tileRegion = new TextureRegion(texture, x, y, tilewidth, tileheight);
                        TiledMapTile tiledMapTile = new StaticTiledMapTile(tileRegion);
                        tiledMapTile.setId(id);
                        tiledMapTile.setOffsetX(offsetX);
                        tiledMapTile.setOffsetY(flipY ? -offsetY : offsetY);
                        tileset.putTile(id++, tiledMapTile);
                    }
                }
            } else {
                Array<Element> tileElements = element.getChildrenByName("tile");
                for (Element tileElement : tileElements) {
                    Element imageElement = tileElement.getChildByName("image");
                    if (imageElement != null) {
                        imageSource = imageElement.getAttribute("source");
                        imageWidth = imageElement.getIntAttribute("width", 0);
                        imageHeight = imageElement.getIntAttribute("height", 0);
                        image = getRelativeFileHandle(tmxFile, imageSource);
                    }
                    TextureRegion texture = imageResolver.getImage(image.path());
                    TiledMapTile tiledMapTile = new StaticTiledMapTile(texture);
                    tiledMapTile.setId(firstgid + tileElement.getIntAttribute("id"));
                    tiledMapTile.setOffsetX(offsetX);
                    tiledMapTile.setOffsetY(flipY ? -offsetY : offsetY);
                    tileset.putTile(tiledMapTile.getId(), tiledMapTile);
                }
            }

            Array<AnimatedTiledMapTile> animatedTiles = new Array<AnimatedTiledMapTile>();
            Array<Element> tileElements = element.getChildrenByName("tile");
            for (Element tileElement : tileElements) {
                int localtid = tileElement.getIntAttribute("id", 0);
                TiledMapTile tiledMapTile = tileset.getTile(firstgid + localtid);
                if (tiledMapTile != null) {
                    Element animationElement = tileElement.getChildByName("animation");
                    if (animationElement != null) {

                        Array<StaticTiledMapTile> staticTiles = new Array<StaticTiledMapTile>();
                        IntArray intervals = new IntArray();
                        for (Element frameElement : animationElement.getChildrenByName("frame")) {
                            staticTiles.add((StaticTiledMapTile) tileset.getTile(firstgid + frameElement.getIntAttribute("tileid")));
                            intervals.add(frameElement.getIntAttribute("duration"));
                        }

                        AnimatedTiledMapTile animatedTile = new AnimatedTiledMapTile(intervals, staticTiles);
                        animatedTile.setId(tiledMapTile.getId());
                        animatedTiles.add(animatedTile);
                        tiledMapTile = animatedTile;
                    }

                    String terrain = tileElement.getAttribute("terrain", null);
                    if (terrain != null) {
                        tiledMapTile.getProperties().put("terrain", terrain);
                    }
                    String probability = tileElement.getAttribute("probability", null);
                    if (probability != null) {
                        tiledMapTile.getProperties().put("probability", probability);
                    }
                    Element properties = tileElement.getChildByName("properties");
                    if (properties != null) {
                        loadProperties(tiledMapTile.getProperties(), properties);
                    }
                }
            }
//            Element terraintypes = element.getChildByName("terraintypes");
//            if (terraintypes != null) {
//                int terrainNumber = 0;
//                Array<Element> terrainElements = terraintypes.getChildrenByName("terrain");
//                for (Element terrainElement : terrainElements) {
//                    String terrainName = terrainElement.getAttribute("name");
//                    int tileId = terrainElement.getIntAttribute("tile");
//                    TiledMapTile tiledMapTile = tileset.getTile(tileId);
//                    tileset.putTile(tileId, tiledMapTile);
////                    tileset.add(tiledMapTile);
//                }
//            }

            for (AnimatedTiledMapTile tile : animatedTiles) {
                tileset.putTile(tile.getId(), tile);
            }

            Element properties = element.getChildByName("properties");
            if (properties != null) {
                loadProperties(tileset.getProperties(), properties);
            }
            map.getTileSets().addTileSet(tileset);
        }
    }

    public static void loadPropertiesStatic(MapProperties properties, Element element) {
        if (element == null) return;
        if (element.getName().equals("properties")) {
            for (Element property : element.getChildrenByName("property")) {
                String name = property.getAttribute("name", null);
                String value = property.getAttribute("value", null);
                if (value == null) {
                    value = property.getText();
                }
                properties.put(name, value);
            }
        }
    }

    public static void loadPropertiesStatic(ObjectMap<String, String> properties, Element element) {
        if (element == null) return;
        if (element.getName().equals("properties")) {
            for (Element property : element.getChildrenByName("property")) {
                String name = property.getAttribute("name", null);
                String value = property.getAttribute("value", null);
                if (value == null) {
                    value = property.getText();
                }
                properties.put(name, value);
            }
        }
    }

    public static FileHandle getRelativeFileHandle(FileHandle file, String path) {
        StringTokenizer tokenizer = new StringTokenizer(path, "\\/");
        FileHandle result = file.parent();
        while (tokenizer.hasMoreElements()) {
            String token = tokenizer.nextToken();
            if (token.equals(".."))
                result = result.parent();
            else {
                result = result.child(token);
            }
        }
        return result;
    }

//    public void loadTileLayer (Map map, Element element) {
//        if (element.getName().equals("layer")) {
//            int width = element.getIntAttribute("width", 0);
//            int height = element.getIntAttribute("height", 0);
//            int tileWidth = element.getParent().getIntAttribute("tilewidth", 0);
//            int tileHeight = element.getParent().getIntAttribute("tileheight", 0);
//            TiledMapTileLayer layer = new TiledMapTileLayer(width, height, tileWidth, tileHeight);
//
//            loadBasicLayerInfo(layer, element);
//
//            int[] ids = getTileIds(element, width, height);
//            TiledMapTileSets tilesets = map.getTileSets();
//            for (int y = 0; y < height; y++) {
//                for (int x = 0; x < width; x++) {
//                    int id = ids[y * width + x];
//                    boolean flipHorizontally = ((id & FLAG_FLIP_HORIZONTALLY) != 0);
//                    boolean flipVertically = ((id & FLAG_FLIP_VERTICALLY) != 0);
//                    boolean flipDiagonally = ((id & FLAG_FLIP_DIAGONALLY) != 0);
//
//                    TiledMapTile tiledMapTile = tilesets.getTile(id & ~MASK_CLEAR);
//                    if (tiledMapTile != null) {
//                        TiledMapTileLayer.Cell cell = createTileLayerCell(flipHorizontally, flipVertically, flipDiagonally);
//                        cell.setTile(tiledMapTile);
//                        layer.setCell(x, flipY ? height - 1 - y : y, cell);
//                    }
//                }
//            }
//
//            Element properties = element.getChildByName("properties");
//            if (properties != null) {
//                loadProperties(layer.getProperties(), properties);
//            }
//            map.getLayers().add(layer);
//        }
//    }

    public void wavesParser(Element waves) {
        waveManager.allTogether = waves.getBoolean("allTogether", false);
        Array<Element> waveElements = waves.getChildrenByName("wave");
        for (Element waveElement : waveElements) {
            int spawnPointX = waveElement.getIntAttribute("spawnPointX");
            int spawnPointY = waveElement.getIntAttribute("spawnPointY");
            int exitPointX = waveElement.getIntAttribute("exitPointX");
            int exitPointY = waveElement.getIntAttribute("exitPointY");
            float spawnInterval = waveElement.getFloat("spawnInterval", 0.0f);
            float startToMove = waveElement.getFloat("startToMove", 0.0f);
            Wave wave = new Wave(new GridPoint2(spawnPointX, spawnPointY), new GridPoint2(exitPointX, exitPointY), startToMove);
            int actionsCount = waveElement.getChildCount();
            for (int a = 0; a < actionsCount; a++) {
                Element action = waveElement.getChild(a);
                String sAction = action.getName();
                if (sAction.equals("unit")) { // mb bad?
                    float delay = action.getFloat("delay", 0.0f);
                    if (delay > 0f) {
                        wave.addAction("delay=" + delay);
                    }
                    String unitTemplateName = action.getAttribute("templateName");

                    float interval = action.getFloat("interval", 0.0f) + spawnInterval;
                    int amount = action.getInt("amount", 0);
                    for (int u = 0; u < amount; u++) {
                        if (interval > 0f) {
                            wave.addAction("interval=" + interval);
                        }
                        wave.addAction(unitTemplateName);
                    }
                }
            }
//            Array<Element> units = waveElement.getChildrenByName("unit");
//            for (Element unit : units) {
//                String unitTemplateName = unit.getAttribute("templateName");
//                int unitsAmount = unit.getIntAttribute("amount");
//                int delay = unit.getIntAttribute("delay", 0);
//                for (int k = 0; k < unitsAmount; k++) {
//                    wave.addTemplateForUnit(unitTemplateName);
//                    wave.addDelayForUnit(delay);
//                }
//            }
            waveManager.addWave(wave);
        }
        Array<Element> waveForUserElements = waves.getChildrenByName("waveForUser");
        for (Element waveElement : waveForUserElements) {
            int spawnPointX = waveElement.getIntAttribute("spawnPointX");
            int spawnPointY = waveElement.getIntAttribute("spawnPointY");
            int exitPointX = waveElement.getIntAttribute("exitPointX");
            int exitPointY = waveElement.getIntAttribute("exitPointY");
            Wave wave = new Wave(new GridPoint2(spawnPointX, spawnPointY), new GridPoint2(exitPointX, exitPointY));
            waveManager.wavesForUser.add(wave);
        }
    }
}
