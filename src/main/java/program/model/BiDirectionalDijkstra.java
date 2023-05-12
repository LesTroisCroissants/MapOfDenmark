package program.model;

import program.shared.MapRoadSegment;
import static program.model.Model.MOT;

import java.util.*;

/**
 * Object that computes the shortest path between a source and a destination for a particular mode of transportation
 */

public class BiDirectionalDijkstra {
    private HashMap<Vertex, DirectedEdge> previousEdge; //Mapping from a vertex to its in edge used in its shortest path from the source
    private PriorityQueue<Vertex> forwardPQ; //Vertices to be explored by the forward search
    private PriorityQueue<Vertex> backwardPQ; //Vertices to be explored by the backward search
    private HashSet<Vertex> forwardMarked; //Vertices already seen by the forward search
    private HashSet<Vertex> backwardMarked; //Vertices already seen by the backward search

    private Vertex shortestForward; //Forward meeting vertex of the shortest route found so far
    private Vertex shortestBackward; //Backward meeting vertex of the shortest route found so far
    private float currentShortestPathLength;
    private Deque<DirectedEdge> edgePath; //Used to store the edges of the shortest path
    private final MOT modeOfTransport; //The mode of transportation used for the search

    public BiDirectionalDijkstra(Vertex source, Vertex destination, MOT modeOfTransport){
        if (source == destination) throw new IllegalArgumentException("Identical source and destination");

        this.modeOfTransport = modeOfTransport;

        // makes sure that the search does not start from a vertex only connected to edges which are intraversable for current MOT
        source = findStartVertex(source);
        destination = findEndVertex(destination);
        if(source == null || destination == null) throw new IllegalArgumentException("No such path exists");

        initializeDataStructures();
        prepareCurrentShortestPathRelatedFields();
        prepareSourceAndDestination(source, destination);

        findPath();
    }

    /**
     * Finds a legal vertex to start from
     * @param current source vertex
     * @return vertex to use as start point
     */
    private Vertex findStartVertex(Vertex current){
        HashSet<Vertex> visited = new HashSet<>();
        ArrayDeque<Vertex> vertices = new ArrayDeque<>();
        vertices.add(current);
        while(!vertices.isEmpty()){
            current = vertices.remove();
            for(DirectedEdge directedEdge : current.outEdges){
                if(skipEdge(directedEdge)){
                    if(!visited.contains(directedEdge.toVertex())){
                        vertices.add(directedEdge.toVertex());
                        visited.add(directedEdge.toVertex());
                    }
                    continue;
                }
                return current;
            }
        }
        return null;
    }

    /**
     * Finds a legal vertex to arrive at
     * @param current destination vertex
     * @return vertex to use as end point
     */
    private Vertex findEndVertex(Vertex current){
        HashSet<Vertex> visited = new HashSet<>();
        ArrayDeque<Vertex> vertices = new ArrayDeque<>();
        vertices.add(current);
        while(!vertices.isEmpty()){
            current = vertices.remove();
            for(DirectedEdge directedEdge : current.inEdges){
                if(skipEdge(directedEdge)){
                    if(!visited.contains(directedEdge.fromVertex())){
                        vertices.add(directedEdge.fromVertex());
                        visited.add(directedEdge.fromVertex());
                    }
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
        for (DirectedEdge directedEdge : currentVertex.outEdges){
            if(skipEdge(directedEdge)){
                continue;
            }
            //Triggers if vertexTo is in the opposite search-space
            if (backwardMarked.contains(directedEdge.toVertex())){
                evaluatePath(directedEdge);
            }
            else {
                relax(directedEdge, directedEdge.toVertex(), directedEdge.fromVertex(), forwardPQ, forwardMarked);
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
                relax(directedEdge, directedEdge.fromVertex(), directedEdge.toVertex(), backwardPQ, backwardMarked);
            }
        }
    }
    /**
     * Returns a boolean indicating if an edge is incompatible with the mode of transportation
     */
    private boolean skipEdge(DirectedEdge directedEdge){
        if(modeOfTransport == MOT.CAR){
            return !directedEdge.getMapRoadSegment().isCarAllowed();
        }
        if(modeOfTransport == MOT.BIKE || modeOfTransport == MOT.WALK){
            return directedEdge.getMapRoadSegment().isOnlyCarAllowed();
        }
        return false;
    }

    /**
     * Evaluates the path between two end-points of an edge back to the source and destination.
     * Sets the currentShortestPath if the presented candidate is a shorter suitor.
     * @param directedEdge bridge that connects the two search spaces
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

    private void relax(DirectedEdge edge, Vertex vertexTo, Vertex vertexFrom, PriorityQueue<Vertex> pq, Set<Vertex> marked) {
        float edgeWeight = edge.weight(modeOfTransport);
        //Triggers if the vertexTo has been explored as part of this search space before; otherwise ignores the vertex since it might have values from a previous search
        if (marked.contains(vertexTo)){
            if (vertexFrom.distTo + edgeWeight < vertexTo.distTo){
                pq.remove(vertexTo); //Remove old element reinsert it with updated weight
                vertexTo.distTo = vertexFrom.distTo + edgeWeight;
                previousEdge.put(vertexTo, edge);
                pq.add(vertexTo);
            }
        }
        //Triggers if the vertexTo has yet to be explored altogether in this search
        else {
            vertexTo.distTo = vertexFrom.distTo + edgeWeight;
            previousEdge.put(vertexTo, edge);
            marked.add(vertexTo);
            pq.add(vertexTo);
        }
    }

    /**
     * Returns true if it remains possible to potentially find a shorter path
     */
    private boolean shorterPathPossible(){
        if (forwardPQ.isEmpty() || backwardPQ.isEmpty()) return false;

        float forwardRadius = forwardPQ.peek().distTo;
        float backwardRadius = backwardPQ.peek().distTo;

        return forwardRadius + backwardRadius < currentShortestPathLength;
    }


    /**
     * Returns the edge common to the shortestForward and shortestBackward
     */
    private DirectedEdge getBridge() {
        DirectedEdge bridge = shortestForward.outEdges.get(0);
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
     */
    public List<MapRoadSegment> getPath(){
        List<MapRoadSegment> segments = new ArrayList<>();

        for (DirectedEdge edge : edgePath){
            segments.add(edge.getMapRoadSegment());
        }

        return segments;
    }

    private float getPathLength(){
        return  currentShortestPathLength;
    }

    /**
     * Returns an Iterable<String> containing all instructions for the found path
     */
    public Iterable<String> getInstructions(){
        ArrayList<String> instructions = new ArrayList<>();

        DirectedEdge previous = edgePath.peek();
        for (DirectedEdge edge : edgePath){
            String previousRoadName = previous.getMapRoadSegment().getName();
            String currentRoadName = edge.getMapRoadSegment().getName();

            if(!currentRoadName.equals(previousRoadName)) { //Will always be skipped for first element
                instructions.add(AuxMath.getDirection(previous, edge) + " " + currentRoadName);
            }
            previous = edge;
        }
        return instructions;
    }
}
