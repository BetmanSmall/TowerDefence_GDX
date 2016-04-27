package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;

/**
 * Created by betma on 24.04.2016.
 */
public class IsometricTiledMapRendererWithSprites extends IsometricTiledMapRenderer {
    public IsometricTiledMapRendererWithSprites(TiledMap map, SpriteBatch spriteBatch) {
        super(map, spriteBatch);
    }

    @Override
    public void renderObject(MapObject object) {
        if(object instanceof TextureMapObject) {
            TextureMapObject textureObj = (TextureMapObject) object;
            super.getBatch().draw(textureObj.getTextureRegion(), textureObj.getX(), textureObj.getY());
        }
    }
}
