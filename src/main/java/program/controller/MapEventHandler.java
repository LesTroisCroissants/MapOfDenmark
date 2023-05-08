package program.controller;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import program.model.ModelContact;
import program.shared.MapPoint;
import program.shared.MapRoadSegment;

public class MapEventHandler {
    private double timeSinceLastMove = 0;
    private Controller controller;
    private Canvas canvas;
    private Affine trans;

    private Point2D lastMousePosition;
    private Point2D lastMouseClickPosition;

    public MapEventHandler(Controller _controller, ModelContact model) {
        controller = _controller;
        canvas = controller.getCanvas();

        lastMousePosition = new Point2D(0, 0);
        lastMouseClickPosition = new Point2D(0, 0);

        canvas.setOnMouseMoved(e -> {
            trans = controller.getAffine();
            var timeNow = System.nanoTime();
            if ((timeNow - timeSinceLastMove) / 1_000_000 < 100) return;
            var mouseX = e.getX();
            var mouseY = e.getY();

            timeSinceLastMove = timeNow;

            try {
                Point2D mouseToPoint = trans.inverseTransform(mouseX, mouseY);
                MapPoint queryPoint = new MapPoint((float) mouseToPoint.getX(), (float) -mouseToPoint.getY(), "");
                MapRoadSegment nn = (MapRoadSegment) model.nearestNeighbor(queryPoint);
                controller.getNearestRoadLabel().setText(nn.getName());
            } catch (NonInvertibleTransformException ex) {
                throw new RuntimeException(ex);
            }
        });

        canvas.setOnMousePressed(e -> {
            lastMouseClickPosition = new Point2D(e.getX(), e.getY());
            lastMousePosition = new Point2D(e.getX(), e.getY());
        });

        canvas.setOnMouseReleased(e -> {
            trans = controller.getAffine();
            // Check if current mouse position is different to the last click
            if (e.getX() != lastMouseClickPosition.getX() || e.getY() != lastMouseClickPosition.getY()) return;

            try {
                // Use this to find the nearest road
                Point2D mouseToPoint = trans.inverseTransform(lastMouseClickPosition.getX(), lastMouseClickPosition.getY());
                MapPoint queryPoint = new MapPoint((float) mouseToPoint.getX(), (float) -mouseToPoint.getY(), "");
                MapRoadSegment nearestRoad = (MapRoadSegment) model.nearestNeighbor(queryPoint);
                controller.getTextField().setText(nearestRoad.getName());
            } catch (NonInvertibleTransformException ex) {
                throw new RuntimeException(ex);
            }
        });
        canvas.setOnMouseDragged(e -> {
            if (e.isPrimaryButtonDown()) {
                double dx = e.getX() - lastMousePosition.getX();
                double dy = e.getY() - lastMousePosition.getY();
                controller.pan(dx, dy);
                controller.draw();
            }

            lastMousePosition = new Point2D(e.getX(), e.getY());
        });
        canvas.setOnScroll(e -> {
            double factor = e.getDeltaY();
            controller.zoom(e.getX(), e.getY(), Math.pow(1.01, factor));
            controller.draw();
        });
    }
}
