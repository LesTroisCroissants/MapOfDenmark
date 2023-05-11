package program.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import program.shared.MapElement;
import program.shared.MapPoint;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RTreeTest {
    // insert (implicit)
    RTree rTree;

    @Test
    void basicQueryTest(){
        List<MapElement> query = rTree.query(new float[]{5,4}, new float[]{6, 5});
        for (MapElement element : query) {
            System.out.println(element.getType());
        }
        assertEquals(6, query.size());

    }

    @Test
    void queryTest(){
        RTree rTree = new RTree(4);
        MapPoint[] points = {
                new MapPoint(0,1,"a"), new MapPoint(1,0,"b"),
                new MapPoint(2,3,"c"), new MapPoint(5,4,"d"),
                new MapPoint(3,1,"e"), new MapPoint(2,2,"f"),
                new MapPoint(10,5,"g"), new MapPoint(12,3,"h"),
                new MapPoint(11,0,"i")
        };
        for (MapPoint point : points) {
            rTree.insert(point);
        }

        List<MapElement> query = rTree.query(new float[]{5,4}, new float[]{6, 5});
        for (MapElement element : query) {
            System.out.println(element.getType());
        }
        assertEquals(6, query.size());

    }

    @Test
    void findNearestNeighborTest() {
    }

    @Test
    void hasOverlapTest() {
    }

    @BeforeEach
    void setUp() {
        RTree rTree = new RTree(4);
        MapPoint[] points = {
                new MapPoint(0,1,"a"), new MapPoint(1,0,"b"),
                new MapPoint(2,3,"c"), new MapPoint(5,4,"d"),
                new MapPoint(3,1,"e"), new MapPoint(2,2,"f"),
                new MapPoint(10,5,"g"), new MapPoint(12,3,"h"),
                new MapPoint(11,0,"i")
        };
        for (MapPoint point : points) {
            rTree.insert(point);
        }

    }

    @AfterEach
    void tearDown() {
    }
}