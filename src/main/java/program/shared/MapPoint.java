package program.shared;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

// import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MapPoint extends MapElement {
    private final float[] point;


    public MapPoint(float x, float y, String type){
        super(type);
        point = new float[2];
        point[0] = x;
        point[1] = y;
        minPoint = point;
        maxPoint = point;
    }

    public MapPoint(Point point, String type) {
        super(type);
        this.point = new float[2];
        this.point[0] = point.getX();
        this.point[1] = point.getY();
        minPoint = this.point;
        maxPoint = this.point;
    }

    @Override
    public void draw(GraphicsContext context) {
        double size = 10 / Math.sqrt(context.getTransform().determinant());
        context.fillOval(point[0], -point[1], size, size);
    }

    @Override
    public String toString() {
        return this.point[0] + " " + this.point[1];
    }
}
