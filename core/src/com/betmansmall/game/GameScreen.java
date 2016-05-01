package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.input.GestureDetector;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.betmansmall.game.gameLogic.GameField;
import com.betmansmall.game.GameScreenInteface.GameInterface;

public class GameScreen implements Screen {
	private static final float MAX_ZOOM = 3f; //max size
	private static final float MIN_ZOOM = 0.2f; // 2x zoom
	private float MAX_DESTINATION_X = 0f;
	private float MAX_DESTINATION_Y = 0f;
	private BitmapFont bitmapFont = new BitmapFont();

	private float currentDuration;
	private float MAX_DURATION_FOR_DEFEAT_SCREEN = 5f;

	private Texture defeatScreen;

	private GameScreen gs;
	public OrthographicCamera camera;

	private GameInterface gameInterface;
	private GameField gameField;

	class CameraController implements GestureListener {
		float velX, velY;
		boolean flinging = false;
		float initialScale = 1;
		boolean lastCircleTouched = false;

		@Override
		public boolean touchDown(float x, float y, int pointer, int button) {
			flinging = false;
			initialScale = camera.zoom;
			return false;
		}

		@Override
		public boolean tap(float x, float y, int count, int button) {
			Gdx.app.log("GameScreen::tap()", " -- x:" + x + " y:" + y + " count:" + count + " button:" + button);
//			CHECK IF THE PAUSE BUTTON IS TOUCHED //CHECK IF THE TOWER BUTTON IS TOUCHED
			if(gameInterface.getCreepsRoulette().isButtonTouched(x,y) || gameInterface.getTowersRoulette().isButtonTouched(x,y)) {
				return false;
			}

			Vector3 touch = new Vector3(x, y, 0);
			camera.unproject(touch);
			GridPoint2 grafCoordinate = new GridPoint2((int) touch.x, (int) touch.y);
			GridPoint2 cellCoordinate = gameField.whichCell(grafCoordinate);

			if(cellCoordinate != null) {
				if(button == 0) {
					gameField.towerActions(cellCoordinate.x, cellCoordinate.y);
//				} else if(button == 1) {
//					gameField.createCreep(cellCoordinate.x, cellCoordinate.y);
				} else if(button == 1) {
					gameField.setExitPoint(cellCoordinate.x, cellCoordinate.y);
				} else if(button == 4) {
					gameField.setSpawnPoint(cellCoordinate.x, cellCoordinate.y);
				}
			}
			return false;
		}

		@Override
		public boolean longPress(float x, float y) {
//			gameField.createSpawnTimerForCreeps();
			return false;
		}

		@Override
		public boolean fling(float velocityX, float velocityY, int button) {
			if(!lastCircleTouched) {
				flinging = true;
				velX = camera.zoom * velocityX * 0.5f;
				velY = camera.zoom * velocityY * 0.5f;
			}
			return false;
		}

		@Override
		public boolean pan(float x, float y, float deltaX, float deltaY) {
			if(gameInterface.getTowersRoulette().makeRotation(x, y, deltaX, deltaY)) {
				lastCircleTouched = true;
				return true;
			}
			lastCircleTouched = false;
			if( camera.position.x + -deltaX * camera.zoom < MAX_DESTINATION_X && camera.position.x + -deltaX * camera.zoom > 0)
				camera.position.add(-deltaX * camera.zoom, 0, 0);
			if( Math.abs(camera.position.y +  deltaY * camera.zoom) < MAX_DESTINATION_Y )
				camera.position.add(0, deltaY * camera.zoom, 0);
			return false;
		}

//		@Override
//		public boolean mouseMoved(int screenX, int screenY) {
//
//		}

		@Override
		public boolean panStop(float x, float y, int pointer, int button) {
			return false;
		}

		@Override
		public boolean zoom(float initialDistance, float distance) {
			float ratio = initialDistance / distance;
			float newZoom = initialScale * ratio;
			if (newZoom < MAX_ZOOM && newZoom > MIN_ZOOM) {
				camera.zoom = newZoom;
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
				if(camera.position.x + -velX * Gdx.graphics.getDeltaTime() > 0 && camera.position.x + -velX * Gdx.graphics.getDeltaTime() < MAX_DESTINATION_X )
					camera.position.add(-velX * Gdx.graphics.getDeltaTime(), 0, 0);
				if( Math.abs(camera.position.y + velY * Gdx.graphics.getDeltaTime()) < MAX_DESTINATION_Y )
					camera.position.add(0,  velY * Gdx.graphics.getDeltaTime(), 0);
				if (Math.abs(velX) < 0.01f) velX = 0;
				if (Math.abs(velY) < 0.01f) velY = 0;
			}
		}
	}

	private CameraController cameraController = new CameraController();

	public GameScreen() {
		this.gs = this;
		this.camera = new OrthographicCamera();
		this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		gameField = new GameField("maps/arena3.tmx");
		gameInterface = new GameInterface(gameField);

		Gdx.input.setInputProcessor(gameInterface.setCommonInputHandler(new GestureDetector(cameraController)));
		Gdx.app.log("tag", "cel " + gameField.getSizeCellX() + " field" + gameField.getSizeFieldX());
		Gdx.app.log("tag", "cel " + gameField.getSizeCellY() + " field" + gameField.getSizeFieldY());
		MAX_DESTINATION_X = gameField.getSizeCellX() * gameField.getSizeFieldX();
		MAX_DESTINATION_Y = gameField.getSizeCellY() * gameField.getSizeFieldY() / 2f;
	}

