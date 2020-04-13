package com.betmansmall.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

/**
 * Created by Crowni on 9/14/2017.
 **/
public class Utils {
    public static void addListeners(EventListener listener, Actor... actors) {
        for (Actor actor : actors)
            actor.addListener(listener);
    }

    public static Drawable getTintedDrawable(TextureAtlas.AtlasRegion region, Color color) {
        Sprite sprite = new Sprite(region);
        sprite.setColor(color);
        return new SpriteDrawable(sprite);
    }
}
