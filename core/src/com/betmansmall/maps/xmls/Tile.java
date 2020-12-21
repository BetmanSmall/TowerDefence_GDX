package com.betmansmall.maps.xmls;

import javax.xml.bind.annotation.XmlAttribute;

public class Tile {
    private String id;
    private String terrain;

    @XmlAttribute
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute
    public String getTerrain() {
        return terrain;
    }

    public void setTerrain(String terrain) {
        this.terrain = terrain;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Tile{");
        sb.append("id='").append(id).append('\'');
        sb.append(", terrain='").append(terrain).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
