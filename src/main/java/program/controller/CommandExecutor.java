package program.controller;

import program.model.Model;
import program.model.ModelContact;
import program.model.POIRegistry;
import program.shared.MapElement;
import program.shared.MapPoint;
import program.shared.Point;
import program.view.ViewContact;

import static program.model.Model.MOT;
public class CommandExecutor {
    Controller controller;
    ModelContact model;
    MapPoint selectedElement;

    public CommandExecutor(ModelContact model) {
        controller = Controller.getInstance();
        this.model = model;
        CommandParser.setCommandExecutor(this);
    }

    public void executeCommand(String command) throws CommandParser.IllegalCommandException {
        CommandParser.parseCommand(command);
    }

    public void addressSearch(String address){
        quitSelection();
        MapPoint mapPoint = model.checkPOIRegistry(address);
        if (mapPoint == null) {
            mapPoint = model.addressSearch(address);
        }
        selectElement(mapPoint);
    }

    public void planRoute(String address, boolean to) throws CommandParser.IllegalCommandException {
        if (selectedElement == null) throw new CommandParser.IllegalCommandException("Cannot find path to " + address + " with no start destination.");
        MapPoint mapPoint = model.checkPOIRegistry(address);
        if (mapPoint == null) {
            mapPoint = model.addressSearch(address);
        }
        if (to) model.planRoute(selectedElement, mapPoint);
        else model.planRoute(mapPoint, selectedElement);
        controller.draw();
    }

    private void selectElement(MapPoint element){
        selectedElement = element;
        controller.focusElement(element);
    }

    public void quitSelection(){
        selectedElement = null;
        controller.clearSelection();

    }

    public void setPOI(String id, String address){
        model.setPOI(id, address);
    }

    //Display-related methods

    public void displayPOIs(){
        //TODO write code
    }

    public void displayProgramInformation(){
        controller.showInfoPopup();
    }

    public void getHelp(){
        controller.showHelpPopup();
    }

    public void displayInstructions(){
        controller.showInstructionsPopup(model.getInstructions());
    }

    //Settings-related methods

    public void setDisplay(String theme){
        model.setTheme(theme);
        controller.draw();
    }

    public void setModeOfTransportation(MOT modeOfTransportation){
        model.setModeOfTransportation(modeOfTransportation);
    }

    public void setDebug() {
        model.setDebug(true);
        // Use points from view
        model.setDrawingArea(new float[]{ 0, 0 }, new float[]{ 100, 100 }, 0);
        model.getElementsToDraw();
        controller.draw();
    }
}
