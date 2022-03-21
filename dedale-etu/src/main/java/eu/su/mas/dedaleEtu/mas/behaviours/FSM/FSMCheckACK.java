package eu.su.mas.dedaleEtu.mas.behaviours.FSM;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
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
		
		MessageTemplate msgTemplate=MessageTemplate.and(
				MessageTemplate.MatchProtocol("PING"),
				MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
		ACLMessage msgReceived=this.myAgent.receive(msgTemplate);
		
		if (msgReceived!=null) {
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
