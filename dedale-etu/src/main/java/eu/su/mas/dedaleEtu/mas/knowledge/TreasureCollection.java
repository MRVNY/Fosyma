package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import eu.su.mas.dedaleEtu.mas.knowledge.Treasure.TypeTreasure;

public class TreasureCollection implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7383476290092058458L;
	private ArrayList<Treasure> listTreasure = new ArrayList<>();
	
	public void addTreasure(Treasure t){
		/*
		if(!this.listTreasure.contains(t)) {
			listTreasure.add(t);
		}
		*/
		if(this.getAllLocation(t.getType()).contains(t.getLocation())) {
			Treasure tr = this.getTreasure(t.getLocation());
			if(tr.getLocation() == t.getLocation()) {
				if(tr.getTreasureAmount() < t.getTreasureAmount()) {
					tr.setTreasureAmount(t.getTreasureAmount());
				}
			}
		}
		else {
			listTreasure.add(t);
		}
	}
	
	public void addTreasures(ArrayList<Treasure> l) throws Exception {
		for(Treasure t: l) {
			addTreasure(t);
		}
	}

	public Treasure removeTreasure(Treasure t) {
		if (listTreasure.remove(t)) {
			return t;
		}
		return null;
	}
	
	public int countGold() {
		int res = 0;
		for(Treasure t: listTreasure) {
			if(t.getType() == TypeTreasure.GOLD) {
				res += t.getTreasureAmount();
			}
		}
		return res;
	}
	
	public int countDiamond() {
		int res = 0;
		for(Treasure t: listTreasure) {
			if(t.getType() == TypeTreasure.DIAMOND) {
				res += t.getTreasureAmount();
			}
		}
		return res;
	}
	
	public String toString() {
		StringBuilder res = new StringBuilder();
		res.append("[");
		for(Treasure t: listTreasure) {
			res.append(t.toString());
			res.append("\t");
		}
		res.append("]");
		return res.toString();
	}
	
	public List<String> getAllLocation(TypeTreasure type){
		List<String> res = new ArrayList<>();
		for(Treasure t: listTreasure) {
			if(t.getType() == type) {
				res.add(t.getLocation());
			}
		}
		return res;
	}
	
	public List<String> getAllLocation(){
		List<String> res = new ArrayList<>();
		for(Treasure t: listTreasure) {
			
			res.add(t.getLocation());
		}
		return res;
	}
	
	public Treasure getMostValueable(TypeTreasure type) {
		Treasure res = null;
		int max = 0;
		for(Treasure t: listTreasure) {
			if(t.getTreasureAmount() > max) {
				res = t;
				max = t.getTreasureAmount();
			}
		}
		return res;
	}
	
	public boolean isIn(Treasure t) {
		return this.listTreasure.contains(t);
	}
	
	public boolean isEmpty() {
		return this.listTreasure.isEmpty();
	}
	
	public List<Integer> getAllValue(TypeTreasure type){
		List<Integer> res = new ArrayList<>();
		for(Treasure t: listTreasure) {
			if(t.getType() == type) {
				res.add(t.getTreasureAmount());
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
			if(t.getLocation() == location) {
				return t;
			}
		}
		return null;
	}
	
	public void mergeTreasure(TreasureCollection tc) {
		boolean alreadyIn = false;
		
	}
	
}
