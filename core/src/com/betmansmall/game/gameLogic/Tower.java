package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.gameLogic.playerTemplates.Direction;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.badlogic.gdx.math.Circle; // AlexGor
import com.badlogic.gdx.math.Vector2; //AlexGor

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
    public int capacity;

    private Circle circle; //AlexGor
    private Body body;
    public Array<Shell> shells;

    public Tower(GridPoint2 position, TemplateForTower templateForTower){
        Gdx.app.log("Tower", "Tower(" + position + ", " + templateForTower.toString() + ");");
        this.position = position;
        this.damage = templateForTower.damage;
        this.radius = templateForTower.radiusDetection;
        this.reloadTime = templateForTower.reloadTime;
        this.elapsedReloadTime = 0;

        this.templateForTower = templateForTower;
        this.idleTile = templateForTower.idleTile;
        this.shells = new Array<Shell>();

        this.capacity = (templateForTower.capacity != null) ? templateForTower.capacity : 0;
        this.shells = new Array<Shell>();
//        this.bulletSpawnPoint = new Vector2(getGraphCorX(), getGraphCorY());
        this.circle = new Circle(getGraphCorX(), getGraphCorY(), templateForTower.radiusDetection); // AlexGor

//<<<<<<< BOX2d block
//        int halfSizeCellX = GameField.getSizeCellX() / 2; // TODO ПЕРЕОСМЫСЛИТЬ!
//        int halfSizeCellY = GameField.getSizeCellY() / 2;
//        float coorX = halfSizeCellX * position.y + position.x * halfSizeCellX;
//        float coorY = halfSizeCellY * position.y - position.x * halfSizeCellY;
//        BodyDef bodyDef = new BodyDef();
//        bodyDef.type = BodyDef.BodyType.StaticBody;
//        bodyDef.position.set(coorX + halfSizeCellX, coorY + halfSizeCellY*2);
//        body = GameField.world.createBody(bodyDef);
//        body.setActive(true);
//        body.getFixtureList().get(0).setUserData("tower");

//        CircleShape circleShape = new CircleShape();
//        circleShape.setRadius(100f);
//        body.createFixture(circleShape, 0.0f);
//        body.setUserData(this);
//        body.setTransform(coorX + halfSizeCellX, coorY + halfSizeCellY*2, body.getAngle());

//        FixtureDef fixtureDef = new FixtureDef();
//        fixtureDef.shape = circleShape;
//        fixtureDef.density = 0.5f;
//        fixtureDef.friction = 0.4f;
//        fixtureDef.restitution = 0.6f; // Make it bounce a little bit
//        fixtureDef.isSensor = true;
//
//        Fixture fixture = body.createFixture(fixtureDef);
//        circleShape.dispose();
//        =======
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
            shells.add(new Shell(templateForTower, creep, new Vector2(getGraphCorX(), getGraphCorY()))); // AlexGor
            elapsedReloadTime = 0f;
            return true;
        }
        return false;
    }

    public void moveAllShells(float delta) {
        for(Shell shell : shells) {
            switch(shell.flightOfShell(delta)) {
                case 0:
//                    if(shell.creep.die(damage)) {
//                        GameField.gamerGold += shell.creep.getTemplateForUnit().bounty;
//                    }
//                    break;
                case -1:
                    shell.dispose();
                    shells.removeValue(shell, false);
            }
        }
    }

    //AlexGor
    public float getGraphCorX () {
        int halfSizeCellX = GameField.getSizeCellX() / 2; // TODO ПЕРЕОСМЫСЛИТЬ!
        float pointX = halfSizeCellX * position.y + position.x * halfSizeCellX;
        return pointX + halfSizeCellX;
    }

    public float getGraphCorY () {
        int halfSizeCellY = GameField.getSizeCellY() / 2;
        float pointY = halfSizeCellY * position.y - position.x * halfSizeCellY;
        return pointY + halfSizeCellY*templateForTower.size;
    }

    private float getRegWidth () {
        TextureRegion tmpTextureRegion = templateForTower.ammunitionPictures.get("ammo_" + Direction.UP).getTextureRegion();
        return ((tmpTextureRegion.getRegionWidth()-(tmpTextureRegion.getRegionWidth()*templateForTower.ammoSize))/2);
    }

    private float getRegHeight () {
        TextureRegion tmpTextureRegion = templateForTower.ammunitionPictures.get("ammo_" + Direction.UP).getTextureRegion();
        return ((tmpTextureRegion.getRegionHeight()-(tmpTextureRegion.getRegionHeight()*templateForTower.ammoSize))/2);
    }
    //AlexGor

    public GridPoint2 getPosition() {
        return position;
    }

    public Circle getCircle() {
        return circle;
    } //AlexGor

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
