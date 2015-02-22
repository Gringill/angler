package controller;

import model.Level;
import model.Tile;
import model.Vector2;
import model.util.Util;
import view.stages.Gameplay;

import java.awt.*;

public class TileMap {
	private static int DEFAULT_WIDTH = 160, DEFAULT_HEIGHT = 100;
	private int width = DEFAULT_WIDTH;
	private int height = DEFAULT_HEIGHT;
	private String[][] tiledef;
	private NodeMap nodemap;
	private Gameplay gameplay;
	private Level level;

	/**
	 * A 2D array of strings defining what terrain is where. Used to populate
	 * the <code>tiles</code> field.
	 */
	private Tile[][] tiles;

	public TileMap(Gameplay gameplay, Level level, int width, int height) {
		this.width = width;
		this.height = height;
		this.gameplay = gameplay;
		this.level = level;
		tiles = new Tile[width][height];
	}

	public TileMap(String[][] tiledef) {
		this.tiledef = tiledef;
		width = tiledef.length;
		height = tiledef[0].length;
	}

	public static TileMap createDefaultTileMap(Gameplay gameplay, Level level) {
		TileMap tileMap = new TileMap(gameplay, level, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		for (int y = 0; y < DEFAULT_HEIGHT; y++) {
			for (int x = 0; x < DEFAULT_WIDTH; x++) {
				tileMap.getTiles()[x][y] = Tile.createDefaultTile(gameplay.getGame(), level, x, y);
			}
		}
		return tileMap;
	}

	public void connectToGame(Gameplay gameplay) {
		level = gameplay.getLevel();
		if (tiledef == null) {
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					tiles[x][y] = Tile.createDefaultTile(gameplay.getGame(), level, x, y);
				}
			}
		} else {
			tiles = parseTiledef(gameplay, tiledef);
		}
		nodemap = new NodeMap(this);
	}

	public Tile[][] parseTiledef(Gameplay gameplay, String[][] tiledef) {
		Tile[][] tiles = new Tile[tiledef.length][tiledef[0].length];
		for (int y = 0; y < tiledef[0].length; y++) {
			for (int x = 0; x < tiledef.length; x++) {
				for (int i = 0; i < gameplay.getLevel().getTileModel().getSize(); i++) {
					Tile t = (Tile) gameplay.getLevel().getTileModel().get(i);
					if (t.getName().equals(tiledef[x][y])) {
						tiles[x][y] = new Tile(gameplay.getGame());
						tiles[x][y].setPosition(new Vector2(x * Util.getTileSize(), y * Util.getTileSize()));
						tiles[x][y].defineAs(t, t.getAttributes());
					}
				}
				if (tiles[x][y] == null) {
					tiles[x][y] = Tile.createDefaultTile(gameplay.getGame(), level, x, y);
				}
			}
		}
		return tiles;
	}

	/**
	 * Modifications to the {@link #tiledef} performed in this manner are not
	 * automatically reflected by the tilegroup, and will not be noticed
	 * visually until level reload or a manual refresh of the tilegroup is
	 * performed.
	 * 
	 * @param worldPoint
	 * @param tileType
	 *            replaces the old tile at <code>position</code>
	 */
	public void setTileAtWorldPoint(Vector2 worldPoint, Tile tileType) {
		worldPoint.snapToWorldPoint((int) Util.getTileSize());
		float x = (worldPoint.x / Util.getTileSize());
		float y = (worldPoint.y / Util.getTileSize());
		tiles[(int) x][(int) y].defineAs(tileType, tileType.getAttributes());
		x *= getNodeMap().getDensity();
		y *= getNodeMap().getDensity();
		for (float yIndex = y; yIndex < y + getNodeMap().getDensity(); yIndex++) {
			for (float xIndex = x; xIndex < x + getNodeMap().getDensity(); xIndex++) {
				nodemap.updatePathability((int) xIndex, (int) yIndex, tileType.getPathability());
			}
		}
	}

	public NodeMap getNodeMap() {
		return nodemap;
	}

	public String[][] getTiledef() {
		String[][] tiledef = new String[width][height];
		for (int y = 0; y < width; y++) {
			for (int x = 0; x < height; x++) {
				for (int i = 0; i < gameplay.getLevel().getTileModel().getSize(); i++) {
					tiledef[x][y] = tiles[x][y].getName();
				}
			}
		}
		return tiledef;
	}

	public Tile[][] getTiles() {
		return tiles;
	}

	/**
	 * Returns the dimensions of the TileMap in GRID UNITS.
	 * 
	 * @return size
	 */
	public Dimension getSize() {
		return new Dimension(width, height);
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Tile getTileAtWorldCoords(Vector2 v) {
		v.multiply(1 / Util.getTileSize());
		return getTiles()[(int) (v.x)][(int) (v.y)];
	}
}
