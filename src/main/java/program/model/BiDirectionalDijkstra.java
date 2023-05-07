package program.model;

import program.shared.MapRoadSegment;
import static program.model.Model.MOT.*;

import java.util.*;

public class BiDirectionalDijkstra {
    private HashMap<Vertex, DirectedEdge> previousEdge;
    private PriorityQueue<Vertex> forwardPQ;
    private PriorityQueue<Vertex> backwardPQ;
    private HashSet<Vertex> forwardMarked;
    private HashSet<Vertex> backwardMarked;

    private Vertex shortestForward;
    private Vertex shortestBackward;
    public float currentShortestPathLength;
    private Deque<DirectedEdge> edgePath = new ArrayDeque<>();
    private final Model.MOT modeOfTransport;

    public BiDirectionalDijkstra(Vertex source, Vertex destination, Model.MOT modeOfTransport){
        if (source == destination) throw new IllegalArgumentException("Identical source and destination");

        this.modeOfTransport = modeOfTransport;
        initializeDataStructures();
        prepareCurrentShortestPathRelatedFields();
        prepareSourceAndDestination(source, destination);

        findPath();
    }

    private void initializeDataStructures(){
        previousEdge = new HashMap<>();
        forwardPQ = new PriorityQueue<>();
        backwardPQ = new PriorityQueue<>();
        forwardMarked = new HashSet<>();
        backwardMarked = new HashSet<>();
    }

    private void prepareCurrentShortestPathRelatedFields(){
        shortestForward = null;
        shortestBackward = null;
        currentShortestPathLength = Float.MAX_VALUE;
    }

    private void prepareSourceAndDestination(Vertex source, Vertex destination){
        source.setDistTo(0);
        destination.setDistTo(0);
        forwardPQ.add(source);
        backwardPQ.add(destination);
        forwardMarked.add(source);
        backwardMarked.add(destination);
    }

    private void findPath(){
        while (shorterPathPossible()){
            searchForward();
            searchBackward();
        }

        setEdgePath();

        // check if path has been found
        if (shortestForward == null) throw new IllegalArgumentException("No such path exists");
    }

    private void searchForward() {
        Vertex currentVertex = forwardPQ.remove();

        for (DirectedEdge directedEdge : currentVertex.outEdges){
            if(skipEdge(directedEdge)) continue;
            //Triggers if vertexTo is in the opposite search-space
            if (backwardMarked.contains(directedEdge.toVertex())){
                evaluatePath(directedEdge);
            }
            else {
                relaxForward(directedEdge);
            }
        }
    }

    private void searchBackward() {
        Vertex currentVertex = backwardPQ.remove();

        for (DirectedEdge directedEdge : currentVertex.inEdges){
            if(skipEdge(directedEdge)) continue;

            //Triggers if vertexFrom is in the opposite search-space
            if (forwardMarked.contains(directedEdge.fromVertex())){
                // evaluatePath(directedEdge); // should not be necessary as we do not wish to evaluate all paths from both directions
            }
            else {
                relaxBackward(directedEdge);
            }
        }
    }

    private boolean skipEdge(DirectedEdge directedEdge){
        if(modeOfTransport == CAR){
            if (!directedEdge.getMapRoadSegment().isCarAllowed()){
                return true;
            }
        }
        if(modeOfTransport == BIKE || modeOfTransport == WALK){
            if (directedEdge.getMapRoadSegment().isOnlyCarAllowed()){
                return true;
            }
        }
        return false;
    }

    private void evaluatePath(DirectedEdge directedEdge) {
        Vertex fromVertex = directedEdge.fromVertex();
        Vertex toVertex = directedEdge.toVertex();
        float pathLength = fromVertex.distTo + directedEdge.weight(modeOfTransport) + toVertex.distTo;

        if (pathLength < currentShortestPathLength) {
            currentShortestPathLength = pathLength;
            shortestForward = fromVertex;
            shortestBackward = toVertex;
        }
    }

    private void relaxForward(DirectedEdge edge) {
        float edgeWeight = edge.weight(modeOfTransport);
        Vertex vertexTo = edge.toVertex();
        Vertex vertexFrom = edge.fromVertex();

        //Triggers if the vertexTo has been explored as part of this search space before otherwise ignores the vertex since it might have values from a previous search
        if (forwardMarked.contains(vertexTo)){
            if(vertexFrom.distTo + edgeWeight < vertexTo.distTo){
                forwardPQ.remove(vertexTo); // Remove old element to update and insert again
                vertexTo.distTo = vertexFrom.distTo + edgeWeight;
                previousEdge.put(vertexTo, edge);
                forwardPQ.add(vertexTo);
            }
        }
        //Triggers if the vertexTo has yet to be explored altogether in this search
        else {
            vertexTo.distTo = vertexFrom.distTo + edgeWeight;
            previousEdge.put(vertexTo, edge);
            forwardMarked.add(vertexTo);
            forwardPQ.add(vertexTo);
        }
    }

