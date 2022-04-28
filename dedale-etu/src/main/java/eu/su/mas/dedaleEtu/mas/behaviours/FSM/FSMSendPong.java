package eu.su.mas.dedaleEtu.mas.behaviours.FSM;

import java.io.IOException;
import java.io.Serializable;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.Adventurer;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.knowledge.Message;
import eu.su.mas.dedaleEtu.mas.knowledge.SerializableComplexeGraph;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class FSMSendPong extends Behaviour {

	private static final long serialVersionUID = 4810526640574152546L;

	private boolean finished = false;

	/**
	 * Current knowledge of the agent regarding the environment
	 */
	private int exitValue = 0;
	
	public FSMSendPong(final Adventurer myagent) {
		super(myagent);

	}

	@Override
	public void action() {
		finished = false;
		//System.out.println(this.myAgent.getLocalName()+" in "+this.getBehaviourName()+" Stade");
		// problème ici, comment savoir qui nous a envoyer le ping depuis ce behavior.
		sendEntireMap(((Adventurer)this.myAgent).getCorresponder());
		finished = true;

	}
	
	private void sendEntireMap(String agentName) {
		ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
		msg.setProtocol("PONG");
		msg.setSender(this.myAgent.getAID());
		msg.addReceiver(new AID(agentName,false));

		Message message = new Message(((Adventurer)this.myAgent));
		//SerializableComplexeGraph<String, MapAttribute> sg=((Adventurer)this.myAgent).getMyMap().getSerializableGraph();
		try {					
			msg.setContentObject((Serializable) message);
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
