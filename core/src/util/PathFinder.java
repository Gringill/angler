package util;

import java.util.ArrayList;

import data.Entity;
import data.GameObject;
import data.Node;
import engine.Game;
import engine.NodeMap;

public class PathFinder {
	public static ArrayList<PathFinder> finders = new ArrayList<>();
	private Game game;
	private NodeMap nodemap;
	private Entity entity;
	private Thread thread;
	private ArrayList<Vector2> path = new ArrayList<>();
	private ArrayList<Vector2> smooth_path = new ArrayList<>();
	private ArrayList<Node> pool_open = new ArrayList<>();
	private ArrayList<Node> pool_closed = new ArrayList<>();
	private ArrayList<Node> delta_nodes = new ArrayList<>();
	private ArrayList<Node> along_path = new ArrayList<>();
	private ArrayList<Node> changed_path = new ArrayList<>();
	private Node[][] local;
	private Node[][] parent;
	private float[][] g;
	private float[][] h;
	private float[][] lowerBound;
	private float[][] upperBound;
	public boolean[][] inOpen;
	public boolean[][] inClosed;

	private float h_weight = 1;
	private Node goal;
	private Node start;
	private long time_start;
	private long time_end;

	public PathFinder(Entity entity) {
		finders.add(this);
		this.entity = entity;
		game = entity.getGame();
		nodemap = game.getLevel().getTileMap().getNodeMap();
	}

	/**
	 * Threaded incremental Phi&#42;
	 * 
	 * @param vStart
	 * @param vGoal
	 */
	public void findThreadedPath(final Vector2 vStart, final Vector2 vGoal) {
		time_start = System.currentTimeMillis();
		// if (thread != null && thread.isAlive()) {
		// Logger.log("Thread still active", 2);
		// }
		thread = (new Thread() {
			public void run() {
				if (nodemap.nodeInBounds(vStart) && nodemap.nodeInBounds(vGoal)) {
					Node first, second;
					initialize(vStart, vGoal);
					if (!nodemap.lineOfSight(entity.getPathability(), vStart, vGoal)) {
						Logger.log("LOS: false, path must be found.", 3);
						findPath(vGoal);
						path.set(0, vStart);
					} else {
						Logger.log("LOS: true, path is goal.", 3);
						path.add(vGoal);
					}
					entity.setPath(path);
					time_end = System.currentTimeMillis();
					Logger.log("Runtime: " + (time_end - time_start) / 1000f, 3);
					while (goal == null) {
						Logger.log("Incrementing.", 2);
						if (getDeltaNodes().size() > 0) {
							for (Node n : getDeltaNodes()) {
								along_path.clear();
								changed_path.clear();
								for (Node c : n.getCorners()) {
									if ((inClosed(c) || inOpen(c)) && !c.equals(start)) {
										along_path.add(c);
										while (along_path.size() > 0) {
											first = getLowestF(along_path, true);
											changed_path.add(first);
											unOpen(first);
											unClose(first);
											setG(first, Float.POSITIVE_INFINITY);
											setLowerBound(first, Float.NEGATIVE_INFINITY);
											setUpperBound(first, Float.POSITIVE_INFINITY);
											setLocal(first, null);
											setParent(first, null);
											for (Node g : first.getNeighbors()) {
												if (getLocal(g).equals(first)) {
													along_path.add(g);
												}
											}
											for (Node g : first.getCorners()) {
												if (getLocal(g).equals(first)) {
													along_path.add(g);
												}
											}
											while (changed_path.size() > 0) {
												second = getLowestF(changed_path, true);
												for (Node g : second.getNeighbors()) {
													if (inClosed(g)) {
														computePath(second, g);
													}
												}
											}
										}

									}
								}
							}
							getDeltaNodes().clear();
						}
					}
				}
			};
		});
		thread.start();
	}

