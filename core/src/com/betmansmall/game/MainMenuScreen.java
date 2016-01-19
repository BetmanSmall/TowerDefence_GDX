package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainMenuScreen implements Screen {

    private OrthographicCamera camera;
    private Texture background;
    private SpriteBatch batch;
    private Creep creep;
    private TowerDefence towerDefence;


    public MainMenuScreen(TowerDefence towerDefence){
        this.towerDefence = towerDefence;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        creep = new Creep(new Sprite(new Texture("creep.png")));
        background = new Texture(Gdx.files.internal("buttons.png"));
        //

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Gdx.app.log("Example", "touch started at (" + screenX + ", " + screenY + ")");
                towerDefence.setScreen(new GameScreen(towerDefence));
                return false;
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(background, Gdx.graphics.getWidth()/2-180, 0, 360, 600);
        batch.end();
        Gdx.app.log("GameScreen FPS", (1/delta) + "");
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.position.set(800f, 0f, 100f);
        camera.update();
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
        batch.dispose();
    }
}
