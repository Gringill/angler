package data;

import com.badlogic.gdx.physics.box2d.Body;

public abstract class KineticObject extends GameObject {
	private Body body;
	private float movespeed = 1.5f;
	private float turnspeed = 0.3f;

	public abstract void generatePhysicsBody();

	public boolean hasBody() {
		return body != null;
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body newBody) {
		body = newBody;
	}

	public float getMoveSpeed() {
		return movespeed;
	}

	public float getTurnSpeed() {
		return turnspeed;
	}

	public void setMoveSpeed(float movespeed) {
		this.movespeed = movespeed;
	}
}
