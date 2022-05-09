package eu.su.mas.dedaleEtu.mas.agents.dummies.explo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;

import eu.su.mas.dedaleEtu.mas.behaviours.FSM.*;
import eu.su.mas.dedaleEtu.mas.knowledge.EquityModule;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.FSMBehaviour;


public class Adventurer extends AbstractDedaleAgent {

	private static final long serialVersionUID = -7969469610241668140L;
	private MapRepresentation myMap;

	private String corresponder;

	private List<String> priorities = new ArrayList<>();
	private String goal = null;

	//enum for mode (by priorities)
	public static final int EXPLORE = 1;
	public static final int LOCATE = 2;
	public static final int SEARCH = 0;
	private int mode;
	private Observation role = Observation.ANY_TREASURE;

	//Name for the FSM state

	private static final String Move = "Move";
	private static final String Ping = "Ping";
	private static final String Check = "Check";
	private static final String Pong = "Pong";
	private static final String End = "End";
	private static final String Decide = "Decide";
    private static final String Collect = "Collect";

    public int goldCap;
    public int diamCap;

    private int collectedAmount = 0;
    private int amountToCollect = 0;

    public EquityModule equity;

	protected void setup(){

		super.setup();

		mode = EXPLORE;

		//get the parameters added to the agent at creation (if any)
		final Object[] args = getArguments();


		List<String> list_agentNames = new ArrayList<String>();

		if(args.length==0){
			System.err.println("Error while creating the agent, names of agent to contact expected");
			System.exit(-1);
		}else{
			int i=2;// WARNING YOU SHOULD ALWAYS START AT 2. This will be corrected in the next release.
			while (i<args.length) {
				list_agentNames = (List<String>) args[i];
				i++;
			}
		}

		List<String> listName = new ArrayList<String>(list_agentNames.size());
		for(String name:list_agentNames) {
			listName.add(name);
		}
		listName.remove(this.getLocalName());
		System.out.println(listName);

		list_agentNames = listName;

		//this.listName = listName;

		//FSM implentation


		FSMBehaviour fsm = new FSMBehaviour(this);
		// Define the different states and behaviours
		fsm.registerFirstState (new FSMMove(this), Move);
		fsm.registerState (new FSMSendPings(this), Ping);
		fsm.registerState (new FSMCheck(this), Check);
		fsm.registerState (new FSMSendPong(this), Pong);
		fsm.registerState (new FSMSendEnd(this), End);
		fsm.registerState (new FSMDecide(this), Decide);
        fsm.registerState (new FSMCollect(this), Collect);

		// Register the transitions
		fsm.registerDefaultTransition (Move,Ping);
		fsm.registerDefaultTransition (Ping,Check);
		fsm.registerDefaultTransition (End,Check);
		fsm.registerDefaultTransition (Pong,Check);
		fsm.registerDefaultTransition (Collect,Ping);

		fsm.registerDefaultTransition (Check,Check);
        fsm.registerTransition (Check,Pong, FSMCheck.SEND_PONG);
        fsm.registerTransition (Check,End, FSMCheck.SEND_END);
        fsm.registerTransition (Check,Decide, FSMCheck.DECIDE);
        fsm.registerTransition (Check,Move, FSMCheck.TIMEOUT);

        fsm.registerDefaultTransition (Decide,Check);
        fsm.registerTransition (Decide,Collect, FSMDecide.COLLECT);


		DataStore dataFSM = new DataStore();
		dataFSM.put("agents",list_agentNames);
		fsm.setDataStore(dataFSM);

		for(Couple<Observation,Integer> o:this.getBackPackFreeSpace()){
			switch (o.getLeft()) {
			case DIAMOND:
				this.diamCap = o.getRight();
				break;
			case GOLD:
				this.goldCap = o.getRight();
				break;
			}
		}


		List<Behaviour> lb=new ArrayList<Behaviour>();

		/************************************************
		 *
		 * ADD the behaviours of the Dummy Moving Agent
		 *
		 ************************************************/



		lb.add(fsm);


		/***
		 * MANDATORY TO ALLOW YOUR AGENT TO BE DEPLOYED CORRECTLY
		 */

		addBehaviour(new startMyBehaviours(this,lb));

		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}