	private void initialize(Vector2 vStart, Vector2 vGoal) {
		// Ensure fresh data
		local = new Node[nodemap.getWidth()][nodemap.getHeight()];
		parent = new Node[nodemap.getWidth()][nodemap.getHeight()];
		g = new float[nodemap.getWidth()][nodemap.getHeight()];
		h = new float[nodemap.getWidth()][nodemap.getHeight()];
		lowerBound = new float[nodemap.getWidth()][nodemap.getHeight()];
		upperBound = new float[nodemap.getWidth()][nodemap.getHeight()];
		inOpen = new boolean[nodemap.getWidth()][nodemap.getHeight()];
		inClosed = new boolean[nodemap.getWidth()][nodemap.getHeight()];
		along_path.clear();
		path.clear();
		changed_path.clear();
		pool_closed.clear();
		pool_open.clear();
		for (int y = 0; y < nodemap.getHeight(); y++) {
			for (int x = 0; x < nodemap.getWidth(); x++) {
				clearData(nodemap.get(x, y));
			}
		}
		goal = nodemap.get((int) vGoal.x, (int) vGoal.y);
		start = nodemap.get((int) vStart.x, (int) vStart.y);
		initializeNode(goal);
		setParent(start, start);
		setLocal(start, start);
		setG(start, 0);
		open(start);
	}

	/**
	 * Incremental Phi&#42;
	 * 
	 * @param vStart
	 * @param vGoal
	 */
	private ArrayList<Vector2> findPath(Vector2 vGoal) {
		Node current;
		Logger.log("A*", 3);
		// Incremental Phi* Logic
		while (pool_open.size() > 0) {
			Logger.log("Loop: ", 2);
			current = getLowestF(pool_open, true);
			if (current.equals(goal)) {
				buildPath(goal);
				path.set(0, start.getPosition()); // poop
				path.set(path.size() - 1, vGoal);
				Logger.log("Found path successfully.", 3);
				// smooth_path = (new PathSmoother(this)).funnel(path);
				// smoothPath.add(vGoal); TODO Path post processing
				return smooth_path;
			} else {
				close(current);
				Logger.log(current + " has " + current.getNeighbors().size() + " neighbors", 2);
				for (Node n : current.getNeighbors()) {
					if (!nodemap.isPathable(entity.getPathability(), n)) {
						Logger.log(n + " is not pathable", 2);
						close(n);
					}
					if (!inClosed(n)) {
						if (!inOpen(n)) {
							setG(n, Float.POSITIVE_INFINITY);
							open(n);
							Logger.log(n + " is not closed and was not open yet but is now", 2);
						} else {
							Logger.log(n + " is not closed but is already open", 2);
						}
						computePath(current, n);
					} else {
						Logger.log(n + " is already closed", 2);
					}
				}
			}
		}
		return null;
	}

	private void computePath(Node current, Node n) {
		Logger.log(current + " " + n + " " + getParent(current), 2);
		boolean elbowTest = elbowTest(n.getPosition(), getParent(current).getPosition());
		boolean losTest = nodemap.lineOfSight(entity.getPathability(), getParent(current).getPosition(), n.getPosition());
		float phi = phi(current.getPosition(), getParent(current).getPosition(), n.getPosition());
		float distance = Util.getDistanceBetweenPoints(getParent(current).getPosition(), n.getPosition());
		if (elbowTest && losTest && phi >= getLowerBound(current) && phi <= getUpperBound(current)) {
			if (getG(getParent(current)) + distance < getG(n)) {
				setG(n, getG(getParent(current)) + distance);
				setParent(n, getParent(current));
				setHeuristic(n, goal);
				setLocal(n, current);
				float l = Float.POSITIVE_INFINITY;
				float u = Float.NEGATIVE_INFINITY;
				for (Node n2 : n.getNeighbors()) {
					l = Math.min(l, phi(n.getPosition(), getParent(current).getPosition(), n2.getPosition()));
					u = Math.max(u, phi(n.getPosition(), getParent(current).getPosition(), n2.getPosition()));
				}
				phi = phi(current.getPosition(), getParent(current).getPosition(), n.getPosition());
				setLowerBound(n, Math.max(l, getLowerBound(current) - phi));
				setUpperBound(n, Math.min(u, getUpperBound(current) - phi));
			}
		} else if (getG(current) + (distance = Util.getDistanceBetweenPoints(current.getPosition(), n.getPosition())) <= getG(n)) {
			if (!losTest) {
				setParent(n, current);
				setG(n, getG(current) + distance);
			} else {
				setParent(n, getParent(current));
				setG(n, getG(getParent(current)) + distance);
			}
			setHeuristic(n, goal);
			setLocal(n, current);
			setLowerBound(n, -45);
			setUpperBound(n, 45);
		}
	}

