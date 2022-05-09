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

	int myMode;
	Observation myRole = null;

	public FSMCheck(final Adventurer myagent) {
		super(myagent);
        myAdventurer = myagent;
		myAbstractAgent = myagent;
	}

	@Override
	public void action() {
		finished = false;
		exitValue = DEFAULT;

		//if(cptCheck==0) myAdventurer.updatePriorities(); //Update the goals so we don't stay on goals from last pos
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
			MessageTemplate msgTemplate = MessageTemplate.and(
					MessageTemplate.MatchProtocol("END"),
					MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL));
			ACLMessage msgReceived = this.myAgent.receive(msgTemplate);

			if (msgReceived != null) {
				SerializableComplexeGraph<String, MapRepresentation.MapAttribute> sgReceived;
				try {
					Message message = (Message) msgReceived.getContentObject();
					//sgReceived = (SerializableComplexeGraph<String, MapRepresentation.MapAttribute>) msgReceived.getContentObject();
					sgReceived = message.getMap();
					myAdventurer.getMyMap().mergeMap(sgReceived);

					//Enchere
					negotiate(message);
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
				finished = true;
			}
		}

		//////////CHECK MODE & ROLE//////////
		//If in LOCATE mode and has no role, pass to decide to get a role
		if(!finished) {
			//Observation myRole = myAbstractAgent.getMyTreasureType();
			if (myMode == Adventurer.LOCATE && myRole == Observation.ANY_TREASURE) {
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
		//if received Pong, Do Enchere and send End (Partial Map)
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
					myAdventurer.getMyMap().mergeMap(sgReceived);

					//Enchere
					negotiate(message);
				} catch (UnreadableException e) {
					e.printStackTrace();
				}

				exitValue = SEND_END;
				finished = true;
			}
		}

		//////////CHECK MAP//////////
		//If found treasure, pass to DECIDE (Only collect when locate)
		if(!finished && myMode == Adventurer.LOCATE) {
			List<Couple<String, List<Couple<Observation, Integer>>>> lobs = myAbstractAgent.observe();//myPosition
			List<Couple<Observation, Integer>> lObservations = lobs.get(0).getRight();
			for (Couple<Observation, Integer> o : lObservations) {
				switch (o.getLeft()) {
					case DIAMOND:case GOLD:
						if(o.getRight()>0){
							exitValue = DECIDE;
							finished = true;
						}
                        break;
                    default:
                        break;
				}
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

	private void negotiate(Message message){
		int hisMode = message.getMode();
		String hisGoal = message.getGoal();
		String hisNextNode = message.getNextNode();
		String hisPos = message.getPosition();

		String myPos = myAbstractAgent.getCurrentPosition();
		String myGoal = message.getGoal();
		String myNextNode = myAdventurer.getNextNode();


		if(message != null && hisNextNode != null && hisPos != null && hisGoal != null && myGoal != null && myPos != null && myNextNode != null) {
			int hisDist = myAdventurer.getMyMap().getShortestLength(hisPos,hisGoal);
			int myDist = myAdventurer.getMyMap().getShortestLength(myPos,myGoal);

			// Hierarchy of modes : L > E > S
			//Check current goals and positions
            boolean sameGoal = hisGoal.equals(myGoal);
            boolean block = myPos.equals(hisNextNode) && myNextNode.equals(hisPos)|| myAdventurer.getNextNode().equals(hisNextNode);

			if (sameGoal || block    												//IF we have the same goal OR we are blocking each other
			&& (hisMode > myMode                                                	//AND IF his mode is more important my mine
			|| (hisMode == myMode && hisDist <= myDist))){	//OR we have the same role but his path is shorter

				//System.out.println(myAdventurer.getLocalName()+": "+myPos+" -> "+ myNextNode+" -> "+myGoal+": "+myAdventurer.getPriorities());
				//System.out.println(message.getName()+": "+hisPos+" -> "+hisNextNode+" -> "+hisGoal);

				myAdventurer.setGoal(null); 										//THEN I give up my goal

				//AND I need to find a new goal in life (I'm only able to change my own goals)
				List<String> myPriorities = myAdventurer.getPriorities();

				if(myPriorities!=null){
					for(String newGoal: myPriorities){
						myNextNode = myAdventurer.getNextNode(newGoal);
                        int newDist = myAdventurer.getMyMap().getShortestLength(myPos,newGoal);

                        block = myNextNode != null && ((myPos.equals(hisNextNode) && myNextNode.equals(hisPos)) || myNextNode.equals(hisNextNode));
                        sameGoal = hisGoal.equals(newGoal);

						//IF same goal OR block BUT we have the same mode and my route is faster
						if((sameGoal || block) && hisMode == myMode && hisDist > newDist) {
							myAdventurer.setGoal(newGoal);
							break;               		//THEN I still beat him and get the goal
						}

						else if(myNextNode!=null) {		//IF not same goal and don't block THEN I get the goal
							myAdventurer.setGoal(newGoal);
							break;
						}
						//ELSE I pass to the next goal
					}
				}
				//System.out.println("DONE" + " - " + myAbstractAgent.getLocalName() +", "+ myPos +" -> " + myAdventurer.getNextNode() +" -> "+myAdventurer.getGoal()+ '\n');
				if(myAdventurer.getGoal()==null){
					myAdventurer.setGoal(myAdventurer.getGoal());
				}
			}
		}
	}

	private boolean waitCheck(){

		//doWait(1);
		cptCheck++;
		if(cptCheck >= WAITCHECK){
			cptCheck = 0;
			return false;
		}
		else return true;
	}

	private boolean waitMap(){

		myAgent.doWait(1);
		cptWaitMap++;
		if(cptWaitMap >= WAITMAP){
			cptWaitMap = 0;
			return false;
		}
		else return true;
	}
}
