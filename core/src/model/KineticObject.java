package model;

import com.badlogic.gdx.physics.box2d.Body;
import controller.Game;
import view.stages.Gameplay;

public abstract class KineticObject extends GameObject {
	private Body body;
	private float movespeed = 1.5f;
	private float turnspeed = 0.3f;

	public KineticObject(Game game) {
		super(game);
	}

	public abstract void generatePhysicsBody(Level level);

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
