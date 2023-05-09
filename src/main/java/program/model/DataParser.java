package program.model;

import program.shared.*;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.*;
import java.util.zip.ZipInputStream;

public class DataParser{
    // * Take an input file
    // * Create MapElements and insert them in RTree
    // * Returns an RTree

    private static AddressBook addressBook;
    private static TreeStorage treeStorage;
    private static List<DirectedEdge> edges;

    public static TreeStorage parse(String fileName, AddressBook _addressBook, List<DirectedEdge> _edges) throws IOException, XMLStreamException, ClassNotFoundException {
        treeStorage = new TreeStorage();
        edges = _edges;
        addressBook = _addressBook;
        if (fileName.endsWith(".zip")) {
            parseZip(fileName);
        } else if (fileName.endsWith(".osm")) {
            parseOSM(fileName);
        }

        return treeStorage;
    }

    private static void parseZip(String fileName) throws IOException, XMLStreamException {
        var input = new ZipInputStream(new FileInputStream(fileName));
        input.getNextEntry();
        parseOSM(input);
    }

    private static void parseOSM(String fileName) throws FileNotFoundException, XMLStreamException, UnsupportedEncodingException {
        parseOSM(new FileInputStream(fileName));
    }


    private static List<Point> nodes;
    private static void parseOSM(InputStream inputStream) throws XMLStreamException, UnsupportedEncodingException {
        XMLStreamReader input = XMLInputFactory.newInstance().createXMLStreamReader(new InputStreamReader(inputStream, "UTF-8"));

        nodes = new ArrayList<>(15_000_000);

        AddressBuilder builder = new AddressBuilder();
        boolean insertAddress = false;
        boolean readingNodes = true;
        Point currentNode = null;

        int tagKind = -1;

        while (input.hasNext() && readingNodes) {
            tagKind = input.next();
            if (tagKind == XMLStreamConstants.START_ELEMENT) {
                String tag = input.getLocalName();
                switch (tag) {
                    case "node" -> {
                        var id = Long.parseLong(input.getAttributeValue(null, "id"));
                        var lat = Float.parseFloat(input.getAttributeValue(null, "lat"));
                        var lon = Float.parseFloat(input.getAttributeValue(null, "lon"));

                        currentNode = new Point(0.56F * lon, lat, id);
                        nodes.add(currentNode);
                    }
                    case "tag" -> {
                        var tagKey = input.getAttributeValue(null, "k");
                        if (tagKey.startsWith("addr")) {
                            var tagValue = input.getAttributeValue(null, "v");
                            switch (tagKey) {
                                case "addr:city" -> builder.setCity(tagValue);
                                case "addr:housenumber" -> builder.setHouse(tagValue);
                                case "addr:postcode" -> builder.setPostcode(tagValue);
                                case "addr:street" -> {
                                    builder.setStreet(tagValue);
                                    insertAddress = true;
                                }
                            }
                        }
                    }
                    case "bounds" -> {
                        float minLat = Float.parseFloat(input.getAttributeValue(null, "minlat"));
                        float maxLat = Float.parseFloat(input.getAttributeValue(null, "maxlat"));
                        float minLon = Float.parseFloat(input.getAttributeValue(null, "minlon"));
                        float maxLon = Float.parseFloat(input.getAttributeValue(null, "maxlon"));
                        treeStorage.setMapArea(minLat, minLon, maxLat, maxLon);
                    }
                    case "way" -> readingNodes = false;
                }
            } else if (tagKind == XMLStreamConstants.END_ELEMENT) {
                String tag = input.getLocalName();
                if (tag.equals("node")) {
                    if (insertAddress) {
                        addressBook.addAddress(
                                builder.build(),
                                currentNode
                        );
                        insertAddress = false;
                    }
                }
            }
        }

        // sorting nodes by id, to binary-search later
        Collections.sort(nodes, Comparator.comparingLong(Point::getId));

        List<Long> wayPoints = new ArrayList<>();
        boolean isRoad = false;
        boolean isElement = false;
        boolean oneway = false;
        boolean isFillableElement = false;
        String type = null;
        String subType = null;
        String roadName = "";

        boolean skipElement = false;

        // it is more common for roads to be shared these are default to avoid excessive sets/rewritings
        boolean carAllowed = true;
        boolean onlyCarAllowed = false;

        int speed = 80;

        while (input.hasNext()) {
            if (tagKind == XMLStreamConstants.START_ELEMENT) {
                String tag = input.getLocalName();
                switch (tag) {
                    case "nd" -> {
                        long id = Long.parseLong(input.getAttributeValue(null, "ref"));
                        wayPoints.add(id);
                    }
                    case "tag" -> {
                        String tagKey = input.getAttributeValue(null, "k");
                        String tagValue = input.getAttributeValue(null, "v");

                        switch (tagKey) {
                            case "highway" -> {
                                isRoad = true;
                                type = tagKey;
                                subType = tagValue;
                                switch (tagValue){
                                    case "motorway", "trunk" -> {
                                        onlyCarAllowed = true;
                                        if (tagValue.equals("motorway")){
                                            speed = 130;
                                        }
                                    }
                                    case "residential", "track" -> {
                                        speed = 50;
                                    }
                                    case "service" -> {
                                        speed = 30;
                                    }
                                    case "footway", "path", "cycleway", "bridleway", "steps", "escalator" -> {
                                        carAllowed = false;
                                    }
                                    case "proposed" -> {
                                        skipElement = true;
                                    }
                                }
                            }
                            case "maxspeed" -> {
                                try {
                                   speed = Integer.parseInt(tagValue);
                                } catch (NumberFormatException e){
                                    System.out.println("Could not parse maxspeed with tagvalue: " + tagValue);
                                    if (type == null) break;
                                    if (type.equals("motorway"))
                                        speed = 130;
                                    else
                                        speed = 80;
                                }

                                // if the speed is above 80, we assume only cars are allowed
                                if (speed > 80){
                                    onlyCarAllowed = true;
                                }
                            }
                            // cars can still drive on roads that have the cycleway or footway tag
                            case "cycleway" -> {
                                isRoad = true;
                                type = tagKey;
                                subType = tagValue;
                                roadName = "Cycle way";
                            }
                            case "footpath", "footway" -> {
                                isRoad = true;
                                type = "footpath";
                                subType = "footpath";
                                roadName = "Footpath";
                            }
                            case "access" -> {
                                if (tagValue.equals("no")){
                                    skipElement = true;
                                }
                            }
                            case "natural" -> {
                                if (tagValue.equals("coastline")) {
                                    isElement = true;
                                    type = tagKey;
                                    subType = tagValue;
                                }
                            }
                            case "oneway" -> {
                                if (tagValue.equals("yes")) oneway = true;
                            }
                            case "junction" -> {
                                if (tagValue.equals("roundabout")) oneway = true;
                            }
                            case "building" -> {
                                if (tagValue.equals("yes")) {
                                    isFillableElement = true;
                                    subType = "building";
                                }
                            }
                            // Todo: Add other map elements to draw here
                        }
                        if (isRoad && tagKey.equals("name")) {
                            roadName = tagValue.intern();
                        }
                    }
                }
            } else if (tagKind == XMLStreamConstants.END_ELEMENT) {
                String tag = input.getLocalName();
                if (tag.equals("way")) {
                    if (skipElement) {skipElement = false; continue;}
                    if (isRoad) {
                        Vertex prev = null;
                        for (Long id : wayPoints) {
                            int nodeIndex = findNodeIndex(id);

                            if (!(nodes.get(nodeIndex) instanceof Vertex)) {
                                nodes.set(nodeIndex, new Vertex(nodes.get(nodeIndex).getX(), nodes.get(nodeIndex).getY(), id));
                            }

                            Vertex current = (Vertex) nodes.get(nodeIndex);
                            if (prev == null) prev = current;
                            else {
                                treeStorage.insert(
                                        createSegment(prev, current, roadName, subType, speed, oneway, carAllowed, onlyCarAllowed), type
                                );
                                prev = current;
                            }
                        }
                    } else if (isElement) {
                        treeStorage.insert(createPath(wayPoints, subType), type);
                    } else if (isFillableElement) {
                        treeStorage.insert(createFillable(wayPoints, subType), type);
                    }
                    wayPoints.clear();
                    isRoad = false;
                    isElement = false;
                    oneway = false;
                    carAllowed = true;
                    onlyCarAllowed = false;
                    isFillableElement = false;
                    type = null;
                    subType = null;
                    speed = 80;
                }
            }
            if (input.hasNext()) tagKind = input.next();
        }
        input.close();
    }

