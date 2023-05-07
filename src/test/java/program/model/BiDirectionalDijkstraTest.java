package program.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import program.shared.MapRoadSegment;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static program.model.Model.MOT.*;

class BiDirectionalDijkstraTest {

    List<Vertex> vertices;
    List<MapRoadSegment> mapRoadSegments;


    @Test
    void arbitraryVerticesTest(){
        BiDirectionalDijkstra bididi = new BiDirectionalDijkstra(vertices.get(1), vertices.get(8), CAR);
        //Iterable<DirectedEdge> edges = bididi.getEdgePath();
        assertEquals(bididi.currentShortestPathLength, 115);
    }

    @Test
    void neighbourVertexTest(){
        BiDirectionalDijkstra bididi = new BiDirectionalDijkstra(vertices.get(3), vertices.get(5), CAR);
        //Iterable<DirectedEdge> edges = bididi.getEdgePath();
        assertEquals(bididi.currentShortestPathLength, 2);
    }

    @Test
    void splitPathTest(){
        BiDirectionalDijkstra bididi = new BiDirectionalDijkstra(vertices.get(6), vertices.get(9), CAR);
        //Iterable<DirectedEdge> edges = bididi.getEdgePath();
        assertEquals(bididi.currentShortestPathLength, 5);
    }

    @Test
    void sameSourceAndDestinationTest(){
        try {
            BiDirectionalDijkstra bididi = new BiDirectionalDijkstra(vertices.get(6), vertices.get(6), CAR);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }
    @Test
    void directionsTest(){
        BiDirectionalDijkstra bdd = new BiDirectionalDijkstra(vertices.get(1), vertices.get(5), CAR);


        for (MapRoadSegment m : bdd.getPath()){
            System.out.println(m.getName());
        }

        System.out.println(bdd.getInstructions());
    }

    @BeforeEach
    void setUp() {
        vertices = new ArrayList<>();
        mapRoadSegments = new ArrayList<>();

        vertices.add(new Vertex(3, 4)); // A 0
        vertices.add(new Vertex(4, 5)); // B 1
        vertices.add(new Vertex(4,3)); // C 2
        vertices.add(new Vertex(4,4)); // D 3
        vertices.add(new Vertex(5,6)); // E 4
        vertices.add(new Vertex(5,4)); // F 5
        vertices.add(new Vertex(6,4)); // G 6
        vertices.add(new Vertex(7,5)); // H 7
        vertices.add(new Vertex(7,3)); // I 8
        vertices.add(new Vertex(9,4)); // J 9
        vertices.add(new Vertex(11,5)); // K 10
        vertices.add(new Vertex(10,3)); // L 11

        addEdge(0, 1, 5, "a");
        addEdge(0, 2, 5, "b");
        addEdge(1, 4, 1, "d");
        addEdge(1, 5, 15, "f");
        addEdge(2, 5,1, "c");
        addEdge(5, 3,2, "e");
        addEdge(4, 5,15, "g");
        addEdge(5, 6,100, "h");
        addEdge(6, 7,3, "j");
        addEdge(6, 8,4, "i");
        addEdge(7, 9,2, "l");
        addEdge(8, 9,2, "k");
        addEdge(9, 10,2, "n");
        addEdge(9, 11,10, "m");
        addEdge(10, 11,2, "o");

    }

    private void addEdge(int x, int y, int w, String name){
        MapRoadSegment road = new MapRoadSegment(vertices.get(x), vertices.get(y), name, "type", 80, true, false);

        DirectedEdge edge = new DirectedEdge(vertices.get(x), vertices.get(y), 1, road);
        vertices.get(x).addOutEdge(edge);
        vertices.get(y).addInEdge(edge);

        edge = new DirectedEdge(vertices.get(y), vertices.get(x), 1, road);
        vertices.get(y).addOutEdge(edge);
        vertices.get(x).addInEdge(edge);
    }

    @AfterEach
    void tearDown() {
    }
}