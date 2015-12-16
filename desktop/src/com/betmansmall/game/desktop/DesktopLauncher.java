package com.betmansmall.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.betmansmall.game.TowerDefence;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Tower Defence";
		config.width = 800;
		config.height = 600;
		config.useGL30 = true;
		new LwjglApplication(new TowerDefence(), config);
//		new LwjglApplication(new OrthographicCameraController(), config);
	}
}
