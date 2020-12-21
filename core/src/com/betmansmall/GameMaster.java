package com.betmansmall;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.gameLogic.playerTemplates.FactionsManager;
import com.betmansmall.maps.TsxLoader;
import com.betmansmall.screens.client.ClientSettingsScreen;
import com.betmansmall.screens.client.GameScreen;
import com.betmansmall.screens.menu.HelpMenuScreen;
import com.betmansmall.screens.menu.MainMenuScreen;
import com.betmansmall.screens.menu.MapEditorScreen;
import com.betmansmall.screens.menu.OptionMenuScreen;
import com.betmansmall.screens.server.ServerSettingsScreen;
import com.betmansmall.server.SessionSettings;
import com.betmansmall.server.accouting.UserAccount;
import com.betmansmall.utils.Version;
import com.betmansmall.utils.logging.Logger;
import com.kotcrab.vis.ui.VisUI;

import org.apache.commons.cli.CommandLine;

/**
 * Created by BetmanSmall on 13.10.201x.
 */
public class GameMaster extends Game {
    public Version version;
    private Array<Screen> screensStack;

    public Array<Image> backgroundImages;
    public Array<String> allMaps;
    public Array<String> gameLevelMaps;

    public Array<FileHandle> tileSetsFileHandles;

    public UserAccount userAccount;
    public AssetManager assetManager;
    public SessionSettings sessionSettings;
    public FactionsManager factionsManager;
    public Screen mainMenuScreen;
    public Screen mapEditorScreen;
    public Screen optionMenuScreen;
    public Screen helpMenuScreen;

    public CommandLine cmd;

    public GameMaster() {}

    public GameMaster(CommandLine cmd) {
        Logger.instance().setCmd(cmd);
        Logger.logFuncStart("cmd.getArgs().length:" + cmd.getArgs().length);
        Logger.logFuncStart("cmd.getOptions().length:" + cmd.getOptions().length);
        this.cmd = cmd;
    }

    @Override
    public void create() {
        Logger.logFuncStart();
        VisUI.load();
//        if (Gdx.app.getType() == Application.ApplicationType.Android) {
//            VisUI.getSkin().getFont("default-font").getData().setScale(Gdx.graphics.getHeight() * 0.0025f, Gdx.graphics.getHeight() * 0.0025f);
//        } else {
//            VisUI.getSkin().getFont("default-font").getData().setScale(Gdx.graphics.getHeight()*0.005f, Gdx.graphics.getHeight()*0.005f);
//        }
        this.version = new Version();
        screensStack = new Array<>();

        backgroundImages = new Array<>();
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

        gameLevelMaps = new Array<>();
        gameLevelMaps.add("maps/3dArena0.tmx");
        gameLevelMaps.add("maps/aaagen.tmx");
        gameLevelMaps.add("maps/arena0.tmx");
        gameLevelMaps.add("maps/arena1.tmx");
        gameLevelMaps.add("maps/arena2.tmx");
        gameLevelMaps.add("maps/arena4.tmx");
        gameLevelMaps.add("maps/arena4_1.tmx");
        gameLevelMaps.add("maps/arenaEmpty.tmx");
        gameLevelMaps.add("maps/arenaEmpty2.tmx");
        gameLevelMaps.add("maps/desert.tmx");
        gameLevelMaps.add("maps/FirstMap(ByART_).tmx");
        gameLevelMaps.add("maps/island.tmx");
        gameLevelMaps.add("maps/isometric_grass_and_water.tmx");
        gameLevelMaps.add("maps/randomMap.tmx");
        gameLevelMaps.add("maps/sample.tmx");
        gameLevelMaps.add("maps/summer.tmx");
        gameLevelMaps.add("maps/winter.tmx");
//        gameLevelMaps.add("maps/old/arena3.tmx");
//        gameLevelMaps.add("maps/old/arena666.tmx");
//        gameLevelMaps.add("maps/old/govnoAndreyMapa.tmx");
//        gameLevelMaps.add("maps/old/NoNameMap.tmx");
//        gameLevelMaps.add("maps/old/people.tmx");
//        gameLevelMaps.add("maps/old/test.tmx");

        FileHandle mapsDir = Gdx.files.internal("maps");
        Array<FileHandle> handles = new Array<>();
        getHandles(mapsDir, handles);

        tileSetsFileHandles = new Array<>();
        allMaps = new Array<>();
        if (!handles.isEmpty()) {
            for (FileHandle fileHandle : handles) {
                String fileHandleExtension = fileHandle.extension();
                if (fileHandleExtension.equals("tmx")) {
                    Logger.logDebug("allMaps.add():" + fileHandle.path());
                    allMaps.add(fileHandle.path());
                } else if (fileHandleExtension.equals("tsx")) {
                    if (fileHandle.parent().name().equals("other")) {
                        Logger.logDebug("tileSetsFileHandles.add():" + fileHandle.path());
                        tileSetsFileHandles.add(fileHandle);
                        TiledMapTileSet tiledMapTileSet = TsxLoader.loadTiledMapTiles(fileHandle, TsxLoader.loadTileSet(fileHandle));
                        Logger.logDebug("tiledMapTileSet:" + tiledMapTileSet);
                        Logger.logDebug("tiledMapTileSet.getName():" + tiledMapTileSet.getName());
                    }
                }
            }
        } else {
            allMaps.addAll(gameLevelMaps);
        }

        Logger.logDebug("gameLevelMaps:" + gameLevelMaps);
        Logger.logDebug("gameLevelMaps.size:" + gameLevelMaps.size);
        Logger.logDebug("allMaps:" + allMaps);
        Logger.logDebug("allMaps.size:" + allMaps.size);
        Logger.logDebug("tileSetsFileHandles:" + tileSetsFileHandles);

        this.userAccount = new UserAccount("root", null, "accID_" + System.currentTimeMillis());
        this.assetManager = new AssetManager();
        try {
            sessionSettings = new SessionSettings();
            factionsManager = new FactionsManager();
            mainMenuScreen = new MainMenuScreen(this);
            optionMenuScreen = new OptionMenuScreen(this, sessionSettings.gameSettings);
            helpMenuScreen = new HelpMenuScreen(this);
        } catch (Exception exeption) {
            exeption.printStackTrace();
        }

        if (cmd != null && cmd.hasOption("server")) {
            addScreen(new ServerSettingsScreen(this));
        } else if (cmd != null && cmd.hasOption("client")) {
            addScreen(new ClientSettingsScreen(this));
        } else if (cmd != null && cmd.hasOption("mapeditor")) {
            addScreen(new MapEditorScreen(this, gameLevelMaps.random()));
        } else {
            addScreen(mainMenuScreen);
        }
    }

