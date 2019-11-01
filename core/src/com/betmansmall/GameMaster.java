package com.betmansmall;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.gameLogic.playerTemplates.FactionsManager;
import com.betmansmall.screens.client.ClientSettingsScreen;
import com.betmansmall.screens.client.GameScreen;
import com.betmansmall.screens.menu.HelpMenuScreen;
import com.betmansmall.screens.menu.MainMenuScreen;
import com.betmansmall.screens.menu.OptionMenuScreen;
import com.betmansmall.screens.server.ServerSettingsScreen;
import com.betmansmall.server.SessionSettings;
import com.betmansmall.server.accouting.UserAccount;
import com.betmansmall.util.logging.Logger;
import com.kotcrab.vis.ui.VisUI;

import org.apache.commons.cli.CommandLine;

/**
 * Created by BetmanSmall on 13.10.201x.
 */
public class GameMaster extends Game {
    private Array<Screen> screensStack;

    public Array<Image> backgroundImages;
    public Array<String> gameLevelMaps;

    public UserAccount userAccount;
    public AssetManager assetManager;
    public SessionSettings sessionSettings;
    public FactionsManager factionsManager;
    public Screen mainMenuScreen;
    public Screen optionMenuScreen;
    public Screen helpMenuScreen;

    public CommandLine cmd;

    public GameMaster() {}

    public GameMaster(CommandLine cmd) {
        Logger.logFuncStart("cmd.getArgs().length:" + cmd.getArgs().length);
        Logger.logFuncStart("cmd.getOptions().length:" + cmd.getOptions().length);
        this.cmd = cmd;
    }

    @Override
    public void create() {
        Logger.logFuncStart();
        VisUI.load();
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
//        gameLevelMaps.add("maps/test.tmx");
//        FileHandle mapsDir = Gdx.files.internal("maps");
//        if (mapsDir.list().length == 0) {
//            gameLevelMaps.add("maps/desert.tmx");
//            gameLevelMaps.add("maps/summer.tmx");
//            gameLevelMaps.add("maps/winter.tmx");
            gameLevelMaps.add("maps/arena0.tmx");
            gameLevelMaps.add("maps/randomMap.tmx");
            gameLevelMaps.add("maps/island.tmx");
            gameLevelMaps.add("maps/arena1.tmx");
            gameLevelMaps.add("maps/arena2.tmx");
            gameLevelMaps.add("maps/old/arena3.tmx");
            gameLevelMaps.add("maps/arena4.tmx");
            gameLevelMaps.add("maps/arena4_1.tmx");
            gameLevelMaps.add("maps/sample.tmx");
//        } else {
//            for (FileHandle fileHandle : mapsDir.list()) {
//                if (fileHandle.extension().equals("tmx")) {
//                    Gdx.app.log("GameMaster::GameMaster()", "-- gameLevelMaps.add():" + fileHandle.path());
//                    gameLevelMaps.add(fileHandle.path());
//                }
//            }
//        }
        Gdx.app.log("GameMaster::GameMaster()", "-- gameLevelMaps.size:" + gameLevelMaps.size);

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
        gameLevelMaps.clear();

        assetManager.dispose();
//        factionsManager.dispose();
//        sessionSettings.dispose();
        mainMenuScreen.dispose();
        optionMenuScreen.dispose();
        helpMenuScreen.dispose();
//        removeAllScreens();
//        Gdx.app.exit();
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
        super.render();
    }

    @Override
    public void resize(int width, int height) {
        Logger.logDebug("resize(" + width + ", " + height + ")");
        super.resize(width, height);
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
}
