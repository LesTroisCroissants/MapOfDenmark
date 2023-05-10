package program.shared;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;

public class MapPath extends MapShape {

    public MapPath(String type, int size) {
        super(type, size);
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
