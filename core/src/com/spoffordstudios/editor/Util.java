package com.spoffordstudios.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class Util {
	private static final String ACTION_SAVE = "SAVE";
	private static final String ACTION_NEW_LEVEL = "NEW_LEVEL";
	private static final String ACTION_OPEN = "OPEN";
	private static final String ACTION_SAVE_AS = "SAVE_AS";
	private static final String ACTION_OBBG_EDITOR = "OBBG_EDITOR";
	private static final String ACTION_OBBG_PAINTER = "OBBG_PAINTER";

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
					break;
				case ACTION_OPEN:
					break;
				case ACTION_SAVE_AS:
					break;
				case ACTION_OBBG_EDITOR:
					new ObjectEditor();
					break;
				case ACTION_OBBG_PAINTER:
					new ObjectPainter();
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
		menuItem.setActionCommand(ACTION_OBBG_EDITOR);
		menuItem.addActionListener(action_listener);
		menu.add(menuItem);
		menuItem = new JMenuItem("Open Object Painter", KeyEvent.VK_P);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.ALT_MASK + ActionEvent.SHIFT_MASK));
		menuItem.setActionCommand(ACTION_OBBG_PAINTER);
		menuItem.addActionListener(action_listener);
		menu.add(menuItem);

		menuBar.add(menu);

		return menuBar;
	}
}
