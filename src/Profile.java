public class Profile {
    public static void main(String[] args) {
        initializeBoardWithRandomMoves(5);
    }

    // init the board with random moves for both players
    public static void initializeBoardWithRandomMoves(int movesPerPlayer) {
        System.out.println("Initializing board with " + movesPerPlayer + " random moves per player...");
        Main.clearGameBoard();
        Move move;
        for (int i = 0; i < movesPerPlayer; i++) {
            move = Main.generateRandomMove(1);
            if(BlockRotator3D.placeShape(move.getTransformedShape(), move.x, move.y, 1)){
                System.out.println("Player 1 placed: " + move);
                Minimax_AlphaBeta.player1Inventory.usePiece(Main.getPieceType(move.shape));
                Minimax_History.player1Inventory.usePiece(Main.getPieceType(move.shape));
                MTDf.player2Inventory.usePiece(Main.getPieceType(move.shape));
            }
            move = Main.generateRandomMove(2);
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
}
