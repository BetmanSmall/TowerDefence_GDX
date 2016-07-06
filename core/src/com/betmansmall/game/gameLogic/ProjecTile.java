package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.utils.ObjectMap;
import com.betmansmall.game.gameLogic.playerTemplates.Direction;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;

/**
 * Created by betmansmall on 29.03.2016.
 */
public class ProjecTile {
    public float x, y;
    public Creep creep;
    public float ammoDistance;
    public float ammoSize;

    public TemplateForTower templateForTower;

    public TextureRegion textureRegion;
    public ObjectMap<String, TiledMapTile> ammunitionPictures;
    private int radius;

    ProjecTile(float x, float y, Creep creep, TemplateForTower templateForTower) {
        this.x = x;
        this.y = y;
        this.creep = creep;
        this.ammoDistance = templateForTower.ammoDistance;
        this.ammoSize = templateForTower.ammoSize;

        this.templateForTower = templateForTower;

        this.textureRegion = templateForTower.ammunitionPictures.get("ammo_" + Direction.UP).getTextureRegion();
        this.ammunitionPictures = templateForTower.ammunitionPictures;
        this.radius = 11;
    }

    /**
     * Говорит пуле постараться достигнуть криппа.
     *
     * @param delta пока не используется. (нужна для ожидания пули времени для перемещения)
     * @return -1 - Пуля не передвинулась. Крип мертв. Нужно убрать пулю из массива пуль.<br>
     * 0 - Пуля передвинулась и достигла крипа.<br>
     * 1 - Пуля передвинулась, но не достигла крипа.<br>
     */
    public int hasReached(float delta) {
        if(creep.isAlive()) {
            float creepCenterX = creep.graphicalCoordinateX + (creep.getCurentFrame().getRegionWidth() / 2);
            float creepCenterY = creep.graphicalCoordinateY + (creep.getCurentFrame().getRegionHeight() / 2);

//            ==================БЫДЛО КОД===============
            if (x == creepCenterX) {
                if (y < creepCenterY) {
                    y += ammoDistance;
                } else if (y > creepCenterY) {
                    y -= ammoDistance;
                }
            } else if (y == creepCenterY) {
                if (x < creepCenterX) {
                    x += ammoDistance;
                } else if (x > creepCenterX) {
                    x -= ammoDistance;
                }
            } else if (x < creepCenterX && y > creepCenterY) {
                x += ammoDistance / 2;
                y -= ammoDistance / 2;
            } else if (x > creepCenterX && y > creepCenterY) {
                x -= ammoDistance / 2;
                y -= ammoDistance / 2;
            } else if (x < creepCenterX && y < creepCenterY) {
                x += ammoDistance / 2;
                y += ammoDistance / 2;
            } else if (x > creepCenterX && y < creepCenterY) {
                x -= ammoDistance / 2;
                y += ammoDistance / 2;
            }
//            ==================БЫДЛО КОД===============

            float x1 = creepCenterX - (radius / 2);
            float y1 = creepCenterY - (radius / 2);
            float x2 = creepCenterX + (radius / 2);
            float y2 = creepCenterY + (radius / 2);

            if (x > x1 && x < x2) {
                if (y > y1 && y < y2) {
                    return 0;
                }
            }
            return 1;
        }
        return -1;
    }

    public void dispose() {
        creep = null;
        templateForTower = null;
        ammunitionPictures = null;
    }
}
