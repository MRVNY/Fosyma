package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.view.Viewer;
import dataStructures.serializableGraph.*;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import javafx.application.Platform;

/**
 * This simple topology representation only deals with the graph, not its content.</br>
 * The knowledge representation is not well written (at all), it is just given as a minimal example.</br>
 * The viewer methods are not independent of the data structure, and the dijkstra is recomputed every-time.
 * 
 * @author hc
 */
public class MapRepresentation implements Serializable {

	/**
	 * A node is open, closed, or agent
	 * @author hc
	 *
	 */

	public enum MapAttribute {	
		agent,open,closed;

	}

	private static final long serialVersionUID = -1333959882640838272L;

	/*********************************
	 * Parameters for graph rendering
	 ********************************/

	private String defaultNodeStyle= "node {"+"fill-color: black;"+" size-mode:fit;text-alignment:under; text-size:14;text-color:white;text-background-mode:rounded-box;text-background-color:black;}";
	private String nodeStyle_open = "node.agent {"+"fill-color: forestgreen;"+"}";
	private String nodeStyle_agent = "node.open {"+"fill-color: blue;"+"}";
	//private String nodeStyle_gold = "node.gold {"+"fill-color: yellow;"+"}";
	//private String nodeStyle_diamond = "node.diamond {"+"fill-color: purple;"+"}";
	private String nodeStyle=defaultNodeStyle+nodeStyle_agent+nodeStyle_open;
	

	private Graph g; //data structure non serializable
	private Viewer viewer; //ref to the display,  non serializable
	private Integer nbEdges;//used to generate the edges ids

	private SerializableComplexeGraph<String, MapAttribute> sg;//used as a temporary dataStructure during migration
	
	private TreasureCollection treasure = new TreasureCollection();
	private HashMap<String,List<Couple<Observation, Integer>>> agentCapacity = new HashMap<>();

	public MapRepresentation() {
		//System.setProperty("org.graphstream.ui.renderer","org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		System.setProperty("org.graphstream.ui", "javafx");
		this.g= new SingleGraph("My world vision");
		this.g.setAttribute("ui.stylesheet",nodeStyle);

		Platform.runLater(() -> {
			openGui();
		});
		//this.viewer = this.g.display();

		this.nbEdges=0;
	}
	

	/**
	 * Add or replace a node and its attribute 
	 * @param id
	 * @param mapAttribute
	 */
	public synchronized void addNode(String id,MapAttribute mapAttribute){
		Node n;
		if (this.g.getNode(id)==null){
			n=this.g.addNode(id);
		}else{
			n=this.g.getNode(id);
		}
		n.clearAttributes();
		n.setAttribute("ui.class", mapAttribute.toString());
		n.setAttribute("ui.label",id);
	}

	/**
	 * Add a node to the graph. Do nothing if the node already exists.
	 * If new, it is labeled as open (non-visited)
	 * @param id id of the node
	 * @return true if added
	 */
	public synchronized boolean addNewNode(String id) {
		if (this.g.getNode(id)==null){
			addNode(id,MapAttribute.open);
			return true;
		}
		return false;
	}
	
	public synchronized boolean addNewTreasure(Treasure t) {
		if (t != null){
			this.treasure.addTreasure(t);
			return true;
		}
		return false;
	}

	/**
	 * Add an undirect edge if not already existing.
	 * @param idNode1
	 * @param idNode2
	 */
	public synchronized void addEdge(String idNode1,String idNode2){
		this.nbEdges++;
		try {
			this.g.addEdge(this.nbEdges.toString(), idNode1, idNode2);
		}catch (IdAlreadyInUseException e1) {
			System.err.println("ID existing");
			System.exit(1);
		}catch (EdgeRejectedException e2) {
			this.nbEdges--;
		} catch(ElementNotFoundException e3){

		}
	}

