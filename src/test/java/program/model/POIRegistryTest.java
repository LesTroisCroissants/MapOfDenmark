package program.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import program.shared.MapElement;
import program.shared.MapPoint;
import program.shared.Point;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class POIRegistryTest {
    POIRegistry poi;
    String[] ids = {"Lasagne", "Lagkage","Legomand","Loppegift", "Æble"};


    @Test
    void putPOIMapElement() {
        String id = "Work";
        MapPoint point = new MapPoint(new Point(-1,-1), "");
        poi.putPOI(id,point);

        assertEquals(poi.getPOI("Work").toString(),point.toString());
    }

    @Test
    void PutPOIFromCoordinates() {
        String id = "Work";
        MapPoint point = new MapPoint(new Point(-1,-1), "");
        poi.putPOI(id,-1, -1);

        assertEquals(poi.getPOI("Work").toString(),point.toString());
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
            MapElement element = poi.getPOI("Smertestillende");
            fail();
        }catch(IllegalArgumentException e){
            assertEquals(e.getMessage(),"No point of interest of that name has been set");
        }
    }

    @Test
    void connectionBetweenIdAndLocationTest(){
        for (int i = 0; i < ids.length; i++){
            assertEquals(i, poi.getPOI(ids[i]).getMaxPoint()[0]);
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
        Set<MapElement> mapElements = new HashSet<>();

        for (MapElement mapElement : poi.getLocations()){
            if (mapElements.contains(mapElement)) fail();
            else mapElements.add(mapElement);
        }

        assertTrue(true);
    }

    //Test for MapPointCreation

    @BeforeEach
    void setUp() {
        poi = POIRegistry.getInstance();

        for (int i = 0; i < ids.length; i++)
            poi.putPOI(ids[i],i,i);
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