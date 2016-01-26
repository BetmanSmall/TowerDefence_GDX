package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;

import com.badlogic.gdx.InputProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameScreen implements Screen {

	private int dragX, dragY;
	private TiledMap map;
	private IsometricTiledMapRenderer renderer;
	public OrthographicCamera cam;
	private Creep creep;

	private TiledMapTileLayer creepLayer;
	private TiledMapTile creepTile;
	private TiledMapTileSet creepSet;
	private TiledMapTileLayer.Cell creepCell;


	public GameScreen(TowerDefence towerDefence) {
		this.cam = new OrthographicCamera();
		this.cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				cam.zoom -= 1f;
				return false;
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				//cam.zoom += 10f;
				return false;
			}

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				//cam.position.x = screenX;
				//cam.position.y = screenY;
				Gdx.app.log("Cam position", "x " + cam.position.x + "y " + cam.position.y);
				cam.update();
				return true;
			}
		});
	}

	@Override
	public void show(){
		map = new TmxMapLoader().load("img/arena.tmx");
		renderer = new IsometricTiledMapRenderer(map);
		creep = new Creep(new Sprite(new Texture("img/grunt.png")));

		creepSet = map.getTileSets().getTileSet("creep");
		creepLayer = (TiledMapTileLayer)map.getLayers().get("Foreground");

		for(int x = 0; x < creepLayer.getWidth();x++) {
			for (int y = 0; y < creepLayer.getHeight(); y++) {
				TiledMapTileLayer.Cell cell = creepLayer.getCell(x,y);
				Object property = cell.getTile().getProperties().get("wrong");
				if(property != null){
					creepCell.
				}
			}
		}
	}


	@Override
	public void render(float delta) {
		Gdx.gl20.glClearColor(0, 0, 0, 1);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

		renderer.setView(cam);
		renderer.render();

		//Draw creep
//		renderer.getBatch().begin();
//		for (int i = 0; i<10; i++){
//			creep.setPosition(i*64,i*32);
//			creep.draw(renderer.getBatch());
//		}
//
//		renderer.getBatch().end();
		//dispose();
	}

	@Override
	public void resize(int width, int height) {
		cam.viewportHeight = height;
		cam.viewportWidth = width;
		cam.position.set(800f, 0f, 100f);
		cam.update();
		Gdx.app.log("Screen size", "width "+ width + "height "+ height );
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