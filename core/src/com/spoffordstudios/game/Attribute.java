package com.spoffordstudios.game;

import java.util.ArrayList;

import com.esotericsoftware.minlog.Log;

public class Attribute {
	public static final String ATTR_NAME = "Name";
	public static final String ATTR_HEALTH = "Health";
	public static final String ATTR_TEXTURE = "Texture";
	public static final String ATTR_BUILDING = "Is a Building";
	public static final String ATTR_SIZE = "Size";
	public static final ArrayList<String> ATTRIBUTES = new ArrayList<String>();
	static {
		ATTRIBUTES.add(ATTR_NAME);
		ATTRIBUTES.add(ATTR_HEALTH);
		ATTRIBUTES.add(ATTR_TEXTURE);
		ATTRIBUTES.add(ATTR_BUILDING);
		ATTRIBUTES.add(ATTR_SIZE);
	}
	String attribute;
	String value;

	public Attribute(String attribute, String value) {
		this.attribute = attribute;
		this.value = value;
	}

	public static ArrayList<Attribute> build(Entity e) {
		ArrayList<Attribute> list = new ArrayList<Attribute>();
		for (String s : ATTRIBUTES) {
			list.add(new Attribute(s, get(e, s)));
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
		default:
			Log.error("Get attribute error on: " + attr);
			return "<Unregistered Attribute>";
		}
	}

	public static void set(Entity e, String attr, String val) {
		switch (attr) {
		case ATTR_NAME:
			e.setName(val);
			break;
		case ATTR_HEALTH:
			e.setHealth(Float.valueOf(val));
			break;
		case ATTR_TEXTURE:
			e.setTexture(val);
			break;
		case ATTR_BUILDING:
			e.setBuilding(Boolean.valueOf(val));
			break;
		case ATTR_SIZE:
			e.setSize(Float.valueOf(val));
			break;
		default:
			Log.error("Set attribute error on: " + attr);
			break;
		}
	}
}
