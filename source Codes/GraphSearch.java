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
                    if (checkTime(startTime)) break;

                    lengthLimit++;
                }
                break;
            case Constants.Strategy.A_STAR:
                finalNode = lengthLimitedSearch(initialState, strategy, heuristicFuntion, Integer.MAX_VALUE);
                break;

        }
        if(Objects.isNull(finalNode)) return null;
        Utility.printSolution(finalNode,expandCounter,maxStoredNode);
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

            if (expandCounter%5000 == 0){
                System.out.println("Expanded Node = " + expandCounter);
                System.out.println("Current State: ");
                Utility.printSate(currentNode.getState());
                System.out.println("Path cost of current Node: " +currentNode.getRealCost());
                System.out.println("Estimated cost to final State : " +currentNode.getEstimatedCost() +"\n");
            }
        }
    }

    private static Boolean goalStateCheck(Node currentNode) {

        final String currentState = currentNode.getStateText();

        return Utility.stateCompare(currentState, Constants.FINAL_STATE_TEXT);
    }

    private static Node move(Node currentNode, String direction) {

        final Integer[][] currentState = new Integer[Constants.ROW_SIZE][Constants.COLUMN_SIZE];
        Utility.copyState(currentNode.getState(), currentState);

        final Integer moveCost = Utility.moveState(currentState, direction);
        final Node newNode = new Node(currentState, currentNode, currentNode.getRealCost() + moveCost, direction);
        return newNode;
    }

    private static void expandNode(Node currentNode, PriorityQueue<Node> frontierList, ArrayList<Node> exploredSet, Integer lenghLimit, String strategy, String heuristicFunction) {

        final Integer[][] currentState = currentNode.getState();
        final Integer[] emptyTileCoorddinate = Utility.detectTileCoordinate(currentState, 0);

        final ArrayList<String> allowableMoves = Utility.getAllowableMoves(emptyTileCoorddinate);


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

            if (isExplored(childNode, exploredSet) || childNode.getRealCost() > lenghLimit || isInFrontier(childNode, frontierList))
                continue;

            frontierList.add(childNode);
        }

        if (frontierList.size() > maxStoredNode) maxStoredNode = frontierList.size();
    }

    private static Boolean isExplored(Node childNode, ArrayList<Node> exploredSet) {

        final Integer exploredSetSize = exploredSet.size();
        for (int i = 0; i < exploredSetSize; i++) {

            final Node currentNode = exploredSet.get(i);

            if ((Objects.isNull(currentNode.getParentNode()) || childNode.getRealCost() >= currentNode.getRealCost())
                    && Utility.stateCompare(currentNode.getStateText(), childNode.getStateText()))
                return true;
        }
        return false;
    }

    private static Boolean isInFrontier(Node childNode, PriorityQueue<Node> frontierList) {

        final Boolean[] flag = {false};
        frontierList.forEach(node -> {
            if ((Utility.stateCompare(node.getParentNode().getStateText(), childNode.getParentNode().getStateText()) || childNode.getRealCost() >= node.getRealCost())
                    && Utility.stateCompare(node.getStateText(), childNode.getStateText())) {
                flag[0] = true;
            }
        });
        return flag[0];


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
                    Integer[] goalCoordinate = Utility.detectTileCoordinate(state, Constants.FINAL_STATE[i][j]);
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

    private static Boolean checkTime(Long startTime) {

        if (System.currentTimeMillis() - startTime > Constants.TIME_LIMIT) return true;
        else return false;
    }
}
