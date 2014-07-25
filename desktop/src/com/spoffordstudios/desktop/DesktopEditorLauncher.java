package com.spoffordstudios.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.spoffordstudios.editor.Editor;
import com.spoffordstudios.game.Game;

public class DesktopEditorLauncher {
	public static void main(String[] arg) {
		TexturePacker.process("../core/assets/unpacked", "../core/assets", Game.ATLAS_NAME);
		Editor editor = new Editor(1920 / 2, 1080 / 2);
		LwjglCanvas canvasGame = new LwjglCanvas(new Game(1920 / 2, 1080 / 2));
		editor.setGameCanvas(canvasGame.getCanvas());
	}
}
