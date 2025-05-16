import java.util.HashMap;
import java.util.Map;

public class PieceInventory {
    private final Map<String, Integer> pieces;
    private static final int L_PIECE_VALUE = 8;
    private static final int T_PIECE_PRESENCE_VALUE = 5;
    private static final int Z_PIECE_PRESENCE_VALUE = 5;
    private static final int O_PIECE_PRESENCE_VALUE = 5;

    public PieceInventory() {
        pieces = new HashMap<>();
        pieces.put("T", 4);
        pieces.put("L", 4);
        pieces.put("Z", 4);
        pieces.put("O", 2);
    }

    public boolean hasPiece(String type) {
        return pieces.getOrDefault(type, 0) > 0;
    }

    public void usePiece(String type) {
        pieces.put(type, pieces.get(type) - 1);
    }

    public void returnPiece(String type) {
        pieces.put(type, pieces.get(type) + 1);
    }

    public boolean isEmpty() {
        return pieces.values().stream().allMatch(count -> count == 0);
    }

    public int getPieceCount(String type) {
        return pieces.getOrDefault(type, 0);
    }

    public int calculateInventoryScore() {
        int score = 0;

        // Score for L pieces (quantity matters)
        score += this.getPieceCount("L") * L_PIECE_VALUE;

        // Score for presence of other pieces (diversity matters)
        if (this.getPieceCount("T") > 0) {
            score += T_PIECE_PRESENCE_VALUE;
        }
        if (this.getPieceCount("Z") > 0) {
            score += Z_PIECE_PRESENCE_VALUE;
        }
        if (this.getPieceCount("O") > 0) {
            score += O_PIECE_PRESENCE_VALUE;
        }
        return score;
    }
}
