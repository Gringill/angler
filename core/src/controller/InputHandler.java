package controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Rectangle;
import model.Entity;
import model.GameObject;
import model.Tile;
import model.Vector2;
import model.util.Logger;
import model.util.Util;
import view.panel.InputAttribute;
import view.stages.StageManager;
import view.stages.Editor;
import view.stages.Gameplay;

public class InputHandler {
	private StageManager stageManager;
	private boolean editorMode;
	private Game game;
	private Editor editor;
	private Vector2 pointA, pointB;

	public InputHandler(StageManager stageManger, boolean editorMode) {
		this.game = stageManger.getGame();
		this.stageManager = stageManger;
		this.editorMode = editorMode;
	}

	public boolean keyUp(int keycode) {
		return false;
	}

	public boolean keyTyped(char character) {
		return false;
	}

	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (editor.getLevel() != null) {
			if (editor.doEdit()) { // Editor Mode
				if (editor.isSelectMode()) { // Select Mode
					if (!Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
						// No control (not adjusting facing)
						float shortestDist = 500;
						float dist;
						Entity ent = null;
						for (Entity e2 : editor.getLevel().getEntities()) {
							dist = Util.getDistanceBetweenPoints(e2.getPosition(), Util.getMouseWorldCoords(editor, new Vector2(screenX, screenY), true));
							if (dist < e2.getSize() && dist < shortestDist) {
								shortestDist = dist;
								ent = e2;
							}
						}
						if (ent != null && ent != editor.getEntityCursor()) {
							ent.setSelected(true);
							editor.setDraggedEntity(ent);
						} else {
							Vector2 coords = Util.getMouseWorldCoords(editor, new Vector2(screenX, screenY), true);
							editor.setDragBeginPoint(coords);
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

	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(editorMode) {
			if (editor.getLevel() != null) {
				if (editor.doEdit()) {
					Vector2 worldTouchPoint = Util.getMouseWorldCoords(editor, new Vector2(screenX, screenY), true);
					if (editor.isPaintMode()) { // Paint entity
						if (editor.getObjectEditor().getPainter().getSelectedTile() == null) {
							Entity newEntity = editor.getEntityCursor().clone();
							editor.getMinimap().registerEntity(newEntity);
							editor.getLevel().addEntity(newEntity);
						} else { // Paint tile
							editor.getLevel().getTileMap().setTileAtWorldPoint(worldTouchPoint, editor.getObjectEditor().getPainter().getSelectedTile());
						}
					} else if (editor.getDragBeginPoint() == null || editor.getDragBeginPoint().equals(worldTouchPoint)) { // Single
						// left click (press and release in same spot)
						leftClick(screenX, screenY);
					} else {
						Rectangle r = Util.getPleasantRectangle(editor.getDragBeginPoint(), worldTouchPoint);
						editor.select(r);
					}
				} else {
					if (button == Buttons.RIGHT) {
						editor.issuePointCommand(editor.getSelectedEntities(), Game.COMMAND_MOVE, Util.getMouseWorldCoords(editor, new Vector2(screenX, screenY), true));
					}
				}
			}
			editor.setDragBeginPoint(null);
			editor.setDraggedEntity(null);
		} else {
			if (button == Buttons.RIGHT) {
				if(stageManager.getActiveStage() instanceof Gameplay) {
					Gameplay stage = (Gameplay) stageManager.getActiveStage();
					stage.issuePointCommand(stage.getSelectedEntities(), Game.COMMAND_MOVE, Util.getMouseWorldCoords(editor, new Vector2(screenX, screenY), false));
				}

			}
		}

		return false;
	}

	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(editorMode) {
			if (editor.isPaintMode()) {

			} else {
				if (editor.getSelectedEntities().size() >= 1 && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
					// Force selected entities to face dragged mouse position
					for (GameObject e : editor.getSelectedEntities()) {
						e.setFacing(e.getPosition().getAngleTowards(Util.getMouseWorldCoords(editor, new Vector2(screenX, screenY), true)));
					}
				} else {
					if (editor.getDraggedEntity() != null) {
						Vector2 worldMouse = Util.getMouseWorldCoords(editor, new Vector2(screenX, screenY), true);
						if (editor.getDraggedEntity().isBuilding()) {
							editor.getDraggedEntity().setPosition(Util.snapToWorldPoint(worldMouse, 25));
						} else {
							editor.getDraggedEntity().setPosition(worldMouse);
						}
					}
				}
			}
		} else {
			if (stageManager.getActiveStage() instanceof Gameplay) {
				Gameplay stage = (Gameplay) stageManager.getActiveStage();
				if (stage.getSelectedEntities().size() >= 1 && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
					// Force selected entities to face dragged mouse position
					for (GameObject e : stage.getSelectedEntities()) {
						e.setFacing(e.getPosition().getAngleTowards(Util.getMouseWorldCoords(editor, new Vector2(screenX, screenY), true)));
					}
				}
			}
		}

		return false;
	}

	public boolean mouseMoved(int screenX, int screenY) {
		if(editorMode) {
			Editor editor = (Editor) stageManager.getActiveStage();
			GameObject cursor = editor.getEntityCursor();
			if (cursor != null) {
				// Because when the editor load there is no level >> no cursor
				Vector2 worldMouse = Util.getMouseWorldCoords(editor, new Vector2(screenX, screenY), true);
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
		}
		return false;
	}



	public boolean scrolled(int amount) {
		return false;
	}

	public void leftClick(int screenX, int screenY) {
		float shortestDist = 9999999999f;
		float dist;
		Entity ent = null;
		Vector2 worldCoords = Util.getMouseWorldCoords(editor, new Vector2(screenX, screenY), true);
		System.out.println("Screen Coords: "
				+ screenX
				+ " "
				+ screenY
				+ " World Coords: "
				+ worldCoords
				+ " Boolean: "
				+ editor
				.getLevel()
				.getTileMap()
				.getNodeMap()
				.isPathableAtNodePoint(0, (int) (worldCoords.x / 50 * editor.getLevel().getTileMap().getNodeMap().getDensity()),
						(int) (worldCoords.y / 50 * editor.getLevel().getTileMap().getNodeMap().getDensity())));
		if (editor.isObjectMode()) { // TODO put entities into quad-trees
			for (Entity e2 : editor.getLevel().getEntities()) {
				dist = Util.getDistanceBetweenPoints(e2.getPosition(), worldCoords);
				if (dist < e2.getSize() && dist < shortestDist) {
					shortestDist = dist;
					ent = e2;
				}
			}
			if (ent != null) {
				for (GameObject oldSelectEntity : editor.getSelectedEntities()) {
					oldSelectEntity.setSelected(false);
				}
				ent.setSelected(true);
				editor.getSelectedEntities().clear();
				editor.getSelectedEntities().add(ent);
				editor.setSubText("Selected unit of type [" + ent.getName() + "]");
			}
		} else {
			if (pointA == null) {
				pointA = worldCoords.copy();
			} else {
				pointB = worldCoords.copy();
				Logger.log(
						"LOS from "
								+ pointA.copy().multiply(1f / 50 * editor.getLevel().getTileMap().getNodeMap().getDensity())
								+ " to "
								+ pointB.copy().multiply(1f / 50 * editor.getLevel().getTileMap().getNodeMap().getDensity())
								+ ": "
								+ editor
								.getLevel()
								.getTileMap()
								.getNodeMap()
								.lineOfSight(0, pointA.multiply(1f / 50 * editor.getLevel().getTileMap().getNodeMap().getDensity()),
										pointB.multiply(1f / 50 * editor.getLevel().getTileMap().getNodeMap().getDensity())), "Tag1", true);
				pointA = null;
				pointB = null;
			}
			Tile tile = editor.getLevel().getTileMap().getTileAtWorldCoords(worldCoords);
			editor.setSelectedTile(tile);
		}
	}

	//	public void transferInput() {
//		gameInputHandler = editor.getInputHandler();
//	}

	public Game getGame() {
		return game;
	}
}