	public MapRepresentation getMyMap() {
		return myMap;
	}

	public void setMyMap(MapRepresentation myMap) {
		this.myMap = myMap;
	}

	public String getCorresponder() {
		return corresponder;
	}

	public void setCorresponder(String corresponder) {
		this.corresponder = corresponder;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public Observation getRole() {
		return role;
	}

	public void setRole(Observation role) {
		this.role = role;
	}

	public List<String> getPriorities(){
		updatePriorities();
		return priorities;
	}

	public String getNextNode(){
		String nextNode = null;
		String myPos = getCurrentPosition();

		if(goal != null && myPos != null) {
			try {
				nextNode = this.myMap.getShortestPathToGoal(myPos, goal);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return nextNode;
	}

    public String getNextNode(String myGoal) {
        String nextNode = null;
        String myPos = getCurrentPosition();

        if (myGoal != null && myPos != null) {
            try {
                nextNode = this.myMap.getShortestPathToGoal(myPos, myGoal);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return nextNode;
    }

    public void updatePriorities(){
        try {
            if (this.myMap != null && this.myMap.hasOpenNode() && mode==Adventurer.EXPLORE || mode==Adventurer.SEARCH){
                //For optimisation, we only update priorities if the list is empty or the map has changed
                //if(priorities==null || priorities.isEmpty() || !myMap.sameMap()){
                    priorities = this.myMap.getClosestOpenNodes(getCurrentPosition());
					if(new Random().nextFloat() < 1/2) Collections.shuffle(priorities);
                //}
            }
            else if (this.myMap != null && mode==Adventurer.LOCATE){
            	// we calculate the value which the agent need to seek at the moment
            	amountToCollect =  this.equity.getSeekingValue() - collectedAmount;
        
                // if the value is 0 or negative, the agent should now help the other to obtain their value
                // so we switch to Search mode
            	//System.out.println(this.getLocalName()+" -> seekingValue: " + this.equity.getSeekingValue() + "; amoutToCollect: " + amountToCollect + " type: " + this.getRole());
//                if(this.goal!= null) {
//                	System.out.println(this.goal);
//                }
            	
            	if(amountToCollect <= 0) {
                	System.out.println(this.getLocalName()+" passes to SEARCH.");
                	mode=Adventurer.SEARCH;
                }
                // Otherwise, we start seeking the treasure with the closest value that we seek
                else {
                	try {
                	//In order to diversify the collect and the treasure which the agent will seek
                	if(Math.random()>=0.5) {
//                		System.out.println("The value has change");
                		amountToCollect = (int) (Math.random() * (amountToCollect - 1 + 1) + 1);  
                	}
                	priorities = this.myMap.getClosestTreasuresOfClosestValue(getCurrentPosition(),role,amountToCollect);
                	}
                	catch(Exception e){
                		mode = Adventurer.SEARCH;
                		System.out.println(this.getLocalName()+" passes to SEARCH.");
                	}

                }
            }
            else if(mode==Adventurer.SEARCH){ //Go to random treasure
            	priorities = this.myMap.getAllNodes();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


	public void resetGoal() {
		updatePriorities();
		if(priorities!=null && !priorities.isEmpty()) goal = priorities.get(0);
	}

	public String getGoal() {
		return goal;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

	public int getCollectedAmount() {
		return collectedAmount;
	}

	public void setCollectedAmount(int collectedAmount) {
		this.collectedAmount = collectedAmount;
	}

    public int getAmountToCollect() {
        return amountToCollect;
    }

	public List<String> possibleNexts(){
		return observe().stream().map((ob -> ob.getLeft())).collect(Collectors.toList());
	}

	public boolean optimised(){
		return true;
	}

}
