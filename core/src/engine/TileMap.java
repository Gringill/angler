package engine;

import java.awt.Dimension;
import java.util.ArrayList;

import util.Logger;
import util.Vector2;
import data.Tile;

public class TileMap {
	private static int DEFAULT_WIDTH = 25, DEFAULT_HEIGHT = 25;
	private int width = DEFAULT_WIDTH;
	private int height = DEFAULT_HEIGHT;
	private String[][] tiledef;
	private NodeMap nodemap;
	private Game game;

	/**
	 * A 2D array of strings defining what terrain is where. Used to populate
	 * the <code>tiles</code> field.
	 */
	private Tile[][] tiles;

	public TileMap(int width, int height) {
		this.width = width;
		this.height = height;
		tiles = new Tile[width][height];
	}

	public TileMap(String[][] tiledef) {
		this.tiledef = tiledef;
		width = tiledef.length;
		height = tiledef[0].length;
	}

	public static TileMap createDefaultTileMap() {
		return new TileMap(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	public void connectToGame(Game game) {
		this.game = game;
		if (tiledef == null) {
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					tiles[x][y] = Tile.createDefaultTile(game, x, y);
				}
			}
		} else {
			tiles = parseTiledef(tiledef);
		}
		nodemap = new NodeMap(this);
	}

	public Tile[][] parseTiledef(String[][] tiledef) {
		Tile[][] tiles = new Tile[tiledef.length][tiledef[0].length];
		for (int y = 0; y < tiledef[0].length; y++) {
			for (int x = 0; x < tiledef.length; x++) {
				for (int i = 0; i < game.getLevel().getTileModel().getSize(); i++) {
					Tile t = (Tile) game.getLevel().getTileModel().get(i);
					if (t.getName().equals(tiledef[x][y])) {
						tiles[x][y] = new Tile(game);
						tiles[x][y].setPosition(new Vector2(x * game.getUtil().getGameScale(), y * game.getUtil().getGameScale()));
						tiles[x][y].defineAs(t, t.getAttributes());
					}
				}
				if (tiles[x][y] == null) {
					tiles[x][y] = Tile.createDefaultTile(game, x, y);
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
		worldPoint.snapToWorldPoint((int) game.getUtil().getGameScale());
		float x = (worldPoint.x / game.getUtil().getGameScale());
		float y = (worldPoint.y / game.getUtil().getGameScale());
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
				for (int i = 0; i < game.getLevel().getTileModel().getSize(); i++) {
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
		v.multiply(1 / game.getUtil().getGameScale());
		return getTiles()[(int) (v.x)][(int) (v.y)];
	}
}
