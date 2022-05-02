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

	int myMode = -1;
	Observation myRole = null;

	public FSMCheck(final Adventurer myagent) {
		super(myagent);
        myAdventurer = myagent;
		myAbstractAgent = myagent;
	}

	@Override
	public void action() {
		if(this.myAdventurer.debug())
			System.out.println("CHECKING" + Integer.toString(cptCheck) + " - " + myAbstractAgent.getLocalName());

		finished = false;
		exitValue = DEFAULT;

		if(cptCheck==0) myAdventurer.updatePriorities(); //Update the goals so we don't stay on goals from last pos
		myMode = myAdventurer.getMode();
		myRole = myAdventurer.getRole();

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
			if(this.myAdventurer.debug()) System.out.println("CHECK END" + " - " + myAbstractAgent.getLocalName());
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

					//Enchere
					enchere(message);
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
				myAdventurer.getMyMap().mergeMap(sgReceived);
				finished = true;
			}
			if (!get) {
				if(this.myAdventurer.debug())
					System.out.println("Didn't get partial map :(" + " - " + myAbstractAgent.getLocalName());
			} else {
				if(this.myAdventurer.debug())
					System.out.println("Got partial map :)" + " - " + myAbstractAgent.getLocalName());
			}
		}

		//////////CHECK MODE & ROLE//////////
		//If in LOCATE mode and has no role, pass to decide to get a role
		if(!finished) {
			if(this.myAdventurer.debug()) System.out.println("CHECK MODE ROLE" + " - " + myAbstractAgent.getLocalName());
			//Observation myRole = myAbstractAgent.getMyTreasureType();
			if (myMode == Adventurer.LOCATE && myRole == Observation.ANY_TREASURE) {
				exitValue = DECIDE;
				finished = true;
			}
		}


		//////////CHECK PING//////////
		//if received Ping, send Pong (Entire Map)
		if(!finished) {
			if(this.myAdventurer.debug()) System.out.println("CHECK PING" + " - " + myAbstractAgent.getLocalName());
			MessageTemplate pingTemplate = MessageTemplate.and(
					MessageTemplate.MatchProtocol("PING"),
					MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
			ACLMessage pingReceived = this.myAgent.receive(pingTemplate);

			if (pingReceived != null) {
				//si on a bien reçu un Ping, On vas devoir envoyer notre map dans un Pong
				//System.out.println(this.getAgent().getLocalName() + " <--PING-- " + pingReceived.getSender().getLocalName());
				myAdventurer.setCorresponder(pingReceived.getSender().getLocalName());
				cptWaitMap = 0; //Extend waiting time for End
				if(this.myAdventurer.debug())
					System.out.println("RESET WAITMAP" + " - " + myAbstractAgent.getLocalName());
				exitValue = SEND_PONG;
				finished = true;
			}
		}


		//////////CHECK PONG//////////
		//if received Pong, Do Enchere and send End (Partial Map)
		if(!finished) {
			if(this.myAdventurer.debug()) System.out.println("CHECK PONG" + " - " + myAbstractAgent.getLocalName());
			MessageTemplate pongTemplate = MessageTemplate.and(
					MessageTemplate.MatchProtocol("PONG"),
					MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL));
			ACLMessage pongReceived = this.myAgent.receive(pongTemplate);

			if (pongReceived != null) {
				//System.out.println(this.getAgent().getLocalName() + " <--PONG-- " + pongReceived.getSender().getLocalName());
				myAdventurer.setCorresponder(pongReceived.getSender().getLocalName());
				if(this.myAdventurer.debug()) System.out.println("GOT PONG" + " - " + myAbstractAgent.getLocalName());

				//Merge map
				SerializableComplexeGraph<String, MapRepresentation.MapAttribute> sgReceived=null;
				try {
					Message message = (Message) pongReceived.getContentObject();
					//sgReceived = (SerializableComplexeGraph<String, MapRepresentation.MapAttribute>) msgReceived.getContentObject();
					sgReceived = message.getMap();

					//Enchere
					enchere(message);
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
			if(this.myAdventurer.debug()) System.out.println("CHECK MAP" + " - " + myAbstractAgent.getLocalName());
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

		if(this.myAdventurer.debug())
			System.out.println("CHECKED" + Integer.toString(cptCheck) + " - " + myAbstractAgent.getLocalName());
	}

	@Override
	public boolean done() {
		return finished;
	}
	
	public int onEnd() {
		return exitValue ;
	}

	private void enchere(Message message){
		if(this.myAdventurer.debug()) System.out.println("ENCHERE" + " - " + myAbstractAgent.getLocalName());
		int hisMode = message.getMode();
		Couple<String,Integer> hisGoal = message.getGoal();
		String hisNextNode = message.getNextNode();
		String hisPos = message.getPosition();

		String myPos = myAbstractAgent.getCurrentPosition();
		Couple<String,Integer> myGoal = message.getGoal();
		String myNextNode = myAdventurer.getNextNode();

		if(message != null && hisNextNode != null && hisPos != null && hisGoal != null && myGoal != null && myPos != null && myNextNode != null) {
			// Hierarchy of modes : W > L > E > S

			//Check current goals and positions
			if (hisGoal.getLeft().equals(myGoal.getLeft())                                //IF we have the same goal
			|| (myPos.equals(hisNextNode) && myAdventurer.getNextNode().equals(hisPos))   //OR we are blocking each other
			&& (hisMode > myMode                                                	//AND IF his mode is more important my mine
			|| (hisMode == myMode && hisGoal.getRight() <= myGoal.getRight()))){	//OR we have the same role but his path is shorter
				myGoal = null;    //THEN I give up my goal
				myAdventurer.setGoal(null);

				//AND I need to find a new goal in life (I'm only able to change my own goals)
				List<Couple<String,Integer>> myPriorities = myAdventurer.getPriorities();
				if(myPriorities!=null){
					for(Couple<String,Integer> newGoal: myPriorities){
						myAdventurer.setGoal(newGoal);
						myNextNode = myAdventurer.getNextNode();
						boolean block = myNextNode != null && myPos.equals(hisNextNode) && myNextNode.equals(hisPos);

						if(newGoal.getLeft().equals(hisGoal.getLeft()) || block) {        		//IF we have the same goal OR we block
							if(hisMode == myMode && hisGoal.getRight() >= newGoal.getRight()){	//BUT if we have the same mode and my route is faster
								myGoal = newGoal;	//THEN I still beat him and get the goal
								myAdventurer.setGoal(myGoal);
								break;
							}
							else { //Or else we pass to the next goal
								myGoal = null;
								myAdventurer.setGoal(null);
							}
						}
						else { //IF we don't have the same goal and we don't block
							if(myNextNode==null){
								myGoal = null;
								myAdventurer.setGoal(null);
							}
							else{
								myGoal = newGoal;    //THEN I get the goal
								myAdventurer.setGoal(myGoal);
								break;
							}
						}
					}
				}
			}
		}
		if(this.myAdventurer.debug()) System.out.println("ENCHERE DONE" + " - " + myAbstractAgent.getLocalName());
	}

	private boolean waitCheck(){
		if(this.myAdventurer.debug()) System.out.println("WAIT CHECK" + " - " + myAbstractAgent.getLocalName());

		//doWait(1);
		cptCheck++;
		if(cptCheck >= WAITCHECK){
			cptCheck = 0;
			return false;
		}
		else return true;
	}

	private boolean waitMap(){
		if(this.myAdventurer.debug()) System.out.println("WAIT MAP" + " - " + myAbstractAgent.getLocalName());

		myAgent.doWait(1);
		cptWaitMap++;
		if(cptWaitMap >= WAITMAP){
			cptWaitMap = 0;
			return false;
		}
		else return true;
	}
}
