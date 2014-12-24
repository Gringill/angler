package com.spoffordstudios.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import engine.Editor;
import engine.Game;

@Deprecated
public class DesktopEditorLauncher {
	public static void main(String[] arg) {
		Game game;
		final LwjglCanvas canvasGame = new LwjglCanvas(game = new Game(1920, 1080));
		TexturePacker.process("core/assets/unpacked", "core/assets", Game.ATLAS_NAME);
		Editor editor = new Editor();
//		editor.connectToGame(game, canvasGame.getCanvas());
	}
}