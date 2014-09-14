package data;

import java.util.ArrayList;

import util.Vector2;
import engine.NodeMap;

public class Node {
	public static final int MOVE_COST_CARDINAL = 10;
	public static final int MOVE_COST_DIAGONAL = 14;

	private Vector2 origin;
	private ArrayList<Node> neighbors = new ArrayList<Node>();
	private ArrayList<Node> corners = new ArrayList<Node>();
	private int pathability = NodeMap.PATH_GROUND;

	public Node(float x, float y) {
		origin = new Vector2(x, y);
	}

	public Node(Vector2 v) {
		origin = v;
	}

	public Vector2 getPosition() {
		return origin;
	}

	public ArrayList<Node> getCorners() {
		return corners;
	}

	public void setCorners(ArrayList<Node> corners) {
		this.corners = corners;
	}

	public ArrayList<Node> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(ArrayList<Node> neighbors) {
		this.neighbors = neighbors;
	}

	public int getPathability() {
		return pathability;
	}

	public void setPathability(int pathability) {
		this.pathability = pathability;
	}

	@Override
	public String toString() {
		return getPosition().toString();
	}
}