	/**
	 * Compute the shortest Path from idFrom to IdTo. The computation is currently not very efficient
	 * 
	 * 
	 * @param idFrom id of the origin node
	 * @param idTo id of the destination node
	 * @return the list of nodes to follow, null if the targeted node is not currently reachable
	 */
	public synchronized List<String> getShortestPath(String idFrom,String idTo){
		List<String> shortestPath=new ArrayList<String>();

		Dijkstra dijkstra = new Dijkstra();//number of edge
		dijkstra.init(g);
		dijkstra.setSource(g.getNode(idFrom));

		dijkstra.compute();//compute the distance to all nodes from idFrom
		//if(g.getNode(idTo)==null || g.getNode(idFrom)==null) return null;
		List<Node> path=dijkstra.getPath(g.getNode(idTo)).getNodePath(); //the shortest path from idFrom to idTo
		Iterator<Node> iter=path.iterator();
		while (iter.hasNext()){
			shortestPath.add(iter.next().getId());
		}
		dijkstra.clear();
		if (shortestPath.isEmpty()) {//The openNode is not currently reachable
			return null;
		}else {
			shortestPath.remove(0);//remove the current position
		}

		return shortestPath;
	}

	public String getShortestPathToClosestOpenNode(String myPosition) {
		//1) Get all openNodes
		List<String> opennodes=getOpenNodes();

		//2) select the closest one
		List<Couple<String,Integer>> lc=
				opennodes.stream()
				.map(on -> (getShortestPath(myPosition,on)!=null)? new Couple<String, Integer>(on,getShortestPath(myPosition,on).size()): new Couple<String, Integer>(on,Integer.MAX_VALUE))//some nodes my be unreachable if the agents do not share at least one common node.
				.collect(Collectors.toList());

		Optional<Couple<String,Integer>> closest=lc.stream().min(Comparator.comparing(Couple::getRight));
		//3) Compute shorterPath

		if(closest.isPresent() && closest.get().getLeft()!=null){
			List<String> out = getShortestPath(myPosition,closest.get().getLeft());
			if (!out.isEmpty()) return out.get(0);
			else return null;
		}
		else return null;
	}



	public List<String> getOpenNodes(){
		return this.g.nodes()
				.filter(x ->x .getAttribute("ui.class")==MapAttribute.open.toString()) 
				.map(Node::getId)
				.collect(Collectors.toList());
	}


	/**
	 * Before the migration we kill all non serializable components and store their data in a serializable form
	 */
	public void prepareMigration(){
		serializeGraphTopology();

		closeGui();

		this.g=null;
	}

	/**
	 * Before sending the agent knowledge of the map it should be serialized.
	 */
	private void serializeGraphTopology() {
		this.sg= new SerializableComplexeGraph<String,MapAttribute>();
		Iterator<Node> iter=this.g.iterator();
		while(iter.hasNext()){
			Node n=iter.next();
			sg.addNode(n.getId(),MapAttribute.valueOf((String)n.getAttribute("ui.class")));
		}
		Iterator<Edge> iterE=this.g.edges().iterator();
		while (iterE.hasNext()){
			Edge e=iterE.next();
			Node sn=e.getSourceNode();
			Node tn=e.getTargetNode();
			sg.addEdge(e.getId(), sn.getId(), tn.getId());
		}
		// adding the treasure to the serialiazeGraph
		sg.addTreasures(treasure);
		
		sg.addCapacity(agentCapacity);
		
		
		
		
	}


	public synchronized SerializableComplexeGraph<String,MapAttribute> getSerializableGraph(){
		serializeGraphTopology();
		return this.sg;
	}

	/**
	 * After migration we load the serialized data and recreate the non serializable components (Gui,..)
	 */
	public synchronized void loadSavedData(){

		this.g= new SingleGraph("My world vision");
		this.g.setAttribute("ui.stylesheet",nodeStyle);

		openGui();

		Integer nbEd=0;
		for (SerializableNode<String, MapAttribute> n: this.sg.getAllNodes()){
			this.g.addNode(n.getNodeId()).setAttribute("ui.class", n.getNodeContent().toString());
			for(String s:this.sg.getEdges(n.getNodeId())){
				this.g.addEdge(nbEd.toString(),n.getNodeId(),s);
				nbEd++;
			}
		}
		this.treasure = sg.getTreasures();
		System.out.println("Loading done");
	}

	/**
	 * Method called before migration to kill all non serializable graphStream components
	 */
	private synchronized void closeGui() {
		//once the graph is saved, clear non serializable components
		if (this.viewer!=null){
			//Platform.runLater(() -> {
			try{
				this.viewer.close();
			}catch(NullPointerException e){
				System.err.println("Bug graphstream viewer.close() work-around - https://github.com/graphstream/gs-core/issues/150");
			}
			//});
			this.viewer=null;
		}
	}

