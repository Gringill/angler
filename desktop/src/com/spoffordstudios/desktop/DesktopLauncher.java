package com.spoffordstudios.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import controller.Game;

public class DesktopLauncher {
	public static void main(String[] arg) {
		TexturePacker.Settings settings = new TexturePacker.Settings();
		settings.filterMin = Texture.TextureFilter.MipMapLinearNearest;
		settings.filterMag= Texture.TextureFilter.MipMapLinearNearest;
		settings.alias = true;
		settings.paddingX = 50;
		settings.paddingY = 50;
		TexturePacker.process(settings, "core/assets/unpacked", "core/assets/", Game.ATLAS_NAME);

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1440;
		config.height = 900;
		new LwjglApplication(new Game(config.width, config.height), config);
	}
}