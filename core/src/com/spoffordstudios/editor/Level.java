package com.spoffordstudios.editor;

import java.awt.Dimension;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.spoffordstudios.game.Entity;

public class Level {
	private static int DEFAULT_WIDTH = 190, DEFAULT_HEIGHT = 100;
	private ArrayList<Entity> tilegroup = new ArrayList<Entity>();
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	private String[][] tiledef;
	private int width, height;
	private TextureAtlas atlas = new TextureAtlas(Gdx.files.local("bin/atlas0.atlas"));

	public Level(String name, int width, int height, String[][] tiledef) {
		this.tiledef = tiledef;
		this.width = width;
		this.height = height;
	}

	public void setTile(Vector2 position, String tile) {
		tiledef[(int) position.x][(int) position.y] = tile;
	}

	public void addEntity(Entity entity) {
		entities.add(entity);
	}

	public String[][] getTileDef() {
		return tiledef;
	}

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

	public static Level getDefaultLevel() {
		String[][] tiledef = new String[DEFAULT_WIDTH][DEFAULT_HEIGHT];
		for (int y = 0; y < DEFAULT_HEIGHT; y++) {
			for (int x = 0; x < DEFAULT_WIDTH; x++) {
				tiledef[x][y] = "grass";
			}
		}
		return new Level("default_level", DEFAULT_WIDTH, DEFAULT_HEIGHT, tiledef);
	}

	public void setup() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				tilegroup.add(new Entity("Tile", new Sprite(atlas
						.findRegion(tiledef[x][y])), x, y));
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
}
