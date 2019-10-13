package com.betmansmall.game.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.betmansmall.game.GameMaster;

public class HtmlLauncher extends GwtApplication {
    @Override
    public GwtApplicationConfiguration getConfig() {
        return new GwtApplicationConfiguration(800, 480);
    }

    @Override
    public ApplicationListener createApplicationListener() {
        return new GameMaster();
    }
}
