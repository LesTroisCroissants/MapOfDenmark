package program.shared;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Class for debugging purposes in relation to the R-tree
 */
public class MapDebugMBR extends MapElement {
    public MapDebugMBR (float[] min, float[] max) {
        super("MBR");
        this.minPoint = min;
        this.maxPoint = max;
    }

    @Override
    public void draw(GraphicsContext context) {
        context.setStroke(Color.BLUE);
        context.strokeRect(minPoint[0], -maxPoint[1],
                maxPoint[0] - minPoint[0],
                maxPoint[1] - minPoint[1]);
        context.setStroke(Color.BLACK);
    }
}
