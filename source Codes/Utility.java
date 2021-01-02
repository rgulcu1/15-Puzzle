

import java.util.*;


public class Utility {


    public static Boolean stateCompare(String state1, String state2) {

        return state1.equals(state2);
    }

    public static Integer moveState(Integer[][] state, String direction) {
        final Integer[] emptyTileCoorddinate = detectTileCoordinate(state, 0);
        final Integer emptyTileRow = emptyTileCoorddinate[0];
        final Integer emptyTileColumn = emptyTileCoorddinate[1];
        Integer moveCost = 1;

        switch (direction) {

            case Constants.Moves.UP:
                swap(emptyTileRow, emptyTileColumn, emptyTileRow - 1, emptyTileColumn, state);
                break;
            case Constants.Moves.DOWN:
                swap(emptyTileRow, emptyTileColumn, emptyTileRow + 1, emptyTileColumn, state);
                break;
            case Constants.Moves.LEFT:
                swap(emptyTileRow, emptyTileColumn, emptyTileRow, emptyTileColumn - 1, state);
                break;
            case Constants.Moves.RIGHT:
                swap(emptyTileRow, emptyTileColumn, emptyTileRow, emptyTileColumn + 1, state);
                break;
            case Constants.Moves.UP_LEFT:
                swap(emptyTileRow, emptyTileColumn, emptyTileRow - 1, emptyTileColumn - 1, state);
                moveCost = 3;
                break;
            case Constants.Moves.UP_RIGHT:
                swap(emptyTileRow, emptyTileColumn, emptyTileRow - 1, emptyTileColumn + 1, state);
                moveCost = 3;
                break;
            case Constants.Moves.DOWN_LEFT:
                swap(emptyTileRow, emptyTileColumn, emptyTileRow + 1, emptyTileColumn - 1, state);
                moveCost = 3;
                break;
            case Constants.Moves.DOWN_RIGHT:
                swap(emptyTileRow, emptyTileColumn, emptyTileRow + 1, emptyTileColumn + 1, state);
                moveCost = 3;
                break;
        }
        return moveCost;
    }

    public static Integer[] detectTileCoordinate(Integer[][] state, Integer tile) {

        Integer rowIndex = 0;
        Integer columnIndex = 0;

        for (int i = 0; i < Constants.ROW_SIZE; i++) {
            for (int j = 0; j < Constants.COLUMN_SIZE; j++) {

                if (state[i][j] == tile) {
                    rowIndex = i;
                    columnIndex = j;
                }
            }
        }
        final Integer[] cooridnate = {rowIndex, columnIndex};
        return cooridnate;
    }

    public static ArrayList<String> getAllowableMoves(Integer[] emptyTileCoorddinate) {

        final ArrayList<String> allowableMoves = new ArrayList<String>(Arrays.asList(Constants.Moves.UP, Constants.Moves.DOWN, Constants.Moves.LEFT, Constants.Moves.RIGHT,
                Constants.Moves.UP_LEFT, Constants.Moves.UP_RIGHT, Constants.Moves.DOWN_LEFT, Constants.Moves.DOWN_RIGHT));

        final Integer emptyTileRow = emptyTileCoorddinate[0];
        final Integer emptyTileColumn = emptyTileCoorddinate[1];

        if (emptyTileRow == 0) {
            allowableMoves.remove(Constants.Moves.UP);
            allowableMoves.remove(Constants.Moves.UP_LEFT);
            allowableMoves.remove(Constants.Moves.UP_RIGHT);
        }

        if (emptyTileRow == Constants.ROW_SIZE - 1) {
            allowableMoves.remove(Constants.Moves.DOWN);
            allowableMoves.remove(Constants.Moves.DOWN_LEFT);
            allowableMoves.remove(Constants.Moves.DOWN_RIGHT);
        }

        if (emptyTileColumn == 0) {
            allowableMoves.remove(Constants.Moves.LEFT);
            allowableMoves.remove(Constants.Moves.UP_LEFT);
            allowableMoves.remove(Constants.Moves.DOWN_LEFT);
        }

        if (emptyTileColumn == Constants.COLUMN_SIZE - 1) {
            allowableMoves.remove(Constants.Moves.RIGHT);
            allowableMoves.remove(Constants.Moves.UP_RIGHT);
            allowableMoves.remove(Constants.Moves.DOWN_RIGHT);
        }

        return allowableMoves;
    }

    public static void copyState(Integer[][] currentState, Integer[][] copyState) {

        for (int i = 0; i < Constants.ROW_SIZE; i++) {
            for (int j = 0; j < Constants.COLUMN_SIZE; j++) {

                copyState[i][j] = currentState[i][j];
            }
        }
    }

    private static void swap(Integer i, Integer j, Integer si, Integer sj, Integer[][] state) {

        Integer temp = state[si][sj];

        state[si][sj] = state[i][j];
        state[i][j] = temp;
    }

    private static void printSate(Integer[][] state) {

        for (int i = 0; i < Constants.ROW_SIZE; i++) {
            for (int j = 0; j < Constants.COLUMN_SIZE; j++) {
                System.out.print(state[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static void printPath(Node goalNode) {

        final ArrayList<Node> path = new ArrayList<>();
        path.add(goalNode);

        Node currentNode = goalNode;
        while (true) {
            final Node parentNode = currentNode.getParentNode();
            if (Objects.isNull(parentNode)) break;
            path.add(parentNode);
            currentNode = parentNode;
        }
        Collections.reverse(path);

        System.out.println("******************* PATH *****************");

        for (int i = 0; i < path.size(); i++) {
            System.out.println();
            printSate(path.get(i).getState());

            if (i != path.size() - 1) {
                System.out.println("\t  |");
                System.out.println("\t  |");
                System.out.println("\t  V");
            }
        }
    }

    public static void printSolution(Node goalNode, int expandCounter, int maxStoredNode) {

        System.out.println("Cost of Solution : " + goalNode.getRealCost());
        printPath(goalNode);
        System.out.println("Number of Expanded Nodes : " + expandCounter);
        System.out.println("Maximum number of stored Node: " + maxStoredNode);
    }

}



