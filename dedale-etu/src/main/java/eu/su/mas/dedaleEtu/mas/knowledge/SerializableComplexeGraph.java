package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import dataStructures.serializableGraph.SerializableNode;

/**
 * This class offer a basic serializable graph as a set of nodes and edges.
 * The nodes are serializable and composed of a nodeID and an object (the node's content).
 * The edges are just represented as a list of nodeId associated to each node.
 * 
 * @author hc
 *
 * @param <String> nodeId
 * @param <V> object that possess all desired attributes associated to a given node
 */
public class SerializableComplexeGraph<String,V> implements Serializable {

	private static final long serialVersionUID = 296081571572029922L;

	private Map<SerializableNode<String,V>,Set<String>> myMap;

	public SerializableComplexeGraph(){
		myMap=new HashMap<SerializableNode<String,V>,Set<String>>();		
	}

	/**
	 * If a node already exists with this identifier the nodeContent is added
	 * @param nodeId Arbitrary and unique string identifying the node.
	 */
	public void addNode(String nodeId,V nodeContent){
		SerializableNode<String, V> n=getNode(nodeId);
		if (n==null){
			n=new SerializableNode<String, V>(nodeId, nodeContent);
		}else{
			n.setContent(nodeContent);
		}
		Set<String> edgeSet=new HashSet<String>();

		if (myMap.containsKey(n))
			edgeSet=myMap.get(n);

		myMap.put(n,edgeSet);
	}

	/**
	 * If a node already exists with this identifier nothing is changed
	 * @param nodeId Arbitrary and unique string identifying the node.
	 */
	public void addNode(String nodeId){
		SerializableNode<String, V> n=new SerializableNode<String, V>(nodeId, null);
		if (!myMap.containsKey(n))
			myMap.put(n,null);
	}

	/**
	 * Get a node by its identifier. 
	 * @param nodeId
	 * @return a node, or null if no node exist with this Id
	 */
	public SerializableNode<String, V> getNode(String nodeId){
		SerializableNode<String, V> n=null;
		boolean found=false;
		if (myMap.containsKey(n)){
			Set<SerializableNode<String,V>> nodeSet=this.myMap.keySet();
			Iterator<SerializableNode<String,V>>iter=nodeSet.iterator();

			while (iter.hasNext() && !found){
				n=iter.next();
				found=(n.equals(new SerializableNode<String, V>(nodeId, null)));
			}	
		}
		return (found)?n:null;
	}
	
	/**
	 *
	 * @return  The set of nodes that constitute the graph
	 */
	public Set<SerializableNode<String, V>> getAllNodes(){
		return this.myMap.keySet();
	}

	/**
	 * If the edge already exist, does nothing. Otherwise add the edge to both nodes.
	 * @param edgeId not used for now.
	 * @param node1Id
	 * @param node2Id
	 */
	public void addEdge(String edgeId,String node1Id,String node2Id){
		Set<String> edges=this.myMap.get(new SerializableNode<String, V>(node1Id));
		Set<String> edges2=this.myMap.get(new SerializableNode<String, V>(node2Id));
		if (edges==null){
			System.err.println("Edges should never be null, empty at best");
			System.exit(-1);
		}
		edges.add(node2Id);
		edges2.add(node1Id);	
	}

	/**
	 * 
	 * @param nodeId
	 * @return the edges associated to a given node as a set of NodeId
	 */
	public Set<String> getEdges(String nodeId){
		return this.myMap.get(new SerializableNode<String, V>(nodeId));
	}
	
	@Override
	public java.lang.String toString() {
		return this.myMap.toString();
	}

}
