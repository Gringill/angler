package data;

import com.badlogic.gdx.physics.box2d.Body;

public abstract class KineticObject extends GameObject {
	private Body body;
	private float movespeed = 3;

	public abstract void generatePhysicsBody();

	public Body getBody() {
		return body;
	}

	public void setBody(Body newBody) {
		body = newBody;
	}

	public float getMoveSpeed() {
		return movespeed;
	}

	public void setMoveSpeed(float movespeed) {
		this.movespeed = movespeed;
	}
}
