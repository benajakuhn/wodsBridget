import java.util.Arrays;

public class Main {
    public static final int[][][] GAME_BOARD = new int[8][8][3];

    public static final int[][] tBlockShape = new int[][] {
        {0, 0, 0}, // center block
        {-1, 0, 0}, //left block
        {1, 0, 0}, // right block
        {0, 1, 0} // top block
    };

    public static final int[][] lBlockShape = new int[][] {
        {0, 0, 0}, // center block
        {-1, 0, 0}, //left block
        {1, 0, 0}, // right block
        {1, 1, 0} // bottom block
    };

    public static final int[][] zBlockShape = new int[][] {
        {0, 0, 0}, // center block
        {1, 0, 0}, // right block
        {0, 1, 0}, // bottom block
        {-1, 1, 0} // bottom left block
    };

    public static final int[][] oBlockShape = new int[][] {
        {0, 0, 0}, // center block
        {1, 0, 0}, // right block
        {0, 1, 0}, // bottom block
        {1, 1, 0} // bottom right block
    };


    public static void main(String[] args) {
        // print index 16 of the rotation matrices
        System.out.println(Arrays.deepToString(BlockRotator3D.ROTATION_MATRICES[16]));


        BlockRotator3D.placeAllRotations(tBlockShape, 3, 3, 2);
        BlockRotator3D.placeAllRotations(lBlockShape, 3, 3, 1);
        BlockRotator3D.placeAllRotations(zBlockShape, 3, 3, 2);
        BlockRotator3D.placeAllRotations(oBlockShape, 3, 3, 1);
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


    // Helper function to print the game board in ASCII art
    public static String toAsciiString() {
        StringBuilder sb = new StringBuilder();
        StringBuilder[][] linesPerLevel = new StringBuilder[3][9]; // 8 lines + title

        // Build each level's output line-by-line
        for (int z = 2; z >= 0; z--) {
            linesPerLevel[z][0] = new StringBuilder("Level " + z + ":        "); // Title line
            for (int y = 0; y < 8; y++) {
                linesPerLevel[z][y + 1] = new StringBuilder();
                for (int x = 0; x < 8; x++) {
                    int value = GAME_BOARD[x][y][z];
                    char c;
                    switch (value) {
                        case 1: c = 'X'; break;
                        case 2: c = 'O'; break;
                        default: c = '.';
                    }
                    linesPerLevel[z][y + 1].append(c).append(' ');
                }
            }
        }

        // Print line by line (title + 8 grid rows)
        for (int line = 0; line < 9; line++) { // 0 = title, 1-8 = grid rows
            for (int z = 2; z >= 0; z--) {
                sb.append(linesPerLevel[z][line]);
                sb.append("   "); // Space between fields
            }
            sb.append('\n');
        }

        sb.append("------------------------------------------------------------------------------------------------\n");
        return sb.toString();
    }


}