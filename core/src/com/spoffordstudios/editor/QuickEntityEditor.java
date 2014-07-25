package com.spoffordstudios.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.spoffordstudios.game.Attribute;
import com.spoffordstudios.game.Entity;

public class QuickEntityEditor extends JPanel {
	private DefaultListModel<Attribute> model_attribute;
	private JList<Attribute> ls_attribute;
	private Entity targetEntity;
	private boolean isOpen;

	public QuickEntityEditor(Entity e) {
		targetEntity = e;
		ls_attribute = new JList<Attribute>();
		ls_attribute.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				@SuppressWarnings("unchecked")
				JList<Attribute> list = (JList<Attribute>) evt.getSource();
				if (evt.getClickCount() == 2) {
					int index = list.locationToIndex(evt.getPoint());
					if (index != -1) {
						String input = "";
						Attribute item = ((Attribute) model_attribute.getElementAt(index));
						if (item.isDisabled() == false) {
							if (item.getAttribute().equals(Attribute.ATTR_TEXTURE)) {
								input = TexturePicker.execute(QuickEntityEditor.this);
								Attribute.set(targetEntity, item.getAttribute(), item.setValue(input));
								Editor.EDITOR.getObjectPainter().rebuildIcons();

							} else {
								input = ((String) JOptionPane.showInputDialog(null, item.getAttribute(), "Set Value", JOptionPane.PLAIN_MESSAGE, null, null, item.getValue()));
								if (Attribute.set(targetEntity, item.getAttribute(), input)) {
									item.setValue(input);
								}
							}
						}

					}
				}
			}
		});
		model_attribute = new DefaultListModel<Attribute>();

		setLayout(new BorderLayout(0, 0));
		ls_attribute.setModel(model_attribute);
		ls_attribute.setCellRenderer(new FocusedTitleListCellRenderer());
		add(ls_attribute);
		setPreferredSize(new Dimension(275, 400));
	}

	public void reloadEntity() {
		model_attribute.removeAllElements();
		for (Attribute a : Attribute.buildAttributeList(targetEntity)) {
			model_attribute.addElement(a);
		}
	}

	public void execute(Entity e) {
		targetEntity = e;
		reloadEntity();
		isOpen = true;
		JOptionPane.showMessageDialog(Editor.EDITOR, this, "Attribute Editor", JOptionPane.INFORMATION_MESSAGE);
		isOpen = false;
	}

	public boolean isOpen() {
		return isOpen;
	}

	private class FocusedTitleListCellRenderer implements ListCellRenderer {
		protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (((Attribute) value).isDisabled()) {
				renderer.setForeground(Color.LIGHT_GRAY);
			}
			return renderer;
		}
	}
}
