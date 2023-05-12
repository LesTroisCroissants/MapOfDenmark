package program.model;

import program.shared.MapElement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RTreeNode implements IBoundingBox, Serializable {
    private float[] min, max; //the minimum and maximum points of the bounding box
    int maxChildren; //this implementation does not use a minimum limit
    List<RTreeNode> children;
    List<MapElement> elements; //this is null for all nodes except leaves

    public RTreeNode(int maxChildren) {
        this.maxChildren = maxChildren;
        min = new float[]{ Float.MAX_VALUE, Float.MAX_VALUE };
        max = new float[]{ Float.MIN_VALUE, Float.MIN_VALUE };
    }

    /**
     * Returns the minimum X and Y coordinates of the MBR
     */
    public float[] getMinPoint() { return min; }

    /**
     * Returns the maximum X and Y coordinates of the MBR
     */
    public float[] getMaxPoint() { return max; }

    public void addElement (MapElement e) {
        if (elements == null) elements = new ArrayList<>();
        elements.add(e);
        if (elements.size() <= maxChildren) updateBoundingBox(); //This is to avoid updating MBR before a split
    }

    public void addChild(RTreeNode n) {
        if (children == null) children = new ArrayList<>();
        children.add(n);
        if (children.size() <= maxChildren) updateBoundingBox(); //This is to avoid updating MBR before a split
    }

    public void updateBoundingBox() {
        float xMin = Float.MAX_VALUE, yMin = Float.MAX_VALUE, xMax = Float.MIN_VALUE, yMax = Float.MIN_VALUE;

        for (IBoundingBox n : isLeaf() ? elements : children) {
            xMin = Math.min(n.getMinPoint()[0], xMin);
            yMin = Math.min(n.getMinPoint()[1], yMin);
            xMax = Math.max(n.getMaxPoint()[0], xMax);
            yMax = Math.max(n.getMaxPoint()[1], yMax);
        }

        min = new float[]{ xMin, yMin };
        max = new float[]{ xMax, yMax };
    }

    public int getChildrenSize() { return children == null ? 0 : children.size(); }
    public int getElementsSize() { return elements == null ? 0 : elements.size(); }

    /**
     * Returns true if this RTreeNode is a leaf node
     */
    public boolean isLeaf() {
        return getChildrenSize() == 0;
    }
}