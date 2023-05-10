package program.controller;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import program.model.ModelContact;
import program.shared.MapPoint;
import program.shared.MapRoadSegment;

/**
 * Handles events that occurs when using the application
 */
public class MapEventHandler {
    private final Controller controller;
    private final ModelContact model;
    private final Canvas canvas;
    private Affine trans;

    private Point2D lastMousePosition;
    private Point2D lastMouseClickPosition;
    private double timeSinceLastMove;

    private String lastSelectedAddress;

    public MapEventHandler(Controller controller, ModelContact model) {
        this.controller = controller;
        this.model = model;
        canvas = controller.getCanvas();
        prepareFields();
        setListeners();
    }

    private void prepareFields(){
        lastMousePosition = new Point2D(0, 0);
        lastMouseClickPosition = new Point2D(0, 0);
        timeSinceLastMove = 0;
    }

    private void setListeners(){
        addReferencePointPreparer();
        addPan();
        addZoom();
        addShowClosestRoad();
        addClickRoadToShowInCLI();
        addCLIRemoveHelpText();
    }

    /**
     * Adds a listener that saves a reference point for mouse presses used by other methods
     */
    private void addReferencePointPreparer(){
        canvas.setOnMousePressed(e -> {
            lastMouseClickPosition = new Point2D(e.getX(), e.getY());
            lastMousePosition = new Point2D(e.getX(), e.getY());
        });
    }

    /**
     * Adds pan when the mouse is dragged
     */
    private void addPan(){
        canvas.setOnMouseDragged(e -> {
            if (e.isPrimaryButtonDown()) {
                double dx = e.getX() - lastMousePosition.getX();
                double dy = e.getY() - lastMousePosition.getY();
                controller.pan(dx, dy);
                controller.draw();
            }

            lastMousePosition = new Point2D(e.getX(), e.getY());
        });
    }

    /**
     * Adds zoom when scrolling
     */
    private void addZoom(){
        canvas.setOnScroll(e -> {
            double factor = e.getDeltaY();
            controller.zoom(e.getX(), e.getY(), Math.pow(1.01, factor));
            controller.draw();
        });
    }

    /**
     * Adds a listener that updates the display with the name of the road closest to the mouse
     */
    private void addShowClosestRoad(){
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
    }

    /**
     * Adds a listener that detects if the mouse is clicked and fills in the most nearby road in the command line
     */
    private void addClickRoadToShowInCLI(){
        canvas.setOnMouseReleased(e -> {
            trans = controller.getAffine();
            // Check if current mouse position is different to the last click
            if (e.getX() != lastMouseClickPosition.getX() || e.getY() != lastMouseClickPosition.getY()) return;

            try {
                // Use this to find the nearest road
                Point2D mouseToPoint = trans.inverseTransform(lastMouseClickPosition.getX(), lastMouseClickPosition.getY());
                MapPoint queryPoint = new MapPoint((float) mouseToPoint.getX(), (float) -mouseToPoint.getY(), "");
                MapRoadSegment nearestRoad = (MapRoadSegment) model.nearestNeighbor(queryPoint);
                TextField textField = controller.getTextField();
                clearTextField();
                if (lastSelectedAddress != null && textField.getText().contains(lastSelectedAddress)) {
                    textField.setText(textField.getText().replace(lastSelectedAddress, nearestRoad.getName()));
                } else {
                    controller.getTextField().setText(textField.getText() + nearestRoad.getName());
                }
                controller.getTextField().end();
                lastSelectedAddress = nearestRoad.getName();
            } catch (NonInvertibleTransformException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    /**
     * Adds a listener that detects mouseclicks on CLI to remove filler text
     */
    private void addCLIRemoveHelpText() {
        controller.getTextField().setOnMousePressed(e -> {
            clearTextField();
        });
    }

    /**
     * Helper function to clear initial text in CLI textfield
     */
    private void clearTextField() {
        TextField textField = controller.getTextField();
        if (textField.getText().startsWith("Use !help")) textField.setText("");
    }

}
