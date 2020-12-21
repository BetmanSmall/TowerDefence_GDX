package com.betmansmall.maps.xmls;

import javax.xml.bind.annotation.XmlAttribute;

public class Terrain {
    private String name;
    private String tile;

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public String getTile() {
        return tile;
    }

    public void setTile(String tile) {
        this.tile = tile;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Terrain{");
        sb.append("name='").append(name).append('\'');
        sb.append(", tile='").append(tile).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
