package program.shared;

import javafx.scene.canvas.GraphicsContext;
import program.model.IBoundingBox;

import java.io.Serializable;

public abstract class MapElement implements IBoundingBox, Serializable {
    protected float[] minPoint;
    protected float[] maxPoint;
    private final String type;

    public MapElement(String type) {
        this.type = type;
        minPoint = new float[]{ Float.MAX_VALUE, Float.MAX_VALUE };
        maxPoint = new float[]{ Float.MIN_VALUE, Float.MIN_VALUE };
    }
    public float[] getMinPoint() {
        return minPoint;
    }
    public float[] getMaxPoint() {
        return maxPoint;
    }
    public String getType() {
        return type;
    }
    public abstract void draw(GraphicsContext context);
}
