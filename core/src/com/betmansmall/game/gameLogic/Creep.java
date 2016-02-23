package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;

/**
 * Created by betmansmall on 22.09.2015.
 */
public class  Creep {
    private GridPoint2 position;
    private boolean alive;
    private int hp;
    private TemplateForUnit templateForUnit;
    private TextureRegion curentFrame;

    private TiledMapTileLayer layer;

    public Creep(GridPoint2 position, TiledMapTileLayer layer, TemplateForUnit templateForUnit) {
        this.position = position;
        this.alive = true;
        this.hp = templateForUnit.getHp();
        this.templateForUnit = templateForUnit;
        this.curentFrame = templateForUnit.getCurrentIdleFrame().getTextureRegion();

        this.layer = layer;

        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        getCollisionLayer().setCell(position.x, position.y, cell);
    }

    public void moveTo(GridPoint2 position) {
        getCollisionLayer().setCell(this.position.x, this.position.y, null);
        this.position = position;
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        getCollisionLayer().setCell(position.x, position.y, cell);
        cell.setTile(templateForUnit.getCurrentIdleFrame());
    }

    public TiledMapTileLayer getCollisionLayer() {
        return layer;
    }

    public GridPoint2 getPosition() {
        return position;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getHp() {
        return hp;
    }

    public TemplateForUnit getTemplateForUnit() {
        return templateForUnit;
    }
}
