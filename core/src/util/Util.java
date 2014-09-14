package util;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;

import engine.Game;

public class Util {

	public static final short CATEGORY_ENTITY = 0x0001;
	public static final short CATEGORY_WATER = 0x0002;
	public static final short CATEGORY_GROUND = 0x0004;
	public static final short MASK_CLIP = CATEGORY_ENTITY | CATEGORY_WATER;
	public static final short MASK_NOCLIP = 0;

	private int game_scale = 50;
	private Game game;

	public Util(Game game) {
		this.game = game;
	}

	/**
	 * http://stackoverflow.com/revisions/3449678/3
	 */
	public static String removeExtention(String filePath) {
		File f = new File(filePath);
		if (f.isDirectory())
			return filePath;

		String name = f.getName();
		final int lastPeriodPos = name.lastIndexOf('.');
		if (lastPeriodPos <= 0) {
			return filePath;
		} else {
			File renamed = new File(f.getParent(), name.substring(0, lastPeriodPos));
			return renamed.getPath();
		}
	}

	public static String pullRegionFromTexture(String texture) {
		String[] splitFile = texture.split("/");
		String file = splitFile[splitFile.length - 1];
		return removeExtention(file);
	}

	public Vector2 getMouseWorldCoords(Vector2 localMouse, boolean doFlipY) {
		OrthographicCamera camera = game.getCamera();
		Vector2 cam = new Vector2(camera.position.x, camera.position.y);

		cam.sub((new Vector2(camera.viewportWidth / 2, camera.viewportHeight / 2)));

		if (doFlipY) {
			localMouse = Util.flipY(localMouse, Gdx.graphics.getHeight());
		}

		localMouse.add(cam);
		return localMouse;
	}

	/**
	 * Takes a point that is relative to the game canvas (ranging from (0,0) to
	 * (Canvas Width, Canvas Height)) and returns where in the game world that
	 * point actually is. Essentially offsets it by the camera. Flips the Y-Axis
	 * if necessary.
	 * 
	 * @param localMouse
	 * @param doFlipY
	 * @return game world Vector2
	 */
	public Vector2 getMouseWorldCoords(Point localMouse, boolean doFlipY) {
		return getMouseWorldCoords(new Vector2(localMouse.x, localMouse.y), doFlipY);
	}

	public static float getDistanceBetweenPoints(Vector2 a, Vector2 b) {
		return (float) Math.sqrt(((b.x - a.x) * (b.x - a.x)) + ((b.y - a.y) * (b.y - a.y)));
	}

	public float getGameScale() {
		return game_scale;
	}

	public void setGameScale(int size) {
		game_scale = size;
	}

	/*
	 * Get the extension of a file.
	 */
	public static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	public static Rectangle getPleasantRectangle(Vector2 a, Vector2 b) {
		Vector2 a2 = a, b2 = b;
		if (b.x > a.x) {
			if (b.y > a.y) {
				// Your good
			} else {
				a2 = new Vector2(a.x, b.y);
				b2 = new Vector2(b.x, a.y);
			}
		} else {
			if (b.y > a.y) {
				a2 = new Vector2(b.x, a.y);
				b2 = new Vector2(a.x, b.y);
			} else {
				a2 = b;
				b2 = a;
			}
		}
		return new Rectangle(a2.x, a2.y, b2.x - a2.x, b2.y - a2.y);
	}

	/**
	 * Vector parameters remain unchanged throughout this method.
	 * 
	 * @param v1
	 * @param v2
	 * @param toDegrees
	 * @return the angle defined by the points v2, v1, (v1.y, v2.x)
	 */
	public static float getAngleTowardsPoint(Vector2 v1, Vector2 v2, boolean toDegrees) {
		v2 = v2.copy();
		v1 = v2.sub(v1);
		double angle = Math.atan2(v1.y, v1.x);
		if (toDegrees)
			return (float) Math.toDegrees(angle);
		return (float) angle;
	}

	public static Vector2 snapToWorldPoint(Vector2 worldMouse, int snap) {
		worldMouse.x = (((int) worldMouse.x / snap) * snap);
		worldMouse.y = (((int) worldMouse.y / snap) * snap);
		return worldMouse;
	}

	public Vector2 getCamBottomLeft(OrthographicCamera camera) {
		camera = game.getCamera();
		Vector2 botLeft = new Vector2(camera.position.x, camera.position.y);
		botLeft.sub((new Vector2(camera.viewportWidth / 2, camera.viewportHeight / 2)));
		return botLeft;
	}

	public static Vector2 flipY(Vector2 v, float h) {
		v.y = h - v.y;
		return v;
	}

	public static Point flipY(Point p, float h) {
		p.y = (int) (h - p.y);
		return p;
	}

	/**
	 * 
	 * @author http://stackoverflow.com/a/9386207/1543465
	 * @param currentPosition
	 * @param destinationPosition
	 * @param speed
	 * @return
	 */
	public static final Vector2 getVelocity(Vector2 currentPosition, Vector2 destinationPosition, float speed) {
		Vector2 nextPosition = new Vector2(0, 0);
		Vector2 velocityPoint = getVelocity(getAngleTowardsPoint(currentPosition, destinationPosition, true), speed);
		nextPosition.x = currentPosition.x + velocityPoint.x;
		nextPosition.y = currentPosition.y + velocityPoint.y;
		return nextPosition;
	}

	/**
	 * 
	 * @author http://stackoverflow.com/a/9386207/1543465
	 * @param angle
	 * @param speed
	 * @return
	 */
	public static final Vector2 getVelocity(float angle, float speed) {
		float x = (float) (Math.cos(Math.toRadians(angle)) * speed);
		float y = (float) (Math.sin(Math.toRadians(angle)) * speed);
		return (new Vector2(x, y));
	}

	/**
	 * 
	 * @param segmentStart
	 *            Start of line
	 * @param segmentEnd
	 *            End of line
	 * @param v
	 *            Point in question
	 * @return
	 */
	public static int getLinePointRelationship(Vector2 segmentStart, Vector2 segmentEnd, Vector2 v) {
		float value = (segmentEnd.x - segmentStart.x) * (v.y - segmentStart.y) - (segmentEnd.y - segmentStart.y) * (v.x - segmentStart.x);
		if (value < 0) { // Point is right of line
			return 1;
		} else if (value > 0) { // Point is left of line
			return -1;
		} else { // Point is on line
			return 0;
		}
	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static double getPerpDotProd(Vector2 a, Vector2 b) {
		return Math.atan2(a.x * b.y - a.y * b.x, a.x * b.x + a.y * b.y);
	}

	public static BufferedImage readBufferedImage(String file) {
		try {
			return ImageIO.read(new File(file));
		} catch (IOException exc) {
			exc.printStackTrace();
		}
		return null;
	}

	public boolean isPointInBounds(Vector2 v) {
		return v.x >= 0 && v.x < game.getGameWidth() && v.y >= 0 && v.y < game.getGameHeight();
	}
}
