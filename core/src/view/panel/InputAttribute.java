package view.panel;

import controller.Game;
import model.Attribute;
import model.GameObject;
import view.stages.Editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class InputAttribute extends JPanel {
	private static ArrayList<String> exemptions = new ArrayList<String>();
	{
		exemptions.add(Attribute.ATTR_NAME);
		exemptions.add(Attribute.ATTR_TEXTURE);
		exemptions.add(Attribute.ATTR_BUILDING);
		exemptions.add(Attribute.ATTR_MOVE_SPEED);
		exemptions.add(Attribute.ATTR_COLOR);
	}
	private Game game;
	private DefaultListModel<Attribute> model_attribute;
	private JList<Attribute> ls_attribute;
	private GameObject targetEntity;
	private boolean isOpen;

	private InputAttribute(final Editor editor, GameObject e) {
		game = e.getGame();
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
								input = InputTexture.execute(editor, InputAttribute.this);
								Attribute.set(targetEntity, item.getAttribute(), item.setValue(input));
								editor.getObjectEditor().getPainter().rebuildUnitIcons();

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
		reloadEntity();
		JOptionPane.showMessageDialog(null, this, "Attribute Editor", JOptionPane.INFORMATION_MESSAGE);
	}

	public void reloadEntity() {
		model_attribute.removeAllElements();
		for (Attribute a : Attribute.buildAttributeList(targetEntity, targetEntity.getAttributes())) {
			model_attribute.addElement(a);
		}
	}

	public static void showDialog(Editor editor, GameObject e) {
		new InputAttribute(editor, e);
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
