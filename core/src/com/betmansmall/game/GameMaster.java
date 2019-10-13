package com.betmansmall.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.betmansmall.CommandProcessor;
import com.betmansmall.Commands;
import com.betmansmall.Cvars;
import com.betmansmall.GdxCommandManager;
import com.betmansmall.GdxCvarManager;
import com.betmansmall.GdxKeyMapper;
import com.betmansmall.Keys;
import com.betmansmall.TTW;
import com.betmansmall.console.RenderedConsole;
import com.betmansmall.cvar.Cvar;
import com.betmansmall.cvar.CvarStateAdapter;
import com.betmansmall.game.gameLogic.playerTemplates.FactionsManager;
import com.betmansmall.graphics.PaletteIndexedBatch;
import com.betmansmall.server.SessionSettings;
import com.betmansmall.util.logging.Logger;

import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collection;

/**
 * Created by BetmanSmall on 13.10.201x.
 */
public class GameMaster extends Game {
    private static final String TAG = "Client";

    private static final boolean DEBUG_AUDIO_UNPACKER = !true;
    private static final boolean DEBUG_VIEWPORTS = !true;

    private final Matrix4 BATCH_RESET = new Matrix4();

    private int viewportHeight;
    private float width, height;

    private final Array<Screen> screensStack = new Array<Screen>();
//    private final SnapshotArray<ScreenBoundsListener> screenBoundsListeners = new SnapshotArray<>(ScreenBoundsListener.class);

//    public static GameMaster game;
//    private FileHandle            home;
    private Viewport              viewport;
    private Viewport              defaultViewport;
    private ScalingViewport       scalingViewport;
    private ExtendViewport        extendViewport;
    private PaletteIndexedBatch   batch;
    private ShaderProgram         shader;
    private ShapeRenderer shapes;
//    private MPQFileHandleResolver mpqs;
    private AssetManager          assets;
    private InputProcessor        input;
    private RenderedConsole       console;
    private GdxCommandManager     commands;
    private GdxCvarManager        cvars;
    private GdxKeyMapper          keys;
    private I18NBundle bundle;
//    private StringTBLs            string;
//    private Colors                colors;
//    private Palettes              palettes;
//    private Colormaps             colormaps;
//    private Fonts                 fonts;
//    private Files                 files;
//    private COFs                  cofs;
//    private Textures              textures;
//    private Audio                 audio;
//    private MusicController       music;
//    private Cursor                cursor;
//    private CharData              charData;
//    private Engine                engine;

    private boolean forceWindowed;
    private boolean forceDrawFps;
    private byte    drawFpsMethod;

//    private final GlyphLayout fps = new GlyphLayout();
    private String realm;

    //---------

    public Array<Image> backgroundImages;

    public FactionsManager factionsManager;
    public Array<String> gameLevelMaps = new Array<String>();
    public Screen mainMenuScreen;
    public Screen optionMenuScreen;
    public Screen helpMenuScreen;
    public SessionSettings sessionSettings;

    public GameMaster() {
        this(TTW.DESKTOP_VIEWPORT_HEIGHT);
    }

    public GameMaster(int viewportHeight) {
//        this.home = home;
        this.viewportHeight = viewportHeight;
    }

    public float width() {
        return width;
    }

    public float height() {
        return height;
    }

