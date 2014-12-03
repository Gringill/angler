package util;

import data.Entity;
import data.GameObject;
import data.Node;
import engine.Game;
import engine.NodeMap;

import java.util.ArrayList;

public class PathFinder {
    public static ArrayList<PathFinder> finders = new ArrayList<>();
    public boolean[][] inOpen;
    public boolean[][] inClosed;
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
    private double[][] g;
    private float[][] h;
    private float[][] lowerBound;
    private float[][] upperBound;
    private float h_weight = 1;
    private Vector2 vStart;
    private Vector2 vGoal;
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

    public static void changeNode(Node n) {
        for (PathFinder pf : finders) {
            pf.delta_nodes.add(n);
        }
    }

    /**
     * Threaded incremental Phi&#42;
     *
     * @param vStart in nodemap coordinates
     * @param vGoal  in nodemap coordinates
     */
    public void findThreadedPath(final Vector2 vStart, final Vector2 vGoal) {
        thread = (new Thread() {
            public void run() {
                initialize(vStart, vGoal);
                if (!nodemap.lineOfSight(entity.getPathability(), vStart, vGoal)) {
                    findPath();
                } else {
                    path.add(vGoal.multiply(50 / nodemap.getDensity()));
                }
                time_start = System.currentTimeMillis();

                if (nodemap.pointInBounds(vStart) && nodemap.pointInBounds(vGoal)) {
                    Node first, second;
                    entity.setPath(path);
                    time_end = System.currentTimeMillis();
                    Logger.log("Runtime: " + (time_end - time_start) / 1000f, "PathFinder", false);
                    while (goal == null) {
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
                            getDeltaNodes().clear();
                        }
                    }
                }
            }

            ;
        });
        thread.start();
    }

    private void initialize(Vector2 vStart, Vector2 vGoal) {
        PathFinder.this.vStart = vStart;
        PathFinder.this.vGoal = vGoal;
        local = new Node[nodemap.getWidth()][nodemap.getHeight()];
        parent = new Node[nodemap.getWidth()][nodemap.getHeight()];
        g = new double[nodemap.getWidth()][nodemap.getHeight()];
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
        Logger.log("goal: " + vGoal, "PathFinder", false);
        goal = nodemap.get((int) (vGoal.x), (int) (vGoal.y));
        start = nodemap.get((int) (vStart.x), (int) (vStart.y));
        initializeNode(goal);
        initializeNode(start);
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
    private ArrayList<Vector2> findPath() {
        Node current;
        Logger.log("A* Start: " + vStart + " to End: " + vGoal, "", false);
        // Incremental Phi* Logic
        while (pool_open.size() > 0) {
            Logger.log("Loop: ", "PathFinder", false);
            current = getLowestF(pool_open, true);
            if (current.equals(goal)) {
                buildPath(goal);

                path.set(0, vStart.copy().multiply((getEntity().getGame().getUtil().getGameScale() / getNodemap().getDensity())));
                // path.set(path.size() - 1, vGoal.copy().multiply((1f /
                // getNodemap().getDensity() * 2)));
                Logger.log("Found path successfully . . . Now lets smooth", "PathFinder", false);
                return path;
            } else {
                close(current);
                Logger.log(current + " has " + current.getNeighbors().size() + " neighbors", "neighbors", false);
                for (Node n : current.getNeighbors()) {
                    if (!nodemap.isNodePathable(entity.getPathability(), n)) {
                        close(n);
                    }
                    if (!inClosed(n)) {
                        if (!inOpen(n)) {
                            initializeNode(n);
                            open(n);
                            Logger.log(n + " is not closed and was not open yet but is now", "neighbors", false);
                        } else {
                            Logger.log(n + " is not closed but is already open", "neighbors", false);
                        }
                        computePath(current, n);
                    } else {
                        Logger.log(n + " is already closed", "neighbors", false);
                    }
                }
            }
        }
        return null;
    }

    private void computePath(Node current, Node n) {
        // boolean elbowTest = elbowTest(n.getPosition(),
        // getParent(current).getPosition());
        boolean losTest = nodemap.lineOfSight(entity.getPathability(), getParent(current).getPosition(), n.getPosition());
        double phi = phi(current.getPosition(), getParent(current).getPosition(), n.getPosition());
        double distance = Util.getDistanceBetweenPoints(getParent(current).getPosition(), n.getPosition());
        Logger.log("LOS from " + getParent(current).getPosition() + " to " + n.getPosition() + ": " + losTest, "computePath", false);
        Logger.log("Phi: " + phi, "computePath", false);
        Logger.log("Distance: " + distance, "computePath", false);
        if (true && losTest && phi >= getLowerBound(current) && phi <= getUpperBound(current)) {
            Logger.log("Passed losTest && phi", "computePath", false);
            if (getG(getParent(current)) + distance < getG(n)) {
                Logger.log("Passed distnace", "computePath", false);
                if ((n.getPosition().x == 2 && n.getPosition().y == 0) || (n.getPosition().x == 3 && n.getPosition().y == 1)) {
                    Logger.log("set", "PathFinder", false);
                }
                setG(n, getG(getParent(current)) + distance);
                Logger.log("Set " + n + "'s parent to " + getParent(current), "computePath", false);
                setParent(n, getParent(current));
                setHeuristic(n, goal);
                setLocal(n, current);
                float l = Float.POSITIVE_INFINITY;
                float u = Float.NEGATIVE_INFINITY;
                for (Node n2 : n.getNeighbors()) {
                    l = Math.min(l, (float) phi(n.getPosition(), getParent(current).getPosition(), n2.getPosition()));
                    u = Math.max(u, (float) phi(n.getPosition(), getParent(current).getPosition(), n2.getPosition()));
                }
                phi = phi(current.getPosition(), getParent(current).getPosition(), n.getPosition());
                setLowerBound(n, Math.max(l, (float) (getLowerBound(current) - phi)));
                setUpperBound(n, Math.min(u, (float) (getUpperBound(current) - phi)));
            }
        } else if (getG(current) + (distance = Util.getDistanceBetweenPoints(current.getPosition(), n.getPosition())) <= getG(n)) {
            if ((n.getPosition().x == 2 && n.getPosition().y == 0) || (n.getPosition().x == 3 && n.getPosition().y == 1)) {
                Logger.log("distance between " + current.getPosition() + " and " + n.getPosition() + ": " + distance, "PathFinder", false);
                Logger.log("g(" + n + ") = g(" + current + ")" + getG(current) + " " + distance + " = " + (getG(current) + distance), "PathFinder", false);
            }
            Logger.log("Set " + n + "'s parent to " + current, "computePath", false);
            setParent(n, current);
            setG(n, getG(current) + distance);
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
     * Zero - ba has the same heading as bc<br>
     * <br>
     * Negative - ba is counterclockwise from bc
     */
    private double phi(Vector2 a, Vector2 b, Vector2 c) {
        double bc = b.getAngleTowards(c);
        double ba = b.getAngleTowards(a);
        return (ba - bc);
    }

    /**
     * <i>Caution!</i> Will modify parameters, make sure to use copies of
     * vectors that you wish to remain intact.
     *
     * @param a
     * @param b
     * @return The smaller angle formed by the ray from b through vertex a and
     * the vertical line through b.
     */
    private boolean elbowTest(Vector2 a, Vector2 b) {
        double aVert = 90;
        double aRay = a.getAngleTowards(b);
        aVert -= aRay;
        if (aVert > 90) {
            aVert = 180 - aVert;
        }
        return aVert % 45 == 0;
    }

    public Node getLowestF(ArrayList<Node> nodes, boolean doRemove) {
        Logger.log("getLowestF()", "getLowestF", false);
        double lowest = Float.POSITIVE_INFINITY;
        Node bestNode = null;
        for (Node n : nodes) {
            Logger.log("Check node " + n + " F: " + getF(n, h_weight) + " G: " + getG(n) + " H: " + getH(n), "Check node", false);
            if (getF(n, h_weight) <= lowest) {
                lowest = getF(n, h_weight);
                bestNode = n;
            }
        }
        Logger.log("Return: " + bestNode, "Return", false);
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
        path.add(currentNode.getPosition().copy().multiply((50 / nodemap.getDensity())));
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
    public double getF(Node n, float weight) {
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

    public float getH(Node n) {
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

    public double getG(Node n) {
        return g[(int) n.getPosition().x][(int) n.getPosition().y];
    }

    public void setG(Node n, double g) {
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

    // public static void validate(int width, int height) {
    // for (PathFinder pf : finders) {
    // pf.setNodeMap(new NodeMap(width, height, pf.getEntity()));
    // pf.getNodemap().generate(width, height);
    // }
    // }

}