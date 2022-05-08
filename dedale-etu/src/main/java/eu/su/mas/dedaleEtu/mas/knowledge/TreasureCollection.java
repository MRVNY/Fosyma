package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import jade.util.leap.Collection;

public class TreasureCollection implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 7383476290092058458L;
	private ArrayList<Treasure> listTreasure = new ArrayList<>();

	public int allGold = 0;
	public int allDiamond = 0;

	private ArrayList<String> seenNodes = new  ArrayList<>();




	/***
	 * Add a treasure to the knowledge or update the value to the lowest possible between the two possibility ( we assume that a lower value is the Wumpus fault )
	 * @param t : a Treasure
	 */
	public void addTreasure(Treasure t){
		/*
		if(!this.listTreasure.contains(t)) {
			listTreasure.add(t);
		}
		*/
		//no value existing yet
		if(this.isEmpty()) {
			this.listTreasure.add(t);
//			this.addAllValueTreasure(t);
			return;
		}
		//updating the value
		if(this.getAllLocation(t.getType()).contains(t.getLocation())) {
//			System.out.println(this.getAllLocation(t.getType()) + "treasure"+ t);
			Treasure tr = this.getTreasure(t.getLocation());
			if(tr.getLocation() == t.getLocation()) {
				if(tr.getTreasureAmount() < t.getTreasureAmount()) {
					tr.setTreasureAmount(t.getTreasureAmount());
				}
			}
		}
		//regular addition
		else {
//			System.out.println(this.getAllLocation(t.getType()) + "treasure"+ t);
			this.listTreasure.add(t);
//			this.addAllValueTreasure(t);
		}

		if(!this.seenNodes.contains(t.getLocation())) {
			this.seenNodes.add(t.getLocation());
			this.addAllValueTreasure(t);
		}
	}

	/***
	 * Adding multiple treasure at once
	 * @param l: a collection of treasure
	 */
	public void addTreasures(ArrayList<Treasure> l) {
		for(Treasure t: l) {
			addTreasure(t);
		}

	}

	/***
	 * remove a treasure of the knowledge if present
	 * @param t: the treasure to remove
	 * @return the removed treasure or null if not found
	 */
	public Treasure removeTreasure(Treasure t) {
		if (listTreasure.remove(t)) {
			return t;
		}
		return null;
	}

	/***
	 * remove a treasure of the knowledge if present
	 * @param location: the location of the treasure
	 * @return the removed treasure or null if not found
	 */
	public Treasure removeTreasure(String location) {
		for(Treasure t: listTreasure) {
			if(t.getLocation().equals(location)) {
				this.removeTreasure(t);
				return t;
			}
		}
		return null;
	}

	/***
	 * Updating the value of a specific treasure
	 * @param location: location of the treasure
	 * @param value: the new value of the treasure
	 */
	public void updateTreasure(String location, int value) {
		Treasure res = null;
		for(Treasure t: listTreasure) {
			if(t.getLocation().equals(location)) {
				res = t;
				break;
			}
		}
		if(res == null) {
			return;
		}
//		if(value == 0) {
//			this.removeTreasure(res);
//		}
		else {
			res.setTreasureAmount(value);
		}
	}

	/***
	 * Count the global value of the Gold currently in the knowledge
	 * @return global value of the Gold currently in the knowledge
	 */
	public int countGold() {
		int res = 0;
		for(Treasure t: listTreasure) {
			if(t.getType().equals(Observation.GOLD)) {
				res += t.getTreasureAmount();
			}
		}
		return res;
	}

	/***
	 * Count the global value of the Diamond currently in the knowledge
	 * @return global value of the Diamond currently in the knowledge
	 */
	public int countDiamond() {
		int res = 0;
		for(Treasure t: listTreasure) {
			if(t.getType().equals(Observation.DIAMOND)) {
				res += t.getTreasureAmount();
			}
		}
		return res;
	}

	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		res.append("[");
		for(Treasure t: this.listTreasure) {
			res.append(t.toString());
			res.append(";");
		}
		res.deleteCharAt(res.length()-1);
		res.append("]");
		return res.toString();
	}

	/***
	 * Give access to all the location of the knowledge for a given type
	 * @param type: Gold or Diamond
	 * @return all the location of the knowledge for the type
	 */
	public List<String> getAllLocation(Observation type){
		List<String> res = new ArrayList<>();
		for(Treasure t: this.listTreasure) {
			if(t.getType().equals(type)) {
				res.add(t.getLocation());
			}
		}
		return res;
	}

	/***
	 * Give access to all the location of the knowledge
	 * @return all the location of the knowledge
	 */
	public List<String> getAllLocation(){
		List<String> res = new ArrayList<>();
		for(Treasure t: this.listTreasure) {
			res.add(t.getLocation());
		}
		return res;
	}

	/***
	 * Give the treasure with the most value among a certain type
	 * @param type:  Gold or Diamond
	 * @return the treasure with the most value
	 */
	public Treasure getMostValueable(Observation type) {
		Treasure res = null;
		int max = 0;
		for(Treasure t: this.listTreasure) {
			if(t.getTreasureAmount() > max) {
				res = t;
				max = t.getTreasureAmount();
			}
		}
		return res;
	}

	/***
	 *
	 * @param t:
	 * @return True if the treasure is already knew, false if not
	 */
	public boolean isIn(Treasure t) {
		return this.listTreasure.contains(t);
	}
	/***
	 *
	 * @return true if the treasure collection is empty, false otherwise
	 */
	public boolean isEmpty() {
		return this.listTreasure.isEmpty();
	}

	/***
	 * Look over the treasure collection to get the total value of a certain type of treasure
	 * @param type : the type of treasure that we want to see
	 * @return
	 */
	public List<Integer> getAllValue(Observation type){
		List<Integer> res = new ArrayList<>();
		for(Treasure t: listTreasure) {
			if(t.getType().equals(type)) {
				res.add(t.getTreasureAmount());
			}
		}
		return res;
	}

    public List<String> getAllTreasure() {
        return listTreasure.stream().map(o -> o.getLocation()).collect(Collectors.toList());
    }

    public List<Couple<String,Integer>> getAllValueDif(Observation type, int value){
		List<Couple<String,Integer>> res = new ArrayList<>();
		for(Treasure t: listTreasure) {
			if(t.getTreasureAmount() == 0) {
				continue;
			}
			if(t.getType().equals(type)) {
                Couple<String,Integer> cp = new Couple<String,Integer>(t.getLocation(),Math.abs(value - t.getTreasureAmount()));
				res.add(cp);
			}
		}
		return res;
	}
	
	public Treasure getTreasure(int value) {
		for(Treasure t:this.listTreasure) {
			if(t.getTreasureAmount() == value) {
				return t;
			}
		}
		return null;
	}
	
	public Treasure getTreasure(String location) {
		for(Treasure t:this.listTreasure) {
			if(t.getLocation().equals(location)) {
				return t;
			}
		}
		return null;
	}
	
	public void mergeTreasure(TreasureCollection tc) {
		this.addTreasures(tc.getTreasures());
	}
	
	public TreasureCollection getMissingPart(TreasureCollection tc) {
		TreasureCollection res = new TreasureCollection();
		for(Treasure ti:this.listTreasure) {
			boolean notAlreadyIn = true;
			for(Treasure tj:tc.listTreasure) {
				if(ti.getLocation().equals(tj.getLocation())) {
					notAlreadyIn = false;
					break;
				}
			}
			if(notAlreadyIn) {
				res.addTreasure(ti);
			}
		}
		
		return res;
	}
	
	public void addAllValueTreasure(Treasure t) {
		if(t.getType().equals(Observation.GOLD)) {
			allGold += t.getTreasureAmount(); 
		}
		if(t.getType().equals(Observation.DIAMOND)){
			allDiamond += t.getTreasureAmount(); 
		}
	}
	
	public ArrayList<Treasure> getTreasures(){
		return this.listTreasure;
	}
	
	
}
