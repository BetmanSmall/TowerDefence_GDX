package com.betmansmall.maps.xmls;

import com.google.common.base.MoreObjects;

public class Tile {
    public String id;
    public String terrain;
    public String probability;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).omitNullValues()
                .add("id", id)
                .add("terrain", terrain)
                .add("probability", probability)
                .toString();
    }
}