    // O(lg2(n))
    private static int findNodeIndex(long nodeId) {
        int low = 0;
        int high = nodes.size() - 1;
        while (low <= high) {
            int middle = low + (high - low) / 2;
            if      (nodeId < nodes.get(middle).getId()) high = middle - 1;
            else if (nodeId > nodes.get(middle).getId()) low = middle + 1;
            else {
                return middle;
            }
        }
        return -1;
    }

    private static MapPath createPath(List<Long> ids, String type) {
        MapPath path = new MapPath(type, ids.size());
        for (Long id : ids){
            path.add(nodes.get(findNodeIndex(id)));
        }

        return path;
    }

    private static MapFillable createFillable(List<Long> ids, String type) {
        MapFillable fillable = new MapFillable(type, ids.size());
        for (Long id : ids){
            fillable.add(nodes.get(findNodeIndex(id)));
        }

        return fillable;
    }

    private static MapRoadSegment createSegment(Vertex a, Vertex b, String name, String subType, int speed, boolean oneway, boolean carAllowed, boolean onlyCarAllowed) {
        MapRoadSegment segment = new MapRoadSegment(a, b, name, subType, speed, carAllowed, onlyCarAllowed);

        float carWeight = segment.getDistance()/speed;

        DirectedEdge forward = new DirectedEdge(a, b, carWeight, segment);
        edges.add(forward);
        a.addOutEdge(forward);
        b.addInEdge(forward);

        DirectedEdge backward;
        if (oneway) {
            backward = new DirectedEdge(b, a, Float.POSITIVE_INFINITY, segment);
        } else {
            backward = new DirectedEdge(b, a, carWeight, segment);
        }
        edges.add(backward);
        a.addInEdge(backward);
        b.addOutEdge(backward);

        return segment;
    }
    /*
        Parser: læse fil og oversætte til kode / objekter | Skal den indsætte data?
        Storage: opbevare data og håndtere at indsætte og hente data
    */

}