    private void relaxBackward(DirectedEdge edge){
        float edgeWeight = edge.weight(modeOfTransport);

        // the vertices are switched as the backward-search goes backwards
        Vertex vertexTo = edge.fromVertex();
        Vertex vertexFrom = edge.toVertex();

        //Triggers if the vertexTo has been explored as part of this search space before otherwise ignores the vertex since it might have values from a previous search
        if (backwardMarked.contains(vertexTo)){
            if(vertexFrom.distTo + edgeWeight < vertexTo.distTo){
                backwardPQ.remove(vertexTo); // Remove old element to update and insert again
                vertexTo.distTo = vertexFrom.distTo + edgeWeight;
                previousEdge.put(vertexTo, edge);
                backwardPQ.add(vertexTo);
            }
        }
        //Triggers if the vertexTo has yet to be explored altogether in this search
        else {
            vertexTo.distTo = vertexFrom.distTo + edgeWeight;
            previousEdge.put(vertexTo, edge);
            backwardMarked.add(vertexTo);
            backwardPQ.add(vertexTo);
        }
    }

    private boolean shorterPathPossible(){
        if (forwardPQ.isEmpty() || backwardPQ.isEmpty()) return false;

        float forwardRadius = forwardPQ.peek().distTo;
        float backwardRadius = backwardPQ.peek().distTo;

        return forwardRadius + backwardRadius < currentShortestPathLength;
    }


    private DirectedEdge getBridge() {
        DirectedEdge bridge = shortestForward.outEdges.iterator().next();
        for (DirectedEdge edgeCandidate : shortestForward.outEdges)
            if (edgeCandidate.toVertex() == shortestBackward) {
                bridge = edgeCandidate;
                break;
            }
        return bridge;
    }

    public void setEdgePath(){
        edgePath.push(getBridge());

        DirectedEdge currentEdge = previousEdge.get(shortestForward);
        while (currentEdge != null){
            edgePath.push(currentEdge);
            currentEdge = previousEdge.getOrDefault(currentEdge.fromVertex(), null);
        }

        currentEdge = previousEdge.get(shortestBackward);
        while (currentEdge != null){
            edgePath.add(currentEdge);
            currentEdge = previousEdge.getOrDefault(currentEdge.toVertex(), null);
        }
    }

    public List<MapRoadSegment> getPath(){
        List<MapRoadSegment> segments = new ArrayList<>();

        for (DirectedEdge edge : edgePath){
            segments.add(edge.getMapRoadSegment());
        }

        return segments;
    }

    public Iterable<String> getInstructions(){
        ArrayList<String> instructions = new ArrayList<>();

        DirectedEdge previous = null;
        for (DirectedEdge edge : edgePath){
            if (previous == null){
                previous = edge;
                continue;
            }

            String previousRoadName = previous.getMapRoadSegment().getName(); // In need of memory, remove this as variable:)
            String currentRoadName = edge.getMapRoadSegment().getName();

            if(!currentRoadName.equals(previousRoadName)) {
                instructions.add(getDirection(previous, edge) + " " + currentRoadName);
            }

            previous = edge;
        }
        return instructions;
    }

    private String getDirection(DirectedEdge comingFrom, DirectedEdge goingTo){
        String DIRECTION_RIGHT = "Turn right onto";
        String DIRECTION_LEFT = "Turn left onto";
        String DIRECTION_STRAIGHT = "Continue on";
        String DIRECTION_U_TURN = "Make a U-turn";
        

        float angle = (float) Math.toDegrees(Math.acos(
                dotProduct(comingFrom, goingTo) / (comingFrom.getMapRoadSegment().getDistance() * goingTo.getMapRoadSegment().getDistance())
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

        float crossProduct = CrossProduct(comingFrom, goingTo);

        if (crossProduct > 0){
            return DIRECTION_LEFT;
        } else {
            return DIRECTION_RIGHT;
        }
    }

    private float CrossProduct(DirectedEdge comingFrom, DirectedEdge goingTo){
        Vertex from = comingFrom.fromVertex();
        Vertex intersection = comingFrom.toVertex();
        Vertex to = goingTo.toVertex();

        return (intersection.getX() - from.getX()) * (to.getY() - from.getY()) - (to.getX() - from.getX()) * (intersection.getY() - from.getY());
    }
    
    private float dotProduct(DirectedEdge comingFrom, DirectedEdge goingTo){
        float vectorAx = comingFrom.toVertex().getX() - comingFrom.fromVertex().getX();
        float vectorAy = comingFrom.toVertex().getY() - comingFrom.fromVertex().getY();

        float vectorBx = goingTo.toVertex().getX() - goingTo.fromVertex().getX();
        float vectorBy = goingTo.toVertex().getY() - goingTo.fromVertex().getY();

        return vectorAx * vectorBx + vectorAy * vectorBy;
    }
}
