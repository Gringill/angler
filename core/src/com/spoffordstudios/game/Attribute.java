package com.spoffordstudios.game;

import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.esotericsoftware.minlog.Log;
import com.spoffordstudios.editor.Editor;

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
	}
	private boolean isDisabled;
	String attribute;
	String value;

	public Attribute() {
	}

	public Attribute(String attribute, String value) {
		this.attribute = attribute;
		this.value = value;
	}

	public static ArrayList<Attribute> buildAttributeList(Entity e) {
		ArrayList<Attribute> list = new ArrayList<Attribute>();
		for (String s : ATTRIBUTES) {
			Attribute a = new Attribute(s, get(e, s));
			if (s.equals(ATTR_NAME) || s.equals(ATTR_TEXTURE) || s.equals(ATTR_BUILDING)) {
				a.disable();
			}
			list.add(a);
		}
		return list;
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

	public static String get(Entity e, String attr) {
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
		default:
			Log.error("Get attribute error on: " + attr);
			return "<Unregistered Attribute>";
		}
	}

	public static boolean set(Entity e, String attr, String val) {

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
					JOptionPane.showMessageDialog(Editor.EDITOR, "Failed to parse value, no changes commited.", "Woops", JOptionPane.WARNING_MESSAGE);
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
					JOptionPane.showMessageDialog(Editor.EDITOR, "Failed to parse value, no changes commited.", "Woops", JOptionPane.WARNING_MESSAGE);
				}

			}
			break;
		case ATTR_SIZE:
			if (val != null && val.length() > 0) {
				try {
					e.setSize(Float.valueOf(val));
					return true;
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(Editor.EDITOR, "Failed to parse value, no changes commited.", "Woops", JOptionPane.WARNING_MESSAGE);
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
					JOptionPane.showMessageDialog(Editor.EDITOR, "Failed to parse value, no changes commited.", "Woops", JOptionPane.WARNING_MESSAGE);
				}

			}
			break;
		case ATTR_FACING:
			if (val != null && val.length() > 0) {
				try {
					e.setFacing(Float.valueOf(val));
					return true;
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(Editor.EDITOR, "Failed to parse value, no changes commited.", "Woops", JOptionPane.WARNING_MESSAGE);
				}

			}
			break;
		case ATTR_WIDTH:
			if (val != null && val.length() > 0) {
				try {
					e.setWidth(Float.valueOf(val));
					return true;
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(Editor.EDITOR, "Failed to parse value, no changes commited.", "Woops", JOptionPane.WARNING_MESSAGE);
				}

			}
			break;
		case ATTR_HEIGHT:
			if (val != null && val.length() > 0) {
				try {
					e.setHeight(Float.valueOf(val));
					return true;
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(Editor.EDITOR, "Failed to parse value, no changes commited.", "Woops", JOptionPane.WARNING_MESSAGE);
				}

			}
			break;
		default:
			return false;
		}
		return false;
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
