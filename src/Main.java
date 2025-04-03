public class Main {
    public static final int[][][] GAME_BOARD = new int[8][8][3];
    public static int[][] tBlockShape = new int[][] {
        {0, 0, 0},
        {-1, 0, 0},
        {1, 0, 0},
        {0, 1, 0}
    };

    public static void main(String[] args) {
//        for (int i = 0; i < 4; i++) {
//            block.place(3, 3);
//            block.rotate();
//            System.out.println(toAsciiString());
//            clearGameBoard();
//        }



        BlockRotator3D.placeAllRotations(tBlockShape, 3, 3, 0);
    }

    public static void clearGameBoard() {
        for (int i = 0; i < GAME_BOARD.length; i++) {
            for (int j = 0; j < GAME_BOARD[i].length; j++) {
                for (int k = 0; k < GAME_BOARD[i][j].length; k++) {
                    GAME_BOARD[i][j][k] = 0;
                }
            }
        }
    }

    public static String toAsciiString() {
        StringBuilder sb = new StringBuilder();
        for (int z = 2; z >= 0; z--) { // from top layer to bottom
            sb.append("Level ").append(z).append(":\n");
            for (int y = 0; y < 8; y++) {
                for (int x = 0; x < 8; x++) {
                    int value = GAME_BOARD[x][y][z];
                    char c;
                    switch (value) {
                    case 1: c = 'X'; break; // your block
                    case 2: c = 'O'; break; // opponent's block
                    default: c = '.';       // empty
                    }
                    sb.append(c).append(' ');
                }
                sb.append('\n');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

}