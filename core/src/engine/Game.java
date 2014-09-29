package engine;

import gui.GameInputHandler;

import java.awt.Point;
import java.util.ArrayList;

import util.Util;
import util.Vector2;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.esotericsoftware.minlog.Log;

import data.Entity;
import data.GameObject;
import data.Level;
import data.Tile;

/**
 * The core class of the Sheep Tag project. Contains only the game logic and
 * pertinent information as to the running instance of the game. All network
 * code is abstracted out of this class into the {@link engine.Net Net} class.
 * 
 * Responsible for creation of the initial LibGdx functionality, game state
 * creation, as well as maintaining the logic & render loop. In addition is the
 * first step in the level loading process.
 * 
 * @author Daniel 'Gringill' Spofford
 * 
 */
public class Game extends ApplicationAdapter implements ContactListener {
	public static final String APP_VERSION = "0.0.1";
	public static final String APP_NAME = "***** *** Editor v" + APP_VERSION;
	public static final String ATLAS_NAME = "atlas0";
	public static final String COMMAND_MOVE = "COMMAND_MOVE";
	private Level level;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private float width, height;
	private ArrayList<Entity> selectedEntities = new ArrayList<Entity>();
	private World world;
	private Box2DDebugRenderer debugRenderer;
	private boolean drawBuildingPathing;
	private boolean drawGrid;
	private Util util = new Util(this);
	private GameInputHandler inputHandler;
	private Minimap minimap;
	private Editor editor;

	public Game(float width, float height) {
		this.width = width;
		this.height = height;
		minimap = new Minimap(this);
	}

	@Override
	public void create() {
		world = new World(new Vector2(0, 0), true);
		world.setContactListener(this);
		debugRenderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, 0);
		// Level level = Level.getDefaultLevel();
		// Net.setupGameClient();
		// loadLevel(level);
		inputHandler = new GameInputHandler(this);
		if (Gdx.input.getInputProcessor() == null) {
			Gdx.input.setInputProcessor(inputHandler);
		}

