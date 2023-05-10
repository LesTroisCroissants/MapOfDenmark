package program.model;

import program.shared.MapRoadSegment;
import static program.model.Model.MOT.*;

import java.util.*;

/**
 * Object that computes the shortest path between a source and a destination for a particular mode of transportation
 */

public class BiDirectionalDijkstra {
    private HashMap<Vertex, DirectedEdge> previousEdge;
    private PriorityQueue<Vertex> forwardPQ;
    private PriorityQueue<Vertex> backwardPQ;
    private HashSet<Vertex> forwardMarked;
    private HashSet<Vertex> backwardMarked;

    private Vertex shortestForward;
    private Vertex shortestBackward;
    public float currentShortestPathLength;
    private Deque<DirectedEdge> edgePath;
    private final Model.MOT modeOfTransport;

    public BiDirectionalDijkstra(Vertex source, Vertex destination, Model.MOT modeOfTransport){
        if (source == destination) throw new IllegalArgumentException("Identical source and destination");

        this.modeOfTransport = modeOfTransport;
        // if the source or destination starts somewhere that you can't go anywhere from we will return the nearest vertex where it is possible to search from
        source = handleBadStartForward(source);
        destination = handleBadStartBackward(destination);
        if(source == null || destination == null){
            throw new IllegalArgumentException("No such path exists");
        }
        initializeDataStructures();
        prepareCurrentShortestPathRelatedFields();
        prepareSourceAndDestination(source, destination);
        findPath();
    }

    private Vertex handleBadStartForward(Vertex current){

        ArrayDeque<Vertex> vertices = new ArrayDeque<>();
        vertices.add(current);
        while(!vertices.isEmpty()){
            current = vertices.remove();
            for(DirectedEdge directedEdge : current.outEdges){
                if(skipEdge(directedEdge)){
                    vertices.add(directedEdge.toVertex());
                    continue;
                }
                return current;
            }
        }
        return null;
    }

    private Vertex handleBadStartBackward(Vertex current){
        ArrayDeque<Vertex> vertices = new ArrayDeque<>();
        vertices.add(current);
        while(!vertices.isEmpty()){
            current = vertices.remove();
            for(DirectedEdge directedEdge : current.inEdges){
                if(skipEdge(directedEdge)){
                    vertices.add(directedEdge.fromVertex());
                    continue;
                }
                return current;
            }
        }
        return null;
    }

    private void initializeDataStructures(){
        previousEdge = new HashMap<>();
        forwardPQ = new PriorityQueue<>();
        backwardPQ = new PriorityQueue<>();
        forwardMarked = new HashSet<>();
        backwardMarked = new HashSet<>();
        edgePath = new ArrayDeque<>();
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

        // check if path has been found
        if (shortestForward == null) throw new IllegalArgumentException("No such path exists");

        setEdgePath();
    }

    private void searchForward() {
        Vertex currentVertex = forwardPQ.remove();
        // Set count to 0 every time we look at a new vertex
        for (DirectedEdge directedEdge : currentVertex.outEdges){
            if(skipEdge(directedEdge)){
                continue;
            }
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
            if(skipEdge(directedEdge)){
                continue;
            }
            //Triggers if vertexFrom is in the opposite search-space
            if (forwardMarked.contains(directedEdge.fromVertex())){
                // evaluatePath(directedEdge); // not necessary as we do not wish to evaluate all paths from both directions
            }
            else {
                relaxBackward(directedEdge);
            }
        }
    }
    /**
     * Returns a boolean indicating if an edge is incompatible with the mode of transportation
     */
    private boolean skipEdge(DirectedEdge directedEdge){
        if(modeOfTransport == CAR){
            return !directedEdge.getMapRoadSegment().isCarAllowed();
        }
        if(modeOfTransport == BIKE || modeOfTransport == WALK){
            return directedEdge.getMapRoadSegment().isOnlyCarAllowed();
        }
        return false;
    }

    /**
     * Evaluates the path between two end-points of an edge back to the source and destination.
     * Sets the currentShortestPath if the presented candidate is a shorter suitor.
     * @param directedEdge
     */
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

    /**
     * Returns true if it remains possible to potentially find a shorter path
     * @return
     */
    private boolean shorterPathPossible(){
        if (forwardPQ.isEmpty() || backwardPQ.isEmpty()) return false;

        float forwardRadius = forwardPQ.peek().distTo;
        float backwardRadius = backwardPQ.peek().distTo;

        return forwardRadius + backwardRadius < currentShortestPathLength;
    }


    /**
     * Returns the edge common to the shortestForward and shortestBackward
     * @return
     */
    private DirectedEdge getBridge() {
        DirectedEdge bridge = shortestForward.outEdges.iterator().next();
        for (DirectedEdge edgeCandidate : shortestForward.outEdges)
            if (edgeCandidate.toVertex() == shortestBackward) {
                bridge = edgeCandidate;
                break;
            }
        return bridge;
    }

    private void setEdgePath(){
        edgePath.push(getBridge());

        //pushes all edges in the forward path
        DirectedEdge currentEdge = previousEdge.get(shortestForward);
        while (currentEdge != null){
            edgePath.push(currentEdge);
            currentEdge = previousEdge.getOrDefault(currentEdge.fromVertex(), null);
        }

        //enqueues all edges in the backward path
        currentEdge = previousEdge.get(shortestBackward);
        while (currentEdge != null){
            edgePath.add(currentEdge);
            currentEdge = previousEdge.getOrDefault(currentEdge.toVertex(), null);
        }
    }

    /**
     * Returns a list of all the MapRoadSegments of the found path
     * @return
     */
    public List<MapRoadSegment> getPath(){
        List<MapRoadSegment> segments = new ArrayList<>();

        for (DirectedEdge edge : edgePath){
            segments.add(edge.getMapRoadSegment());
        }

        return segments;
    }

    public float getPathLength(){
        return  currentShortestPathLength;
    }

    /**
     * Returns an Iterable<String> containing all instructions for the found path
     * @return
     */
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
                AuxMath.dotProduct(comingFrom, goingTo) / (comingFrom.getMapRoadSegment().getDistance() * goingTo.getMapRoadSegment().getDistance())
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
}
