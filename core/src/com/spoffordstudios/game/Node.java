package com.spoffordstudios.game;

import java.awt.Point;
import java.util.ArrayList;

public class Node {
	ArrayList<Node> neighbors = new ArrayList<Node>();
	int x;
	int y;
	Node parent;
	int g_score = 10;
	int h_score;

	public Node(Point p) {
		x = p.x;
		y = p.y;
	}

	public Node(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getF() {
		return g_score + h_score;
	}
}