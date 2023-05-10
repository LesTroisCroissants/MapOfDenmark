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

    private MapPoint middlePoint;


    public enum MOT{
        CAR,
        BIKE,
        WALK
    }
    private MOT modeOfTransport = MOT.CAR;


    public Model() throws XMLStreamException, IOException, ClassNotFoundException {
        addressBook = AddressBook.getInstance();
        edges = new ArrayList<>();

        String toOpen = "src/main/data/map.zip";
        open(toOpen);

        poiRegistry = POIRegistry.getInstance();
        plannedRoute = null;

        settings = new Settings();
    }

    private void save(String fileName) throws IOException {
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(storage);
            out.writeObject(addressBook);
            out.writeObject(edges);
        }
    }

    private void open(String fileName) throws IOException, XMLStreamException, ClassNotFoundException {
        addressBook.clear();
        edges.clear();
        storage = null;

        if(fileName.endsWith(".obj")){
            try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(fileName)))){
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
            storage = DataParser.parse(fileName, addressBook, edges);
            File objfile = new File(fileName + ".obj");
            if(!objfile.exists()){
                save(fileName + ".obj");
                edges.clear();
            }
        }
    }

    public void setDrawingArea(float[] p1, float[] p2, int zoomLevel) {
        TreeStorage.detail detail;
        // Find detail from zoomLevel
        if (zoomLevel <= 2500 && zoomLevel > 0) detail = TreeStorage.detail.LOW;
        else if (zoomLevel >= 2500 && zoomLevel < 10000) detail = TreeStorage.detail.MEDIUM;
        else detail = TreeStorage.detail.HIGH;

        // detail = DataStorage.detail.MEDIUM;
        elementsToDraw = storage.query(p1, p2, detail);
    }

    public void planRoute(MapPoint from, MapPoint to) {
        var startTime = System.nanoTime();
        Vertex start = storage.nearestVertex(from);
        Vertex end = storage.nearestVertex(to);
        BiDirectionalDijkstra bididi = new BiDirectionalDijkstra(start, end, modeOfTransport);
        plannedRoute = bididi.getPath();
        instructions = bididi.getInstructions();
        System.out.println("route planning time: " + (System.nanoTime()-startTime) / 1_000_000);

        setMiddlePoint(calculateMiddlePoint(from.getMaxPoint(),to.getMaxPoint()));
    }

    private void setMiddlePoint(MapPoint middlePoint) {
        this.middlePoint = middlePoint;
    }

    public MapPoint getMiddlePoint() {
        return middlePoint;
    }

    public void clearRoute() {
        if (plannedRoute != null) plannedRoute.clear();
    }

    @Override
    public List<MapRoadSegment> getPlannedRoute() {
        return plannedRoute;
    }

    @Override
    public Iterable<String> getInstructions() {
        return instructions;
    }

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

    public void loadNewFile(String fileName) {
        try {
            open(fileName);
        } catch (IOException | XMLStreamException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private MapPoint calculateMiddlePoint(float[] from, float[] to){
        return new MapPoint((to[0] - from[0])/2 + from[0],  (to[1] - from[1])/2 + from[1], "");
    }

    @Override
    public MapPoint addressSearch(String address) {
        // When searching, should pan to MapElement / point on map if not in view.
        AddressBook addressBook = AddressBook.getInstance();
        /*
            We want to return a MapElement, as it will contain coordinates of the given address
            This will allow the view to draw ontop of the element to indicate location
            As well as pan to the element.

         */
        Address parsedAddress = AddressParser.parse(address);
        MapPoint point = addressBook.addressSearch(parsedAddress);
        if (point == null) throw new AddressParser.InvalidAddressException("Given address does not exist", parsedAddress.toString());
        return point;
    }

    @Override
    public void setPOI(String id, String address) {
        // Can keep nearest neighbor on save
        try {
            Address parsedAddress = AddressParser.parse(address);
            poiRegistry.putPOI(id, parsedAddress);
        } catch (AddressParser.InvalidAddressException e) {
            throw e;
        }
    }

    @Override
    public Iterable<String> getPOIs() {
        Iterable<String> ids = poiRegistry.getIds();
        ArrayList<String> pois = new ArrayList<>();

        for (String id : ids) {
            pois.add(id + " : " + poiRegistry.getPOI(id));
        }

        return pois;
    }

    public Address checkPOIRegistry(String id) {
        Address address = null;
        try {
            address = poiRegistry.getPOI(id);
        } catch (IllegalArgumentException e) {
            return address;
        }
        return address;
    }

    @Override
    public void setTheme(String theme) {
        settings.setTheme(theme);
    }

    @Override
    public Theme getTheme() {
        return settings.getTheme();
    }

    @Override
    public void setModeOfTransportation(MOT modeOfTransportation) {
        this.modeOfTransport = modeOfTransportation;
    }

    public List<MapElement> getElementsToDraw() {
        return elementsToDraw;
    }
}
