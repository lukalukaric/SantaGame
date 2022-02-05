package com.game.santa.desktop.santaWithClasses;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.game.santa.santaWithClasses.SantaGameV2;

public class DesktopLauncherV2 {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new SantaGameV2(), config);
	}
}
