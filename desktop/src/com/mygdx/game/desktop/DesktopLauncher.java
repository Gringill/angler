package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.spoffordstudios.game.Main;

public class DesktopLauncher {
	public static void main(String[] arg) {
		TexturePacker.process("../core/assets/unpacked", "../core/assets", Main.ATLAS_NAME);
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1920/2;
		config.height = 1080/2;
		new LwjglApplication(new Main(), config);
	}
}