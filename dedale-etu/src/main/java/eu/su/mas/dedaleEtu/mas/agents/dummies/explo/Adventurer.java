package eu.su.mas.dedaleEtu.mas.agents.dummies.explo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;

import eu.su.mas.dedaleEtu.mas.behaviours.FSM.*;
import eu.su.mas.dedaleEtu.mas.knowledge.EquityModule;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.FSMBehaviour;


public class Adventurer extends AbstractDedaleAgent {

	private static final long serialVersionUID = -7969469610241668140L;
	private MapRepresentation myMap;
	
	private String corresponder;
	private List<String> listName;

	private int diamondCollected;
	private int diamondFound;
	private int goldCollected;
	private int goldFound;
	
	private List<Couple<String,Integer>> goals = new ArrayList<>();

	//enum for mode
	public static final int EXPLORE = 0;
	public static final int LOCATE = 1;
	public static final int SEARCH = 2;
	private int mode;
	private Observation role = Observation.ANY_TREASURE;

	//Name for the FSM state
	
	private static final String Move = "Move";
	private static final String Ping = "Ping";
	private static final String Check = "Check";
	private static final String Pong = "Pong";
	private static final String End = "End";
	private static final String Decide = "Decide";
    private static final String Collect = "Collect";
    
    public EquityModule equity;

	protected void setup(){

		super.setup();

		mode = EXPLORE;
		
		//get the parameters added to the agent at creation (if any)
		final Object[] args = getArguments();
		
		
		List<String> list_agentNames = new ArrayList<String>();
		
		if(args.length==0){
			System.err.println("Error while creating the agent, names of agent to contact expected");
			System.exit(-1);
		}else{
			int i=2;// WARNING YOU SHOULD ALWAYS START AT 2. This will be corrected in the next release.
			while (i<args.length) {
				list_agentNames = (List<String>) args[i];
				i++;
			}
		}
		
		List<String> listName = new ArrayList<String>(list_agentNames.size());
		for(String name:list_agentNames) {
			listName.add(name);
		}
		listName.remove(this.getLocalName());
		System.out.println(listName);
		
		list_agentNames = listName;
		
		//this.listName = listName;
		
		//FSM implentation
		
		
		FSMBehaviour fsm = new FSMBehaviour(this);
		// Define the different states and behaviours
		fsm.registerFirstState (new FSMMove(this), Move);
		fsm.registerState (new FSMSendPings(this), Ping);
		fsm.registerState (new FSMCheck(this), Check);
		fsm.registerState (new FSMSendPong(this), Pong);
		fsm.registerState (new FSMSendEnd(this), End);
		fsm.registerState (new FSMDecide(this), Decide);
        fsm.registerState (new FSMCollect(this), Collect);
		
		// Register the transitions
		fsm.registerDefaultTransition (Move,Ping);
		fsm.registerDefaultTransition (Ping,Check);
		fsm.registerDefaultTransition (End,Check);
		fsm.registerDefaultTransition (Pong,Check);
		fsm.registerDefaultTransition (Collect,Ping);

		fsm.registerDefaultTransition (Check,Check);
        fsm.registerTransition (Check,Pong, FSMCheck.SEND_PONG);
        fsm.registerTransition (Check,End, FSMCheck.SEND_END);
        fsm.registerTransition (Check,Decide, FSMCheck.DECIDE);
        fsm.registerTransition (Check,Move, FSMCheck.TIMEOUT);

        fsm.registerDefaultTransition (Decide,Check);
        fsm.registerTransition (Decide,Collect, FSMDecide.COLLECT);

		
		DataStore dataFSM = new DataStore();
		dataFSM.put("agents",list_agentNames);
		fsm.setDataStore(dataFSM);
		

		List<Behaviour> lb=new ArrayList<Behaviour>();
		
		/************************************************
		 * 
		 * ADD the behaviours of the Dummy Moving Agent
		 * 
		 ************************************************/
		
		
		
		lb.add(fsm);
		
		
		/***
		 * MANDATORY TO ALLOW YOUR AGENT TO BE DEPLOYED CORRECTLY
		 */
		
		addBehaviour(new startMyBehaviours(this,lb));
		
		System.out.println("the  agent "+this.getLocalName()+ " is started");
		
	}

	public MapRepresentation getMyMap() {
		return myMap;
	}

	public void setMyMap(MapRepresentation myMap) {
		this.myMap = myMap;
	}

	public String getCorresponder() {
		return corresponder;
	}

	public void setCorresponder(String corresponder) {
		this.corresponder = corresponder;
	}

//	public float[] updateRatio(int diamond, int gold){
//		diamondCollected += diamond;
//		goldCollected += gold;
//
//		float nbG = 0;
//
//		List<Couple<String,List<Couple<Observation,Integer>>>> lobs=this.observe();
//		List<Couple<Observation,Integer>> lObservations = lobs.get(0).getRight();
//		for (Couple<Observation, Integer> o : lObservations) {
//			switch (o.getLeft()) {
//				case DIAMOND:
//					nbD += o.getRight();
//				case GOLD:
//					nbG += o.getRight();
//					break;
//				default:
//					break;
//			}
//		}
//		float ratioLocal = nbD/nbG;
//		return new float[]{0,0};
//	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public Observation getRole() {
		return role;
	}

	public void setRole(Observation role) {
		this.role = role;
	}

	public List<Couple<String,Integer>> getGoals(){
		return goals;
	}

	public void setGoals(List<Couple<String,Integer>> goals){
		this.goals = goals;
	}

	public void popGoals(){
		this.goals.remove(0);
	}
}
