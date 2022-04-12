package eu.su.mas.dedaleEtu.mas.knowledge;

public class Treasure {
		
	
	public enum TypeTreasure {	
		GOLD,DIAMOND;

	}
	
	private int treasureAmount;
	// Id of the Node
	private String location;
	private TypeTreasure type;
	
	public Treasure(int treasureAmount, String Location, TypeTreasure type) {
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

	public TypeTreasure getType() {
		return type;
	}
	
	public String toString() {
		return "("+this.type+";"+this.treasureAmount+")";
	}

}
