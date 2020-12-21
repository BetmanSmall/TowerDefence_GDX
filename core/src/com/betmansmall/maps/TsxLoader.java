package com.betmansmall.maps;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.betmansmall.game.gameLogic.playerTemplates.SimpleTemplate;
import com.betmansmall.maps.jsons.GsonTileSet;
import com.betmansmall.maps.xmls.Tileset;
import com.betmansmall.utils.json.JSONObject;
import com.betmansmall.utils.json.XML;
import com.betmansmall.utils.logging.Logger;

//import com.google.common.base.MoreObjects;
//import org.json.JSONObject;
//import org.json.XML;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TsxLoader {
    private static Gson gson = new GsonBuilder().create();

    public static TiledMapTileSet loadTiledMapTiles(FileHandle fileHandle, Tileset tileset) {
//        Logger.logFuncStart("fileHandle:" + fileHandle, "tileset:" + tileset);
        TiledMapTileSet tiledMapTileSet = new TiledMapTileSet();
        tiledMapTileSet.setName(tileset.getName());
        tiledMapTileSet.getProperties().putAll(tileset.getProperties());
        SimpleTemplate simpleTemplate = null;
        try {
            simpleTemplate = new SimpleTemplate(fileHandle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Integer integer : simpleTemplate.tiles.keys()) {
            tiledMapTileSet.putTile(integer, simpleTemplate.tiles.get(integer));
        }
//        Logger.logDebug("MoreObjects.toStringHelper(tiledMapTileSet).toString():" + MoreObjects.toStringHelper(tiledMapTileSet).toString());
//        Logger.logDebug("gson.toJson(tiledMapTileSet):" + gson.toJson(tiledMapTileSet));
        return tiledMapTileSet;
    }

    public static Tileset loadTileSet(FileHandle fileHandle) {
        try {
//            Logger.logDebug("fileHandle.readString():" + fileHandle.readString());
            JSONObject xmlJSONObj = XML.toJSONObject(fileHandle.readString());
            String jsonPrettyPrintString = xmlJSONObj.toString();
//            Logger.logDebug("jsonPrettyPrintString:" + jsonPrettyPrintString);
            GsonTileSet gsonTileSet = gson.fromJson(jsonPrettyPrintString, GsonTileSet.class);

            return gsonTileSet.tileset;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    @Override
//    protected void loadTileSet(TiledMap map, XmlReader.Element element, FileHandle tmxFile, ImageResolver imageResolver) {
//        super.loadTileSet(map, element, tmxFile, imageResolver);
//    }
}
