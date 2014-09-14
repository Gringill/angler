package gui;

import util.Util;
import util.Vector2;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

import data.GameObject;
import engine.Game;

public class GameInputHandler implements InputProcessor {
	private Game game;
	private Vector2 dragBeginPoint;

	public GameInputHandler(Game game) {
		this.game = game;
	}

	public Vector2 getDragBeginPoint() {
		return dragBeginPoint;
	}

	public void setDragBeginPoint(Vector2 v) {
		dragBeginPoint = v;
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == Buttons.RIGHT) {
			game.issuePointCommand(game.getSelectedEntities(), Game.COMMAND_MOVE, game.getUtil().getMouseWorldCoords(new Vector2(screenX, screenY), false));
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (getGame().getSelectedEntities().size() >= 1 && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
			// Force selected entities to face dragged mouse position
			for (GameObject e : getGame().getSelectedEntities()) {
				e.setFacing(Util.getAngleTowardsPoint(e.getPosition(), getGame().getUtil().getMouseWorldCoords(new Vector2(screenX, screenY), true), true));
			}
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {

		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	public Game getGame() {
		return game;
	}
}
