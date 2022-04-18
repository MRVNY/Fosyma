package eu.su.mas.dedaleEtu.mas.behaviours.FSM;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.Adventurer;
import jade.core.behaviours.Behaviour;

import java.util.List;

public class FSMCollect extends Behaviour {

	/**
	 *
	 */
	private static final long serialVersionUID = 3124181066200035014L;

	private boolean finished = false;

	//exitValues
	private final int DEFAULT = 0;

	/**
	 * Current knowledge of the agent regarding the environment
	 */
	private int exitValue = DEFAULT;

	public FSMCollect(final Adventurer myagent) {
		super(myagent);
	}

	@Override
	public void action() {
		finished = false;
		//System.out.println(this.myAgent.getLocalName()+" in "+this.getBehaviourName()+" Stade");

		//get role
		//Observation role = ((AbstractDedaleAgent) this.myAgent).getMyTreasureType();
		Observation role = ((Adventurer)this.myAgent).getRole();

		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		if (myPosition!=null){
			//List of observable from the agent's current position
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition

			List<Couple<Observation,Integer>> lObservations = lobs.get(0).getRight();
			//System.out.println(this.myAgent.getLocalName()+" -- list of observables: "+lObservations);
			/*
			for(Couple<Observation,Integer> o:lObservations) {
				switch (o.getLeft()) {
					case DIAMOND:case GOLD:
						if ((o.getLeft() == role || role == Observation.ANY_TREASURE) && o.getRight()>0) {
							System.out.println(this.myAgent.getLocalName() + " - My treasure type is : " + ((AbstractDedaleAgent) this.myAgent).getMyTreasureType());
							System.out.println(this.myAgent.getLocalName() + " - My current backpack capacity is:" + ((AbstractDedaleAgent) this.myAgent).getBackPackFreeSpace());
							System.out.println(this.myAgent.getLocalName() + " - Value of the treasure on the current position: " + o.getLeft() + ": " + o.getRight());
							System.out.println(this.myAgent.getLocalName() + " - I try to open the safe: " + ((AbstractDedaleAgent) this.myAgent).openLock(o.getLeft()));
							System.out.println(this.myAgent.getLocalName() + " - The agent grabbed : " + ((AbstractDedaleAgent) this.myAgent).pick());
							((Adventurer)this.myAgent).setRole(o.getLeft());
							System.out.println(this.myAgent.getLocalName() + " - the remaining backpack capacity is: " + ((AbstractDedaleAgent) this.myAgent).getBackPackFreeSpace());
						}
						break;
					default:
						break;
				}
			}
			*/
		}

		finished = true;
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
