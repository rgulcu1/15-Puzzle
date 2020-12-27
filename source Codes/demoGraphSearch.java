import java.util.Objects;

public class demoGraphSearch {

    public static void main(String[] args) {

        Integer totalExpand = 0;
        Integer totalStoredNodes = 0;
        Integer[] solution;
        Integer solutionCounter = 0;

        final Integer[][] inialState = new Integer[Constants.ROW_SIZE][Constants.COLUMN_SIZE];
        GraphSearch.copyState(Constants.FINAL_STATE,inialState);

        for (int i = 0; i <10 ; i++) {
            GraphSearch.copyState(Constants.FINAL_STATE,inialState);
            StateMixer.mixState(inialState,Constants.DEPTH);

            System.out.println("\n------------------------------- " +(i+1)+"th RUN--------------------------\n");

            //solution = GraphSearch.graphSearch(inialState,Constants.Strategy.UCS , Constants.Strategy.HEURISTIC_1);
            //solution = GraphSearch.graphSearch(inialState,Constants.Strategy.A_STAR , Constants.Strategy.HEURISTIC_1);
             //solution = GraphSearch.graphSearch(Constants.INITIAL_STATE,Constants.Strategy.A_STAR , Constants.Strategy.HEURISTIC_2);
            solution = GraphSearch.graphSearch(inialState,Constants.Strategy.A_STAR , Constants.Strategy.HEURISTIC_3);

            if(Objects.isNull(solution)) continue;
            totalExpand+=solution[0];
            totalStoredNodes+=solution[1];
            solutionCounter ++;
        }

        System.out.println("\n ********************************AVERAGE RESULTS*****************************\n");
        System.out.println("Average Number of Expanded nodes for depth " + Constants.DEPTH +": " + totalExpand/solutionCounter);
        System.out.println("Average Number of Stored nodes in memory for depth " + Constants.DEPTH +": " + totalStoredNodes/solutionCounter);
    }
}
