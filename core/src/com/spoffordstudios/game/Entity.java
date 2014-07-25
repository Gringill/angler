package com.spoffordstudios.game;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.spoffordstudios.editor.Util;

public class Entity implements Json.Serializable {
	private String name;
	private Vector2 position = new Vector2();
	private float facing;
	private Sprite sprite; // TODO replace with animations
	// private ArrayList<Animation> animations = new ArrayList<Animation>();
	private String texture = "bin/unpacked/cam.png";
	private float health = 100;
	private float size = 25;
	private float width = 25;
	private float height = 25;
	private boolean isBuilding;
	private boolean selected;

	public Entity(String name, Sprite sprite, float x, float y) {
		this.sprite = sprite;
		this.name = name;
		position.set(x, y);
	}

	public Entity(String name, float x, float y) {
		String file = com.spoffordstudios.editor.Util.pullRegionFromTexture(texture);
		this.sprite = new Sprite(Game.getLevel().getAtlas().findRegion(file));
		this.name = name;
		position.set(x, y);
	}

	public Entity() {

	}

	public void update() {

	}

	public void draw(SpriteBatch batch) {
		if (isBuilding()) {
			Sprite sprite = new Sprite(Game.getLevel().getAtlas().findRegion("pathability_color"));
			sprite.setSize(width, height);
			batch.draw(sprite, (getX()) - (sprite.getWidth() / 2), (getY()) - (sprite.getHeight() / 2), (sprite.getWidth() / 2), (sprite.getHeight() / 2), sprite.getWidth(), sprite.getHeight(), 1, 1,
					getFacing());
		}
		batch.draw(sprite, (getX()) - (sprite.getWidth() / 2), (getY()) - (sprite.getHeight() / 2), (sprite.getWidth() / 2), (sprite.getHeight() / 2), sprite.getWidth(), sprite.getHeight(), 1, 1,
				getFacing());
	}

	public float getX() {
		return position.x;
	}

	public float getY() {
		return position.y;
	}

	public void setSprite(Sprite newSprite) {
		sprite = newSprite;
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

	public Vector2 getPosition() {
		return position.cpy();
	}

	public void setPosition(Vector2 newPosition) {
		position = newPosition;
	}

	/**
	 * Pulls all attributes from the source entity and puts them into this
	 * entity, effectively redefining this entity as an instance of the source
	 * entity. The sprite field is cloned in a shallow manner.
	 * 
	 * @param sourceEntity
	 * @return
	 */
	public void defineAs(Entity sourceEntity) {

		ArrayList<Attribute> attributes = Attribute.buildAttributeList(sourceEntity);
		for (Attribute a : attributes) {
			Attribute.set(this, a.attribute, a.value);
		}
		if (sourceEntity.getSprite() != null) {
			setSprite(new Sprite(sourceEntity.getSprite()));
		} else {
			String atlasRegionName = Util.pullRegionFromTexture(texture);
			sprite = new Sprite(Game.getLevel().getAtlas().findRegion(atlasRegionName));
		}

	}

	public void defineAs(ArrayList<Attribute> attributes) {
		for (Attribute a : attributes) {
			Attribute.set(this, a.attribute, a.value);
		}
	}

	/**
	 * Returns a new entity defined as an exact shallow copy of this entity.
	 */
	public Entity clone() {
		Entity e = new Entity();
		e.defineAs(this);
		return e;
	}

	@Override
	public void write(Json json) {
		json.writeValue("attributes", Attribute.buildAttributeList(this));
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		ArrayList<Attribute> list = json.readValue(ArrayList.class, jsonData.get("attributes"));
		defineAs(list);
		if (Game.getLevel().getAtlas() != null) {
			sprite = new Sprite(Game.getLevel().getAtlas().findRegion(Util.pullRegionFromTexture(texture)));
		}
	}

	public boolean isSelected() {
		return selected;
	}

	public float getFacing() {
		return facing;
	}

	public void setFacing(float newFacing) {
		facing = newFacing;
	}

	public void setSelected(boolean b) {
		selected = b;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}
}
