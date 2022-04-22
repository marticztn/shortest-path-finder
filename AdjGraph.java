// C343 Fall 2020
//
// a simple implementation for graphs with adjacency lists

// Problem Set 11 starter file

import java.util.*;

public class AdjGraph implements Graph {

    // is it a directed graph (true or false) :
    private boolean digraph;

    private int totalNodes;
    // all the nodes in the graph:
    private Vector<String> nodeList;

    private int totalEdges;
    // all the adjacency lists, one for each node in the graph:
    private Vector<LinkedList<Integer>> adjList;

    // all the weights of the edges, one for each node in the graph:
    private Vector<LinkedList<Integer>> adjWeight;

    // every visited node:
    private Vector<Boolean> visited;

    // list of nodes pre-visit:
    private Vector<Integer> nodeEnum;

    public AdjGraph() {
        init();
    }

    public AdjGraph(boolean ifdigraph) {
        init();
        digraph = ifdigraph;
    }

    public void init() {
        nodeList = new Vector<String>();
        adjList = new Vector<LinkedList<Integer>>();
        adjWeight = new Vector<LinkedList<Integer>>();
        visited = new Vector<Boolean>();
        nodeEnum = new Vector<Integer>();
        totalNodes = totalEdges = 0;
        digraph = false;
    }

    // set vertices:
    public void setVertices(String[] nodes) {
        for (int i = 0; i < nodes.length; i ++) {
            nodeList.add(nodes[i]);
            adjList.add(new LinkedList<Integer>());
            adjWeight.add(new LinkedList<Integer>());
            visited.add(false);
            totalNodes ++;
        }
    }

    // add a vertex:
    public void addVertex(String label) {
        nodeList.add(label);
        visited.add(false);
        adjList.add(new LinkedList<Integer>());
        adjWeight.add(new LinkedList<Integer>());
        totalNodes ++;
    }

    public int getNode(String node) {
        for (int i = 0; i < nodeList.size(); i ++) {
            if (nodeList.elementAt(i).equals(node)) return i;
        }
        return -1;
    }

    // return the number of vertices:
    public int length() {
        return nodeList.size();
    }

    // add edge from v1 to v2:
    public void setEdge(int v1, int v2, int weight) {
        LinkedList<Integer> tmp = adjList.elementAt(v1);
        if (adjList.elementAt(v1).contains(v2) == false) {
            tmp.add(v2);
            adjList.set(v1,  tmp);
            totalEdges ++;
            LinkedList<Integer> tmp2 = adjWeight.elementAt(v1);
            tmp2.add(weight);
            adjWeight.set(v1,  tmp2);
        }
    }

    public void setEdge(String v1, String v2, int weight) {
        if ((getNode(v1) != -1) && (getNode(v2) != -1)) {
            // add edge from v1 to v2:
            setEdge(getNode(v1), getNode(v2), weight);
            // for undirected graphs, add edge from v2 to v1 as well:
            if (digraph == false) {
                setEdge(getNode(v2), getNode(v1), weight);
            }
        }
    }

    // keep track whether a vertex has been visited or not,
    //    for graph traversal purposes:
    public void setVisited(int v) {
        visited.set(v, true);
        nodeEnum.add(v);
    }

    public boolean ifVisited(int v) {
        return visited.get(v);
    }


    // new for Problem Set 11:
    public LinkedList<Integer> getNeighbors(int v) {
        return adjList.get(v);
    }

    public int getWeight(int v, int u) {
        LinkedList<Integer> tmp = getNeighbors(v);
        LinkedList<Integer> weight = adjWeight.get(v);
        if (tmp.contains(u)) {
            return weight.get(tmp.indexOf(u));
        } else {
            return Integer.MAX_VALUE;
        }
    }

    // clean up before traversing the graph:
    public void clearWalk() {
        nodeEnum.clear();
        for (int i = 0; i < nodeList.size(); i ++)
            visited.set(i, false);
    }

    public void walk(String method) {
        clearWalk();
        // traverse the graph:
        for (int i = 0; i < nodeList.size(); i ++) {
            if (ifVisited(i) == false) {
                if (method.equals("BFS")) {
                    BFS(i);      // i is the start node
                } else if (method.equals("DFS")) {
                    DFS(i); // i is the start node
                } else {
                    System.out.println("unrecognized traversal order: " + method);
                    System.exit(0);
                }
            }
        }
        System.out.println(method + ":");
        displayEnum();
    }

