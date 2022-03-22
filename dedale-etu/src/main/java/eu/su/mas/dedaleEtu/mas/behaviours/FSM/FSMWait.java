package eu.su.mas.dedaleEtu.mas.behaviours.FSM;

import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.Adventurer;
import jade.core.behaviours.Behaviour;

public class FSMWait extends Behaviour {


	/**
	 * 
	 */
	private static final long serialVersionUID = 9220246796976927693L;

	private boolean finished = false;

	/**
	 * Current knowledge of the agent regarding the environment
	 */
	private int exitValue = 0;
	
	public FSMWait(final Adventurer myagent) {
		super(myagent);

	}

	@Override
	public void action() {
		System.out.println("I'm in "+this.getBehaviourName()+" Stade");
		
		//System.out.println("Me as "+ this.getAgent().getName()+ " is going to sleep.");
		//l'agent s'endord la durée reste a détermier
		this.getAgent().doWait(1000);
		//System.out.println("Me as "+ this.getAgent().getName()+ " have awaken.");
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
