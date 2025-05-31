package brgt;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Move {
    public int[][] shape;
    public int[][] rotationMatrix;
    public int x, y;
    public int player; // 1 or 2
    public int rotationIndex; // Field exists

    public Move(int[][] shape, int[][] rotationMatrix, int x, int y, int player) {
        this.shape = shape;
        this.rotationMatrix = rotationMatrix;
        this.x = x;
        this.y = y;
        this.player = player;
        this.rotationIndex = -1;
    }

    public Move(int rotationIndex, int[][] shape, int[][] rotationMatrix, int x, int y, int player) {
        this.rotationIndex = rotationIndex;
        this.shape = shape;
        this.rotationMatrix = rotationMatrix;
        this.x = x;
        this.y = y;
        this.player = player;
    }

    public List<int[]> getTransformedShape() {
        return BlockRotator3D.applyRotation(shape, rotationMatrix);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return x == move.x &&
            y == move.y &&
            player == move.player &&
            rotationIndex == move.rotationIndex &&
            Arrays.deepEquals(shape, move.shape);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(x, y, player, rotationIndex);
        result = 31 * result + Arrays.deepHashCode(shape);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("pieceType=").append(Main.getPieceType(shape));

        sb.append(", rotationIndex=").append(this.rotationIndex);

        sb.append(", x=").append(x);
        sb.append(", y=").append(y);
        sb.append(", player=").append(player);
        return sb.toString();
    }
}