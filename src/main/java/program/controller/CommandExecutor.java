package program.controller;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import program.model.Model;
import program.model.ModelContact;
import program.shared.MapElement;
import program.shared.MapPoint;
import program.shared.Point;
import program.view.ViewContact;

import java.io.File;

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
        MapPoint me = model.addressSearch(address);
        selectElement(me);
    }

    public void planRoute(String address, boolean to) throws CommandParser.IllegalCommandException {
        if (selectedElement == null) throw new CommandParser.IllegalCommandException("Cannot find path to " + address + " with no start destination.");
        MapPoint me = model.addressSearch(address);
        MapPoint middlePoint = calculateMiddlePoint(selectedElement.getMaxPoint(), me.getMaxPoint());

        if (to){
            model.planRoute(selectedElement, me);
        }
        else model.planRoute(me, selectedElement);
        controller.draw();
        controller.focusElement(middlePoint);
        controller.setZoomLevel(0);
    }

    public MapPoint calculateMiddlePoint(float[] from, float[] to){
        return new MapPoint((to[0] - from[0])/2 + from[0],  (to[1] - from[1])/2 + from[1], "");
    }

    private void selectElement(MapPoint element){
        selectedElement = element;
        controller.focusElement(element);
    }

    public void quitSelection(){
        selectedElement = null;
    }

    public void setPOI(String id, String address){
        model.setPOI(id, address);
    }

    //Display-related methods

    public void displayPOIs(){
        controller.showPOIListPopup(model.getPOIs());
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

    public void load() {
        String defaultFilePath = "src/main/data";
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(defaultFilePath));
        Stage chooserStage = new Stage();
        File selectedFile = fileChooser.showOpenDialog(chooserStage);
        String filePath = selectedFile.getAbsolutePath();
        String fileName = selectedFile.getName();
        model.loadNewFile(filePath);
        controller.setErrorLabelText("loaded file: " + fileName);
        controller.initView();
    }
}
