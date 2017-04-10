package com.betmansmall.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Array;

/**
 * Created by BetmanSmall on 13.10.2015.
 */
public class TowerDefence extends Game {
    /**
     * List of all screens in the stack
     */
    private Array<Screen> screensArray = new Array<Screen>();

    private static volatile TowerDefence instance;

    public Array<String> gameLevelMaps = new Array<String>();

    public void nextGameLevel() {
        if(gameLevelMaps.size > 0) {
//            removeTopScreen();
            addScreen(new GameScreen(gameLevelMaps.first()));
            gameLevelMaps.removeIndex(0);
        } else {
            if(screensArray.size > 1) {
                removeTopScreen();
            } else {
                addScreen(new MainMenuScreen(instance));
            }
        }
    }

    public static TowerDefence getInstance() {
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
        instance = this;
        Screen mainMenuScreen = new MainMenuScreen(this);
        addScreen(mainMenuScreen);
    }

    /**
     * @param screen
     * @brief Add screen
     */
    public void addScreen(Screen screen) {
        if (screen != null) {
            screensArray.add(screen);
            this.setScreen(screen);
        }
    }

    /**
     * @brief Remove screen from the top of the stack
     */
    public void removeTopScreen() {
        if (screensArray != null) {
            int count = screensArray.size;
            if (count > 0) {
                Screen screen = screensArray.get(count - 1);
                if (screen != null) {
//                    screen.dispose(); // Нужно ли вызывать? Если вызывать то падает=(
//                    screen.hide();
                    screensArray.removeIndex(count - 1);
                    count = screensArray.size;
                    if(count > 0) {
                        Screen popToScreen = screensArray.get(count - 1);
                        if (popToScreen != null) {
                            this.setScreen(popToScreen);
                        }
                    }
                }
            }
        }
    }

    /**
     * Remove all screens from the stack
     */
    public void removeAllScreens() {
        if (screensArray != null) {
            int size = screensArray.size;
            if (size > 0) {
                for (int i = size - 1; i >= 0; i--) {
                    Screen screen = screensArray.get(i);
                    if (screen != null) {
//                        screen.hide();
                        screensArray.removeIndex(size - 1);
                    }
                }
            }
        }
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        Gdx.app.log("TowerDefence::dispose()", "-- Called!");
        super.dispose();
        removeAllScreens();
        closeApplication();
    }

    public void closeApplication() {
        Gdx.app.exit();
    }
}
