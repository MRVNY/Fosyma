package eu.su.mas.dedaleEtu.mas.agents.dummies.explo;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;

import eu.su.mas.dedaleEtu.mas.behaviours.FSM.FSMCheckACK;
import eu.su.mas.dedaleEtu.mas.behaviours.FSM.FSMCheckPing;
import eu.su.mas.dedaleEtu.mas.behaviours.FSM.FSMExplo;
import eu.su.mas.dedaleEtu.mas.behaviours.FSM.FSMPing;
import eu.su.mas.dedaleEtu.mas.behaviours.FSM.FSMSEM;
import eu.su.mas.dedaleEtu.mas.behaviours.FSM.FSMSPM;
import eu.su.mas.dedaleEtu.mas.behaviours.FSM.FSMWCM;
import eu.su.mas.dedaleEtu.mas.behaviours.FSM.FSMWait;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.FSMBehaviour;




public class Adventurer extends AbstractDedaleAgent {

	private static final long serialVersionUID = -7969469610241668140L;
	private MapRepresentation myMap;
	
	private String corresponder;
	
	

	//Name for the FSM state
	
	private static final String Explo = "Exploration";
	private static final String Ping = "Ping";
	private static final String Wait = "Wait";
	private static final String CheckPing = "CheckPing";
	private static final String SEM = "SendEntireMap";
	private static final String WCM = "Wait_CheckforMap";
	private static final String CheckACK = "CheckACK";
	private static final String SPM = "SendPieceofMap";

	protected void setup(){

		super.setup();
		
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
		
		
		
		//lb.add(new Explo(this,this.myMap));
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
	
	
	
}
