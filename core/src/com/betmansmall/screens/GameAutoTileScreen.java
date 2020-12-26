package com.betmansmall.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.betmansmall.GameMaster;
import com.betmansmall.game.gameLogic.MapEditorCameraController;
import com.betmansmall.maps.AutoTiler;
import com.betmansmall.maps.TmxMap;
import com.betmansmall.utils.AbstractScreen;
import com.betmansmall.utils.logging.Logger;

public class GameAutoTileScreen extends AbstractScreen {
    private static final int MAP_WIDTH = 32;
    private static final int MAP_HEIGHT = 16;
//    private static final String PROMPT_TEXT = "Click anywhere to generate a new map";
//    private static final Color PROMPT_COLOR = Color.CORAL;
//    private static final float PROMPT_FADE_IN = 2f;
//    private static final float PROMPT_FADE_OUT = 4f;

//    private OrthographicCamera camera;
//    private OrthographicCamera guiCam;
//    private Viewport viewport;
//    private ScreenViewport screenViewport;

//    private SpriteBatch batch;
//    private BitmapFont font;
//    private final GlyphLayout layout = new GlyphLayout();

    private AutoTiler autoTiler;
    private TmxMap tmxMap;
//    private TiledMap map;
    private BatchTiledMapRenderer renderer;
//    private float elapsedTime = 0;

    private MapEditorCameraController mapEditorCameraController;

    public GameAutoTileScreen(GameMaster game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

//        camera = new OrthographicCamera();
//        guiCam = new OrthographicCamera();
//        viewport = new FitViewport(MAP_WIDTH, MAP_HEIGHT, camera);
//        screenViewport = new ScreenViewport(guiCam);
//        guiCam.setToOrtho(false);

//        batch = new SpriteBatch();
//        font = new BitmapFont(Gdx.files.internal("utils/arial-15.fnt"), false);
//        font.setColor(PROMPT_COLOR);
//        layout.setText(font, PROMPT_TEXT);

        autoTiler = new AutoTiler(MAP_WIDTH, MAP_HEIGHT, Gdx.files.internal("maps/other/winter18.json"));
        tmxMap = autoTiler.generateMap();
        if (tmxMap.isometric) {
            renderer = new IsometricTiledMapRenderer(tmxMap);
        } else {
            renderer = new OrthogonalTiledMapRenderer(tmxMap);
        }
        someHappens();

        mapEditorCameraController = new MapEditorCameraController(this);

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(mapEditorCameraController);
        inputMultiplexer.addProcessor(new GestureDetector(mapEditorCameraController));
        inputMultiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.RIGHT) {
                    someHappens();
                }
                return true;
            }

            @Override
            public boolean keyDown(int keycode) {
                if (Input.Keys.PLUS == keycode) {
                    autoTiler.setTimeSleep(true);
                } else if (Input.Keys.MINUS == keycode) {
                    autoTiler.setTimeSleep(false);
                }
                return super.keyDown(keycode);
            }
        });
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    public void someHappens() {
//        final float unitScale = 1f / Math.max(autoTiler.getTileWidth(), autoTiler.getTileHeight());
        autoTiler.generateMap();
//        elapsedTime = 0;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
//        viewport.update(width, height);
//        screenViewport.update(width, height);
        mapEditorCameraController.camera.viewportHeight = height;
        mapEditorCameraController.camera.viewportWidth = width;
        mapEditorCameraController.camera.update();
//        mapEditorInterface.getViewport().update(width, height, true);
        Logger.logDebug("New width:" + width + " height:" + height);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.render(delta);

//        viewport.apply(true);
        mapEditorCameraController.update(delta);
        renderer.setView(mapEditorCameraController.camera);
        renderer.render();

//        elapsedTime += delta;
//        screenViewport.apply(true);
//        batch.setProjectionMatrix(guiCam.combined);
//        batch.begin();
//        font.setColor(PROMPT_COLOR.r, PROMPT_COLOR.g, PROMPT_COLOR.b, (elapsedTime - PROMPT_FADE_IN) % PROMPT_FADE_OUT);
//        font.draw(batch, PROMPT_TEXT, (screenViewport.getScreenWidth() - layout.width) / 2.0f, screenViewport.getScreenHeight() - layout.height);
//        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        tmxMap.dispose();
//        font.dispose();
//        batch.dispose();
    }
}
