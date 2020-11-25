package com.betmansmall.game.gameInterface;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public abstract class GameAbsInterface extends Stage implements GestureDetector.GestureListener {
    public GameAbsInterface() {
        super(new ScreenViewport());
    }

    public abstract void render(float delta);
    public abstract void resize();
    public abstract void resize(int width, int height);
//        initInterface();
//        addListeners();
}
