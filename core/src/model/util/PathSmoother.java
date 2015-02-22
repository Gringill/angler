package model.util;

import controller.Game;
import model.Vector2;

import java.util.ArrayList;

public class PathSmoother {
    ArrayList<Vector2> smooth_path = new ArrayList<>();
    private float offset;
    private Vector2 apex;
    private ArrayList<Vector2> leftVertices = new ArrayList<>();
    private ArrayList<Vector2> rightVertices = new ArrayList<>();
    private int leftIndex;
    private int rightIndex;
    private PathFinder path_finder;
    private Game game;

    public PathSmoother(PathFinder path_finder, Game game) {
        this.game = game;
        this.path_finder = path_finder;
        offset = path_finder.getEntity().getSize() / 2f / Util.getTileSize();
    }

    public static int getLinePointRelationship(Vector2 segmentStart, Vector2 segmentEnd, Vector2 v) {
        float value = (segmentEnd.x - segmentStart.x) * (v.y - segmentStart.y) - (segmentEnd.y - segmentStart.y) * (v.x - segmentStart.x);
        if (value < 0) { // Point is right of line
            return 1;
        } else if (value > 0) { // Point is left of line
            return -1;
        } else { // Point is on line
            return 0;
        }
    }

    public ArrayList<Vector2> funnel(int pathability, ArrayList<Vector2> path) {
        ArrayList<Vector2[]> portals = buildPortals(path);

        leftIndex = 0;
        rightIndex = 1;

        String text = "Portals: ";
        for (Vector2[] portal : portals) {
            text += " [" + portal[0] + ", " + portal[1] + "]";
            leftVertices.add(portal[0]);
            rightVertices.add(portal[1]);
        }
        Logger.log(text, "smooth", false);
        apex = path.get(0);
        smooth_path.add(apex);

        for (int i = 2; i < rightVertices.size(); i++) {
            updateFunnel(false, i - 1, i);
            updateFunnel(true, i, i);
        }
        // simplify(pathability, smooth_path);
        smooth_path.add(path.get(path.size() - 1));
        return smooth_path;
    }

    private void simplify(int pathability, ArrayList<Vector2> path) {
        Vector2 v, v2;
        int a = 0;
        for (int i = 0; i < path.size() - 2; i++) {
            v = path.get(a);
            v2 = path.get(i + 2);
            if (path_finder.getNodemap().lineOfSight(pathability, v, v2)) {
                path.remove(i + 1);
            } else {
                a = i + 1;
            }
        }
    }

    private void updateFunnel(boolean leftSide, int li, int ri) {
        Vector2 previousVertex, leftVertex = leftVertices.get(li), rightVertex = rightVertices.get(ri);

        if (leftSide) {
            if (getLinePointRelationship(apex, leftVertex, rightVertex) < 0) {

                rightIndex = ri;
                leftIndex = li;
                apex = rightVertex.copy().add(getPrettyPoint(rightIndex, rightVertices, false));
                smooth_path.add(apex);

                return;
            }
        } else {
            if (getLinePointRelationship(apex, rightVertex, leftVertex) > 0) {

                rightIndex = ri;
                leftIndex = li;
                apex = leftVertex.copy().add(getPrettyPoint(leftIndex, leftVertices, true));
                smooth_path.add(apex);

                return;
            }
        }

        if (leftSide) {
            for (int i = leftIndex; i < li; i++) {
                previousVertex = leftVertices.get(i);
                if (getLinePointRelationship(apex, leftVertex, previousVertex) > 0) {

                    rightIndex = ri;
                    leftIndex = li - 1;
                    apex = leftVertices.get(leftIndex).copy().add(getPrettyPoint(leftIndex, leftVertices, true));
                    smooth_path.add(apex);

                    return;
                }
            }
        } else {
            Vector2 prettyOffset = null;
            int i = rightIndex;
            for (; i < ri; i++) {
                previousVertex = rightVertices.get(i);
                if (getLinePointRelationship(apex, rightVertex, previousVertex) < 0) {

                    leftIndex = li;
                    rightIndex = ri - 1;
                    prettyOffset = getPrettyPoint(i, rightVertices, false);
                    apex = rightVertices.get(rightIndex).copy().add(getPrettyPoint(rightIndex, rightVertices, false));
                    // apex = rightVertices.get(i).copy().add(prettyOffset);
                    smooth_path.add(apex);

                }
            }
            if (prettyOffset != null) {

            }
        }
        // insert while step loop blocked
        // pauseForAnalysis();
    }

