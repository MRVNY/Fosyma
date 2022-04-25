package eu.su.mas.dedaleEtu.mas.behaviours.FSM;

import java.io.IOException;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.Adventurer;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.knowledge.SerializableComplexeGraph;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class FSMSendEnd extends Behaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1627005025862577801L;

	private boolean finished = false;

	/**
	 * Current knowledge of the agent regarding the environment
	 */
	private int exitValue = 0;
	
	public FSMSendEnd(final AbstractDedaleAgent myagent) {
		super(myagent);

	}

	@Override
	public void action() {
		finished = false;
		
		//System.out.println(this.myAgent.getLocalName()+" in "+this.getBehaviourName()+" Stade");
		sendPieceMap(((Adventurer)this.myAgent).getCorresponder());
		finished = true;

	}
	
	private void sendPieceMap(String agentName) {
		ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
		msg.setProtocol("END");
		msg.setSender(this.myAgent.getAID());
		msg.addReceiver(new AID(agentName,false));
		
		SerializableComplexeGraph<String, MapAttribute> sg=((Adventurer)this.myAgent).getMyMap().getSerializableGraph();
		try {					
			msg.setContentObject(sg);
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
