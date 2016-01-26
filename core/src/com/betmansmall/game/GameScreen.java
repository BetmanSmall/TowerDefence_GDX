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
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameScreen implements Screen {

	private int dragX, dragY;
	private TiledMap map;
	private TiledMapTileLayer collisionLayer;
	private IsometricTiledMapRenderer renderer;
	public OrthographicCamera cam;
	private int moveToX, moveToY;
	private Array<Creep> creeps;

	private TiledMapTileLayer creepLayer;
	private TiledMapTile creepTile;
	private TiledMapTileSet creepSet;
	private TiledMapTileLayer.Cell creepCell;

	ArrayList<TiledMapTileLayer.Cell> waterCellsInScene;
	private Map<String,TiledMapTile> waterTiles;
	float elapsedSinceAnimation = 0.0f;

	private int count2, global = 0;

	public GameScreen(TowerDefence towerDefence) {
		this.cam = new OrthographicCamera();
		this.cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				//cam.zoom -= 1f;
				moveToX = screenX;
				moveToY = screenY;
				Vector2 point =creeps.get(1).coordinatesConverter(
						(int)(moveToX/creeps.get(1).getCollisionLayer().getTileWidth()),
						(int)(moveToY/creeps.get(1).getCollisionLayer().getTileHeight()));
				creeps.get(1).setPosition(point.x, point.y);
				Gdx.app.log("Point", (int)(moveToX/creeps.get(1).getCollisionLayer().getTileWidth())+" "+moveToX);
				return false;
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				//cam.zoom += 10f;
				return false;
			}

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				cam.position.x = screenX;
				cam.position.y = screenY;
				Gdx.app.log("Cam position", "x " + cam.position.x + "y " + cam.position.y);
				cam.update();
				return true;
			}
		});
		creeps = new Array<Creep>();
	}

	@Override
	public void show(){
		map = new TmxMapLoader().load("img/arena.tmx");
		renderer = new IsometricTiledMapRenderer(map);


		//Create tile set
		TiledMapTileSet tileset =  map.getTileSets().getTileSet("creep");
		waterTiles = new HashMap<String,TiledMapTile>();

		//Search in tileset objects with property "creep" and put them in waterTiles
		for(TiledMapTile tile:tileset){
			Object property = tile.getProperties().get("creep");
			if(property != null)
				waterTiles.put((String)property,tile);
		}

		//Create an array of cells
		waterCellsInScene = new ArrayList<TiledMapTileLayer.Cell>();
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("Background");
		for(int x = 0; x < layer.getWidth();x++){
			for(int y = 0; y < layer.getHeight();y++){
				TiledMapTileLayer.Cell cell = layer.getCell(x,y);
				Object property = cell.getTile().getProperties().get("wrong");
				if(property != null){
					waterCellsInScene.add(cell);
					Gdx.app.log("Property : " + property, "Count2: " + count2);
					count2++;
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

		elapsedSinceAnimation += Gdx.graphics.getDeltaTime();
		if(elapsedSinceAnimation > 0.5f){
			Gdx.app.log("Count global " + global, "Hello");
			updateWaterAnimations();
			global++;
			elapsedSinceAnimation = 0.0f;
		}

		//Draw creep
//		renderer.getBatch().begin();
//		for (int i = 0; i<10; i++){
//			creep.setPosition(i*64,i*32);
//			creep.draw(renderer.getBatch());
//		}
//
//		renderer.getBatch().end();
//		renderer.getBatch().begin();
//		for (int i = 0; i<creeps.size; i++){
//			creeps.get(i).draw(renderer.getBatch());
//		}


		//renderer.getBatch().end();
		//dispose();
	}

	private void updateWaterAnimations(){
			for (int x = 1; x < waterCellsInScene.size(); x++) {
				TiledMapTileLayer.Cell cell = waterCellsInScene.get(x);
				String property = (String) cell.getTile().getProperties().get("wrong");
				Integer currentAnimationFrame = Integer.parseInt(property);

				currentAnimationFrame++;
				if (currentAnimationFrame > waterTiles.size())
					currentAnimationFrame = 1;

				TiledMapTile newTile = waterTiles.get(currentAnimationFrame.toString());
				cell.setTile(newTile);

		}
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