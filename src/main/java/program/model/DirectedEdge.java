package program.model;

import program.shared.MapRoadSegment;

import java.io.Serializable;

public class DirectedEdge implements Serializable {
    private Vertex vertexFrom;
    private Vertex vertexTo;
    private final float carWeight;
    private MapRoadSegment mapRoadSegment;

    public DirectedEdge(Vertex vertexFrom, Vertex vertexTo, float carWeight, MapRoadSegment representedRoadSegment) {
        this.vertexFrom = vertexFrom;
        this.vertexTo = vertexTo;
        this.mapRoadSegment = representedRoadSegment;

        this.carWeight = carWeight;
    }

    /**
     * Returns the tail vertex of the directed edge.
     * @return the tail vertex of the directed edge
     */
    public Vertex fromVertex() {
        return vertexFrom;
    }

    /**
     * Returns the head vertex of the directed edge.
     * @return the head vertex of the directed edge
     */
    public Vertex toVertex() {
        return vertexTo;
    }

    /**
     * Returns the weight of the directed edge.
     * @return the weight of the directed edge
     */
    public float weight(Model.MOT modeOfTransport) {
        return switch (modeOfTransport) {
            case BIKE, WALK -> mapRoadSegment.getDistance();
            default -> carWeight;
        };
    }

    public MapRoadSegment getMapRoadSegment(){
        return mapRoadSegment;
    }

    /**
     * Returns a string representation of the directed edge.
     * @return a string representation of the directed edge
     */
    public String toString() {
        return vertexFrom.toString() + "->" + vertexTo.toString() + " " + String.format("%5.2f", mapRoadSegment.getDistance());
    }

}
