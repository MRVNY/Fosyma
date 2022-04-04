package eu.su.mas.dedaleEtu.mas.agents.dummies.explo;

import java.util.ArrayList;
import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;

import eu.su.mas.dedaleEtu.mas.behaviours.FSM.*;
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

	//enum for mode
	private int EXPLORE = 0;
	private int LOCATE = 1;
	private int SEARCH = 2;
	private int mode;

	//Name for the FSM state
	
	private static final String Explo = "Exploration";
	private static final String Ping = "Ping";
	private static final String Wait = "Wait";
	private static final String CheckPing = "CheckPing";
	private static final String SEM = "SendEntireMap";
	private static final String WCM = "Wait_CheckforMap";
	private static final String CheckACK = "CheckACK";
	private static final String SPM = "SendPieceofMap";
	private static final String DECIDE = "Decide";

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
		fsm. registerFirstState (new FSMExplo(this), Explo);
		fsm. registerState (new FSMPing(this), Ping);
		fsm. registerState (new FSMWait(this), Wait);
		fsm. registerState (new FSMCheckPing(this), CheckPing);
		fsm. registerState (new FSMSEM(this), SEM);
		fsm. registerState (new FSMWCM(this), WCM);
		fsm. registerState (new FSMCheckACK(this), CheckACK);
		fsm. registerState (new FSMSPM(this), SPM);
		fsm. registerState (new FSMDecide(this), DECIDE);
		
		// Register the transitions
		fsm. registerDefaultTransition (Explo,Ping);
		fsm. registerDefaultTransition (Ping,Wait);
		fsm. registerDefaultTransition (Wait,CheckPing);
		fsm. registerDefaultTransition (CheckPing,CheckACK);
		fsm. registerDefaultTransition (CheckACK,Explo);
		fsm. registerDefaultTransition (SPM,Wait);
		fsm. registerDefaultTransition (SEM,WCM);
		fsm. registerDefaultTransition (WCM,CheckPing);
		
		
		fsm. registerTransition (CheckPing,SEM, 1) ;
		fsm. registerTransition (CheckACK,SPM, 1) ;
		
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

}
