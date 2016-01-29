package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {

    private Image background;
    private Texture buttons;
    private TextureRegion menuButton;

    private TowerDefence towerDefence;
    MenuButtons menuButtons;
    private Stage mmStage;
    private ImageButton settings;


    public MainMenuScreen(TowerDefence towerDefence){
        this.towerDefence = towerDefence;
        create();
    }

    class MenuButtons extends Actor{
        @Override
        public void draw(Batch batch, float parentAlpha) {
            batch.draw(menuButton,getX(),getY(),getWidth(),getHeight());
        }
    }

    private void create() {
        background = new Image(new Texture(Gdx.files.internal("img/background.jpg")));
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        background.setPosition(0f, 0f);

        buttons = new Texture(Gdx.files.internal("img/buttons.png"));
        menuButton = new TextureRegion(buttons, 0, 0, 360, 600);
        mmStage = new Stage(new ScreenViewport());
        menuButtons = new MenuButtons();
        menuButtons.setPosition(mmStage.getWidth() / 2 - buttons.getWidth() / 2, 0);
        menuButtons.setSize(buttons.getWidth(), buttons.getHeight());

        menuButtons.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("down");
                touchDownAnalizer(x, y);
                return true;
            }
        });
        settings = new ImageButton(new Image(new Texture(Gdx.files.internal("img/settings.png"))).getDrawable());
        settings.setSize(175, 175);
        settings.setPosition(Gdx.graphics.getWidth() - settings.getWidth() , 0);
        settings.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                Gdx.app.log("MainScreen", "Settings");
            }
        });


        mmStage.addActor(background);
        mmStage.addActor(menuButtons);
        mmStage.addActor(settings);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(mmStage);
        }

    private void touchDownAnalizer(float x, float y){
        if(y > 73 * menuButtons.getHeight()/600f && y < 231 * menuButtons.getHeight()/600f ){
            Gdx.app.log("MainScreen", "Exit");
            towerDefence.closeApplication();
        }else if(y > 259 * menuButtons.getHeight()/600f && y < 417 * menuButtons.getHeight()/600f ) {
            Gdx.app.log("MainScreen", "About");
        } else if(y > 442 * menuButtons.getHeight()/600f && y < 600 * menuButtons.getHeight()/600f ) {
            Gdx.app.log("MainScreen","Play");
            towerDefence.setScreen(new GameScreen(towerDefence));
        } else {
            Gdx.app.log("MainScreen","Unknown analyzer");
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mmStage.act(delta);
        mmStage.draw();
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
        //Should not be here!
        //dispose();
    }

    @Override
    public void dispose() {
        mmStage.dispose();
    }
}
