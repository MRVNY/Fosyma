package eu.su.mas.dedaleEtu.mas.behaviours.FSM;

import java.util.Iterator;
import java.util.List;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;

import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.Adventurer;
import eu.su.mas.dedaleEtu.mas.behaviours.ShareMapBehaviour;


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
public class FSMExplo extends SimpleBehaviour {

	


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



	public FSMExplo(final Adventurer myagent) {
		super(myagent);
		myMap = myagent.getMyMap();
			
	}

	@Override
	public void action() {
		
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
			
			Couple<Observation,Integer> treasure = null;
			List<Couple<Observation,Integer>> lObservations= lobs.get(0).getRight();
			for(Couple<Observation,Integer> o:lObservations){
				switch (o.getLeft()) {
				case DIAMOND: this.myMap.addNode(myPosition, MapAttribute.diamond);
				case GOLD: this.myMap.addNode(myPosition, MapAttribute.gold);
				default : this.myMap.addNode(myPosition, MapAttribute.closed);
				}
			}
			
			
			//1) remove the current node from openlist and add it to closedNodes.
			//this.myMap.addNode(myPosition, MapAttribute.closed);

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
			
			// ATTENTION JE SUIS UN TEST DE COLLECTE DE RESSOURCES
			
			
			//System.out.println(this.myAgent.getLocalName()+" -- list of observables: "+lObservations);
			
			Boolean b=false;
			for(Couple<Observation,Integer> o:lObservations){
				switch (o.getLeft()) {
				case DIAMOND:case GOLD:
					System.out.println(this.myAgent.getLocalName()+" - My treasure type is : "+((AbstractDedaleAgent) this.myAgent).getMyTreasureType());
					System.out.println(this.myAgent.getLocalName()+" - My current backpack capacity is:"+ ((AbstractDedaleAgent) this.myAgent).getBackPackFreeSpace());
					System.out.println(this.myAgent.getLocalName()+" - Value of the treasure on the current position: "+o.getLeft() +": "+ o.getRight());
					System.out.println(this.myAgent.getLocalName()+" - I try to open the safe: "+((AbstractDedaleAgent) this.myAgent).openLock(o.getLeft()));
					System.out.println(this.myAgent.getLocalName()+" - The agent grabbed : "+((AbstractDedaleAgent) this.myAgent).pick());
					System.out.println(this.myAgent.getLocalName()+" - the remaining backpack capacity is: "+ ((AbstractDedaleAgent) this.myAgent).getBackPackFreeSpace());
					b=true;
					break;
				default:
					break;
				}
			}
			
			// ATTENTION JE SUIS UN TEST DE COLLECTE DE RESSOURCES

			//3) while openNodes is not empty, continues.
			if (!this.myMap.hasOpenNode()){
				// on dosi donc passer a la collecte de trésors, avec toute la map connue 
				finished=true;
				ExitValue = 0;
				
				
				System.out.println(this.myAgent.getLocalName()+" - Exploration successufully done.");
			}else{
				//4) select next move.
				//4.1 If there exist one open node directly reachable, go for it,
				//	 otherwise choose one from the openNode list, compute the shortestPath and go for it
				if (nextNode==null){
					//no directly accessible openNode
					//chose one, compute the path and take the first step.
					nextNode=this.myMap.getShortestPathToClosestOpenNode(myPosition).get(0);//getShortestPath(myPosition,this.openNodes.get(0)).get(0);
					//System.out.println(this.myAgent.getLocalName()+"-- list= "+this.myMap.getOpenNodes()+"| nextNode: "+nextNode);
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
