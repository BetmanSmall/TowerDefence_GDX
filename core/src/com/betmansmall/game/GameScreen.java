package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
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

import java.util.ArrayList;

public class GameScreen implements Screen {
	private static final float MAX_ZOOM = 2f; //normal size
	private static final float MIN_ZOOM = 0.2f; // 2x zoom

	private Vector2 dragOld, dragNew;

	private TowerDefence towerDefence;
	private GameScreen gs;
	public OrthographicCamera cam;

	private final GameInterface gameInterface = new GameInterface();
	private GameField gameField;

	public GameScreen(final TowerDefence towerDefence) {
		this.gs = this;
		this.towerDefence = towerDefence;
		this.cam = new OrthographicCamera();
		this.cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		InputMultiplexer im = new InputMultiplexer();
		GestureListener gestureListener = new GestureListener() {
			@Override
			public boolean touchDown(float x, float y, int pointer, int button) {
//				Gdx.app.log("GameScreen::GestureListener::touchDown()", "-- (" + x + ", " + y + ", " + pointer + ", " + button + ");");
				dragOld = dragNew = new Vector2(Gdx.input.getX(), Gdx.input.getY());
				return true; //workaround
			}

			@Override
			public boolean tap(float x, float y, int count, int button) {
//				Gdx.app.log("GameScreen::GestureListener::tap()", "-- (" + x + ", " + y + ", " + count + ", " + button + ");");

				if (gameInterface.update(x, y)) {
					if (gameInterface.isTouched(GameInterface.GameInterfaceElements.START_WAVE_BUTTON)) {
						gameField.waveAlgorithm.searh();
						gameField.createTimerForCreeps();
						for (int i = 0; i < gameField.getTowers().size; i++) {
							gameField.getTowers().get(i).createTimerForTowers();
						}
						gameInterface.setVisible(false, GameInterface.GameInterfaceElements.START_WAVE_BUTTON);
					}
					if (gameInterface.isTouched(GameInterface.GameInterfaceElements.RETURN_BUTTON)) {
						towerDefence.setMainMenu(null);
					}
					return true;
				}
				
				Vector3 touch = new Vector3(x, y, 0);
				cam.unproject(touch);

				GridPoint2 gameCoor = new GridPoint2((int) touch.x, (int) touch.y);
//				Gdx.app.log("GameScreen::GestureListener::tap()", "-- gameCoorX:" + gameCoor.x + " gameCoorY:" + gameCoor.y);

				GridPoint2 tileCoor = gameField.whichCell(gameCoor);
//				Gdx.app.log("GameScreen::GestureListener::tap()", "-- tileCoorX:" + tileCoor.x + " tileCoorY:" + tileCoor.y);

				if(tileCoor != null) {
					if(gameField.cellIsEmpty(tileCoor.x, tileCoor.y)) {
						if(button == 0) {
							gameField.getTowers().add(new Tower(gameField.getLayerForeGround(), gameField.getTowerTiles().get("1"), tileCoor));
							gameField.waveAlgorithm.searh();
						} else if(button == 1) {
							gameField.waveAlgorithm.searh(tileCoor.x, tileCoor.y);
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
				Gdx.app.log("GameScreen::GestureListener::Zoom()", "-- amount:" + amount + " distance:" + distance + " inintD:" + initialDistance);
				if(amount > 0 && cam.zoom < MAX_ZOOM) cam.zoom += amount/10000f;
				if(amount < 0 && cam.zoom > MIN_ZOOM) cam.zoom += amount/10000f;
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

		gameField = new GameField("maps/arena.tmx");
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