    @Override
    public void create() {
        Logger.logFuncStart();
        TTW.game = this;
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        boolean usesStdOut = true;
        final OutputStream consoleOut = usesStdOut ? System.out : Gdx.files.internal("console.out").write(false);

        TTW.console = console = RenderedConsole.wrap(consoleOut);
        try {
            System.setOut(console.out);
            System.setErr(console.out);
        } catch (SecurityException e) {
            console.out.println("stdout could not be redirected to console: " + e.getMessage());
            throw new GdxRuntimeException("Unable to bind console out.", e);
        } finally {
            console.setVisible(false);
            console.addProcessor(CommandProcessor.INSTANCE);
            console.addSuggestionProvider(CommandProcessor.INSTANCE);
            Calendar calendar = Calendar.getInstance();
            DateFormat format = DateFormat.getDateTimeInstance();
            console.out.println(format.format(calendar.getTime()));
//            console.out.println(home.path());
        }

        TTW.assets = assets = new AssetManager();
        Texture.setAssetManager(assets);
        console.create();

        Collection<Throwable> throwables;
        TTW.commands = commands = new GdxCommandManager();
        throwables = Commands.addTo(commands);
        for (Throwable t : throwables) {
            Gdx.app.error(TAG, t.getMessage(), t);
        }

        TTW.cvars = cvars = new GdxCvarManager();
        throwables = Cvars.addTo(cvars);
        for (Throwable t : throwables) {
            Gdx.app.error(TAG, t.getMessage(), t);
        }

        TTW.keys = keys = new GdxKeyMapper();
        throwables = Keys.addTo(keys);
        for (Throwable t : throwables) {
            Gdx.app.error(TAG, t.getMessage(), t);
        }

//        colors.load();

        TTW.input = input = new InputProcessor();
        input.addProcessor(console);
        input.addProcessor(keys.newInputProcessor());

        Gdx.input.setInputProcessor(input);
        Gdx.input.setCatchBackKey(true);
        Gdx.input.setCatchMenuKey(true);

        TTW.scalingViewport = scalingViewport = new ScalingViewport(Scaling.fillY, (float) Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight() * viewportHeight, viewportHeight);
        TTW.extendViewport = extendViewport = new ExtendViewport(TTW.DESKTOP_VIEWPORT_MIN_WIDTH, TTW.DESKTOP_VIEWPORT_HEIGHT, 0, TTW.DESKTOP_VIEWPORT_HEIGHT);
        TTW.defaultViewport = defaultViewport = viewportHeight < TTW.DESKTOP_VIEWPORT_HEIGHT ? scalingViewport : extendViewport;
        TTW.viewport = viewport = defaultViewport;
        ShaderProgram.pedantic = false;
        TTW.shader = shader = new ShaderProgram(
                Gdx.files.internal("shaders/indexpalette3.vert"),
                Gdx.files.internal("shaders/indexpalette3.frag"));
        TTW.batch = batch = new PaletteIndexedBatch(1024, shader); // TODO: adjust this as needed
        TTW.shapes = shapes = new ShapeRenderer();

        bindCvars();

//        if ((Gdx.app.getType() == Application.ApplicationType.Android && !home.child("data").exists()) || DEBUG_AUDIO_UNPACKER) {
//            setScreen(new AudioUnpackerScreen());
//        } else {
//            setScreen(new SplashScreen());
//        }
//
//        // TODO: This needs to be updated if some shader settings change to match the "new" black
//        final float color = 10/255f;//0.025f;
//        Gdx.gl.glClearColor(color, color, color, 1.0f);
//
//        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        // --------

        backgroundImages = new Array<Image>();
        FileHandle imagesDir = Gdx.files.internal("backgrounds");
        FileHandle[] fileHandles = imagesDir.list();
        Logger.logDebug("fileHandles.length:" + fileHandles.length);
        if (fileHandles.length == 0) {
            int index = 1;
            FileHandle fileHandle = null;
            while (true) {
                Logger.logDebug("try load:" + imagesDir + "/background" + index + ".png");
                try {
                    fileHandle = Gdx.files.internal(imagesDir + "/background" + index + ".png");
                    Logger.logDebug("-- fileHandle:" + fileHandle);
                    Logger.logDebug("-- fileHandle.exists():" + fileHandle.exists());
                    Logger.logDebug("-- fileHandle.isDirectory():" + fileHandle.isDirectory());
                    if (fileHandle.exists() && !fileHandle.isDirectory()) {
                        Image image = new Image(new Texture(fileHandle));
                        image.setFillParent(true);
                        backgroundImages.add(image);
                    } else {
                        break;
                    }
                    index++;
                } catch (Exception exp) {
                    Logger.logWarn("exp:" + exp);
                    break;
                }
            }
        } else {
            for (FileHandle fileHandle : fileHandles) {
                if (fileHandle.extension().equals("png")) {
                    Image image = new Image(new Texture(fileHandle));
                    image.setFillParent(true);
                    backgroundImages.add(image);
                }
            }
        }
        Logger.logDebug("backgroundImages.size:" + backgroundImages.size);

        try {
            sessionSettings = new SessionSettings(new GameSettings());
            factionsManager = new FactionsManager();
            mainMenuScreen = new MainMenuScreen();
            optionMenuScreen = new OptionMenuScreen(this, sessionSettings.gameSettings);
            helpMenuScreen = new HelpMenuScreen(this);

            addScreen(mainMenuScreen);
        } catch (Exception exeption) {
            exeption.printStackTrace();
        }
    }

    private void bindCvars() {
        Cvars.Client.Display.ShowFPS.addStateListener(new CvarStateAdapter<Byte>() {
            @Override
            public void onChanged(Cvar<Byte> cvar, Byte from, Byte to) {
                drawFpsMethod = to;
            }
        });

        Cvars.Client.Display.Gamma.addStateListener(new CvarStateAdapter<Float>() {
            @Override
            public void onChanged(Cvar<Float> cvar, Float from, Float to) {
                batch.setGamma(to);
            }
        });

        Cvars.Client.Display.VSync.addStateListener(new CvarStateAdapter<Boolean>() {
            @Override
            public void onChanged(Cvar<Boolean> cvar, Boolean from, Boolean to) {
                Gdx.graphics.setVSync(to);
            }
        });

        Cvars.Client.Realm.addStateListener(new CvarStateAdapter<String>() {
            @Override
            public void onChanged(Cvar<String> cvar, String from, String to) {
                realm = to;
            }
        });
    }

