package program.model;

import program.shared.Point;

import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class Vertex extends Point implements Comparable<Vertex>, Serializable {
    transient List<DirectedEdge> outEdges; //all edges with this as their fromVertex
    transient List<DirectedEdge> inEdges; //all edges with this as their toVertex
     transient float distTo; //used for pathfinding

    public Vertex(float x, float y, long id) {
        super(x, y, id);
        outEdges = new ArrayList<>();
        inEdges = new ArrayList<>();
        distTo = Float.POSITIVE_INFINITY;
    }

    public Vertex(float x, float y){
        super(x, y);
        outEdges = new ArrayList<>();
        inEdges = new ArrayList<>();
        distTo = Float.POSITIVE_INFINITY;
    }

    public void addOutEdge(DirectedEdge edge){
        outEdges.add(edge);
    }
    public void addInEdge(DirectedEdge edge) {
        inEdges.add(edge);
    }

    @Override
    public String toString() {
        return getX() + " " + getY();
    }

    /**
     * Compares the distance to the two vertices; used during pathfinding
     * @param other the object to be compared.
     * @return
     */
    @Override
    public int compareTo(Vertex other){
        return Float.compare(distTo, other.distTo);
    }

    public void setDistTo(float i) {
        distTo = i;
    }

    @Serial
    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        this.inEdges = new ArrayList<>();
        this.outEdges = new ArrayList<>();
    }
}
