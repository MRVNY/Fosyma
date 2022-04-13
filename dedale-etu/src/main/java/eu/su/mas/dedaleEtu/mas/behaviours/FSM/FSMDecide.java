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
	public final static int DEFAULT = 0; //
	public final static int COLLECT = 1;

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

			//get role
			Observation role = ((AbstractDedaleAgent) this.myAgent).getMyTreasureType();

			//get bagSpace
			List<Couple<Observation, Integer>> bag = ((AbstractDedaleAgent) this.myAgent).getBackPackFreeSpace();
			int bagSpace = 0;
			if (role != Observation.ANY_TREASURE){
				for(Couple<Observation,Integer> o:bag) {
					if(o.getLeft() == role) bagSpace = o.getRight();
				}
			}

			//List of observable from the agent's current position
			List<Couple<String, List<Couple<Observation, Integer>>>> lobs = ((AbstractDedaleAgent) this.myAgent).observe();//myPosition
			List<Couple<Observation, Integer>> lObservations = lobs.get(0).getRight();

			//get value and Type of the Treasure
			//int value = 0;
			Observation type = Observation.ANY_TREASURE;
			for(Couple<Observation,Integer> o:lObservations) {
				if (o.getRight()!=null && o.getRight() > 0){
					//value = o.getRight();
					type = o.getLeft();
				}
			}

//			//get mode
//			int mode = ((Adventurer) this.myAgent).getMode();

			//get ratio
			float TRatio = 1/2;
			float ARatio = 1/2;


			//////////////Decision////////////////////

			//No role & TRatio_OK -> Collect
			if (role == Observation.ANY_TREASURE && TRatio >= 1/3){
				exitValue = COLLECT;
			}

			//Role_OK & ARatio_OK -> Collect
			if (role == type && ARatio >= 1/3){
				exitValue = COLLECT;
			}

			//BagFull -> SearchMode -> Check
			if (bagSpace==0 && role != Observation.ANY_TREASURE){
				((Adventurer)this.myAgent).setMode(Adventurer.SEARCH);
				exitValue = DEFAULT;
			}

			finished = true;
		}
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
