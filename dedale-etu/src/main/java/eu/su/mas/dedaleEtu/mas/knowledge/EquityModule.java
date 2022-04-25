package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;

import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.Adventurer;

public class EquityModule implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3800567495028222161L;
	public Adventurer agent = null;
	public int ratio;
	
	public EquityModule(Adventurer agent) {
		this.agent = agent;
	}
	
	public int calculateRatio() {
		this.ratio = this.agent.getMyMap().getTreasureCollection().allDiamond;
		return ratio;
	}
	
	public String decisionType(){
		return null;
	}
	

}
