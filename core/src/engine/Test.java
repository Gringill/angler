package engine;

import util.Vector2;

public class Test {
	public static void main(String[] args) {

		Vector2 v = new Vector2(1, 0);
		v.setAngle(90);
		Vector2 v2 = new Vector2(1, 0);
		v2.setAngle(91);
		if (v.copy().crs(v2) < 0) {
			System.out.println(v2.angle() + " is right of " + v.angle());
		} else {
			System.out.println(v2.angle() + " is left of " + v.angle());
		}
		v = new Vector2(1, 0);
		v2 = new Vector2(0, 1);
		System.out.println(Math.acos(v.normalize().dot(v2.normalize())));
	}
}
