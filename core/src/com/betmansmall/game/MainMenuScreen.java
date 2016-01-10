package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.betmansmall.game.map.Editor.TileSet;
import com.badlogic.gdx.InputProcessor;

public class MainMenuScreen implements Screen {

    private int dragX, dragY;
    private TiledMap map;
    private IsometricTiledMapRenderer renderer;
    private OrthographicCamera cam;


    InputProcessor ip = new InputProcessor() {

        @Override
        public boolean keyDown(int keycode) {
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            dragX = screenX;
            dragY = screenY;
            return true;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            float dX = (float)(screenX-dragX)/(float)Gdx.graphics.getWidth();
            float dY = (float)(dragY-screenY)/(float)Gdx.graphics.getHeight();
            dragX = screenX;
            dragY = screenY;
            cam.position.add(dX * 10f, dY * 10f, 0f);
            cam.lookAt(0,0,0);
            cam.update();
            return true;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return false;
        }

        @Override
        public boolean scrolled(int amount) {
            return false;
        }
    };
    public MainMenuScreen(TowerDefence towerDefence) {
        this.cam = new OrthographicCamera();
        this.cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }


    @Override
    public void show() {
        map = new TmxMapLoader().load("isomap.tmx");

        renderer = new IsometricTiledMapRenderer(map);
    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClearColor(0,0,0,1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setView(cam);
        renderer.render();
        //dispose();
    }

    @Override
    public void resize(int width, int height) {
        cam.viewportWidth = width;
        cam.viewportHeight = height;
        cam.position.set(800f, 0f, 100f);
        cam.update();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
    }
}
