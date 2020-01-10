package com.betmansmall.server.data;

import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.gameLogic.Unit;
import com.betmansmall.game.gameLogic.UnitsManager;

import java.util.ArrayList;

public class UnitsManagerData implements NetworkPackage {
    public ArrayList<UnitInstanceData> units;
    public boolean hardOrSoftUpdate;

    public UnitsManagerData(UnitsManager unitsManager) {
        this.units = new ArrayList<>(unitsManager.units.size);
        for (int u = 0; u < unitsManager.units.size; u++) {
            Unit unit = unitsManager.units.get(u);
//        for (Unit unit : unitsManager.units) { // pizda baga || GdxRuntimeException: #iterator() cannot be used nested
            UnitInstanceData unitData = new UnitInstanceData(unit);
            this.units.add(unitData);
        }
        this.hardOrSoftUpdate = true;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("UnitsManagerData[");
        sb.append("units.size():" + units.size());
        if (full) {
            for (UnitInstanceData unitInstanceData : units) {
                sb.append("," + unitInstanceData);
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
