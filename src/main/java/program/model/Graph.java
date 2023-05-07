//package program.model;
//
//import program.shared.MapElement;
//import program.shared.MapRoadSegment;
//
//import java.util.HashMap;
//
//
//// This class is heavily influenced by Robert Sedgewick and Kevin Wayne's library "algs4.jar"
//// implementation of "EdgeWeightedDigraph" which has been modified slightly for use in our mapgraph.
//public class Graph {
//    private int edgeAmount;
//    private HashMap<Integer, Vertex> idToVertex;
//
//    //region GraphDebugging
//    public static void main(String[] args) {
//        Graph graph = new Graph();
//        graph.testMethod(graph);
//    }
//
//    private void testMethod(Graph graph){
//        /*MapRoadSegment debuggingSegment = new MapRoadSegment(new Point(10,10), new Point(20, 20), "lolType");
//
//        // loop and add random edges and vertices
//        final int TESTSIZE = 15_000_000;
//
//        Random random = new Random();
//
//        long start = System.currentTimeMillis();
//
//        // Vertices added while reading nodes of the osm-file
//        for (int n = 0; n < TESTSIZE; n++){
//            Vertex newVertex = new Vertex(random.nextFloat(0, TESTSIZE), random.nextFloat(0, TESTSIZE));
//            graph.addVertex(newVertex);
//            idToVertex.put(n, newVertex);
//        }
//        // Edges added while reading ways of osm-file
//
//        for (int n = 0; n < TESTSIZE - 1; n++){
//            //TODO: maybe add if statements that set a weight variable?
//            Vertex from = idToVertex.get(n);
//            Vertex to = idToVertex.get(n + 1);
//            double weight = distance(from, to);
//
//            // Edge going one way
//            DirectedEdge directedEdge = new DirectedEdge(from, to, weight, debuggingSegment);
//            from.addEdge(directedEdge);
//
//            // Edge going the other way
//            DirectedEdge otherWay = new DirectedEdge(to, from, weight, debuggingSegment);
//            to.addEdge(otherWay);
//        }
//
//        long end = System.currentTimeMillis();
//
//        System.out.println(end-start);
//        //System.out.println(graph);
//         */
//    }
//    //endregion
//
//
//    // Initializes graph with initial amount of vertices and 0 edges
//    public Graph() {
//        this.edgeAmount = 0;
//        idToVertex = new HashMap<>();
//    }
//
//
//    public void addVertex(Vertex vertex){
//        idToVertex.put(getVertexAmount(), vertex);
//    }
//
//    public void addEdge(Vertex fromVertex, Vertex toVertex, float weight, MapRoadSegment mapRoadSegment) {
//        DirectedEdge directedEdge = new DirectedEdge(fromVertex, toVertex, weight, mapRoadSegment);
//        fromVertex.addOutEdge(directedEdge);
//        toVertex.addInEdge(directedEdge);
//        edgeAmount++;
//    }
//
//
//    public double distance(Vertex from,Vertex to){
//        double xDifference = to.getX() - from.getX();
//        double yDifference = to.getY() - from.getY();
//        return Math.sqrt(Math.pow(xDifference, 2) + Math.pow(yDifference, 2));
//    }
//
//
//
//
//
//
//    // returns bag of directed edges incident from vertex (TODO: maybe change parameter to MapElement)
////    public Iterable<DirectedEdge> adjacentVertices(Vertex vertex) {
////        // validateVertex(vertex);
////        return adjacencyMap.get(vertex);
////    }
//
//// returns bag of all directed edges in digraph
////    public Iterable<DirectedEdge> edges() {
////        Bag<DirectedEdge> list = new Bag<DirectedEdge>();
////        for (int vertex = 0; vertex < adjacencyMap.size(); vertex++) {
////            for (DirectedEdge edge : adjacentVertices(vertex)) {
////                list.add(edge);
////            }
////        }
////        return list;
////    }
//
//    private void validateVertex(MapElement vertex) { // TODO: probably not a MapElement like that lol
//        if (vertex == null)
//            throw new IllegalArgumentException("Vertex is null");
//    }
//
//    private static final String NEWLINE = System.getProperty("line.separator");
//    public String toString() {
//
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(getVertexAmount() + " " + edgeAmount + NEWLINE);
//        for (int vertexId = 0; vertexId < getVertexAmount(); vertexId++) {
//            stringBuilder.append(vertexId + ": ");
//            for (DirectedEdge edge : idToVertex.get(vertexId).outEdges) {
//                stringBuilder.append(edge + "  ");
//            }
//            stringBuilder.append(NEWLINE);
//        }
//
//        return stringBuilder.toString();
//
//    }
//
//
//    //region Getters
//    public int getVertexAmount() {
//        return idToVertex.size();
//    }
//    public int getEdgeAmount() {
//        return edgeAmount;
//    }
//    //endregion
//}