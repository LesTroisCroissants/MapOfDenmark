package program.model;

import program.shared.MapRoadSegment;
import program.shared.Point;

public class AuxMath {
    public static float pointToRoadDistance(float[] p, MapRoadSegment road) {
        /*float lineSlope = getSlope(road.getVertexA(), road.getVertexB());
        float lineIntercept = getLineIntercept(road.getVertexA(), lineSlope);
        float pointOnLine = Math.abs(lineSlope * p[0 + lineIntercept - p[1);
        return pointOnLine / (float) (Math.sqrt(Math.pow(lineSlope, 2) + 1));*/

        /*
         * Source for math used
         *  https://stackoverflow.com/questions/849211/shortest-distance-between-a-point-and-a-line-segment
         */

        Point p1 = road.getVertexA();
        Point p2 = road.getVertexB();

        float A = p[0] - p1.getX();
        float B = p[1] - p1.getY();
        float C = p2.getX() - p1.getX();
        float D = p2.getY() - p1.getY();

        float dot = A * C + B * D;
        float lengthSquared = C * C + D * D;
        float param = -1;
        if (lengthSquared != 0) param = dot / lengthSquared;

        float xx, yy;

        if (param < 0) {
            xx = p1.getX();
            yy = p1.getY();
        }
        else if (param > 1) {
            xx = p2.getX();
            yy = p2.getY();
        }
        else {
            xx = p1.getX() + param * C;
            yy = p1.getY() + param * D;
        }

        var dx = p[0] - xx;
        var dy = p[1] - yy;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public static float dotProduct(DirectedEdge comingFrom, DirectedEdge goingTo){
        float vectorAx = comingFrom.toVertex().getX() - comingFrom.fromVertex().getX();
        float vectorAy = comingFrom.toVertex().getY() - comingFrom.fromVertex().getY();

        float vectorBx = goingTo.toVertex().getX() - goingTo.fromVertex().getX();
        float vectorBy = goingTo.toVertex().getY() - goingTo.fromVertex().getY();

        return vectorAx * vectorBx + vectorAy * vectorBy;
    }

    public static float lengthOfCrossProductVector(DirectedEdge comingFrom, DirectedEdge goingTo){
        Vertex from = comingFrom.fromVertex();
        Vertex intersection = comingFrom.toVertex();
        Vertex to = goingTo.toVertex();

        return (intersection.getX() - from.getX()) * (to.getY() - from.getY()) - (to.getX() - from.getX()) * (intersection.getY() - from.getY());
    }
}