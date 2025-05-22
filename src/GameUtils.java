import java.util.ArrayList;
import java.util.List;

public class GameUtils {
    public static String getMoveHistoryKey(Move move) {
        String pieceType = Main.getPieceType(move.shape);
        int rotationIndex = move.rotationIndex;
        return pieceType + "_r" + rotationIndex + "_x" + move.x + "_y" + move.y;
    }

    public static boolean tryMove(Move move) {
        List<int[]> transformedShapeList = move.getTransformedShape();

        return BlockRotator3D.placeShape(
            transformedShapeList,
            move.x,
            move.y,
            move.player
        );
    }

    public static void undoMove(Move move) {
        List<int[]> transformedShapeList = move.getTransformedShape();

        BlockRotator3D.removeShape(
            transformedShapeList,
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

    public static void addMoves(List<Move> moves, int[][] originalShape, int[][] rotationMatrix, int player, int rotationIndex) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                moves.add(new Move(rotationIndex, originalShape, rotationMatrix, x, y, player));
            }
        }
    }

    public static boolean isTerminal(PieceInventory player1Inventory, PieceInventory player2Inventory) {
        return player1Inventory.isEmpty() && player2Inventory.isEmpty();
    }

    public static int evaluate(PieceInventory player1Inventory, PieceInventory player2Inventory) {
        GameChecker.Result maxPlayerResult = GameChecker.checkPlayer(Main.flattenTopView(), 1);
        GameChecker.Result minPlayerResult = GameChecker.checkPlayer(Main.flattenTopView(), 2);

        if (maxPlayerResult.hasWon) {
            return 1000;
        } else if (minPlayerResult.hasWon) {
            return -1000;
        } else {
            int score = player1Inventory.calculateInventoryScore() - player2Inventory.calculateInventoryScore();
            score += ((maxPlayerResult.longestPath - minPlayerResult.longestPath)*10);
            return score;
        }
    }
}
