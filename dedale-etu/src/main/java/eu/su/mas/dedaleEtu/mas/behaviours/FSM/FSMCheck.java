package eu.su.mas.dedaleEtu.mas.behaviours.FSM;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.Adventurer;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.Message;
import eu.su.mas.dedaleEtu.mas.knowledge.SerializableComplexeGraph;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.util.List;

public class FSMCheck extends Behaviour {
	private static final long serialVersionUID = 3124181066200035014L;

	private boolean finished = false;

	//exitValues
	public final static int DEFAULT = 0;
	public final static int SEND_PONG = 1;
	public final static int SEND_END = 2;
	public final static int DECIDE = 3;
	public final static int TIMEOUT = 4;

	//TIMEOUT
	private int WAITCHECK = 10; //For Check
	private int WAITMAP = 100; // Fot Wait_Partial_Map
	private int cptCheck = 0;
	private int cptWaitMap = 0;

	private int exitValue = 0;
	
    private Adventurer myAdventurer;
	private AbstractDedaleAgent myAbstractAgent;

	public FSMCheck(final Adventurer myagent) {
		super(myagent);
        myAdventurer = myagent;
		myAbstractAgent = myagent;
	}

	@Override
	public void action() {
		finished = false;
		exitValue = DEFAULT;

		boolean waitMapDone = waitMap();

		//////////TIMEOUT//////////
		//Even if Check Timeout, we'll still wait for waitMap
		if (!waitCheck() && waitMapDone){
			//System.out.println("Check TIMEOUT");
			exitValue = TIMEOUT;
			finished = true;
		}


		//////////CHECK END//////////
		//Wait for END(Partial Map)
		if(!finished && !waitMapDone){
			boolean get = false;

			MessageTemplate msgTemplate = MessageTemplate.and(
					MessageTemplate.MatchProtocol("END"),
					MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL));
			ACLMessage msgReceived = this.myAgent.receive(msgTemplate);

			if (msgReceived != null) {
				get = true;
				SerializableComplexeGraph<String, MapRepresentation.MapAttribute> sgReceived = null;
				try {
					Message message = (Message) msgReceived.getContentObject();
					//sgReceived = (SerializableComplexeGraph<String, MapRepresentation.MapAttribute>) msgReceived.getContentObject();
					sgReceived = message.getMap();
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
				myAdventurer.getMyMap().mergeMap(sgReceived);
				finished = true;
			}
			if (!get) {
				//System.out.println("Didn't get partial map :(");
			} else {
				//System.out.println("Got partial map :)");
			}
		}


		//////////CHECK MODE & ROLE//////////
		//If in LOCATE mode and has no role, pass to decide to get a role
		if(!finished) {
			int mode = myAdventurer.getMode();
			//Observation role = myAbstractAgent.getMyTreasureType();
			Observation role = myAdventurer.getRole();
			if (mode == Adventurer.LOCATE && role == Observation.ANY_TREASURE) {
				exitValue = DECIDE;
				finished = true;
			}
		}


		//////////CHECK PING//////////
		//if received Ping, send Pong (Entire Map)
		if(!finished) {
			MessageTemplate pingTemplate = MessageTemplate.and(
					MessageTemplate.MatchProtocol("PING"),
					MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
			ACLMessage pingReceived = this.myAgent.receive(pingTemplate);

			if (pingReceived != null) {
				//si on a bien reçu un Ping, On vas devoir envoyer notre map dans un Pong
				//System.out.println(this.getAgent().getLocalName() + " <--PING-- " + pingReceived.getSender().getLocalName());
				myAdventurer.setCorresponder(pingReceived.getSender().getLocalName());
				cptWaitMap = 0; //Extend waiting time for End
				exitValue = SEND_PONG;
				finished = true;
			}
		}


		//////////CHECK PONG//////////
		//if received Pong, send End (Partial Map)
		if(!finished) {
			MessageTemplate pongTemplate = MessageTemplate.and(
					MessageTemplate.MatchProtocol("PONG"),
					MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL));
			ACLMessage pongReceived = this.myAgent.receive(pongTemplate);

			if (pongReceived != null) {
				//System.out.println(this.getAgent().getLocalName() + " <--PONG-- " + pongReceived.getSender().getLocalName());
				myAdventurer.setCorresponder(pongReceived.getSender().getLocalName());

				//Merge map
				SerializableComplexeGraph<String, MapRepresentation.MapAttribute> sgReceived=null;
				try {
					Message message = (Message) pongReceived.getContentObject();
					//sgReceived = (SerializableComplexeGraph<String, MapRepresentation.MapAttribute>) msgReceived.getContentObject();
					sgReceived = message.getMap();
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
				myAdventurer.getMyMap().mergeMap(sgReceived);

				exitValue = SEND_END;
				finished = true;
			}
		}


		//////////CHECK MAP//////////
		//If found treasure, pass to DECIDE
		if(!finished) {
			List<Couple<String, List<Couple<Observation, Integer>>>> lobs = myAbstractAgent.observe();//myPosition
			List<Couple<Observation, Integer>> lObservations = lobs.get(0).getRight();
			//get value of the Treasure
			int value = 0;
			for (Couple<Observation, Integer> o : lObservations) {
				if (o.getRight() != null && o.getRight() > 0) {
					value = o.getRight();
				}
			}

			//If there's at least one type of non-empty treasure
			if (value > 0) {
				//System.out.println(this.myAgent.getLocalName() + " found treasure");
				exitValue = DECIDE;
				finished = true;
			}
		}


		//////////NOTHING//////////
		//If nothing, repeat CHECK
		finished = true;
	}

	@Override
	public boolean done() {
		return finished;
	}
	
	public int onEnd() {
		return exitValue ;
	}

	public boolean waitCheck(){
		//doWait(1);
		cptCheck++;
		if(cptCheck >= WAITCHECK){
			cptCheck = 0;
			return false;
		}
		else return true;
	}

	public boolean waitMap(){
		myAgent.doWait(1);
		cptWaitMap++;
		if(cptWaitMap >= WAITMAP){
			cptWaitMap = 0;
			return false;
		}
		else return true;
	}
}
