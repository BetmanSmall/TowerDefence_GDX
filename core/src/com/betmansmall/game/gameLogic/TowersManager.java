package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;

/**
 * Created by betmansmall on 22.02.2016.
 */
public class TowersManager {
    private Array<Tower> towers;

    public TowersManager() {
        towers = new Array<Tower>();
    }

    public Tower createTower(GridPoint2 position, TiledMapTileLayer layer, TemplateForTower templateForTower) {
        towers.add(new Tower(position, layer, templateForTower));
        return towers.peek();
    }

    public void removeTower(GridPoint2 position, TiledMapTileLayer layer) {
        towers.removeValue(getTower(position), false);
        layer.getCell(position.x, position.y).setTile(null);
    }

    public Tower getTower(int id) {
        return towers.get(id);
    }

    public Tower getTower(GridPoint2 position) {
        for(int i=0; i < towers.size; i++) {
            GridPoint2 towerPosition = towers.get(i).getPosition();
            if(towerPosition.equals(position)) {
                return towers.get(i);
            }
        }
        return null;
    }

    public int amountTowers() {
        return towers.size;
    }
}
