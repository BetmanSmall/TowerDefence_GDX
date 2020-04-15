package com.betmansmall.screens.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by betma on 06.12.2018.
 */
public class MenuItem extends Actor {
    private static int N = 0;
    private int currentN;

    private Texture texMenuItem;
    private TextureRegion tex;

    private BitmapFont font;

    public MenuItem( float width, float height ) {
        texMenuItem = new Texture(Gdx.files.internal("buttons/itemNEW.png"));
        tex = new TextureRegion(texMenuItem, 0, 0, 512, 512);

        this.setWidth(width);
        this.setHeight(height);

        font = new BitmapFont();

        N += 1;
        currentN = N;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.draw(tex, getX(), getY(), this.getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());

        font.setColor(1, 1, 1, 1);
        font.draw(batch, String.valueOf(currentN), getX() + getWidth()/2, getY() + getHeight()/2 );
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return super.hit(x, y, touchable);
        // Если нажали на данный элемент, то возвращаем его выше
//        return ((x > 0 && x < getWidth() && y > 0 && y < getHeight()) ? this : null);
    }

//    @Override
//    public boolean touchDown(float x, float y, int pointer) {
//        width += 10;
//        height += 10;
//        return true;
//    }
//
//    @Override
//    public void touchUp(float x, float y, int pointer) {
//        width  -= 10;
//        height -= 10;
//    }
}
