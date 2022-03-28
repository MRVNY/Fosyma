package eu.su.mas.dedaleEtu.mas.behaviours.FSM;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.Adventurer;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class FSMWCM extends Behaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7532358182307391020L;

	private boolean finished = false;

	/**
	 * Current knowledge of the agent regarding the environment
	 */
	private int exitValue = 0;
	
	public FSMWCM(final AbstractDedaleAgent myagent) {
		super(myagent);

	}

	@Override
	public void action() {
		System.out.println(this.myAgent.getLocalName()+" in "+this.getBehaviourName()+" Stade");
		//System.out.println("Me as "+ this.getAgent().getName()+ " is going to sleep.");
		this.getAgent().doWait(1000);
		//System.out.println("Me as "+ this.getAgent().getName()+ " have awaken.");
		
		MessageTemplate msgTemplate=MessageTemplate.and(
				MessageTemplate.MatchProtocol("SENDMAP"),
				MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL));
		ACLMessage msgReceived=this.myAgent.receive(msgTemplate);
		
		if (msgReceived!=null) {
			SerializableSimpleGraph<String, MapAttribute> sgreceived=null;
			try {
				sgreceived = (SerializableSimpleGraph<String, MapAttribute>)msgReceived.getContentObject();
			} catch (UnreadableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			((Adventurer)this.myAgent).getMyMap().mergeMap(sgreceived);
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