		batch = new SpriteBatch(1000);
	}

	@Override
	public void render() {
		ShapeRenderer sr = null;
		Sprite s;
		handleInput();
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		Rectangle scissors = new Rectangle();
		Rectangle clipBounds = new Rectangle(camera.position.x - width / 2f, camera.position.y - height / 2f, width, height);
		ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), clipBounds, scissors);
		ScissorStack.pushScissors(scissors);

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (getLevel() != null) {
			//
			// In a level
			//
			sr = new ShapeRenderer();
			batch.begin();
			for (Tile[] ta : getLevel().getTileMap().getTiles()) {
				for (Tile t : ta) {
					t.update();
					t.draw(batch);
				}
			}
			if (editor.getSelectedTile() != null) {
				editor.getSelectedTile().drawSelection(batch);
			}
			batch.end();

			if (drawGrid) {
				sr.setColor(Color.WHITE);
				sr.setProjectionMatrix(camera.combined);
				sr.begin(ShapeType.Line);
				for (int y = 0; y < getLevel().getTileMap().getSize().height * 50; y++) {
					sr.line(0, y * 50, getLevel().getTileMap().getSize().width * 50, y * 50);
				}
				for (int x = 0; x < getLevel().getTileMap().getSize().width * 50; x++) {
					sr.line(x * 50f, 0, x * 50f, getLevel().getTileMap().getSize().height * 50f);
				}
				sr.end();
			}
			batch.begin();
			for (GameObject e : getLevel().getEntities()) {
				e.update();
				e.draw(batch);
			}
			batch.flush();
			batch.end();

			sr.setColor(Color.GREEN);
			sr.begin(ShapeType.Line);
			for (GameObject e : getLevel().getEntities()) {
				if (e.isSelected()) {
					sr.circle(e.getX(), (e.getY()), e.getSize() / 2f);
				}
			}

			Point mouseLocalCoords = new Point(Gdx.input.getX(), Gdx.input.getY());
			if (mouseLocalCoords != null && inputHandler.getDragBeginPoint() != null) {
				Vector2 mouseWorldCoords = getUtil().getMouseWorldCoords(mouseLocalCoords, true);
				Vector2 begin = inputHandler.getDragBeginPoint();
				sr.rect(begin.x, begin.y, mouseWorldCoords.x - begin.x, mouseWorldCoords.y - begin.y);
			}

			debugRenderer.render(world, camera.combined.cpy().scale(util.getGameScale(), util.getGameScale(), 1));
			world.step(1 / 60f, 6, 2);
		}
		if (sr != null) {
			sr.flush();
		}
		ScissorStack.popScissors();
	}

	public void setLevel(Level level) {
		this.level = level;
		for (GameObject e : getLevel().getEntities()) {
			getMinimap().registerEntity(e);
		}
	}

	public Level getLevel() {
		return level;
	}

	public Minimap getMinimap() {
		return minimap;
	}

	private void handleInput() {
		if (getLevel() != null) {
			if (Gdx.input.isKeyPressed(Input.Keys.A)) {
				camera.zoom += 0.02;
			}
			if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
				camera.zoom -= 0.02;
			}
			if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
				if (camera.position.x > camera.viewportWidth / 2)
					camera.translate(-12, 0, 0);
			}
			if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
				if (camera.position.x < (getLevel().getTileMap().getSize().width * 50) - (camera.viewportWidth / 2))
					camera.translate(12, 0, 0);
			}
			if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
				if (camera.position.y > camera.viewportHeight / 2)
					camera.translate(0, -10, 0);
			}
			if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
				if (camera.position.y < (getLevel().getTileMap().getSize().height * 50) - (camera.viewportHeight / 2))
					camera.translate(0, 10, 0);
			}
			if (camera.position.y >= (getLevel().getTileMap().getSize().height * 50) - (camera.viewportHeight / 2))
				camera.position.set(camera.position.x, (getLevel().getTileMap().getSize().height * 50) - (camera.viewportHeight / 2), 0);
			if (camera.position.y <= camera.viewportHeight / 2)
				camera.position.set(camera.position.x, camera.viewportHeight / 2 + 1, 0);
			if (camera.position.x >= (getLevel().getTileMap().getSize().width * 50) - (camera.viewportWidth / 2))
				camera.position.set((getLevel().getTileMap().getSize().width * 50) - (camera.viewportWidth / 2), camera.position.y, 0);
			if (camera.position.x <= camera.viewportWidth / 2)
				camera.position.set(camera.viewportWidth / 2 + 1, camera.position.y, 0);
		}
	}

	public void select(Rectangle r) {
		selectedEntities.clear();
		for (Entity e : getLevel().getEntities()) {
			if (r.contains(e.getPosition())) {
				e.setSelected(true);
				selectedEntities.add(e);
			} else {
				e.setSelected(false);
			}
		}
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	/**
	 * Works with vector parameter directly
	 * 
	 * @param entities
	 * @param command
	 * @param goal
	 */
	public void issuePointCommand(ArrayList<Entity> entities, String command, Vector2 goal) {
		for (Entity e : entities) {
			e.issuePointCommand(command, goal);
		}
	}

	public World getWorld() {
		return world;
	}

	public ArrayList<Entity> getSelectedEntities() {
		return selectedEntities;
	}

	public boolean doDrawBuildingPathing() {
		return drawBuildingPathing;
	}

	public void setDrawBuildingPathing(boolean flag) {
		drawBuildingPathing = flag;
	}

	@Override
	public void beginContact(Contact contact) {
		Log.debug("Collision!");
	}

	@Override
	public void endContact(Contact contact) {
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {

	}

	public float getGameWidth() {
		return (float) level.getTileMap().getSize().getWidth() * util.getGameScale();
	}

	public float getGameHeight() {
		return 0;
	}

	public Util getUtil() {
		return util;
	}

	public boolean isLoading() {
		return batch == null;
	}

	public GameInputHandler getInputHandler() {
		return inputHandler;
	}

	public void connectToEditor(Editor editor) {
		this.editor = editor;
	}
}