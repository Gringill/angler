package com.spoffordstudios.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

import engine.Editor;
import engine.Game;

public class DesktopEditorLauncher {
	public static void main(String[] arg) {
		Game game;
		final LwjglCanvas canvasGame = new LwjglCanvas(game = new Game(1920 / 2, 1080 / 2));
		TexturePacker.process("core/assets/unpacked", "core/assets", Game.ATLAS_NAME);
		Editor editor = new Editor(1920f / 2f, 1080f / 2f);
		editor.connectToGame(game, canvasGame.getCanvas());
	}
}