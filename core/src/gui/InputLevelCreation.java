package gui;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import data.Level;
import engine.Editor;
import engine.Game;

public class InputLevelCreation extends JPanel {
	private Editor editor;
	private JLabel lbl_level_name;
	private JTextField input_name;
	private JLabel lbl_size;
	private JTextField input_height;
	private JTextField input_width;
	private JLabel lbl_height;

	public InputLevelCreation(Editor editor) {
		this.editor = editor;
		setLayout(null);
		lbl_level_name = new JLabel("Level Name");
		lbl_level_name.setBounds(10, 30, 72, 30);
		lbl_level_name.setHorizontalAlignment(SwingConstants.RIGHT);
		lbl_level_name.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbl_size = new JLabel("Width");
		lbl_size.setBounds(46, 60, 36, 30);
		lbl_size.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbl_size.setHorizontalAlignment(SwingConstants.RIGHT);
		lbl_height = new JLabel("Height");
		lbl_height.setBounds(40, 90, 42, 30);
		lbl_height.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_height.setAlignmentX(0.5f);
		input_width = new JTextField();
		input_width.setText("50");
		input_width.setBounds(116, 60, 75, 30);
		input_width.setToolTipText("Width");
		input_width.setMaximumSize(new Dimension(200, 50));
		input_width.setColumns(5);
		input_height = new JTextField();
		input_height.setText("50");
		input_height.setBounds(116, 90, 75, 30);
		input_height.setToolTipText("Height");
		input_height.setMaximumSize(new Dimension(200, 50));
		input_height.setColumns(5);
		input_name = new JTextField();
		input_name.setText("New_Level_Name");
		input_name.setBounds(116, 30, 200, 30);
		input_name.setColumns(15);

		add(lbl_height);
		add(input_width);
		add(input_height);
		add(input_name);
		add(lbl_level_name);
		add(lbl_size);

		setPreferredSize(new Dimension(400, 250));
		int result = JOptionPane.showConfirmDialog(editor, this, "New level...", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			Level l = Level.createLevel(input_name.getText(), Integer.valueOf(input_width.getText()), Integer.valueOf(input_height.getText()), editor);
			editor.openLevel(l);
		}
	}
}
