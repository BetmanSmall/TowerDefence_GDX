package com.betmansmall.maps;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.ImageResolver;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.betmansmall.util.logging.Logger;

import java.util.StringTokenizer;

public class MapLoader extends TmxMapLoader {

//    @Override
//    public TmxMap load(String mapPath) { // how good? 3
//        Logger.logFuncStart("mapPath:" + mapPath);
//        TiledMap tiledMap = super.load(mapPath, new TmxMapLoader.Parameters());
//        return new TmxMap(tiledMap, mapPath);
//    }

    @Override
//    public TiledMap loadTiledMap(FileHandle tmxFile, Parameters parameter, ImageResolver imageResolver) { // gdx 1.9.10
    protected TiledMap loadTilemap(Element root, FileHandle tmxFile, ImageResolver imageResolver) { // gdx 1.9.9
        Logger.logFuncStart();
        TmxMap map = new TmxMap(tmxFile.path());

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

        // inject zone 1
        map.width = mapWidth;
        map.height = mapHeight;
        map.tileWidth = tileWidth;
        map.tileHeight = tileHeight;
        if (mapOrientation != null && mapOrientation.equals("isometric")) {
            map.isometric = true;
        }
        this.map = map;
        // inject zone 2

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
            loadLayer(map, map.getLayers(), element, tmxFile, imageResolver);
        }
        return map;
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
//        return BaseTmxMapLoader.getRelativeFileHandle(file, path);
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
}