	/**
	 * The angle (measured in degrees) between the rays bc and ba
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @return Positive - ba is clockwise from bc<br>
	 * <br>
	 *         Zero - ba has the same heading as bc<br>
	 * <br>
	 *         Negative - ba is counterclockwise from bc
	 */
	private float phi(Vector2 a, Vector2 b, Vector2 c) {
		float bc = Util.getAngleTowardsPoint(b, c, false);
		float ba = Util.getAngleTowardsPoint(b, a, false);
		Logger.log("b: " + b + " c: " + c + " bc: " + bc, 1);
		Logger.log("b: " + b + " a: " + a + " ba: " + ba, 1);
		Logger.log("(ba - bc): " + Math.toDegrees(ba - bc), 1);
		if (ba - bc == 0) {
			Logger.log("same heading", 1);
		} else if (ba - bc < 0) {
			Logger.log("ba should be counter clockwise of bc", 1);
		} else {
			Logger.log("ba should be clockwise of bc", 1);
		}
		return (ba - bc);
	}

	/**
	 * <i>Caution!</i> Will modify parameters, make sure to use copies of
	 * vectors that you wish to remain intact.
	 * 
	 * @param a
	 * @param b
	 * @return The smaller angle formed by the ray from b through vertex a and
	 *         the vertical line through b.
	 */
	private boolean elbowTest(Vector2 a, Vector2 b) {
		float aVert = 90;
		float aRay = Util.getAngleTowardsPoint(a, b, true);
		aVert -= aRay;
		if (aVert > 90) {
			aVert = 180 - aVert;
		}
		return aVert % 45 == 0;
	}

	public Node getLowestF(ArrayList<Node> nodes, boolean doRemove) {
		Logger.log("getLowestF()", 2);
		float lowest = Float.POSITIVE_INFINITY;
		Node bestNode = null;
		for (Node n : nodes) {
			Logger.log("Check node " + n + " F: " + getF(n, h_weight), 1);
			if (getF(n, h_weight) <= lowest) {
				lowest = getF(n, h_weight);
				bestNode = n;
			}
		}
		Logger.log("Return: " + bestNode, 2);
		unOpen(bestNode);
		if (doRemove) {
			nodes.remove(bestNode);
		}
		return bestNode;
	}

	private void initializeNode(Node n) {
		setG(n, Float.POSITIVE_INFINITY);
		setParent(n, null);
		setLocal(n, null);
		setLowerBound(n, Float.NEGATIVE_INFINITY);
		setUpperBound(n, Float.POSITIVE_INFINITY);
	}

	private void buildPath(Node currentNode) {
		if (!getParent(currentNode).equals(currentNode)) {
			buildPath(getParent(currentNode));
		}
		path.add(currentNode.getPosition());
	}

	private boolean inOpen(Node n) {
		return inOpen[(int) n.getPosition().x][(int) n.getPosition().y];
	}

	private boolean inClosed(Node n) {
		return inClosed[(int) n.getPosition().x][(int) n.getPosition().y];
	}

	private void unClose(Node u) {
		if (inClosed(u)) {
			inClosed[(int) u.getPosition().x][(int) u.getPosition().y] = false;
			pool_closed.remove(u);
		}
	}

	private void unOpen(Node u) {
		if (inOpen(u)) {
			inOpen[(int) u.getPosition().x][(int) u.getPosition().y] = false;
			pool_open.remove(u);
		}
	}

