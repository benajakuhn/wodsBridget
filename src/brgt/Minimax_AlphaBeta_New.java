package brgt;
import java.util.ArrayList;
import java.util.List;

public class Minimax_AlphaBeta_New {

    public static int MAX_DEPTH = 2; // Adjustable
    public static PieceInventory player1Inventory = new PieceInventory();
    public static PieceInventory player2Inventory = new PieceInventory();

    public static long evaluatedNodes = 0;
    public static long prunedNodes = 0;
    public static long total_time = 0;


    public static int minimax(int depth, boolean maximizingPlayer, int alpha, int beta, int maxPlayerNumber) {
        if (depth == 0 || GameUtils.isTerminal(player1Inventory, player2Inventory)) {
            return GameUtils.evaluate(player1Inventory, player2Inventory);
        }

        int currentPlayerNumber = maximizingPlayer ? maxPlayerNumber : (maxPlayerNumber == 1 ? 2 : 1);
        PieceInventory currentInventory = currentPlayerNumber == 1 ? player1Inventory : player2Inventory;

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : generateMoves(currentInventory, currentPlayerNumber)) {
                if (tryMove(move)) {
                    evaluatedNodes++;
                    currentInventory.usePiece(Main.getPieceType(move.shape));
                    int eval = minimax(depth - 1, false, alpha, beta, maxPlayerNumber);
                    undoMove(move);
                    currentInventory.returnPiece(Main.getPieceType(move.shape));
                    maxEval = Math.max(maxEval, eval);
                    alpha = Math.max(alpha, eval); // Update alpha
                    if (beta <= alpha) { // Pruning condition
                        prunedNodes++;
                        break;
                    }
                }
            }
            return maxEval;
        } else { // Minimizing player
            int minEval = Integer.MAX_VALUE;
            for (Move move : generateMoves(currentInventory, currentPlayerNumber)) {
                if (tryMove(move)) {
                    evaluatedNodes++;
                    currentInventory.usePiece(Main.getPieceType(move.shape));
                    int eval = minimax(depth - 1, true, alpha, beta, maxPlayerNumber);
                    undoMove(move);
                    currentInventory.returnPiece(Main.getPieceType(move.shape));
                    minEval = Math.min(minEval, eval);
                    beta = Math.min(beta, eval); // Update beta
                    if (beta <= alpha) { // Pruning condition
                        prunedNodes++;
                        break;
                    }
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

    public static List<Move> generateMoves(PieceInventory inventory, int player) {
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

    public static Move findBestMove(int playerNumber) {
        long startTime = System.nanoTime();
        evaluatedNodes = 0; // Reset counters for each call
        prunedNodes = 0;
        total_time = 0;

        int bestValue = Integer.MIN_VALUE;
        Move bestMove = null;

        PieceInventory currentInventory = playerNumber == 1 ? player1Inventory : player2Inventory;

        List<Move> moves = generateMoves(currentInventory, playerNumber);
        int totalMoves = moves.size();
        int currentMoveIndex = 0;

        for (Move move : moves) {
            currentMoveIndex++;

            if (tryMove(move)) {
                evaluatedNodes++;
                currentInventory.usePiece(Main.getPieceType(move.shape));
                // Initial call to minimax with alpha = Integer.MIN_VALUE and beta = Integer.MAX_VALUE
                // Opponent is minimizing
                int moveValue = minimax(MAX_DEPTH - 1, false, Integer.MIN_VALUE, Integer.MAX_VALUE, playerNumber);
                undoMove(move);
                currentInventory.returnPiece(Main.getPieceType(move.shape));

                if (moveValue > bestValue) {
                    bestValue = moveValue;
                    bestMove = move;
                }
            }
        }

        long endTime = System.nanoTime(); // End time measurement
        total_time = (endTime - startTime) / 1_000_000; // Convert to milliseconds
        System.out.println("Total Execution time: " + total_time + " ms");
        System.out.println("Total Evaluated nodes: " + evaluatedNodes);
        System.out.println("Total Pruned branches: " + prunedNodes);

        if (bestMove != null) {
            System.out.println("Best move found for Player " + playerNumber + ": " + bestMove + " with value: " + bestValue);
        } else {
            System.out.println("No best move found for Player " + playerNumber + " (possibly no valid moves).");
        }
        return bestMove;
    }
}