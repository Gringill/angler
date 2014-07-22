package com.spoffordstudios.editor;

import java.awt.BorderLayout;
import java.awt.Canvas;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import com.esotericsoftware.minlog.Log;
import com.spoffordstudios.game.Entity;
import com.spoffordstudios.game.Game;

public class Editor extends JFrame {
	public static Editor EDITOR;
	private DefaultListModel<Entity> mod_entity = new DefaultListModel<Entity>();
	private JPanel content;
	private JMenuBar menubar;
	private Level level;
	private int width, height;

	public Editor(int width, int height) {
		super(Game.APP_NAME);
		this.width = width;
		this.height = height;
		EDITOR = this;
		content = (JPanel) getContentPane();
		Log.setLogger(new Log.Logger());
		Log.DEBUG();
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		menubar = Util.defineEditorMenuBar();
		setJMenuBar(menubar);
		setVisible(true);
	}

	public void setGameCanvas(Canvas canvas) {
		content.add(canvas, BorderLayout.CENTER);
		canvas.setSize(width, height);
		EDITOR.pack();
		EDITOR.revalidate();
	}

	public static void setLevel(Level level) {
		EDITOR.level = level;
		Game.GAME.loadLevel(level);
	}

	public Level getLevel() {
		return level;
	}

	public static boolean enabled() {
		return (EDITOR != null);
	}

	public DefaultListModel<Entity> getSharedEntityModel() {
		return mod_entity;
	}
}
