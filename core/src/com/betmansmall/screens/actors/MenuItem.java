package com.betmansmall.screens.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.betmansmall.utils.logging.Logger;

public class MenuItem extends Actor {
    private int currentN;
    private String text;
    private Texture texMenuItem;
    private TextureRegion tex;
    private BitmapFont font;

    public MenuItem(float width, float height, int currentN) {
        texMenuItem = new Texture(Gdx.files.internal("buttons/itemNEW.png"));
        tex = new TextureRegion(texMenuItem, 0, 0, 512, 512);
        this.setWidth(width);
        this.setHeight(height);
        font = new BitmapFont();
        this.currentN = currentN;
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        text = userObject.toString();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(tex, getX(), getY(), this.getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        font.setColor(1, 1, 1, 1);
        font.draw(batch, String.valueOf(currentN), getX() + getWidth()/2, getY() + getHeight()/2 );
        font.draw(batch, text, getX() + getWidth()/3, getY() + getHeight()/2 + getHeight()/3);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
//        return super.hit(x, y, touchable);
        if (x > 0 && x < getWidth() && y > 0 && y < getHeight()) {
            Logger.logDebug("x:" + x, "y:" + y, "this:" + this);
            return this;
//        } else if (x > getX() && x < getRight() && y > getY() && y < getTop()) {
//            Logger.logInfo("x:" + x, "y:" + y, "this:" + this);
        }
        return null;
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "currentN=" + currentN +
                ", text='" + text + '\'' +
                '}';
    }
}
