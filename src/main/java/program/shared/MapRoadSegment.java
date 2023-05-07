package program.shared;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import program.model.Vertex;

import java.io.*;

public class MapRoadSegment extends MapElement implements Serializable {
    private final Vertex a;
    private final Vertex b;
    private final String name;
    private final int maxSpeed;
    private final float distance;
    boolean carAllowed;
    boolean onlyCarAllowed;
    public MapRoadSegment(Vertex a, Vertex b, String name, String type, int maxSpeed, boolean carAllowed, boolean onlyCarAllowed) {
        super(type);
        minPoint = new float[]{
                Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY())
        };
        maxPoint = new float[]{
                Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY())
        };
        this.a = a;
        this.b = b;
        this.name = name;

        this.carAllowed = carAllowed;
        this.onlyCarAllowed = onlyCarAllowed;
        this.maxSpeed = maxSpeed;
        this.distance = calculateDistance(a, b);
    }

    private float calculateDistance(Vertex vertexFrom, Vertex vertexTo){
        float xDifference = vertexTo.getX() - vertexFrom.getX();
        float yDifference = vertexTo.getY() - vertexFrom.getY();
        return (float) Math.sqrt(Math.pow(xDifference, 2) + Math.pow(yDifference, 2));
    }

    @Override
    public void draw(GraphicsContext context) {
        // Because of screen coordinates, latitude is negated and therefore drawn in negative
        context.beginPath();
        context.moveTo(a.getX(), -a.getY());
        context.lineTo(b.getX(), -b.getY());
        context.stroke();
    }

    public String getName() {
        return name;
    }

    public Vertex getVertexA() {
        return a;
    }
    public Vertex getVertexB() {
        return b;
    }

    public float getDistance() {
        return distance;
    }
    public int getMaxSpeed(){
        return maxSpeed;
    }
    public boolean isCarAllowed(){
        return carAllowed;
    }
    public boolean isOnlyCarAllowed(){
        return onlyCarAllowed;
    }

}
