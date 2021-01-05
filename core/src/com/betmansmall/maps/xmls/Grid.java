package com.betmansmall.maps.xmls;

import com.google.common.base.MoreObjects;

public class Grid {
    public String orientation;
    public String width;
    public String height;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("orientation", orientation)
                .add("width", width)
                .add("height", height)
                .toString();
    }
}
