package program.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class Theme {
    private Color primary;
    private Color secondary;
    private Color tertiary;
    private Color land;
    private Color building;
    private Color other;
    private Color highlighted;

    public Theme(Color primary, Color secondary, Color tertiary, Color land, Color building, Color other, Color highlighted)  {
        this.primary = primary;
        this.secondary = secondary;
        this.tertiary = tertiary;
        this.land = land;
        this.building = building;
        this.other = other;
        this.highlighted = highlighted;
    }

    public void prepareDraw(GraphicsContext gc, String type, double determinant){
        switch (type) {
            case "primary" -> {
                gc.setStroke(primary);
                gc.setLineWidth(5 / Math.sqrt(determinant));
            }
            case "secondary" -> {
                gc.setStroke(secondary);
                gc.setLineWidth(3 / Math.sqrt(determinant));
            }
            case "tertiary" -> {
                gc.setStroke(tertiary);
                gc.setLineWidth(2 / Math.sqrt(determinant));
            }
            case "land" -> gc.setFill(land);
            case "building" -> gc.setFill(building);
            case "highlighted" -> {
                gc.setStroke(highlighted);
                gc.setLineWidth(5 / Math.sqrt(determinant));
                gc.setFill(highlighted);
            }
            default -> {
                gc.setStroke(other);
                gc.setLineWidth(1 / Math.sqrt(determinant));
            }
        }
    }
}
