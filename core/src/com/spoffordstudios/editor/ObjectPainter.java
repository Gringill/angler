package com.spoffordstudios.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.spoffordstudios.game.Entity;
import javax.swing.BoxLayout;

public class ObjectPainter extends JFrame implements ActionListener, ListDataListener {
	private JPanel contentPane;
	private JPanel pan_tiles = new JPanel();
	private DefaultListModel<Entity> mod_entity;
	private JRadioButton rdbtnText = new JRadioButton("Text");
	private JRadioButton rdbtnIcons = new JRadioButton("Icons");
	private JList<Entity> ls_units = new JList<Entity>();

	/**
	 * Create the frame.
	 */
	public ObjectPainter() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);

		JPanel tab_units = new JPanel();
		tabbedPane.addTab("Units", null, tab_units, null);

		mod_entity = Editor.EDITOR.getSharedEntityModel();
		mod_entity.addListDataListener(this);
		tab_units.setLayout(new BoxLayout(tab_units, BoxLayout.X_AXIS));
		ls_units.setModel(mod_entity);
		tab_units.add(ls_units);

		JPanel pan_units = new JPanel();
		tab_units.add(pan_units);

		JPanel tab_tiles = new JPanel();
		tabbedPane.addTab("Tiles", null, tab_tiles, null);
		JList<String> ls_tiles = new JList<String>();
		tab_tiles.add(ls_tiles);
		pan_tiles.add(new JLabel("test"));
		tab_tiles.add(pan_tiles);

		JPanel btnPanel = new JPanel();
		ButtonGroup group = new ButtonGroup();

		rdbtnText.setSelected(true);

		group.add(rdbtnIcons);
		group.add(rdbtnText);
		rdbtnIcons.addActionListener(this);
		rdbtnText.addActionListener(this);
		btnPanel.add(rdbtnIcons);
		btnPanel.add(rdbtnText);
		contentPane.add(btnPanel, BorderLayout.NORTH);
		setLocationRelativeTo(Editor.EDITOR);
		setVisible(true);
	}

	public void rebuildIcons() {
		Entity e;
		pan_tiles.removeAll();
		for (int i = 0; i < mod_entity.size(); i++) {
			e = mod_entity.get(i);
			pan_tiles.add(new JLabel(new ImageIcon(e.getTexture())));
		}
		pan_tiles.add(new JLabel("test2"));
		revalidate();
		System.out.println("rebuild");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == rdbtnIcons || e.getSource() == rdbtnText) {
			if (rdbtnText.isSelected()) {
				pan_tiles.setVisible(false);
				ls_units.setVisible(true);
			} else {
				pan_tiles.setVisible(true);
				ls_units.setVisible(false);
			}
		}
	}

	@Override
	public void dispose() {
		mod_entity.removeListDataListener(this);
		super.dispose();
	}

	@Override
	public void intervalAdded(ListDataEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void intervalRemoved(ListDataEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void contentsChanged(ListDataEvent e) {
		rebuildIcons();
	}
}