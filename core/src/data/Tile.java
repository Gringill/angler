package data;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.esotericsoftware.minlog.Log;
import engine.Game;
import engine.NodeMap;
import gui.screens.GamePlay;
import util.Util;
import util.Vector2;

import java.util.ArrayList;

public class Tile extends KineticObject {
	public static final String DEFAULT_TEXTURE = "grass.png";
	public static final String PATH_DEFAULT = "bin/unpacked/";

	private Color color = new Color(0, 75, 0, 255);

	public Tile() {
		buildAttributes();
	}

	public Tile(Game game) {
		buildAttributes();
		setGame(game);
	}

	public Tile(Game game, String tileName, Sprite s, int x, int y) {
		buildAttributes();
		setGame(game);
		setSprite(s);
		setName(tileName);
		setPosition(new Vector2(x * game.getUtil().getTileSize(), y * game.getUtil().getTileSize()));

		if (s != null) {
			getSprite().setSize(game.getUtil().getTileSize(), game.getUtil().getTileSize());
		}
	}

	public static Tile createDefaultTile(Game game, Level level, int x, int y) {
		Sprite s = new Sprite(level.getAtlas().findRegion(Util.removeExtention(DEFAULT_TEXTURE)));
		Tile t = new Tile(game, Util.removeExtention(DEFAULT_TEXTURE), s, x, y);
		Attribute.set(t, Attribute.ATTR_TEXTURE, PATH_DEFAULT + DEFAULT_TEXTURE);
		t.generatePhysicsBody();
		return t;
	}

	@Override
	public void draw(SpriteBatch batch) {
		float sWidth = getGame().getUtil().getTileSize();
		float sHeight = getGame().getUtil().getTileSize();
		batch.draw(getSprite(), (getX()), (getY()), (sWidth / 2), (sHeight / 2), sWidth, sHeight, 1, 1, getFacing().angle());
	}

	@Override
	public void drawSelection(SpriteBatch batch) {
		float sWidth = getGame().getUtil().getTileSize();
		float sHeight = getGame().getUtil().getTileSize();
		Sprite sprite = new Sprite(getGame().getLevel().getAtlas().findRegion("white_pixel"));
		Color oldColor = batch.getColor();
		batch.setColor(1, 1, 1, .30f);
		batch.draw(sprite, (getX()), (getY()), (sWidth / 2), (sHeight / 2), sWidth, sHeight, 1, 1, getFacing().angle());
		batch.setColor(oldColor);
	}

	public void defineAs(ArrayList<Attribute> attributes) {
		for (Attribute a : attributes) {
			Attribute.set(this, a.getAttribute(), a.getValue());
		}
	}

	/**
	 * Pulls all attributes from the source tile and puts them into this tile,
	 * effectively redefining this tile as an instance of the source tile. The
	 * sprite field is cloned in a shallow manner.
	 *
	 * @param sourceTile
	 * @return
	 */
	public void defineAs(Tile sourceTile, ArrayList<String> atttributes) {
		ArrayList<Attribute> attributes = Attribute.buildAttributeList(sourceTile, atttributes);
		for (Attribute a : attributes) {
			Attribute.set(this, a.getAttribute(), a.getValue());
		}
		String atlasRegionName = Util.pullRegionFromTexture(getTexture());
		setSprite(new Sprite(getGame().getLevel().getAtlas().findRegion(atlasRegionName)));
		if (getBody() != null) {
			getGame().getWorld().destroyBody(getBody());
		}
		generatePhysicsBody();
	}

	public void buildAttributes() {
		myAttributes.clear();
		myAttributes.add(Attribute.ATTR_TEXTURE);
		myAttributes.add(Attribute.ATTR_COLOR);
		myAttributes.add(Attribute.ATTR_PATHABILITY);
		myAttributes.add(Attribute.ATTR_NAME);
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color newColor) {
		color = newColor;
	}

	@Override
	public void generatePhysicsBody() {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set((int) getX() / getGame().getUtil().getTileSize() + .5f, (int) getY() / getGame().getUtil().getTileSize() + .5f);
		setBody(getGame().getWorld().createBody(bodyDef));
		getBody().setSleepingAllowed(false);
		PolygonShape poly = new PolygonShape();
		poly.setAsBox(.5f, .5f);
		FixtureDef fixtureDef = new FixtureDef();
		switch (getPathability()) {
		case NodeMap.PATH_SWIMMING:
			fixtureDef.filter.categoryBits = Util.CATEGORY_WATER;
			fixtureDef.filter.maskBits = Util.MASK_CLIP;
			break;
		case NodeMap.PATH_GROUND:
			fixtureDef.filter.categoryBits = Util.CATEGORY_GROUND;
			fixtureDef.filter.maskBits = Util.MASK_NOCLIP;
			break;
		default:
			fixtureDef.filter.categoryBits = Util.CATEGORY_GROUND;
			fixtureDef.filter.maskBits = Util.MASK_NOCLIP;
			break;
		}
		fixtureDef.shape = poly;
		fixtureDef.density = 1;
		fixtureDef.friction = 0.4f;
		getBody().createFixture(fixtureDef).setUserData(this);
		poly.dispose();
	}

	public void update() {
		// Important that this is here to override basic update functionality.
	}

	public float getX() {
		return getPosition().x;
	}

	public float getY() {
		return getPosition().y;
	}

	@Override
	public void write(Json json) {
		json.writeValue("attributes", Attribute.buildAttributeList(this, myAttributes));
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		ArrayList<?> rawList = json.readValue(ArrayList.class, jsonData.get("attributes"));
		ArrayList<Attribute> attrList = new ArrayList<Attribute>();
		for (Object o : rawList) {
			if (o instanceof Attribute) {
				attrList.add((Attribute) o);
			} else {
				Log.debug("Failed reading tile attribute.");
			}
		}
		defineAs(attrList);
	}

	/**
	 * Returns a new entity defined as an exact shallow copy of this entity.
	 */
	public Tile clone() {
		Tile t = new Tile(getGame());
		t.defineAs(this);
		return t;
	}

}