    private ArrayList<Vector2[]> buildPortals(ArrayList<Vector2> path) {
        Logger.log("Build Portals", "smooth", false);
        ArrayList<Vector2[]> portals = new ArrayList<>();
        Vector2 vStart = path.get(0).copy();
        Logger.log("Start Node: " + vStart + "Snapped to: " + vStart.snapToWorldPoint(1), "smooth", false);
        Vector2[] portal = new Vector2[2];
        switch (vStart.headingTowards(path.get(1))) {
            case Vector2.NORTH:
                Logger.log("North", "smooth", false);
                portal[0] = new Vector2(vStart.x, vStart.y);
                portal[1] = new Vector2(vStart.x + 1, vStart.y);
                portals.add(portal);
                portal = new Vector2[2];
                portal[0] = new Vector2(vStart.x, vStart.y + 1);
                portal[1] = new Vector2(vStart.x + 1, vStart.y + 1);
                portals.add(portal);
                break;
            case Vector2.EAST:
                Logger.log("East", "smooth", false);
                portal[0] = new Vector2(vStart.x, vStart.y + 1);
                portal[1] = new Vector2(vStart.x, vStart.y);
                portals.add(portal);
                portal = new Vector2[2];
                portal[0] = new Vector2(vStart.x + 1, vStart.y + 1);
                portal[1] = new Vector2(vStart.x + 1, vStart.y);
                portals.add(portal);
                break;
            case Vector2.SOUTH:
                Logger.log("South", "smooth", false);
                portal[0] = new Vector2(vStart.x + 1, vStart.y + 1);
                portal[1] = new Vector2(vStart.x, vStart.y + 1);
                portals.add(portal);
                portal = new Vector2[2];
                portal[0] = new Vector2(vStart.x + 1, vStart.y);
                portal[1] = new Vector2(vStart.x, vStart.y);
                portals.add(portal);
                break;
            case Vector2.WEST:
                Logger.log("West", "smooth", false);
                portal[0] = new Vector2(vStart.x + 1, vStart.y);
                portal[1] = new Vector2(vStart.x + 1, vStart.y + 1);
                portals.add(portal);
                portal = new Vector2[2];
                portal[0] = new Vector2(vStart.x, vStart.y);
                portal[1] = new Vector2(vStart.x, vStart.y + 1);
                portals.add(portal);
                break;
        }
        portal = null; // Cleared
        for (int i = 1; i < path.size() - 1; i++) {
            portals.add(path.get(i).getSharedEdge(path.get(i + 1)));
        }
        Vector2[] endPortal = new Vector2[2];
        Vector2 endVertex = path.get(path.size() - 1);
        Vector2 snappedEndVertex = endVertex.copy().snapToWorldPoint(1);
        switch (endVertex.headingTowards(path.get(path.size() - 2))) {
            case Vector2.NORTH:
                Logger.log("North", "smooth", false);
                endPortal[0] = new Vector2(snappedEndVertex.x + 1, snappedEndVertex.y + 1);
                endPortal[1] = new Vector2(snappedEndVertex.x, snappedEndVertex.y + 1);
                portals.add(endPortal);
                // endPortal = new Vector2[2];
                // endPortal[0] = new Vector2(snappedEndVertex.x + 1,
                // snappedEndVertex.y);
                // endPortal[1] = new Vector2(snappedEndVertex.x,
                // snappedEndVertex.y);
                // portals.add(endPortal);
                break;
            case Vector2.EAST:
                Logger.log("East", "smooth", false);
                endPortal[0] = new Vector2(snappedEndVertex.x, snappedEndVertex.y);
                endPortal[1] = new Vector2(snappedEndVertex.x, snappedEndVertex.y + 1);
                portals.add(endPortal);
                // endPortal = new Vector2[2];
                // endPortal[0] = new Vector2(snappedEndVertex.x + 1,
                // snappedEndVertex.y);
                // endPortal[1] = new Vector2(snappedEndVertex.x + 1,
                // snappedEndVertex.y + 1);
                // portals.add(endPortal);
                break;
            case Vector2.SOUTH:
                Logger.log("South", "smooth", false);
                endPortal[0] = new Vector2(snappedEndVertex.x, snappedEndVertex.y);
                endPortal[1] = new Vector2(snappedEndVertex.x + 1, snappedEndVertex.y);
                portals.add(endPortal);
                // endPortal = new Vector2[2];
                // endPortal[0] = new Vector2(snappedEndVertex.x, snappedEndVertex.y
                // + 1);
                // endPortal[1] = new Vector2(snappedEndVertex.x + 1,
                // snappedEndVertex.y + 1);
                // portals.add(endPortal);
                break;
            case Vector2.WEST:
                Logger.log("West", "smooth", false);
                endPortal[0] = new Vector2(snappedEndVertex.x + 1, snappedEndVertex.y + 1);
                endPortal[1] = new Vector2(snappedEndVertex.x + 1, snappedEndVertex.y);
                portals.add(endPortal);
                // endPortal = new Vector2[2];
                // endPortal[0] = new Vector2(snappedEndVertex.x, snappedEndVertex.y
                // + 1);
                // endPortal[1] = new Vector2(snappedEndVertex.x,
                // snappedEndVertex.y);
                // portals.add(endPortal);
                break;
        }
        endPortal = null;
        return portals;
    }

    public Vector2 getPrettyPoint(int i, ArrayList<Vector2> channel, boolean isLeftChannel) {
        Vector2 a = null, b = channel.get(i).copy(), c = null;
        for (int x = i; x - 1 >= 0; x--) {
            a = channel.get(x - 1);
            if (a.x != b.x && a.y != b.y) {
                break;
            }
        }
        a = a.copy();
        for (int x = i; x + 1 < channel.size(); x++) {
            c = channel.get(x + 1);
            System.out.println("c: " + c);
            if (c.x != b.x && c.y != b.y) {
                break;
            }
        }
        System.out.println("c2: " + c);
        c = c.copy();

        a = b.copy().sub(a).normalize();
        c = b.sub(c).normalize();

        return a.add(c).normalize().multiply(offset);
    }
}