	@Override
	public void show() {
		//Start position of camera
		camera.position.add((gameField.getSizeFieldX()*gameField.getSizeCellX())/2,0,0);
	}

	private void inputHandler(float delta) {
		if(Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
			if(camera.zoom <= MAX_ZOOM)
				camera.zoom += 0.1f;
			camera.update();
			Gdx.app.log("GameScreen::inputHandler()", "-- Pressed MINUS");
		} else if(Gdx.input.isKeyJustPressed(Input.Keys.PLUS)) {
			if(camera.zoom >= MIN_ZOOM)
				camera.zoom -= 0.1f;
			camera.update();
			Gdx.app.log("GameScreen::inputHandler()", "-- Pressed PLUS");
		} else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_0) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_0)) {
//			gameField.setGamePause(!gameField.getGamePaused());
			gameInterface.getCreepsRoulette().buttonClick();
			Gdx.app.log("GameScreen::inputHandler()", "-- Pressed NUM_0");
		} else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
			gameField.isDrawableGrid = !gameField.isDrawableGrid;
			Gdx.app.log("GameScreen::inputHandler()", "-- gameField.isDrawableGrid:" + gameField.isDrawableGrid);
		} else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
			gameField.isDrawableCreeps = !gameField.isDrawableCreeps;
			Gdx.app.log("GameScreen::inputHandler()", "-- gameField.isDrawableCreeps:" + gameField.isDrawableCreeps);
		} else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
			gameField.isDrawableTowers = !gameField.isDrawableTowers;
			Gdx.app.log("GameScreen::inputHandler()", "-- gameField.isDrawableTowers:" + gameField.isDrawableTowers);
		} else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
			gameField.isDrawableRoutes = !gameField.isDrawableRoutes;
			Gdx.app.log("GameScreen::inputHandler()", "-- gameField.isDrawableRoutes:" + gameField.isDrawableRoutes);
		} else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
			gameField.isDrawableGridNav = !gameField.isDrawableGridNav;
			Gdx.app.log("GameScreen::inputHandler()", "-- gameField.isDrawableGridNav:" + gameField.isDrawableGridNav);
		}
	}
	
	@Override
	public void render(float delta) {
//		Gdx.app.log("GameScreen::render()", "-- delta:" + delta);
		Gdx.gl20.glClearColor(0, 0, 0, 1);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

		String gameState = gameField.getGameState();
		if (gameState.equals("In progress")) {
			inputHandler(delta);
			cameraController.update();
			camera.update();
			gameField.render(delta, camera);
			gameInterface.act(delta);
			gameInterface.draw();
			gameInterface.getInterfaceStage().getBatch().begin();
			bitmapFont.getData().setScale(4);
			bitmapFont.setColor(Color.YELLOW);
			bitmapFont.draw(gameInterface.getInterfaceStage().getBatch(), String.valueOf("Gold amount: " + gameField.getGamerGold()), Gdx.graphics.getWidth() / 2 - 150, Gdx.graphics.getHeight() - 10);
			gameInterface.getInterfaceStage().getBatch().end();
		} else if(gameState.equals("Lose")){
			currentDuration += delta;
			if(currentDuration > MAX_DURATION_FOR_DEFEAT_SCREEN) {
				//this.dispose();
				TowerDefence.getInstance().setMainMenu(this);
				return;
			}
			if(defeatScreen == null)
				defeatScreen = new Texture(Gdx.files.internal("img/defeat.jpg"));
			gameInterface.getInterfaceStage().getBatch().begin();
			gameInterface.getInterfaceStage().getBatch().draw(defeatScreen, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			gameInterface.getInterfaceStage().getBatch().end();
		}else if (gameState.equals("Win")){
			currentDuration += delta;
			if(currentDuration > MAX_DURATION_FOR_DEFEAT_SCREEN) {
				//this.dispose();
				TowerDefence.getInstance().setMainMenu(this);
				return;
			}
			if(defeatScreen == null)
				defeatScreen = new Texture(Gdx.files.internal("img/victory.jpg"));
			gameInterface.getInterfaceStage().getBatch().begin();
			gameInterface.getInterfaceStage().getBatch().draw(defeatScreen, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			gameInterface.getInterfaceStage().getBatch().end();
		} else {
			Gdx.app.log("Something goes wrong", "123");
		}
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportHeight = height;
		camera.viewportWidth = width;
		camera.update();
		Gdx.app.log("GameScreen::resize()", "-- New width:" + width + " height:" + height);
		//gameInterface.getInterfaceStage().getViewport().update(width, height);
		//gameInterface.getInterfaceStage().getCamera().viewportHeight = height;
		//gameInterface.getInterfaceStage().getCamera().viewportWidth = width;
		//gameInterface.getInterfaceStage().getCamera().update();
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
		gameField = null;
		gameInterface = null;
	}
}