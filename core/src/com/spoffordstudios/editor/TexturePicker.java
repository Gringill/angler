package com.spoffordstudios.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.spoffordstudios.game.Attribute;
import com.spoffordstudios.game.Game;

public class TexturePicker extends JPanel {
	String texture;
	boolean wait = true;

	public TexturePicker() {
		final TextureView view = new TextureView();
		final JList<String> ls_tex = new JList<String>();
		final DefaultListModel<String> model_tex = new DefaultListModel<>();

		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String a = e.getActionCommand();

				switch (a) {
				case "ok":
					wait = false;
					break;
				case "cancel":
					wait = false;
					break;

				default:
					break;
				}
			}
		};
		ls_tex.setModel(model_tex);
		ls_tex.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				@SuppressWarnings("unchecked")
				JList<Attribute> list = (JList<Attribute>) evt.getSource();
				int index = list.locationToIndex(evt.getPoint());
				if (evt.getClickCount() == 1) {
					texture = "bin/" + model_tex.get(index) + ".png";
					view.icon = new ImageIcon(texture);
					view.repaint();
				}
			}
		});
		for (AtlasRegion a : Game.getLevel().getAtlas().getRegions()) {
			model_tex.addElement(a.name);
		}

		setLayout(new BorderLayout());
		add(ls_tex, BorderLayout.WEST);
		add(view, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);

		setPreferredSize(new Dimension(600, 500));
		setVisible(true);
	}

	public static String execute(Component parent) {
		TexturePicker tp = new TexturePicker();
		int result = JOptionPane.showConfirmDialog(parent, tp, "Texture Picker", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
		if (result == JOptionPane.OK_OPTION) {
			return tp.texture;
		} else {
			return "";
		}
	}

	private static class TextureView extends JPanel {
		ImageIcon icon = new ImageIcon();
		String status = "continue";

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Image img = icon.getImage();
			if (img != null) {
				Rectangle innerArea = new Rectangle();
				SwingUtilities.calculateInnerArea(this, innerArea);

				float ratio;
				if (innerArea.width >= innerArea.height) {
					ratio = img.getHeight(null) / img.getWidth(null);
					g.drawImage(img, innerArea.x, innerArea.y, (int) (innerArea.height * ratio), innerArea.height, null);
				} else {
					ratio = img.getWidth(null) / img.getHeight(null);
					g.drawImage(img, innerArea.x, innerArea.y, innerArea.width, (int) (innerArea.width * ratio), null);
				}

			}
		}
	}
}
