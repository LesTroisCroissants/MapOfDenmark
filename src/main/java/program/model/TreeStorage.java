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
    RTree other; // For everything else

    /**
     * Used to describe the level of detail desired
     */
    public enum detail {
        LOW,
        MEDIUM,
        HIGH
    }

    public TreeStorage() {
        int maxChildren = 8;
        primary = new RTree(maxChildren);
        secondary = new RTree(10);
        tertiary = new RTree(20);
        buildings = new RTree(20);
        otherRoads = new RTree(maxChildren);
        coastline = new RTree(maxChildren);
        other = new RTree(maxChildren);
    }

    /**
     * Returns a list of mapelements for the given bounds and level of detail
     * @param min minpoint
     * @param max maxpoint
     * @param detail level of detail
     * @return list of MapElement
     */
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

    /**
     * Returns the MapRoadSegment closest to a MapPoint
     * @param q the query point
     * @return the closest MapRoadSegment
     */
    public MapRoadSegment nearestNeighbor(MapPoint q) {
        //var start = System.nanoTime();
        List<MapRoadSegment> nearestSegments = new ArrayList<>();
        // Add from all road trees
        nearestSegments.add(primary.findNearestNeighbor(q));
        nearestSegments.add(secondary.findNearestNeighbor(q));
        nearestSegments.add(tertiary.findNearestNeighbor(q));
        nearestSegments.add(otherRoads.findNearestNeighbor(q));

        MapRoadSegment nnRoad = null;
        float nnDist = Float.POSITIVE_INFINITY;

        PriorityQueue<NodeDistanceInfo<MapRoadSegment>> nearestNodes = new PriorityQueue<>();
        nearestNodes.addAll(nearestSegments.stream().map(s -> new NodeDistanceInfo<>(q.getMinPoint(), s, true)).toList());

        while (!nearestNodes.isEmpty()) {
            NodeDistanceInfo<MapRoadSegment> curr = nearestNodes.poll();
            if (curr.minDist > nnDist) continue;

            float distToLine = AuxMath.pointToRoadDistance(q.getMinPoint(), curr.node);
            if (distToLine < nnDist) {
                nnRoad = curr.node;
                nnDist = distToLine;
            }
        }

        //System.out.println((System.nanoTime() - start) / 1000000);

        return nnRoad;
    }

    /**
     * Returns the vertex closest to a query point
     * @param q query point
     * @return the vertex closest to the point
     */
    public Vertex nearestVertex(MapPoint q) {
        float[] p = q.getMinPoint();
        MapRoadSegment road = nearestNeighbor(q);
        float distA = AuxMath.calculateDistance(road.getVertexA(), new Vertex(p[0], p[1]));
        float distB = AuxMath.calculateDistance(road.getVertexB(), new Vertex(p[0], p[1]));
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

    public void setDebug(boolean debug, List<String> trees) {
        primary.setDebug(debug && trees.contains("primary"));
        secondary.setDebug(debug && trees.contains("secondary"));
        tertiary.setDebug(debug && trees.contains("tertiary"));
        buildings.setDebug(debug && trees.contains("buildings"));
        coastline.setDebug(debug && trees.contains("coastline"));
        otherRoads.setDebug(debug && trees.contains("otherRoads"));
        other.setDebug(debug && trees.contains("other"));
    }

    public float getMinLat() { return minLat; }
    public float getMaxLat() { return maxLat; }
    public float getMinLon() { return minLon; }
    public float getMaxLon() { return maxLon; }
}
