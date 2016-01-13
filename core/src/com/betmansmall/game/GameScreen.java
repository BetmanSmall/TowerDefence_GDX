package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;

import com.badlogic.gdx.InputProcessor;

public class GameScreen implements Screen {

	private int dragX, dragY;
	private TiledMap map;
	private IsometricTiledMapRenderer renderer;
	public OrthographicCamera cam;

	private Texture background;
	private SpriteBatch batch;

	public GameScreen(TowerDefence towerDefence) {
		this.cam = new OrthographicCamera();
		this.cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		background = new Texture(Gdx.files.internal("img/buttons.png"));
		map = new TmxMapLoader().load("img/isomap.tmx");
		renderer = new IsometricTiledMapRenderer(map);
	}

	@Override
	public void show(){
	}

	@Override
	public void render(float delta) {
		Gdx.gl20.glClearColor(0,0,0,1);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);


		batch = new SpriteBatch();
		batch.begin();
		batch.draw(background, 10, 10);
		batch.end();

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