package program.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import program.shared.MapPoint;
import static program.model.Model.MOT.*;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BiDirectionalDijkstraTest {
    Model model;

    BiDirectionalDijkstraTest() throws XMLStreamException, IOException, ClassNotFoundException {
        model = new Model();
        model.loadNewFile("src/main/data/fyn.osm.zip");
    }


    @Test
    void directionsTest(){
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
        MapPoint endPoint = model.addressSearch("Albanigade 21A, 5000 Odense");
        Vertex start = model.nearestVertex(startPoint);
        Vertex end = model.nearestVertex(endPoint);
        try {
            BiDirectionalDijkstra bididi = new BiDirectionalDijkstra(start, end, CAR);
        } catch (IllegalArgumentException e){
            fail();
        }
        assertTrue(true);
    }

    @Test
    void footPathTest(){
        MapPoint startPoint =  model.addressSearch("Jollehavnen 2 5300 Kerteminde");
        MapPoint endPoint = model.addressSearch("Sydstranden 68, 5300 Kerteminde");
        Vertex start = model.nearestVertex(startPoint);
        Vertex end = model.nearestVertex(endPoint);
        BiDirectionalDijkstra bididiCar = new BiDirectionalDijkstra(start, end, CAR);
        BiDirectionalDijkstra bididiWalk = new BiDirectionalDijkstra(start, end, WALK);
        assertTrue(bididiWalk.getPath().size() < bididiCar.getPath().size());
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
            assertEquals("No such path exists", e.getMessage());
        }
    }


/*

    @Test
    void neighbourVertexTest(){
        BiDirectionalDijkstra bididi = new BiDirectionalDijkstra(vertices.get(3), vertices.get(5), WALK);
        //Iterable<DirectedEdge> edges = bididi.getEdgePath();
        assertEquals(bididi.currentShortestPathLength, 2);
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


 */
    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }
}