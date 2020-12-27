import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class StateMixer {

    public static void mixState(Integer[][] goalState, Integer depth) {

        final ArrayList<Integer[][]> states = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < depth; i++) {
            final Integer[][] tempState = new Integer[Constants.ROW_SIZE][Constants.COLUMN_SIZE];
            GraphSearch.copyState(goalState, tempState);
            final Integer[][] copytempState = new Integer[Constants.ROW_SIZE][Constants.COLUMN_SIZE];
            GraphSearch.copyState(tempState, copytempState);
            states.add(copytempState);

            final Integer[] emptyTileCoorddinate = GraphSearch.detectTileCoordinate(goalState,0);
            final ArrayList<String> allowableMoves = GraphSearch.getAllowableMoves(emptyTileCoorddinate);

            while (true) {
                String randomMovement = allowableMoves.get(rand.nextInt(allowableMoves.size()));
                GraphSearch.moveState(tempState, randomMovement);
                Boolean isCircle = false;
                for (int j = 0; j < states.size(); j++) {
                    if (GraphSearch.stateCompare(calculateStateText(tempState), calculateStateText(states.get(j)))) {
                        isCircle = true;
                        break;
                    }
                }

                if (isCircle) {
                    allowableMoves.remove(randomMovement);
                    GraphSearch.copyState(goalState, tempState);
                    continue;
                }
                break;
            }
            GraphSearch.copyState(tempState,goalState);
        }

    }

    private static String calculateStateText(Integer[][] state){
        final StringBuilder sb = new StringBuilder();
        for(Integer[] s1 : state){
            sb.append(Arrays.toString(s1));
        }
       return sb.toString();
    }
}
