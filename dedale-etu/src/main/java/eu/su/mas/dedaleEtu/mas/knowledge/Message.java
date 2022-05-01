package eu.su.mas.dedaleEtu.mas.knowledge;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.Adventurer;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

public class Message implements Serializable {

    private SerializableComplexeGraph<String, MapRepresentation.MapAttribute> map;
    private int mode;
    private Observation role;
    private Couple<String,Integer> goal;
    private String nextNode;
    private String position;

    public Message(Adventurer agent){
        map = agent.getMyMap().getSerializableGraph();
        mode = agent.getMode();
        role = agent.getRole();
        goal = agent.getGoal();
        nextNode = agent.getNextNode();
        position = agent.getCurrentPosition();
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

    public Couple<String,Integer> getGoal(){
        return goal;
    }

    public String getNextNode() {
        return nextNode;
    }

    public String getPosition() {
        return position;
    }
}
