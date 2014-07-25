package com.spoffordstudios.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.spoffordstudios.game.Entity;

import java.awt.FlowLayout;

import javax.swing.JScrollPane;

public class EntityPainter extends JFrame implements ActionListener, ListDataListener {
	private static final String FRAME_NAME = "Entity Editor";
	private JPanel contentPane;
	private JPanel pan_tiles = new JPanel();
	private JPanel pan_units = new JPanel();
	private DefaultListModel<Entity> mod_entity;
	private JRadioButton rdbtnText = new JRadioButton("Text");
	private JRadioButton rdbtnIcons = new JRadioButton("Icons");
	private JList<Entity> ls_units;
	private Entity selectedEntity;
	private EntityButton selectedButton;
	private JPanel tab_units = new JPanel();
	private final JScrollPane scrollPane;
	private JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

	/**
	 * Create the frame.
	 */
	public EntityPainter() {
		super(FRAME_NAME);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(new Dimension(400, 300));
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		mod_entity = Editor.EDITOR.getSharedEntityModel();
		mod_entity.addListDataListener(this);

		tab_units.setLayout(new BorderLayout(0, 0));
		pan_units.setLayout(new WrapLayout());

		JList<String> ls_tiles = new JList<String>();

		JPanel tab_tiles = new JPanel();
		tab_tiles.setLayout(new BorderLayout(0, 0));
		tab_tiles.add(ls_tiles, BorderLayout.CENTER);
		tab_tiles.add(pan_tiles, BorderLayout.CENTER);

		tabbedPane.addTab("Units", null, tab_units, null);

		ls_units = new JList<Entity>();
		ls_units.setModel(mod_entity);
		ls_units.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				@SuppressWarnings("unchecked")
				JList<Entity> list = (JList<Entity>) evt.getSource();
				if (evt.getClickCount() == 1) {
					int index = list.locationToIndex(evt.getPoint());
					if (index != -1) {
						setSelectedEntity(((Entity) mod_entity.get(index)));
					}
				}
			}
		});
		scrollPane = new JScrollPane(ls_units);
		tab_units.add(scrollPane, BorderLayout.CENTER);
		tabbedPane.addTab("Tiles", null, tab_tiles, null);

		ButtonGroup group = new ButtonGroup();

		group.add(rdbtnIcons);
		group.add(rdbtnText);
		rdbtnIcons.addActionListener(this);
		rdbtnText.setSelected(true);
		rdbtnText.addActionListener(this);

		JPanel btnPanel = new JPanel();
		btnPanel.add(rdbtnIcons);
		btnPanel.add(rdbtnText);

		contentPane.add(tabbedPane, BorderLayout.CENTER);
		contentPane.add(btnPanel, BorderLayout.NORTH);
		setAlwaysOnTop(true);
		setLocationRelativeTo(Editor.EDITOR);
		setVisible(true);
	}

	public void rebuildIcons() {
		Entity e;
		JButton btn;
		pan_units.removeAll();

		for (int i = 0; i < mod_entity.size(); i++) {
			e = mod_entity.get(i);
			BufferedImage img = null;
			try {
				img = ImageIO.read(new File(e.getTexture()));
				btn = new EntityButton(e, new ImageIcon(img.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
				btn.setActionCommand(e.getTexture());
				btn.addActionListener(this);
				btn.setBorder(BorderFactory.createRaisedBevelBorder());
				pan_units.add(btn);
			} catch (IOException exc) {
				exc.printStackTrace();
			}
		}
		pan_units.revalidate();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == rdbtnIcons || e.getSource() == rdbtnText) {
			if (rdbtnText.isSelected()) {
				tab_units.add(scrollPane, BorderLayout.CENTER);
				tab_units.remove(pan_units);

			} else {
				tab_units.add(pan_units, BorderLayout.CENTER);
				tab_units.remove(scrollPane);

			}
			tab_units.revalidate();
			tab_units.repaint();
		} else if (e.getSource() instanceof EntityButton) {
			if (selectedButton != null) {
				selectedButton.setBorder(BorderFactory.createRaisedBevelBorder());
			}
			selectedButton = ((EntityButton) e.getSource());
			selectedButton.setBorder(BorderFactory.createLineBorder(Color.red));
			setSelectedEntity(selectedButton.getEntity());
		}
	}

	private void setSelectedEntity(Entity e) {
		selectedEntity = e;
		Editor.EDITOR.getEntityCursor().defineAs(selectedEntity);
		Editor.EDITOR.setSubText("Place entity [" + selectedEntity.getName() + "]");
	}

	@Override
	public void dispose() {
		mod_entity.removeListDataListener(this);
		super.dispose();
	}

	@Override
	public void intervalAdded(ListDataEvent e) {
		rebuildIcons();
	}

	@Override
	public void intervalRemoved(ListDataEvent e) {
		rebuildIcons();
	}

	@Override
	public void contentsChanged(ListDataEvent e) {
		rebuildIcons();
	}

	private class EntityButton extends JButton {
		private Entity e;

		public EntityButton(Entity e, ImageIcon icon) {
			super(icon);
			this.e = e;
		}

		public Entity getEntity() {
			return e;
		}

		@Override
		public String toString() {
			return e.getName();
		}
	}
}