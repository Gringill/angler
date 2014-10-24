package util;

public class Vector2 extends com.badlogic.gdx.math.Vector2 {
	public static final short NORTH = 0;
	public static final short EAST = 1;
	public static final short SOUTH = 2;
	public static final short WEST = 3;

	public Vector2() {
		x = 1;
	}

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

	public Vector2 snapToWorldPoint(float snap) {
		x = (((int) (x / snap)) * snap);
		y = (((int) (y / snap)) * snap);
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

	public Vector2 applyPolarOffset(double offset, Vector2 angle) {
		x += (Math.cos(angle.angleRad()) * offset);
		y += (Math.sin(angle.angleRad()) * offset);
		return this;
	}

	/**
	 * @param angle
	 *            in degrees
	 */
	public Vector2 rotate(double angle) {
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);

		double newX = this.x * cos - this.y * sin;
		double newY = this.x * sin + this.y * cos;

		this.x = (float) newX;
		this.y = (float) newY;

		return this;
	}

	public double angleTowardsPoint(Vector2 v2) {
		double angle = Math.atan2(v2.y - y, v2.x - x);
		
		return angle;
	}

	public static void main(String[] args) {
		Vector2 v = new Vector2(1, 1);
		Vector2 v2 = new Vector2(1, 0);
		System.out.println(v.angle());
		System.out.println(v2.angle());
		System.out.println(Math.toDegrees(v.angleTowardsPoint(v2)));
	}

	/**
	 * 
	 * @param v1
	 * @param v2
	 * @param toDegrees
	 * @return the angle defined by the points v2, v1, (v1.y, v2.x)
	 */
	public double angleTowardsPoint(float x, float y) {
		return Math.atan2(y -= this.y, x -= this.x);
	}

	public double angleBetween(Vector2 v) {
		return Math.acos(copy().normalize().dot(v.copy().normalize()));
	}

	public int headingTowards(Vector2 v2) {
		Vector2 v = copy().snapToWorldPoint(1);
		v2 = v2.copy().snapToWorldPoint(1);
		int dx = (int) (v2.x - v.x);
		int dy = (int) (v2.y - v.y);
		if (Math.abs(dx) >= Math.abs(dy)) {
			if (dx >= 0) {
				return EAST;
			} else {
				return WEST;
			}
		} else {
			if (dy >= 0) {
				return NORTH;
			} else {
				return SOUTH;
			}
		}
	}

	/**
	 * 
	 * @param v
	 * @return 1 if v is counterclockwise of this <br>
	 *         -1 if v is clockwise of this
	 */
	public int getAngularRelationship(Vector2 v) {
		if (y * v.x > x * v.y) {
			return 1; // Counter-Clockwise
		} else {
			return -1; // Clockwise
		}
	}

	/**
	 * @return 1 if v is counterclockwise of this <br>
	 *         -1 if v is clockwise of this
	 */
	public int getAngularRelationship(float x2, float y2) {
		if (y * x2 > x * y2) {
			return 1; // Counter-Clockwise
		} else {
			return -1; // Clockwise
		}
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
