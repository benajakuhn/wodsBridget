import java.util.ArrayList;
import java.util.List;

public class Minimax_AlphaBeta {

    public static int MAX_DEPTH = 2; // Adjustable
    public static PieceInventory player1Inventory = new PieceInventory();
    public static PieceInventory player2Inventory = new PieceInventory();

    public static int evaluatedNodes = 0;
    public static int prunedNodes = 0; // Counter for pruned nodes


    public static int minimax(int depth, boolean maximizingPlayer, int alpha, int beta, int maxPlayerNumber) {
        if (depth == 0 || isTerminal()) {
            return evaluate(maxPlayerNumber);
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

    private static boolean isTerminal() {
        // If both players have no pieces left, the game is over
        return player1Inventory.isEmpty() && player2Inventory.isEmpty();
    }

    private static int evaluate(int maxPlayerNumber) {
        int minPlayerNumber = maxPlayerNumber == 1 ? 2 : 1;
        GameChecker.Result maxPlayerResult = GameChecker.checkPlayer(Main.flattenTopView(), maxPlayerNumber);
        GameChecker.Result minPlayerResult = GameChecker.checkPlayer(Main.flattenTopView(), minPlayerNumber);

        if (maxPlayerResult.hasWon) {
            return 1000;
        } else if (minPlayerResult.hasWon) {
            return -1000;
        } else {
            return maxPlayerResult.longestPath - minPlayerResult.longestPath;
        }
    }

    public static Move findBestMove(int playerNumber) {
        long startTime = System.nanoTime();
        evaluatedNodes = 0; // Reset counters for each call
        prunedNodes = 0;

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
        System.out.println(); // Move to next line after progress bar is complete

        System.out.println("Evaluated nodes at Depth " + MAX_DEPTH + ": " + evaluatedNodes);
        System.out.println("Pruned branches: " + prunedNodes);

        long endTime = System.nanoTime(); // End time measurement
        long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds
        System.out.println("Execution time: " + duration + " ms");

        if (bestMove != null) {
            System.out.println("Best move found for Player " + playerNumber + ": " + bestMove + " with value: " + bestValue);
        } else {
            System.out.println("No best move found for Player " + playerNumber + " (possibly no valid moves).");
        }
        return bestMove;
    }
}