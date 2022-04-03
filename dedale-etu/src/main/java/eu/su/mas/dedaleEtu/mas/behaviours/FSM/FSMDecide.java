package eu.su.mas.dedaleEtu.mas.behaviours.FSM;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.Adventurer;
import jade.core.behaviours.Behaviour;

import java.util.List;

public class FSMDecide extends Behaviour {

	/**
	 *
	 */
	private static final long serialVersionUID = 3124181066200035014L;

	private boolean finished = false;

	//exitValues
	private static final int DEFAULT = 0;
	private static final int COLLECT = 1;

	/**
	 * Current knowledge of the agent regarding the environment
	 */
	private int exitValue = DEFAULT;

	public FSMDecide(final Adventurer myagent) {
		super(myagent);
	}

	@Override
	public void action() {
		exitValue = DEFAULT;
		System.out.println(this.myAgent.getLocalName() + " in " + this.getBehaviourName() + " Stade");

		String myPosition = ((AbstractDedaleAgent) this.myAgent).getCurrentPosition();
		if (myPosition != null) {
			//List of observable from the agent's current position
			List<Couple<String, List<Couple<Observation, Integer>>>> lobs = ((AbstractDedaleAgent) this.myAgent).observe();//myPosition

			List<Couple<Observation, Integer>> lObservations = lobs.get(0).getRight();
			//System.out.println(this.myAgent.getLocalName()+" -- list of observables: "+lObservations);

			//get mode
			int mode = ((Adventurer) this.myAgent).getMode();

			//get bag
			List<Couple<Observation, Integer>> bag = ((AbstractDedaleAgent) this.myAgent).getBackPackFreeSpace();

			//get role
			Observation role = ((AbstractDedaleAgent) this.myAgent).getMyTreasureType();

			//get ratio


			if(!bagFull(bag)) exitValue = COLLECT;

			finished = true;
		}
	}

	private boolean bagFull(List<Couple<Observation, Integer>> bag){
		boolean out = true;
		for (Couple<Observation, Integer> o : bag) {
			if(o.getRight()>0) out = false;
		}
		return out;
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
