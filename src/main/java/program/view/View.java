package program.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import program.App;

import java.io.IOException;

/**
 * Responsible for loading fxml and managing pop-ups
 */

public class View implements ViewContact {
    private static ViewContact instance;
    private Stage helpPopup;
    private Stage infoPopup;
    private Stage directionsPopup;
    private Stage poiListPopup;
    private boolean helpPopupOpen = false;
    private boolean infoPopupOpen = false;
    private boolean directionsPopupOpen = false;
    private boolean poiListPopupOpen = false;
    /**
     * Instantiates the view or throws an exception if it has already been instantiated
     * @param primaryStage
     * @throws IOException
     */
    public static void instantiateView(Stage primaryStage) throws IOException {
        if (instance != null) throw new RuntimeException();
        else new View(primaryStage);
    }

    private View(Stage primaryStage) throws IOException {
        instance = this;
        Scene scene = loadFXML("view.fxml");
        configureStage(primaryStage, scene);
    }

    /**
     * Returns the singleton instance of View
     * @return
     */
    public static ViewContact getInstance(){
        if (instance == null) throw new RuntimeException();
        return instance;
    }

    /**
     * Loads the fxml from a given path and returns a scene based off the GridPane root of the fxml file.
     * Binds the window size between the GridPane and Scene
     * @param fxmlPath
     * @return
     * @throws IOException
     */
    private Scene loadFXML(String fxmlPath) throws IOException {
        //FXMLLoader also creates controller
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxmlPath));
        GridPane root = fxmlLoader.load();
        Scene scene = new Scene(root);
        bindWindowSize(root, scene);
        return scene;
    }

    private void bindWindowSize(GridPane root, Scene scene){
        root.prefWidthProperty().bind(scene.widthProperty());
        root.prefHeightProperty().bind(scene.heightProperty());
    }

    /**
     * Configures the stage by setting dimensions and listeners and showing it.
     * @param primaryStage
     * @param scene
     */
    private void configureStage(Stage primaryStage, Scene scene){
        setStageDimensions(primaryStage, 500, 500);
        setStageListeners(primaryStage);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setStageDimensions(Stage primaryStage, int minWidth, int minHeight){
        primaryStage.setMinHeight(minHeight);
        primaryStage.setMinWidth(minWidth);
    }

    private void setStageListeners(Stage primaryStage){
        // close all open windows of the program when the main window is closed
        primaryStage.setOnHidden(event -> {
            if (helpPopup != null) helpPopup.close();
            if (infoPopup != null) infoPopup.close();
            if (directionsPopup != null) directionsPopup.close();
            if (poiListPopup != null) poiListPopup.close();
        });
    }


    public void showHelpPopup(){
        if (!helpPopupOpen){
            createHelpPopup();
            helpPopup.show();
            helpPopupOpen = true;
        } else {
            throw new IllegalArgumentException("Help pop-up is already open");
        }
    }

    public void showInfoPopup(){
        if (!infoPopupOpen) {
            createInfoPopup();
            infoPopup.show();
            infoPopupOpen = true;
        } else {
            throw new IllegalArgumentException("Info pop-up is already open");
        }
    }

    public void showDirectionsPopup(Iterable<String> directions){
        if (!directionsPopupOpen) {
            createDirectionsPopup(directions);
            directionsPopup.show();
            directionsPopupOpen = true;
        } else {
            throw new IllegalArgumentException("Directions pop-up is already open");
        }
    }

    public void showPOIListPopup(Iterable<String> POIList){
        if (!poiListPopupOpen) {
            createPOIListPopup(POIList);
            poiListPopup.show();
            poiListPopupOpen = true;
        }
    }

    private void createPOIListPopup(Iterable<String> poiList){
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefHeight(400);
        textArea.setPrefWidth(400);

        boolean isEmpty = true;

        for (String poi : poiList) {
            isEmpty = false;
            textArea.appendText(poi);
            textArea.appendText(System.lineSeparator());
        }

        if (isEmpty) {
            textArea.appendText("No POIs found");
        }

        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setPrefSize(400, 400);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane);

        poiListPopup = new Stage();
        poiListPopup.setScene(scene);

        // set height and width
        poiListPopup.setResizable(false);
        poiListPopup.setWidth(400);
        poiListPopup.setHeight(400);
        poiListPopup.initModality(Modality.NONE);

        poiListPopup.setOnHidden(event -> {
            poiListPopupOpen = false;
            poiListPopup.close();
        });
        poiListPopup.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.Q == event.getCode() || KeyCode.ESCAPE == event.getCode()) {
                poiListPopup.close();
            }
        });
    }

    private void createDirectionsPopup(Iterable<String> directions){
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefHeight(400);
        textArea.setPrefWidth(400);

        for (String direction : directions) {
            textArea.appendText(direction);
            textArea.appendText(System.lineSeparator());
        }

        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setPrefSize(400, 400);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        Scene scene = new Scene(scrollPane);

        directionsPopup = new Stage();
        directionsPopup.setScene(scene);

        // set height and width
        directionsPopup.setResizable(false);
        directionsPopup.setWidth(400);
        directionsPopup.setHeight(400);
        directionsPopup.initModality(Modality.NONE);

        directionsPopup.setOnHidden(event -> {
            directionsPopupOpen = false;
            directionsPopup.close();
        });
        directionsPopup.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.Q == event.getCode() || KeyCode.ESCAPE == event.getCode()) {
                directionsPopup.close();
            }
        });

    }

    private void createHelpPopup(){
        //region PopupUI
        TextFlow textFlow = new TextFlow();
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("On the map drag while holding down the left mouse button to pan and use the scroll wheel to zoom in and out."));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("The input field constitutes the means with which the program is interacted with, for functionality other than viewing, zooming and panning. By typing an address, the map will find, show and select the address. It also accepts the following commands and their abbreviations shown in parentheses:"));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("!about"));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("   Shows information about the program such as version and development team."));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("!help (!h)"));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("   Shows this help menu."));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("!instructions (!i)"));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("   Shows instructions on how to get to your destination once a path has been found."));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("!q"));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("   Deselects a selected point."));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("!display (!d)"));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("   Changes the visual style of the map to one specified after the command. The available themes are: brook | mint | sakura | verdant"));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("!walk (!w), !bike (!b) and !car (!c)"));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("   Sets the current mode of transportation used for route planning to either walking, biking or driving."));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("!as"));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("   When !as is written between an address and an ID it marks the address as a point of interest with the given ID, which henceforth suffices to refer to this address."));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("!poi (!p)"));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("   Displays the points of interest currently defined in the program."));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("!to (!t)"));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("   Proposes a route from the currently selected address TO one written after the command."));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("!from (!f)"));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("   Proposes a route to the currently selected address FROM one written after the command."));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("!steps (!s)"));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("   Shows the itinerary of a currently proposed route in details."));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("!load (!l)"));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("   Launches a file-selector window where a .zip or .obj file of a desired map can be chosen and loaded."));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("!debug"));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("   Starts the debug view."));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        //endregion

        ScrollPane scrollPane = new ScrollPane(textFlow);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToWidth(true);

        Scene helpScene = new Scene(scrollPane,400, 600);

        helpPopup = new Stage();

        // set height and width
        helpPopup.setResizable(false);
        helpPopup.setWidth(500);
        helpPopup.setHeight(500);

        helpPopup.initModality(Modality.NONE);
        helpPopup.setTitle("Help page");
        helpPopup.setScene(helpScene);

        helpPopup.setOnHidden(event -> {
            helpPopupOpen = false;
            helpPopup.close();
        });
        helpPopup.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.Q == event.getCode() || KeyCode.ESCAPE == event.getCode()) {
                helpPopup.close();
            }
        });
    }

    private void createInfoPopup(){
        //region PopupUI
        TextFlow textFlow = new TextFlow();
        textFlow.setTextAlignment(TextAlignment.CENTER);
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("#{FYPMAP} version 1.0"));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("Designed and developed by Annabell Nørdam, Johan Brandi, Niklas Christensen, Olivier-Baptiste Hansen and Philip Pedersen."));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text(System.lineSeparator()));
        textFlow.getChildren().add(new Text("This program was developed in context of the course \"First-year Project: Map of Denmark. Visualization, Navigation, Searching, and Route Planning\" in tandem with the course \"Algorithms and Data Structures\" at the IT-University of Copenhagen in 2023."));
        //endregion
        infoPopup = new Stage();
        // set height and width
        infoPopup.setResizable(false);
        infoPopup.setWidth(500);
        infoPopup.setHeight(200);
        infoPopup.initModality(Modality.NONE);
        infoPopup.setTitle("Program Information");
        Scene infoScene = new Scene(textFlow);
        infoPopup.setScene(infoScene);

        infoPopup.setOnHidden(event -> {
            infoPopupOpen = false;
            infoPopup.close();
        });
        infoPopup.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.Q == event.getCode() || KeyCode.ESCAPE == event.getCode()) {
                infoPopup.close();
            }
        });
    }
}
