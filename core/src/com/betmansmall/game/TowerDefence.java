package com.betmansmall.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.gameLogic.playerTemplates.FactionsManager;

/**
 * Created by BetmanSmall on 13.10.2015.
 */
public class TowerDefence extends Game {
    private static volatile TowerDefence instance;

    public Array<Image> backgroundImages;

    public FactionsManager factionsManager;
    public Array<String> gameLevelMaps = new Array<String>();

    public Array<Screen> screensStack = new Array<Screen>();
    public Screen mainMenuScreen;
    public Screen optionMenuScreen;
    public Screen helpMenuScreen;

    public static TowerDefence getInstance() {
        Gdx.app.log("TowerDefence::getInstance()", "--");
        TowerDefence localInstance = instance;
        if (localInstance == null) {
            synchronized (TowerDefence.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new TowerDefence();
                }
            }
        }
        return localInstance;
    }

    @Override
    public void create() {
        Gdx.app.log("TowerDefence::create()", "--");
        instance = this;

        FileHandle imagesDir = Gdx.files.internal("backgrounds");
        FileHandle[] fileHandles = imagesDir.list();
        backgroundImages = new Array<Image>();
        for (FileHandle fileHandle : fileHandles) {
            if (fileHandle.extension().equals("png")) {
                Image image = new Image(new Texture(fileHandle));
                image.setFillParent(true);
                backgroundImages.add(image);
            }
        }

        try {
//            factionsManager = new FactionsManager();
            mainMenuScreen = new MainMenuScreen(this);
            optionMenuScreen = new OptionMenuScreen(this);
            helpMenuScreen = new HelpMenuScreen(this);

            addScreen(mainMenuScreen);
        } catch (Exception exeption) {
            exeption.printStackTrace();
        }
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("TowerDefence::resize(" + width + "," + height + ")", "--");
        super.resize(width, height);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void pause() {
        Gdx.app.log("TowerDefence::pause()", "-- Called!");
        super.pause();
    }

    @Override
    public void resume() {
        Gdx.app.log("TowerDefence::resume()", "-- Called!");
        super.resume();
    }

    @Override
    public void dispose() {
        Gdx.app.log("TowerDefence::dispose()", "-- Called!");
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
        Gdx.app.log("TowerDefence::addScreen(" + screen + ")", "-- screensStack:" + screensStack);
        if (screen != null) {
            screensStack.add(screen);
            this.setScreen(screen);
        }
    }

    public void removeTopScreen() {
        Gdx.app.log("TowerDefence::removeTopScreen()", "--");
        if (screensStack != null) {
            int count = screensStack.size;
            if (count > 0) {
                Screen screen = screensStack.get(count - 1);
                if (screen != null) {
//                    screen.dispose(); // Нужно ли вызывать? Если вызывать то падает=(
//                    screen.hide();
                    screensStack.removeIndex(count - 1);
                    count = screensStack.size;
                    if(count > 0) {
                        Screen popToScreen = screensStack.get(count - 1);
                        if (popToScreen != null) {
                            this.setScreen(popToScreen);
                        }
                    }
                }
            }
        }
    }

//    public void removeAllScreens() {
//        Gdx.app.log("TowerDefence::removeAllScreens()", "--");
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
        Gdx.app.log("TowerDefence::nextGameLevel()", "--");
        if(gameLevelMaps.size > 0) {
            removeTopScreen();
            addScreen(new GameScreen(gameLevelMaps.first(), factionsManager, new GameSettings(gameLevelMaps.first()))); // default level of Difficulty for Campaign
            gameLevelMaps.removeIndex(0);
        } else {
            Gdx.app.log("TowerDefence::nextGameLevel()", "-- gameLevelMaps.size:" + gameLevelMaps.size);
//            removeAllScreens();
//            if(screensStack.size > 1) {
//                removeTopScreen();
//            } else {
//                addScreen(new MainMenuScreen(instance));
//            }
        }
    }
}
