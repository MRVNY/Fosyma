package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;

import eu.su.mas.dedale.env.Observation;

public class Treasure implements Serializable{
		
	/*
	public enum TypeTreasure {	
		GOLD,DIAMOND;

	}
	*/
	
	private int treasureAmount;
	// Id of the Node
	private String location;
	private Observation type;
	
	public Treasure(int treasureAmount, String Location, Observation type) {
		this.treasureAmount = treasureAmount;
		this.location = location;
		this.type = type;
	}

	public int getTreasureAmount() {
		return treasureAmount;
	}

	public String getLocation() {
		return location;
	}

	public Observation getType() {
		return type;
	}
	
	public String toString() {
		return "("+this.type+";"+this.treasureAmount+")";
	}

	public void setTreasureAmount(int treasureAmount) {
		this.treasureAmount = treasureAmount;
	}
	

}
