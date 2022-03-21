package eu.su.mas.dedaleEtu.mas.behaviours.FSM;

import java.io.IOException;
import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class FSMSEM extends Behaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4810526640574152546L;

	private boolean finished = false;

	/**
	 * Current knowledge of the agent regarding the environment
	 */
	private int exitValue = 0;
	
	public FSMSEM(final AbstractDedaleAgent myagent) {
		super(myagent);

	}

	@Override
	public void action() {
		// problème ici, comment savoir qui nous a envoyer le ping depuis ce behavior.
		List<String> list_agentNames = (List<String>) this.getParent().getDataStore().get("agents");
		for(String agent : list_agentNames) {
			//sendEntireMap(agent);
		}
		finished = true;

	}
	
	private void sendEntireMap(String agentName) {
		ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
		msg.setProtocol("PING");
		msg.setSender(this.myAgent.getAID());
		msg.addReceiver(new AID("agentName",false));
		try {					
			msg.setContentObject(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
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
