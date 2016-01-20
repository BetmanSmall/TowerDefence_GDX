package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class MainMenuScreen implements Screen {

    private OrthographicCamera camera;
    private Texture background;
    private Texture buttons;
    private SpriteBatch mainmenubatch;
    private TowerDefence towerDefence;
    private Actor

    public MainMenuScreen(TowerDefence towerDefence){
        this.towerDefence = towerDefence;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void show() {
        mainmenubatch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("img/background.jpg"));
        buttons = new Texture(Gdx.files.internal("img/buttons.png"));

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Gdx.app.log("Example", "touch started at (" + screenX + ", " + screenY + ")");
                //towerDefence.setScreen(new GameScreen(towerDefence));
                touchDownAnalizer(screenX, screenY);
                return false;
            }
        });
    }

    private void touchDownAnalizer(int x, int y){
        if(x>=780 && x<= 1140 && y>=442 && y<=600) //if Play button pressed
        {
            towerDefence.setScreen(new GameScreen(towerDefence));
            Gdx.app.log("Playbutton was pressed!","Coordinates were:"+ x + ", " + y);
        }

        if(x>=780 && x<= 1140 && y>=259 && y<=417) //if About button pressed
        {
            Gdx.app.log("About was pressed!", "Coordinates were:" + x + ", " + y);
        }
        if(x>=780 && x<= 1140 && y>=73 && y<=231) //if Exit button pressed
        {
            Gdx.app.log("Exit was pressed!", "Coordinates were:" + x + ", " + y);
            //towerDefence.closeApplication();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mainmenubatch.begin();
        mainmenubatch.draw(background,0,0);
        mainmenubatch.draw(buttons, Gdx.graphics.getWidth() / 2 - 180, 0, 360, 600);
        mainmenubatch.end();
        //Gdx.app.log("GameScreen FPS", (1/delta) + "");
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
        mainmenubatch.dispose();
    }
}
