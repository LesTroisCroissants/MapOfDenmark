package program.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import program.shared.MapElement;
import program.shared.MapPoint;

import static org.junit.jupiter.api.Assertions.*;

class POIRegistryTest {/*
    POIRegistry poi;
    @BeforeEach
    void setUp() {
        poi = POIRegistry.getInstance();
    }
    @Test
    void putPOIMapElement() {
        String id = "Lasagne";
        MapPoint point = new MapPoint(55.6781590F, 12.5817710F);
        poi.putPOI(id,point);
        MapElement pointtest = poi.getPOI(id);
        assertEquals(pointtest.toString(),point.toString());
    }

    @Test
    void PutPOI() {
        String id = "Lagkage";
        MapPoint point = new MapPoint(55.6629030F, 12.6147960F);
        poi.putPOI(id,55.6629030F, 12.6147960F);
        MapElement pointtest = poi.getPOI(id);
        assertEquals(pointtest.toString(),point.toString());
    }

    @Test
    void removePOI() {
        String id = "Legomand";
        MapPoint point = new MapPoint(55.6674600F, 12.5919800F);
        poi.putPOI(id,point);
        poi.removePOI(id);
        for(String i : poi.getIds()){
            assert !i.equals(id);
        }
        assert(true);
    }
    @Test
    void removeInvalidPOI() {
        poi.removePOI("lol");
        assert(true);
    }

    @Test
     void getInvalidPOI() {
        try{
            MapElement element = poi.getPOI("Loppegift");
        }catch(IllegalArgumentException e){
            assertEquals(e.getMessage(),"No point of interest of that name has been set");
        }
    }

    @Test
    void getIds() {
        int count = 0;
        String[] ids = {"Lasagne", "Lagkage","Legomand","Loppegift"};
        MapPoint[] points = {new MapPoint(55.6781590F, 12.5817710F),new MapPoint(55.6629030F, 12.6147960F),new MapPoint(55.6674600F, 12.5919800F), new MapPoint(55.3874600F, 12.5215100F)};
        for(int i = 0; i < ids.length; i++){
            poi.putPOI(ids[i],points[i]);
        }
        Iterable<String> keys = poi.getIds();
        for(int i = 0; i < ids.length;i++){
            for(Object id: keys){
                if(ids[i].equals(id)) count++;
            }
        }
        assertEquals(count,4);
    }

    @Test
    void getLocations() {
        int count = 0;
        String[] ids = {"Lasagne", "Lagkage","Legomand","Loppegift"};
        MapPoint[] points = {new MapPoint(55.6781590F, 12.5817710F),new MapPoint(55.6629030F, 12.6147960F),new MapPoint(55.6674600F, 12.5919800F), new MapPoint(55.3874600F, 12.5215100F)};
        for(int i = 0; i < ids.length; i++){
            poi.putPOI(ids[i],points[i]);
        }
        Iterable<MapElement> values = poi.getLocations();
        for(int i = 0; i < ids.length;i++){
            for(Object value : values){
                if(points[i].toString().equals(value.toString())) count++;
            }
        }
        assertEquals(count,4);
    }
*/
}