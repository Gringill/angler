package com.spoffordstudios.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.esotericsoftware.minlog.Log;
import com.spoffordstudios.editor.Editor;
import com.spoffordstudios.editor.Level;
import com.spoffordstudios.network.Net;

/**
 * The core class of the Sheep Tag project. Contains only the game logic and
 * pertinent information as to the running instance of the game. All network
 * code is abstracted out of this class into the
 * {@link com.spoffordstudios.network.Net Net} class.
 * 
 * Responsible for creation of the initial LibGdx functionality, game state
 * creation, as well as maintaining the logic & render loop. In addition is the
 * first step in the level loading process.
 * 
 * @author Daniel 'Gringill' Spofford
 * 
 */
public class Game extends ApplicationAdapter {
	public static final String APP_VERSION = "0.0.1";
	public static final String APP_NAME = "Sheep Tag Editor v" + APP_VERSION;
	public static final String ATLAS_NAME = "atlas0";
	public static Game GAME;
	private static Level level;

	@SuppressWarnings("unused")
	private Stage stage;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private int width, height;

	public Game(int width, int height) {
		GAME = this;
		this.width = width;
		this.height = height;
	}

	@Override
	public void create() {
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
		Level level = Level.getDefaultLevel();

		if (Editor.enabled()) {
			Editor.setLevel(level);
		} else {
			Net.setupGameClient();
			loadLevel(level);
		}
		batch = new SpriteBatch(1000);
	}

	@Override
	public void render() {
		handleInput();
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		Rectangle scissors = new Rectangle();
		Rectangle clipBounds = new Rectangle(camera.position.x - width / 2, camera.position.y - height / 2, width, height);
		ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), clipBounds, scissors);
		ScissorStack.pushScissors(scissors);

		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (level != null) {
			batch.begin();
			for (Entity e : level.getTileGroup()) {
				e.update();
				batch.draw(e.draw(), e.getX() * 50, e.getY() * 50, 50, 50);
			}
			batch.end();
			ShapeRenderer sr = new ShapeRenderer();
			sr.setColor(Color.BLACK);
			sr.setProjectionMatrix(camera.combined);
			sr.begin(ShapeType.Line);
			for (int y = 0; y < level.getDimension().height; y++) {
				sr.line(0, y * 50, Gdx.graphics.getWidth(), y * 50);
			}
			for (int x = 0; x < level.getDimension().width; x++) {
				sr.line(x * 50, 0, x * 50, Gdx.graphics.getHeight());
			}
			sr.end();
			batch.begin();
			for (Entity e : level.getEntities()) {
				e.update();
				batch.draw(e.draw(), e.getX() * 50, e.getY() * 50, 50, 50);
			}
			batch.end();
		}
		ScissorStack.popScissors();
		Log.trace("Camera Properties: " + camera.position);
		Log.trace("FPS: " + Gdx.graphics.getFramesPerSecond());
	}

	public void loadLevel(Level level) {
		this.level = level;
		level.setup();
	}

	public static Level getLevel() {
		return level;
	}

	private void handleInput() {
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			camera.zoom += 0.02;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
			camera.zoom -= 0.02;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			if (camera.position.x > Gdx.graphics.getWidth() / 2)
				camera.translate(-3 * (camera.zoom / 1), 0, 0);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			if (camera.position.x < getLevel().getDimension().width * 50)
				camera.translate(3 * (camera.zoom / 1), 0, 0);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			if (camera.position.y > Gdx.graphics.getHeight() / 2)
				camera.translate(0, -3 * (camera.zoom / 1), 0);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			if (camera.position.y < getLevel().getDimension().height * 50)
				camera.translate(0, 3 * (camera.zoom / 1), 0);
		}
	}
}