import java.util.ArrayList;
import java.util.List;

public class Minimax {

    private static final int MAX_DEPTH = 3; // Adjustable
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
                    currentInventory.usePiece(getPieceType(move.shape));
                    int eval = minimax(depth - 1, false);
                    undoMove(move);
                    currentInventory.returnPiece(getPieceType(move.shape));
                    maxEval = Math.max(maxEval, eval);
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : generateMoves(currentInventory, currentPlayer)) {
                if (tryMove(move)) {
                    currentInventory.usePiece(getPieceType(move.shape));
                    int eval = minimax(depth - 1, true);
                    undoMove(move);
                    currentInventory.returnPiece(getPieceType(move.shape));
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
        // TODO: Implement a terminal state check
        return false;
    }

    private static int evaluate() {
        // TODO: Implement a heuristic evaluation function
        return 0;
    }

    private static String getPieceType(int[][] shape) {
        if (shape == Main.tBlockShape) return "T";
        if (shape == Main.lBlockShape) return "L";
        if (shape == Main.zBlockShape) return "Z";
        if (shape == Main.oBlockShape) return "O";
        throw new IllegalArgumentException("Unknown piece type!");
    }
}
