package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {

    private Texture background;
    private Texture buttons;
    private TextureRegion menuButton;
    private Texture settingsTexture;
    private Skin settingsSkin;

    private SpriteBatch mainmenubatch;
    private TowerDefence towerDefence;
    MenuButtons menuButtons;
    private Stage mmStage;
    private ImageButton settings;


    public MainMenuScreen(TowerDefence towerDefence){
        this.towerDefence = towerDefence;
    }

    class MenuButtons extends Actor{
        @Override
        public void draw(Batch batch, float parentAlpha) {
            batch.draw(menuButton,getX(),getY(),getWidth(),getHeight());
        }
    }

    @Override
    public void show() {
        background = new Texture(Gdx.files.internal("img/background.jpg"));
        buttons = new Texture(Gdx.files.internal("img/buttons.png"));
        settingsTexture = new Texture((Gdx.files.internal("img/setting.png")));
        settingsSkin = new Skin();
        settingsSkin.getAtlas().getTextures().
        menuButton = new TextureRegion(buttons, 0, 0, 360, 600);
        mainmenubatch = new SpriteBatch();
        mmStage = new Stage(new ScreenViewport());
        mmStage.clear();
        Gdx.input.setInputProcessor(mmStage);

        menuButtons = new MenuButtons();
        menuButtons.setPosition(mmStage.getWidth() / 2 - buttons.getWidth() / 2, 0);
        menuButtons.setSize(buttons.getWidth(), buttons.getHeight());
        mmStage.addActor(menuButtons);

        menuButtons.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("down");
                touchDownAnalizer(x, y);
                return true;
            }
        });
        settings = new ImageButton(settingsSkin);
        settings.clear();
        settings.setPosition(Gdx.graphics.getWidth() - 200, Gdx.graphics.getHeight() - 200);
        }

    private void touchDownAnalizer(float x, float y){
        if(true){
            towerDefence.setScreen(new GameScreen(towerDefence));
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        menuButtons.setPosition(mmStage.getWidth() / 2 - buttons.getWidth() / 2, 0);
        mmStage.getBatch().begin();
        mmStage.getBatch().draw(background, 0, 0);
        mmStage.getBatch().end();
        mmStage.draw();
        mmStage.act(delta);


        //Gdx.app.log("GameScreen FPS", (1/delta) + "");
    }

    @Override
    public void resize(int width, int height) {
        mmStage.getViewport().update(width, height, true);
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
        mmStage.dispose();
    }
}
