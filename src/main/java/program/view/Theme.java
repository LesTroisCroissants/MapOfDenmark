package program.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class Theme {
    private Color primary;
    private Color secondary;
    private Color tertiary;
    private Color land;
    private Color buildings;
    private Color other;

    public Theme(Color primary, Color secondary, Color tertiary, Color land, Color buildings, Color other)  {
        this.primary = primary;
        this.secondary = secondary;
        this.tertiary = tertiary;
        this.land = land;
        this.buildings = buildings;
        this.other = other;
    }

    public void prepareDraw(GraphicsContext gc, String type, double determinant){
        switch (type){
            case "primary":
                gc.setStroke(primary);
                gc.setLineWidth(5/Math.sqrt(determinant));
                break;
            case "secondary":
                gc.setStroke(secondary);
                gc.setLineWidth(3/Math.sqrt(determinant));
                break;
            case "tertiary":
                gc.setStroke(tertiary);
                gc.setLineWidth(2/Math.sqrt(determinant));
                break;
            case "land":
                gc.setFill(land);
                break;
            case "buildings":
                gc.setFill(buildings);
                break;
            default:
                gc.setStroke(other);
                gc.setLineWidth(1/Math.sqrt(determinant));
        }
    }
}
