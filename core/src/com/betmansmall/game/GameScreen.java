package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.betmansmall.game.gameLogic.GameField;
import com.betmansmall.game.gameLogic.Tower;
import com.betmansmall.game.GameScreenInteface.GameInterface;

public class GameScreen implements Screen {
	private static final float MAX_ZOOM = 2f; //max size
	private static final float MIN_ZOOM = 0.2f; // 2x zoom

	private GameScreen gs;
	private TowerDefence towerDefence;
	class CameraController implements GestureListener {
		float velX, velY;
		boolean flinging = false;
		float initialScale = 1;

		@Override
		public boolean touchDown(float x, float y, int pointer, int button) {
			flinging = false;
			initialScale = cam.zoom;
			return false;
		}

		@Override
		public boolean tap(float x, float y, int count, int button) {
			Vector3 touch = new Vector3(x, y, 0);
			cam.unproject(touch);

			GridPoint2 gameCoordinate = new GridPoint2((int) touch.x, (int) touch.y);
			GridPoint2 tileCooCoordinate = gameField.whichCell(gameCoordinate);

			if(tileCooCoordinate != null) {
				if(gameField.cellIsEmpty(tileCooCoordinate.x, tileCooCoordinate.y)) {
					if(button == 0) {
						gameField.getTowers().add(new Tower(gameField.getLayerForeGround(), gameField.getTowerTiles().get("1"), tileCooCoordinate));
						gameField.waveAlgorithm.searh();
					} else if(button == 1) {
						gameField.waveAlgorithm.searh(tileCooCoordinate.x, tileCooCoordinate.y);
					}
				}
			}
			return false;
		}

		@Override
		public boolean longPress(float x, float y) {
			return false;
		}

		@Override
		public boolean fling(float velocityX, float velocityY, int button) {
			flinging = true;
			velX = cam.zoom * velocityX * 0.5f;
			velY = cam.zoom * velocityY * 0.5f;
			return false;
		}

		@Override
		public boolean pan(float x, float y, float deltaX, float deltaY) {
			return false;
		}

		@Override
		public boolean panStop(float x, float y, int pointer, int button) {
			return false;
		}

		@Override
		public boolean zoom(float initialDistance, float distance) {
			float ratio = initialDistance / distance;
			float newZoom = initialScale * ratio;
			if (newZoom < MAX_ZOOM && newZoom > MIN_ZOOM) {
				cam.zoom = newZoom;
			}
			return false;
		}

		@Override
		public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
			return false;
		}

		public void update () {
			if (flinging) {
				velX *= 0.98f;
				velY *= 0.98f;
				cam.position.add(-velX * Gdx.graphics.getDeltaTime(), velY * Gdx.graphics.getDeltaTime(), 0);
				if (Math.abs(velX) < 0.01f) velX = 0;
				if (Math.abs(velY) < 0.01f) velY = 0;
			}
		}
	}

	public OrthographicCamera cam;

	private final GameInterface gameInterface = new GameInterface();
	private GameField gameField;

	private CameraController cameraController = new CameraController();

	public GameScreen(TowerDefence towerDefence) {
		this.gs = this;
		this.towerDefence = towerDefence;
		this.cam = new OrthographicCamera();
		this.cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		Gdx.input.setInputProcessor(gameInterface.setCommonInputHandler(new GestureDetector(cameraController)));

		gameField = new GameField("maps/arena.tmx");
	}

	@Override
	public void show() {
	}

	private void inputHandler(float delta) {
		if(Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
			if(cam.zoom <= MAX_ZOOM)
				cam.zoom += 0.1f;
			cam.update();
			Gdx.app.log("GameScreen::inputHandler()", "-- Pressed MINUS");
		} else if(Gdx.input.isKeyJustPressed(Input.Keys.PLUS)) {
			if(cam.zoom >= MIN_ZOOM)
				cam.zoom -= 0.1f;
			cam.update();
			Gdx.app.log("GameScreen::inputHandler()", "-- Pressed PLUS");
		} else if(Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_0)) {
			gameField.waveAlgorithm.searh();
			gameField.createTimerForCreeps();
			Gdx.app.log("GameScreen::inputHandler()", "-- Pressed NUMPAD_0");
		} else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
			gameField.isDrawableGrid = !gameField.isDrawableGrid;
			Gdx.app.log("GameScreen::inputHandler()", "-- gameField.isDrawableGrid:" + gameField.isDrawableGrid);
		} else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
			gameField.isDrawableSteps = !gameField.isDrawableSteps;
			Gdx.app.log("GameScreen::inputHandler()", "-- gameField.isDrawableSteps:" + gameField.isDrawableSteps);
		}
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl20.glClearColor(0, 0, 0, 1);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

		gameInterface.act(delta);
		inputHandler(delta);
		cameraController.update();
		cam.update();

		gameField.render(delta, cam);
		gameInterface.draw();
	}

	@Override
	public void resize(int width, int height) {
		cam.viewportHeight = height;
		cam.viewportWidth = width;
		cam.update();
		Gdx.app.log("GameScreen::resize()", "-- New width:" + width + " height:" + height);
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