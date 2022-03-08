package eu.su.mas.dedaleEtu.mas.behaviours.lim;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.behaviours.Behaviour;

public class Reply extends Behaviour {
	
	private boolean finished = false;

	/**
	 * Current knowledge of the agent regarding the environment
	 */
	private MapRepresentation myMap;
	private AbstractDedaleAgent otheragent;
	
	public Reply(final AbstractDedaleAgent myagent, MapRepresentation myMap,AbstractDedaleAgent otheragent) {
		super(myagent);
		this.myMap=myMap;// the map should now be only the part that the other agent miss.2 
		this.otheragent = otheragent;
		
	}

	@Override
	public void action() {
		
		finished = true;
	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return finished;
	}

}
