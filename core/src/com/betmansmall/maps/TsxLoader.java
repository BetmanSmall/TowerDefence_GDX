package com.betmansmall.maps;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.betmansmall.game.gameLogic.playerTemplates.SimpleTemplate;
import com.betmansmall.utils.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class TsxLoader {

    public static TiledMapTileSet loadTiledMapTiles(FileHandle fileHandle, Tileset tileset) {
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
        return tiledMapTileSet;
    }

    public static Tileset loadTileSet(FileHandle fileHandle) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Tileset.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Tileset tileSet = (Tileset) jaxbUnmarshaller.unmarshal(fileHandle.file());
            Logger.logDebug("tileSet:" + tileSet);
            return tileSet;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

//    @Override
//    protected void loadTileSet(TiledMap map, XmlReader.Element element, FileHandle tmxFile, ImageResolver imageResolver) {
//        super.loadTileSet(map, element, tmxFile, imageResolver);
//    }
}
