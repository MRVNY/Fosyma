package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	private ArrayList<String> rankGold = new ArrayList<>();
	private ArrayList<String> rankDiam = new ArrayList<>();
	
	private HashMap<String,Integer> capG = new HashMap<>();
	private HashMap<String,Integer> capD = new HashMap<>();
	
	private Observation type;
	private Integer seekingValue;

	private String name;
	
	public EquityModule(MapRepresentation map,String name) {
		this.name = name;
		this.map = map;
		agentCapacity = this.map.getCapacity();
		this.calculateCapacity();
		//System.out.println("cap Diams :"+this.totalCapacityDiamond +", cap Gold :"+ this.totalCapacityGold);
		this.rankType();
		//System.out.println("rank Diams :"+this.capD +", rank Gold :"+ this.capG);
		this.attributedType();
		//System.out.println(this.name+"--->"+this.type);
	}
	
	
	public int getSeekingValue() {
		return this.seekingValue.intValue();
	}
	
	public Observation getType() {
		return type;
	}
	
	private void calculateCapacity() {
		for(String agentName:agentCapacity.keySet()) {
			for(Couple<Observation,Integer> o:agentCapacity.get(agentName)){
				switch (o.getLeft()) {
				case DIAMOND:
					this.totalCapacityDiamond += o.getRight();
					break;
				case GOLD:
					this.totalCapacityGold += o.getRight();
					break;
				default:
					break; 
				}
			}
		}
	}
	
	public void attributedType() {
//		int goldOverflow = map.getTreasureCollection().allGold - this.totalCapacityGold;
//		int diamOverflow = map.getTreasureCollection().allDiamond - this.totalCapacityDiamond;
//        
//		if(goldOverflow <= 0 && diamOverflow <= 0) {
//			int overflow =   map.getTreasureCollection().allDiamond;
//	        while (overflow <= 0) {
//	        	String best = this.bestAgentFor(capD);
//	        	overflow -= capD.get(best); 
//	        	capD.remove(best);
//	        }
//		}
//		if(goldOverflow > 0 && diamOverflow > 0) {
//			
//		}
//		if(goldOverflow < 0 && diamOverflow > 0) {
//			
//		}
//		if(goldOverflow > 0 && diamOverflow < 0) {
//			
//		}
		HashMap<String,Integer> copyG = this.copyCap(capG);
		HashMap<String,Integer> copyD = this.copyCap(capD);
		boolean bi = true;
		while(!copyG.isEmpty()) {
			if(bi) {
				bi = false;
				String best = this.bestAgentFor(copyG);
				copyG.remove(best);
				copyD.remove(best);
				this.rankGold.add(best);
			}
			else {
				bi = true;
				String best = this.bestAgentFor(copyD);
				copyG.remove(best);
				copyD.remove(best);
				this.rankDiam.add(best);
			}
		}
		if(this.rankGold.contains(this.name)) {
			this.type = Observation.GOLD;
			this.seekingValue = map.getTreasureCollection().allGold / this.rankGold.size();
		}
		else {
			this.type = Observation.DIAMOND;
			this.seekingValue = map.getTreasureCollection().allDiamond / this.rankDiam.size();
		}
	}
	
	
	private void rankType() {
		
		for(String agentName:agentCapacity.keySet()) {
			for(Couple<Observation,Integer> o:agentCapacity.get(agentName)){
				switch (o.getLeft()) {
				case DIAMOND:
					capD.put(agentName, o.getRight());
					break;
				case GOLD:
					capG.put(agentName, o.getRight());
					break;
				}
			}
		}
		
		capG = sortValues(capG);
		capD = sortValues(capD);
		
		
		
	}
	
	private static HashMap sortValues(HashMap map) {   
		List list = new LinkedList(map.entrySet());  
		//Custom Comparator  
		Collections.sort(list, new Comparator()   
		{  
			public int compare(Object o1, Object o2) {  
				return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());  
			}
		});  
		//copying the sorted list in HashMap to preserve the iteration order  
		HashMap sortedHashMap = new LinkedHashMap();  
		for (Iterator it = list.iterator(); it.hasNext();) {  
			Map.Entry entry = (Map.Entry) it.next();  
			sortedHashMap.put(entry.getKey(), entry.getValue());  
		}   
		return sortedHashMap;  
	}  
	
	public Observation bestTypeForAgent(String name) {
		return (capD.get(name)>capG.get(name)) ? Observation.DIAMOND : Observation.GOLD;
	}
	
	private String bestAgentFor(HashMap<String,Integer> cap) {
		String bestAgent = null;
		int max = 0;
		for(String n: cap.keySet()) {
			if(cap.get(n) > max) {
				bestAgent = n;
				max = cap.get(n);
			}
		}
		return bestAgent;
	}
	
	private HashMap<String,Integer> copyCap(HashMap<String,Integer> cap) {
		HashMap<String,Integer> copy = new HashMap<>();
		for(String agentName:cap.keySet()) {
			copy.put(agentName, cap.get(agentName));
		}
		return copy;
	}
	
	
	
}
