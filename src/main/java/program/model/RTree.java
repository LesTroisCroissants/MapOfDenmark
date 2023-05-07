package program.model;

import javafx.util.Pair;
import program.shared.*;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.*;

public class RTree implements Serializable {
    RTreeNode root;

    private int minChildren;
    private int maxChildren;
    private boolean debug = false;
    private int level = 0;

    private int elements = 0;

    public RTree(int minChildren, int maxChildren) {
        root = new RTreeNode(maxChildren);
        this.minChildren = minChildren;
        this.maxChildren = maxChildren;
    }

    public RTreeNode getRoot() { return root; }

    public void insert(MapElement element) {
        elements++;
        RTreeNode node = root;
        Stack<RTreeNode> path = new Stack<>();
        while (!node.isLeaf()) {
            path.push(node);
            node = pickChild(node.children, element.getMinPoint(), element.getMaxPoint());
        }

        node.addElement(element);

        RTreeNode splitNode = null;
        if (node.elements.size() > node.maxChildren) splitNode = split(node);

        while (!path.isEmpty()) {
            RTreeNode pathNode = path.pop();
            if (splitNode != null) {
                pathNode.addChild(splitNode);
                if (pathNode.children.size() > node.maxChildren) {
                    splitNode = split(pathNode);
                } else {
                    splitNode = null;
                }
            }
            pathNode.updateBoundingBox();
        }
    }

    public List<MapElement> query(float[] min, float[] max) {
        List<MapElement> results = new ArrayList<>();

        int currentLevel = 0;

        ArrayDeque<RTreeNode> path = new ArrayDeque<>();
        path.push(root);
        while (!path.isEmpty()) {
            RTreeNode current = path.removeFirst();
            if (path.peekFirst() == null) {
                currentLevel++;
            }
            if (current.isLeaf()) {
                /*for (MapElement e : current.elements) {
                    if (hasOverlap(min, max, e.getMinPoint(), e.getMaxPoint())) results.add(e);
                }*/
                results.addAll(current.elements);
            } else {
                for (RTreeNode n : current.children) {
                    if (hasOverlap(min, max, n.getMinPoint(), n.getMaxPoint())) path.addFirst(n);
                }
                if (debug && currentLevel > level) {
                    results.add(new MapDebugMBR(current.getMinPoint(), current.getMaxPoint()));
                }
            }
        }

        return results;
    }

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

