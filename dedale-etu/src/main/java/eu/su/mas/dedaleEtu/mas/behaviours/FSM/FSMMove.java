package eu.su.mas.dedaleEtu.mas.behaviours.FSM;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;

import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.knowledge.EquityModule;
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
	private static final long serialVersionUID = 8402143361534297234L;

	private boolean finished = false;

	/**
	 * Current knowledge of the agent regarding the environment
	 */
	private MapRepresentation myMap;
	private Adventurer myAdventurer;
	private AbstractDedaleAgent myAbstractAgent;
	private String lastPos = "";
	private int cptBlock = 0;
	private final int BLOCKMAX = 10;
	private int cptDeBlock = 0;


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
		myAdventurer = myagent;
		myAbstractAgent = myagent;
		myAdventurer.updatePriorities();
	}

	@Override
	public void action() {
		if(this.myAdventurer.debug()) System.out.println("START MAP" + " - " + myAbstractAgent.getLocalName());

		finished = false;
		//System.out.println(this.myAgent.getLocalName()+" in "+this.getBehaviourName()+" State");

		if(this.myMap==null) {
			this.myMap= new MapRepresentation();
			this.myMap.addCapacity(myAdventurer.getLocalName(), myAdventurer.getBackPackFreeSpace());	
		}

		this.myMap.countSameMap();
		

		//0) Retrieve the current position
		String myPosition=myAbstractAgent.getCurrentPosition();

		if (myPosition!=null){

			//List of observable from the agent's current position
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=myAbstractAgent.observe();//myPosition

			/**
			 * Just added here to let you see what the agent is doing, otherwise he will be too quick
			 */
			try {
				this.myAgent.doWait(100);
			} catch (Exception e) {
				e.printStackTrace();
			}

			List<Couple<Observation,Integer>> lObservations= lobs.get(0).getRight();
			boolean hasTreasure = false;
			for(Couple<Observation,Integer> o:lObservations){
				switch (o.getLeft()) {
				case DIAMOND:
					this.myMap.addNewTreasure(new Treasure(o.getRight(), myPosition, Observation.DIAMOND));
					hasTreasure = true;
					//System.out.println(this.getAgent().getLocalName() + " just found Gold");
					break;

				case GOLD:
					this.myMap.addNewTreasure(new Treasure(o.getRight(), myPosition, Observation.GOLD));
					hasTreasure = true;
					//System.out.println(this.getAgent().getLocalName() + " just found Diamond");
					break;

//				default:
//					if(myMap.getTreasureCollection().getAllLocation().contains(myPosition)) {
//						//System.out.println(this.myMap.getTreasureCollection());
//						//System.out.println(myPosition);
//						this.myMap.getTreasureCollection().updateTreasure(myPosition, 0);
//						//System.out.println(this.myMap.getTreasureCollection());
//					}
//					break;


                }
			}

			if(!hasTreasure){
				if(myMap.getTreasureCollection().getAllLocation().contains(myPosition)){
					this.myMap.getTreasureCollection().updateTreasure(myPosition, 0);
				}
				else this.myMap.getTreasureCollection().removeTreasure(myPosition);
				myAdventurer.resetGoal();
			}


			//1) remove the current node from openlist and add it to closedNodes.
			this.myMap.addNode(myPosition, MapAttribute.closed);

			//2) get the surrounding nodes and, if not in closedNodes, add them to open nodes.
			Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
			while(iter.hasNext()){
				String nodeId=iter.next().getLeft();
				boolean isNewNode=this.myMap.addNewNode(nodeId);
				//the node may exist, but not necessarily the edge
				if (myPosition!=nodeId) {
					this.myMap.addEdge(myPosition, nodeId);
				}
			}

			//3) while openNodes is not empty, continues.

			int mode = myAdventurer.getMode();

			if ((!this.myMap.hasOpenNode() || this.myMap.sameMapTIMEOUT()) && mode == Adventurer.EXPLORE){
				System.out.println(this.myAgent.getLocalName()+" passes to LOCATE");
				myAdventurer.setMode(Adventurer.LOCATE);

				myAdventurer.equity = new EquityModule(myAdventurer.getMyMap(),this.getAgent().getLocalName());
				myAdventurer.setRole(myAdventurer.equity.getType());

				//System.out.println(myAdventurer.equity.getType());
				
				//System.out.println(this.myMap.getCapacity());
				
				//this crap is only here for testing purpose, don't mind it.
				
//				this.myMap.getTreasureCollection().removeTreasure("25");
//				this.myMap.getTreasureCollection().removeTreasure("7");
//				
//				this.myMap.getTreasureCollection().updateTreasure("25",0);
//				this.myMap.getTreasureCollection().updateTreasure("7",0);
				
				//Ressources sur la cartes actuellement
//				System.out.println("Gold: "+this.myMap.getTreasureCollection().countGold());
//				System.out.println("Diamond: "+this.myMap.getTreasureCollection().countDiamond());

			}

			//4) select next move.

			//4.1 If there exist one open node directly reachable, go for it,
			//otherwise choose one from the openNode list, compute the shortestPath and go for it
			//We now update goals in FSMCheck since we need to do enchere

			myAdventurer.setMyMap(myMap);

			if(this.myAdventurer.debug())
				System.out.println("MAP DONE" + " - " + myAbstractAgent.getLocalName());

			String nextNode = myAdventurer.getNextNode();

			if(nextNode==null || nextNode.equals(myPosition) || myAdventurer.getGoal().equals(myPosition)){
				myAdventurer.resetGoal();
				nextNode = myAdventurer.getNextNode();
			}

			if(myPosition.equals(lastPos)) cptBlock++; //Unblock mechanism

            if(cptBlock >= BLOCKMAX){
				if(cptDeBlock==0) System.out.println("DEBLOCK "+myAdventurer.getLocalName() +": "+ myPosition+" -> "+nextNode);
				List<String> otherNodes = myAdventurer.possibleNexts();
				otherNodes.remove(nextNode);
				Collections.shuffle(otherNodes);
				if(!otherNodes.isEmpty())nextNode = otherNodes.get(0);
				cptDeBlock++;

				if(cptDeBlock >= BLOCKMAX) {
					cptDeBlock = 0;
					cptBlock = 0;
				}
			}

			if(nextNode==null) {
				Random r = new Random();
				int moveId = 1 + r.nextInt(lobs.size() - 1);//removing the current position from the list of target, not necessary as to stay is an action but allow quicker random move
				nextNode = lobs.get(moveId).getLeft();
			}

			lastPos = myPosition;

			if(this.myAdventurer.debug()) System.out.println("NEXT NODE DONE" + " - " + myAbstractAgent.getLocalName());

			if(nextNode!=null) myAbstractAgent.moveTo(nextNode);

			finished=true;
            if(this.myAdventurer.debug()) System.out.println("MOVED" + " - " + myAbstractAgent.getLocalName());

		}
	}

	@Override
	public boolean done() {
		return finished;
	}
	
	public int onEnd() {return ExitValue ;}

}
