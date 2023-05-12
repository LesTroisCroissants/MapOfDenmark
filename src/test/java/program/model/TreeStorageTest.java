package program.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import program.shared.MapElement;
import program.shared.MapPoint;
import program.shared.MapRoadSegment;

import java.util.List;

import static program.model.TreeStorage.detail;
import static org.junit.jupiter.api.Assertions.*;

class TreeStorageTest {

    TreeStorage storage;

    @BeforeEach
    void setUp() {
        storage = new TreeStorage();
    }

    @AfterEach
    void tearDown() {
        storage = null;
    }

    private void insertElements(){
        MapPoint[] points = {
                new MapPoint(0,1,"building"), new MapPoint(1,0,"primary"), // a, b
                new MapPoint(2,3,"highway"), new MapPoint(5,4,"secondary"), // c, d
                new MapPoint(3,1,"tertiary"), new MapPoint(2,2,"building"), // e, f
                new MapPoint(10,5,"secondary"), new MapPoint(12,3,"coastline"), // g, h
                new MapPoint(11,0,"coastline") // i
        };
        for (MapPoint point : points) {
            storage.insert(point, point.getType());
        }
    }

    private void insertVerticesAndRoads(){
        Vertex c = new Vertex(2,2);
        Vertex d = new Vertex(2,0);
        Vertex e = new Vertex(4,4);

        storage.insert(new MapRoadSegment(c, d,"G","primary",1,true,false), "highway");
        storage.insert(new MapRoadSegment(c, e,"I","secondary",1,true,false), "highway");
        storage.insert(new MapRoadSegment(c, e,"I","tertiary",1,true,false), "highway");
        storage.insert(new MapRoadSegment(c, e,"I","highway",1,true,false), "highway");
        storage.insert(new MapRoadSegment(c, e,"I","primary",1,true,false), "highway");
    }

    @Test
    void queryLowDetailTest() {
        insertElements();
        List<String> correct = List.of(new String[]{"12.0 3.0", "11.0 0.0"});

        List<MapElement> query = storage.query(new float[]{0,0}, new float[]{20, 20},detail.LOW);

        int count = 0;
        for (MapElement element : query) {
            if (correct.contains(element.toString())){
                count++;
            }
        }
        assertEquals(2, count);
    }

    @Test
    void queryMediumDetailTest() {
        insertElements();
        List<String> correct = List.of(new String[]{"12.0 3.0", "11.0 0.0", "10.0 5.0", "5.0 4.0", "3.0 1.0", "1.0 0.0"});

        List<MapElement> query = storage.query(new float[]{0,0}, new float[]{20, 20},detail.MEDIUM);

        int count = 0;
        for (MapElement element : query) {
            if (correct.contains(element.toString())){
                count++;
            }
        }
        assertEquals(6, count);
    }

    @Test
    void queryHighDetailTest() {
        insertElements();
        List<String> correct = List.of(new String[]{"12.0 3.0", "11.0 0.0", "10.0 5.0", "5.0 4.0", "3.0 1.0", "1.0 0.0", "0.0 1.0", "2.0 3.0", "2.0 2.0"});

        List<MapElement> query = storage.query(new float[]{0,0}, new float[]{20, 20},detail.HIGH);

        int count = 0;
        for (MapElement element : query) {
            if (correct.contains(element.toString())){
                count++;
            }
        }
        assertEquals(9, count);
    }


    @Test
    void nearestNeighborTest() {
        insertVerticesAndRoads();
        MapRoadSegment nearest = storage.nearestNeighbor(new MapPoint(1.5F,1F,"highway"));
        assertEquals("G", nearest.getName());
    }

    @Test
    void nearestVertexTest() {
        insertVerticesAndRoads();
        Vertex nearest = storage.nearestVertex(new MapPoint(5F,7F,"highway"));
        assertEquals("4.0 4.0", nearest.toString());
    }
}