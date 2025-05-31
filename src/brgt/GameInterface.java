package brgt;

public class GameInterface {
    public record GameState(boolean gameOver, boolean moveInvalid, String winner, int[][][] board)
    public static boolean lastMoveWasInvalid = false;

    public static GameState playerMove(inputAlgo, String playerInp){
        if(!lastMoveWasInvalid){ //skip ai move if last move was invalid
        //Scanner scanner = new Scanner(System.in);
        String aiAlgorithm = Main.DEFAULT_AI_ALGORITHM;

        if (args.length > 0) {
            //String inputAlgo = args[0].toUpperCase();
            if (inputAlgo.equals("MINIMAX") || inputAlgo.equals("HISTORY") || inputAlgo.equals("MTDF")) {
                aiAlgorithm = inputAlgo;
            } else {
                System.out.println("Invalid AI algorithm specified: " + args[0] + ". Defaulting to " + Main.DEFAULT_AI_ALGORITHM);
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
                Minimax_AlphaBeta.player1Inventory.usePiece(getPieceType(bestMove.shape));
            } else if (aiAlgorithm.equals("MTDF")) {
                MTDf.player1Inventory.usePiece(getPieceType(bestMove.shape));
            } else {
                Minimax_History.player1Inventory.usePiece(getPieceType(bestMove.shape));
            }

            System.out.println("AI placed a piece:");
            System.out.println(bestMove);
            System.out.println(toAsciiString());

            if (checkWin()){
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
                        Minimax_AlphaBeta.player2Inventory.usePiece(getPieceType(randomMove.shape));
                    } else if (aiAlgorithm.equals("MTDF")) {
                        MTDf.player2Inventory.usePiece(getPieceType(randomMove.shape));
                    } else {
                        Minimax_History.player2Inventory.usePiece(getPieceType(randomMove.shape));
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
                        return new GameState(false, true, "NA", Main.GAME_BOARD);
                    }

                    String pieceType = parts[0].toUpperCase();
                    int rotationIndex = Integer.parseInt(parts[1]);
                    System.out.println("Rotation index: " + rotationIndex);
                    int x = Integer.parseInt(parts[2]);
                    int y = Integer.parseInt(parts[3]);

                    int[][] shape = getShapeFromType(pieceType);
                    int[][] rotationMatrix = BlockRotator3D.ROTATION_MATRICES[rotationIndex];
                    Move playerMove = new Move(shape, rotationMatrix, x, y, 2);

                    if (BlockRotator3D.placeShape(playerMove.getTransformedShape(), playerMove.x, playerMove.y, playerMove.player)) {
                        if (aiAlgorithm.equals("MINIMAX")) {
                            Minimax_AlphaBeta.player2Inventory.usePiece(getPieceType(playerMove.shape));
                        } else if (aiAlgorithm.equals("MTDF")) {
                            MTDf.player2Inventory.usePiece(getPieceType(playerMove.shape));
                        } else {
                            Minimax_History.player2Inventory.usePiece(getPieceType(playerMove.shape));
                        }
                        //break; // Exit the loop if the move is valid
                    } else {
                        System.out.println("Invalid move. Try again.");
                    }
                //} //no loop for invalid player moves via haskell
                System.out.println("You placed a piece:");
            }
            System.out.println(toAsciiString());


            if (checkWin()) {
                return new GameState(true, false, "player", Main.GAME_BOARD);
            }
        //} //no game loop for playing from haskell
        //scanner.close();
        return Main.GAME_BOARD;
    }


}