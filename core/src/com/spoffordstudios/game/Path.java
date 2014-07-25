package com.spoffordstudios.game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Path {
	private static final int MOVE_COST = 10;
	private static final int DIAG_MOVE_COST = 14;
	final static int[] xOffsets = { 0, 1, 1, 1, 0, -1, -1, -1 };
	final static int[] yOffsets = { 1, 1, 0, -1, -1, -1, 0, 1 };
	private Node[][] nodemap;
	private int width;
	private int height;

	public Path(int width, int height) {
		this.width = width;
		this.height = height;
		nodemap = new Node[width][height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				nodemap[x][y] = new Node(new Point(x, y));
			}
		}
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				setNeighbors(nodemap[x][y], nodemap);
			}
		}
	}

	public ArrayList<Node> aStar(Node start, Node goal) {
		ArrayList<Node> cameFrom = new ArrayList<Node>();
		ArrayList<Node> openSet = new ArrayList<Node>();
		ArrayList<Node> closedSet = new ArrayList<Node>();
		Node current;
		int tempGScore;

		openSet.add(start);
		start.g_score = 0;
		start.h_score = getH(start, goal);
		start.parent = start;
		while (openSet.size() > 0) {
			current = getLowestF(openSet);
			if (current == goal) {
				return buildPath(cameFrom, goal);
			}

			openSet.remove(current);
			closedSet.add(current);
			System.out.println(current);
			System.out.println(current.neighbors);
			for (Node n : current.neighbors) {
				if (closedSet.contains(n)) { // Or not walkable etc.
					continue;
				}

				tempGScore = current.g_score + getH(current, n);
				if (openSet.contains(n)) {
					if (tempGScore < n.g_score) {
						n.parent = current;
						n.g_score = tempGScore;
						n.h_score = getH(n, goal);
					}
				} else {
					openSet.add(n);
					n.parent = current;
					n.g_score = tempGScore;
					n.h_score = getH(n, goal);
				}
			}
		}
		return null;
	}

	public ArrayList<Node> buildPath(ArrayList<Node> pathToHere, Node current) {
		if (current.parent != current) {
			pathToHere = buildPath(pathToHere, current.parent);
			pathToHere.add(current);
			return pathToHere;
		} else {
			pathToHere.add(current);
			return pathToHere;
		}
	}

	public int getH(Node a, Node b) {

		int dx = Math.abs(a.x - b.x);
		int dy = Math.abs(a.y - b.y);
		return MOVE_COST * (dx + dy);
	}

	public Node getLowestF(ArrayList<Node> nodes) {
		int lowest = 999999999;
		Node node = null;
		for (Node n : nodes) {
			if (n.getF() < lowest) {
				lowest = n.getF();
				node = n;
			}
		}
		return node;
	}

	public boolean inBounds(Point p) {
		if (p.x >= 0 && p.x < width && p.y >= 0 && p.y < height)
			return true;
		return false;
	}

	public void setNeighbors(Node node, Node[][] nodes) {
		for (int i = 0; i < 8; i++) {
			Point neighborPoint = new Point(node.x + xOffsets[i], node.y + yOffsets[i]);
			if (inBounds(neighborPoint)) {
				node.neighbors.add(nodes[neighborPoint.x][neighborPoint.y]);
			}
		}
	}

	// For testing
	public static void main(String[] args) {
		Path p = new Path(20, 20);
		final ArrayList<Node> steps = p.aStar(p.nodemap[0][19], p.nodemap[0][0]);
		JFrame frame = new JFrame("Test A*");
		JPanel content = new JPanel() {
			@Override
			public void paint(Graphics g) {
				for (int y = 0; y < 20; y++) {
					for (int x = 0; x < 20; x++) {
						g.drawRect(x * 10, (20 * 10) - (y * 10), 8, 8);
					}
				}
				g.setColor(Color.green);
				for (Node n : steps) {
					g.drawRect(n.x * 10, (20 * 10) - (n.y * 10), 8, 8);
				}
			}
		};
		frame.setContentPane(content);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(200, 200, 300, 300);
		frame.setVisible(true);
	}
}