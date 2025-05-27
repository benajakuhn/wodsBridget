import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Profile {
    public static void main(String[] args) {
        int timeLimitMs = 15_000;
        int depthLimit = 1;

        // Initialize the game board and inventories
        initializeBoardWithRandomMoves(4);

        // Write header to CSV files
        writeToCSV("TimeProfiling.csv", "Algorithm", "Time (ms)", "Evaluated Nodes", "Pruned Nodes", "Depth Reached", "Total Time (ms)", "TT Hits");
        writeToCSV("DepthProfiling.csv", "Algorithm", "Depth", "Evaluated Nodes", "Pruned Nodes", "Total Time (ms)", "TT Hits");

        for (int i = 0; i < 100; i++) {
            System.out.println();
            System.out.println("-----------------------------------Starting profiling with time limit: "+ timeLimitMs +"ms------------------------------------");
            getMoveByTime(timeLimitMs);

            if (depthLimit <= 3 || timeLimitMs > 600_000) {
                System.out.println();
                System.out.println("-----------------------------------Starting profiling with depth limit: "+ depthLimit +"------------------------------------");
                getMoveByDepth(depthLimit);
                depthLimit = depthLimit + 1;
            }

            timeLimitMs = timeLimitMs * 2;
        }
    }

    // init the board with random moves for both players
    public static void initializeBoardWithRandomMoves(int movesPerPlayer) {
        System.out.println("Initializing board with " + movesPerPlayer + " random moves per player...");
        Main.clearGameBoard();
        Move move;
        for (int i = 0; i < movesPerPlayer; i++) {
            move = Minimax_AlphaBeta.findBestMove(1);
            if(BlockRotator3D.placeShape(move.getTransformedShape(), move.x, move.y, 1)){
                System.out.println("Player 1 placed: " + move);
                Minimax_AlphaBeta.player1Inventory.usePiece(Main.getPieceType(move.shape));
                Minimax_History.player1Inventory.usePiece(Main.getPieceType(move.shape));
                MTDf.player2Inventory.usePiece(Main.getPieceType(move.shape));
            }
            move = Minimax_AlphaBeta.findBestMove(2);
            if(BlockRotator3D.placeShape(move.getTransformedShape(), move.x, move.y, 2)){
                System.out.println("Player 2 placed: " + move);
                Minimax_AlphaBeta.player2Inventory.usePiece(Main.getPieceType(move.shape));
                Minimax_History.player2Inventory.usePiece(Main.getPieceType(move.shape));
                MTDf.player1Inventory.usePiece(Main.getPieceType(move.shape));
            }
        }

        // --- Final Board State and Inventories ---
        System.out.println("\n--- Board initialization complete ---");
        System.out.println(Main.toAsciiString());

        // Print inventory status.
        System.out.println("Player 1 Inventory after setup:");
        System.out.println("  T: " + Minimax_History.player1Inventory.getPieceCount("T") +
            ", L: " + Minimax_History.player1Inventory.getPieceCount("L") +
            ", Z: " + Minimax_History.player1Inventory.getPieceCount("Z") +
            ", O: " + Minimax_History.player1Inventory.getPieceCount("O"));

        System.out.println("Player 2 Inventory after setup:");
        System.out.println("  T: " + Minimax_History.player2Inventory.getPieceCount("T") +
            ", L: " + Minimax_History.player2Inventory.getPieceCount("L") +
            ", Z: " + Minimax_History.player2Inventory.getPieceCount("Z") +
            ", O: " + Minimax_History.player2Inventory.getPieceCount("O"));
    }
    public static void getMoveByDepth(int depth) {
        System.out.println("\nStarting Minimax AlphaBeta search with depth: " + depth);
        Minimax_AlphaBeta.MAX_DEPTH = depth;
        Minimax_AlphaBeta.findBestMove(1);
        writeToCSV("DepthProfiling.csv", "Minimax AlphaBeta", depth, Minimax_AlphaBeta.evaluatedNodes, Minimax_AlphaBeta.prunedNodes, Minimax_AlphaBeta.total_time);

        System.out.println("\nStarting Minimax History search with depth: " + depth);
        Minimax_History.MAX_DEPTH = depth;
        Minimax_History.findBestMove(Integer.MAX_VALUE);
        writeToCSV("DepthProfiling.csv", "Minimax History", depth, Minimax_History.evaluatedNodes, Minimax_History.prunedNodes, Minimax_History.total_time);

        System.out.println("\nStarting MTD-f search with depth: " + depth);
        MTDf.findBestMoveMTDf(depth, Integer.MAX_VALUE, 0, 1);
        writeToCSV("DepthProfiling.csv", "MTD-f", depth, MTDf.total_evaluatedNodes, MTDf.prunedNodes, MTDf.total_time, MTDf.total_ttHits);
    }

    public static void getMoveByTime(int timeInMillis) {
        System.out.println("\nStarting Minimax History search with time limit: " + timeInMillis + " ms");
        Minimax_History.MAX_DEPTH = Integer.MAX_VALUE;
        Minimax_History.findBestMove(timeInMillis);
        writeToCSV("TimeProfiling.csv", "Minimax History",timeInMillis,Minimax_History.evaluatedNodes, Minimax_History.prunedNodes, Minimax_History.depthReached, Minimax_History.total_time);

        System.out.println("\nStarting MTD-f search with time limit: " + timeInMillis + " ms");
        MTDf.findBestMoveMTDf(Integer.MAX_VALUE, timeInMillis, 0, 1);
        writeToCSV("TimeProfiling.csv", "MTD-f", timeInMillis, MTDf.total_evaluatedNodes, MTDf.prunedNodes, MTDf.depthReached, MTDf.total_time, MTDf.total_ttHits);
    }


    public static void writeToCSV(String filename, Object... parameters) {
        try {
            File file = new File("data", filename);
            boolean fileExists = file.exists();

            // Create directory if it doesn't exist
            File directory = file.getParentFile();
            if (directory != null && !directory.exists()) {
                directory.mkdirs();
            }

            // Open file in append mode
            try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
                // Convert parameters to comma-separated string
                String line = Arrays.stream(parameters)
                    .map(Object::toString)
                    .collect(Collectors.joining(","));

                // Write the line to the CSV file
                writer.println(line);
            }

            if (!fileExists) {
                System.out.println("Created new CSV file: " + filename);
            }

        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }
}
