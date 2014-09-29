package util;

public class Vector2 extends com.badlogic.gdx.math.Vector2 {
	public static final short NORTH = 0;
	public static final short EAST = 1;
	public static final short SOUTH = 2;
	public static final short WEST = 3;

	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vector2(Vector2 v) {
		x = v.x;
		y = v.y;
	}

	public Vector2(com.badlogic.gdx.math.Vector2 v) {
		x = v.x;
		y = v.y;
	}

	public Vector2 add(Vector2 v) {
		x += v.x;
		y += v.y;
		return this;
	}

	public Vector2 add(float x, float y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public Vector2 sub(Vector2 v) {
		x -= v.x;
		y -= v.y;
		return this;
	}

	public Vector2 multiply(float scalar) {
		x *= scalar;
		y *= scalar;
		return this;
	}

	public Vector2 normalize() {
		float length = length();
		if (length != 0) {
			x /= length;
			y /= length;
		}
		return this;
	}

	public float length() {
		return (float) Math.sqrt(x * x + y * y);
	}

	public Vector2 copy() {
		return new Vector2(this);
	}

	public boolean equals(Vector2 v) {
		return (v.x == x && v.y == y);
	}

	public Vector2 snapToWorldPoint(int snap) {
		x = (((int) x / snap) * snap);
		y = (((int) y / snap) * snap);
		return this;
	}

	public Vector2[] getSharedEdge(Vector2 v2) {
		Vector2 v = copy().snapToWorldPoint(1);
		v2 = v2.copy().snapToWorldPoint(1);
		Vector2[] edge = { new Vector2(v.x + .5f, v.y + .5f), new Vector2(v.x + .5f, v.y + .5f) };
		if (v2.x > v.x) { // ~East
			edge[0] = new Vector2(v.x + 1, v.y + 1);
			edge[1] = new Vector2(v.x + 1, v.y);
		} else if (v2.x < v.x) { // ~West
			edge[0] = new Vector2(v.x, v.y);
			edge[1] = new Vector2(v.x, v.y + 1);
		} else {
			if (v2.y > v.y) { // North
				edge[0] = new Vector2(v.x, v.y + 1);
				edge[1] = new Vector2(v.x + 1, v.y + 1);
			} else if (v2.y < v.y) { // South
				edge[0] = new Vector2(v.x + 1, v.y);
				edge[1] = new Vector2(v.x, v.y);
			}
		}
		return edge;
	}

	public Vector2 applyPolarOffset(double offset, double angle) {
		angle = Math.toRadians(angle);
		x += (Math.cos(angle) * offset);
		y += (Math.sin(angle) * offset);
		return this;
	}

	public int headingTowards(Vector2 v2) {
		Vector2 v = copy().snapToWorldPoint(1);
		v2 = v2.copy().snapToWorldPoint(1);
		if (v2.x > v.x) {
			return EAST;
		} else if (v2.x < v.x) {
			return WEST;
		} else {
			if (v2.y > v.y) {
				return NORTH;
			} else if (v2.y < v.y) {
				return SOUTH;
			}
		}
		return -1;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
