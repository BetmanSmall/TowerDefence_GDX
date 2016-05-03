package com.betmansmall.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.betmansmall.game.TowerDefence;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Tower Defence";
		config.width = 1920;
		config.height = 1080;
		config.useGL30 = true;
//		config.fullscreen = true;
//		config.vSyncEnabled = true;
		new LwjglApplication(new TowerDefence(), config);
//		new LwjglApplication(new OrthographicCameraController(), config);
	}
}
