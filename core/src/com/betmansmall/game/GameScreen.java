package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.utils.Timer;
import com.betmansmall.game.gameLogic.GameField;

//import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameScreen implements Screen {
	private static final float MAX_ZOOM = 2f; //normal size
	private static final float MIN_ZOOM = 0.2f; // 2x zoom

	private Vector2 dragOld, dragNew;

	private TowerDefence towerDefence;
	private GameScreen gs;
	public OrthographicCamera cam;

	private final GameInterface gameInterface = new GameInterface();
	private GameField gameField;

//	private TiledMap _map;
//	private IsometricTiledMapRenderer renderer;
//	private ArrayList<com.betmansmall.game.gameLogic.Creep> creeps;
//	private ArrayList<com.betmansmall.game.gameLogic.Tower> towers;
//	private Map<String,TiledMapTile> waterTiles, towerTiles;
//	private Map<GridPoint2, Integer> stepsForWaveAlgorithm;
//	private TiledMapTileLayer _layer, _layerB;
//	private GridPoint2 exitPoint;
//	private int currentFinishedCreeps, gameOverLimitCreeps;
//	private int intervalForTimerCreeps = 1;
//	private Timer.Task timerForCreeps;

	public GameScreen(final TowerDefence towerDefence) {
		this.gs = this;
		this.towerDefence = towerDefence;
		this.cam = new OrthographicCamera();
		this.cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

//		_map = new TmxMapLoader().load("img/arena.tmx");
//		_layer = (TiledMapTileLayer) _map.getLayers().get("Foreground");
//		_layerB = (TiledMapTileLayer) _map.getLayers().get("Background");

		InputMultiplexer im = new InputMultiplexer();
		GestureListener gestureListener = new GestureListener() {

			@Override
			public boolean touchDown(float x, float y, int pointer, int button) {
				Gdx.app.log("Call function", "touchDown(" + x + ", " + y + ", " + pointer + ", " + button + ");");
				dragOld = dragNew = new Vector2(Gdx.input.getX(), Gdx.input.getY());
				return true; //workaround
			}

			@Override
			public boolean tap(float x, float y, int count, int button) {
				Gdx.app.log("Call function", "tap(" + x + ", " + y + ", " + count + ", " + button + ");");
				
				if(gameInterface.update(x,y)) {
					if(gameInterface.isTouched(GameInterface.GameInterfaceElements.START_WAVE_BUTTON)) {
						gameField.waveAlgorithm();
						gameField.createTimerForCreeps();
						for(int i=0;i<gameField.getTowers().size;i++) {
							gameField.getTowers().get(i).createTimerForTowers();
						}
						gameInterface.setVisible(false,GameInterface.GameInterfaceElements.START_WAVE_BUTTON);
					}
					if(gameInterface.isTouched(GameInterface.GameInterfaceElements.RETURN_BUTTON)) {
						towerDefence.setMainMenu(null);
					}
					return true;
				}
				
				Vector3 touch = new Vector3(x, y, 0);
				GridPoint2 clickedCell = new GridPoint2();
				cam.unproject(touch);

				for (int tileX = 0; tileX < gameField.getSizeFieldX(); tileX++){
					for(int tileY = 0; tileY < gameField.getSizeFieldY(); tileY++){
						float x_pos = (tileX * gameField.getSizeCellX() / 2.0f ) + (tileY * gameField.getSizeCellX() / 2.0f);
						float y_pos = - (tileX * gameField.getSizeCellY() / 2.0f) + (tileY * gameField.getSizeCellY() / 2.0f) + gameField.getSizeCellY() / 2.0f;
						ArrayList<Vector2> tilePoints = new ArrayList<Vector2>();
						tilePoints.add(new Vector2(x_pos,y_pos));
						tilePoints.add(new Vector2(x_pos + gameField.getSizeCellX() / 2.0f,
								y_pos + gameField.getSizeCellY() / 2.0f));
						tilePoints.add(new Vector2(x_pos + gameField.getSizeCellX(), y_pos));
						tilePoints.add(new Vector2(x_pos + gameField.getSizeCellX() / 2.0f,
								y_pos - gameField.getSizeCellY() / 2.0f));
						CollisionDetection cl = new CollisionDetection();
						if(cl.estimation(tilePoints, touch)) {
							Gdx.app.log("Click tile", "x=" + tileX + " y=" + tileY);
							clickedCell = new GridPoint2(tileX,tileY);
						}
					}
				}
				if(CollisionDetection.cellIsEmpty(clickedCell.x, clickedCell.y, gameField.getLayerForeGround())) {
					gameField.getTowers().add(new com.betmansmall.game.gameLogic.Tower(gameField.getLayerForeGround(), gameField.getTowerTiles().get("2"), clickedCell));
					gameField.waveAlgorithm();
				}
				return false;
			}

			@Override
			public boolean longPress(float x, float y) {
				return false;
			}

			@Override
			public boolean fling(float velocityX, float velocityY, int button) {
				return false;
			}

			@Override
			public boolean pan(float x, float y, float deltaX, float deltaY) {
				moveCamera();
				return false;
			}

			@Override
			public boolean panStop(float x, float y, int pointer, int button) {

				return false;
			}

			@Override
			public boolean zoom(float initialDistance, float distance) {
				int amount = ((int)initialDistance - (int)distance) / (int)5f;
				Gdx.app.log("Zoom", "Amount: " + amount + ", distance: " + distance + ", inintD: " + initialDistance);
				if (amount > 0 && cam.zoom < MAX_ZOOM) cam.zoom += amount/10000f;
				if (amount < 0 && cam.zoom > MIN_ZOOM) cam.zoom += amount/10000f;
				cam.update();
				return false;
			}

			@Override
			public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
				return false;
			}
		};
		im.addProcessor(new GestureDetector(gestureListener));

		Gdx.input.setInputProcessor(im);

		gameField = new GameField("img/arena.tmx");
	}

	private void moveCamera() {
		dragNew = new Vector2(Gdx.input.getX(), Gdx.input.getY());
		if (!dragNew.equals(dragOld) && dragOld != null)
		{
			cam.translate(dragOld.x - dragNew.x, dragNew.y - dragOld.y); //Translate by subtracting the vectors
			cam.update();
		}
		dragOld = dragNew; //Drag old becomes drag new.
	}

	@Override
	public void show() {
	}

	private void inputHandler(float delta) {
		if(Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
			Gdx.app.log("inputHandler", "Pressed MINUS");
			if(cam.zoom <= MAX_ZOOM)
				cam.zoom += 0.1f;
			cam.update();
		} else if(Gdx.input.isKeyJustPressed(Input.Keys.PLUS)) {
			Gdx.app.log("inputHandler", "Pressed PLUS");
			if(cam.zoom >= MIN_ZOOM)
				cam.zoom -= 0.1f;
			cam.update();
		} else if(Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_0)) {
			gameField.waveAlgorithm();
			gameField.createTimerForCreeps();
		} else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
			gameField.isDrawableGrid = !gameField.isDrawableGrid;
		} else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
			gameField.isDrawableSteps = !gameField.isDrawableSteps;
		}
	}
	
	@Override
	public void render(float delta) {
		inputHandler(delta);

		Gdx.gl20.glClearColor(0, 0, 0, 1);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

		gameField.render(delta, cam);

		gameInterface.draw();
	}

	@Override
	public void resize(int width, int height) {
		cam.viewportHeight = height;
		cam.viewportWidth = width;
//		cam.position.set(800f, 0f, 100f);
		cam.update();
		Gdx.app.log("Screen resize", "width "+ width + "height "+ height );
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
		//	dispose();
	}

	@Override
	public void dispose() {
		gameField.dispose();
	}
}