    // Problem Set 11 TODO:
    //
    // write your methods here.
    //
    public void relax(int v, int u, int[] d, Boolean[] sptArr, int[][] matrix) {
        if (!sptArr[u] && matrix[v][u] != 0 && d[v] != Integer.MAX_VALUE && d[v] + matrix[v][u] < d[u])
            d[u] = d[v] + matrix[v][u];
    }

    public void dijkstra1(AdjGraph g, String s) {
        // Convert the graph to a matrix
        int[][] matrix = new int[totalNodes][totalNodes];
        for(int i = 0; i < totalNodes; ++i) {
            Arrays.fill(matrix[i], 0);
        }

        // Build the matrix with weight
        for(int i = 0; i < adjList.size(); ++i) {
            for (int j = 0; j < adjList.get(i).size(); ++j) {
                matrix[i][adjList.get(i).get(j)] = adjWeight.get(i).get(j);
            }
        }

        // Initialize the distance array
        int[] d = new int[totalNodes];
        Boolean[] sptArr = new Boolean[totalNodes];

        // Initialize the array
        for (int i = 0; i < totalNodes; i++) {
            d[i] = Integer.MAX_VALUE;
            sptArr[i] = false;
        }

        // The distance for a node itself is always ZERO
        d[g.getNode(s)] = 0;

        // Calculate the shortest path
        for (int i = 0; i < totalNodes - 1; i++) {
            int vertex = minDistanceNeighbor(d, sptArr);
            sptArr[vertex] = true;

            // Relaxation
            for (int j = 0; j < totalNodes; j++)
                relax(vertex, j, d, sptArr, matrix);
        }

        printShortestPath(d, s);
    }

    public void topologicalSortWithQueue(AdjGraph g) {
        int[] arr = new int[g.totalNodes];

        for (int i = 0; i < g.totalNodes; i++) {
            LinkedList<Integer> tmp = g.adjList.get(i);
            for (int node : tmp) {
                arr[node]++;
            }
        }

        Queue<Integer> q = new LinkedList<>();
        for (int i = 0; i < g.totalNodes; i++) {
            if (arr[i] == 0)
                q.add(i);
        }

        int count = 0;

        Vector<Integer> top = new Vector<>();
        while (!q.isEmpty()) {
            int u = q.poll();
            top.add(u);

            for (int node : g.adjList.get(u)) {
                if (--arr[node] == 0)
                    q.add(node);
            }
            count++;
        }

        if (count != g.totalNodes) {
            System.out.println("Solution not found");
            return;
        }

        System.out.print("Topological Sort result: ");
        for (int i : top)
            System.out.print(i + " ");
        System.out.println();
    }

    public void printShortestPath(int[] d, String s) {
        for(int i = 0; i < totalNodes; ++i)
            System.out.println(s + " -> " + nodeList.get(i) + " = " + (d[i] == Integer.MAX_VALUE ? "INF" : d[i]));
    }

    public int minDistanceNeighbor(int[] d, Boolean[] sptArr) {
        int minVal = Integer.MAX_VALUE;
        int minIndex = -1;

        for (int i = 0; i < totalNodes; ++i) {
            if(!sptArr[i] && d[i] <= minVal) {
                minVal = d[i];
                minIndex = i;
            }
        }

        return minIndex;
    }

    public void DFS(int v) {
        setVisited(v);
        LinkedList<Integer> neighbors = adjList.elementAt(v);
        for (int i = 0; i < neighbors.size(); i ++) {
            int v1 = neighbors.get(i);
            if (ifVisited(v1) == false) {
                DFS(v1);
            }
        }
    }

    public void BFS(int s) {
        ArrayList<Integer> toVisit = new ArrayList<Integer>();
        toVisit.add(s);
        while (toVisit.size() > 0) {
            int v = toVisit.remove(0);   // first-in, first-visit
            setVisited(v);
            LinkedList<Integer> neighbors = adjList.elementAt(v);
            for (int i = 0; i < neighbors.size(); i ++) {
                int v1 = neighbors.get(i);
                if ( (ifVisited(v1) == false) && (toVisit.contains(v1) == false) ) {
                    toVisit.add(v1);
                }
            }
        }
    }

    public void display() {
        System.out.println("total nodes: " + totalNodes);
        System.out.println("total edges: " + totalEdges);
    }

    public void displayEnum() {
        for (int i = 0; i < nodeEnum.size(); i ++) {
            System.out.print(nodeList.elementAt(nodeEnum.elementAt(i)) + " ");
        }
        System.out.println();
    }

