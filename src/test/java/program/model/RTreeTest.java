package program.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import program.shared.MapElement;
import program.shared.MapPoint;
import program.shared.MapRoadSegment;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RTreeTest {
    // insert (tested implicitly with query)
    RTree rTree;

    // Query for some known points and check that these are returned by the R-tree query.
    // Other points are also allowed to be returned, but do not matter in this basic test.
    @Test
    void basicQueryTest(){
        rTree = new RTree(4);
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

        List<String> correct = List.of(new String[]{"a", "b", "e", "f"});
        List<MapElement> query = rTree.query(new float[]{0,0}, new float[]{3, 2});

        int count = 0;
        for (MapElement element : query) {
            if (correct.contains(element.getType())){
                count++;
            }
        }
        assertEquals(4, count);
    }

    @Test
    void findNearestNeighborTest() {
        rTree = new RTree(4);

        Vertex a = new Vertex(0,0);
        Vertex b = new Vertex(0,2);

        Vertex c = new Vertex(2,2);
        Vertex d = new Vertex(2,0);

        Vertex e = new Vertex(4,4);
        Vertex f = new Vertex(4,2);

        MapRoadSegment roadF = new MapRoadSegment(a, b, "F", "test", 1, true, false);
        MapRoadSegment roadH = new MapRoadSegment(b, c,"H","test",1,true,false);
        MapRoadSegment roadG = new MapRoadSegment(c, d,"G","test",1,true,false);
        MapRoadSegment roadI = new MapRoadSegment(c, e,"I","test",1,true,false);
        MapRoadSegment roadJ = new MapRoadSegment(e, f, "J", "test", 1, true, false);

        rTree.insert(roadF);
        rTree.insert(roadH);
        rTree.insert(roadG);
        rTree.insert(roadI);
        rTree.insert(roadJ);

        MapRoadSegment nearest = rTree.findNearestNeighbor(new MapPoint(1.5F,1,"type"));
        assertEquals("G", nearest.getName());
    }

}