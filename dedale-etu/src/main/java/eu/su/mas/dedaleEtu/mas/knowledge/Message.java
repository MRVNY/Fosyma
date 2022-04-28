package eu.su.mas.dedaleEtu.mas.knowledge;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.Adventurer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Message implements Serializable {

    private SerializableComplexeGraph<String, MapRepresentation.MapAttribute> map;
    private int mode;
    private Observation role;
    private List<Couple<String,Integer>> goals;

    public Message(Adventurer agent){
        map = agent.getMyMap().getSerializableGraph();
        mode = agent.getMode();
        role = agent.getRole();
        goals = agent.getGoals();
    }

    public void setMap(SerializableComplexeGraph<String, MapRepresentation.MapAttribute> map) {
        this.map = map;
    }

    public SerializableComplexeGraph<String, MapRepresentation.MapAttribute> getMap(){
        return this.map;
    }

    public Observation getRole(){
        return role;
    }

    public int getMode(){
        return mode;
    }

    public List<Couple<String,Integer>> getGoals(){
        return goals;
    }


}
