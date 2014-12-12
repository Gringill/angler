package util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import engine.Game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Util {

    public static final short CATEGORY_ENTITY = 0x0001;
    public static final short CATEGORY_WATER = 0x0002;
    public static final short CATEGORY_GROUND = 0x0004;
    public static final short MASK_CLIP = CATEGORY_ENTITY | CATEGORY_WATER;
    public static final short MASK_NOCLIP = 0;

    private int tileSize = 50;
    private Game game;
    private Sprite rectSprite;

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

    @Deprecated
    public static float getDistanceBetweenPoints(Vector2 a, Vector2 b) {
        return (float) Math.sqrt(((b.x - a.x) * (b.x - a.x)) + ((b.y - a.y) * (b.y - a.y)));
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

    public static Vector2 snapToWorldPoint(Vector2 worldMouse, int snap) {
        worldMouse.x = (((int) worldMouse.x / snap) * snap);
        worldMouse.y = (((int) worldMouse.y / snap) * snap);
        return worldMouse;
    }

    public static void flipY(Vector2 v) {
        v.y = Gdx.graphics.getHeight() - v.y;
    }

    public static Point flipY(Point p, float h) {
        p.y = (int) (h - p.y);
        return p;
    }

    /**
     * @param currentPosition
     * @param destinationPosition
     * @param speed
     * @return
     * @author http://stackoverflow.com/a/9386207/1543465
     */
    public static final Vector2 getVelocity(Vector2 currentPosition, Vector2 destinationPosition, float speed) {
        Vector2 nextPosition = new Vector2(0, 0);
        Vector2 velocityPoint = getVelocity(currentPosition.getAngleTowards(destinationPosition), speed);
        nextPosition.x = currentPosition.x + velocityPoint.x;
        nextPosition.y = currentPosition.y + velocityPoint.y;
        return nextPosition;
    }

    /**
     * @param angle
     * @param speed
     * @return
     * @author http://stackoverflow.com/a/9386207/1543465
     */
    public static final Vector2 getVelocity(double angle, double speed) {
        double x = (Math.cos(angle) * speed);
        double y = (Math.sin(angle) * speed);
        return (new Vector2((float) x, (float) y));
    }

    /**
     * @param segmentStart Start of line
     * @param segmentEnd   End of line
     * @param v            Point in question
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

    public static BufferedImage readBufferedImage(String file) {
        try {
            return ImageIO.read(new File(file));
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        return null;
    }

    public Vector2 getMouseWorldCoords(Vector2 localMouse, boolean doFlipY) {
        OrthographicCamera camera = game.getCamera();
        Vector2 cam = new Vector2(camera.position.x, camera.position.y);

        cam.sub((new Vector2(camera.viewportWidth / 2, camera.viewportHeight / 2)));

        if (doFlipY) {
            Util.flipY(localMouse);
        }

        localMouse.add(cam);
//        localMouse.x *= game.getStretch().x;
//        localMouse.y *= game.getStretch().y;

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

    public float getTileSize() {
        return tileSize;
    }

    public Vector2 getCamBottomLeft() {
        OrthographicCamera camera;
        camera = game.getCamera();
        Vector2 botLeft = new Vector2(camera.position.x, camera.position.y);
        botLeft.sub((new Vector2(camera.viewportWidth / 2, camera.viewportHeight / 2)));
        return botLeft;
    }

    public void drawRect(SpriteBatch batch, int x, int y, int width, int height, int thickness) {
        batch.draw(rectSprite, x, y, width, thickness);
        batch.draw(rectSprite, x, y, thickness, height);
        batch.draw(rectSprite, x, y+height-thickness, width, thickness);
        batch.draw(rectSprite, x+width-thickness, y, thickness, height);
    }

    public void drawLine(SpriteBatch batch, int x1, int y1, int x2, int y2, int thickness) {
        int dx = x2-x1;
        int dy = y2-y1;
        float dist = (float)Math.sqrt(dx*dx + dy*dy);
        float deg = (float) Math.toDegrees(Math.atan2(dy, dx));
        rectSprite.setColor(1,0,0,1);
        batch.draw(rectSprite, x1, y1, 0, 0, dist, thickness, 1, 1, deg);
    }

    public void setRectSprite(Sprite rectSprite) {
        this.rectSprite = rectSprite;
    }

    public Sprite getRectSprite() {
        return rectSprite;
    }
}
