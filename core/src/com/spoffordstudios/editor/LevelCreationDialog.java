package com.spoffordstudios.editor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.spoffordstudios.game.Game;

public class LevelCreationDialog extends JFrame {
	private JLabel lbl_level_name;
	private JTextField input_name;
	private JButton btn_create;
	private JButton btn_cancel;
	private JLabel lbl_size;
	private JTextField input_height;
	private JTextField input_width;
	private JLabel lbl_height;
	private ActionListener listener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case "create":
				createMap();
				break;
			case "cancel":
				cancelCreation();
				break;
			default:
				break;
			}
		}
	};

	public LevelCreationDialog() {
		super("Level Creation Dialog");
		setResizable(false);
		getContentPane().setLayout(null);
		lbl_level_name = new JLabel("Level Name");
		lbl_level_name.setBounds(10, 30, 72, 30);
		lbl_level_name.setHorizontalAlignment(SwingConstants.RIGHT);
		lbl_level_name.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbl_size = new JLabel("Width");
		lbl_size.setBounds(46, 60, 36, 30);
		lbl_size.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbl_size.setHorizontalAlignment(SwingConstants.RIGHT);
		btn_create = new JButton("Create");
		btn_create.setLocation(72, 145);
		btn_create.setSize(100, 50);
		btn_create.setActionCommand("create");
		btn_create.addActionListener(listener);
		btn_cancel = new JButton("Cancel");
		btn_cancel.setLocation(216, 145);
		btn_cancel.setSize(100, 50);
		btn_cancel.setActionCommand("cancel");
		btn_cancel.addActionListener(listener);
		lbl_height = new JLabel("Height");
		lbl_height.setBounds(40, 90, 42, 30);
		lbl_height.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_height.setAlignmentX(0.5f);
		input_width = new JTextField();
		input_width.setText("1000");
		input_width.setBounds(116, 60, 75, 30);
		input_width.setToolTipText("Width");
		input_width.setMaximumSize(new Dimension(200, 50));
		input_width.setColumns(5);
		input_height = new JTextField();
		input_height.setText("1000");
		input_height.setBounds(116, 90, 75, 30);
		input_height.setToolTipText("Height");
		input_height.setMaximumSize(new Dimension(200, 50));
		input_height.setColumns(5);
		input_name = new JTextField();
		input_name.setText("New_Level_Name");
		input_name.setBounds(116, 30, 200, 30);
		input_name.setColumns(15);

		getContentPane().add(btn_create);
		getContentPane().add(btn_cancel);
		getContentPane().add(lbl_height);
		getContentPane().add(input_width);
		getContentPane().add(input_height);
		getContentPane().add(input_name);
		getContentPane().add(lbl_level_name);
		getContentPane().add(lbl_size);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(400, 250);
		setLocationRelativeTo(Editor.EDITOR);
		setVisible(true);
	}

	public void createMap() {
		Game.GAME.loadLevel(Level.createLevel(input_name.getText(), Integer.valueOf(input_width.getText()), Integer.valueOf(input_height.getText())));
	}

	public void cancelCreation() {
		dispose();
	}
}
