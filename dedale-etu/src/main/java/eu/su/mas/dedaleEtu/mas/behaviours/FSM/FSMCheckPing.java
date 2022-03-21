package eu.su.mas.dedaleEtu.mas.behaviours.FSM;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class FSMCheckPing extends Behaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3124181066200035014L;

	private boolean finished = false;

	/**
	 * Current knowledge of the agent regarding the environment
	 */
	private int exitValue = 0;
	
	public FSMCheckPing(final AbstractDedaleAgent myagent) {
		super(myagent);
	}

	@Override
	public void action() {
		// On vérifie si on a reçu un Ping 
		MessageTemplate msgTemplate=MessageTemplate.and(
				MessageTemplate.MatchProtocol("PING"),
				MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
		ACLMessage msgReceived=this.myAgent.receive(msgTemplate);
		
		if (msgReceived!=null) {
			//si on a bien reçu un Ping, On vas devoir envoyer notre map dans un ACK
			exitValue = 1;
		}
		//sinon on vérifier si l'on à pas recu de ACK nous même
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
