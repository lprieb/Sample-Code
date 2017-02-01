/*
 * Name: Luis Prieb
 * Student ID: 2016843211
 * Don't forget to remove the package line (if it exists)
 */

import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.ArrayList;

/*
 * Do not import anything else
 */

public class Graph
{

    private int numNodes, numEdges;
    HashMap<Node, ArrayList<Edge>> adjacencyList = new HashMap<Node, ArrayList<Edge>>();
    ArrayList<Edge> edgeList = new ArrayList<Edge>();
    ArrayList<Node> nodeList = new ArrayList<Node>();
    
    /*
     * Our instance variables
     *
     * numNodes - int - holds the number of nodes currently in the graph
     * numEdges - int - holds the number of edges currently in the graph
     *
     * You may add any other instance variables that you wish
     */

    public Graph()
    {
        /*
         * Constructor for our Graph
         */
        numNodes = 0;
        numEdges = 0;
    }

    public Node addVertex(String name)
    {
        /*
         * Add a vertex with the given name to the graph
         * 
         * Return null if there is already a vertex with this name, otherwise
         * return the constructed vertex
         */
        // Need to Write Search Algorithm for name
        if(containsNode(name))
        {
        	return null;
        }
        else
        {
        	numNodes += 1;
        	Node nodeToAdd = new Node(name);
        	ArrayList<Edge> listToAdd = new ArrayList<Edge>();
        	adjacencyList.put(nodeToAdd, listToAdd);
        	nodeList.add(nodeToAdd);
        	return nodeToAdd;
        }
    }
    private Node addVertex(Node n)
    {
    	if(containsNode(n))
        {
        	return null;
        }
        else
        {
        	numNodes += 1;
        	Node nodeToAdd = n;
        	ArrayList<Edge> listToAdd = new ArrayList<Edge>();
        	adjacencyList.put(nodeToAdd, listToAdd);
        	nodeList.add(nodeToAdd);
        	return nodeToAdd;
        }
    }

    public Edge addEdge(Node u, Node v, int weight)
    {
        /*
         * Add an edge with the given endpoints and weight to the graph
         *
         * If u and/or v do not exist, create them and add them to the graph
         * and then add the edge
         * 
         * If an edge with these endpoints already exists, do not overwrite it
         *
         * Note that this graph is undirected, meaning an edge u,v is the same 
         * as v,u
         *
         *
         * Return null if there is already an edge with these endpoints or if u
         * and v are the same. Otherwise, return the edge
         */
        if(!containsNode(u))
        	addVertex(u);

        if(!containsNode(v))
        	addVertex(v);

        if(containsEdge(u, v))
       	{
       		return null;
       	}
       	else
       	{
       		numEdges += 1;
       		Edge edgeToAdd = new Edge(u, v, weight);
       		ArrayList<Edge> uEdgeList = adjacencyList.get(u);
       		ArrayList<Edge> vEdgeList = adjacencyList.get(v);
       		vEdgeList.add(edgeToAdd);
       		uEdgeList.add(edgeToAdd);
       		adjacencyList.put(u,uEdgeList);
       		adjacencyList.put(v,vEdgeList);
       		edgeList.add(edgeToAdd);
       		return edgeToAdd;
       	}
    }
	private Node getNode(String name)
	{
		for(Node n : nodeList)
		{
			if(n.getName() == name)
				return n;
		}
		return null;
	}
	private Edge getEdge(Node u, Node v)
	{
		for(Edge e : edgeList)
		{
			if((u == e.getU() || u == e.getV()) && (v == e.getU() || v == e.getV()))
			{
				return e;
			}
		}
		
		return null;
	}
	private boolean containsNode(String name)
	{
		Node n = getNode(name);
        if(n == null)
        	return false;
        else
        	return true;
	}
	private boolean containsNode(Node n)
	{
		for(Node node : nodeList)
		{
			if(node == n)
				return true;
		}
		return false;
	}
	private boolean containsEdge(Node u, Node v)
	{
		Edge e = getEdge(u, v);
		if(e == null)
        	return false;
        else
        	return true;
	}

    public HashMap<Node, ArrayList<Edge>> getAdjacencyList()
    {
        /*
         * Return a hashmap with all of the nodes in the graph as the keys
         *     and the values being a list of all the edges that have the node
         *     as an endpoint
         */
        return adjacencyList;
    }

    public HashMap<Node, Integer> dijkstra(Node source)
    {
        /*
         * Return a hashmap with all of the nodes in the graph as the keys and
         * the value being the length of the shortest path from the source to
         * the node. Return null if there are negative weights in the graph
         */
        HashMap<Node, Integer> shortestPaths = new HashMap<Node,Integer>();
        Node node, neighbor;
        int cDistance;
        int nDistance;

        for(Node n : nodeList)
        {
        	shortestPaths.put(n, Integer.MAX_VALUE);
        	n.setValue(Integer.MAX_VALUE);
        }
        shortestPaths.put(source, 0); // This will replace MAX_VALUE with 0 for the source node
        source.setValue(0);

        PriorityQueue<Node> pq = new PriorityQueue<Node>();

        for(Node n: nodeList)
        {
        	pq.add(n);
        }
        while(pq.size() > 0)
        {
        	node = pq.poll();
        	for(Edge e : adjacencyList.get(node))
        	{

        		neighbor = getNeighbor(e, node);
        		if(pq.contains(neighbor))
        		{
        			cDistance = shortestPaths.get(neighbor);
        			if(e.getWeight() < 0)
        				return null;
        			nDistance = e.getWeight() + shortestPaths.get(node);

        			if(nDistance < cDistance)
        			{
        				shortestPaths.put(neighbor, nDistance);
        				neighbor.setValue(nDistance);
        				// Value changed, so we remove and add the queue again to reposition
        				pq.remove(neighbor);
        				pq.add(neighbor);
        			}
        		}
        	}

        }

        return shortestPaths;

    }
    private Node getNeighbor(Edge e, Node n)
    {
    	if(n == e.getU())
    		return e.getV();
    	else if(n == e.getV())
    		return e.getU();
    	else
    		return null;
    }

