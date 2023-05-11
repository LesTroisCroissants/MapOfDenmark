package program.model;

import program.shared.MapPoint;
import program.shared.MapRoadSegment;
import program.shared.Point;

public class AuxMath {
    /**
     * Calculates the shortest distance from a point to a line segment
     * @param p the point
     * @param road the line segment represented as a MapRoadSegment
     * @return the shortest distance from the point to the line segment
     */
    public static float pointToRoadDistance(float[] p, MapRoadSegment road) {
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

        float dx = p[0] - xx;
        float dy = p[1] - yy;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Calculates the dot product between two vectors represented as DirectedEdge
     * @return the dot product
     */
    public static float dotProduct(DirectedEdge comingFrom, DirectedEdge goingTo){
        float vectorAx = comingFrom.toVertex().getX() - comingFrom.fromVertex().getX();
        float vectorAy = comingFrom.toVertex().getY() - comingFrom.fromVertex().getY();

        float vectorBx = goingTo.toVertex().getX() - goingTo.fromVertex().getX();
        float vectorBy = goingTo.toVertex().getY() - goingTo.fromVertex().getY();

        return vectorAx * vectorBx + vectorAy * vectorBy;
    }

    /**
     * Calculates the length of a hypothetical cross product vector; used entre autre for determining turning direction
     * @return a float indicating the length of a hypothetical cross product vector
     */
    public static float lengthOfCrossProductVector(DirectedEdge comingFrom, DirectedEdge goingTo){
        Vertex from = comingFrom.fromVertex();
        Vertex intersection = comingFrom.toVertex();
        Vertex to = goingTo.toVertex();

        return (intersection.getX() - from.getX()) * (to.getY() - from.getY()) - (to.getX() - from.getX()) * (intersection.getY() - from.getY());
    }

    /**
     * Calculates the distance between two vertices
     * @param vertexFrom first vertex
     * @param vertexTo second vertex
     * @return the distance between the two vertices
     */
    public static float calculateDistance(Vertex vertexFrom, Vertex vertexTo){
        float xDifference = vertexTo.getX() - vertexFrom.getX();
        float yDifference = vertexTo.getY() - vertexFrom.getY();
        return (float) Math.sqrt(Math.pow(xDifference, 2) + Math.pow(yDifference, 2));
    }

    /**
     * Returns a description of how one should turn when going from one DirectedEdge to another
     * @param comingFrom DirectedEdge to travel from
     * @param goingTo DirectedEdge to travel to
     * @return a String describing if/how one should turn
     */
    public static String getDirection(DirectedEdge comingFrom, DirectedEdge goingTo){
        String DIRECTION_RIGHT = "Turn right onto";
        String DIRECTION_LEFT = "Turn left onto";
        String DIRECTION_STRAIGHT = "Continue on";
        String DIRECTION_U_TURN = "Make a U-turn";


        float angle = (float) Math.toDegrees(Math.acos(
                AuxMath.dotProduct(comingFrom, goingTo) / (comingFrom.getMapRoadSegment().getDistance() * goingTo.getMapRoadSegment().getDistance()) //dot product divided by the product of the edge lengths
        ));

        // if the angle is < 20 degrees: straight
        // more than 160 degrees: U-turn
        // else cross product to determine left/right

        if (angle <= 20 || angle >= 160){
            if (angle <= 20){
                return DIRECTION_STRAIGHT;
            } else {
                return DIRECTION_U_TURN;
            }
        }

        float crossProduct = AuxMath.lengthOfCrossProductVector(comingFrom, goingTo);

        if (crossProduct > 0){
            return DIRECTION_LEFT;
        } else {
            return DIRECTION_RIGHT;
        }
    }

    public static MapPoint calculateMiddlePoint(float[] from, float[] to){
        return new MapPoint((to[0] - from[0])/2 + from[0],  (to[1] - from[1])/2 + from[1], "");
    }
}