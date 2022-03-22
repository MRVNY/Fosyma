package eu.su.mas.dedaleEtu.mas.behaviours.FSM;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.Adventurer;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class FSMCheckACK extends Behaviour {

	private boolean finished = false;

	/**
	 * Current knowledge of the agent regarding the environment
	 */
	private int exitValue = 0;
	
	public FSMCheckACK(final AbstractDedaleAgent myagent) {
		super(myagent);

	}

	@Override
	public void action() {
		System.out.println("I'm in "+this.getBehaviourName()+" Stade");
		
		MessageTemplate msgTemplate=MessageTemplate.and(
				MessageTemplate.MatchProtocol("PONG"),
				MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL));
		ACLMessage msgReceived=this.myAgent.receive(msgTemplate);
		
		if (msgReceived!=null) {
			((Adventurer)this.myAgent).setCorresponder(this.getAgent().getLocalName());
			exitValue = 1;
		}
		finished = true;

	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return finished;
	}
	
	public int onEnd() {
		return exitValue;
	}
}
