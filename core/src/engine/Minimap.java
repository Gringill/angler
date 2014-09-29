package engine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.Timer;

import util.Util;
import util.Vector2;

import com.badlogic.gdx.graphics.OrthographicCamera;

import data.GameObject;
import data.Tile;

public class Minimap extends JPanel implements MouseMotionListener {
	Game game;
	private ArrayList<GameObject> entities = new ArrayList<GameObject>();
	private Dimension actualSize = new Dimension();

	public Minimap(Game game) {
		this.game = game;
		setPreferredSize(new Dimension(300, 300));
		addMouseMotionListener(this);
		AbstractAction a = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		};
		new Timer(1, a).start();
	}

	@Override
	public void paint(Graphics g) {
		Dimension size = getSize();
		double w, h, x, y;
		double xScale = (size.width / actualSize.getWidth());
		double yScale = (size.height / actualSize.getHeight());

		if (game.getLevel() != null) {
			g.setColor(Color.black);
			g.fillRect(0, 0, (int) size.getWidth(), (int) size.getHeight());
			// Draw tiles to minimap
			for (Tile[] ta : game.getLevel().getTileMap().getTiles()) {
				for (Tile t : ta) {
					g.setColor(new Color(t.getColor().toIntBits()));
					w = (game.getUtil().getGameScale() * xScale);
					h = (game.getUtil().getGameScale() * xScale);
					x = (t.getX() * xScale);
					y = (size.height - (t.getY() * yScale + h)) + 1;
					g.fillRect((int) x, (int) y, (int) w, (int) h);
				}
			}

			// Draw entities to minimap
			g.setColor(Color.RED);
			for (GameObject e : entities) {
				w = (e.getSprite().getWidth() * xScale);
				h = (e.getSprite().getHeight() * yScale);
				x = (e.getX() * xScale) - w / 2;
				y = ((size.height - e.getY() * yScale) - h / 2) + 1;
				g.fillOval((int) x, (int) y, (int) w, (int) h);
			}

			// Draw camera to minimap
			g.setColor(Color.YELLOW);
			OrthographicCamera camera = game.getCamera();
			Vector2 botLeft = Util.flipY(game.getUtil().getCamBottomLeft(camera));
			w = (camera.viewportWidth * xScale) + 1;
			h = (camera.viewportHeight * yScale) + 1;
			x = (botLeft.x * xScale);
			y = (botLeft.y * yScale) - h + 1;
			g.drawRect((int) x, (int) y, (int) w, (int) h);
		}
	}

	public void registerEntity(GameObject e) {
		entities.add(e);
	}

	public void unregisterEntity(GameObject e) {
		entities.remove(e);
	}

	public void updateSize() {
		this.actualSize.width = (int) (game.getLevel().getTileMap().getSize().getWidth() * 50) - 1;
		this.actualSize.height = (int) (game.getLevel().getTileMap().getSize().getHeight() * 50) - 1;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Dimension size = getSize();
		float xScale = (float) (actualSize.getWidth() / size.width);
		float yScale = (float) (actualSize.getHeight() / size.height);
		Point worldClick = e.getPoint();
		Util.flipY(worldClick, getHeight());
		worldClick.x = (int) (worldClick.x * (xScale));
		worldClick.y = (int) (worldClick.y * (yScale));
		game.getCamera().position.set(worldClick.x, worldClick.y, 0);
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}
}
