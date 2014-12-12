package engine;

import data.Node;
import util.Logger;
import util.PathFinder;
import util.Vector2;

import java.util.ArrayList;

public class NodeMap {
    public static final int PATH_GROUND = 0;
    public static final int PATH_SWIMMING = 1;
    public static final int PATH_FLYING = 2;
    public static final Vector2 CORNER_PATHABLE = new Vector2(0, 0);
    private static final int[] xOffsets = {0, 1, 0, -1, 1, 1, -1, -1};
    private static final int[] yOffsets = {1, 0, -1, 0, 1, -1, -1, 1};
    private int NEIGHBOR_MODE = 8;
    private float density = 2;
    private Node[][] nodes;

    public NodeMap(TileMap tilemap) {
        generate(tilemap);
    }

    public boolean lineOfSight(int pathability, Vector2 a, Vector2 b) {
        Logger.log("Begin LOS: ", "los", false);
        int x1 = (int) (a.x);
        int y1 = (int) (a.y);
        int x2 = (int) (b.x);
        int y2 = (int) (b.y);
        int i; // loop counter
        int xstep, ystep; // the step on y and x axis
        int error; // the error accumulated during the increment
        int errorprev; // *vision the previous value of the error variable
        int x = x1, y = y1; // the line points
        int ddx, ddy; // compulsory variables: the double values of dy and dx
        int dx = x2 - x1;
        int dy = y2 - y1;
        // POINT(y1, x1); // first point
        if (!isPathableAtNodePoint(pathability, x1, y1)) {
            return false;
        }
        // NB the last point can't be here, because of its previous point (which
        // has to be verified)
        if (dx < 0) {
            xstep = -1;
            dx = -dx;
        } else {
            xstep = 1;
        }
        if (dy < 0) {
            ystep = -1;
            dy = -dy;
        } else {
            ystep = 1;
        }
        ddx = 2 * dx;
        ddy = 2 * dy;
        if (ddy >= ddx) { // first octant (0 <= slope <= 1)
            // compulsory initialization (even for errorprev, needed when
            // dx==dy)
            errorprev = error = dy; // start in the middle of the square
            for (i = 0; i < dy; i++) { // do not use the first point (already
                // done)
                y += ystep;
                error += ddx;
                if (error > ddy) { // increment y if AFTER the middle ( > )
                    x += xstep;
                    error -= ddy;
                    // three cases (octant == right->right-top for directions
                    // below):
                    if (error + errorprev < ddy) {// bottom square also
                        if (!isPathableAtNodePoint(pathability, x - xstep, y)) {
                            return false;
                        }
                    } else if (error + errorprev > ddy) {// left square also
                        if (!isPathableAtNodePoint(pathability, x, y - ystep)) {
                            return false;
                        }
                    } else { // corner: bottom and left squares also
                        if (!isPathableAtNodePoint(pathability, y - ystep, x) || !isPathableAtNodePoint(pathability, y, x - xstep)) {
                            return false;
                        }
                    }
                }
                if (!isPathableAtNodePoint(pathability, x, y)) {
                    return false;
                }
                errorprev = error;
            }
        } else { // the same as above
            Logger.log("Else", "los", false);
            errorprev = error = dx;
            for (i = 0; i < dx; i++) {
                x += xstep;
                error += ddy;
                if (error > ddx) {
                    y += ystep;
                    error -= ddx;
                    if (error + errorprev < ddx)
                        Logger.log("Check 1", "los", false);
                    if (!isPathableAtNodePoint(pathability, x, y - ystep)) {
                        Logger.log("a", "los", false);
                        return false;
                    } else if (error + errorprev > ddx) {
                        if (!isPathableAtNodePoint(pathability, x - xstep, y)) {
                            Logger.log("b", "los", false);
                            return false;
                        }
                    } else {
                        if (!isPathableAtNodePoint(pathability, y, x - xstep) || !isPathableAtNodePoint(pathability, y - ystep, x)) {
                            Logger.log("c", "los", false);
                            return false;
                        }
                    }
                }
                Logger.log("Check 2", "los", false);
                if (!isPathableAtNodePoint(pathability, x, y)) {
                    Logger.log("a", "los", false);
                    return false;
                }
                errorprev = error;
            }
        }
        // the last point (y2,x2) has to be the same with the last point of the
        // algorithm
        assert ((y == y2) && (x == x2));
        return true;
    }

