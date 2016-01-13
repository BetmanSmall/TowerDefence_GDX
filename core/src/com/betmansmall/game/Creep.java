package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by betmansmall on 22.09.2015.
 */
public class Creep extends Sprite {
    int hp;
    boolean alive;
    int number;
    private Vector2 velocity = new Vector2();
    public Creep(Sprite sprite){
        super(sprite);
    }

    @Override
    public void draw(Batch batch) {
        update(Gdx.graphics.getDeltaTime());
        super.draw(batch);
    }

    public void update(float delta){

    }
}
