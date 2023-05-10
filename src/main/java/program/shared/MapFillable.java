package program.shared;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class MapFillable extends MapShape {

    public MapFillable(String type, int size) {
        super(type, size);
    }

    @Override
    public void draw(GraphicsContext context) {
        context.moveTo(points[0], -points[1]);
        context.beginPath();
        for (int i = 2; i < points.length; i += 2){
            context.lineTo(points[i], -points[i+1]);
        }
        context.lineTo(points[0], -points[1]);
        context.fill();
    }
}
