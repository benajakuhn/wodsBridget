import java.util.List;

public class Move {
    public int[][] shape;
    public int[][] rotationMatrix;
    public int x, y;
    public int player; // 1 or 2

    public Move(int[][] shape, int[][] rotationMatrix, int x, int y, int player) {
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
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Move{");
        sb.append("pieceType=").append(Main.getPieceType(shape));
        sb.append(", rotationMatrix=").append(java.util.Arrays.deepToString(rotationMatrix));
        sb.append(", x=").append(x);
        sb.append(", y=").append(y);
        sb.append(", player=").append(player);
        sb.append('}');
        return sb.toString();
    }
}
