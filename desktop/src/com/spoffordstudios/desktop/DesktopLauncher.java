package com.spoffordstudios.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

import engine.Game;

public class DesktopLauncher {
	public static void main(String[] arg) {
		TexturePacker.process("core/assets/unpacked", "core/assets/", Game.ATLAS_NAME);
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1920 / 2;
		config.height = 1080 / 2;
		new LwjglApplication(new Game(config.width, config.height), config);
	}
}