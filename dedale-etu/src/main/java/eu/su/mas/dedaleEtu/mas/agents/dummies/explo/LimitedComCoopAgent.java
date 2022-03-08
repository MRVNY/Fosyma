package eu.su.mas.dedaleEtu.mas.agents.dummies.explo;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;

import eu.su.mas.dedaleEtu.mas.behaviours.ExploCoopBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.FSM.FSMExplo;
import eu.su.mas.dedaleEtu.mas.behaviours.FSM.FSMReceive;
import eu.su.mas.dedaleEtu.mas.behaviours.FSM.FSMSend;
import eu.su.mas.dedaleEtu.mas.behaviours.lim.Explo;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;

/**
 * <pre>
 * ExploreCoop agent. 
 * Basic example of how to "collaboratively" explore the map
 *  - It explore the map using a DFS algorithm and blindly tries to share the topology with the agents within reach.
 *  - The shortestPath computation is not optimized
 *  - Agents do not coordinate themselves on the node(s) to visit, thus progressively creating a single file. It's bad.
 *  - The agent sends all its map, periodically, forever. Its bad x3.
 *   - You should give him the list of agents'name to send its map to in parameter when creating the agent.
 *   Object [] entityParameters={"Name1","Name2};
 *   ag=createNewDedaleAgent(c, agentName, ExploreCoopAgent.class.getName(), entityParameters);
 *  
 * It stops when all nodes have been visited.
 * 
 * 
 *  </pre>
 *  
 * @author hc
 *
 */


public class LimitedComCoopAgent extends AbstractDedaleAgent {

	private static final long serialVersionUID = -7969469610241668140L;
	private MapRepresentation myMap;
	

	//Name for the FSM state
	
	private static final String MoveOn = "MoveOn";
	private static final String Send = "IsSomeoneThere";
	private static final String IGotIt = "IGotIt";

	protected void setup(){

		super.setup();
		
		//get the parameters added to the agent at creation (if any)
		final Object[] args = getArguments();
		
		
		if(args.length==0){
			System.err.println("Error while creating the agent, names of agent to contact expected");
			System.exit(-1);
		}else{
			int i=2;// WARNING YOU SHOULD ALWAYS START AT 2. This will be corrected in the next release.
			while (i<args.length) {
				i++;
			}
		}
		//FSM implentation
		
		/*
		FSMBehaviour fsm = new FSMBehaviour(this);
		// Define the different states and behaviours
		fsm. registerFirstState (new FSMExplo(this,this.myMap), MoveOn);
		fsm. registerState (new FSMSend(this,this.myMap), Send);
		fsm. registerState (new FSMReceive(this,this.myMap), IGotIt);
		
		// Register the transitions
		fsm. registerDefaultTransition (MoveOn,Send);//Default
		fsm. registerDefaultTransition (Send,IGotIt);//Default
		fsm. registerDefaultTransition (IGotIt,MoveOn);//Default
		
		
		fsm. registerTransition (B,B, 2) ;//Cond 2
		fsm. registerTransition (B,C, 1) ;//Cond 1
		*/

		List<Behaviour> lb=new ArrayList<Behaviour>();
		
		/************************************************
		 * 
		 * ADD the behaviours of the Dummy Moving Agent
		 * 
		 ************************************************/
		
		
		
		lb.add(new Explo(this,this.myMap));

		
		
		/***
		 * MANDATORY TO ALLOW YOUR AGENT TO BE DEPLOYED CORRECTLY
		 */
		
		
		addBehaviour(new startMyBehaviours(this,lb));
		
		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}
	
	
}
