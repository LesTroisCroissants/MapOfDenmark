package program.model;

import program.shared.MapElement;
import program.shared.MapPoint;

import java.util.HashMap;
import java.util.Map;

public class POIRegistry {
    private final Map<String, MapElement> pointsOfInterest;
    private static POIRegistry registry;

    public static POIRegistry getInstance(){
        if (registry == null) registry = new POIRegistry();
        return registry;
    }

    private POIRegistry(){
        pointsOfInterest = new HashMap<>();
    }

    /**
     * Adds a POI from a given Address with a given name
     * @param id
     * @param mapElement
     */
    public void putPOI(String id, MapElement mapElement){
        pointsOfInterest.put(id, mapElement);
    }

    /**
     * Adds a POI from given coordinates with a given name
     * @param id
     * @param lat
     * @param lon
     */
    public void putPOI(String id, float lat, float lon){
        MapPoint point = new MapPoint(lat, lon, "");
        putPOI(id, point);
    }

    /**
     * Removes a POI with a given id
     * @param id
     */
    public void removePOI(String id){
        if (pointsOfInterest.containsKey(id)) pointsOfInterest.remove(id);
        else throw new IllegalArgumentException("No point of interest of that name has been set");
    }

    /**
     * Returns the MapElement corresponding to a given id or throws an exception if the id has not been set
     * @param id
     * @return
     * @throws IllegalArgumentException
     */
    public MapElement getPOI(String id) {
        if (pointsOfInterest.containsKey(id)) return pointsOfInterest.get(id);
        else throw new IllegalArgumentException("No point of interest of that name has been set");
    }

    /**
     * Returns an Iterable over all POI ids
     * @return
     */
    public Iterable<String> getIds(){
        return pointsOfInterest.keySet();
    }

    /**
     * Returns an Iterable of all MapElements associated with a POI
     * @return
     */
    public Iterable<MapElement> getLocations(){
        return pointsOfInterest.values();
    }
}
