package com.betmansmall.maps.xmls;

import com.google.common.base.MoreObjects;

public class TerrainTypes {
    public Terrain[] terrain;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).omitNullValues()
                .add("terrain", terrain)
                .toString();
    }
}
