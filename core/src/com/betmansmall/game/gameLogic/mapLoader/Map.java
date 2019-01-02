package com.betmansmall.game.gameLogic.mapLoader;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Disposable;

public class Map implements Disposable {
    private MapLayers mapLayers;
    private MapProperties mapProperties;

    public Map () {
        this.mapLayers = new MapLayers();
        this.mapProperties = new MapProperties();
    }

    @Override
    public void dispose() {
    }

    public MapLayers getLayers() {
        return mapLayers;
    }

    public MapProperties getProperties() {
        return mapProperties;
    }
}
