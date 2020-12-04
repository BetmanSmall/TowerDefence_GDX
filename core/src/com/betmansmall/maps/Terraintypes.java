package com.betmansmall.maps;

import java.util.Arrays;

public class Terraintypes {
    private Terrain[] terrain;

    public Terrain[] getTerrain() {
        return terrain;
    }

    public void setTerrain(Terrain[] terrain) {
        this.terrain = terrain;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Terraintypes{");
        sb.append("terrain=").append(Arrays.toString(terrain));
        sb.append('}');
        return sb.toString();
    }
}