	/**
	 * Method called after a migration to reopen GUI components
	 */
	private synchronized void openGui() {
		this.viewer =new FxViewer(this.g, FxViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);//GRAPH_IN_GUI_THREAD)
		viewer.enableAutoLayout();
		viewer.setCloseFramePolicy(FxViewer.CloseFramePolicy.CLOSE_VIEWER);
		viewer.addDefaultView(true);

		g.display();
	}
	
	/**
	 * 
	 * @return true if there exist at least one openNode on the graph 
	 */
	public boolean hasOpenNode() {
		return (this.g.nodes()
				.filter(n -> n.getAttribute("ui.class")==MapAttribute.open.toString())
				.findAny()).isPresent();
	}

	public void mergeMap(SerializableComplexeGraph<String, MapAttribute> sgreceived) {
		//System.out.println("You should decide what you want to save and how");
		//System.out.println("We currently blindy add the topology");

		for (SerializableNode<String, MapAttribute> n: sgreceived.getAllNodes()){
			//System.out.println(n);
			boolean alreadyIn =false;
			//1 Add the node
			Node newnode=null;
			try {
				newnode=this.g.addNode(n.getNodeId());
			}	catch(IdAlreadyInUseException e) {
				alreadyIn=true;
				//System.out.println("Already in"+n.getNodeId());
			}
			if (!alreadyIn) {
				newnode.setAttribute("ui.label", newnode.getId());
				newnode.setAttribute("ui.class", n.getNodeContent().toString());
			}else{
				newnode=this.g.getNode(n.getNodeId());
				//3 check its attribute. If it is below the one received, update it.
				if (((String) newnode.getAttribute("ui.class"))==MapAttribute.closed.toString() || n.getNodeContent().toString()==MapAttribute.closed.toString()) {
					newnode.setAttribute("ui.class",MapAttribute.closed.toString());
				}
			}
		}

		//4 now that all nodes are added, we can add edges
		for (SerializableNode<String, MapAttribute> n: sgreceived.getAllNodes()){
			for(String s:sgreceived.getEdges(n.getNodeId())){
				addEdge(n.getNodeId(),s);
			}
		}
		//merge the treasure knowledge
		if(!sgreceived.getTreasures().isEmpty()) {
			this.treasure.mergeTreasure(sgreceived.getTreasures());
		}
		this.agentCapacity.putAll(sgreceived.getCapacity());
		//System.out.println("Merge done");
	}

	
	/***
	 * We try to only collect the part of the map that we're missing 
	 * @param sgreceived the other map
	 * @return the missing part
	 */
	public MapRepresentation getMissingPart(SerializableComplexeGraph<String, MapAttribute> sgreceived) {
		// we want to get only the part that our map is missing in sgreceived
		MapRepresentation partialMap = new MapRepresentation();
		
		for (SerializableNode<String, MapAttribute> n: sg.getAllNodes()) {
			boolean notAlreadyIn =true;
			for (SerializableNode<String, MapAttribute> m: sgreceived.getAllNodes()){
				if(n.getNodeId() == m.getNodeId()) {
					notAlreadyIn = false;
					break;
				}
			if(notAlreadyIn) {
				partialMap.g.addNode(n.getNodeId());
			}
		}
	}
		
		
		partialMap.treasure = this.treasure.getMissingPart(sgreceived.getTreasures());
		partialMap.agentCapacity.putAll(sgreceived.getCapacity());
		return partialMap;
	}
	
	public String getShortestPathToClosestTreasure(String myPosition,Observation type) throws Exception {
		if (this.treasure.isEmpty()) {
			throw new Exception("La liste de trésors est vide pour le moment");
		}
		
		//1) Get all location of Treasures
		List<String> treasurenodes=this.treasure.getAllLocation(type);
		if (treasurenodes.isEmpty()) {
			throw new Exception("La liste de trésors ne semble pas contenir de " + type);
		}

		//2) select the closest one
		List<Couple<String,Integer>> lc=
				treasurenodes.stream()
				.map(on -> (getShortestPath(myPosition,on)!=null)? new Couple<String, Integer>(on,getShortestPath(myPosition,on).size()): new Couple<String, Integer>(on,Integer.MAX_VALUE))//some nodes my be unreachable if the agents do not share at least one common node.
				.collect(Collectors.toList());

		Optional<Couple<String,Integer>> closest=lc.stream().min(Comparator.comparing(Couple::getRight));
		//3) Compute shorterPath

		if(closest.isPresent() && closest.get().getLeft()!=null){
			List<String> out = getShortestPath(myPosition,closest.get().getLeft());
			if (!out.isEmpty()) return out.get(0);
			else return null;
		}
		else return null;

	}
	
