package com.spoffordstudios.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

public class Entity {
	private String name;
	private Vector2 position = new Vector2();
	private Sprite sprite;
	private String texture;
	private float health = 100;
	private float size = 1;
	private boolean isBuilding = false;

	public Entity(String name, Sprite sprite, int x, int y) {
		this.sprite = sprite;
		this.name = name;
		position.set(x, y);
	}

	public void update() {

	}

	public Sprite draw() {
		return sprite;
	}

	public static void preload(TextureAtlas atlas) {

	}

	public float getX() {
		return position.x;
	}

	public float getY() {
		return position.y;
	}

	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public String toString() {
		return name;
	}

	public float getHealth() {
		return health;
	}

	public boolean isBuilding() {
		return isBuilding;
	}

	public String getName() {
		return name;
	}

	public void setName(String newName) {
		name = newName;
	}

	public void setHealth(Float val) {
		health = val;
	}

	public void setBuilding(Boolean val) {
		isBuilding = val;
	}

	public void setTexture(String val) {
		texture = val;
	}

	public String getTexture() {
		return texture;
	}

	public void setSize(float val) {
		size = val;
	}

	public float getSize() {
		return size;
	}
}
