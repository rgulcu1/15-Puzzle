import java.util.*;

public class GraphSearch {

    private static Integer expandCounter = 0;

    private static Integer maxStoredNode = 0;

    private static Long startTime;

    public static Integer[] graphSearch(Integer[][] initialState, String strategy, String heuristicFuntion) {
        expandCounter = 0;
        startTime = System.currentTimeMillis();

        Node finalNode = null;
        switch (strategy) {
            case Constants.Strategy.UCS:
                finalNode = lengthLimitedSearch(initialState, strategy, heuristicFuntion, Integer.MAX_VALUE);
                break;
            case Constants.Strategy.ILS:
                Integer lengthLimit = 1;
                while (true) {
                    finalNode = lengthLimitedSearch(initialState, strategy, heuristicFuntion, lengthLimit);
                    if (!Objects.isNull(finalNode)) break;
                    lengthLimit++;
                }
                break;
            case Constants.Strategy.A_STAR:
                finalNode = lengthLimitedSearch(initialState, strategy, heuristicFuntion, Integer.MAX_VALUE);
                break;

        }
        if(Objects.isNull(finalNode)) return null;
        printSolution(finalNode);
        final Integer[] solutions = {expandCounter, maxStoredNode};
        return solutions;
    }

    private static Node lengthLimitedSearch(Integer[][] initialState, String strategy, String heuristicFuntion, Integer lengthLimit) {
        maxStoredNode = 0;

        Integer initialCost = 0;
        if (strategy.equals(Constants.Strategy.A_STAR)) {
            if (heuristicFuntion.equals(Constants.Strategy.HEURISTIC_1)) initialCost = heuristicFunction1(initialState);
            else if (heuristicFuntion.equals(Constants.Strategy.HEURISTIC_2)) initialCost = heuristicFunction2(initialState);
            else initialCost = heuristicFunction3(initialState);
        }

        final Node initialNode = new Node(initialState, null, 0, "");
        initialNode.setEstimatedCost(initialCost);
        final PriorityQueue<Node> frontierList = new PriorityQueue<>();
        Node currentNode = initialNode;
        final ArrayList<Node> exploredSet = new ArrayList<>();

        frontierList.add(initialNode);


        while (true) {
            if (checkTime(startTime)) {
                System.out.println("!!! The algorithm didnt find the solution for given period !!!");
                return null;
            }

            if (frontierList.isEmpty()) return null;

            currentNode = frontierList.poll();

            if (goalStateCheck(currentNode)) return currentNode;

            exploredSet.add(currentNode);
            expandNode(currentNode, frontierList, exploredSet, lengthLimit, strategy, heuristicFuntion);
            expandCounter++;
        }
    }

    private static Boolean goalStateCheck(Node currentNode) {

        final String currentState = currentNode.getStateText();

        return stateCompare(currentState, Constants.FINAL_STATE_TEXT);
    }

    public static Boolean stateCompare(String state1, String state2) {

        return state1.equals(state2);
    }

