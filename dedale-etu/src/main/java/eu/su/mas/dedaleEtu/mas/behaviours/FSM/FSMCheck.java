package eu.su.mas.dedaleEtu.mas.behaviours.FSM;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.Adventurer;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.List;

public class FSMCheck extends Behaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3124181066200035014L;

	private boolean finished = false;

	//exitValues
	public final static int DEFAULT = 0; //
	public final static int Send_Entire_Map = 1;
	public final static int Send_Partial_Map = 2;
	public final static int DECIDE = 3;
	public final static int TIMEOUT = 4;

	/**
	 * Current knowledge of the agent regarding the environment
	 */
	private int exitValue = 0;
	
	public FSMCheck(final Adventurer myagent) {
		super(myagent);
	}

	@Override
	public void action() {
		exitValue = DEFAULT;
		//System.out.println(this.myAgent.getLocalName()+" in "+this.getBehaviourName()+" Stade");

		//Check Mode & Role
		int mode = ((Adventurer) this.myAgent).getMode();
		Observation role = ((AbstractDedaleAgent) this.myAgent).getMyTreasureType();
		if (mode==Adventurer.LOCATE && role==Observation.ANY_TREASURE){
			exitValue = DECIDE;
			finished = true;
		}

		//Check Ping
		MessageTemplate pingTemplate = MessageTemplate.and(
				MessageTemplate.MatchProtocol("PING"),
				MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
		ACLMessage pingReceived=this.myAgent.receive(pingTemplate);
		
		
		if (pingReceived!=null) {
			//si on a bien reçu un Ping, On vas devoir envoyer notre map dans un ACK
			System.out.println( this.getAgent().getLocalName()+" receive a message from "+pingReceived.getSender().getLocalName());
			((Adventurer)this.myAgent).setCorresponder(pingReceived.getSender().getLocalName());
			exitValue = Send_Entire_Map;
			finished = true;
		}
		//sinon on vérifier si l'on à pas recu de ACK nous même


		//Check ACK
		MessageTemplate pongTemplate = MessageTemplate.and(
				MessageTemplate.MatchProtocol("PONG"),
				MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL));
		ACLMessage pongReceived=this.myAgent.receive(pongTemplate);

		if (pongReceived!=null) {
			((Adventurer)this.myAgent).setCorresponder(pongReceived.getSender().getLocalName());
			exitValue = Send_Partial_Map;
			finished = true;
		}

		//Check Map
		List<Couple<String, List<Couple<Observation, Integer>>>> lobs = ((AbstractDedaleAgent) this.myAgent).observe();//myPosition
		List<Couple<Observation, Integer>> lObservations = lobs.get(0).getRight();
		//get value and Type of the Treasure
		int value = 0;
		for(Couple<Observation,Integer> o:lObservations) {
			if (o.getRight() > 0){
				value = o.getRight();
			}
		}
		if (value==0){
			exitValue = DECIDE;
			finished = true;
		}

		if (!((Adventurer) this.myAgent).countTime()){
			exitValue = TIMEOUT;
		}

		finished = true;
	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return finished;
	}
	
	public int onEnd() {
		return exitValue ;
	}

}
