package program.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import program.shared.Address;
import program.shared.MapElement;
import program.shared.MapPoint;
import program.shared.Point;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class POIRegistryTest {
    POIRegistry poi;
    String[] ids = {"lasagne", "lagkage","legomand","loppegift", "æble"};


    @Test
    void putPOIAddress() {
        String id = "Work";
        Address address = new Address("street", "house-number", "zip-code", "city");
        poi.putPOI(id, address);

        assertEquals(poi.getPOI("Work").toString(), address.toString());
    }

    @Test
    void removePOI() {
        String id = ids[0];
        poi.removePOI(id);
        for (String s : poi.getIds())
            if (s.equals(id))
                fail();

        assert(true);
    }
    @Test
    void removeInvalidPOI() {
        try {
            poi.removePOI("Smertestillende");
            fail();
        } catch (IllegalArgumentException e){
            assertEquals(e.getMessage(),"No point of interest of that name has been set");
        }
    }

    @Test
     void getInvalidPOI() {
        try{
            Address element = poi.getPOI("Smertestillende");
            fail();
        }catch(IllegalArgumentException e){
            assertEquals(e.getMessage(),"No point of interest of that name has been set");
        }
    }

    @Test
    void connectionBetweenIdAndLocationTest(){
        for (String id : ids) {
            assertEquals(id, poi.getPOI(id).getStreet());
        }
    }

    /**
     * Tests that all ids returned are different
     */
    @Test
    void getIds() {
        Set<String> idSet = new HashSet<>();

        for (String s : poi.getIds()) {
            if (idSet.contains(s)) fail();
            else idSet.add(s);
        }

        assertTrue(true);
    }


    /**
     * Tests that all locations returned are different
     */
    @Test
    void getLocationsTest() {
        Set<Address> addresses = new HashSet<>();

        for (Address address : poi.getLocations()){
            if (addresses.contains(address)) fail();
            else addresses.add(address);
        }

        assertTrue(true);
    }

    //Test for MapPointCreation

    @BeforeEach
    void setUp() {
        poi = POIRegistry.getInstance();

        for (String id : ids) {
            Address address = new Address(id, "house-number", "zip-code", "city");
            poi.putPOI(id, address);
        }
    }

    @AfterEach
    void tearDown(){ //Two loops to avoid exception
        Set<String> ids = new HashSet<>();

        for (String s : poi.getIds())
            ids.add(s);

        for (String s : ids){
            poi.removePOI(s);
        }
    }
}