    @Override
    public void resize(int width, int height) {
        Logger.logDebug("resize(" + width + ", " + height + ")");
        this.width  = width;
        this.height = height;
        BATCH_RESET.setToOrtho2D(0, 0, width, height);
        console.resize(width, height);
        //viewport.update(width, height, true);
        scalingViewport.update(width, height, true);
        extendViewport.update(width, height, true);
        super.resize(width, height);
        Gdx.app.debug(TAG, viewport + "; " + width + "x" + height + "; " + viewport.getWorldWidth() + "x" + viewport.getWorldHeight());
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Camera camera = viewport.getCamera();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapes.setProjectionMatrix(camera.combined);

        if (DEBUG_VIEWPORTS) {
            shapes.begin(ShapeRenderer.ShapeType.Filled);
            shapes.setColor(Color.DARK_GRAY);
            shapes.rect(0, 0, 853, 480);
            shapes.setColor(Color.GRAY);
            shapes.rect(0, 0, 640, 480);
            shapes.setColor(Color.BLUE);
            shapes.rect(0, 0, 840, 360);
            shapes.setColor(Color.RED);
            shapes.rect(0, 0, 720, 360);
            shapes.setColor(Color.LIGHT_GRAY);
            shapes.rect(0, 0, 640, 360);
            shapes.setColor(Color.WHITE);
            shapes.rect(0, 0, 100, 100);
            shapes.setColor(Color.GREEN);
            shapes.rect(0, 0, 2, viewport.getWorldHeight());
            shapes.rect(0, viewport.getWorldHeight() - 2, viewport.getWorldWidth(), viewport.getWorldHeight());
            shapes.rect(viewport.getWorldWidth() - 2, 0, 2, viewport.getWorldHeight());
            shapes.rect(0, 0, viewport.getWorldWidth(), 2);
            shapes.end();
        }

        super.render();
//        cursor.act(Gdx.graphics.getDeltaTime());
//        cursor.render(batch);

        Batch b = batch;
        b.setProjectionMatrix(BATCH_RESET);
        b.begin(); {
            batch.setShader(null);
//            if (!TTW.assets.update()) {
//                drawLoading(b);
//            }
//
//            if (drawFpsMethod > 0 || forceDrawFps) {
//                drawFps(b);
//            }

            console.render(b);
        } b.end();
    }

    @Override
    public void pause() {
        Logger.logDebug("Called!");
        super.pause();
    }

    @Override
    public void resume() {
        Logger.logDebug("Called!");
        TTW.game = this;
//        TTW.home = home;
        TTW.viewport = viewport;
        TTW.defaultViewport = defaultViewport;
        TTW.scalingViewport = scalingViewport;
        TTW.extendViewport = extendViewport;
        TTW.batch = batch;
        TTW.shader = shader;
        TTW.shapes = shapes;
//        TTW.mpqs = mpqs;
        TTW.assets = assets;
        TTW.input = input;
        TTW.console = console;
        TTW.commands = commands;
        TTW.cvars = cvars;
        TTW.keys = keys;
        TTW.bundle = bundle;
//        TTW.string = string;
//        TTW.colors = colors;
//        TTW.palettes = palettes;
//        TTW.colormaps = colormaps;
//        TTW.fonts = fonts;
//        TTW.files = files;
//        TTW.cofs = cofs;
//        TTW.textures = textures;
//        TTW.audio = audio;
//        TTW.music = music;
//        TTW.cursor = cursor;
//        TTW.charData = charData;
//        TTW.engine = engine;
        super.resume();
    }

    @Override
    public void dispose() {
        Logger.logDebug("Called!");
        super.dispose();

        Gdx.app.debug(TAG, "Disposing shader...");
        shader.dispose();
        Gdx.app.debug(TAG, "Disposing batch...");
        batch.dispose();

        Collection<Throwable> throwables;
        Gdx.app.debug(TAG, "Saving CVARS...");
        throwables = cvars.saveAll();
        for (Throwable t : throwables) {
            Gdx.app.error(TAG, t.getMessage(), t);
        }

        Gdx.app.debug(TAG, "Saving key assignments...");
        throwables = keys.saveAll();
        for (Throwable t : throwables) {
            Gdx.app.error(TAG, t.getMessage(), t);
        }

        Gdx.app.debug(TAG, "Disposing console...");
        console.dispose();

        Gdx.app.debug(TAG, "Disposing assets...");
//        palettes.dispose();
//        colormaps.dispose();
//        textures.dispose();
        assets.dispose();

        try {
            Gdx.app.debug(TAG, "Resetting stdout...");
            System.setOut(System.out);
            Gdx.app.debug(TAG, "Resetting stderr...");
            System.setErr(System.err);
        } catch (SecurityException ignored) {
        } finally {
            Gdx.app.debug(TAG, "Flushing console...");
            console.out.flush();
            console.out.close();
        }

        backgroundImages.clear();
//        factionsManager.dis
        gameLevelMaps.clear();
        screensStack.clear();
        mainMenuScreen.dispose();
        optionMenuScreen.dispose();
        helpMenuScreen.dispose();
//        removeAllScreens();
//        Gdx.app.exit();
    }

