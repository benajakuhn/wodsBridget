import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MTDf {

    // Transposition Table Entry
    static class TTEntry {
        int score;
        enum BoundType { EXACT, LOWER, UPPER }
        BoundType boundType;
        int depth;
        Move bestMove; // Store the best move found at this node for this entry

        public TTEntry(int score, BoundType boundType, int depth, Move bestMove) {
            this.score = score;
            this.boundType = boundType;
            this.depth = depth;
            this.bestMove = bestMove;
        }
    }

    private static Map<Long, TTEntry> transpositionTable = new HashMap<>(); // Key: Game state hash

    private static Map<String, Integer> historyTable = new HashMap<>();

    // Statistics
    public static long evaluatedNodes = 0;
    public static long ttHits = 0;
    public static long prunedNodes = 0;

    public static long total_evaluatedNodes = 0;
    public static long total_ttHits = 0;
    public static long depthReached = 0;
    public static long total_time = 0;

    // Constants for score bounds
    private static final int INFINITY = Integer.MAX_VALUE;
    private static final int NEGATIVE_INFINITY = Integer.MIN_VALUE;

    private static long overallStartTime = 0;
    private static long timeLimitMillis = 0;

    public static PieceInventory player1Inventory = new PieceInventory();
    public static PieceInventory player2Inventory = new PieceInventory();

    // Hashed string representation of the game state
    private static Long getGameStateKey() {
        return (long) Main.toAsciiString().hashCode();
    }


    // Helper class to return score and best move from MT
    private static class MT_Result {
        int score;
        Move bestMove;
        MT_Result(int score, Move bestMove) {
            this.score = score;
            this.bestMove = bestMove;
        }
    }

    /**
     * Memory-enhanced Test (MT) function.
     * Implemented as a null-window Alpha-Beta search.
     *
     * @param depth            Remaining depth to search.
     * @param maximizingPlayer True if the current player is maximizing, false otherwise.
     * @param alpha            The alpha value for the search window.
     * @param beta             The beta value for the search window (for MTD(f), beta = alpha + 1).
     * @param ply              Current ply from root (for history table depth weighting, if used)
     * @return The score resulting from the search.
     */
    private static MT_Result memoryEnhancedTest(int depth, boolean maximizingPlayer, int alpha, int beta, int ply) {
        evaluatedNodes++;
        Move bestMoveForThisNode = null;
        int originalAlpha = alpha; // For TT bound type determination

        // Transposition Table Lookup
        Long gameStateKey = getGameStateKey();
        TTEntry ttEntry = transpositionTable.get(gameStateKey);

        // Check time limit
        if (((System.nanoTime() - overallStartTime) / 1_000_000) >= timeLimitMillis) {
            Move bestMove = (ttEntry != null) ? ttEntry.bestMove : null;
            int score = maximizingPlayer ? alpha : beta; // Return bound based on player
            return new MT_Result(score, bestMove);
        }

        if (ttEntry != null && ttEntry.depth >= depth) {
            ttHits++;
            if (ttEntry.boundType == TTEntry.BoundType.EXACT) {
                return new MT_Result(ttEntry.score, ttEntry.bestMove);
            } else if (ttEntry.boundType == TTEntry.BoundType.LOWER) {
                if (ttEntry.score >= beta) return new MT_Result(ttEntry.score, ttEntry.bestMove);
                alpha = Math.max(alpha, ttEntry.score);
            } else if (ttEntry.boundType == TTEntry.BoundType.UPPER) {
                if (ttEntry.score <= alpha) return new MT_Result(ttEntry.score, ttEntry.bestMove);
                beta = Math.min(beta, ttEntry.score);
            }
            if (alpha >= beta) {
                return new MT_Result(ttEntry.score, ttEntry.bestMove); // Or the bound that caused the cutoff
            }
        }


        // Leaf node or terminal state check
        if (depth == 0 || GameUtils.isTerminal(player1Inventory, player2Inventory)) { // Assuming isTerminal is accessible
            return new MT_Result(GameUtils.evaluate(player1Inventory, player2Inventory), null); // Assuming evaluate is accessible
        }

        PieceInventory currentInventory = maximizingPlayer ? player1Inventory : player2Inventory;
        int currentPlayer = maximizingPlayer ? 1 : 2;
        List<Move> moves = GameUtils.generateMoves(currentInventory, currentPlayer); // Assuming generateMoves

        // Move Ordering (TT move first, then history heuristic)
        if (ttEntry != null && ttEntry.bestMove != null) {
            if(moves.remove(ttEntry.bestMove)) {
                moves.add(0, ttEntry.bestMove);
            }
        }

        moves.sort((m1, m2) -> {
            int score1 = historyTable.getOrDefault(GameUtils.getMoveHistoryKey(m1), 0);
            int score2 = historyTable.getOrDefault(GameUtils.getMoveHistoryKey(m2), 0);
            return Integer.compare(score2, score1);
        });


        if (maximizingPlayer) {
            int maxEval = NEGATIVE_INFINITY;
            for (Move move : moves) {
                if (GameUtils.tryMove(move)) {
                    currentInventory.usePiece(Main.getPieceType(move.shape));

                    MT_Result result = memoryEnhancedTest(depth - 1, false, alpha, beta, ply + 1);

                    GameUtils.undoMove(move);
                    currentInventory.returnPiece(Main.getPieceType(move.shape));

                    if (result.score > maxEval) {
                        maxEval = result.score;
                        bestMoveForThisNode = move;
                    }
                    alpha = Math.max(alpha, maxEval);
                    if (beta <= alpha) {
                        prunedNodes++;
                        String moveKey = GameUtils.getMoveHistoryKey(move);
                        historyTable.put(moveKey, historyTable.getOrDefault(moveKey, 0) + (1 << depth)); // Depth-weighted
                        break;
                    }
                }
            }
            // Store in Transposition Table
            TTEntry.BoundType newBoundType;
            if (maxEval <= originalAlpha) {
                newBoundType = TTEntry.BoundType.UPPER;
            } else if (maxEval >= beta) {
                newBoundType = TTEntry.BoundType.LOWER;
            } else {
                newBoundType = TTEntry.BoundType.EXACT;
            }
            transpositionTable.put(gameStateKey, new TTEntry(maxEval, newBoundType, depth, bestMoveForThisNode));
            return new MT_Result(maxEval, bestMoveForThisNode);

        } else { // Minimizing player
            int minEval = INFINITY;
            for (Move move : moves) {
                if (GameUtils.tryMove(move)) {
                    currentInventory.usePiece(Main.getPieceType(move.shape));

                    MT_Result result = memoryEnhancedTest(depth - 1, true, alpha, beta, ply + 1);

                    GameUtils.undoMove(move);
                    currentInventory.returnPiece(Main.getPieceType(move.shape));

                    if (result.score < minEval) {
                        minEval = result.score;
                        bestMoveForThisNode = move;
                    }
                    beta = Math.min(beta, minEval);
                    if (beta <= alpha) { // Alpha cutoff
                        prunedNodes++;
                        String moveKey = GameUtils.getMoveHistoryKey(move);
                        historyTable.put(moveKey, historyTable.getOrDefault(moveKey, 0) + (1 << depth));
                        break;
                    }
                }
            }
            TTEntry.BoundType newBoundType;
            if (minEval <= originalAlpha) {
                newBoundType = TTEntry.BoundType.UPPER;
            } else if (minEval >= beta) {
                newBoundType = TTEntry.BoundType.LOWER;
            } else {
                newBoundType = TTEntry.BoundType.EXACT;
            }
            transpositionTable.put(gameStateKey, new TTEntry(minEval, newBoundType, depth, bestMoveForThisNode));
            return new MT_Result(minEval, bestMoveForThisNode);
        }
    }



    /**
     * Finds the best move using MTD(f) with iterative deepening.
     *
     * @param maxSearchDepth The maximum depth for iterative deepening.
     * @param l_timeLimitMillis Time limit for the search.
     * @param initialAspirationGuess The guess for the minimax score, typically from the previous iteration's result.
     * @param player The current player (1 for Max, 2 for Min).
     * @return The best Move found.
     */
    public static Move findBestMoveMTDf(int maxSearchDepth, long l_timeLimitMillis, int initialAspirationGuess, int player) {
        total_evaluatedNodes = 0;
        evaluatedNodes = 0;
        prunedNodes = 0;
        depthReached = 0;
        total_time = 0;
        total_ttHits = 0;
        historyTable.clear();
        transpositionTable.clear();

        timeLimitMillis = l_timeLimitMillis;
        overallStartTime = System.nanoTime();
        Move overallBestMove = null;
        int bestScoreSoFar = (player == 1) ? NEGATIVE_INFINITY : INFINITY; // Max player aims high, Min player aims low

        System.out.println("Starting MTD(f) Iterative Deepening Search...");

        int currentGuess = initialAspirationGuess;

        for (int currentDepth = 1; currentDepth <= maxSearchDepth; currentDepth++) {
            long iterationStartTime = System.nanoTime();
            evaluatedNodes = 0;
            ttHits = 0;
            depthReached = currentDepth;
            // historyTable.clear(); // Optionally clear history table per iteration or keep it cumulative

//            System.out.println("\n--- MTD(f) Iteration Depth: " + currentDepth + ", Initial Guess: " + currentGuess + " ---");

            // MTD(f) loop for the current depth
            int lowerBound = NEGATIVE_INFINITY;
            int upperBound = INFINITY;
            int f = currentGuess; // 'f' is the value being tested/refined

            Move bestMoveThisDepth = null;

            do {
                int beta = f; // The 'bound' to test against in MTD(f)
                // MT is called with a null window around beta: (beta-1, beta)

//                System.out.println("  MTD(f) call with beta (test bound) = " + beta);

                // MT_Result contains the score and the best move found for that specific MT call
                MT_Result result = memoryEnhancedTest(currentDepth, player == 1, beta - 1, beta, 0);

                if (result.score < beta) { // Failed low, found an upper bound
                    upperBound = result.score;
                    f = result.score; // Next test is this new upper bound
                    if (player == 1 && result.score > bestScoreSoFar) { // Maximizing player
                        // bestMoveThisDepth = result.bestMove; // Update if this path is better
                    } else if (player == 2 && result.score < bestScoreSoFar) { // Minimizing player
                        // bestMoveThisDepth = result.bestMove;
                    }
                    bestMoveThisDepth = result.bestMove; // Always update based on last successful bound
                } else { // Failed high (or exact match), found a lower bound
                    lowerBound = result.score;
                    f = result.score + 1; // Next test is one above this new lower bound
                    if (player == 1 && result.score > bestScoreSoFar) {
                        bestScoreSoFar = result.score;
                        bestMoveThisDepth = result.bestMove;
                    } else if (player == 2 && result.score < bestScoreSoFar) {
                        bestScoreSoFar = result.score;
                        bestMoveThisDepth = result.bestMove;
                    }
                }
//                System.out.println("    MT returned: " + result.score + ", New bounds: [" + lowerBound + ", " + upperBound + "], Next f: " + f);

            } while (lowerBound < upperBound);

            currentGuess = lowerBound; // The converged minimax value for this depth
            overallBestMove = bestMoveThisDepth; // Best move for this converged value
            bestScoreSoFar = currentGuess;

            total_evaluatedNodes += evaluatedNodes;
            total_ttHits += ttHits;

//            long iterationEndTime = System.nanoTime();
//            System.out.println("Depth " + currentDepth + ": Converged Value = " + currentGuess + ", Best Move: " + overallBestMove);
//            System.out.println("Depth " + currentDepth + " time: " + (iterationEndTime - iterationStartTime) / 1_000_000 + " ms");
//            System.out.println("Evaluated Nodes (this depth): " + evaluatedNodes + ", TT Hits: " + ttHits);

            // Check time limit
            if (((System.nanoTime() - overallStartTime) / 1_000_000) >= timeLimitMillis && currentDepth < maxSearchDepth) {
                System.out.println("Time limit reached. Breaking iterative deepening.");
                break;
            }
        }

        System.out.println("\n--- MTD(f) Iterative Deepening Search Complete ---");
        long overallEndTime = System.nanoTime();
        total_time = (overallEndTime - overallStartTime) / 1_000_000;
        System.out.println("Total execution time: " + total_time + " ms");
        System.out.println("Total Evaluated nodes: " + total_evaluatedNodes);
        System.out.println("Total pruned branches: " + prunedNodes);
        System.out.println("Total TT Hits: " + total_ttHits);
        System.out.println("Depth reached: " + depthReached);
        if (overallBestMove != null) {
            System.out.println("Overall Best move found: " + overallBestMove + " with value: " + currentGuess);
        } else {
            System.out.println("No best move found.");
        }
        return overallBestMove;
    }
}
