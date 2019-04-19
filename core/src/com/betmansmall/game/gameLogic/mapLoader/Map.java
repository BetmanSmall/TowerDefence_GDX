package com.betmansmall.game.gameLogic.mapLoader;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class Map implements Disposable {
    private Array<MapLayer> mapLayers;
    private MapProperties mapProperties;

    public Map () {
        this.mapLayers = new Array<MapLayer>();
        this.mapProperties = new MapProperties();
    }

    @Override
    public void dispose() {
    }

    public Array<MapLayer> getLayers() {
        return mapLayers;
    }

    public MapProperties getProperties() {
        return mapProperties;
    }
}
