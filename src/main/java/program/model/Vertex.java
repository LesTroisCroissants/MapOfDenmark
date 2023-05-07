package program.model;

import program.shared.Point;

import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class Vertex extends Point implements Comparable<Vertex>, Serializable {
    transient List<DirectedEdge> outEdges;
    transient List<DirectedEdge> inEdges;
    transient float distTo;

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
