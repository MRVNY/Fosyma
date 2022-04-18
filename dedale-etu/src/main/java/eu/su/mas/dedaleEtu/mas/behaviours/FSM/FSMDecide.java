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
		finished = false;
		exitValue = DEFAULT;
		//System.out.println(this.myAgent.getLocalName() + " in " + this.getBehaviourName() + " Stade");
		String myPosition = ((AbstractDedaleAgent) this.myAgent).getCurrentPosition();

		if (myPosition != null) {

			//get role
			//Observation role = ((AbstractDedaleAgent) this.myAgent).getMyTreasureType();
			Observation role = ((Adventurer)this.myAgent).getRole();

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
			int value = 0;
			Observation type = Observation.ANY_TREASURE;
			for(Couple<Observation,Integer> o:lObservations) {
				if (o.getRight()!=null && o.getRight() > 0){
					value = o.getRight();
					type = o.getLeft();
				}
			}

//			//get mode
			int mode = ((Adventurer) this.myAgent).getMode();

			//get ratio
			float TRatio = 1/2;
			float ARatio = 1/2;


			//////////////Decision////////////////////

			if(!finished) {
				if (mode == Adventurer.LOCATE && role == Observation.ANY_TREASURE) {
					((Adventurer)this.myAgent).setRole(type); //Temporary buy fix
					finished = true;
				}
			}

			//BagFull -> SearchMode -> Check
			if (!finished && bagSpace==0 && role != Observation.ANY_TREASURE && mode!=Adventurer.SEARCH){
				//System.out.println(this.myAgent.getLocalName()+" passes to SEARCH");
				((Adventurer)this.myAgent).setMode(Adventurer.SEARCH);
				finished = true;
			}

			//Can't verify role cuz bug not fixed by prof
			//No role & TRatio_OK -> Collect
			if (!finished && value>0 && role == Observation.ANY_TREASURE && TRatio >= 1/3){
				//System.out.println(this.myAgent.getLocalName() + " decides to collect and got a role");
				exitValue = COLLECT;
				finished = true;
			}

			//Role_OK & ARatio_OK -> Collect
			if ( !finished && value>0 && role == type && ARatio >= 1/3){
				//System.out.println(this.myAgent.getLocalName() + " decides to collect");
				exitValue = COLLECT;
				finished = true;
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
