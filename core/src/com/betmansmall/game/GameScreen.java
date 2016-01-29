package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
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

	private Batch spriteBatch = new SpriteBatch(10);
	private Image returnButton;

	ArrayList<TiledMapTileLayer.Cell> creepCellsInScene;
	private Map<String,TiledMapTile> waterTiles;

	private TowerDefence towerDefence;
	private GameScreen gs;
	private float getNormalCoordX(float x) {
		return x;
	}

	private float getNormalCoordY(float y) {
		return (float) Gdx.graphics.getHeight() - y;
	}

	public GameScreen(final TowerDefence towerDefence) {
		this.gs = this;
		this.towerDefence = towerDefence;
		this.cam = new OrthographicCamera();
		this.cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		returnButton = new Image(new Texture(Gdx.files.internal("img/return.png")));
		returnButton.setSize(55, 55);
		returnButton.setPosition(0, Gdx.graphics.getHeight() - returnButton.getHeight());

		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {

				System.out.println("x=" + screenX + " y=" + getNormalCoordY(screenY) + " getY=" + returnButton.getY()
						+ " getyw" + (returnButton.getY() + returnButton.getHeight()));
				if(returnButton.getX() < getNormalCoordX(screenX) && getNormalCoordX(screenX) < returnButton.getX() + returnButton.getWidth() &&
						returnButton.getY() < getNormalCoordY(screenY) && getNormalCoordY(screenY) < returnButton.getY() + returnButton.getHeight() ) {
					towerDefence.setMainMenu(gs);
					return true;
				}

				//cam.zoom -= 1f;
				moveToX = screenX;
				moveToY = screenY;
				Vector2 point = creeps.get(1).coordinatesConverter(
						(int) (moveToX / creeps.get(1).getCollisionLayer().getTileWidth()),
						(int) (moveToY / creeps.get(1).getCollisionLayer().getTileHeight()));
				creeps.get(1).setPosition(point.x, point.y);
				Gdx.app.log("Point", (int) (moveToX / creeps.get(1).getCollisionLayer().getTileWidth()) + " " + moveToX);
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
			if(property != null) {
				waterTiles.put((String) property, tile);
				Gdx.app.log("Tile for tileset", " = " + tile);
			}
		}

		//Create an array of cells
		creepCellsInScene = new ArrayList<TiledMapTileLayer.Cell>();
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("Foreground");
		for(int x = 0; x < layer.getWidth();x++){
			for(int y = 0; y < layer.getHeight();y++){
				TiledMapTileLayer.Cell cell = layer.getCell(x, y);

				//If there is no Foregroung cells, create a new one with creep in it
				if(cell == null) {
					cell = new TiledMapTileLayer.Cell();
					layer.setCell(x,y,cell);
					cell.setTile(waterTiles.get("1"));
					creepCellsInScene.add(cell);
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
		spriteBatch.begin();
		returnButton.draw(spriteBatch, 1);
		spriteBatch.end();
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
		renderer = null;
	}
}