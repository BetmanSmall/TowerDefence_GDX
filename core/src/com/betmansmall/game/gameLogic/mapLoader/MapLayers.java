package com.betmansmall.game.gameLogic.mapLoader;

import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;

import java.util.Iterator;

/** Ordered list of {@link MapLayer} instances owned by a {@link com.badlogic.gdx.maps.Map} */
public class MapLayers implements Iterable<MapLayer> {
    private Array<MapLayer> layers = new Array<MapLayer>();

    /** @param index
     * @return the MapLayer at the specified index */
    public MapLayer get (int index) {
        return layers.get(index);
    }

    /** @param name
     * @return the first layer having the specified name, if one exists, otherwise null */
    public MapLayer get (String name) {
        for (int i = 0, n = layers.size; i < n; i++) {
            MapLayer layer = layers.get(i);
            if (name.equals(layer.getName())) {
                return layer;
            }
        }
        return null;
    }

    /** Get the index of the layer having the specified name, or -1 if no such layer exists. */
    public int getIndex (String name) {
        return getIndex(get(name));
    }

    /** Get the index of the layer in the collection, or -1 if no such layer exists. */
    public int getIndex (MapLayer layer) {
        return layers.indexOf(layer, true);
    }

    /** @return number of layers in the collection */
    public int getCount () {
        return layers.size;
    }

    /** @param layer layer to be added to the set */
    public void add (MapLayer layer) {
        this.layers.add(layer);
    }

    /** @param index removes layer at index */
    public void remove (int index) {
        layers.removeIndex(index);
    }

    /** @param layer layer to be removed */
    public void remove (MapLayer layer) {
        layers.removeValue(layer, true);
    }

    /** @param type
     * @return array with all the layers matching type */
    public <T extends MapLayer> Array<T> getByType (Class<T> type) {
        return getByType(type, new Array<T>());
    }

    /** @param type
     * @param fill array to be filled with the matching layers
     * @return array with all the layers matching type */
    public <T extends MapLayer> Array<T> getByType (Class<T> type, Array<T> fill) {
        fill.clear();
        for (int i = 0, n = layers.size; i < n; i++) {
            MapLayer layer = layers.get(i);
            if (ClassReflection.isInstance(type, layer)) {
                fill.add((T)layer);
            }
        }
        return fill;
    }

    /** @return iterator to set of layers */
    @Override
    public Iterator<MapLayer> iterator () {
        return layers.iterator();
    }
}