    public void setupNeighbors(float x, float y) {
        x /= density;
        y /= density;
        Node n = get((int) x, (int) y);
        ArrayList<Node> neighbors = new ArrayList<>();
        ArrayList<Node> corners = new ArrayList<>();
        Vector2 v;
        for (int i = 4; i < 8; i++) {
            v = new Vector2(n.getPosition().x + xOffsets[i], n.getPosition().y + yOffsets[i]).snapToWorldPoint(1);
            if (pointInBounds(v)) {
                corners.add(get((int) v.x, (int) v.y));
            }
        }
        for (int i = 0; i < NEIGHBOR_MODE; i++) {
            v = new Vector2(n.getPosition().x + xOffsets[i], n.getPosition().y + yOffsets[i]).snapToWorldPoint(1);
            if (pointInBounds(v)) {
                neighbors.add(get((int) v.x, (int) v.y));
            }
        }
        n.setNeighbors(neighbors);
        n.setCorners(corners);
    }

    public boolean nodeInBounds(float x, float y) {
        boolean b = x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
        // Logger.log("pointInBounds(" + x + ", " + y + "): " + b, 3);
        return b;
    }

    public boolean pointInBounds(Vector2 v) {
        return v.x >= 0 && v.x < getWidth() && v.y >= 0 && v.y < getHeight();
    }

    private void generate(TileMap tilemap) {
        int width = (int) (tilemap.getSize().getWidth() * density);
        int height = (int) (tilemap.getSize().getHeight() * density);
        nodes = new Node[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Node n = new Node(new Vector2((x), (y)));
                n.setPathability(tilemap.getTiles()[(int) (x / density)][(int) (y / density)].getPathability());
                set(x, y, n);
            }
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                setupNeighbors(x, y);
            }
        }
    }

    public boolean isNodePathable(int pathability, Node n) {
        return isPathableAtNodePoint(pathability, (int) n.getPosition().x, (int) n.getPosition().y);
    }

    public boolean isPathableAtNodePoint(int pathability, int x, int y) {
        boolean b = get(x, y).getPathability() <= pathability;
        return nodeInBounds(x, y) && b;
    }

    public void updatePathability(int x, int y, int pathability) {
        nodes[x][y].setPathability(pathability);
        PathFinder.changeNode(get(x, y));
    }

    public int getWidth() {
        return nodes.length;
    }

    public int getHeight() {
        return nodes[0].length;
    }

    public Node get(int x, int y) {
        return nodes[x][y];
    }

    public void set(int x, int y, Node n) {
        nodes[x][y] = n;
    }

    public float getDensity() {
        return density;
    }

    // TODO Write method
    public Vector2 isCornerPathable(float scale, int pathability, Vector2 v) {
        Vector2 cornerAngle = new Vector2(0, 0);
        if (!isPathableAtNodePoint(pathability, (int) ((v.x / scale * getDensity())), (int) ((v.y / scale * getDensity())))) {
            cornerAngle.add(-1, -1);
        }
        if (!isPathableAtNodePoint(pathability, (int) ((v.x / scale * getDensity())), (int) ((v.y / scale * getDensity()) - 1))) {
            cornerAngle.add(-1, 1);
        }
        System.out.println(v.toString());
        if (!isPathableAtNodePoint(pathability, (int) ((v.x / scale * getDensity()) - 1), (int) ((v.y / scale * getDensity()) - 1))) {
            System.out.println((int) ((v.x / scale * getDensity()) - 1) + " " + (int) ((v.y / scale * getDensity()) - 1));
            cornerAngle.add(1, 1);
        } else {
            System.out.println("b");
        }
        if (!isPathableAtNodePoint(pathability, (int) ((v.x / scale * getDensity()) - 1), (int) ((v.y / scale * getDensity())))) {
            cornerAngle.add(1, -1);
        }
        return cornerAngle;
    }
}
