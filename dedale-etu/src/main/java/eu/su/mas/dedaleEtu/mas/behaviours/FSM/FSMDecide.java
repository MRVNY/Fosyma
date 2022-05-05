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

	private Adventurer myAdventurer;
	private AbstractDedaleAgent myAbstractAgent;

	/**
	 * Current knowledge of the agent regarding the environment
	 */
	private int exitValue = DEFAULT;

	public FSMDecide(final Adventurer myagent) {
		super(myagent);
        myAdventurer = myagent;
		myAbstractAgent = myagent;
	}

	@Override
	public void action() {
		try {
			this.myAgent.doWait(100);
		} catch (Exception e) {
			e.printStackTrace();
		}
		finished = false;
		exitValue = DEFAULT;
		//System.out.println(this.myAgent.getLocalName() + " in " + this.getBehaviourName() + " Stade");
		String myPosition = myAbstractAgent.getCurrentPosition();

		if (myPosition != null) {

			//get role
			//Observation role = myAbstractAgent.getMyTreasureType();
			Observation role = myAdventurer.getRole();

			//get bagSpace
			List<Couple<Observation, Integer>> bag = myAbstractAgent.getBackPackFreeSpace();
			int bagSpace = 0;
			if (role != Observation.ANY_TREASURE){
				for(Couple<Observation,Integer> o:bag) {
					if(o.getLeft() == role) bagSpace = o.getRight();
				}
			}

			//List of observable from the agent's current position
			List<Couple<String, List<Couple<Observation, Integer>>>> lobs = myAbstractAgent.observe();//myPosition
			List<Couple<Observation, Integer>> lObservations = lobs.get(0).getRight();

			//get value and Type of the Treasure
			int value = 0;
			Observation type = Observation.ANY_TREASURE;
			for(Couple<Observation,Integer> o:lObservations) {
				switch (o.getLeft()) {
					case DIAMOND:case GOLD:
						value = o.getRight();
						type = o.getLeft();
						break;
				}
			}


			//////////////Decision////////////////////
            //We're sure the mode is already LOCATE

            // If the agent doesn't have a role well give him one (Deprecated by EquityModule)
			if(!finished) {
				if (role == Observation.ANY_TREASURE) {
					myAdventurer.setRole(type); //Temporary buy fix
					finished = true;
				}
			}

			//BagFull -> SearchMode -> Check
			if (!finished && bagSpace==0){
				if(role != Observation.ANY_TREASURE) {
					System.out.println(this.myAgent.getLocalName() + " passes to SEARCH");
					myAdventurer.setMode(Adventurer.SEARCH);
				}
				finished = true;
			}

			//Can't verify role cuz bug not fixed by prof
			//No role & TRatio_OK -> Collect
			// if (!finished && value>0 && role == Observation.ANY_TREASURE && TRatio >= 1/3){
			// 	//System.out.println(this.myAgent.getLocalName() + " decides to collect and got a role");
			// 	exitValue = COLLECT;
			// 	finished = true;
			// }

			//If I'm at by goal / the amount is perfect -> Collect
            int amountToCollect = myAdventurer.getAmountToCollect();
			Couple<String,Integer> goal = myAdventurer.getGoal();
			int canCollect = Math.min(bagSpace,value);

			if (!finished && goal!=null && role == type && (canCollect==amountToCollect || goal.getLeft().equals(myPosition))) {
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
