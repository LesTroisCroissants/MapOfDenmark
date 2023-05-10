package program.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import program.shared.MapPoint;
import program.shared.MapRoadSegment;
import static program.model.Model.MOT.*;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

import static program.model.Model.MOT.*;

class BiDirectionalDijkstraTest {
    List<Vertex> vertices;
    List<MapRoadSegment> mapRoadSegments;
    Model model;


    @Test
    void shortestPathTest(){
        ArrayList<String> expected = new ArrayList<>(
                List.of(new String[]{
                        "Turn left onto Troels Alle",
                        "Turn left onto Lindøalleen",
                        "Turn right onto Moseskovvej",
                        "Turn right onto Solbakken",
                        "Turn right onto Munkebjergvej",
                        "Continue on Gamle Havnekaj"
                })
        );

        MapPoint from = model.addressSearch("Fjordvej 56, 5330 Munkebo");
        MapPoint to = model.addressSearch("Bycentret 205, 5330 Munkebo");
        model.planRoute(from, to);
        Iterable<String> route = model.getInstructions();

        int count = 0;
        int correctAmount = 0;
        for (String instruction : route){
            if(instruction.equals(expected.get(count))) correctAmount++;
            count++;
        }
        assertEquals(correctAmount, expected.size());
    }

    @Test
    void blockedCarPathTest(){
        MapPoint startPoint =  model.addressSearch("Sydstranden 68, 5300 Kerteminde");
        MapPoint endPoint = model.addressSearch("Albanigade 21A, st th, 5000 Odense");
       Vertex start = model.nearestVertex(startPoint);
       Vertex end = model.nearestVertex(endPoint);
       BiDirectionalDijkstra bididi = new BiDirectionalDijkstra(start, end, CAR);
       float correctLength = 0.0029433654F;
       assertEquals(bididi.currentShortestPathLength, correctLength);
    }

    @Test
    void footPathTest(){
        MapPoint startPoint =  model.addressSearch("Jollehavnen 2 5300 Kerteminde");
        MapPoint endPoint = model.addressSearch("Sydstranden 68, 5300 Kerteminde");
        Vertex start = model.nearestVertex(startPoint);
        Vertex end = model.nearestVertex(endPoint);
        BiDirectionalDijkstra bididi = new BiDirectionalDijkstra(start, end, WALK);
        float correctLength = 0.0051203435F;
        assertEquals(bididi.currentShortestPathLength, correctLength);
    }
    @Test
    void noSuchPathTest(){
        MapPoint startPoint =  model.addressSearch("Jollehavnen 2 5300 Kerteminde");
        MapPoint endPoint = model.addressSearch("Romsø 3 5300 Kerteminde");
        Vertex start = model.nearestVertex(startPoint);
        Vertex end = model.nearestVertex(endPoint);
        try
        {
            BiDirectionalDijkstra bididi = new BiDirectionalDijkstra(start, end, CAR);
        }catch (IllegalArgumentException e){
            assertTrue(e.getMessage().equals("No such path exists"));
        }
    }

    @Test
    void nullBadStartTest(){
        MapPoint startPoint =  model.addressSearch("Vigelsø 2 5450 Otterup");
        MapPoint endPoint = model.addressSearch("Klingeskov 30 5450 Otterup");
        Vertex start = model.nearestVertex(startPoint);
        Vertex end = model.nearestVertex(endPoint);
        try
        {
            BiDirectionalDijkstra bididi = new BiDirectionalDijkstra(null, end, CAR);
        }catch (IllegalArgumentException e){
            assertTrue(e.getMessage().equals("Start or end vertex is null"));
        }
    }
/*
    @Test
    void arbitraryVerticesTest(){
        BiDirectionalDijkstra bididi = new BiDirectionalDijkstra(vertices.get(1), vertices.get(8), WALK);
        //Iterable<DirectedEdge> edges = bididi.getEdgePath();
        assertEquals(bididi.currentShortestPathLength, 115);
    }

    @Test
    void neighbourVertexTest(){
        BiDirectionalDijkstra bididi = new BiDirectionalDijkstra(vertices.get(3), vertices.get(5), WALK);
        //Iterable<DirectedEdge> edges = bididi.getEdgePath();
        assertEquals(bididi.currentShortestPathLength, 2);
    }

    @Test
    void splitPathTest(){
        BiDirectionalDijkstra bididi = new BiDirectionalDijkstra(vertices.get(6), vertices.get(9), WALK);
        //Iterable<DirectedEdge> edges = bididi.getEdgePath();
        assertEquals(bididi.currentShortestPathLength, 5);
    }

    @Test
    void sameSourceAndDestinationTest(){
        try {
            BiDirectionalDijkstra bididi = new BiDirectionalDijkstra(vertices.get(6), vertices.get(6), WALK);
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

 */

    @BeforeEach
    void setUp() throws XMLStreamException, IOException, ClassNotFoundException {
        model = new Model();

//        vertices = new ArrayList<>();
//        mapRoadSegments = new ArrayList<>();
//
//        vertices.add(new Vertex(3, 4)); // A 0
//        vertices.add(new Vertex(4, 5)); // B 1
//        vertices.add(new Vertex(4,3)); // C 2
//        vertices.add(new Vertex(4,4)); // D 3
//        vertices.add(new Vertex(5,6)); // E 4
//        vertices.add(new Vertex(5,4)); // F 5
//        vertices.add(new Vertex(6,4)); // G 6
//        vertices.add(new Vertex(7,5)); // H 7
//        vertices.add(new Vertex(7,3)); // I 8
//        vertices.add(new Vertex(9,4)); // J 9
//        vertices.add(new Vertex(11,5)); // K 10
//        vertices.add(new Vertex(10,3)); // L 11
//
//        addEdge(0, 1,  "a");
//        addEdge(0, 2,  "b");
//        addEdge(1, 4,  "d");
//        addEdge(1, 5,  "f");
//        addEdge(2, 5, "c");
//        addEdge(5, 3, "e");
//        addEdge(4, 5, "g");
//        addEdge(5, 6, "h");
//        addEdge(6, 7, "j");
//        addEdge(6, 8, "i");
//        addEdge(7, 9, "l");
//        addEdge(8, 9, "k");
//        addEdge(9, 10, "n");
//        addEdge(9, 11, "m");
//        addEdge(10, 11, "o");

    }

//    private void addEdge(int x, int y, String name){
//        MapRoadSegment road = new MapRoadSegment(vertices.get(x), vertices.get(y), name, "type", 80, true, false);
//
//        DirectedEdge edge = new DirectedEdge(vertices.get(x), vertices.get(y), 1, road);
//        vertices.get(x).addOutEdge(edge);
//        vertices.get(y).addInEdge(edge);
//
//        edge = new DirectedEdge(vertices.get(y), vertices.get(x), 1, road);
//        vertices.get(y).addOutEdge(edge);
//        vertices.get(x).addInEdge(edge);
//    }

    @AfterEach
    void tearDown() {
    }
}