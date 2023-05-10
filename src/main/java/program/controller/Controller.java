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
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import program.model.Model;
import program.model.ModelContact;
import program.shared.MapElement;
import program.shared.MapPoint;
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
    private MapEventHandler mapEventHandler;


    @FXML
    private Label zoomLevel;

    @FXML
    private Label errorLabel;

    @FXML
    private Label nearestRoad;

    @FXML
    public Canvas canvas;

    @FXML
    private TextField textField;

    @FXML
    public GridPane gridpane;

    GraphicsContext graphicsContext;

    private Affine trans;

    private Point2D localBoundMin;
    private Point2D localBoundMax;

    float[] drawingBoundMin;
    float[] drawingBoundMax;

    private List<MapElement> focusedElements;
    private int zoomValue;

    private boolean debug;
    private int debugScreenIndent = 120;

    public static Controller getInstance(){
        if (instance == null) throw new RuntimeException();
        return instance;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        view = View.getInstance();
        initializeCanvas();
        initializeGridpane();
        trans = new Affine();

        zoomValue = 0;
        focusedElements = new ArrayList<>();

        // Handle Exceptions some day lol
        try {
            model = new Model();
        } catch (XMLStreamException | ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
        commandExecutor = new CommandExecutor(model);
        mapEventHandler = new MapEventHandler(this, model);

        initView();
    }

    public void initView(){
        zoomValue = 0;
        focusedElements = new ArrayList<>();
        trans = new Affine();
        pan(-0.56*model.getMinLon(), model.getMaxLat());
        zoom(0, 0, canvas.getHeight() / (model.getMaxLat() - model.getMinLat()));
        updateCanvasBounds();
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
                errorLabel.setText("Command accepted");
                commandExecutor.executeCommand(textField.getCharacters().toString());
            } catch (CommandParser.IllegalCommandException | IllegalArgumentException ice) {
                errorLabel.setText(ice.getMessage());
            } /*catch (NullPointerException npe){
                errorLabel.setText("No directions");
            }*/ catch (Exception e) {
                errorLabel.setText(e.getMessage());
            }
            textField.setText("");
        }
    }

    public void draw() {
        graphicsContext.setTransform(new Affine());
        graphicsContext.setLineWidth(1/Math.sqrt(trans.determinant()));

        model.getTheme().prepareDraw(graphicsContext, "land", trans.determinant());
        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        graphicsContext.setTransform(trans);

        zoomValue = (int) graphicsContext.getTransform().getMxx();
        model.setDrawingArea(drawingBoundMin, drawingBoundMax, zoomValue);

        for (MapElement e : model.getElementsToDraw()) {
            model.getTheme().prepareDraw(graphicsContext, e.getType(), trans.determinant());
            e.draw(graphicsContext);
        }

        model.getTheme().prepareDraw(graphicsContext, "highlighted", trans.determinant());
        if (model.getPlannedRoute() != null) {
            for (MapElement e : model.getPlannedRoute()) {
                e.draw(graphicsContext);
            }
        }

        for (MapElement e : focusedElements) {
            e.draw(graphicsContext);
        }

        if (debug) {
            graphicsContext.strokeRect(localBoundMin.getX(), localBoundMin.getY(),
                localBoundMax.getX()-localBoundMin.getX(),
                localBoundMax.getY()-localBoundMin.getY());
        }
    }

    public void clearSelection() {
        focusedElements.clear();
        model.clearRoute();
        draw();
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

    public void showPOIListPopup(Iterable<String> POIList) {
        view.showPOIListPopup(POIList);
    }


    private void updateCanvasBounds() {
        Bounds bounds = canvas.getBoundsInLocal();

        try {
            /*
                Get current bounds of the Canvas without transformations to represent
                points as X and Y / lon and lat
             */
            if (debug) {
                localBoundMin = trans.inverseTransform(bounds.getMinX() + debugScreenIndent, bounds.getMinY() + debugScreenIndent);
                localBoundMax = trans.inverseTransform(bounds.getMaxX() - debugScreenIndent, bounds.getMaxY() - debugScreenIndent);
            } else {
                localBoundMin = trans.inverseTransform(bounds.getMinX(), bounds.getMinY());
                localBoundMax = trans.inverseTransform(bounds.getMaxX(), bounds.getMaxY());
            }

            // Get reference points for query in R-tree, where
            drawingBoundMin = new float[]{ (float)localBoundMin.getX(), (float)-localBoundMax.getY() };
            drawingBoundMax = new float[]{ (float)localBoundMax.getX(), (float)-localBoundMin.getY() };

        } catch (NonInvertibleTransformException e) {
            // Not sure how to handle this if it ever happens
            throw new RuntimeException(e.getMessage());
        }
    }

    private void focusElement(MapPoint element, boolean clean) {
        if (clean) focusedElements.clear();
        focusedElements.add(element);
        panTo(element, 80);
    }

    public void panTo(MapPoint element, int factor) {
        for (int i = 0; i < 2; i++) { // we assume that we have to do this twice because of JavaFX funkiness
            trans = new Affine();
            var screenCenterX = (localBoundMax.getX() - localBoundMin.getX())/2;
            var screenCenterY = (localBoundMax.getY() - localBoundMin.getY())/2;
            pan(-element.getMinPoint()[0] + screenCenterX, element.getMinPoint()[1] + screenCenterY);
            zoom(0, 0, canvas.getHeight() / (model.getMaxLat() - model.getMinLat()));
            zoom(0, 0, factor);
            draw();
        }
    }

    public void focusElement(MapPoint element) {
        focusElement(element, false);
    }

    public void pan(double dx, double dy) {
        trans.prependTranslation(dx, dy);
        updateCanvasBounds();
    }

    void zoom(double dx, double dy, double factor) {
        int MAX_ZOOM_LEVEL = 1_050_000;
        double newMxx = trans.getMxx() * factor;
        if ((newMxx < (canvas.getHeight() - debugScreenIndent / (model.getMaxLat() - model.getMinLat())) || newMxx > MAX_ZOOM_LEVEL)) {
            return;
        }

        pan(-dx, -dy);
        trans.prependScale(factor, factor);
        zoomValue = (int) graphicsContext.getTransform().getMxx();
        zoomLevel.setText("Zoom level: " + Math.round(zoomValue / 100F));
        pan(dx, dy);

    }

    public Canvas getCanvas() { return canvas; }
    public Affine getAffine() { return trans; }
    public Label getNearestRoadLabel() { return nearestRoad; }
    public TextField getTextField () { return textField; }

    public void setErrorLabelText(String s){
        errorLabel.setText(s);
    }

    public void setDebug(boolean b) {
        debug = b;
        updateCanvasBounds();
        draw();
    }
}

