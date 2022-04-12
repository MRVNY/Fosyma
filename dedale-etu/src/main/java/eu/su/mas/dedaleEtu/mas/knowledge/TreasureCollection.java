package eu.su.mas.dedaleEtu.mas.knowledge;

import java.util.ArrayList;

import eu.su.mas.dedaleEtu.mas.knowledge.Treasure.TypeTreasure;

public class TreasureCollection {
	
	private ArrayList<Treasure> listTreasure = new ArrayList<>();
	
	public void addTreasure(Treasure t) {
		listTreasure.add(t);
	}
	
	public void addTreasures(ArrayList<Treasure> l) {
		listTreasure.addAll(l);
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
	
	public String toString() {
		StringBuilder res = new StringBuilder();
		return res.toString();
	}
}
