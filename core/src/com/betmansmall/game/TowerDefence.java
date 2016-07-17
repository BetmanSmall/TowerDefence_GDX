package com.betmansmall.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import java.util.ArrayList;


/**
 * Created by BetmanSmall on 13.10.2015.
 */
public class TowerDefence extends Game {

    private ArrayList<Screen> mScreensArray = new ArrayList<Screen>();

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
        AddScreen(mainMenuScreen);
    }

    public void AddScreen(Screen screen) {
        if (screen != null) {
            mScreensArray.add(screen);
            this.setScreen(screen);
        }
    }

    public void RemoveTopScreen() {
        if (mScreensArray != null) {
            int count = mScreensArray.size();
            Screen screen = mScreensArray.get(count - 1);
            if (screen != null) {
                //screen.hide();
                mScreensArray.remove(count - 1);
                Screen popToScreen = mScreensArray.get(count - 2);
                if (popToScreen != null) {
                    this.setScreen(popToScreen);
                }
            }
        }
    }

    public void RemoveAllScreens() {
        if (mScreensArray != null)
        {
            int size = mScreensArray.size();
            for (int i = size - 1; i >= 0; i--) {
                Screen screen = mScreensArray.get(i);
                if (screen != null) {
                    screen.hide();
                    mScreensArray.remove(size - 1);
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
        RemoveAllScreens();
        closeApplication();
    }

    public void closeApplication(){
        Gdx.app.exit();
    }
}
