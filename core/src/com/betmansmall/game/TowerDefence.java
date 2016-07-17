package com.betmansmall.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by BetmanSmall on 13.10.2015.
 */
public class TowerDefence extends Game {

    /**
     * List of all screens in the stack
     */
    private List<Screen> screensArray = new ArrayList<Screen>();

    private static volatile TowerDefence instance;

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
//        Gdx.graphics.setDisplayMode(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),true);
        instance = this;
        Screen mainMenuScreen = new MainMenuScreen(this);
        addScreen(mainMenuScreen);
    }

    /**
     * @brief Add screen
     * @param screen
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
            int count = screensArray.size();
            if (count > 0) {
                Screen screen = screensArray.get(count - 1);
                if (screen != null) {
                    //screen.hide();
                    screensArray.remove(count - 1);
                    Screen popToScreen = screensArray.get(count - 2);
                    if (popToScreen != null) {
                        this.setScreen(popToScreen);
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
            int size = screensArray.size();
            if (size > 0) {
                for (int i = size - 1; i >= 0; i--) {
                    Screen screen = screensArray.get(i);
                    if (screen != null) {
                        screen.hide();
                        screensArray.remove(size - 1);
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
        super.dispose();
        removeAllScreens();
        closeApplication();
    }

    public void closeApplication(){
        Gdx.app.exit();
    }
}
