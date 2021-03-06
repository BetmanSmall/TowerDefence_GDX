package com.betmansmall.maps.xmls;

import com.google.common.base.MoreObjects;

public class Terrain {
    public String name;
    public String tile;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).omitNullValues()
                .add("name", name)
                .add("tile", tile)
                .toString();
    }
}