                    float distToLine = RTreeMath.pointToRoadDistance(q, (MapRoadSegment) element);
                    if (distToLine < nnDist) {
                        nearestNeighbor = element;
                        nnDist = distToLine;
                    }
                }
            }
        }
        return nearestNeighbor;
    }

    private void enqueueChildNodes(PriorityQueue<NodeDistanceInfo<RTreeNode>> pq, List<RTreeNode> children, float[] q) {
        pq.addAll(children.stream().map(node -> new NodeDistanceInfo<>(q, node)).toList());
    }

    private <type extends IBoundingBox> Pair<type, type> findCandidates(List<type> nodes) {
        float maxArea = 0F;
        type candidate1 = null, candidate2 = null;
        // Find the two elements that create the largest area
        for (int i = 0; i < nodes.size(); i++) {
            type e1 = nodes.get(i);
            for (int j = i + 1; j < nodes.size(); j++) {
                type e2 = nodes.get(j);
                float areaIncrease = calculateAreaIncrease(e1.getMinPoint(),
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
        if (candidate1 == null) candidate1 = nodes.get(0);
        if (candidate2 == null) candidate2 = nodes.get(1);
        return new Pair<>(candidate1, candidate2);
    }

    // Takes two candidates to decide the splits and two nodes to divide the children to.
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

            //if (candidate1Children.size() < minChildren) candidate1Children.add(c);
            //else if (candidate2Children.size() < minChildren) candidate2Children.add(c);
            if (c1Area > c2Area) {
                candidate2Children.add(c);
            } else {
                candidate1Children.add(c);
            }
        }
        return new Pair<>(candidate1Children, candidate2Children);
    }

    private <type extends IBoundingBox> RTreeNode split(RTreeNode node) {
        // Change split to exist on insert of node, so that insert can return a node to be inserted
        // Maybe have node as leaf?
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
            newRoot.children.add(root);
            newRoot.children.add(newNode);
            root.updateBoundingBox();
            root = newRoot;
            return null;
        }
        return newNode;
    }

    /*
        In an attempt to make the code more readable and avoid repetition, I have attempted
        to merge the functionality of these methods in a single new method: split()
        These methods create a lot of repetition and are meant for handling non-leaf and leaf
        splits.
     */
    /*
    private void splitRoot() {
        // Debugging
        nodes++;
        rootSplit++;

        RTreeNode newRoot = new RTreeNode(maxChildren);
        RTreeNode splitNode;
        if (root.isLeaf())  splitNode = splitLeaf(root);
        else splitNode = splitParent(root);

        newRoot.addChild(root);
        newRoot.addChild(splitNode);

        root = newRoot;
    }

    private RTreeNode splitParent(RTreeNode node) {
        // Debugging
        nodes++;

        RTreeNode newNode = new RTreeNode(maxChildren);
        Pair<RTreeNode, RTreeNode> candidates = findCandidates(node.children);
        RTreeNode candidate1 = candidates.getKey(), candidate2 = candidates.getValue();
        Pair<List<RTreeNode>, List<RTreeNode>> dividedChildren = divideChildren(candidates, node.children);


        node.children = dividedChildren.getKey();
        newNode.children = dividedChildren.getValue();

        newNode.updateBoundingBox();
        node.updateBoundingBox();

        return newNode;
    }


    private RTreeNode splitLeaf(RTreeNode node) {
        // Debugging
        nodes++;

        RTreeNode newNode = new RTreeNode(maxChildren);

        Pair<MapElement, MapElement> candidates = findCandidates(node.elements);
        MapElement candidate1 = candidates.getKey(), candidate2 = candidates.getValue();
        Pair<List<MapElement>, List<MapElement>> dividedElements = divideChildren(candidates, node.elements);

        node.elements = dividedElements.getKey();
        newNode.elements = dividedElements.getValue();

        newNode.updateBoundingBox();
        node.updateBoundingBox();

        return newNode;
    }

     */

    private RTreeNode pickChild(List<RTreeNode> nodes, float[] childMin, float[] childMax) {
        List<RTreeNode> validNodes = new ArrayList<>();
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
                if (candidate.children.size() == candidate.maxChildren) {
                    candidate = node;
                } else if (node.children.size() == candidate.maxChildren) {
                } else {
                    // If they both have/haven't got space for children, use smallest MBR
                    candidate = calculateArea(candidate.getMinPoint(), candidate.getMaxPoint()) <
                            calculateArea(node.getMinPoint(), node.getMaxPoint()) ? candidate : node;
                }
            }
        }
        return candidate;
    }

    public boolean hasOverlap(float[] currentMin, float[] currentMax, float[] otherMin, float[] otherMax) {
        boolean overlapX = currentMin[0] <= otherMax[0] && currentMax[0] >= otherMin[0];
        boolean overlapY = currentMin[1] <= otherMax[1] && currentMax[1] >= otherMin[1];

        return overlapX && overlapY;
    }

    private float calculateArea(float[] min, float[] max) {
        return (max[0] - min[0]) * (max[1] - min[1]);
    }

    private float calculateAreaIncrease(float[] currentMin, float[] currentMax, float[] otherMin, float[] otherMax) {
        float currentArea = (currentMax[0] - currentMin[0]) * (currentMax[1] - currentMin[1]);
        float newArea = (Math.max(currentMax[0], otherMax[0]) - Math.min(currentMin[0], otherMin[0]))
                * (Math.max(currentMax[1], otherMax[1]) - Math.min(currentMin[1], otherMin[1]));
        return newArea - currentArea;
    }

    public void setDebug(boolean debug, int level) {
        this.debug = debug;
        this.level = level;
    }

    public void setDebug(boolean debug) {
        setDebug(debug, 2);
    }
}
