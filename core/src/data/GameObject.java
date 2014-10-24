package data;

import java.util.ArrayList;

import util.Util;
import util.Vector2;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import engine.Game;
import engine.NodeMap;

public abstract class GameObject implements Json.Serializable {
	public final ArrayList<String> myAttributes = new ArrayList<String>();
	private Game game;
	private String name;
	private Vector2 position = new Vector2(0, 0);
	private Vector2 facing = new Vector2();
	private Sprite sprite;
	private String texture = "bin/unpacked/cam.png";
	private float health = 100;
	private float size = 25;
	private float width = 25;
	private float height = 25;
	private boolean isBuilding;
	private boolean selected;
	private int pathability = NodeMap.PATH_GROUND;

	public GameObject(Game game, String name, Sprite sprite, float x, float y) {
		this.game = game;
		this.sprite = sprite;
		this.name = name;
		setPosition(new Vector2(x, y));
	}

	public GameObject(Game game, String name, float x, float y) {
		this.game = game;
		String file = util.Util.pullRegionFromTexture(texture);
		this.sprite = new Sprite(game.getLevel().getAtlas().findRegion(file));
		this.name = name;
		setPosition(new Vector2(x, y));
	}

	public GameObject(Game game) {
		this.game = game;
	}

	GameObject() {

	}

	public abstract void update();

	public abstract void draw(SpriteBatch batch);

	public abstract void drawSelection(SpriteBatch batch);

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
		return position.copy();
	}

	public void setPosition(Vector2 newPosition) {
		position = newPosition.copy();
	}

	/**
	 * Pulls all attributes from the source entity and puts them into this
	 * entity, effectively redefining this entity as an instance of the source
	 * entity. The sprite field is cloned in a shallow manner.
	 * 
	 * @param sourceEntity
	 * @return
	 */
	public void defineAs(GameObject sourceEntity) {
		ArrayList<Attribute> attributes = Attribute.buildAttributeList(sourceEntity, myAttributes);
		for (Attribute a : attributes) {
			Attribute.set(this, a.getAttribute(), a.getValue());
		}
		if (sourceEntity.getSprite() != null) {
			setSprite(new Sprite(sourceEntity.getSprite()));
		} else {
			String atlasRegionName = Util.pullRegionFromTexture(texture);
			sprite = new Sprite(game.getLevel().getAtlas().findRegion(atlasRegionName));
		}
	}

	public void defineAs(ArrayList<Attribute> attributes) {
		for (Attribute a : attributes) {
			Attribute.set(this, a.getAttribute(), a.getValue());
		}
	}

	/**
	 * Returns a new entity defined as an exact shallow copy of this entity.
	 */
	public abstract GameObject clone();

	public boolean isSelected() {
		return selected;
	}

	public abstract void buildAttributes();

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Vector2 getFacing() {
		return facing.copy();
	}

	public void setFacing(double newFacing) {
		facing.setAngleRad((float) newFacing);
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

	public int getPathability() {
		return pathability;
	}

	public void setPathability(float newPathability) {
		pathability = (int) newPathability;
	}

	public ArrayList<String> getAttributes() {
		return myAttributes;
	}

	@Override
	public void write(Json json) {
		json.writeValue("attributes", Attribute.buildAttributeList(this, myAttributes));
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		ArrayList<Attribute> list = json.readValue(ArrayList.class, jsonData.get("attributes"));
		defineAs(list);
		if (game.getLevel() != null) {
			sprite = new Sprite(game.getLevel().getAtlas().findRegion(Util.pullRegionFromTexture(texture)));
		}
	}
}
