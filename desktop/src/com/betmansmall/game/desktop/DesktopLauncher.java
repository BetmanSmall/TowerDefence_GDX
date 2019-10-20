package com.betmansmall.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.betmansmall.GameMaster;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Tower Defence";
        config.x = -3;
        config.y = 0;
        config.width = 1280;
        config.height = 720;
//        config.useGL30 = true;
//        config.fullscreen = true;
//        config.vSyncEnabled = true;
        new LwjglApplication(new GameMaster(), config);
//        new LwjglApplication(new OrthographicCameraController(), config);
    }
}
