package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;

public class EquityModule implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3800567495028222161L;
	
	private int totalCapacityDiamond;
	private int totalCapacityGold;
	private MapRepresentation map;
	private HashMap<String,List<Couple<Observation, Integer>>> agentCapacity;
	
	public EquityModule(MapRepresentation map) {
		this.map = map;
		agentCapacity = this.map.getCapacity();
		this.calculateCapacity();
	}
	
	private void calculateCapacity() {
		for(String agentName:agentCapacity.keySet()) {
			for(Couple<Observation,Integer> o:agentCapacity.get(agentName)){
				switch (o.getLeft()) {
				case DIAMOND:
					this.totalCapacityDiamond += o.getRight();
				case GOLD:
					this.totalCapacityGold += o.getRight();
				default:
					break; 
				}
			}
		}
	}
	
	public Observation getAttributedType() {
		return null;
		
	}
	
	public void rankType() {
		
	}
	
	
	
	
	
	

}
