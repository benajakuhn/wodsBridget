import java.util.Scanner;

public class Main {
    public static final int[][][] GAME_BOARD = new int[8][8][3];

    public static final int[][] tBlockShape = new int[][]{
            {0, 0, 0}, // center block
            {-1, 0, 0}, //left block
            {1, 0, 0}, // right block
            {0, 1, 0} // top block
    };

    public static final int[][] lBlockShape = new int[][]{
            {0, 0, 0}, // center block
            {-1, 0, 0}, //left block
            {1, 0, 0}, // right block
            {1, 1, 0} // bottom block
    };

    public static final int[][] zBlockShape = new int[][]{
            {0, 0, 0}, // center block
            {1, 0, 0}, // right block
            {0, 1, 0}, // bottom block
            {-1, 1, 0} // bottom left block
    };

    public static final int[][] oBlockShape = new int[][]{
            {0, 0, 0}, // center block
            {1, 0, 0}, // right block
            {0, 1, 0}, // bottom block
            {1, 1, 0} // bottom right block
    };


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Current board:");
            System.out.println(toAsciiString());

            // Max Player (AI) Move
            System.out.println("AI is thinking...");
            Move bestMove = Minimax.findBestMove();
            if (bestMove == null) {
                System.out.println("AI has no moves left! Game over.");
                break;
            }
            BlockRotator3D.placeShape(bestMove.getTransformedShape(), bestMove.x, bestMove.y, bestMove.player);
            Minimax.player1Inventory.usePiece(getPieceType(bestMove.shape));
            System.out.println("AI placed a piece:");
            System.out.println(toAsciiString());

            if (checkWin()) break;

            // Min Player (Human) Move
            System.out.println("Your turn! Enter piece type (T/L/Z/O), rotation index (0-23), x (0-7), y (0-7): ");
            String input = scanner.nextLine();
            String[] parts = input.trim().split("\\s+");
            if (parts.length != 4) {
                System.out.println("Invalid input. Try again.");
                continue;
            }

            String pieceType = parts[0].toUpperCase();
            int rotationIndex = Integer.parseInt(parts[1]);
            int x = Integer.parseInt(parts[2]);
            int y = Integer.parseInt(parts[3]);

            int[][] shape = getShapeFromType(pieceType);
            int[][] rotationMatrix = BlockRotator3D.ROTATION_MATRICES[rotationIndex];
            Move playerMove = new Move(shape, rotationMatrix, x, y, 2);

            if (BlockRotator3D.placeShape(playerMove.getTransformedShape(), playerMove.x, playerMove.y, playerMove.player)) {
                Minimax.player2Inventory.usePiece(pieceType);
            } else {
                System.out.println("Invalid move. Try again.");
                continue;
            }

            System.out.println("You placed a piece:");
            System.out.println(toAsciiString());

            if (checkWin()) break;
        }
        scanner.close();
    }

    public static boolean checkWin() {
        GameChecker.Result maxResult = GameChecker.checkPlayer(flattenTopView(), 1);
        GameChecker.Result minResult = GameChecker.checkPlayer(flattenTopView(), 2);

        if (maxResult.hasWon) {
            System.out.println("AI (Max Player) wins!");
            return true;
        }
        if (minResult.hasWon) {
            System.out.println("You (Min Player) win!");
            return true;
        }
        return false;
    }

    private static int[][] getShapeFromType(String type) {
        switch (type) {
            case "T":
                return tBlockShape;
            case "L":
                return lBlockShape;
            case "Z":
                return zBlockShape;
            case "O":
                return oBlockShape;
            default:
                throw new IllegalArgumentException("Unknown piece type: " + type);
        }
    }

    public static String getPieceType(int[][] shape) {
        if (shape == tBlockShape) return "T";
        if (shape == lBlockShape) return "L";
        if (shape == zBlockShape) return "Z";
        if (shape == oBlockShape) return "O";
        throw new IllegalArgumentException("Unknown piece type!");
    }

    // Helper function to clear the game board
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
                        case 1:
                            c = 'X';
                            break;
                        case 2:
                            c = 'O';
                            break;
                        default:
                            c = '.';
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

    // Helper function to flatten the game board for top view
    public static int[][] flattenTopView() {
        int[][] flatBoard = new int[8][8];
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                for (int z = 2; z >= 0; z--) {
                    if (GAME_BOARD[x][y][z] != 0) {
                        flatBoard[x][y] = GAME_BOARD[x][y][z];
                        break;
                    }
                }
            }
        }
        return flatBoard;
    }
}