    private static Node move(Node currentNode, String direction) {

        final Integer[][] currentState = new Integer[Constants.ROW_SIZE][Constants.COLUMN_SIZE];
        copyState(currentNode.getState(), currentState);

        final Integer moveCost = moveState(currentState, direction);
        final Node newNode = new Node(currentState, currentNode, currentNode.getRealCost() + moveCost, direction);
        return newNode;
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

    private static void swap(Integer i, Integer j, Integer si, Integer sj, Integer[][] state) {

        Integer temp = state[si][sj];

        state[si][sj] = state[i][j];
        state[i][j] = temp;
    }

    private static void expandNode(Node currentNode, PriorityQueue<Node> frontierList, ArrayList<Node> exploredSet, Integer lenghLimit, String strategy, String heuristicFunction) {

        final Integer[][] currentState = currentNode.getState();
        final Integer[] emptyTileCoorddinate = detectTileCoordinate(currentState, 0);

        final ArrayList<String> allowableMoves = getAllowableMoves(emptyTileCoorddinate);
        allowableMoves.remove(getReverseDirection(currentNode.getMove()));


        for (int i = 0; i < allowableMoves.size(); i++) {

            final Node childNode = move(currentNode, allowableMoves.get(i));

            if (strategy.equals(Constants.Strategy.A_STAR)) {
                if (heuristicFunction.equals(Constants.Strategy.HEURISTIC_1))
                    childNode.setEstimatedCost(heuristicFunction1(childNode.getState()));
                else if (heuristicFunction.equals(Constants.Strategy.HEURISTIC_2))
                    childNode.setEstimatedCost(heuristicFunction2(childNode.getState()));
                else
                    childNode.setEstimatedCost(heuristicFunction3(childNode.getState()));
            }

            if (isExplored(childNode, exploredSet) || childNode.getRealCost() > lenghLimit || isInFrontiew(childNode, frontierList))
                continue;

            frontierList.add(childNode);
        }

        if (frontierList.size() > maxStoredNode) maxStoredNode = frontierList.size();
    }

    public static ArrayList<String> getAllowableMoves(Integer[] emptyTileCoorddinate) {

        final ArrayList<String> allowableMoves = new ArrayList<String>(List.of(Constants.Moves.UP, Constants.Moves.DOWN, Constants.Moves.LEFT, Constants.Moves.RIGHT,
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

    private static Boolean isExplored(Node childNode, ArrayList<Node> exploredSet) {

        final Integer exploredSetSize = exploredSet.size();
        for (int i = 0; i < exploredSetSize; i++) {

            final Node currentNode = exploredSet.get(i);

            if ((Objects.isNull(currentNode.getParentNode()) || stateCompare(currentNode.getParentNode().getStateText(), childNode.getParentNode().getStateText()))
                    && stateCompare(currentNode.getStateText(), childNode.getStateText()))
                return true;
        }
        return false;
    }

    private static Boolean isInFrontiew(Node childNode, PriorityQueue<Node> frontierList) {

        final Boolean[] flag = {false};
        frontierList.forEach(node -> {
            if (stateCompare(node.getParentNode().getStateText(), childNode.getParentNode().getStateText()) && stateCompare(node.getStateText(), childNode.getStateText())) {
                flag[0] = true;
            }
        });
        return flag[0];


    }

    private static void printFrontier(PriorityQueue<Node> frontierList) {

        final PriorityQueue<Node> copyfrontierList = new PriorityQueue<>(frontierList);

        while (!copyfrontierList.isEmpty()) {
            System.out.println(copyfrontierList.poll());
        }
    }

    public static void printSate(Integer[][] state) {

        for (int i = 0; i < Constants.ROW_SIZE; i++) {
            for (int j = 0; j < Constants.COLUMN_SIZE; j++) {
                System.out.print(state[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void copyState(Integer[][] currentState, Integer[][] copyState) {

        for (int i = 0; i < Constants.ROW_SIZE; i++) {
            for (int j = 0; j < Constants.COLUMN_SIZE; j++) {

                copyState[i][j] = currentState[i][j];
            }
        }
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

    private static void printSolution(Node goalNode) {

        System.out.println("Cost of Solution : " + goalNode.getRealCost());
        printPath(goalNode);
        System.out.println("Number of Expanded Nodes : " + expandCounter);
        System.out.println("Maximum number of stored Node: " + maxStoredNode);
    }

    private static Integer heuristicFunction1(Integer[][] state) {

        Integer missPlacedCounter = 0;
        for (int i = 0; i < Constants.ROW_SIZE; i++) {
            for (int j = 0; j < Constants.COLUMN_SIZE; j++) {
                if (state[i][j] != Constants.FINAL_STATE[i][j] && (i != 2 || j != 1)) {

                    missPlacedCounter++;
                }

            }
        }
        return missPlacedCounter;
    }

    private static Integer heuristicFunction2(Integer[][] state) {

        Integer totalCityBlockDistance = 0;
        for (int i = 0; i < Constants.ROW_SIZE; i++) {
            for (int j = 0; j < Constants.COLUMN_SIZE; j++) {

                if (i != 2 || j != 1) {
                    Integer[] goalCoordinate = detectTileCoordinate(state, Constants.FINAL_STATE[i][j]);
                    totalCityBlockDistance += (Math.abs(i - goalCoordinate[0]) + Math.abs(j - goalCoordinate[1]));
                }
            }
        }
        return totalCityBlockDistance;
    }

    private static Integer heuristicFunction3(Integer[][] state) {

        Integer rowDifference = 0;
        Integer columnDifference = 0;
        Integer estimatedDistance = 0;
        Integer diagonalMove = 0;

        for (int i = 0; i < Constants.ROW_SIZE; i++) {
            for (int j = 0; j < Constants.COLUMN_SIZE; j++) {
                final Integer tile = state[i][j];

                switch (tile) {
                    case 0:
                        continue;
                    case 1:
                        rowDifference = Math.abs(i - 0);
                        columnDifference = Math.abs(j - 0);
                        diagonalMove = Math.min(rowDifference, columnDifference);
                        estimatedDistance += (diagonalMove * 3) + Math.abs(rowDifference - columnDifference);
                        break;
                    case 2:
                        rowDifference = Math.abs(i - 0);
                        columnDifference = Math.abs(j - 1);
                        diagonalMove = Math.min(rowDifference, columnDifference);
                        estimatedDistance += (diagonalMove * 3) + Math.abs(rowDifference - columnDifference);
                        break;
                    case 3:
                        rowDifference = Math.abs(i - 0);
                        columnDifference = Math.abs(j - 2);
                        diagonalMove = Math.min(rowDifference, columnDifference);
                        estimatedDistance += (diagonalMove * 3) + Math.abs(rowDifference - columnDifference);
                        break;
                    case 4:
                        rowDifference = Math.abs(i - 0);
                        columnDifference = Math.abs(j - 3);
                        diagonalMove = Math.min(rowDifference, columnDifference);
                        estimatedDistance += (diagonalMove * 3) + Math.abs(rowDifference - columnDifference);
                        break;
                    case 5:
                        rowDifference = Math.abs(i - 1);
                        columnDifference = Math.abs(j - 3);
                        diagonalMove = Math.min(rowDifference, columnDifference);
                        estimatedDistance += (diagonalMove * 3) + Math.abs(rowDifference - columnDifference);
                        break;
                    case 6:
                        rowDifference = Math.abs(i - 2);
                        columnDifference = Math.abs(j - 3);
                        diagonalMove = Math.min(rowDifference, columnDifference);
                        estimatedDistance += (diagonalMove * 3) + Math.abs(rowDifference - columnDifference);
                        break;
                    case 7:
                        rowDifference = Math.abs(i - 3);
                        columnDifference = Math.abs(j - 3);
                        diagonalMove = Math.min(rowDifference, columnDifference);
                        estimatedDistance += (diagonalMove * 3) + Math.abs(rowDifference - columnDifference);
                        break;
                    case 8:
                        rowDifference = Math.abs(i - 3);
                        columnDifference = Math.abs(j - 2);
                        diagonalMove = Math.min(rowDifference, columnDifference);
                        estimatedDistance += (diagonalMove * 3) + Math.abs(rowDifference - columnDifference);
                        break;
                    case 9:
                        rowDifference = Math.abs(i - 3);
                        columnDifference = Math.abs(j - 1);
                        diagonalMove = Math.min(rowDifference, columnDifference);
                        estimatedDistance += (diagonalMove * 3) + Math.abs(rowDifference - columnDifference);
                        break;
                    case 10:
                        rowDifference = Math.abs(i - 3);
                        columnDifference = Math.abs(j - 0);
                        diagonalMove = Math.min(rowDifference, columnDifference);
                        estimatedDistance += (diagonalMove * 3) + Math.abs(rowDifference - columnDifference);
                        break;
                    case 11:
                        rowDifference = Math.abs(i - 2);
                        columnDifference = Math.abs(j - 0);
                        diagonalMove = Math.min(rowDifference, columnDifference);
                        estimatedDistance += (diagonalMove * 3) + Math.abs(rowDifference - columnDifference);
                        break;
                    case 12:
                        rowDifference = Math.abs(i - 1);
                        columnDifference = Math.abs(j - 0);
                        diagonalMove = Math.min(rowDifference, columnDifference);
                        estimatedDistance += (diagonalMove * 3) + Math.abs(rowDifference - columnDifference);
                        break;
                    case 13:
                        rowDifference = Math.abs(i - 1);
                        columnDifference = Math.abs(j - 1);
                        diagonalMove = Math.min(rowDifference, columnDifference);
                        estimatedDistance += (diagonalMove * 3) + Math.abs(rowDifference - columnDifference);
                        break;
                    case 14:
                        rowDifference = Math.abs(i - 1);
                        columnDifference = Math.abs(j - 2);
                        diagonalMove = Math.min(rowDifference, columnDifference);
                        estimatedDistance += (diagonalMove * 3) + Math.abs(rowDifference - columnDifference);
                        break;
                    case 15:
                        rowDifference = Math.abs(i - 2);
                        columnDifference = Math.abs(j - 2);
                        diagonalMove = Math.min(rowDifference, columnDifference);
                        estimatedDistance += (diagonalMove * 3) + Math.abs(rowDifference - columnDifference);
                        break;
                }

            }
        }
        return estimatedDistance;
    }

    private static String getReverseDirection(String direction) {

        switch (direction) {

            case Constants.Moves.UP:
                return Constants.Moves.DOWN;
            case Constants.Moves.DOWN:
                return Constants.Moves.UP;
            case Constants.Moves.LEFT:
                return Constants.Moves.RIGHT;
            case Constants.Moves.RIGHT:
                return Constants.Moves.LEFT;
            case Constants.Moves.UP_LEFT:
                return Constants.Moves.DOWN_RIGHT;
            case Constants.Moves.UP_RIGHT:
                return Constants.Moves.DOWN_LEFT;
            case Constants.Moves.DOWN_LEFT:
                return Constants.Moves.UP_RIGHT;
            case Constants.Moves.DOWN_RIGHT:
                return Constants.Moves.UP_LEFT;
            default:
                return null;
        }
    }

    private static Boolean checkTime(Long startTime) {

        if (System.currentTimeMillis() - startTime > Constants.TIME_LIMIT) return true;
        else return false;
    }
}