    public static void main(String argv[]) {
        AdjGraph g1 = new AdjGraph(true);
        String[] nodes1 = {"A", "B", "C", "D", "E"};
        int weight = 1;
        g1.setVertices(nodes1);
        g1.setEdge("A", "B", weight);
        g1.setEdge("B", "C", weight);
        g1.setEdge("C", "D", weight);
        g1.setEdge("A", "C", weight);
        g1.dijkstra1(g1, "C");
        g1.topologicalSortWithQueue(g1);

        System.out.println("====================");

        //second example: g2
        AdjGraph g2 = new AdjGraph(true);
        String[] nodes2 = {"a", "b", "c", "d", "e", "f"};
        g2.setVertices(nodes2);
        g2.setEdge("a", "b", 9);
        g2.setEdge("a", "f", 5);
        g2.setEdge("a", "e", 3);
        g2.setEdge("b", "c", 5);
        g2.setEdge("b", "f", 4);
        g2.setEdge("c", "d", 2);
        g2.setEdge("c", "f", 8);
        g2.setEdge("d", "f", 7);
        g2.setEdge("d", "e", 1);
        g2.setEdge("e", "f", 5);
        g2.dijkstra1(g2, "a");
        g2.topologicalSortWithQueue(g2);

        System.out.println("====================");

        AdjGraph g3 = new AdjGraph(true);
        String[] nodes3 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
        g3.setVertices(nodes3);
        g3.setEdge("a", "b", 2);
        g3.setEdge("b", "f", 1);
        g3.setEdge("a", "e", 8);
        g3.setEdge("c", "a", 3);
        g3.setEdge("b", "f", 2);
        g3.setEdge("c", "d", 2);
        g3.setEdge("c", "f", 1);
        g3.setEdge("d", "f", 1);
        g3.setEdge("f", "e", 8);
        g3.setEdge("e", "f", 1);
        g3.setEdge("g", "a", 4);
        g3.setEdge("g", "e", 2);
        g3.setEdge("g", "h", 5);
        g3.setEdge("h", "i", 5);
        g3.setEdge("i", "j", 9);
        g3.setEdge("j", "i", 6);
        g3.setEdge("h", "a", 4);
        g3.dijkstra1(g3, "h");
        g3.topologicalSortWithQueue(g3);

        System.out.println("====================");

        AdjGraph g4 = new AdjGraph(true);
        String[] nodes4 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
        g4.setVertices(nodes4);
        g4.setEdge("a", "b", 2);
        g4.setEdge("b", "f", 1);
        g4.setEdge("a", "e", 1);
        g4.setEdge("c", "a", 2);
        g4.setEdge("b", "f", 4);
        g4.setEdge("c", "d", 6);
        g4.setEdge("h", "i", 4);
        g4.setEdge("i", "j", 5);
        g4.setEdge("j", "i", 2);
        g4.setEdge("h", "a", 7);
        g4.dijkstra1(g4, "i");
        g4.topologicalSortWithQueue(g4);

        System.out.println("====================");

        AdjGraph g5 = new AdjGraph(true);
        String[] nodes5 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
        g5.setVertices(nodes5);
        g5.setEdge("a", "b", 1);
        g5.setEdge("d", "f", 2);
        g5.setEdge("f", "e", 3);
        g5.setEdge("f", "a", 2);
        g5.setEdge("f", "g", 1);
        g5.setEdge("g", "a", 5);
        g5.setEdge("g", "i", 4);
        g5.setEdge("h", "j", 5);
        g5.setEdge("a", "c", 5);
        g5.setEdge("c", "d", 3);
        g5.setEdge("e", "f", 1);
        g5.dijkstra1(g5, "e");
        g5.topologicalSortWithQueue(g5);

        System.out.println("====================");

        AdjGraph g6 = new AdjGraph(true);
        String[] nodes6 = {"a", "b", "c", "d", "e", "f"};
        g6.setVertices(nodes6);
        g6.setEdge("f", "e", 1);
        g6.setEdge("f", "a", 1);
        g6.setEdge("e", "d", 1);
        g6.setEdge("e", "b", 1);
        g6.setEdge("c", "d", 1);
        g6.setEdge("d", "b", 1);
        g6.dijkstra1(g6, "a");
        g6.topologicalSortWithQueue(g6);
    }


    // Problem Set 11 TODO:

    // write your new main() method here:

    // for Problem Set 11 Task B:
    //   provide 3 different examples, using the two different versions of Dijkstra's algorithm
    //   with at least 10 nodes for each different graph

    // for Problem Set 11 Task C:
    //   provide 3 different examples, using the two different versions of Dijkstra's algorithm
    //   with at least 10 nodes for each different graph


} // end of class AdjGraph
