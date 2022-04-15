package eu.su.mas.dedaleEtu.mas.behaviours.FSM;

import java.util.Iterator;
import java.util.List;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;

import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.Adventurer;


import jade.core.behaviours.SimpleBehaviour;



/**
 * <pre>
 * This behaviour allows an agent to explore the environment and learn the associated topological map.
 * The algorithm is a pseudo - DFS computationally consuming because its not optimised at all.
 * 
 * When all the nodes around him are visited, the agent randomly select an open node and go there to restart its dfs. 
 * This (non optimal) behaviour is done until all nodes are explored. 
 * 
 * Warning, this behaviour does not save the content of visited nodes, only the topology.
 * Warning, the sub-behaviour ShareMap periodically share the whole map
 * </pre>
 * @author hc
 *
 */
public class FSMMove extends SimpleBehaviour {

	


	/**
	 * 
	 */
	private static final long serialVersionUID = 8402143361534297234L;

	private boolean finished = false;

	/**
	 * Current knowledge of the agent regarding the environment
	 */
	private MapRepresentation myMap;

	private List<String> list_agentNames;

/**
 *
 * @param myagent
 * @param myMap known map of the world the agent is living in
 * @param agentNames name of the agents to share the map with
 */
		
		
	private int ExitValue = 0;



	public FSMMove(final Adventurer myagent) {
		super(myagent);
		myMap = myagent.getMyMap();
			
	}

	@Override
	public void action() {
		finished = false;
		
		//System.out.println(this.myAgent.getLocalName()+" in "+this.getBehaviourName()+" Stade");

		if(this.myMap==null) {
			this.myMap= new MapRepresentation();
		}
		

		//0) Retrieve the current position
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();

		if (myPosition!=null){
			//List of observable from the agent's current position
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition

			/**
			 * Just added here to let you see what the agent is doing, otherwise he will be too quick
			 */
			try {
				this.myAgent.doWait(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			List<Couple<Observation,Integer>> lObservations= lobs.get(0).getRight();
			/*
			Couple<Observation,Integer> treasure = null;
			
			for(Couple<Observation,Integer> o:lObservations){
				switch (o.getLeft()) {
				case DIAMOND: this.myMap.addNode(myPosition, MapAttribute.diamond);
				case GOLD: this.myMap.addNode(myPosition, MapAttribute.gold);
				default : this.myMap.addNode(myPosition, MapAttribute.closed);
				}
			}
			*/
			
			//1) remove the current node from openlist and add it to closedNodes.
			this.myMap.addNode(myPosition, MapAttribute.closed);

			//2) get the surrounding nodes and, if not in closedNodes, add them to open nodes.
			String nextNode=null;
			Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
			while(iter.hasNext()){
				String nodeId=iter.next().getLeft();
				boolean isNewNode=this.myMap.addNewNode(nodeId);
				//the node may exist, but not necessarily the edge
				if (myPosition!=nodeId) {
					this.myMap.addEdge(myPosition, nodeId);
					if (nextNode==null && isNewNode) nextNode=nodeId;
				}
			}

			//3) while openNodes is not empty, continues.
			if (!this.myMap.hasOpenNode()){
				if(((Adventurer)this.myAgent).getMode()==Adventurer.EXPLORE) {
					System.out.println(this.myAgent.getLocalName()+" - Exploration successufully done.");
				}
				// on dosi donc passer a la collecte de trésors, avec toute la map connue
				((Adventurer)this.myAgent).setMode(Adventurer.LOCATE);
				finished=true;
			}else{
				int mode = ((Adventurer)this.myAgent).getMode();

				//4) select next move.

				//4.1 If there exist one open node directly reachable, go for it,
				//	 otherwise choose one from the openNode list, compute the shortestPath and go for it
				if (mode==Adventurer.EXPLORE){
					//no directly accessible openNode
					//chose one, compute the path and take the first step.
					nextNode=this.myMap.getShortestPathToClosestOpenNode(myPosition).get(0);//getShortestPath(myPosition,this.openNodes.get(0)).get(0);
					//System.out.println(this.myAgent.getLocalName()+"-- list= "+this.myMap.getOpenNodes()+"| nextNode: "+nextNode);
				}

				else if (mode==Adventurer.LOCATE){
					nextNode=this.myMap.getShortestPathToClosestOpenNode(myPosition).get(0);//getShortestPath(myPosition,this.openNodes.get(0)).get(0);
				}

				else if (mode==Adventurer.SEARCH){
					nextNode=this.myMap.getShortestPathToClosestOpenNode(myPosition).get(0);//getShortestPath(myPosition,this.openNodes.get(0)).get(0);
				}


				((Adventurer)this.myAgent).setMyMap(myMap);
				
				((AbstractDedaleAgent)this.myAgent).moveTo(nextNode);
			
				finished=true;
			}

		}
	}

	@Override
	public boolean done() {
		return finished;
	}
	
	public int onEnd() {
		return ExitValue ;
		}

}
