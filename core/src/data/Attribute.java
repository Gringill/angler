package data;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.esotericsoftware.minlog.Log;
import util.Vector2;

import javax.swing.*;
import java.util.ArrayList;

public class Attribute extends JLabel implements Serializable {
	public static final String ATTR_NAME = "Name";
	public static final String ATTR_HEALTH = "Health";
	public static final String ATTR_TEXTURE = "Texture";
	public static final String ATTR_BUILDING = "Is a Building";
	public static final String ATTR_SIZE = "Size";
	public static final String ATTR_POSITION = "Position";
	public static final String ATTR_FACING = "Facing";
	public static final String ATTR_WIDTH = "Width";
	public static final String ATTR_HEIGHT = "Height";
	public static final String ATTR_MOVE_SPEED = "Move Speed";
	public static final String ATTR_PATHABILITY = "Pathability";
	public static final String ATTR_COLOR = "Color";
	public static final ArrayList<String> ATTRIBUTES = new ArrayList<String>();
	static {
		ATTRIBUTES.add(ATTR_NAME);
		ATTRIBUTES.add(ATTR_HEALTH);
		ATTRIBUTES.add(ATTR_TEXTURE);
		ATTRIBUTES.add(ATTR_BUILDING);
		ATTRIBUTES.add(ATTR_SIZE);
		ATTRIBUTES.add(ATTR_POSITION);
		ATTRIBUTES.add(ATTR_FACING);
		ATTRIBUTES.add(ATTR_WIDTH);
		ATTRIBUTES.add(ATTR_HEIGHT);
		ATTRIBUTES.add(ATTR_MOVE_SPEED);
		ATTRIBUTES.add(ATTR_PATHABILITY);
		ATTRIBUTES.add(ATTR_COLOR);
	}
	String attribute;
	String value;
	private boolean isDisabled;

	public Attribute() {
	}

	public Attribute(String attribute, String value) {
		this.attribute = attribute;
		this.value = value;
	}

	public static ArrayList<Attribute> buildAttributeList(GameObject e, ArrayList<String> attributes) {
		ArrayList<Attribute> list = new ArrayList<Attribute>();
		Attribute a;
		for (String s : ATTRIBUTES) {
			if (attributes.contains(s)) {
				list.add(new Attribute(s, get(e, s)));
			}
		}
		return list;
	}

	public static String get(GameObject e, String attr) {
		switch (attr) {
		case ATTR_NAME:
			return e.getName();
		case ATTR_HEALTH:
			return String.valueOf(e.getHealth());
		case ATTR_TEXTURE:
			return e.getTexture();
		case ATTR_BUILDING:
			return String.valueOf(e.isBuilding());
		case ATTR_SIZE:
			return String.valueOf(e.getSize());
		case ATTR_POSITION:
			return String.valueOf(e.getPosition().x + "," + e.getPosition().y);
		case ATTR_FACING:
			return String.valueOf(e.getFacing());
		case ATTR_WIDTH:
			return String.valueOf(e.getWidth());
		case ATTR_HEIGHT:
			return String.valueOf(e.getHeight());
		case ATTR_MOVE_SPEED:
			return String.valueOf(((Entity) e).getMoveSpeed());
		case ATTR_PATHABILITY:
			return String.valueOf(e.getPathability());
		case ATTR_COLOR:
			if (e instanceof Tile) {
				Color c = ((Tile) e).getColor();
				return (int) (c.r * 255) + ", " + (int) (c.g * 255) + ", " + (int) (c.b * 255) + ", " + (int) (c.a * 255);
			} else {
				Log.error("Get attribute error on: " + attr + "/n-> Attempted to change color of non-tile entity.");
			}
			return "";
		default:
			Log.error("Get attribute error on: " + attr + "/n-> Unregistered attribute.");
			return "<Unregistered attribute>";
		}
	}

