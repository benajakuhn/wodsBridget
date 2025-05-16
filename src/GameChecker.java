import java.util.*;

public class GameChecker {

    private static final int[] DX = {0, 0, 1, -1}; // Right, Left, Down, Up
    private static final int[] DY = {1, -1, 0, 0};

    private static final int L_PIECE_VALUE = 8;
    private static final int T_PIECE_PRESENCE_VALUE = 5;
    private static final int Z_PIECE_PRESENCE_VALUE = 5;
    private static final int O_PIECE_PRESENCE_VALUE = 5;

    public static class Result {
        boolean hasWon;
        int longestPath;
    }

    public static Result checkPlayer(int[][] board, int player) {
        int size = board.length;
        boolean[][] visited = new boolean[size][size];
        Result result = new Result();
        result.hasWon = false;
        result.longestPath = 0;

        // Try to connect left to right
        for (int y = 0; y < size; y++) {
            if (board[0][y] == player) {
                dfs(board, visited, 0, y, player, true, 1, result);
            }
        }

        // Reset visited and try top to bottom
        visited = new boolean[size][size];
        for (int x = 0; x < size; x++) {
            if (board[x][0] == player) {
                dfs(board, visited, x, 0, player, false, 1, result);
            }
        }

        return result;
    }

    public static int calculateInventoryScore(PieceInventory inventory) {
        int score = 0;

        // Score for L pieces (quantity matters)
        score += inventory.getPieceCount("L") * L_PIECE_VALUE;

        // Score for presence of other pieces (diversity matters)
        if (inventory.getPieceCount("T") > 0) {
            score += T_PIECE_PRESENCE_VALUE;
        }
        if (inventory.getPieceCount("Z") > 0) {
            score += Z_PIECE_PRESENCE_VALUE;
        }
        if (inventory.getPieceCount("O") > 0) {
            score += O_PIECE_PRESENCE_VALUE;
        }
        return score;
    }

    private static void dfs(int[][] board, boolean[][] visited, int x, int y, int player, boolean horizontal, int currentLength, Result result) {
        int size = board.length;
        visited[x][y] = true;

        if (horizontal && x == size - 1) result.hasWon = true;
        if (!horizontal && y == size - 1) result.hasWon = true;

        result.longestPath = Math.max(result.longestPath, currentLength);

        for (int d = 0; d < 4; d++) {
            int nx = x + DX[d];
            int ny = y + DY[d];

            if (nx >= 0 && nx < size && ny >= 0 && ny < size &&
                    !visited[nx][ny] && board[nx][ny] == player) {
                dfs(board, visited, nx, ny, player, horizontal, currentLength + 1, result);
            }
        }

        visited[x][y] = false; // Backtrack
    }
}
