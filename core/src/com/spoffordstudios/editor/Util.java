package com.spoffordstudios.editor;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.spoffordstudios.game.Game;

public class Util {
	private static final String ACTION_SAVE = "SAVE";
	private static final String ACTION_NEW_LEVEL = "NEW_LEVEL";
	private static final String ACTION_OPEN = "OPEN";
	private static final String ACTION_SAVE_AS = "SAVE_AS";
	private static final String ACTION_ENTITY_EDITOR = "ENTITY_EDITOR";
	private static final String ACTION_ENTITY_PAINTER = "ENTITY_PAINTER";
	private static int GRID_SIZE = 50;

	public static JMenuBar defineEditorMenuBar() {
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem menuItem;
		ActionListener action_listener;

		action_listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				switch (arg0.getActionCommand()) {
				case ACTION_NEW_LEVEL:
					new LevelCreationDialog();
					break;
				case ACTION_SAVE:
					Game.getLevel().save(null);
					break;
				case ACTION_OPEN:
					Level.open();
					break;
				case ACTION_SAVE_AS:
					Game.getLevel().saveAs();
					break;
				case ACTION_ENTITY_EDITOR:
					EntityEditor obj_editor = new EntityEditor();
					break;
				case ACTION_ENTITY_PAINTER:
					EntityPainter objectPainter = new EntityPainter();
					break;
				default:
					break;
				}
			}
		};

		menuBar = new JMenuBar();
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);
		menuItem = new JMenuItem("New level..", KeyEvent.VK_N);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.ALT_MASK));
		menuItem.setActionCommand(ACTION_NEW_LEVEL);
		menuItem.addActionListener(action_listener);
		menu.add(menuItem);
		menuItem = new JMenuItem("Save..", KeyEvent.VK_S);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
		menuItem.setActionCommand(ACTION_SAVE);
		menuItem.addActionListener(action_listener);
		menu.add(menuItem);
		menuItem = new JMenuItem("Save as..", KeyEvent.VK_S);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK + ActionEvent.SHIFT_MASK));
		menuItem.setActionCommand(ACTION_SAVE_AS);
		menuItem.addActionListener(action_listener);
		menu.add(menuItem);
		menuItem = new JMenuItem("Open", KeyEvent.VK_O);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.ALT_MASK));
		menuItem.setActionCommand(ACTION_OPEN);
		menuItem.addActionListener(action_listener);
		menu.add(menuItem);

		menu = new JMenu("Edit");
		menu.setMnemonic(KeyEvent.VK_E);
		menuBar.add(menu);
		menuItem = new JMenuItem("Open Object Editor", KeyEvent.VK_O);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.ALT_MASK + ActionEvent.SHIFT_MASK));
		menuItem.setActionCommand(ACTION_ENTITY_EDITOR);
		menuItem.addActionListener(action_listener);
		menu.add(menuItem);
		menuItem = new JMenuItem("Open Object Painter", KeyEvent.VK_P);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.ALT_MASK + ActionEvent.SHIFT_MASK));
		menuItem.setActionCommand(ACTION_ENTITY_PAINTER);
		menuItem.addActionListener(action_listener);
		menu.add(menuItem);

		menuBar.add(menu);

		return menuBar;
	}

	/**
	 * http://stackoverflow.com/revisions/3449678/3
	 */
	public static String removeExtention(String filePath) {
		// These first few lines the same as Justin's
		File f = new File(filePath);

		// if it's a directory, don't remove the extention
		if (f.isDirectory())
			return filePath;

		String name = f.getName();

		// Now we know it's a file - don't need to do any special hidden
		// checking or contains() checking because of:
		final int lastPeriodPos = name.lastIndexOf('.');
		if (lastPeriodPos <= 0) {
			// No period after first character - return name as it was passed in
			return filePath;
		} else {
			// Remove the last period and everything after it
			File renamed = new File(f.getParent(), name.substring(0, lastPeriodPos));
			return renamed.getPath();
		}
	}

	public static String pullRegionFromTexture(String texture) {
		String[] splitFile = texture.split("/");
		String file = splitFile[splitFile.length - 1];
		return removeExtention(file);
	}

	public static Vector2 getMouseWorldCoords(Vector2 localMouse) {
		OrthographicCamera camera = Game.GAME.getCamera();
		Vector2 cam = new Vector2(camera.position.x, camera.position.y).sub((new Vector2(camera.viewportWidth / 2, camera.viewportHeight / 2)));
		localMouse.y = Gdx.graphics.getHeight() - localMouse.y;
		return localMouse;
	}

	public static Vector2 getMouseWorldCoords(Point localMouse) {
		return getMouseWorldCoords(new Vector2(localMouse.x, localMouse.y));
	}

	public static int getGridSize() {
		return GRID_SIZE;
	}

	public static void setGridSize(int size) {
		GRID_SIZE = size;
	}

	/*
	 * Get the extension of a file.
	 */
	public static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	public static float distPoints(Vector2 a, Vector2 b) {
		return (float) Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
	}

	public static Rectangle getPleasantRectangle(Vector2 a, Vector2 b) {
		Vector2 a2 = a, b2 = b;
		if (b.x > a.x) {
			if (b.y > a.y) {
				// Your good
			} else {
				a2 = new Vector2(a.x, b.y);
				b2 = new Vector2(b.x, a.y);
			}
		} else {
			if (b.y > a.y) {
				a2 = new Vector2(b.x, a.y);
				b2 = new Vector2(a.x, b.y);
			} else {
				a2 = b;
				b2 = a;
			}
		}
		return new Rectangle(a2.x, a2.y, b2.x - a2.x, b2.y - a2.y);
	}

	public static float facingTowardsPoint(Vector2 v1, Vector2 v2) {
		v1 = v2.sub(v1);
		return (float) Math.toDegrees(Math.atan2(v1.y, v1.x));
	}

	public static Vector2 getSnappedWorldPoint(Vector2 worldMouse, int snap, int width, int height) {
		worldMouse.x = (((int) worldMouse.x / snap) * snap) + (width / 2f);
		worldMouse.y = (((int) worldMouse.y / snap) * snap) + (width / 2f);
		return worldMouse;
	}
}
