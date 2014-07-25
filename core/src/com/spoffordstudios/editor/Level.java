package com.spoffordstudios.editor;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.spoffordstudios.game.Entity;

/**
 * Holds all information relevant to a specific level, from size, to entities
 * within, to tiles. Can parse in level files with the
 * {@link #deserializeLevel(File selectedFile)} method.
 * 
 * @author Daniel
 */
public class Level implements Json.Serializable {
	private static final String LEVEL_FILE_TYPE = "stlvl";
	private static int DEFAULT_WIDTH = 190, DEFAULT_HEIGHT = 100;
	private static TextureAtlas atlas = new TextureAtlas(Gdx.files.local("bin/atlas0.atlas"));

	/**
	 * If the level has been saved before this will contain that location.
	 */
	private File lastSave;
	/**
	 * A 2D array of strings defining what terrain is where. Used to populate
	 * the <code>tilegroup</code> field.
	 */
	private String[][] tiledef;
	/**
	 * A list of entities defined in the {@link #setup} method in accordance to
	 * the {@link #tiledef}.
	 */
	private ArrayList<Entity> tilegroup = new ArrayList<Entity>();
	/**
	 * A working list of entities to be updated and drawn with every cycle.
	 */
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	private int width, height;

	public Level() {

	}

	public Level(String name, int width, int height, String[][] tiledef) {
		this.tiledef = tiledef;
		this.width = width;
		this.height = height;
	}

	/**
	 * Modifications to the {@link #tiledef} performed in this manner are not
	 * automatically reflected by the tilegroup, and will not be noticed
	 * visually until level reload or a manual refresh of the tilegroup is
	 * performed.
	 * 
	 * @param position
	 * @param tile
	 *            replaces the old tile at <code>position</code>
	 */
	public void setTile(Vector2 position, String tile) {
		tiledef[(int) position.x][(int) position.y] = tile;
	}

	public String[][] getTileDef() {
		return tiledef;
	}

	/**
	 * Creates a level with the specified attributes.
	 * 
	 * @param name
	 * @param width
	 *            in grid units!
	 * @param height
	 *            in grid units!
	 * @return
	 */
	public static Level createLevel(String name, Integer width, Integer height) {
		String[][] td = new String[width][height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				td[x][y] = "grass";
			}
		}
		Level l = new Level(name, width, height, td);
		return l;
	}

	/**
	 * Convenience method that creates a new level with a predefined default set
	 * of attributes.
	 */
	public static Level getDefaultLevel() {
		String[][] tiledef = new String[DEFAULT_WIDTH][DEFAULT_HEIGHT];
		for (int y = 0; y < DEFAULT_HEIGHT; y++) {
			for (int x = 0; x < DEFAULT_WIDTH; x++) {
				tiledef[x][y] = "grass";
			}
		}
		return new Level("default_level", DEFAULT_WIDTH, DEFAULT_HEIGHT, tiledef);
	}

	/**
	 * Loops over the {@link #tiledef}'s (atlas) string values and uses them to
	 * populate the {@link tilegroup} with sprites.
	 */
	public void buildTileGroup() {
		Sprite s;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				s = new Sprite(atlas.findRegion(tiledef[x][y]));
				s.setSize(50, 50);
				tilegroup.add(new Entity("Tile", s, x * 50, y * 50));
			}
		}
	}

	public void loadAtlas() {
		atlas = new TextureAtlas(Gdx.files.local("bin/atlas0.atlas"));
	}
	
	public TextureAtlas getAtlas() {
		return atlas;
	}

	public ArrayList<Entity> getEntities() {
		return entities;
	}

	public ArrayList<Entity> getTileGroup() {
		return tilegroup;
	}

	public Dimension getDimension() {
		return new Dimension(width, height);
	}

	public Entity addEntity(Entity entity) {
		entities.add(entity);
		return entity;
	}

	public void removeEntity(Entity e) {
		entities.remove(e);
	}

	/**
	 * Shows the file chooser dialog for opening.
	 */
	public static void open() {
		final JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return "Sheep Tag Level";
			}

			@Override
			public boolean accept(File f) {
				String extension = Util.getExtension(f);
				if (extension != null) {
					if (extension.equals(Level.LEVEL_FILE_TYPE)) {
						return true;
					} else {
						return false;
					}
				}
				return false;
			}
		});
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = fc.showOpenDialog(Editor.EDITOR);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			Editor.openLevel(deserializeLevel(fc.getSelectedFile()));
		}
	}

	/**
	 * Show the file chooser dialog for saving.
	 */
	public void saveAs() {
		final JFileChooser fc = new JFileChooser();
		File file;
		fc.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return "Sheep Tag Level";
			}

			@Override
			public boolean accept(File f) {
				String extension = Util.getExtension(f);
				if (extension != null) {
					if (extension.equals(Level.LEVEL_FILE_TYPE)) {
						return true;
					} else {
						return false;
					}
				}
				return false;
			}
		});
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = fc.showSaveDialog(Editor.EDITOR);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
			if (file.toString().endsWith("." + LEVEL_FILE_TYPE) == false) {
				file = new File(file.toString() + "." + LEVEL_FILE_TYPE);
			}
			save(file);
		}
	}

	/**
	 * If the <code>target</code> parameter is null and the {@link #lastSave}
	 * field is not null it will save. Otherwise it will default to the
	 * {@link #saveAs()} method.
	 * 
	 * @param saveAsOverrideTarget
	 *            when populated causes the {@link #saveAs()} method to be
	 *            called instead
	 */
	public void save(File saveAsOverrideTarget) {
		if (saveAsOverrideTarget != null) {
			Json json = new Json();
			String s = json.prettyPrint(this);
			(new FileHandle(saveAsOverrideTarget)).writeString(s, false);
			lastSave = saveAsOverrideTarget;
		} else {
			if (lastSave != null) {
				Json json = new Json();
				String s = json.toJson(this);
				(new FileHandle(lastSave)).writeString(s, false);
			} else {
				saveAs();
			}
		}
	}

	/**
	 * Parses a Sheep Tag level file from the standard Json format.
	 * 
	 * @param selectedFile
	 *            expected to be in Json format.
	 * @return the target file as a built level object
	 */
	public static Level deserializeLevel(File selectedFile) {
		FileHandle file = new FileHandle(selectedFile.getAbsolutePath());
		String jsonText = file.readString();
		JsonValue jsonData = new JsonReader().parse(jsonText);
		Level level = new Json().readValue(Level.class, jsonData);
		return level;
	}

	@Override
	public void write(Json json) {
		json.writeValue("width", width);

		json.writeValue("height", height);

		json.writeValue("mod_entity", Editor.EDITOR.getSharedEntityModel());

		json.writeValue("entities", entities);

		json.writeValue("tiledef", tiledef);

	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		width = json.readValue(Integer.class, jsonData.get("width"));
		height = json.readValue(Integer.class, jsonData.get("height"));
		Editor.EDITOR.getObjectEditor().registerEntityListModel(json.readValue(DefaultListModel.class, jsonData.get("mod_entity")));
		entities = json.readValue(ArrayList.class, Entity.class, jsonData.get("entities"));
		tiledef = json.readValue(String[][].class, jsonData.get("tiledef"));
	}
}
