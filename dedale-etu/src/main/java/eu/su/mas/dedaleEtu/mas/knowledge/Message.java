package eu.su.mas.dedaleEtu.mas.knowledge;

import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.Adventurer;
import java.io.Serializable;

public class Message implements Serializable {

	private static final long serialVersionUID = 6747727532564253694L;
	private SerializableComplexeGraph<String, MapRepresentation.MapAttribute> map;
    private int mode;
    private String goal;
    private String nextNode;
    private String position;
    private String name;

    public Message(Adventurer agent){
        map = agent.getMyMap().getSerializableGraph();
        mode = agent.getMode();
        goal = agent.getGoal();
        nextNode = agent.getNextNode();
        position = agent.getCurrentPosition();
        name = agent.getLocalName();
    }

    public void setMap(SerializableComplexeGraph<String, MapRepresentation.MapAttribute> map) {
        this.map = map;
    }

    public SerializableComplexeGraph<String, MapRepresentation.MapAttribute> getMap(){
        return this.map;
    }

    public int getMode(){
        return mode;
    }

    public String getGoal(){
        return goal;
    }

    public String getNextNode() {
        return nextNode;
    }

    public String getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }
}
