package program.model;

import javafx.util.Pair;
import program.shared.*;

import java.io.Serializable;
import java.util.*;

public class RTree implements Serializable {
    RTreeNode root;

    private int maxChildren;
    private boolean debug = false;


    public RTree(int maxChildren) {
        root = new RTreeNode(maxChildren);
        this.maxChildren = maxChildren;
    }

    /**
     * Inserts MapElement into leaf node
     * @param element
     */
    public void insert(MapElement element) {
        RTreeNode node = root;
        Stack<RTreeNode> path = new Stack<>();

        // Find most optimal leaf to insert element into
        while (!node.isLeaf()) {
            path.push(node);
            node = pickChild(node.children, element.getMinPoint(), element.getMaxPoint());
        }

        node.addElement(element);

        RTreeNode splitNode = null;
        if (node.getElementsSize() > node.maxChildren) splitNode = split(node);

        while (!path.isEmpty()) {
            RTreeNode pathNode = path.pop();
            if (splitNode != null) {
                pathNode.addChild(splitNode);
                if (pathNode.getChildrenSize() > node.maxChildren) {
                    splitNode = split(pathNode);
                } else {
                    splitNode = null;
                }
            }
            pathNode.updateBoundingBox();
        }
    }

    /**
     * Traverses the RTree and returns MapElements with minimum bounding rectangles that overlap with the rectangle
     * given by a minimum and maximum point. Min and max are float arrays consisting of [0] as x-coordinate and [1]
     * as y-coordinate.
     * @param min float array consisting of [0] as x-coordinate and [1] as a y-coordinate
     * @param max float array consisting of [0] as x-coordinate and [1] as a y-coordinate
     * @return A List of MapElements
     */
    public List<MapElement> query(float[] min, float[] max) {
        List<MapElement> results = new ArrayList<>();

        ArrayDeque<RTreeNode> path = new ArrayDeque<>();
        path.push(root);
        while (!path.isEmpty()) {
            RTreeNode current = path.removeFirst();
            if (current.isLeaf()) {
               if (current.getElementsSize() > 0) {
                   results.addAll(current.elements);
                   /*for (MapElement e : current.elements) {
                       if (hasOverlap(min, max, e.getMinPoint(), e.getMaxPoint())) results.add(e);
                   }*/
                   if (debug) results.add(new MapDebugMBR(current.getMinPoint(), current.getMaxPoint()));
               }
            } else {
                for (RTreeNode n : current.children) {
                    if (hasOverlap(min, max, n.getMinPoint(), n.getMaxPoint())) path.addFirst(n);
                }
            }
        }

        return results;
    }

    /**
     * Traverses the RTree to find the MapElement closest to the given point.
     * @param point A MapPoint consisting of an x-coordinate and y-coordinate.
     * @return Under the current implementation, it will always return a MapRoadSegment
     */
    public MapElement findNearestNeighbor(MapPoint point) {
        float[] q = point.getMinPoint();
        PriorityQueue<NodeDistanceInfo<RTreeNode>> nearestNodes = new PriorityQueue<>();
        NodeDistanceInfo<RTreeNode> mmd = null;

        if (!root.isLeaf()){
            enqueueChildNodes(nearestNodes, root.children, q);
            mmd = nearestNodes.peek();
        }

        float nnDist = Float.MAX_VALUE;
        MapElement nearestNeighbor = null;

        while (!nearestNodes.isEmpty()) {
            NodeDistanceInfo<RTreeNode> current = nearestNodes.poll();
            if (current.minDist > mmd.minMaxDist) continue;
            if (current.minMaxDist < mmd.minMaxDist /*&& current.minMaxDist != 0*/) {
                mmd = current;

            }
            if (nearestNeighbor != null && current.minDist > nnDist) continue;
            if (!current.node.isLeaf()) enqueueChildNodes(nearestNodes, current.node.children, q);
            else {
                PriorityQueue<NodeDistanceInfo<MapElement>> elements = new PriorityQueue<>();
                for (MapElement e : current.node.elements) elements.add(new NodeDistanceInfo<>(q, e));
                while (!elements.isEmpty()) {
                    NodeDistanceInfo<MapElement> elementInfo = elements.poll();
                    MapElement element = elementInfo.node;
                    //if (elementInfo.minDist > nnDist) continue;

                    float distToLine = AuxMath.pointToRoadDistance(q, (MapRoadSegment) element);
                    if (distToLine < nnDist) {
                        nearestNeighbor = element;
                        nnDist = distToLine;
                    }
                }
            }
        }
        return nearestNeighbor;
    }

