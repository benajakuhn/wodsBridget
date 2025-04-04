import java.util.HashMap;
import java.util.Map;

public class PieceInventory {
    private final Map<String, Integer> pieces;

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
}
