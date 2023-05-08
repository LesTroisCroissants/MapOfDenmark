package program.model;

import org.junit.jupiter.api.Test;
import program.shared.MapRoadSegment;

import static org.junit.jupiter.api.Assertions.*;

class AuxMathTest {

    @Test
    void pointToRoadDistancePointRightTest(){
        float[] p = new float[]{1,1};
        MapRoadSegment mrs = new MapRoadSegment(new Vertex(0,0), new Vertex(0,2), "", "", 0, true, true);
        assertEquals(1, AuxMath.pointToRoadDistance(p, mrs));
    }

    @Test
    void pointToRoadDistancePointLeftTest(){
        float[] p = new float[]{-1,1};
        MapRoadSegment mrs = new MapRoadSegment(new Vertex(0,0), new Vertex(0,2), "", "", 0, true, true);
        assertEquals(1, AuxMath.pointToRoadDistance(p, mrs));
    }

    @Test
    void pointToRoadDistancePointBelowTest(){
        float[] p = new float[]{0,-1};
        MapRoadSegment mrs = new MapRoadSegment(new Vertex(0,0), new Vertex(0,2), "", "", 0, true, true);
        assertEquals(1, AuxMath.pointToRoadDistance(p, mrs));
    }

    @Test
    void pointToRoadDistancePointAboveTest(){
        float[] p = new float[]{0,3};
        MapRoadSegment mrs = new MapRoadSegment(new Vertex(0,0), new Vertex(0,2), "", "", 0, true, true);
        assertEquals(1, AuxMath.pointToRoadDistance(p, mrs));
    }

    //Insert tests for slope

    @Test
    void dotProductTest(){
        DirectedEdge from = new DirectedEdge(new Vertex(0,0), new Vertex(1,2),0,null);
        DirectedEdge to = new DirectedEdge(new Vertex(5,2), new Vertex(-3,9),0,null);
        assertEquals(6, AuxMath.dotProduct(from, to));
    }

    @Test
    void lengthOfCrossProductVectorTest(){
        DirectedEdge from = new DirectedEdge(new Vertex(1,1), new Vertex(3,1),0,null);
        DirectedEdge to = new DirectedEdge(new Vertex(1,1), new Vertex(1,3),0,null);
        assertEquals(4, AuxMath.lengthOfCrossProductVector(from, to));
    }
}