import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Minimax_History {
    public static int MAX_DEPTH = 5;
    public static PieceInventory player1Inventory = new PieceInventory();
    public static PieceInventory player2Inventory = new PieceInventory();

    // Counters for search statistics
    public static int evaluatedNodes = 0;
    public static int prunedNodes = 0;

    // History table: Map<MoveKey_String, Score_Integer>
    private static Map<String, Integer> historyTable = new HashMap<>();


    public static int minimax(int depth, boolean maximizingPlayer, int alpha, int beta, int currentPlyFromRoot) {
        if (depth == 0 || GameUtils.isTerminal(player1Inventory, player2Inventory)) {
            return GameUtils.evaluate(player1Inventory, player2Inventory);
        }

        PieceInventory currentInventory = maximizingPlayer ? player1Inventory : player2Inventory;
        int currentPlayer = maximizingPlayer ? 1 : 2;

        List<Move> moves = GameUtils.generateMoves(currentInventory, currentPlayer);

        moves.sort((m1, m2) -> {
            int score1 = historyTable.getOrDefault(GameUtils.getMoveHistoryKey(m1), 0);
            int score2 = historyTable.getOrDefault(GameUtils.getMoveHistoryKey(m2), 0);
            return Integer.compare(score2, score1);
        });

        Move bestMoveForThisNode = null;

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : moves) {
                if (GameUtils.tryMove(move)) {
                    evaluatedNodes++;
                    currentInventory.usePiece(Main.getPieceType(move.shape));
                    int eval = minimax(depth - 1, false, alpha, beta, currentPlyFromRoot + 1);
                    GameUtils.undoMove(move);
                    currentInventory.returnPiece(Main.getPieceType(move.shape));

                    if (eval > maxEval) {
                        maxEval = eval;
                        bestMoveForThisNode = move;
                    }
                    alpha = Math.max(alpha, eval);
                    if (beta <= alpha) {
                        prunedNodes++;
                        String moveKey = GameUtils.getMoveHistoryKey(move);
                        historyTable.put(moveKey, historyTable.getOrDefault(moveKey, 0) + (int) Math.pow(2, depth));
                        return maxEval;
                    }
                }
            }
            if (bestMoveForThisNode != null) {
                String moveKey = GameUtils.getMoveHistoryKey(bestMoveForThisNode);
                historyTable.put(moveKey, historyTable.getOrDefault(moveKey, 0) + (int) Math.pow(2, depth));
            }
            return maxEval;
        } else { // Minimizing player
            int minEval = Integer.MAX_VALUE;
            for (Move move : moves) {
                if (GameUtils.tryMove(move)) {
                    evaluatedNodes++;
                    currentInventory.usePiece(Main.getPieceType(move.shape));
                    int eval = minimax(depth - 1, true, alpha, beta, currentPlyFromRoot + 1);
                    GameUtils.undoMove(move);
                    currentInventory.returnPiece(Main.getPieceType(move.shape));

                    if (eval < minEval) {
                        minEval = eval;
                        bestMoveForThisNode = move;
                    }
                    beta = Math.min(beta, eval);
                    if (beta <= alpha) {
                        prunedNodes++;
                        String moveKey = GameUtils.getMoveHistoryKey(move);
                        historyTable.put(moveKey, historyTable.getOrDefault(moveKey, 0) + (int) Math.pow(2, depth));
                        return minEval;
                    }
                }
            }
            if (bestMoveForThisNode != null) {
                String moveKey = GameUtils.getMoveHistoryKey(bestMoveForThisNode);
                historyTable.put(moveKey, historyTable.getOrDefault(moveKey, 0) + (int) Math.pow(2, depth));
            }
            return minEval;
        }
    }

    public static Move findBestMove(long TIME_LIMIT_MS) {
        long overallStartTime = System.nanoTime();
        evaluatedNodes = 0;
        prunedNodes = 0;
        historyTable.clear();

        Move overallBestMove = null;
        int overallBestValue = Integer.MIN_VALUE;

        System.out.println("Starting Iterative Deepening Search...");

        for (int currentDepthIteration = 1; (((System.nanoTime() - overallStartTime) / 1_000_000) <= TIME_LIMIT_MS) && currentDepthIteration <= MAX_DEPTH; currentDepthIteration++) {
            long iterationStartTime = System.nanoTime();
            System.out.println("\n--- Iteration Depth: " + currentDepthIteration + " ---");

            int iterationBestValue = Integer.MIN_VALUE;
            Move iterationBestMove = null;

            PieceInventory currentInventory = player1Inventory;
            int currentPlayer = 1;

            List<Move> moves = GameUtils.generateMoves(currentInventory, currentPlayer);
            moves.sort((m1, m2) -> {
                int score1 = historyTable.getOrDefault(GameUtils.getMoveHistoryKey(m1), 0);
                int score2 = historyTable.getOrDefault(GameUtils.getMoveHistoryKey(m2), 0);
                return Integer.compare(score2, score1);
            });

            int totalMovesAtThisLevel = moves.size();
            int currentMoveIndex = 0;

            for (Move move : moves) {
                // Stop if the time limit is reached
                if (((System.nanoTime() - overallStartTime) / 1_000_000) >= TIME_LIMIT_MS) {
                    break;
                }

                currentMoveIndex++;
                if (GameUtils.tryMove(move)) {
                    currentInventory.usePiece(Main.getPieceType(move.shape));
                    int moveValue = minimax(currentDepthIteration - 1, false, Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
                    GameUtils.undoMove(move);
                    currentInventory.returnPiece(Main.getPieceType(move.shape));

                    if (moveValue > iterationBestValue) {
                        iterationBestValue = moveValue;
                        iterationBestMove = move;
                    }
                }

                //int progress = (currentMoveIndex * 100) / totalMovesAtThisLevel;
               // System.out.print("\rDepth " + currentDepthIteration + " Progress: [" + String.join("", Collections.nCopies(progress / 2, "=")) + String.join("", Collections.nCopies(50 - progress / 2, " ")) + "] " + progress + "%");
            }
            System.out.println();

            // Print iteration best move and update the overall best move
            overallBestMove = iterationBestMove;
            overallBestValue = iterationBestValue;
            System.out.println("Depth " + currentDepthIteration + ": Best move found: " + iterationBestMove + " with value: " + iterationBestValue);

            // Print the first few elements of the history table
            System.out.println("History Table (first few entries):");
            historyTable.entrySet().stream()
                .forEach(entry -> System.out.println(entry.getKey() + " -> " + entry.getValue()));

            long iterationEndTime = System.nanoTime();
            System.out.println("Depth " + currentDepthIteration + " time: " + (iterationEndTime - iterationStartTime) / 1_000_000 + " ms");
            System.out.println("Cumulative Evaluated Nodes: " + evaluatedNodes + ", Pruned Branches: " + prunedNodes);
        }

        System.out.println("\n--- Iterative Deepening Search Complete ---");
        long overallEndTime = System.nanoTime();
        System.out.println("Total execution time: " + (overallEndTime - overallStartTime) / 1_000_000 + " ms");
        System.out.println("Total Evaluated nodes: " + evaluatedNodes);
        System.out.println("Total Pruned branches: " + prunedNodes);

        if (overallBestMove != null) {
            System.out.println("Overall Best move found: " + overallBestMove + " with value: " + overallBestValue);
        } else {
            System.out.println("No best move found across all iterations.");
        }
        return overallBestMove;
    }
}
