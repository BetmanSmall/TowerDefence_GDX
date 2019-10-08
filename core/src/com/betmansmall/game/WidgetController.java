package com.betmansmall.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.gameLogic.playerTemplates.FactionsManager;
import com.betmansmall.util.logging.Logger;

/**
 * Created by BetmanSmall on 13.10.2015.
 */
public class WidgetController extends Game {
    private static volatile WidgetController instance;

    public Array<Image> backgroundImages;

    public FactionsManager factionsManager;
    public Array<String> gameLevelMaps = new Array<String>();

    public Array<Screen> screensStack = new Array<Screen>();
    public Screen mainMenuScreen;
    public Screen optionMenuScreen;
    public Screen helpMenuScreen;
    public GameSettings gameSettings;

    public AssetManager assetManager = new AssetManager();

    public static WidgetController getInstance() {
        Logger.logDebug("");
        WidgetController localInstance = instance;
        if (localInstance == null) {
            synchronized (WidgetController.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new WidgetController();
                }
            }
        }
        return localInstance;
    }

    @Override
    public void create() {
        Logger.logDebug("");

        instance = this;

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
            gameSettings = new GameSettings();
            factionsManager = new FactionsManager();
            mainMenuScreen = new MainMenuScreen(this);
            optionMenuScreen = new OptionMenuScreen(this, gameSettings);
            helpMenuScreen = new HelpMenuScreen(this);

            addScreen(mainMenuScreen);
        } catch (Exception exeption) {
            exeption.printStackTrace();
        }
    }

    @Override
    public void resize(int width, int height) {
        Logger.logDebug("resize(" + width + ", " + height + ")");
        super.resize(width, height);
    }

    @Override
    public void render() {
        super.render();
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
    public void dispose() {
        Logger.logDebug("Called!");
        super.dispose();
        backgroundImages.clear();
//        factionsManager.dis
        gameLevelMaps.clear();
        screensStack.clear();
        mainMenuScreen.dispose();
        optionMenuScreen.dispose();
        helpMenuScreen.dispose();
//        removeAllScreens();
        Gdx.app.exit();
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
//                    Gdx.app.log("WidgetController::removeTopScreen()", "-- gameScreen1:" + gameScreen1);
//                    if (gameScreen1 != null) {
//                    lastScreen.dispose(); // Нужно ли вызывать? Если вызывать то падает=(
//                        Gdx.app.log("WidgetController::removeTopScreen()", "-- gameScreen1.gameInterface.mapPathLabel:" + gameScreen1.gameInterface.mapPathLabel);
//                        lastScreen.hide();
                        screensStack.removeIndex(count - 1);
//                        Gdx.app.log("WidgetController::removeTopScreen()", "-- gameScreen1.gameInterface:" + gameScreen1.gameInterface);
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
//        Gdx.app.log("WidgetController::removeAllScreens()", "--");
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
            gameSettings.setGameTypeByMap(mapPath);
            addScreen(new GameScreen(factionsManager, gameSettings));
            gameLevelMaps.removeIndex(0);
        } else {
            Logger.logDebug("gameLevelMaps.size:" + gameLevelMaps.size);
//            removeAllScreens();
            if(screensStack.size > 1) {
                removeTopScreen();
            } else {
                addScreen(new MainMenuScreen(instance));
            }
        }
    }
}
