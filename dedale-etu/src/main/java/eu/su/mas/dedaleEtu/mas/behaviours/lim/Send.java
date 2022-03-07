package eu.su.mas.dedaleEtu.mas.behaviours.lim;

import java.io.IOException;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;


public class Send extends Behaviour {
	
	private static final long serialVersionUID = 8567689731496787661L;

	private boolean finished = false;

	/**
	 * Current knowledge of the agent regarding the environment
	 */
	private MapRepresentation myMap;
	
	public Send(final AbstractDedaleAgent myagent, MapRepresentation myMap) {
		super(myagent);
		this.myMap=myMap;
		
	}

	@Override
	public void action() {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setProtocol("SHARE-MAP");
		msg.setSender(this.myAgent.getAID());
		//We need to know which agents are around, this need to be an argument or with another method
		if (this.myAgent.getLocalName().equals("1stAgent")) {
			msg.addReceiver(new AID("2ndAgent",false));
		}else {
			msg.addReceiver(new AID("1stAgent",false));
		}
		SerializableSimpleGraph<String, MapAttribute> sg=this.myMap.getSerializableGraph();
		try {					
			msg.setContentObject(sg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
		
		this.myAgent.addBehaviour(new Receive((AbstractDedaleAgent)this.myAgent,this.myMap));
		
		finished = true;

	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return finished;
	}
	

}

