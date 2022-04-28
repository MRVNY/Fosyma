package eu.su.mas.dedaleEtu.mas.behaviours.FSM;

import java.io.IOException;
import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.Adventurer;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class FSMSendPings extends Behaviour {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -2964208976149209685L;

	private boolean finished = false;

	/**
	 * Current knowledge of the agent regarding the environment
	 */
	private int exitValue = 0;
	
	public FSMSendPings(final Adventurer myagent) {super(myagent);}

	@Override
	public void action() {
		finished = false;
		
		//System.out.println(this.myAgent.getLocalName()+" in "+this.getBehaviourName()+" Stade");
		//récupération de la listes des agents 
		List<String> list_agentNames = (List<String>) this.getParent().getDataStore().get("agents");
		
		//System.out.println(((Adventurer)this.myAgent).getMyMap());
		
		//envoie d'un ping pour chaque agents
		for(String agent : list_agentNames) {
			sendPing(agent);
		}
		
		finished = true;

	}
	
	private void sendPing(String agentName) {
		ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
		msg.setProtocol("PING");
		msg.setSender(this.myAgent.getAID());
		msg.addReceiver(new AID(agentName,false));
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