	public String getShortestPathToMostValuableTreasure(String myPosition,Observation type) throws Exception {
		if (this.treasure.isEmpty()) {
			throw new Exception("La liste de trésors est vide pour le moment");
		}
		
		return this.getShortestPath(myPosition, this.treasure.getMostValueable(type).getLocation()).get(0);
	}
	
	public String getShortestPathToSomeTreasure(String myPosition,Treasure treasure) throws Exception {
		if(this.treasure.isIn(treasure)) {
			return this.getShortestPath(myPosition,treasure.getLocation()).get(0);
		}
		throw new Exception(treasure +" n'existe pas sur la map.");
	}
	
	public String getShortestPathToSomeValueTreasure(String myPosition, int value,Observation type) throws Exception {
		if (this.treasure.isEmpty()) {
			throw new Exception("La liste de trésors est vide pour le moment");
		}
		
		List<Integer> treasureValues = this.treasure.getAllValue(type);
		//if there is a treasure for the values that we're looking for we set it as our objectif
		if(treasureValues.contains(value)) {
			return this.getShortestPath(myPosition, treasure.getTreasure(value).getLocation()).get(0);
		}
		else {
			float dist = Float.POSITIVE_INFINITY;
			Treasure closest = null;
			for(Treasure t:this.treasure.getTreasures()) {
				if(dist > Math.abs(value - t.getTreasureAmount())) {
					dist = Math.abs(value - t.getTreasureAmount());
					closest = t;
				}
			}
			return closest.getLocation();
		}
	}
	
	public String getShortestPathToGoal(String myPosition,String goal) throws Exception {
		if(goal == null) {
			throw new Exception("The goal isn't defined.");
		}
		List<String> paths = this.getShortestPath(myPosition, goal);
		if (!paths.isEmpty()) return paths.get(0);
		else return null;
	}
	
	public TreasureCollection getTreasureCollection() {
		return this.treasure;
	}
	
	public void addCapacity(String agentName, List<Couple<Observation,Integer>> data) {
		if(!this.agentCapacity.containsKey(agentName)) {
			this.agentCapacity.put(agentName,data);
		}
		
	}
	
	public HashMap<String,List<Couple<Observation, Integer>>> getCapacity(){
		return this.agentCapacity;
	}
	
	public List<Couple<String,Integer>> getClosestOpenNodes(String myPosition) {
        //1) Get all openNodes
        List<String> opennodes=getOpenNodes();

        //2) Sort nodes
        List<Couple<String,Integer>> lc =
                opennodes.stream()
                        .map(on -> (getShortestPath(myPosition,on)!=null)? new Couple<String, Integer>(on,getShortestPath(myPosition,on).size()): new Couple<String, Integer>(on,Integer.MAX_VALUE))//some nodes my be unreachable if the agents do not share at least one common node.
                        .collect(Collectors.toList());

        lc.sort(Comparator.comparingInt(Couple::getRight));

        return lc;
    }

    public List<Couple<String,Integer>> getClosestTreasures(String myPosition,Observation type) throws Exception {
        if (this.treasure.isEmpty()) {
            throw new Exception("La liste de trésors est vide pour le moment");
        }

        //1) Get all location of Treasures
        List<String> treasurenodes = this.treasure.getAllLocation(type);
        if (treasurenodes.isEmpty()) {
            throw new Exception("La liste de trésors ne semble pas contenir de " + type);
        }

        //2) Sort nodes
        List<Couple<String,Integer>> lc =
                treasurenodes.stream()
                        .map(on -> (getShortestPath(myPosition,on)!=null)? new Couple<String, Integer>(on,getShortestPath(myPosition,on).size()): new Couple<String, Integer>(on,Integer.MAX_VALUE))//some nodes my be unreachable if the agents do not share at least one common node.
                        .collect(Collectors.toList());

        lc.sort(Comparator.comparingInt(Couple::getRight));

        return lc;
    }


}