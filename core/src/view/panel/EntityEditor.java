package view.panel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import model.Entity;
import model.Tile;
import model.util.Logger;
import model.util.Util;
import view.stages.Editor;
import model.Attribute;
import model.GameObject;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EntityEditor extends JFrame implements ActionListener {
	private static final String FRAME_NAME = "Entity Editor";
	private static final String ACTION_DELETE = "delete";
	private static final String ACTION_NEW = "new";
	private Editor editor;
	private JList<GameObject> ls_entity = new JList<GameObject>();
	private JList<Attribute> ls_attribute;

	private DefaultListModel<Attribute> modelAttribute;

	private GameObject selectedObject;
	private EntityPainter painter;
	private JPanel entity_tab = new JPanel();
	private JRadioButton rb_entities = new JRadioButton("Entities");
	private JRadioButton rb_tiles = new JRadioButton("Tiles");

	public EntityEditor(Editor editor) {
		super(FRAME_NAME);
		this.editor = editor;
		painter = new EntityPainter(editor, editor.getLevel().getEntityModel(), editor.getLevel().getTileModel());
		defineEntityTab();

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(380, 340);
		setLocationRelativeTo(null);
		setContentPane(entity_tab);
	}

	private void defineEntityTab() {
		JButton btn_new = new JButton("New");
		btn_new.setActionCommand(ACTION_NEW);
		btn_new.addActionListener(this);

		JButton btn_delete = new JButton("Del");
		btn_delete.setActionCommand(ACTION_DELETE);
		btn_delete.addActionListener(this);

		rb_entities.addActionListener(this);
		rb_tiles.addActionListener(this);
		rb_entities.setSelected(true);

		ButtonGroup group = new ButtonGroup();
		group.add(rb_entities);
		group.add(rb_tiles);

		JPanel pan_buttons = new JPanel();
		pan_buttons.setLayout(new BoxLayout(pan_buttons, BoxLayout.X_AXIS));
		pan_buttons.add(btn_new);
		pan_buttons.add(btn_delete);
		pan_buttons.add(rb_entities);
		pan_buttons.add(rb_tiles);

		modelAttribute = new DefaultListModel<Attribute>();
		ls_entity.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				@SuppressWarnings("unchecked")
				JList<GameObject> list = (JList<GameObject>) evt.getSource();
				if (evt.getClickCount() == 1) {
					int index = list.locationToIndex(evt.getPoint());
					if (index != -1) {
						GameObject e = ((DefaultListModel<GameObject>) ls_entity.getModel()).get(index);
						refreshAttributes(e);
					}
				}
			}
		});
		entity_tab.setLayout(new BorderLayout());
		ls_entity.setModel(editor.getLevel().getEntityModel());
		JScrollPane sp_entity = new JScrollPane(ls_entity);
		sp_entity.setPreferredSize(new Dimension(125, 10));

		JPanel pan_select = new JPanel();
		pan_select.setLayout(new BorderLayout());
		pan_select.setPreferredSize(new Dimension(150, 10));
		pan_select.add(sp_entity, BorderLayout.CENTER);

		ls_attribute = new JList<Attribute>();
		ls_attribute.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				@SuppressWarnings("unchecked")
				JList<Attribute> list = (JList<Attribute>) evt.getSource();
				if (evt.getClickCount() == 2) {
					int index = list.locationToIndex(evt.getPoint());
					if (index != -1) {
						String input = "";
						Attribute item = ((Attribute) modelAttribute.getElementAt(index));
						if (item.getAttribute().equals(Attribute.ATTR_TEXTURE)) {
							input = InputTexture.execute(editor, EntityEditor.this);
							Attribute.set(selectedObject, item.getAttribute(), (item.setValue(input)));
							for (GameObject e : editor.getLevel().getEntities()) {
								if (e.getName().equals(selectedObject.getName())) {
									Attribute.set(e, item.getAttribute(), (item.setValue(input)));
								}
							}
							if (editor.getObjectEditor().getPainter() != null) {
								editor.getObjectEditor().getPainter().rebuildUnitIcons();
							}
						} else if (item.getAttribute().equals(Attribute.ATTR_PATHABILITY)) {
							input = InputPathability.execute(EntityEditor.this);
							Attribute.set(selectedObject, item.getAttribute(), (item.setValue(input)));
							for (GameObject e : editor.getLevel().getEntities()) {
								if (e.getName().equals(selectedObject.getName())) {
									Attribute.set(e, item.getAttribute(), (item.setValue(input)));
								}
							}
							if (editor.getObjectEditor().getPainter() != null) {
								editor.getObjectEditor().getPainter().rebuildUnitIcons();
							}
						} else {
							input = ((String) JOptionPane.showInputDialog(null, item.getAttribute(), "Set Value", JOptionPane.QUESTION_MESSAGE, null, null, item.getValue()));
							Attribute.set(selectedObject, item.getAttribute(), (item.setValue(input)));
						}
					}
				}
			}
		});
		ls_attribute.setModel(modelAttribute);

		JPanel pan_attributes = new JPanel();
		pan_attributes.setLayout(new BorderLayout());
		pan_attributes.add(ls_attribute);

		entity_tab.add(pan_buttons, BorderLayout.NORTH);
		entity_tab.add(pan_select, BorderLayout.WEST);
		entity_tab.add(pan_attributes, BorderLayout.CENTER);
	}

	public void deleteSelectedEntity() {
	}

	private String getValidName(String name, int index) {
		if (isNameUnique(name + index)) {
			return name + index;
		}
		return getValidName(name, ++index);
	}

	public void newEntity(String name, Sprite sprite) {
		if (name == null || name.length() == 0) {
			name = "Entity";
		}
		if (!isNameUnique(name)) {
			name = getValidName(name, 0);
		}
		editor.getLevel().getEntityModel().addElement(new Entity(editor.getGame(), name, sprite, 0, 0));
	}

	public void newTile(String name, String texture, int pathability, Color color) {
		Tile newTile;
		Sprite sprite = null;
		if (name == null || name.length() == 0) {
			name = "Tile";
			Logger.log("Attempted to create tile with invalid name", "EntityEditor", false);
		}
		if (texture == null || texture.length() == 0) {
			sprite = new Sprite(editor.getLevel().getAtlas().findRegion(Util.removeExtention(Tile.DEFAULT_TEXTURE)));
			texture = Tile.PATH_DEFAULT + Tile.DEFAULT_TEXTURE;
		} else {
			sprite = new Sprite(editor.getLevel().getAtlas().findRegion(Util.removeExtention(texture)));
			texture = Tile.PATH_DEFAULT + texture;
		}
		if (!isNameUnique(name)) {
			name = getValidName(name, 0);
		}
		newTile = new Tile(editor.getGame(), name, sprite, 0, 0);
		Attribute.set(newTile, Attribute.ATTR_TEXTURE, texture);
		Attribute.set(newTile, Attribute.ATTR_PATHABILITY, String.valueOf(pathability));
		Attribute.set(newTile, Attribute.ATTR_COLOR, color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ", 255");
		editor.getLevel().getTileModel().addElement(newTile);
	}

	private boolean isNameUnique(String name) {
		if (isTileMode()) {
			for (int i = 0; i < editor.getLevel().getTileModel().size(); i++) {
				String c = editor.getLevel().getTileModel().getElementAt(i).toString();
				if (c.toString().equals(name)) {
					return false;
				}
			}
		} else {
			for (int i = 0; i < editor.getLevel().getEntityModel().size(); i++) {
				String c = editor.getLevel().getEntityModel().getElementAt(i).toString();
				if (c.toString().equals(name)) {
					return false;
				}
			}
		}
		return true;
	}

	public void refreshAttributes(GameObject o) {
		selectedObject = o;
		modelAttribute.clear();
		if (isTileMode()) {
			for (Attribute a : Attribute.buildAttributeList(o, o.getAttributes())) {
				modelAttribute.addElement(a);
			}
		} else {
			for (Attribute a : Attribute.buildAttributeList(o, o.getAttributes())) {
				modelAttribute.addElement(a);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case ACTION_DELETE:
			deleteSelectedEntity();
			break;
		case ACTION_NEW:
			if (isTileMode()) {
				newTile(null, null, 0, new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
			} else {
				newEntity(null, null);
			}
			break;
		default:
			if (e.getSource() == rb_entities || e.getSource() == rb_tiles) {
				if (isTileMode()) {
					ls_entity.setModel(editor.getLevel().getTileModel());
				} else {
					ls_entity.setModel(editor.getLevel().getEntityModel());
				}
				entity_tab.revalidate();
				entity_tab.repaint();
			}
			break;
		}
	}

	public boolean isTileMode() {
		return !rb_entities.isSelected();
	}

	public EntityPainter getPainter() {
		return painter;
	}

	public void openEntityPainter() {
		painter.setVisible(true);
	}

}