    /**
     * Creates a NodeDistanceInfo object for each RTreeNode given and inserts them into a priority queue.
     * @param pq A priority queue of NodeDistanceInfo containing RTreeNodes
     * @param children A List of RTreeNodes
     * @param q A point as a float array where [0] is the x-coordinate and [1] is the y-coordinate
     */
    private void enqueueChildNodes(PriorityQueue<NodeDistanceInfo<RTreeNode>> pq, List<RTreeNode> children, float[] q) {
        pq.addAll(children.stream().map(node -> new NodeDistanceInfo<>(q, node)).toList());
    }

    /**
     * Finds the two elements of the given list of nodes that would create the most area increase and returns them.
     * @param nodes A list of elements that extend the interface IBoundingBox
     * @return Returns a Pair of elements of the given type
     * @param <type> The type of the given elements
     */
    private <type extends IBoundingBox> Pair<type, type> findCandidates(List<type> nodes) {
        float maxArea = -Float.MAX_VALUE;
        type candidate1 = null, candidate2 = null;
        // Find the two elements that create the largest area
        for (int i = 0; i < nodes.size(); i++) {
            type e1 = nodes.get(i);
            for (int j = i + 1; j < nodes.size(); j++) {
                type e2 = nodes.get(j);

                float areaIncrease = calculateAreaIncrease(
                        e1.getMinPoint(),
                        e1.getMaxPoint(),
                        e2.getMinPoint(),
                        e2.getMaxPoint());

                if (areaIncrease > maxArea) {
                    maxArea = areaIncrease;
                    candidate1 = e1;
                    candidate2 = e2;
                }
            }
        }
        return new Pair<>(candidate1, candidate2);
    }

    /**
     * Finds and distributes elements from a list between two candidate elements of a given type.
     * The elements are distributed to the element that would cause the least area increase of their
     * minimal bounding rectangle.
     * @param candidates A Pair of two elements of the given type
     * @param children A list of elements of the given type
     * @return A Pair of lists containing the divided elements
     * @param <type> The given type that implement the interface IBoundingBox
     */
    private <type extends IBoundingBox> Pair<List<type>, List<type>> divideChildren(Pair<type, type> candidates, List<type> children) {
        List<type> candidate1Children = new ArrayList<>();
        candidate1Children.add(candidates.getKey());
        List<type> candidate2Children = new ArrayList<>();
        candidate2Children.add(candidates.getValue());

        children.remove(candidates.getKey());
        children.remove(candidates.getValue());

        for (type c : children) {
            float c1Area = calculateAreaIncrease(
                    candidates.getKey().getMinPoint(),
                    candidates.getKey().getMaxPoint(),
                    c.getMinPoint(), c.getMaxPoint());
            float c2Area = calculateAreaIncrease(
                    candidates.getValue().getMinPoint(),
                    candidates.getValue().getMaxPoint(),
                    c.getMinPoint(), c.getMaxPoint());
            if (c1Area > c2Area) {
                candidate2Children.add(c);
            } else {
                candidate1Children.add(c);
            }
        }
        return new Pair<>(candidate1Children, candidate2Children);
    }

    /**
     * Takes RTreeNode and splits its contents into two, using Quadratic Split.
     * It manipulates the given node and returns the other.
     * @param node An RTreeNode
     * @return An RTreeNode with the other part of the split
     * @param <type>
     */
    private <type extends IBoundingBox> RTreeNode split(RTreeNode node) {
        List<type> nodes = node.isLeaf() ? (ArrayList<type>) node.elements : (ArrayList<type>) node.children;
        RTreeNode newNode = new RTreeNode(maxChildren);
        // Quadratic split finds the two elements that create the most area.
        Pair<type, type> candidates = findCandidates(nodes);
        // type candidate1 = candidates.getKey(), candidate2 = candidates.getValue();
        // the nodes/elements are divided amongst the two candidates.
        Pair<List<type>, List<type>> dividedChildren = divideChildren(candidates, nodes);

        // In an attempt to rid ourselves of repeated code we use casting.
        if (node.isLeaf()) {
            node.elements = (ArrayList<MapElement>) dividedChildren.getKey();
            newNode.elements = (ArrayList<MapElement>) dividedChildren.getValue();
        } else {
            node.children = (ArrayList<RTreeNode>) dividedChildren.getKey();
            newNode.children = (ArrayList<RTreeNode>) dividedChildren.getValue();
        }

        newNode.updateBoundingBox();
        node.updateBoundingBox();

        // In case the node is the root node, we create a new root node to change the height of the tree.
        if (node == root) {
            RTreeNode newRoot = new RTreeNode(maxChildren);
            newRoot.addChild(root);
            newRoot.addChild(newNode);
            root.updateBoundingBox();
            root = newRoot;
            return null;
        }
        return newNode;
    }