	private void open(Node n) {
		if (!inOpen(n)) {
			inOpen[(int) n.getPosition().x][(int) n.getPosition().y] = true;
			pool_open.add(n);
		}
	}

	private void close(Node n) {
		if (!inClosed(n)) {
			inClosed[(int) n.getPosition().x][(int) n.getPosition().y] = true;
			pool_closed.add(n);
		}
	}

	public GameObject getEntity() {
		return entity;
	}

	public NodeMap getNodemap() {
		return nodemap;
	}

	public void setNodeMap(NodeMap nodemap) {
		this.nodemap = nodemap;
	}

	public void setHeuristicWeight(Float f) {
		h_weight = f;
	}

	/**
	 * @return g + h
	 */
	public float getF(Node n, float weight) {
		return g[(int) n.getPosition().x][(int) n.getPosition().y] + (h[(int) n.getPosition().x][(int) n.getPosition().y] * weight);
	}

	public float getUpperBound(Node n) {
		return upperBound[(int) n.getPosition().x][(int) n.getPosition().y];
	}

	public void setUpperBound(Node n, float ub) {
		upperBound[(int) n.getPosition().x][(int) n.getPosition().y] = ub;
	}

	public float getLowerBound(Node n) {
		return lowerBound[(int) n.getPosition().x][(int) n.getPosition().y];
	}

	public void setLowerBound(Node n, float lb) {
		lowerBound[(int) n.getPosition().x][(int) n.getPosition().y] = lb;
	}

	public float getHeuristic(Node n) {
		return h[(int) n.getPosition().x][(int) n.getPosition().y];
	}

	public void setHeuristic(Node a, Node n) {
		h[(int) a.getPosition().x][(int) a.getPosition().y] = Util.getDistanceBetweenPoints(a.getPosition(), n.getPosition());
		// Vector2 a = getPosition();
		// Vector2 b = n.getPosition();
		// h = (b.x - a.x) + (b.y - a.y);
	}

	public void setH(Node n, float h) {
		this.h[(int) n.getPosition().x][(int) n.getPosition().y] = h;
	}

	public Node getParent(Node n) {
		return parent[(int) n.getPosition().x][(int) n.getPosition().y];
	}

	public void setParent(Node n, Node parent) {
		this.parent[(int) n.getPosition().x][(int) n.getPosition().y] = parent;
	}

	public Node getLocal(Node n) {
		return local[(int) n.getPosition().x][(int) n.getPosition().y];
	}

	public void setLocal(Node n, Node local) {
		this.local[(int) n.getPosition().x][(int) n.getPosition().y] = local;
	}

	public ArrayList<Node> getDeltaNodes() {
		return delta_nodes;
	}

	public void setDeltaNodes(ArrayList<Node> delta_nodes) {
		this.delta_nodes = delta_nodes;
	}

	public float getG(Node n) {
		return g[(int) n.getPosition().x][(int) n.getPosition().y];
	}

	public void setG(Node n, float g) {
		this.g[(int) n.getPosition().x][(int) n.getPosition().y] = g;
	}

	public Node deepCopy(Node n) {
		Node a = new Node(n.getPosition().copy());
		if (getParent(n) != null) {
			setParent(a, new Node(getParent(n).getPosition()));
		}
		if (local[(int) n.getPosition().x][(int) n.getPosition().y] != null) {
			setLocal(a, new Node(getLocal(n).getPosition()));
		}
		return a;
	}

	public void clearData(Node n) {
		local[(int) n.getPosition().x][(int) n.getPosition().y] = null;
		parent[(int) n.getPosition().x][(int) n.getPosition().y] = null;
		setG(n, 0);
		setH(n, 0);
		unOpen(n);
		unClose(n);
	}

	public static void changeNode(Node n) {
		for (PathFinder pf : finders) {
			pf.delta_nodes.add(n);
		}
	}

	// public static void validate(int width, int height) {
	// for (PathFinder pf : finders) {
	// pf.setNodeMap(new NodeMap(width, height, pf.getEntity()));
	// pf.getNodemap().generate(width, height);
	// }
	// }

}