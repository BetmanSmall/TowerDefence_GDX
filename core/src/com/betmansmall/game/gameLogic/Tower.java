package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.gameLogic.playerTemplates.Direction;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;

/**
 * Created by Андрей on 24.01.2016.
 */
public class Tower {
    private GridPoint2 position;
    private int damage;
    private int radius;
    private float reloadTime;
    private float elapsedReloadTime;

    private TemplateForTower templateForTower;
    private TiledMapTile idleTile;
    public Array<Shell> shells;

    private Body body;

    public Tower(GridPoint2 position, TemplateForTower templateForTower){
        this.position = position;
        this.damage = templateForTower.damage;
        this.radius = templateForTower.radius;
        this.reloadTime = templateForTower.reloadTime;
        this.elapsedReloadTime = 0;

        this.templateForTower = templateForTower;
        this.idleTile = templateForTower.idleTile;
        this.shells = new Array<Shell>();

        int halfSizeCellX = GameField.getSizeCellX() / 2; // TODO ПЕРЕОСМЫСЛИТЬ!
        int halfSizeCellY = GameField.getSizeCellY() / 2;
        float coorX = halfSizeCellX * position.y + position.x * halfSizeCellX;
        float coorY = halfSizeCellY * position.y - position.x * halfSizeCellY;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(coorX + halfSizeCellX, coorY + halfSizeCellY*2);
        body = GameField.world.createBody(bodyDef);
//        body.setActive(true);
//        body.getFixtureList().get(0).setUserData("tower");

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(100f);
        body.createFixture(circleShape, 0.0f);
        body.setUserData(this);
//        body.setTransform(coorX + halfSizeCellX, coorY + halfSizeCellY*2, body.getAngle());

//        FixtureDef fixtureDef = new FixtureDef();
//        fixtureDef.shape = circleShape;
//        fixtureDef.density = 0.5f;
//        fixtureDef.friction = 0.4f;
//        fixtureDef.restitution = 0.6f; // Make it bounce a little bit
//        fixtureDef.isSensor = true;
//
//        Fixture fixture = body.createFixture(fixtureDef);
        circleShape.dispose();
    }

    public void dispose() {
//        groundBox.dispose();
    }

    public boolean recharge(float delta) {
        elapsedReloadTime += delta;
        if(elapsedReloadTime >= reloadTime) {
            return true;
        }
        return false;
    }

    public boolean shoot(Creep creep) {
        if(elapsedReloadTime >= reloadTime) {
            int halfSizeCellX = GameField.getSizeCellX() / 2; // TODO ПЕРЕОСМЫСЛИТЬ!
            int halfSizeCellY = GameField.getSizeCellY() / 2;
            float coorX = halfSizeCellX * position.y + position.x * halfSizeCellX;
            float coorY = halfSizeCellY * position.y - position.x * halfSizeCellY;
            TextureRegion tmpTextureRegion = templateForTower.ammunitionPictures.get("ammo_" + Direction.UP).getTextureRegion();
            coorX += (tmpTextureRegion.getRegionWidth()-(tmpTextureRegion.getRegionWidth()*templateForTower.ammoSize))/2;
            coorY += (tmpTextureRegion.getRegionHeight()-(tmpTextureRegion.getRegionHeight()*templateForTower.ammoSize))/2;
            shells.add(new Shell(coorX, coorY, creep, templateForTower));
            elapsedReloadTime = 0f;
            return true;
        }
        return false;
    }

    public void moveAllShells(float delta) {
        for(Shell shell: shells) {
            switch(shell.hasReached(delta)) {
                case 0:
                    if(shell.creep.die(damage)) {
                        GameField.gamerGold += shell.creep.getTemplateForUnit().bounty;
//                        GameField.gamerGold = GameField.gamerGold + shell.creep.getTemplateForUnit().bounty;
                    }
                case -1:
                    shell.dispose();
                    shells.removeValue(shell, false);
            }
        }
    }

    public GridPoint2 getPosition() {
        return position;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
    public int getDamage() {
        return damage;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
    public int getRadius() {
        return radius;
    }

    public void setReloadTime(float reloadTime) {
        this.reloadTime = reloadTime;
    }
    public float getReloadTime() {
        return reloadTime;
    }

    public TemplateForTower getTemplateForTower() {
        return templateForTower;
    }

    public TextureRegion getCurentFrame() {
        return idleTile.getTextureRegion();
    }
}