    /**
     * Iterates over a list of RTreeNodes to find the best fitting node for the given bounding rectangle
     * @param nodes A List of RTreeNodes
     * @param childMin The minimum point of the bounding rectangle
     * @param childMax The maximum point of the bounding rectangle
     * @return Returns the RTreeNode where the given bounding rectangle will create the least amount of increased area.
     */
    private RTreeNode pickChild(List<RTreeNode> nodes, float[] childMin, float[] childMax) {
        // Find overlapping elements in list
        List<RTreeNode> overlap = new ArrayList<>();
        for (RTreeNode node : nodes) {
            if (hasOverlap(node.getMinPoint(), node.getMaxPoint(), childMin, childMax)) overlap.add(node);
        }

        // If only one element overlaps, it's the best candidate
        if (overlap.size() == 1) return overlap.get(0);

        // Take first element as best candidate to begin with
        float minArea = Float.MAX_VALUE;
        RTreeNode candidate = null;

        // If several or none of the nodes overlap
        for (RTreeNode node : overlap.size() == 0 ? nodes : overlap) {
            if (candidate == null) {
                candidate = node;
                continue;
            }

            // Check which element will increase area least
            float newArea = calculateAreaIncrease(
                    node.getMinPoint(), node.getMaxPoint(),
                    candidate.getMinPoint(), candidate.getMaxPoint());

            if (newArea < minArea) {
                candidate = node;
                minArea = newArea;
                continue;
            }

            // If area increase is the same, the best candidate is the one with space for children
            if (newArea == minArea) {
                var firstArea = calculateArea(candidate.getMinPoint(), candidate.getMaxPoint());
                var secondArea = calculateArea(node.getMinPoint(), node.getMaxPoint());
                if (firstArea == secondArea && candidate.getChildrenSize() == candidate.maxChildren) {
                    candidate = node;
                } else {
                    candidate = calculateArea(candidate.getMinPoint(), candidate.getMaxPoint()) <
                            calculateArea(node.getMinPoint(), node.getMaxPoint()) ? candidate : node;
                }
            }
        }
        return candidate;
    }

    /**
     * Calculates whether two rectangles, given by the minimum and maximum point has any overlap
     * @param currentMin Minimum point of the first rectangle
     * @param currentMax Maximum point of the first rectangle
     * @param otherMin Minimum point of the other rectangle
     * @param otherMax Maximum point of the other rectangle
     * @return Returns a boolean value representational of whether the rectangles overlap
     */
    public boolean hasOverlap(float[] currentMin, float[] currentMax, float[] otherMin, float[] otherMax) {
        boolean overlapX = currentMin[0] <= otherMax[0] && currentMax[0] >= otherMin[0];
        boolean overlapY = currentMin[1] <= otherMax[1] && currentMax[1] >= otherMin[1];

        return overlapX && overlapY;
    }

    /**
     * Calculates the area of a rectangle
     * @param min Minimum point of the given rectangle
     * @param max Maximum point of the given rectangle
     * @return Area as a float
     */
    private float calculateArea(float[] min, float[] max) {
        return (max[0] - min[0]) * (max[1] - min[1]);
    }

    /**
     * Calculates the area increase created by using the minimum x and y-coordinates and maximum x and y-coordinates
     * of two rectangles.
     * @param currentMin Minimum point of the first rectangle
     * @param currentMax Maximum point of the first rectangle
     * @param otherMin Minimum point of the other rectangle
     * @param otherMax Maximum point of the other rectangle
     * @return The area increase as a float
     */
    private float calculateAreaIncrease(float[] currentMin, float[] currentMax, float[] otherMin, float[] otherMax) {
        float currentArea = calculateArea(currentMin, currentMax);
        float otherArea = calculateArea(otherMin, otherMax);

        float newArea = (Math.max(currentMax[0], otherMax[0]) - Math.min(currentMin[0], otherMin[0]))
                * (Math.max(currentMax[1], otherMax[1]) - Math.min(currentMin[1], otherMin[1]));

        return newArea - currentArea - otherArea;
    }

    /**
     * Enables the query to also return minimum bounding rectangles of leaves as MapElements
     * @param debug A boolean whether to enable or disable debugging
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
