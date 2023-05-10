package program.shared;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;

public class MapPath extends MapElement {
    private float[] points;
    private int count;

    public MapPath(String type, int size) {
        super(type);
        points = new float[size * 2];
        count = 0;
    }

    public void add(Point point) {
        if (point.getX() < minPoint[0]){
            minPoint[0] = point.getX();
        }
        if (point.getX() > maxPoint[0]) {
            maxPoint[0] = point.getX();
        }
        if (point.getY() < minPoint[1]) {
            minPoint[1] = point.getY();
        }
        if (point.getY() > maxPoint[1]) {
            maxPoint[1] = point.getY();
        }
        points[count] = point.getX();
        count++;
        points[count] = point.getY();
        count++;
    }

    @Override
    public void draw(GraphicsContext context) {
        /*context.strokeRect(minPoint.getX(), -maxPoint.getY(),
                maxPoint.getX() - minPoint.getX(),
                maxPoint.getY() - minPoint.getY());*/
        context.moveTo(points[0], -points[1]);
        context.beginPath();
        for (int i = 2; i < points.length; i += 2){
            context.lineTo(points[i], -points[i+1]);
        }
        context.stroke();
    }
}