    @Override
    public void dispose() {
        Logger.logFuncStart();
        super.dispose();
        VisUI.dispose();
        screensStack.clear();

        backgroundImages.clear();
        allMaps.clear();
        gameLevelMaps.clear();

        assetManager.dispose();
//        factionsManager.dispose();
//        sessionSettings.dispose();
        mainMenuScreen.dispose();
        optionMenuScreen.dispose();
        helpMenuScreen.dispose();
//        removeAllScreens();
//        removeTopScreen();
        Logger.instance().dispose();
        Gdx.app.exit();
    }

    @Override
    public void pause() {
        Logger.logDebug("Called!");
        super.pause();
    }

    @Override
    public void resume() {
        Logger.logDebug("Called!");
        super.resume();
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        try {
            super.render();
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.logError("ex:" + ex);
        }
    }

    @Override
    public void resize(int width, int height) {
        Logger.logDebug("resize(" + width + ", " + height + ")");
        super.resize(width, height);
    }

    public void getHandles(FileHandle begin, Array<FileHandle> handles) {
        FileHandle[] newHandles = begin.list();
        for (FileHandle f : newHandles) {
            if (f.isDirectory()) {
                getHandles(f, handles);
            } else {
                handles.add(f);
            }
        }
    }

    public void addScreen(Screen screen) {
        Logger.logDebug("adding screen " + screen + ". screensStack:" + screensStack);
        if (screen != null) {
            screensStack.add(screen);
            this.setScreen(screen);
        }
    }

    public void removeTopScreen() {
        Logger.logFuncStart("screensStack:" + screensStack);
        if (screensStack != null) {
            Logger.logInfo("screensStack.size:" + screensStack.size);
            int size = screensStack.size;
            if (size == 0) {
//                Screen screen = screensStack.get(0);
//                if (screen != null) {
//                    if (screen instanceof MainMenuScreen) {
//                        if (mainMenuScreen != null) {
//                            this.setScreen(mainMenuScreen);
//                        } else {
//                            this.setScreen(new MainMenuScreen(this));
//                        }
//                    } else if (screen instanceof MainMenuScreen) {
//                        GameScreen gameScreen = (GameScreen) screen;
//                        if (gameScreen != null) {
//                            this.setScreen(gameScreen);
//                        }
//                    }
//                }
            } else if (size > 0) {
                Screen lastScreen = screensStack.get(size - 1);
                Logger.logDebug("lastScreen:" + lastScreen);
                if (lastScreen instanceof GameScreen) {
                    GameScreen gameScreen1 = (GameScreen) lastScreen;
                    Gdx.app.log("GameMaster::removeTopScreen()", "-- gameScreen1:" + gameScreen1);
                    if (gameScreen1 != null) {
//                        lastScreen.hide();
                        gameScreen1.dispose(); // Нужно ли вызывать? Если вызывать то падает=(
                        screensStack.removeValue(gameScreen1, true); // or false?
//                        Gdx.app.log("GameMaster::removeTopScreen()", "-- gameScreen1.gameInterface:" + gameScreen1.gameInterface);
                        size = screensStack.size;
//                        Logger.logDebug("screensStack.size:" + screensStack.size);
                        if (size > 0) {
                            Screen popToScreen = screensStack.get(size - 1);
                            Logger.logDebug("popToScreen:" + popToScreen);
                            if (popToScreen != null) {
                                this.setScreen(popToScreen);
                            }
                        }
                    } else {
                        this.setScreen(mainMenuScreen);
                    }
                }
            }
        }
        Logger.logDebug("screensStack:" + screensStack);
        Logger.logDebug("gameLevelMaps.size:" + gameLevelMaps.size);
        this.setScreen(mainMenuScreen);
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
            addScreen(new GameScreen(this));
//            addScreen(new GameScreen(this));
            gameLevelMaps.removeIndex(0);
        } else {
            Logger.logDebug("gameLevelMaps.size:" + gameLevelMaps.size);
//            removeAllScreens();
            if(screensStack.size > 1) {
                removeTopScreen();
            } else {
                addScreen(new MainMenuScreen(this));
            }
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GameMaster{");
        sb.append("version=").append(version);
        sb.append(", allMaps=").append(allMaps);
        sb.append(", gameLevelMaps=").append(gameLevelMaps);
        sb.append(", userAccount=").append(userAccount);
        sb.append(", sessionSettings=").append(sessionSettings);
        sb.append(", factionsManager=").append(factionsManager);
        sb.append(", cmd=").append(cmd);
        sb.append('}');
        return sb.toString();
    }
}
