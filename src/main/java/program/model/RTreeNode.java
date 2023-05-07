package program.model;

import program.shared.IBoundingBox;
import program.shared.MapElement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RTreeNode implements IBoundingBox, Serializable {
    private float[] min, max;
    int maxChildren;
    List<RTreeNode> children;
    List<MapElement> elements;
    public RTreeNode(int maxChildren) {
        this.maxChildren = maxChildren;
        children = new ArrayList<>(maxChildren);
        elements = new ArrayList<>(maxChildren);
        min = new float[]{ Float.MAX_VALUE, Float.MAX_VALUE };
        max = new float[]{ Float.MIN_VALUE, Float.MIN_VALUE };
    }

    public float[] getMinPoint() { return min; }
    public float[] getMaxPoint() { return max; }

    public void addElement (MapElement e) {
        elements.add(e);
        if (elements.size() <= maxChildren) updateBoundingBox();
    }

    public void addChild(RTreeNode n) {
        children.add(n);
        if (children.size() <= maxChildren) updateBoundingBox();
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

    public boolean isLeaf() {
        return children.size() == 0;
    }
}