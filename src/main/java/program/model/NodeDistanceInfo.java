package program.model;

/**
 * Object to contain information; used during nearest neighbour queries
 * @param <type>
 */
public class NodeDistanceInfo<type extends IBoundingBox> implements Comparable<NodeDistanceInfo<type>> {
    float minDist;
    float minMaxDist;
    boolean compareMinDist;
    type node;
    public NodeDistanceInfo(float[] q, type node, boolean compareMinDist) {
        this.minDist = calculateMinDistance(node.getMinPoint(), node.getMaxPoint(), q);
        this.minMaxDist = calculateMinMaxDistance(node.getMinPoint(), node.getMaxPoint(), q);
        this.node = node;
        this.compareMinDist = compareMinDist;
    }

    public NodeDistanceInfo(float[] q, type node) {
        this(q, node, false);
    }

    private float calculateMinDistance(float[] min, float[] max, float[] q) {
        return calculateMinDistance(min[0], max[0], q[0])
                + calculateMinDistance(min[1], max[1], q[1]);
    }

    private float calculateMinDistance(float min, float max, float qd) {
        if (qd <= min) return (float) Math.pow(qd - min, 2);
        if (qd >= max) return (float) Math.pow(qd - max, 2);
        return 0;
    }

    private float calculateMinMaxDistance(float[] min, float[] max, float[] q) {
        float rm = q[0] <= (min[0] + max[0]) / 2 ? min[0] : max[0];
        float rM = q[1] >= (min[1] + max[1]) / 2 ? min[1] : max[1];
        return (float) Math.pow(q[0] - rm, 2) + (float) Math.pow(q[1] - rM, 2);
    }

    @Override
    public int compareTo(NodeDistanceInfo<type> o) {
        if (compareMinDist) return Float.compare(minDist, o.minDist);
        return Float.compare(minMaxDist, o.minMaxDist);
    }
}
