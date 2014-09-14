package gui;

import util.Logger;
import util.Util;
import util.Vector2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Rectangle;

import data.Entity;
import data.GameObject;
import data.Tile;
import engine.Editor;
import engine.Game;

public class EditorInputHandler implements InputProcessor {
	private Editor editor;
	private GameInputHandler gameInputHandler;

	public EditorInputHandler(Editor editor) {
		this.editor = editor;
	}

	public void leftClick(float screenX, float screenY) {
		Logger.log("Left Click.", 3);
		float shortestDist = 9999999999f;
		float dist;
		Entity ent = null;
		Vector2 worldCoords = editor.getGame().getUtil().getMouseWorldCoords(new Vector2(screenX, screenY), true);
		if (editor.isObjectMode()) {
			Logger.log("Object Mode.", 3);
			for (Entity e2 : editor.getGame().getLevel().getEntities()) {
				dist = Util.getDistanceBetweenPoints(e2.getPosition(), worldCoords);
				if (dist < e2.getSize() && dist < shortestDist) {
					shortestDist = dist;
					ent = e2;
				}
			}
			if (ent != null) {
				for (GameObject oldSelectEntity : editor.getGame().getSelectedEntities()) {
					oldSelectEntity.setSelected(false);
				}
				ent.setSelected(true);
				editor.getGame().getSelectedEntities().clear();
				editor.getGame().getSelectedEntities().add(ent);
				editor.setSubText("Selected unit of type [" + ent.getName() + "]");
			}
		} else {
			Logger.log("Tile Mode.", 3);
			Tile tile = editor.getGame().getLevel().getTileMap().getTileAtWorldCoords(worldCoords);
			editor.setSelectedTile(tile);
		}
	}

	public void transferInput() {
		gameInputHandler = editor.getGame().getInputHandler();
	}

	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
		case Input.Keys.ENTER:
			if (editor.getGame().getSelectedEntities().size() > 0) {
				InputAttribute.showDialog(editor, editor.getGame().getSelectedEntities().get(0));
			} else {
				if (editor.getSelectedTile() != null) {
					InputAttribute.showDialog(editor, editor.getSelectedTile());
				}
			}
			break;
		case Input.Keys.NUM_1:
			editor.getGame().getCamera().position.set(480, 270, 0);
			break;
		case Input.Keys.NUM_2:
			editor.getGame().getCamera().position.set(480, 810, 0);
			break;
		case Input.Keys.DEL:
			for (GameObject e : editor.getGame().getSelectedEntities()) {
				editor.getGame().getLevel().getEntities().remove(e);
				editor.getGame().getMinimap().unregisterEntity(e);
			}

			break;
		case Input.Keys.B:
			for (Entity e : editor.getGame().getSelectedEntities()) {
				e.generatePhysicsBody();
			}
			break;
		case Input.Keys.S:
			editor.stepFlag = true;
			break;
		}
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
		if (editor.getGame().getLevel() != null) {
			if (editor.doEdit()) { // Editor Mode
				if (editor.isSelectMode()) { // Select Mode
					if (!Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
						// No control (not adjusting facing)
						float shortestDist = 500;
						float dist;
						Entity ent = null;

						for (Entity e2 : editor.getGame().getLevel().getEntities()) {
							dist = Util.getDistanceBetweenPoints(e2.getPosition(), editor.getGame().getUtil().getMouseWorldCoords(new Vector2(screenX, screenY), true));
							if (dist < e2.getSize() && dist < shortestDist) {
								shortestDist = dist;
								ent = e2;
							}
						}
						if (ent != null && ent != editor.getEntityCursor()) {
							ent.setSelected(true);
							editor.setDraggedEntity(ent);
						} else {
							gameInputHandler.setDragBeginPoint(editor.getGame().getUtil().getMouseWorldCoords(new Vector2(screenX, screenY), true));
						}
					} else {
						// Changing rotation
					}
				}
			} else {

			}
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (editor.getGame().getLevel() != null) {
			if (editor.doEdit()) {
				Vector2 end = editor.getGame().getUtil().getMouseWorldCoords(new Vector2(screenX, screenY), true);
				if (editor.isPaintMode()) { // Paint entity
					if (editor.getObjectEditor().getPainter().getSelectedTile() == null) {
						Entity newEntity = editor.getEntityCursor().clone();
						editor.getGame().getMinimap().registerEntity(newEntity);
						editor.getGame().getLevel().addEntity(newEntity);
					} else { // Paint tile
						editor.getGame().getLevel().getTileMap().setTile(end, editor.getObjectEditor().getPainter().getSelectedTile());
					}
				} else if (gameInputHandler.getDragBeginPoint() == null || gameInputHandler.getDragBeginPoint().equals(end)) { // Single
					// left click (press and release in same spot)
					leftClick(screenX, screenY);
				} else {
					Rectangle r = Util.getPleasantRectangle(gameInputHandler.getDragBeginPoint(), end);
					editor.select(r);
				}
			} else {
				if (button == Buttons.RIGHT) {
					editor.getGame();
					editor.getGame().issuePointCommand(editor.getGame().getSelectedEntities(), Game.COMMAND_MOVE, editor.getGame().getUtil().getMouseWorldCoords(new Vector2(screenX, screenY), true));
				}
			}
		}
		if (gameInputHandler != null) {
			gameInputHandler.setDragBeginPoint(null);
		}
		editor.setDraggedEntity(null);
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (editor.isPaintMode()) {

		} else {
			if (editor.getGame().getSelectedEntities().size() >= 1 && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
				// Force selected entities to face dragged mouse position
				for (GameObject e : editor.getGame().getSelectedEntities()) {
					e.setFacing(Util.getAngleTowardsPoint(e.getPosition(), editor.getGame().getUtil().getMouseWorldCoords(new Vector2(screenX, screenY), true), true));
				}
			} else {
				if (editor.getDraggedEntity() != null) {
					Vector2 worldMouse = editor.getGame().getUtil().getMouseWorldCoords(new Vector2(screenX, screenY), true);
					if (editor.getDraggedEntity().isBuilding()) {
						editor.getDraggedEntity().setPosition(Util.snapToWorldPoint(worldMouse, 25));
					} else {
						editor.getDraggedEntity().setPosition(worldMouse);
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		GameObject cursor = editor.getEntityCursor();
		if (cursor != null) {
			// Because when the editor load there is no level >> no cursor
			Vector2 worldMouse = editor.getGame().getUtil().getMouseWorldCoords(new Vector2(screenX, screenY), true);
			if (cursor.isBuilding()) {
				if (editor.getObjectEditor().getPainter().isTileMode()) {
					cursor.setPosition(Util.snapToWorldPoint(worldMouse, 50));
				} else {
					cursor.setPosition(Util.snapToWorldPoint(worldMouse, 25).add(cursor.getWidth() / 2, cursor.getHeight() / 2));
				}
			} else {
				cursor.setPosition(worldMouse);
			}
		}
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