	public static boolean set(GameObject e, String attr, String val) {
		switch (attr) {
		case ATTR_NAME:
			e.setName(val);
			return true;
		case ATTR_HEALTH:
			if (val != null && val.length() > 0) {
				try {
					e.setHealth(Float.valueOf(val));
					return true;
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null, "Failed to parse health, no changes commited.", "Woops", JOptionPane.WARNING_MESSAGE);
				}
			}
			break;
		case ATTR_TEXTURE:
			e.setTexture(val);
			return true;
		case ATTR_BUILDING:
			if (val != null && val.length() > 0) {
				try {
					e.setBuilding(Boolean.valueOf(val));
					return true;
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null, "Failed to parse building, no changes commited.", "Woops", JOptionPane.WARNING_MESSAGE);
				}

			}
			break;
		case ATTR_SIZE:
			if (val != null && val.length() > 0) {
				try {
					e.setSize(Float.valueOf(val));
					return true;
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null, "Failed to parse size, no changes commited.", "Woops", JOptionPane.WARNING_MESSAGE);
				}

			}
			break;
		case ATTR_POSITION:
			if (val != null && val.length() > 0) {
				try {
					String[] splitVal = val.split(",");
					e.setPosition(new Vector2(Float.valueOf(splitVal[0]), Float.valueOf(splitVal[1])));
					return true;
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null, "Failed to parse position, no changes commited.", "Woops", JOptionPane.WARNING_MESSAGE);
				}

			}
			break;
		case ATTR_FACING:
			if (val != null && val.length() > 0) {
				try {
					e.setFacing(Float.valueOf(val));
					return true;
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null, "Failed to parse facing, no changes commited.", "Woops", JOptionPane.WARNING_MESSAGE);
				}

			}
			break;
		case ATTR_WIDTH:
			if (val != null && val.length() > 0) {
				try {
					e.setWidth(Float.valueOf(val));
					return true;
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null, "Failed to parse width, no changes commited.", "Woops", JOptionPane.WARNING_MESSAGE);
				}
			}
			break;
		case ATTR_HEIGHT:
			if (val != null && val.length() > 0) {
				try {
					e.setHeight(Float.valueOf(val));
					return true;
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null, "Failed to parse height, no changes commited.", "Woops", JOptionPane.WARNING_MESSAGE);
				}
			}
			break;
		case ATTR_MOVE_SPEED:
			if (val != null && val.length() > 0) {
				try {
					((Entity) e).setMoveSpeed(Float.valueOf(val));
					return true;
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null, "Failed to parse move speed, no changes commited.", "Woops", JOptionPane.WARNING_MESSAGE);
				}
			}
			break;
		case ATTR_PATHABILITY:
			if (val != null && val.length() > 0) {
				try {
					e.setPathability(Float.valueOf(val));
					return true;
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null, "Failed to parse pathability from val: " + val, "Woops", JOptionPane.WARNING_MESSAGE);
				}
			}
			break;
		case ATTR_COLOR:
			if (e instanceof Tile) {
				try {
					String[] channels = val.split(", ");
					int r = Integer.valueOf(channels[0]);
					int g = Integer.valueOf(channels[1]);
					int b = Integer.valueOf(channels[2]);
					int a = Integer.valueOf(channels[3]);
					((Tile) e).setColor(new Color(r, g, b, a));
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null, "Failed to parse color, no changes commited.", "Woops", JOptionPane.WARNING_MESSAGE);
				}
			}
			break;
		default:
			return false;
		}
		return false;
	}

	public String setAttribute(String attr) {
		return attribute = attr;
	}

	public String setValue(String val) {
		return value = val;
	}

	public String getAttribute() {
		return attribute;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return attribute + " - " + value;
	}

	public boolean isDisabled() {
		return isDisabled;
	}

	public void disable() {
		isDisabled = true;
	}

	@Override
	public void write(Json json) {
		json.writeValue("attribute", attribute);
		json.writeValue("value", value);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		attribute = json.readValue("attribute", String.class, jsonData);
		value = json.readValue("value", String.class, jsonData);
	}
}