    public void addScreen(Screen screen) {
        Logger.logDebug("adding screen " + screen + ". screensStack:" + screensStack);
        if (screen != null) {
            screensStack.add(screen);
            this.setScreen(screen);
        }
    }

    public void removeTopScreen() {
        Logger.logDebug("screensStack:" + screensStack);
        if (screensStack != null) {
            int count = screensStack.size;
            Logger.logDebug("screensStack.size:" + screensStack.size);
            if (count > 0) {
                Screen lastScreen = screensStack.get(count - 1);
                Logger.logDebug("lastScreen:" + lastScreen);
//                if (lastScreen instanceof GameScreen) {
//                    GameScreen gameScreen1 = (GameScreen) lastScreen;
//                    Gdx.app.log("GameMaster::removeTopScreen()", "-- gameScreen1:" + gameScreen1);
//                    if (gameScreen1 != null) {
//                    lastScreen.dispose(); // Нужно ли вызывать? Если вызывать то падает=(
//                        Gdx.app.log("GameMaster::removeTopScreen()", "-- gameScreen1.gameInterface.mapPathLabel:" + gameScreen1.gameInterface.mapPathLabel);
//                        lastScreen.hide();
                        screensStack.removeIndex(count - 1);
//                        Gdx.app.log("GameMaster::removeTopScreen()", "-- gameScreen1.gameInterface:" + gameScreen1.gameInterface);
                        count = screensStack.size;
                        Logger.logDebug("screensStack.size:" + screensStack.size);
                        if (count > 0) {
                            Screen popToScreen = screensStack.get(count - 1);
                            Logger.logDebug("popToScreen:" + popToScreen);
                            if (popToScreen != null) {
                                this.setScreen(popToScreen);
                            }
                        }
//                    }
//                }
            } else {
                this.setScreen(mainMenuScreen);
            }
        }
        Logger.logDebug("screensStack:" + screensStack);
        Logger.logDebug("gameLevelMaps.size:" + gameLevelMaps.size);
    }

//    public void removeAllScreens() {
//        Gdx.app.log("GameMaster::removeAllScreens()", "--");
//        if (screensStack != null) {
//            for(Screen screen : screensStack) {
//                screen.dispose(); // Дич ебаная. с этими скринами у нас точно какие то проблемы...
//            }
//            screensStack.clear();
//            int size = screensStack.size;
//            if (size > 0) {
//                for (int i = size - 1; i >= 0; i--) {
//                    Screen screen = screensStack.get(i);
//                    if (screen != null) {
////                        screen.hide();
//                        screensStack.removeIndex(size - 1);
//                    }
//                }
//            }
//        }
//    }

    public void nextGameLevel() {
        Logger.logDebug("gameLevelMaps.size:" + gameLevelMaps.size);
        if(gameLevelMaps.size > 0) {
//            removeTopScreen();
            String mapPath = gameLevelMaps.first();
            sessionSettings.gameSettings.setGameTypeByMap(mapPath);
            addScreen(new GameScreen());
            gameLevelMaps.removeIndex(0);
        } else {
            Logger.logDebug("gameLevelMaps.size:" + gameLevelMaps.size);
//            removeAllScreens();
            if(screensStack.size > 1) {
                removeTopScreen();
            } else {
                addScreen(new MainMenuScreen());
            }
        }
    }

    public static class InputProcessor extends InputMultiplexer {
        boolean vibration = true;

        public InputProcessor() {
            Cvars.Client.Input.Vibration.addStateListener(new CvarStateAdapter<Boolean>() {
                @Override
                public void onChanged(Cvar<Boolean> cvar, Boolean from, Boolean to) {
                    vibration = to;
                }
            });
        }

        public void vibrate(int millis) {
            if (vibration) Gdx.input.vibrate(millis);
        }

        public void vibrate(long[] pattern, int repeat) {
            if (vibration) Gdx.input.vibrate(pattern, repeat);
        }
    }
}
