package program.model;

import program.shared.*;
import program.view.Theme;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Model implements ModelContact{
    private TreeStorage storage;
    private AddressBook addressBook;
    private POIRegistry poiRegistry;
    private Settings settings;

    private List<DirectedEdge> edges;

    private List<MapElement> elementsToDraw;
    private List<MapRoadSegment> plannedRoute;
    private Iterable<String> instructions;

    private MapPoint middlePoint; //used for panning after route planning


    public enum MOT{
        CAR,
        BIKE,
        WALK
    }


    public Model() throws XMLStreamException, IOException, ClassNotFoundException {
        addressBook = AddressBook.getInstance();
        edges = new ArrayList<>();

        String toOpen = "src/main/data/denmark.zip";
        open(toOpen);

        poiRegistry = POIRegistry.getInstance();
        plannedRoute = null;

        settings = new Settings();
    }

    /**
     * Writes a .obj file based on the opened file
     * @param filePath name of the file to be saved
     */
    private void save(String filePath) throws IOException {
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(storage);
            out.writeObject(addressBook);
            out.writeObject(edges);
        }
    }

    /**
     * Opens a file; either .obj or .osm (.osm.zip)
     * @param filePath the path of the file to be opened
     */
    private void open(String filePath) throws IOException, XMLStreamException, ClassNotFoundException {
        addressBook.clear();
        edges.clear();
        storage = null;

        if(filePath.endsWith(".obj")){
            try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filePath)))){
                storage = (TreeStorage) in.readObject();
                AddressBook ab = (AddressBook) in.readObject();
                edges = (List<DirectedEdge>) in.readObject();

                AddressBook.setInstance(ab);
                for (DirectedEdge e : edges) {
                    e.fromVertex().addOutEdge(e);
                    e.toVertex().addInEdge(e);
                }
            }
        } else {
            storage = DataParser.parse(filePath, addressBook, edges);
            File objfile = new File(filePath + ".obj");
            if(!objfile.exists()){
                save(filePath + ".obj");
                edges.clear();
            }
        }
    }

    /**
     * Sets the elements to draw by querying the data storage
     */
    public void setDrawingArea(float[] p1, float[] p2, int zoomLevel) {
        TreeStorage.detail detail;
        // Find detail from zoomLevel
        if (zoomLevel <= 2500 && zoomLevel > 0) detail = TreeStorage.detail.LOW;
        else if (zoomLevel >= 2500 && zoomLevel < 10000) detail = TreeStorage.detail.MEDIUM;
        else detail = TreeStorage.detail.HIGH;

        elementsToDraw = storage.query(p1, p2, detail);
    }

    /**
     * Runs a bidirectional dijkstra to determine the shortest path
     * @param from start destination
     * @param to end destination
     */
    public void planRoute(MapPoint from, MapPoint to) {
        //var startTime = System.nanoTime();
        Vertex start = storage.nearestVertex(from);
        Vertex end = storage.nearestVertex(to);
        BiDirectionalDijkstra bididi = new BiDirectionalDijkstra(start, end, settings.getModeOfTransportation());
        plannedRoute = bididi.getPath();
        instructions = bididi.getInstructions();
        //System.out.println("route planning time: " + (System.nanoTime()-startTime) / 1_000_000);

        middlePoint = AuxMath.calculateMiddlePoint(from.getMaxPoint(),to.getMaxPoint());
    }

    /**
     * Returns the middle point of a currently planned route
     */
    public MapPoint getMiddlePoint() {
        return middlePoint;
    }

    /**
     * Ensures there is no current planned route
     */
    public void clearRoute() {
        if (plannedRoute != null) plannedRoute.clear();
    }

    /**
     * Returns the current planned route
     */
    @Override
    public List<MapRoadSegment> getPlannedRoute() {
        return plannedRoute;
    }

    /**
     * Returns instructions for a planned route
     */
    @Override
    public Iterable<String> getInstructions() {
        return instructions;
    }

    /**
     * Searches the data for the MapRoadSegment closest to a given point
     * @param q MapPoint for the query location
     * @return MapRoadSegment closest to the query point
     */
    @Override
    public MapRoadSegment nearestNeighbor(MapPoint q) {
        return storage.nearestNeighbor(q);
    }

    @Override
    public float getMinLon() {
        return storage.getMinLon();
    }

    @Override
    public float getMinLat() {
        return storage.getMinLat();
    }

    @Override
    public float getMaxLon() {
        return storage.getMaxLon();
    }

    @Override
    public float getMaxLat() {
        return storage.getMaxLat();
    }

    @Override
    public void setDebug(boolean debug, List<String> trees) {
        storage.setDebug(debug, trees);
    }

    /**
     * Opens a new file during the runtime of the program
     * @param filePath path to the file to be loaded
     */
    public void loadNewFile(String filePath) {
        try {
            open(filePath);
        } catch (IOException | XMLStreamException | ClassNotFoundException e) {
            throw new IllegalArgumentException("Unable to load file");
        }
    }

    /**
     * Retrieves an address from the address book and returns its MapPoint
     * @param address String representation of the address
     * @return MapPoint indicating the location of the address
     */
    @Override
    public MapPoint addressSearch(String address) {
        AddressBook addressBook = AddressBook.getInstance();
        /*
            We want to return a MapElement, as it will contain coordinates of the given address
            This will allow the view to draw on top of the element to indicate location
            As well as pan to the element.

         */
        Address parsedAddress = AddressParser.parse(address);
        MapPoint point = addressBook.addressSearch(parsedAddress);
        if (point == null) throw new AddressParser.InvalidAddressException("Given address does not exist", parsedAddress.toString());
        return point;
    }


    /**
     * Adds an address as a POI under a given ID
     * @param id key for later retrieval of the address
     * @param address string representation of the address
     */
    @Override
    public void setPOI(String id, String address) {
        Address parsedAddress = AddressParser.parse(address);
        poiRegistry.putPOI(id, parsedAddress);
    }

    /**
     * Returns all IDs for POIs
     * @return Iterable of IDs
     */
    @Override
    public Iterable<String> getPOIs() {
        return poiRegistry.getIds();
    }

    /**
     * Returns the address of a given ID or null if there are no matches
     * @param id string to check
     * @return null if there are no POIs with the ID
     */
    public Address checkPOIRegistry(String id) {
        Address address;
        try {
            address = poiRegistry.getPOI(id);
        } catch (IllegalArgumentException e) {
            return null;
        }
        return address;
    }
    public Vertex nearestVertex(MapPoint q) {
        return storage.nearestVertex(q);
    }

    /**
     * Sets the theme to a new one
     * @param theme theme to be changed to
     */
    @Override
    public void setTheme(String theme) {
        settings.setTheme(theme);
    }

    /**
     * Returns the currently set theme
     */
    @Override
    public Theme getTheme() {
        return settings.getTheme();
    }

    /**
     * Sets the mode of transportation
     * @param modeOfTransportation mode of transportation to change to
     */
    @Override
    public void setModeOfTransportation(MOT modeOfTransportation) {
        settings.setModeOfTransportation(modeOfTransportation);
    }

    /**
     * Returns a list of the currently set elements to draw
     */
    public List<MapElement> getElementsToDraw() {
        return elementsToDraw;
    }
}
