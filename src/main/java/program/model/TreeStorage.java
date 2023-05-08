package program.model;

import program.shared.*;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class TreeStorage implements Serializable {
    // Store multiple R-trees according to layers
    // Query multiple r-trees based on parameters
    // Store data in correct r-tree

    private float minLat, maxLat, minLon, maxLon;

    int elementCount = 0;

    // Find a meaningful way to store data in multiple R-trees.
    // This is just an example of some layers that could make sense to store individually.
    // Roads
    RTree primary; // Primary roads
    RTree secondary; // Secondary roads
    RTree tertiary; // Tertiary roads
    RTree otherRoads; // For smaller roads and paths

    // Shapes
    RTree buildings; // Buildings - allows for hiding buildings until zoomed far in fx.
    RTree coastline; // Coastlines - drawn to give Denmark it's beautiful shape
    RTree other; // For everything else :)

    public enum detail {
        LOW,
        MEDIUM,
        HIGH
    }

    public TreeStorage() {
        // Use testing to change these values or take them as parameters
        int minChildren = 2, maxChildren = 4;
        primary = new RTree(minChildren, maxChildren);
        secondary = new RTree(minChildren, maxChildren);
        tertiary = new RTree(minChildren, maxChildren);
        buildings = new RTree(minChildren, maxChildren);
        otherRoads = new RTree(minChildren, maxChildren);
        coastline = new RTree(minChildren, maxChildren);
        other = new RTree(minChildren, maxChildren);

        // Use debug value to decide whether to draw debugging MBRs
        // TODO: implement this
    }

    public List<MapElement> query(float[] min, float[] max, detail detail) {
        List<MapElement> drawFirst = new ArrayList<>();
        List<MapElement> drawLast = new ArrayList<>();

        switch (detail) {
            case HIGH:
                drawLast.addAll(other.query(min, max));
                drawLast.addAll(otherRoads.query(min, max));
                drawLast.addAll(buildings.query(min, max));
            case MEDIUM:
                drawLast.addAll(tertiary.query(min, max));
                drawLast.addAll(secondary.query(min, max));
                drawLast.addAll(primary.query(min, max));
            case LOW:
                drawFirst.addAll(coastline.query(min, max));
                break;
        }

        List<MapElement> results = new ArrayList<>();
        results.addAll(drawFirst);
        results.addAll(drawLast);

        return results;
    }

    public MapRoadSegment nearestNeighbor(MapPoint q) {
        var start = System.nanoTime();
        List<MapRoadSegment> nearestSegments = new ArrayList<>();
        // Add from all road trees
        nearestSegments.add((MapRoadSegment) primary.findNearestNeighbor(q));
        nearestSegments.add((MapRoadSegment) secondary.findNearestNeighbor(q));
        nearestSegments.add((MapRoadSegment) tertiary.findNearestNeighbor(q));
        nearestSegments.add((MapRoadSegment) otherRoads.findNearestNeighbor(q));

        MapRoadSegment nnRoad = null; // TODO: maybe give this another name?
        float nnDist = Float.POSITIVE_INFINITY;

        PriorityQueue<NodeDistanceInfo<MapRoadSegment>> nearestNodes = new PriorityQueue<>();
        nearestNodes.addAll(nearestSegments.stream().map(s -> new NodeDistanceInfo<>(q.getMinPoint(), s, true)).toList());

        while (!nearestNodes.isEmpty()) {
            NodeDistanceInfo<MapRoadSegment> curr = nearestNodes.poll();
            if (curr.minDist > nnDist) continue;

            float distToLine = AuxMath.pointToRoadDistance(q.getMinPoint(), curr.node);
            if (distToLine < nnDist) {
                //nn = curr; Seperately we need to find closest vertex in final element
                nnRoad = curr.node;
                nnDist = distToLine;
            }
        }

        //System.out.println((System.nanoTime() - start) / 1000000);

        return nnRoad;
    }

    public Vertex nearestVertex(MapPoint point) {
        float[] p = point.getMinPoint();
        MapRoadSegment road = nearestNeighbor(point);
        float distA = (float) Math.pow(p[1] - road.getVertexA().getY(), 2) + (float) Math.pow(p[0] - road.getVertexA().getX(), 2);
        float distB = (float) Math.pow(p[1] - road.getVertexB().getY(), 2) + (float) Math.pow(p[0] - road.getVertexB().getX(), 2);
        return distA < distB ? road.getVertexA() : road.getVertexB();
    }

    public void insert(MapElement element, String type) {
        elementCount++;
        switch (element.getType()) {
            case "coastline" -> coastline.insert(element);
            case "building" -> buildings.insert(element);
            case "primary" -> primary.insert(element);
            case "secondary" -> secondary.insert(element);
            case "tertiary" -> tertiary.insert(element);
            default -> {
                if (type.equals("highway")) otherRoads.insert(element);
                else other.insert(element);
            }
        }
    }

    public void setMapArea(float minLat, float minLon, float maxLat, float maxLon) {
        this.minLat = minLat;
        this.minLon = minLon;
        this.maxLat = maxLat;
        this.maxLon = maxLon;
    }

    public void setDebug(boolean debug) {
        //primary.setDebug(debug);
        //secondary.setDebug(debug);
        tertiary.setDebug(debug);
        //land.setDebug(debug);
        //buildings.setDebug(debug);
        //coastline.setDebug(debug);
        //other.setDebug(debug);
    }

    public float getMinLat() { return minLat; }
    public float getMaxLat() { return maxLat; }
    public float getMinLon() { return minLon; }
    public float getMaxLon() { return maxLon; }
}
