package eu.su.mas.dedaleEtu.mas.behaviours.FSM;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;

import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.Adventurer;


import eu.su.mas.dedaleEtu.mas.knowledge.Treasure;
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
				this.myAgent.doWait(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
			List<Couple<Observation,Integer>> lObservations= lobs.get(0).getRight();
			for(Couple<Observation,Integer> o:lObservations){
				switch (o.getLeft()) {
				case DIAMOND:
					this.myMap.addNewTreasure(new Treasure(o.getRight(), myPosition, Observation.DIAMOND));
					//System.out.println(this.getAgent().getLocalName() + " just found Gold");
					break;
				case GOLD: 
					this.myMap.addNewTreasure(new Treasure(o.getRight(), myPosition, Observation.GOLD));
					//System.out.println(this.getAgent().getLocalName() + " just found Diamond");
					break;
				}
			}
			
			
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
					//
				}
			}

			//3) while openNodes is not empty, continues.

			int mode = ((Adventurer)this.myAgent).getMode();

			if (!this.myMap.hasOpenNode() && mode == Adventurer.EXPLORE){
				System.out.println(this.myAgent.getLocalName()+" passes to LOCATE");
				((Adventurer)this.myAgent).setMode(Adventurer.LOCATE);
				
				//affichage des trésors trouvés
				System.out.println(this.myMap.getTreasureCollection());
				//Ressources trouvés 
				System.out.println("Gold: "+this.myMap.getTreasureCollection().countGold());
				System.out.println("Diamond: "+this.myMap.getTreasureCollection().countDiamond());
			}

			//4) select next move.

			//4.1 If there exist one open node directly reachable, go for it,
			//	 otherwise choose one from the openNode list, compute the shortestPath and go for it
			if (this.myMap.hasOpenNode() && mode==Adventurer.EXPLORE || mode==Adventurer.SEARCH){
				//no directly accessible openNode
				//chose one, compute the path and take the first step.
				nextNode=this.myMap.getShortestPathToClosestOpenNode(myPosition);//getShortestPath(myPosition,this.openNodes.get(0)).get(0);
				//System.out.println(this.myAgent.getLocalName()+"-- list= "+this.myMap.getOpenNodes()+"| nextNode: "+nextNode);
			}

			else if (mode==Adventurer.LOCATE){
				Observation role = ((Adventurer)this.myAgent).getRole();
				Observation treType;
				if(role==Observation.DIAMOND) treType = Observation.DIAMOND;
				else treType = Observation.GOLD;

				try {
					nextNode = this.myMap.getShortestPathToClosestTreasure(myPosition, treType);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if(nextNode==null){
				Random r = new Random();
				int moveId = 1 + r.nextInt(lobs.size() - 1);//removing the current position from the list of target, not necessary as to stay is an action but allow quicker random move
				nextNode = lobs.get(moveId).getLeft();
			}

			((Adventurer)this.myAgent).setMyMap(myMap);

			((AbstractDedaleAgent)this.myAgent).moveTo(nextNode);

			finished=true;

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
