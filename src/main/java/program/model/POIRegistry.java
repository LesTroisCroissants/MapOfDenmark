package program.model;

import program.shared.Address;

import java.util.HashMap;
import java.util.Map;

public class POIRegistry {
    private final Map<String, Address> pointsOfInterest;
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
     * @param address
     */
    public void putPOI(String id, Address address){
        pointsOfInterest.put(id, address);
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
     * Returns the Address corresponding to a given id or throws an exception if the id has not been set
     */
    public Address getPOI(String id) {
        if (pointsOfInterest.containsKey(id)) return pointsOfInterest.get(id);
        else throw new IllegalArgumentException("No point of interest of that name has been set");
    }

    /**
     * Returns an Iterable over all POI ids
     */
    public Iterable<String> getIds(){
        return pointsOfInterest.keySet();
    }

    /**
     * Returns an Iterable of all MapElements associated with a POI
     */
    public Iterable<Address> getLocations(){
        return pointsOfInterest.values();
    }

}
