package eu.su.mas.dedaleEtu.mas.behaviours.FSM;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.Adventurer;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.behaviours.Behaviour;

import java.util.List;

public class FSMCollect extends Behaviour {

	private static final long serialVersionUID = 3124181066200035014L;

	private boolean finished = false;

    private Adventurer myAdventurer;
	private AbstractDedaleAgent myAbstractAgent;

	//exitValues
	private final int DEFAULT = 0;

	/**
	 * Current knowledge of the agent regarding the environment
	 */
	private int exitValue = DEFAULT;

	public FSMCollect(Adventurer myagent) {
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
		//System.out.println(this.myAgent.getLocalName()+" in "+this.getBehaviourName()+" Stade");

		//get role
		Observation role = myAdventurer.getRole();
		

		String myPosition=myAbstractAgent.getCurrentPosition();
		if (myPosition!=null){
			//List of observable from the agent's current position
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=myAbstractAgent.observe();//myPosition

			List<Couple<Observation,Integer>> lObservations = lobs.get(0).getRight();
			//System.out.println(this.myAgent.getLocalName()+" -- list of observables: "+lObservations);

			for(Couple<Observation,Integer> o:lObservations) {
				switch (o.getLeft()) {
					case DIAMOND:case GOLD:
						if ((o.getLeft() == role || role == Observation.ANY_TREASURE) && o.getRight()>0) {
							//System.out.println(this.myAgent.getLocalName() + " - My treasure type is : " + myAbstractAgent.getMyTreasureType());
							//System.out.println(this.myAgent.getLocalName() + " - My treasure type is : " + myAdventurer.getRole());
							//System.out.println(this.myAgent.getLocalName() + " - My current backpack capacity is:" + myAbstractAgent.getBackPackFreeSpace());
							//System.out.println(this.myAgent.getLocalName() + " - Value of the treasure on the current position: " + o.getLeft() + ": " + o.getRight());
							System.out.println(this.myAgent.getLocalName() + " - I try to open the safe: " + myAbstractAgent.openLock(o.getLeft()));
							int before = o.getRight();
							int pickUp = myAbstractAgent.pick();
							myAdventurer.setCollectedAmount(myAdventurer.getCollectedAmount() + pickUp);
							myAdventurer.getMyMap().getTreasureCollection().updateTreasure(myPosition, before - pickUp);
							System.out.println(this.myAgent.getLocalName() + " - The agent grabbed : " + pickUp);
//							myAdventurer.setRole(o.getLeft());
							//System.out.println(this.myAgent.getLocalName() + " - the remaining backpack capacity is: " + myAbstractAgent.getBackPackFreeSpace());
						}
						break;
					default:
						break;
				}
			}
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
