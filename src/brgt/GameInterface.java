package brgt;
import java.util.Arrays;
import java.util.Objects;

public class GameInterface {
    //jdk 11, no records (bruh) public record GameState(boolean gameOver, boolean moveInvalid, String winner, int[][][] board){}
    public static boolean lastMoveWasInvalid = false;

    public static GameState playerMove(String inputAlgo, String playerInp){
        String aiAlgorithm = Main.DEFAULT_AI_ALGORITHM;
        if(!lastMoveWasInvalid){ //skip ai move if last move was invalid
        //Scanner scanner = new Scanner(System.in);

        //if (args.length > 0) {
        if(!inputAlgo.equals("")){
            //String inputAlgo = args[0].toUpperCase();
            if (inputAlgo.equals("MINIMAX") || inputAlgo.equals("HISTORY") || inputAlgo.equals("MTDF")) {
                aiAlgorithm = inputAlgo;
            } else {
                //System.out.println("Invalid AI algorithm specified: " + args[0] + ". Defaulting to " + Main.DEFAULT_AI_ALGORITHM);
                System.out.println("Invalid AI algorithm specified: " + inputAlgo + ". Defaulting to " + Main.DEFAULT_AI_ALGORITHM);
            }
        } else {
            System.out.println("No AI algorithm specified. Defaulting to " + Main.DEFAULT_AI_ALGORITHM);
        }
        System.out.println("Using AI Algorithm: " + aiAlgorithm);


        //while (true) { //no game loop for playing from haskell
            System.out.println("Current board:");
            System.out.println(Main.toAsciiString());

            System.out.println("AI (" + aiAlgorithm + ") is thinking...");
            Move bestMove = null;

            switch (aiAlgorithm) {
            case "MINIMAX":
                bestMove = Minimax_AlphaBeta.findBestMove(1);
                break;
            case "HISTORY":
                bestMove = Minimax_History.findBestMove(Main.TIME_LIMIT_MS);
                break;
            case "MTDF":
                bestMove = MTDf.findBestMoveMTDf(Main.MTDF_MAX_DEPTH, Main.TIME_LIMIT_MS, Main.MTDF_INITIAL_GUESS, 1);
                break;
            default:
                System.out.println("Error: Unknown AI algorithm selected. Defaulting to History.");
                bestMove = Minimax_History.findBestMove(Main.TIME_LIMIT_MS);
                break;
            }


            if (bestMove == null) {
                System.out.println("AI has no moves left! Game over.");
                return new GameState(true, false, "player", Main.GAME_BOARD);
            }

            BlockRotator3D.placeShape(bestMove.getTransformedShape(), bestMove.x, bestMove.y, bestMove.player);

            if (aiAlgorithm.equals("MINIMAX")) {
                Minimax_AlphaBeta.player1Inventory.usePiece(Main.getPieceType(bestMove.shape));
            } else if (aiAlgorithm.equals("MTDF")) {
                MTDf.player1Inventory.usePiece(Main.getPieceType(bestMove.shape));
            } else {
                Minimax_History.player1Inventory.usePiece(Main.getPieceType(bestMove.shape));
            }

            System.out.println("AI placed a piece:");
            System.out.println(bestMove);
            System.out.println(Main.toAsciiString());

            if (Main.checkWin()){
                return new GameState(true, false, "AI", Main.GAME_BOARD);
            }
            } //skip ai move if last move was invalid
            lastMoveWasInvalid = false; //reset

            // Min Player (Human) Move
            if(false) {//if (IS_RANDOM_PLAYER_ACTIVE) {
                System.out.println("Random Player's turn. Thinking...");
                Move randomMove = Minimax_AlphaBeta.findBestMove(2); // Player 2
                if (randomMove != null) {
                    BlockRotator3D.placeShape(randomMove.getTransformedShape(), randomMove.x, randomMove.y, randomMove.player);
                    if (aiAlgorithm.equals("MINIMAX")) {
                        Minimax_AlphaBeta.player2Inventory.usePiece(Main.getPieceType(randomMove.shape));
                    } else if (aiAlgorithm.equals("MTDF")) {
                        MTDf.player2Inventory.usePiece(Main.getPieceType(randomMove.shape));
                    } else {
                        Minimax_History.player2Inventory.usePiece(Main.getPieceType(randomMove.shape));
                    }
                    System.out.println("Random Player placed a piece:");
                    System.out.println(randomMove);
                } else {
                    System.out.println("Random Player has no moves left!");
                }
            } else {
                //while (true) { //no loop for invalid player moves via haskell
                    System.out.println("Your turn! Enter piece type (T/L/Z/O), rotation index (0-23), x (0-7), y (0-7): ");
                    String input = playerInp;//scanner.nextLine();
                    String[] parts = input.trim().split("\\s+");
                    if (parts.length != 4) {
                        System.out.println("Invalid input. Try again.");
                        lastMoveWasInvalid = true; //skip AI so we can try again
                        return GameState.invalidGS();
                    }

                    String pieceType = parts[0].toUpperCase();
                    int rotationIndex = Integer.parseInt(parts[1]);
                    System.out.println("Rotation index: " + rotationIndex);
                    int x = Integer.parseInt(parts[2]);
                    int y = Integer.parseInt(parts[3]);
                    int[][] shape = null;
                    try {
                    shape = Main.getShapeFromType(pieceType);
                    } catch (Exception e){
                        System.out.println("Invalid piece. Try again.");
                        lastMoveWasInvalid = true; //skip AI so we can try again
                        return GameState.invalidGS();
                    }
                    int[][] rotationMatrix = BlockRotator3D.ROTATION_MATRICES[rotationIndex];
                    Move playerMove = new Move(shape, rotationMatrix, x, y, 2);

                    if (BlockRotator3D.placeShape(playerMove.getTransformedShape(), playerMove.x, playerMove.y, playerMove.player)) {
                        if (aiAlgorithm.equals("MINIMAX")) {
                            Minimax_AlphaBeta.player2Inventory.usePiece(Main.getPieceType(playerMove.shape));
                        } else if (aiAlgorithm.equals("MTDF")) {
                            MTDf.player2Inventory.usePiece(Main.getPieceType(playerMove.shape));
                        } else {
                            Minimax_History.player2Inventory.usePiece(Main.getPieceType(playerMove.shape));
                        }
                        //break; // Exit the loop if the move is valid
                    } else {
                        System.out.println("Invalid move. Try again.");
                    }
                //} //no loop for invalid player moves via haskell
                System.out.println("You placed a piece:");
            }
            System.out.println(Main.toAsciiString());


            if (Main.checkWin()) {
                return new GameState(true, false, "player", Main.GAME_BOARD);
            }
        //} //no game loop for playing from haskell
        //scanner.close();
        return new GameState(false, false, "NA", Main.GAME_BOARD);
    }


public static final class GameState {
    private final boolean gameOver;
    private final boolean moveInvalid;
    private final String winner;
    private final int[][][] board;

    public GameState(boolean gameOver, boolean moveInvalid, String winner, int[][][] board) {
        this.gameOver = gameOver;
        this.moveInvalid = moveInvalid;
        this.winner = winner;
        this.board = board;
    }

    public static GameState invalidGS(){
        return new GameState(false, true, "NA", Main.GAME_BOARD);
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isMoveInvalid() {
        return moveInvalid;
    }

    public String getWinner() {
        return winner;
    }

    public int[][][] getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameState gameState = (GameState) o;
        return gameOver == gameState.gameOver &&
               moveInvalid == gameState.moveInvalid &&
               Objects.equals(winner, gameState.winner) &&
               Arrays.deepEquals(board, gameState.board);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(gameOver, moveInvalid, winner);
        result = 31 * result + Arrays.deepHashCode(board);
        return result;
    }

    @Override
    public String toString() {
        return "GameState[" +
               "gameOver=" + gameOver +
               ", moveInvalid=" + moveInvalid +
               ", winner='" + winner + '\'' +
               ", board=" + Arrays.deepToString(board) +
               ']';
    }
}

}