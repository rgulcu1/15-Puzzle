import java.util.Arrays;

public class Node  implements  Comparable<Node>{

    private  Integer[][] state;

    private Node parentNode;

    private Integer realCost;

    private Integer estimatedCost = 0;

    private String move;

    private String stateText;


    public Node(Integer[][] state, Node parentNode, Integer realCost,String move) {
        this.state = state;
        this.parentNode = parentNode;
        this.realCost = realCost;
        this.move = move;
        calculateStateText();
    }

    private void calculateStateText(){
        final StringBuilder sb = new StringBuilder();
        for(Integer[] s1 : state){
            sb.append(Arrays.toString(s1));
        }
        this.stateText = sb.toString();
    }

    public Integer[][] getState() {
        return state;
    }

    public Node getParentNode() {
        return parentNode;
    }

    public Integer getRealCost() {
        return realCost;
    }

    public Integer getEstimatedCost() {
        return estimatedCost;
    }

    public void setRealCost(Integer realCost) {
        this.realCost = realCost;
    }

    public void setEstimatedCost(Integer estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }

    public String getStateText() {
        return stateText;
    }

    @Override
    public int compareTo(Node o) {

        if(o.getRealCost() + o.getEstimatedCost() > this.getRealCost() + this.getEstimatedCost()) return -1;
        else if(o.getRealCost() + o.getEstimatedCost() < this.getRealCost() + this.getEstimatedCost()) return 1;
        else return 0;
    }

    @Override
    public String toString() {
        Integer totalCost = getRealCost()+getEstimatedCost();
        return ""+totalCost;
    }


}