    public int shortestPathLength(Node source, Node target)
    {
        /*
         * Return the length of the shortest path from source to target
         * or return -1 if there are negative edge weights
         */
         HashMap<Node, Integer> shortestPaths = dijkstra(source);
         return shortestPaths.get(target);
    }

    public ArrayList<Edge> minSpanningTree()
    {
        /*
         * Return a list of all of the edges in the minimum spanning tree of
         * the graph. The order does not matter.
         */
        ArrayList<Edge> minTree = new ArrayList<Edge>();
        Node firstNode = nodeList.get(0);
        Node currentNode;
        Node minNeighbor;
        Node currentNeighbor;
        Edge currentEdge;
        int minWeight;
        Edge minEdge;
        myEntry cEntry;
        myEntry cEntryNeighbor;
        ArrayList<myEntry> entryList = new ArrayList<myEntry>();
        ArrayList<Edge> tempEdgeCont = new ArrayList<Edge>();

        for(Node n : nodeList)
        {
        	n.setValue(Integer.MAX_VALUE);
        }
        firstNode.setValue(0);

        PriorityQueue<myEntry> pq = new PriorityQueue<myEntry>();
        for(Node n : nodeList)
    	{
    		cEntry = new myEntry(n);
    		entryList.add(cEntry);
    		pq.add(cEntry);
    	}

        while(pq.size() > 0)
        {
        	cEntry = pq.poll();
        	currentNode = cEntry.getNode();
        	currentEdge = cEntry.getMinEdge();
        	minWeight = Integer.MAX_VALUE;
        	minEdge = null;
        	tempEdgeCont = adjacencyList.get(currentNode);
        	

        	if(currentEdge != null)
        	{
        		minTree.add(currentEdge);
        	}

        	for(Edge e: tempEdgeCont)
        	{

        		currentNeighbor = getNeighbor(e, currentNode);
        		cEntryNeighbor =getEntry(currentNeighbor, entryList);
        		if(pq.contains(cEntryNeighbor))
        		{
        			if(e.getWeight() < currentNeighbor.getValue())
        			{
        				currentNeighbor.setValue(e.getWeight());
        				cEntryNeighbor.setMinEdge(e);
        				pq.remove(cEntryNeighbor);
        				pq.add(cEntryNeighbor);
        			}
	        		
	        	}
        	}
        	
        }

        return minTree;
    }

    public int minSpanningTreeWeight()
    {
        /*
         * Return the total weight of the minimum spanning tree of the graph
         */
        ArrayList<Edge> aMinSpanningTree = minSpanningTree();
        int totalWeight = 0;

        for(Edge e : aMinSpanningTree)
        {
        	totalWeight += e.getWeight();
        }

        return totalWeight;
    }

    public int getNumNodes()
    {
        /*
         * Return the number of nodes in the graph
         */
        return numNodes;
    }

    public int getNumEdges()
    {
        /*
         * Return the number of edges in the graph
         */
        return numEdges;
    }

    public ArrayList<Node> getNodes()
    {
        /*
         * Return the nodes in the graph
         */
        return nodeList;
    }

    public ArrayList<Edge> getEdges()
    {
        /*
         * Return the edges in the graph
         */
        return edgeList;
    }
    private myEntry getEntry(Node n, ArrayList<myEntry> entryList)
    {
    	for(myEntry e : entryList)
    	{
    		if(e.getNode() == n)
    			return e;
    	}
    	return null;

    }
    // Had to make my own because we couldn't import java.util.Map.Entry
    class myEntry implements Comparable<myEntry>
    {
    	Node n;
    	Edge minEdge;
    	public myEntry(Node n)
    	{
    		this.n = n;
    		this.minEdge = null;
    	}
    	public myEntry(Node n, Edge minEdge)
    	{
    		this.n = n;
    		this.minEdge = minEdge;
    	}
    	public void setMinEdge(Edge minEdge)
    	{
    		this.minEdge = minEdge;
    	}
    	public Edge getMinEdge()
    	{
    		return this.minEdge;
    	}
    	public Node getNode()
    	{
    		return n;
    	}
    	public int compareTo(myEntry e)
    	{
    		Node fNode = e.getNode();
    		
    		return n.getValue() - fNode.getValue();
    	}
    }


}

/*
 *  The code below was provided by the department of Computer Science at Yonsei University
 */

class Node implements Comparable<Node>
{
    private String name;
    private int value;

    public Node(String name)
    {
        this.name = name;
        this.value = 0;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setValue(int value)
    {
        this.value = value;
    }

    public String getName()
    {
        return name;
    }

    public int getValue()
    {
        return value;
    }

    public int compareTo(Node n)
    {
        return this.value - n.getValue();
    }
}

class Edge implements Comparable<Edge>
{
    private Node u,v;
    private int weight;

    public Edge(Node u, Node v, int weight)
    {
        this.u = u;
        this.v = v;
        this.weight = weight;
    }

    public void setU(Node u)
    {
        this.u = u;
    }

    public void setV(Node v)
    {
        this.v = v;
    }

    public void setWeight(int weight)
    {
        this.weight = weight;
    }

    public Node getU()
    {
        return u;
    }

    public Node getV()
    {
        return v;
    }

    public int getWeight()
    {
        return weight;
    }

    public int compareTo(Edge e)
    {
        return this.weight - e.getWeight();
    }
}