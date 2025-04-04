import java.util.ArrayList;
import java.util.List;

public class Minimax {

    private static final int MAX_DEPTH = 2; // Adjustable
    public static PieceInventory player1Inventory = new PieceInventory();
    public static PieceInventory player2Inventory = new PieceInventory();

    public static int minimax(int depth, boolean maximizingPlayer) {
        if (depth == 0 || isTerminal()) {
            return evaluate();
        }

        PieceInventory currentInventory = maximizingPlayer ? player1Inventory : player2Inventory;
        int currentPlayer = maximizingPlayer ? 1 : 2;

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : generateMoves(currentInventory, currentPlayer)) {
                if (tryMove(move)) {
                    currentInventory.usePiece(Main.getPieceType(move.shape));
                    int eval = minimax(depth - 1, false);
                    undoMove(move);
                    currentInventory.returnPiece(Main.getPieceType(move.shape));
                    maxEval = Math.max(maxEval, eval);
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : generateMoves(currentInventory, currentPlayer)) {
                if (tryMove(move)) {
                    currentInventory.usePiece(Main.getPieceType(move.shape));
                    int eval = minimax(depth - 1, true);
                    undoMove(move);
                    currentInventory.returnPiece(Main.getPieceType(move.shape));
                    minEval = Math.min(minEval, eval);
                }
            }
            return minEval;
        }
    }

    private static boolean tryMove(Move move) {
        return BlockRotator3D.placeShape(
                move.getTransformedShape(),
                move.x,
                move.y,
                move.player
        );
    }

    private static void undoMove(Move move) {
        BlockRotator3D.removeShape(
                move.getTransformedShape(),
                move.x,
                move.y
        );
    }

    private static List<Move> generateMoves(PieceInventory inventory, int player) {
        List<Move> moves = new ArrayList<>();

        if (inventory.hasPiece("T")) {
            for (int rotationIndex : BlockRotator3D.TBLOCK_ROTATION_INDICES) {
                addMoves(moves, Main.tBlockShape, rotationIndex, player);
            }
        }
        if (inventory.hasPiece("L")) {
            for (int rotationIndex : BlockRotator3D.LBLOCK_ROTATION_INDICES) {
                addMoves(moves, Main.lBlockShape, rotationIndex, player);
            }
        }
        if (inventory.hasPiece("Z")) {
            for (int rotationIndex : BlockRotator3D.ZBLOCK_ROTATION_INDICES) {
                addMoves(moves, Main.zBlockShape, rotationIndex, player);
            }
        }
        if (inventory.hasPiece("O")) {
            for (int rotationIndex : BlockRotator3D.OBLOCK_ROTATION_INDICES) {
                addMoves(moves, Main.oBlockShape, rotationIndex, player);
            }
        }

        return moves;
    }

    private static void addMoves(List<Move> moves, int[][] shape, int rotationIndex, int player) {
        int[][] rotationMatrix = BlockRotator3D.ROTATION_MATRICES[rotationIndex];
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                moves.add(new Move(shape, rotationMatrix, x, y, player));
            }
        }
    }

    private static boolean isTerminal() {
        // If both players have no pieces left, the game is over
        return player1Inventory.isEmpty() && player2Inventory.isEmpty();
    }

    private static int evaluate() {
        GameChecker.Result maxPlayerResult = GameChecker.checkPlayer(Main.flattenTopView(), 1);
        GameChecker.Result minPlayerResult = GameChecker.checkPlayer(Main.flattenTopView(), 2);

        if (maxPlayerResult.hasWon) {
            return 100;
        } else if (minPlayerResult.hasWon) {
            return -100;
        } else {
            return maxPlayerResult.longestPath - minPlayerResult.longestPath;
        }
    }

    public static Move findBestMove() {
        int bestValue = Integer.MIN_VALUE;
        Move bestMove = null;

        PieceInventory currentInventory = player1Inventory; // Assuming Player 1 is maximizing
        int currentPlayer = 1;

        List<Move> moves = generateMoves(currentInventory, currentPlayer);
        int totalMoves = moves.size();
        int currentMoveIndex = 0;

        for (Move move : moves) {
            currentMoveIndex++;

            if (tryMove(move)) {
                currentInventory.usePiece(Main.getPieceType(move.shape));
                int moveValue = minimax(MAX_DEPTH, false);
                undoMove(move);
                currentInventory.returnPiece(Main.getPieceType(move.shape));

                if (moveValue > bestValue) {
                    bestValue = moveValue;
                    bestMove = move;
                }
            }

            // Progress bar
            int progress = (int) ((currentMoveIndex / (double) totalMoves) * 100);
            StringBuilder progressBar = new StringBuilder("[");
            int barLength = 50; // Adjust the bar length here
            int filledLength = (int) (barLength * progress / 100.0);
            for (int i = 0; i < barLength; i++) {
                if (i < filledLength) {
                    progressBar.append("=");
                } else {
                    progressBar.append(" ");
                }
            }
            progressBar.append("] ").append(progress).append("%");

            System.out.print("\r" + progressBar.toString());
        }
        System.out.println(); // Move to next line after progress bar is complete

        System.out.println("Best move found: " + bestMove + " with value: " + bestValue);
        return bestMove;
    }
}
