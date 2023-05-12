package program.controller;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import program.model.ModelContact;
import program.shared.Address;
import program.shared.MapPoint;

import java.io.File;
import java.util.Arrays;

import static program.model.Model.MOT;
public class CommandExecutor {
    Controller controller;
    ModelContact model;
    MapPoint selectedElement;
    boolean debug = false;

    public CommandExecutor(ModelContact model) {
        controller = Controller.getInstance();
        this.model = model;
        CommandParser.setCommandExecutor(this);
    }

    /**
     * Parses and executes a command
     * @param command a string containing the command
     * @throws CommandParser.IllegalCommandException when the command given is not part of the system or with wrong number of arguments
     */
    public void executeCommand(String command) throws CommandParser.IllegalCommandException {
        CommandParser.parseCommand(command);
    }

    /**
     * Performs the address search command; that is returns a POI matching the query or asking model for the address point
     * @param address address to be searched for
     */
    public void addressSearch(String address){
        quitSelection();

        // use POI if possible
        Address poiAddress = model.checkPOIRegistry(address);
        MapPoint mapPoint = (poiAddress != null) ? model.addressSearch(poiAddress.toString()) : model.addressSearch(address);

        selectElement(mapPoint);
    }

    /**
     * Performs the plan route command from/to a previously selected MapElement to/from a given address
     * @param address address to be used as either source or destination depending on to
     * @param to indicates if the search conducted should be forward (true) or backward (false)
     * @throws CommandParser.IllegalCommandException thrown when no MapElement has been selected previously
     */
    public void planRoute(String address, boolean to) throws CommandParser.IllegalCommandException {
        if (selectedElement == null) throw new CommandParser.IllegalCommandException("Cannot find path to " + address + " with no start destination.");

        // use POI if possible
        Address poiAddress = model.checkPOIRegistry(address);
        MapPoint point = (poiAddress != null) ? model.addressSearch(poiAddress.toString()) : model.addressSearch(address);

        if (to) model.planRoute(selectedElement, point);
        else model.planRoute(point, selectedElement);

        controller.draw();
        controller.panTo(model.getMiddlePoint(), 2);
    }

    /**
     * Selects the element for use in e.g. pathfinding
     * @param element element to be selected
     */
    private void selectElement(MapPoint element){
        selectedElement = element;
        controller.focusElement(element);
    }

    /**
     * Deselects any selected elements
     */
    public void quitSelection(){
        selectedElement = null;
        controller.clearSelection();
    }

    /**
     * Performs the set POI command; that is saves an association between the ID and an address
     * @param id identifying string for the POI
     * @param address address associated with the ID
     */
    public void setPOI(String id, String address){
        model.setPOI(id, address);
    }

    /**
     * Creates a popup displaying currently set POIs
     */
    public void displayPOIs(){
        controller.showPOIListPopup(model.getPOIs());
    }

    /**
     * Creates a popup displaying information about the program
     */
    public void displayProgramInformation(){
        controller.showInfoPopup();
    }

    /**
     * Creates a popup displaying a reference for using the program
     */
    public void getHelp(){
        controller.showHelpPopup();
    }

    /**
     * Creates a popup displaying navigation instructions for a currently planned route
     */
    public void displayInstructions(){
        controller.showInstructionsPopup(model.getInstructions());
    }

    /**
     * Switches the display style to a given theme
     * @param theme theme to switch to
     */
    public void setDisplay(String theme){
        model.setTheme(theme);
        controller.draw();
    }

    /**
     * Sets the mode of transportation
     * @param modeOfTransportation mode of transportation to be set
     */
    public void setModeOfTransportation(MOT modeOfTransportation){
        model.setModeOfTransportation(modeOfTransportation);
    }

    /**
     * Activates debug mode
     */
    public void setDebug(String trees) {
        if (!debug) debug = true;
        else if (trees.equals("")) debug = false;
        model.setDebug(debug, Arrays.stream(trees.split(" ")).toList());
        controller.setDebug(debug);
    }

    /**
     * Performs the load command; that is opens a file navigator from which a .zip or .osm can be opened
     */
    public void load() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("src/main/data"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        model.loadNewFile(selectedFile.getAbsolutePath());
        controller.initView();
        controller.draw();
        controller.setErrorLabelText("loaded file: " + selectedFile.getName());
    }
}
