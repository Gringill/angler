package com.spoffordstudios.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.spoffordstudios.game.Attribute;
import com.spoffordstudios.game.Entity;

public class ObjectEditor extends JFrame implements ActionListener {
	private static final String OBBG_NAME = "Object Editor";
	private static final String ACTION_DELETE = "delete";
	private static final String ACTION_NEW = "new";
	private JList<Entity> ls_entity = new JList<Entity>();
	private DefaultListModel<Attribute> model_attribute;
	private JList<Attribute> ls_attribute;
	private Entity selectedEntity;
	private DefaultListModel<Entity> mod_entity;

	public ObjectEditor() {
		super(OBBG_NAME);
		JTabbedPane tabpane = new JTabbedPane();

		JPanel entity_tab = new JPanel();
		entity_tab.setLayout(new BoxLayout(entity_tab, BoxLayout.X_AXIS));

		tabpane.addTab("Units", new ImageIcon("assets/unpacked/icon_units.png"), entity_tab);

		JPanel content = new JPanel();
		content.setLayout(new BorderLayout(0, 0));
		entity_tab.add(content);

		JPanel west = new JPanel();
		content.add(west, BorderLayout.WEST);
		west.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane(ls_entity);
		west.add(scrollPane, BorderLayout.CENTER);
		mod_entity = Editor.EDITOR.getSharedEntityModel();
		ls_entity.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				@SuppressWarnings("unchecked")
				JList<Entity> list = (JList<Entity>) evt.getSource();
				if (evt.getClickCount() == 1) {
					int index = list.locationToIndex(evt.getPoint());
					Entity e = ((Entity) mod_entity.get(index));
					refreshAttributes(e);
				}
			}
		});
		ls_entity.setModel(mod_entity);
		scrollPane.setPreferredSize(new Dimension(125, 0));

		JPanel panel = new JPanel();
		west.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		JButton btn_new_entity = new JButton("New");
		btn_new_entity.setActionCommand(ACTION_NEW);
		btn_new_entity.addActionListener(this);
		panel.add(btn_new_entity);

		JButton btnB = new JButton("Del");
		btnB.setActionCommand(ACTION_DELETE);
		btnB.addActionListener(this);
		panel.add(btnB);

		JPanel center = new JPanel();
		content.add(center, BorderLayout.CENTER);
		center.setLayout(new BorderLayout(0, 0));

		ls_attribute = new JList<Attribute>();
		ls_attribute.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				@SuppressWarnings("unchecked")
				JList<Attribute> list = (JList<Attribute>) evt.getSource();
				if (evt.getClickCount() == 2) {
					int index = list.locationToIndex(evt.getPoint());
					String input = "";
					Attribute item = ((Attribute) model_attribute.getElementAt(index));
					if (item.getAttribute().equals(Attribute.ATTR_TEXTURE)) {
						input = TexturePicker.execute(ObjectEditor.this);
						Attribute.set(selectedEntity, item.getAttribute(), (item.setValue(input)));
					} else {
						input = ((String) JOptionPane.showInputDialog(null, item.getAttribute(), "Set Value", JOptionPane.QUESTION_MESSAGE, null, null, item.getValue()));
						Attribute.set(selectedEntity, item.getAttribute(), (item.setValue(input)));
					}
				}
			}
		});
		model_attribute = new DefaultListModel<Attribute>();
		ls_attribute.setModel(model_attribute);
		center.add(ls_attribute);

		JPanel ability_tab = new JPanel();
		tabpane.addTab("Abilities", new ImageIcon("assets/unpacked/icon_abilities.png"), ability_tab);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(0, 0, 760, 340);
		setContentPane(tabpane);
		setVisible(true);
	}

	public void deleteSelectedEntity() {

	}

	private String getValidName(String name, int index) {
		for (int i = 0; i < mod_entity.size(); i++) {
			String c = mod_entity.getElementAt(i).toString();
			if (c.toString().equals(name + String.valueOf(index))) {
				return getValidName(name, index + 1);
			}
		}
		return name + index;
	}

	public void newEntity() {
		String name = "NewEntity";
		name = getValidName(name, 0);
		mod_entity.addElement(new Entity(name, null, 0, 0));
	}

	public void refreshAttributes(Entity e) {
		selectedEntity = e;
		model_attribute.clear();
		for (Attribute a : Attribute.build(e)) {
			model_attribute.addElement(a);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case ACTION_DELETE:
			deleteSelectedEntity();
			break;
		case ACTION_NEW:
			newEntity();
			break;
		default:
			break;
		}
	}
}