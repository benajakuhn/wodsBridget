import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays; // Required for Arrays.deepEquals

public class Minimax_History {
    private static final int ITERATIVE_DEEPENING_TARGET_DEPTH = 3; // Adjustable target depth
    public static PieceInventory player1Inventory = new PieceInventory();
    public static PieceInventory player2Inventory = new PieceInventory();

    // Counters for search statistics
    public static int evaluatedNodes = 0;
    public static int prunedNodes = 0;

    // History table: Map<MoveKey_String, Score_Integer>
    private static Map<String, Integer> historyTable = new HashMap<>();

    private static String getMoveHistoryKey(Move move) {
        String pieceType = Main.getPieceType(move.shape);
        int rotationIndex = move.rotationIndex;
        return pieceType + "_r" + rotationIndex + "_x" + move.x + "_y" + move.y;
    }


    public static int minimax(int depth, boolean maximizingPlayer, int alpha, int beta, int currentPlyFromRoot) {
        if (depth == 0 || isTerminal()) {
            return evaluate();
        }

        PieceInventory currentInventory = maximizingPlayer ? player1Inventory : player2Inventory;
        int currentPlayer = maximizingPlayer ? 1 : 2;

        List<Move> moves = generateMoves(currentInventory, currentPlayer);

        moves.sort((m1, m2) -> {
            int score1 = historyTable.getOrDefault(getMoveHistoryKey(m1), 0);
            int score2 = historyTable.getOrDefault(getMoveHistoryKey(m2), 0);
            return Integer.compare(score2, score1);
        });

        Move bestMoveForThisNode = null;

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : moves) {
                if (tryMove(move)) {
                    evaluatedNodes++;
                    currentInventory.usePiece(Main.getPieceType(move.shape));
                    int eval = minimax(depth - 1, false, alpha, beta, currentPlyFromRoot + 1);
                    undoMove(move);
                    currentInventory.returnPiece(Main.getPieceType(move.shape));

                    if (eval > maxEval) {
                        maxEval = eval;
                        bestMoveForThisNode = move;
                    }
                    alpha = Math.max(alpha, eval);
                    if (beta <= alpha) {
                        prunedNodes++;
                        String moveKey = getMoveHistoryKey(move);
                        historyTable.put(moveKey, historyTable.getOrDefault(moveKey, 0) + (int) Math.pow(2, depth));
                        return maxEval;
                    }
                }
            }
            if (bestMoveForThisNode != null) {
                String moveKey = getMoveHistoryKey(bestMoveForThisNode);
                historyTable.put(moveKey, historyTable.getOrDefault(moveKey, 0) + (int) Math.pow(2, depth));
            }
            return maxEval;
        } else { // Minimizing player
            int minEval = Integer.MAX_VALUE;
            for (Move move : moves) {
                if (tryMove(move)) {
                    evaluatedNodes++;
                    currentInventory.usePiece(Main.getPieceType(move.shape));
                    int eval = minimax(depth - 1, true, alpha, beta, currentPlyFromRoot + 1);
                    undoMove(move);
                    currentInventory.returnPiece(Main.getPieceType(move.shape));

                    if (eval < minEval) {
                        minEval = eval;
                        bestMoveForThisNode = move;
                    }
                    beta = Math.min(beta, eval);
                    if (beta <= alpha) {
                        prunedNodes++;
                        String moveKey = getMoveHistoryKey(move);
                        historyTable.put(moveKey, historyTable.getOrDefault(moveKey, 0) + (int) Math.pow(2, depth));
                        return minEval;
                    }
                }
            }
            if (bestMoveForThisNode != null) {
                String moveKey = getMoveHistoryKey(bestMoveForThisNode);
                historyTable.put(moveKey, historyTable.getOrDefault(moveKey, 0) + (int) Math.pow(2, depth));
            }
            return minEval;
        }
    }


    private static boolean tryMove(Move move) {
        List<int[]> transformedShapeList = move.getTransformedShape();

        return BlockRotator3D.placeShape(
            transformedShapeList,
            move.x,
            move.y,
            move.player
        );
    }

    private static void undoMove(Move move) {
        List<int[]> transformedShapeList = move.getTransformedShape();

        BlockRotator3D.removeShape(
            transformedShapeList, // Pass the converted int[][]
            move.x,
            move.y
        );
    }


    public static List<Move> generateMoves(PieceInventory inventory, int player) {
        List<Move> moves = new ArrayList<>();

        if (inventory.hasPiece("T")) {
            int[][] shape = Main.tBlockShape;
            for (int rotationIndex : BlockRotator3D.TBLOCK_ROTATION_INDICES) {
                addMoves(moves, shape, BlockRotator3D.ROTATION_MATRICES[rotationIndex], player, rotationIndex);
            }
        }
        if (inventory.hasPiece("L")) {
            int[][] shape = Main.lBlockShape;
            for (int rotationIndex : BlockRotator3D.LBLOCK_ROTATION_INDICES) {
                addMoves(moves, shape, BlockRotator3D.ROTATION_MATRICES[rotationIndex], player, rotationIndex);
            }
        }
        if (inventory.hasPiece("Z")) {
            int[][] shape = Main.zBlockShape;
            for (int rotationIndex : BlockRotator3D.ZBLOCK_ROTATION_INDICES) {
                addMoves(moves, shape, BlockRotator3D.ROTATION_MATRICES[rotationIndex], player, rotationIndex);
            }
        }
        if (inventory.hasPiece("O")) {
            int[][] shape = Main.oBlockShape;
            for (int rotationIndex : BlockRotator3D.OBLOCK_ROTATION_INDICES) {
                addMoves(moves, shape, BlockRotator3D.ROTATION_MATRICES[rotationIndex], player, rotationIndex);
            }
        }
        return moves;
    }

    private static void addMoves(List<Move> moves, int[][] originalShape, int[][] rotationMatrix, int player, int rotationIndex) { // MODIFIED SIGNATURE
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                moves.add(new Move(rotationIndex, originalShape, rotationMatrix, x, y, player));
            }
        }
    }

    private static boolean isTerminal() {
        return player1Inventory.isEmpty() && player2Inventory.isEmpty();
    }

    private static int evaluate() {
        GameChecker.Result maxPlayerResult = GameChecker.checkPlayer(Main.flattenTopView(), 1);
        GameChecker.Result minPlayerResult = GameChecker.checkPlayer(Main.flattenTopView(), 2);

        if (maxPlayerResult.hasWon) {
            return 1000;
        } else if (minPlayerResult.hasWon) {
            return -1000;
        } else {
            return maxPlayerResult.longestPath - minPlayerResult.longestPath;
        }
    }

    public static Move findBestMove() {
        long overallStartTime = System.nanoTime();
        evaluatedNodes = 0;
        prunedNodes = 0;
        historyTable.clear();

        Move overallBestMove = null;
        int overallBestValue = Integer.MIN_VALUE;

        System.out.println("Starting Iterative Deepening Search...");

        for (int currentDepthIteration = 1; currentDepthIteration <= ITERATIVE_DEEPENING_TARGET_DEPTH; currentDepthIteration++) {
            long iterationStartTime = System.nanoTime();
            System.out.println("\n--- Iteration Depth: " + currentDepthIteration + " ---");

            int iterationBestValue = Integer.MIN_VALUE;
            Move iterationBestMove = null;

            PieceInventory currentInventory = player1Inventory;
            int currentPlayer = 1;

            List<Move> moves = generateMoves(currentInventory, currentPlayer);
            moves.sort((m1, m2) -> {
                int score1 = historyTable.getOrDefault(getMoveHistoryKey(m1), 0);
                int score2 = historyTable.getOrDefault(getMoveHistoryKey(m2), 0);
                return Integer.compare(score2, score1);
            });

            int totalMovesAtThisLevel = moves.size();
            int currentMoveIndex = 0;

            for (Move move : moves) {
                currentMoveIndex++;
                if (tryMove(move)) {
                    currentInventory.usePiece(Main.getPieceType(move.shape));
                    int moveValue = minimax(currentDepthIteration - 1, false, Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
                    undoMove(move);
                    currentInventory.returnPiece(Main.getPieceType(move.shape));

                    if (moveValue > iterationBestValue) {
                        iterationBestValue = moveValue;
                        iterationBestMove = move;
                    }
                }
                if (totalMovesAtThisLevel > 0) {
                    int progress = (currentMoveIndex * 100) / totalMovesAtThisLevel;
                    System.out.print("\rDepth " + currentDepthIteration + " Progress: [" + String.join("", Collections.nCopies(progress / 2, "=")) + String.join("", Collections.nCopies(50 - progress / 2, " ")) + "] " + progress + "%");
                }
            }
            System.out.println();

            if (iterationBestMove != null) {
                overallBestMove = iterationBestMove;
                overallBestValue = iterationBestValue;
                System.out.println("Depth " + currentDepthIteration + ": Best move found: " + iterationBestMove + " with value: " + iterationBestValue);
            } else {
                System.out.println("Depth " + currentDepthIteration + ": No valid moves found or all pruned at this depth.");
            }
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
