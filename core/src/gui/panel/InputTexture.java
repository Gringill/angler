package gui.panel;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import data.Attribute;
import engine.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InputTexture extends JPanel {
	boolean wait = true;
	private Game game;
	private String texture;

	public InputTexture(Game game) {
		final TextureView view = new TextureView();
		final JList<String> ls_tex = new JList<String>();
		final DefaultListModel<String> model_tex = new DefaultListModel<>();

		this.game = game;

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
					texture = "bin/unpacked/" + model_tex.get(index) + ".png";
					view.icon = new ImageIcon(texture);
					view.repaint();
				}
			}
		});
		for (AtlasRegion a : game.getLevel().getAtlas().getRegions()) {
			model_tex.addElement(a.name);
		}

		setLayout(new BorderLayout());
		add(ls_tex, BorderLayout.WEST);
		add(view, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);

		setPreferredSize(new Dimension(275, 400));
		setVisible(true);
	}

	public static String execute(Game game, Component parent) {
		InputTexture tp = new InputTexture(game);
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
