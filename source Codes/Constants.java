public final class Constants {

    public static final Integer[][] FINAL_STATE = {{1, 2, 3, 4},
            {12, 13, 14, 5},
            {11, 0, 15, 6},
            {10, 9, 8, 7}};

    public static final Integer[][] INITIAL_STATE = {{1,3,5,4},
            {2, 13, 14, 15},
            {11, 12, 9, 6},
            {0, 10, 8, 7}};



    public static final String FINAL_STATE_TEXT = "[1, 2, 3, 4][12, 13, 14, 5][11, 0, 15, 6][10, 9, 8, 7]";

    public static final Integer COLUMN_SIZE = FINAL_STATE[0].length;

    public static final Integer ROW_SIZE = FINAL_STATE.length;

    public static final Long TIME_LIMIT = Long.valueOf(300000);

    public static final Integer DEPTH = 8;


    public class Strategy {

        public static final String UCS = "UCS";

        public static final String ILS = "ILS";

        public static final String A_STAR = "Astar";

        public static final String HEURISTIC_1 = "H1";

        public static final String HEURISTIC_2 = "H2";

        public static final String HEURISTIC_3 = "H3";
    }

    public class Moves {

        public static final String LEFT = "L";

        public static final String RIGHT = "R";

        public static final String UP = "U";

        public static final String DOWN = "D";

        public static final String UP_LEFT = "UL";

        public static final String UP_RIGHT = "UR";

        public static final String DOWN_LEFT = "DL";

        public static final String DOWN_RIGHT = "DR";


    }
}


