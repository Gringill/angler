package gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import data.GameObject;
import data.Tile;
import engine.Editor;
import gui.WrapLayout;

public class EntityPainter extends JFrame implements ListDataListener {
	private static final String FRAME_NAME = "Entity Editor";
	public static final String MODE_ENTITY = "Entity Mode";
	private Editor editor;
	private JPanel contentPane;
	private JPanel pan_tiles = new JPanel();
	private JPanel pan_units = new JPanel();
	private DefaultListModel<GameObject> mod_entity;
	private DefaultListModel<GameObject> mod_tile;
	private JRadioButton rdbtnText = new JRadioButton("Text");
	private JRadioButton rdbtnIcons = new JRadioButton("Icons");
	private JList<GameObject> ls_units;
	private JList<GameObject> ls_tiles;
	private GameObject selectedEntity;
	private EntityButton selectedButton;
	private JPanel tab_units = new JPanel();
	private JPanel tab_tiles = new JPanel();
	private final JScrollPane sp_entities;
	private final JScrollPane sp_tiles;
	private JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	private ActionListener unitActionListener;
	private ActionListener tileActionListener;
	private Tile selectedTile;

	/**
	 * Create the frame.
	 */
	public EntityPainter(final Editor editor, DefaultListModel<GameObject> model_entity, DefaultListModel<GameObject> model_tile) {
		super(FRAME_NAME);
		this.editor = editor;
		setTitle("Painter");
		mod_tile = model_tile;
		mod_entity = model_entity;
		defineListeners();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(new Dimension(400, 300));
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		mod_entity.addListDataListener(this);
		mod_tile.addListDataListener(this);

		tab_units.setLayout(new BorderLayout(0, 0));
		pan_units.setLayout(new WrapLayout());

		tab_tiles.setLayout(new BorderLayout(0, 0));
		pan_tiles.setLayout(new WrapLayout());

		tabbedPane.addTab("Units", null, tab_units, null);
		tabbedPane.addTab("Tiles", null, tab_tiles, null);

		ls_units = new JList<GameObject>();
		ls_units.setModel(mod_entity);
		ls_units.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				@SuppressWarnings("unchecked")
				JList<GameObject> list = (JList<GameObject>) evt.getSource();
				if (evt.getClickCount() == 1) {
					int index = list.locationToIndex(evt.getPoint());
					if (index != -1) {
						selectedTile = null;
						selectedEntity = ((GameObject) mod_entity.get(index));
						editor.getEntityCursor().defineAs(selectedEntity);
					}
				}
			}
		});
		ls_tiles = new JList<GameObject>();
		ls_tiles.setModel(mod_tile);
		ls_tiles.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				@SuppressWarnings("unchecked")
				JList<GameObject> list = (JList<GameObject>) evt.getSource();
				if (evt.getClickCount() == 1) {
					int index = list.locationToIndex(evt.getPoint());
					if (index != -1) {
						selectedEntity = null;

						selectedTile = ((Tile) mod_tile.get(index));
						if (editor.getObjectEditor().getPainter().isTileMode()) {
							GameObject e = editor.getEntityCursor();
							e.setBuilding(true);
							e.setSprite(null);
							e.setWidth(editor.getGame().getUtil().getTileSize());
							e.setHeight(editor.getGame().getUtil().getTileSize());
						}
					}
				}
			}
		});

		sp_entities = new JScrollPane(ls_units);
		tab_units.add(sp_entities, BorderLayout.CENTER);

		sp_tiles = new JScrollPane(ls_tiles);
		tab_tiles.add(sp_tiles, BorderLayout.CENTER);

		ButtonGroup group = new ButtonGroup();

		group.add(rdbtnIcons);
		group.add(rdbtnText);
		rdbtnIcons.addActionListener(unitActionListener);
		rdbtnText.setSelected(true);
		rdbtnText.addActionListener(unitActionListener);

		JPanel btnPanel = new JPanel();
		btnPanel.add(rdbtnIcons);
		btnPanel.add(rdbtnText);

		contentPane.add(tabbedPane, BorderLayout.CENTER);
		contentPane.add(btnPanel, BorderLayout.NORTH);
		setLocationRelativeTo(editor);
	}

	public void rebuildUnitIcons() {
		GameObject e;
		JButton btn;
		pan_units.removeAll();

		for (int i = 0; i < mod_entity.size(); i++) {
			e = mod_entity.get(i);
			BufferedImage img = null;
			try {
				img = ImageIO.read(new File(e.getTexture()));
				btn = new EntityButton(e, new ImageIcon(img.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
				btn.setActionCommand(e.getTexture());
				btn.addActionListener(unitActionListener);
				btn.setBorder(BorderFactory.createRaisedBevelBorder());
				pan_units.add(btn);
			} catch (IOException exc) {
				exc.printStackTrace();
			}
		}
		pan_units.revalidate();
	}

	public void rebuildTileIcons() {
		Tile t;
		JButton btn;
		pan_tiles.removeAll();

		for (int i = 0; i < mod_tile.size(); i++) {
			t = (Tile) mod_tile.get(i);
			BufferedImage img = null;
			try {
				if (editor.getGame().getLevel().atlas.findRegion(t.getName()) != null) {
					img = ImageIO.read(new File("core/assets/unpacked/" + t.getName() + ".png"));
				} else {
					img = ImageIO.read(new File("core/assets/unpacked/ic_error.png"));
				}
				btn = new EntityButton(t, new ImageIcon(img.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
				btn.setActionCommand("core/assets/unpacked/" + t.getName() + ".png");
				btn.addActionListener(tileActionListener);
				btn.setBorder(BorderFactory.createRaisedBevelBorder());
				pan_tiles.add(btn);
			} catch (IOException exc) {
				exc.printStackTrace();
			}
		}
		pan_tiles.revalidate();
	}

	private void selectEntity(ListDataEvent e) {
		if (e.getSource() == mod_entity) {
			rebuildUnitIcons();
		} else {
			rebuildTileIcons();
		}
	}

	@Override
	public void intervalAdded(ListDataEvent e) {
		selectEntity(e);
	}

	@Override
	public void intervalRemoved(ListDataEvent e) {
		selectEntity(e);
	}

	@Override
	public void contentsChanged(ListDataEvent e) {
		selectEntity(e);
	}

	private void defineListeners() {
		unitActionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == rdbtnIcons || e.getSource() == rdbtnText) {
					if (rdbtnText.isSelected()) {
						tab_units.add(sp_entities, BorderLayout.CENTER);
						tab_units.remove(pan_units);

						tab_tiles.add(sp_tiles, BorderLayout.CENTER);
						tab_tiles.remove(pan_tiles);
					} else {
						tab_units.add(pan_units, BorderLayout.CENTER);
						tab_units.remove(sp_entities);

						tab_tiles.add(pan_tiles, BorderLayout.CENTER);
						tab_tiles.remove(sp_tiles);
					}
					tabbedPane.revalidate();
					tabbedPane.repaint();
				} else if (e.getSource() instanceof EntityButton) {
					if (selectedButton != null) {
						selectedButton.setBorder(BorderFactory.createRaisedBevelBorder());
					}
					selectedButton = ((EntityButton) e.getSource());
					selectedButton.setBorder(BorderFactory.createLineBorder(Color.red));
					selectedEntity = selectedButton.getEntity();
					editor.getEntityCursor().defineAs(selectedEntity);
				}
			}
		};
		tileActionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		};
	}

	public Tile getSelectedTile() {
		return selectedTile;
	}

	private class EntityButton extends JButton {
		private GameObject e;

		public EntityButton(GameObject e, ImageIcon icon) {
			super(icon);
			this.e = e;
		}

		public GameObject getEntity() {
			return e;
		}

		@Override
		public String toString() {
			return e.getName();
		}
	}

	public boolean isTileMode() {
		return tab_tiles.isVisible();
	}
}