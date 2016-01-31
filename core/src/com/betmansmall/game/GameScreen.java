package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameScreen implements Screen {

	private static final float MAX_ZOOM = 1f; //normal size
	private static final float MIN_ZOOM = 0.5f; // 2x zoom
	private static final float SPEED_ZOOM = 0.02f;

	private Vector2 dragOld, dragNew;

	private TiledMap _map;
	private IsometricTiledMapRenderer renderer;
	public OrthographicCamera cam;
	private Array<Creep> creeps;

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

		InputMultiplexer im = new InputMultiplexer();
		im.addProcessor(new InputAdapter() {
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				Vector3 touch = new Vector3(screenX, screenY, 0);
				cam.unproject(touch);
				Gdx.app.log("Cam" + touch.x + " " + touch.y, "");

				for (int x = 0; x < 25; x++){
					for(int y = 0; y < 25; y++){
						float x_pos = (x * 64 /2.0f ) + (y * 64 / 2.0f);
						float y_pos = - (x * 32 / 2.0f) + (y * 32 /2.0f) + 16;
						ArrayList<Vector2> tilePoints = new ArrayList<Vector2>();
						tilePoints.add(new Vector2(x_pos,y_pos));
						tilePoints.add(new Vector2(x_pos+32, y_pos+16));
						tilePoints.add(new Vector2(x_pos+64, y_pos));
						tilePoints.add(new Vector2(x_pos+32,y_pos-16));
						if(estimation(tilePoints, touch))
							Gdx.app.log("Tile", "X" + x + " Y" + y);
					}
				}

				if (button == 0) {
					dragNew = new Vector2(Gdx.input.getX(), Gdx.input.getY());

					dragOld = dragNew;
					if (returnButton.getX() < getNormalCoordX(screenX) && getNormalCoordX(screenX) < returnButton.getX() + returnButton.getWidth() &&
							returnButton.getY() < getNormalCoordY(screenY) && getNormalCoordY(screenY) < returnButton.getY() + returnButton.getHeight()) {
						towerDefence.setMainMenu(gs);
						return true;
					}

					if (true) return true; //workaround
				} else if (button == 1) {
					setCreep();
				}
				return false;
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				//cam.zoom += 10f;
				return false;
			}

			@Override
			public boolean scrolled(int amount) {
				if (amount > 0 && cam.zoom < MAX_ZOOM) cam.zoom += SPEED_ZOOM;
				if (amount < 0 && cam.zoom > MIN_ZOOM) cam.zoom -= SPEED_ZOOM;
				cam.update();
				return false;
			}

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				moveCamera();
				return true;
			}

		});
		GestureListener gestureListener = new GestureListener() {

			@Override
			public boolean touchDown(float x, float y, int pointer, int button) {
				return false;
			}

			@Override
			public boolean tap(float x, float y, int count, int button) {
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
				return false;
			}

			@Override
			public boolean panStop(float x, float y, int pointer, int button) {
				return false;
			}

			@Override
			public boolean zoom(float initialDistance, float distance) {
				cam.zoom += initialDistance - distance;
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
		creeps = new Array<Creep>();
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

	public boolean estimation(ArrayList<Vector2> mapPoints, Vector3 touch) {
		int res = 0;
		for(int i=0;i<mapPoints.size()-1;i++)
			res += getDelta(mapPoints.get(i).x, mapPoints.get(i).y, mapPoints.get(i + 1).x, mapPoints.get(i + 1).y, touch);
		res += getDelta(mapPoints.get(3).x,mapPoints.get(3).y, mapPoints.get(0).x, mapPoints.get(0).y, touch);
		if (res == 0) {
			return false;
		}
		else
			return true;
	}
	float getDelta(float q,float w, float e, float r, Vector3 touch) {
		int j = getOktant(q - touch.x, touch.y - w);
		int h = getOktant(e - touch.x, touch.y - r);
		if ((h - j) > 4)
			return  h-j - 8;
		else if ((h-j) < -4)
			return  h-j + 8;
		else if ((h-j) == 4 || (h-j) == -4){
			int f = correlation(q, w, e, r, touch);
			if(f == 0)
				Gdx.app.log("Точка находится на границе полигона","");
			return f;
		}
		return h-j;
	}

	int correlation(float q,float w, float e, float r, Vector3 touch) {
		float result = opredelitel(r,w,1,1)*touch.x + opredelitel(1, 1, e, q)*touch.y + opredelitel(w,q,r,e);
		Gdx.app.log("Функция прямой равна", result + "");
		if(result == 0){
			return 0;
		} else if(result < 0){
			return -4;
		} else if(result > 0)
			return 4;
		return 0;
	}

	int getOktant(float X, float Y) {
		if(0 <= Y && Y < X) {
		return 1;
		}
		else if(0 < X && X <= Y) {
		return 2;
		}
		else if(-Y < X && X <= 0) {
		return 3;
		}
		else if(0 < Y && Y <= -X) {
		return 4;
		}
		else if(X < Y && Y <= 0) {
		return 5;
		}
		else if(Y <= X && X < 0) {
		return 6;
		}
		else if(0 <= X && X < -Y) {
		return 7;
		}
		else if(-X <= Y && Y < 0) {
		return 8;
		}
		return 0;
		}

		float opredelitel(float x, float s, float t, float f) {
		return x*f - s*t;
		}

	@Override
	public void show(){
		_map = new TmxMapLoader().load("img/arena.tmx");
		renderer = new IsometricTiledMapRenderer(_map);

		showCreeps();

	}

	public void showCreeps() {
		//Create tile set
		TiledMapTileSet tileset =  _map.getTileSets().getTileSet("creep");
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
		TiledMapTileLayer layer = (TiledMapTileLayer) _map.getLayers().get("Foreground");
		TiledMapTileLayer layerB = (TiledMapTileLayer) _map.getLayers().get("Background");
		for(int x = 0; x < layer.getWidth();x++){
			for(int y = 0; y < layer.getHeight();y++){
				TiledMapTileLayer.Cell cell = layerB.getCell(x, y);

				//If there is no Foregroung cells, create a new one with creep in it
				/*if(cell == null) {
					cell = new TiledMapTileLayer.Cell();
					layer.setCell(x,y,cell);
					cell.setTile(waterTiles.get("1"));
					creepCellsInScene.add(cell);
				}*/
				if(cell.getTile().getProperties().get("spawn") != null && cell.getTile().getProperties().get("spawn").equals("1")) {
					Gdx.app.log("spawn", "" + cell.getTile().getProperties().get("spawn"));
					cell = new TiledMapTileLayer.Cell();
					layer.setCell(x,y,cell);
					cell.setTile(waterTiles.get("1"));
					creepCellsInScene.add(cell);
				}
			}
		}
	}

	public void setCreep() {

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
	//	dispose();
	}

	@Override
	public void dispose() {
		_map.dispose();
		_map = null;
		renderer.dispose();
		renderer = null;
	}
}