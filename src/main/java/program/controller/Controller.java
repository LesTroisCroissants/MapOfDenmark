package program.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import program.model.Model;
import program.model.ModelContact;
import program.shared.MapElement;
import program.shared.MapPoint;
import program.shared.MapRoadSegment;
import program.shared.Point;
import program.view.View;
import program.view.ViewContact;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private static Controller instance;
    private ViewContact view;
    private ModelContact model;
    private CommandExecutor commandExecutor;


    @FXML
    private Label zoomLevel;

    @FXML
    private Label errorLabel;

    @FXML
    public Canvas canvas;

    @FXML
    private TextField textField;

    @FXML
    public GridPane gridpane;

    GraphicsContext graphicsContext;

    private Affine trans;

    private float[] drawingBoundMin;
    private float[] drawingBoundMax;

    private Point2D localBoundMin;
    private Point2D localBoundMax;

    private List<MapElement> focusedElements;
    private int zoom;

    public static Controller getInstance(){
        if (instance == null) throw new RuntimeException();
        return instance;
    }

    double lastX;
    double lastY;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        view = View.getInstance();
        initializeCanvas();
        initializeGridpane();
        trans = new Affine();

        zoom = 0;
        focusedElements = new ArrayList<>();

        // Handle Exceptions some day lol
        try {
            model = new Model();
        } catch (XMLStreamException | ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
        commandExecutor = new CommandExecutor(model);

        pan(-0.56*model.getMinLon(), model.getMaxLat());
        zoom(0, 0, canvas.getHeight() / (model.getMaxLat() - model.getMinLat()));

        /*canvas.setOnMouseMoved(e -> {
            lastX = e.getX();
            lastY = e.getY();
            Point2D mousePos = null;
            try {
                mousePos = trans.inverseTransform(lastX, lastY);
            } catch (NonInvertibleTransformException ex) {
                throw new RuntimeException(ex);
            }
            MapRoadSegment nearestRoad = (MapRoadSegment)
                    model.nearestNeighbor(new MapPoint((float) mousePos.getX(), (float) -mousePos.getY(), ""));

        });*/

        canvas.setOnMousePressed(e -> {
            lastX = e.getX();
            lastY = e.getY();

            try {
                // Use this to find the nearest road
                Point2D mousePos = trans.inverseTransform(lastX, lastY);
                MapRoadSegment nearestRoad = (MapRoadSegment)
                        model.nearestNeighbor(new MapPoint((float) mousePos.getX(), (float) -mousePos.getY(), ""));
                //System.out.println(new Point2D(mousePos.getX(), -mousePos.getY()));
            } catch (NonInvertibleTransformException ex) {
                throw new RuntimeException(ex);
            }
        });
        canvas.setOnMouseDragged(e -> {
            if (e.isPrimaryButtonDown()) {
                double dx = e.getX() - lastX;
                double dy = e.getY() - lastY;
                pan(dx, dy, true);
            }

            lastX = e.getX();
            lastY = e.getY();
        });
        canvas.setOnScroll(e -> {
            double factor = e.getDeltaY();
            zoom(e.getX(), e.getY(), Math.pow(1.01, factor));
        });
    }

    private void initializeCanvas(){
        canvas.setWidth(920);
        canvas.setHeight(530);
        graphicsContext = canvas.getGraphicsContext2D();

    }

    private void initializeGridpane(){
        gridpane.prefWidthProperty().addListener((ov, oldValue, newValue) -> {
            canvas.setWidth(gridpane.getWidth());
            updateCanvasBounds();
            draw();
        });

        gridpane.prefHeightProperty().addListener((ov, oldValue, newValue) -> {
            canvas.setHeight(gridpane.getHeight() - 63); //63 indicates the bottom padding for the search bar
            updateCanvasBounds();
            draw();
        });
    }

    public void handleKeyTyped(KeyEvent keyEvent) {
        if (keyEvent.getCharacter().equals("" + (char)13)){ // 13 is the ascii character for carriage-return, and it is being cast to char and then String
            try {
                commandExecutor.executeCommand(textField.getCharacters().toString());
                errorLabel.setText("Command accepted");
            } catch (CommandParser.IllegalCommandException ice) {
                errorLabel.setText(ice.getMessage());
            } catch (NullPointerException npe){
                errorLabel.setText("No directions");
            }
            textField.setText("");
        }
    }

    public void draw() {
        //var start = System.nanoTime();

        graphicsContext.setTransform(new Affine());
        graphicsContext.setLineWidth(1/Math.sqrt(trans.determinant()));

        model.getTheme().prepareDraw(graphicsContext, "land", trans.determinant());
        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        graphicsContext.setTransform(trans);

        zoom = (int) graphicsContext.getTransform().getMxx();
        zoomLevel.setText(String.valueOf(Math.round(zoom / 1000F)));

        model.setDrawingArea(drawingBoundMin, drawingBoundMax, zoom);

        for (MapElement e : model.getElementsToDraw()) {
            model.getTheme().prepareDraw(graphicsContext, e.getType(), trans.determinant());
            e.draw(graphicsContext);
        }

        if (model.getPlannedRoute() != null) for (MapElement e : model.getPlannedRoute()) {
            e.draw(graphicsContext);
        }

        graphicsContext.setStroke(Color.RED);
        for (MapElement e : focusedElements) {
            e.draw(graphicsContext);
        }

        graphicsContext.setStroke(Color.RED);
        /*graphicsContext.strokeRect(localBoundMin.getX(), localBoundMin.getY(),
                localBoundMax.getX()-localBoundMin.getX(),
                localBoundMax.getY()-localBoundMin.getY());*/

        //System.out.println((System.nanoTime() - start) / 1000000);
    }

    public void showHelpPopup(){
        view.showHelpPopup();
    }

    public void showInfoPopup(){
        view.showInfoPopup();
    }

    public void showInstructionsPopup(Iterable<String> instructions) {
        view.showDirectionsPopup(instructions);
    }


    void updateCanvasBounds() {
        Bounds bounds = canvas.getBoundsInLocal();

        try {
            //localBoundMin = trans.inverseTransform(bounds.getMinX() + 60, bounds.getMinY() + 60);
            //localBoundMax = trans.inverseTransform(bounds.getMaxX() - 60, bounds.getMaxY() - 60);
            localBoundMin = trans.inverseTransform(bounds.getMinX(), bounds.getMinY());
            localBoundMax = trans.inverseTransform(bounds.getMaxX(), bounds.getMaxY());

            drawingBoundMin = new float[]{ (float)localBoundMin.getX(), (float)-localBoundMax.getY() };
            drawingBoundMax = new float[]{ (float)localBoundMax.getX(), (float)-localBoundMin.getY() };
        } catch (NonInvertibleTransformException e) {
            // lol
        }
    }

    void focusElement(MapPoint element, boolean clean) {
        focusedElements.add(model.nearestNeighbor(element));
        if (clean) focusedElements.clear();
        focusedElements.add(element);
        for (int i = 0; i < 2; i++) {
            trans = new Affine();
            var screenCenterX = (localBoundMax.getX() - localBoundMin.getX())/2;
            var screenCenterY = (localBoundMax.getY() - localBoundMin.getY())/2;
            pan(-element.getMinPoint()[0] + screenCenterX, element.getMinPoint()[1] + screenCenterY);
            zoom(0, 0, canvas.getHeight() / (model.getMaxLat() - model.getMinLat()));
            zoom(0, 0, 80.0);
        }
    }

    void focusElement(MapPoint element) {
        focusElement(element, false);
    }

    void pan(double dx, double dy, boolean draw) {
        pan(dx, dy);
        if (draw) draw();
    }

    void pan(double dx, double dy) {
        trans.prependTranslation(dx, dy);
        // canvas bounds
        updateCanvasBounds();
    }

    void zoom(double dx, double dy, double factor) {
        pan(-dx, -dy);
        trans.prependScale(factor, factor);
        pan(dx, dy);
        // Figure out some good number

        draw();
    }


    /**
     * Returns a canvas that can contain map elements
     */
    public Canvas getCanvas() {
        return canvas;
    }

    public String getTextFieldText(){
        return textField.getText();
    }

    public void setTextFieldText(String s){
        textField.setText(s);
    }

    public void setZoomLevel(int zoom){
        zoomLevel.setText(Integer.toString(zoom));
    }

    public void setErrorLabel(String errorMessage){
        errorLabel.setText(errorMessage);
    }

}

