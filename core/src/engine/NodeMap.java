package engine;

import java.util.ArrayList;

import util.Logger;
import util.PathFinder;
import util.Vector2;
import data.Node;

public class NodeMap {
	public static final int PATH_GROUND = 0;
	public static final int PATH_SWIMMING = 1;
	public static final int PATH_FLYING = 2;
	private static final int[] xOffsets = { 0, 1, 0, -1, 1, 1, -1, -1 };
	private static final int[] yOffsets = { 1, 0, -1, 0, 1, -1, -1, 1 };
	private int NEIGHBOR_MODE = 8;

	private Node[][] nodes;

	public NodeMap(TileMap tilemap) {
		generate(tilemap);
	}

	public boolean lineOfSight(int pathability, Vector2 a, Vector2 b) {
		int y1 = (int) a.x;
		int x1 = (int) a.y;
		int y2 = (int) b.x;
		int x2 = (int) b.y;
		int i; // loop counter
		int ystep, xstep; // the step on y and x axis
		int error; // the error accumulated during the increment
		int errorprev; // *vision the previous value of the error variable
		int y = y1, x = x1; // the line points
		int ddy, ddx; // compulsory variables: the double values of dy and dx
		int dx = x2 - x1;
		int dy = y2 - y1;
		// POINT(y1, x1); // first point
		if (!isPathable(pathability, y1, x1)) {
			return false;
		}
		// NB the last point can't be here, because of its previous point (which
		// has to be verified)
		if (dy < 0) {
			ystep = -1;
			dy = -dy;
		} else
			ystep = 1;
		if (dx < 0) {
			xstep = -1;
			dx = -dx;
		} else
			xstep = 1;
		ddy = 2 * dy; // work with double values for full precision
		ddx = 2 * dx;
		if (ddx >= ddy) { // first octant (0 <= slope <= 1)
			// compulsory initialization (even for errorprev, needed when
			// dx==dy)
			errorprev = error = dx; // start in the middle of the square
			for (i = 0; i < dx; i++) { // do not use the first point (already
										// done)
				x += xstep;
				error += ddy;
				if (error > ddx) { // increment y if AFTER the middle ( > )
					y += ystep;
					error -= ddx;
					// three cases (octant == right->right-top for directions
					// below):
					if (error + errorprev < ddx) {// bottom square also

						if (!isPathable(pathability, y - ystep, x)) {
							return false;
						}
					} else if (error + errorprev > ddx) {// left square also
						if (!isPathable(pathability, y, x - xstep)) {
							return false;
						}

					} else { // corner: bottom and left squares also
						// if (!isPathable(pathability, y - ystep, x) ||
						// !isPathable(pathability, y, x - xstep)) {
						// return false;
						// }
					}
				}
				if (!isPathable(pathability, y, x)) {
					return false;
				}
				errorprev = error;
			}
		} else { // the same as above
			errorprev = error = dy;
			for (i = 0; i < dy; i++) {
				y += ystep;
				error += ddx;
				if (error > ddy) {
					x += xstep;
					error -= ddy;
					if (error + errorprev < ddy)
						if (!isPathable(pathability, y, x - xstep)) {
							return false;
						} else if (error + errorprev > ddy) {
							if (!isPathable(pathability, y - ystep, x)) {
								return false;
							}
						} else {
							// if (!isPathable(pathability, y, x - xstep) ||
							// !isPathable(pathability, y - ystep, x)) {
							// return false;
							// }
						}
				}
				if (!isPathable(pathability, y, x)) {
					return false;
				}
				errorprev = error;
			}
		}
		// assert ((y == y2) && (x == x2)); // the last point (y2,x2) has to be
		// the same with the last point of the algorithm
		return true;
	}

	public void setupNeighbors(int x, int y) {
		Node n = get(x, y);
		ArrayList<Node> neighbors = new ArrayList<>();
		ArrayList<Node> corners = new ArrayList<>();
		Vector2 v;
		for (int i = 4; i < 8; i++) {
			v = new Vector2(n.getPosition().x + xOffsets[i], n.getPosition().y + yOffsets[i]).snapToWorldPoint(1);
			if (nodeInBounds(v)) {
				corners.add(get((int) v.x, (int) v.y));
			}
		}
		for (int i = 0; i < NEIGHBOR_MODE; i++) {
			v = new Vector2(n.getPosition().x + xOffsets[i], n.getPosition().y + yOffsets[i]).snapToWorldPoint(1);
			if (nodeInBounds(v)) {
				neighbors.add(get((int) v.x, (int) v.y));
			}
		}
		n.setNeighbors(neighbors);
		n.setCorners(corners);
	}

	public boolean nodeInBounds(float x, float y) {
		return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
	}

	public boolean nodeInBounds(Vector2 v) {
		return v.x >= 0 && v.x < getWidth() && v.y >= 0 && v.y < getHeight();
	}

	public boolean isPathable(int pathability, Node n) {
		Vector2 v = n.getPosition();
		float x = v.x;
		float y = v.y;
		return isPathable(pathability, x, y);
	}

	private void generate(TileMap tilemap) {
		int width = (int) tilemap.getSize().getWidth();
		int height = (int) tilemap.getSize().getHeight();
		nodes = new Node[width][height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Node n = new Node(new Vector2((x), (y)));
				n.setPathability(tilemap.getTiles()[x][y].getPathability());
				set(x, y, n);
			}
		}
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				setupNeighbors(x, y);
			}
		}
	}

	public boolean isPathable(int pathability, float x, float y) {
		boolean nodeInBounds = nodeInBounds(x, y) && get((int) x, (int) y).getPathability() <= pathability;
		Logger.log("isPathable(" + (int) x + ", " + (int) y + "): " + nodeInBounds + " Entity:" + pathability + " Tile:" + get((int) x, (int) y).getPathability(), 2);
		return nodeInBounds;
	}

	public void updatePathability(int x, int y, int pathability) {
		nodes[x][y].setPathability(pathability);
		PathFinder.changeNode(get(x, y));
	}

	public static int getPathability(String pathability) {
		switch (pathability) {
		case "Ground":
			return 0;
		case "Swimming":
			return 1;
		case "Flying":
			return 2;
		default:
			return 0;
		}
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

}
