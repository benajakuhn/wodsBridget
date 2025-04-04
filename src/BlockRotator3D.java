import java.util.ArrayList;
import java.util.List;

public class BlockRotator3D {
    private static final int[][][] ROTATION_MATRICES = generateRotationMatrices();

    public static void placeAllRotations(int[][] shape, int originX, int originY, int player) {
        for (int[][] rotation : ROTATION_MATRICES) {
            List<int[]> transformed = applyRotation(shape, rotation);
            if(placeShape(transformed, originX, originY, player)){
                System.out.println(Main.toAsciiString());
                Main.clearGameBoard();
            }

        }
    }

    // Applies a rotation matrix to the shape's points
    private static List<int[]> applyRotation(int[][] shape, int[][] matrix) {
        List<int[]> result = new ArrayList<>();
        for (int[] point : shape) {
            int x = matrix[0][0] * point[0] + matrix[0][1] * point[1] + matrix[0][2] * point[2];
            int y = matrix[1][0] * point[0] + matrix[1][1] * point[1] + matrix[1][2] * point[2];
            int z = matrix[2][0] * point[0] + matrix[2][1] * point[1] + matrix[2][2] * point[2];
            result.add(new int[] {x, y, z});
        }
        return result;
    }

    // Places points of a shape on the game board
    private static boolean placeShape(List<int[]> points, int originX, int originY, int player) {
        int originZ = 0;
        List<int[]> placed = new ArrayList<>();
        boolean validPlacement = true;

        // set the correct z value
        checkZ(points);

        for (int[] p : points) {
            int x = originX + p[0];
            int y = originY + p[1];
            int z = originZ + p[2];
            if ((x >= 0 && x < 8 && y >= 0 && y < 8 && z < 3) && (Main.GAME_BOARD[x][y][z] == 0)) {
                placed.add(p);
                Main.GAME_BOARD[x][y][z] = player;
            } else {
                validPlacement = false;
            }
        }

        for (int[] p : placed) {
            int x = originX + p[0];
            int y = originY + p[1];
            int z = originZ + p[2];

            while (z > 0){
                z--;
                if (Main.GAME_BOARD[x][y][z] == 0) {
                    validPlacement = false;
                    break;
                }
            }
        }


        if (!validPlacement) {
            // If placement is invalid, remove the shape from the game board
            removeShape(placed, originX, originY);
            return false;
        } else {
            return true;
        }
    }

    private static void removeShape(List<int[]> points, int originX, int originY) {
        int originZ = 0;
        checkZ(points);

        for (int[] p : points) {
            int x = originX + p[0];
            int y = originY + p[1];
            int z = originZ + p[2];

            Main.GAME_BOARD[x][y][z] = 0;
        }
    }


    private static void checkZ(List<int[]> points) {
        boolean negativeZ = false;
        for (int[] p : points) {
            if (p[2] < 0) {
                negativeZ = true;
                break;
            }
        }
        if (negativeZ) {
            for (int[] p : points) {
                p[2] = p[2] + 1;
            }
        }
    }

    private static int[][][] generateRotationMatrices() {
        // 24 rotation matrices representing all possible rotations of a cube
        return new int[][][] {
            // identity
            {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}}, // No rotation
            {{1, 0, 0}, {0, 0, 1}, {0, -1, 0}}, // 90 degrees around X-axis
            {{1, 0, 0}, {0, -1, 0}, {0, 0, -1}}, // 180 degrees around X-axis
            {{1, 0, 0}, {0, 0, -1}, {0, 1, 0}}, // 270 degrees around X-axis

            {{0, 1, 0}, {-1, 0, 0}, {0, 0, 1}}, // 90 degrees around Y-axis
            {{0, 0, 1}, {-1, 0, 0}, {0, 1, 0}}, // 90 degrees around Y-axis, then 90 degrees around Z-axis
            {{0, -1, 0}, {-1, 0, 0}, {0, 0, -1}}, // 90 degrees around Y-axis, then 180 degrees around Z-axis
            {{0, 0, -1}, {-1, 0, 0}, {0, -1, 0}}, // 90 degrees around Y-axis, then 270 degrees around Z-axis

            {{-1, 0, 0}, {0, -1, 0}, {0, 0, 1}}, // 180 degrees around Y-axis
            {{-1, 0, 0}, {0, 0, 1}, {0, 1, 0}}, // 180 degrees around Y-axis, then 90 degrees around Z-axis
            {{-1, 0, 0}, {0, 1, 0}, {0, 0, -1}}, // 180 degrees around Y-axis, then 180 degrees around Z-axis
            {{-1, 0, 0}, {0, 0, -1}, {0, -1, 0}}, // 180 degrees around Y-axis, then 270 degrees around Z-axis

            {{0, -1, 0}, {1, 0, 0}, {0, 0, 1}}, // 270 degrees around Y-axis
            {{0, 0, 1}, {1, 0, 0}, {0, -1, 0}}, // 270 degrees around Y-axis, then 90 degrees around Z-axis
            {{0, 1, 0}, {1, 0, 0}, {0, 0, -1}}, // 270 degrees around Y-axis, then 180 degrees around Z-axis
            {{0, 0, -1}, {1, 0, 0}, {0, 1, 0}}, // 270 degrees around Y-axis, then 270 degrees around Z-axis

            {{0, 0, -1}, {0, 1, 0}, {1, 0, 0}}, // 90 degrees around Z-axis
            {{0, 1, 0}, {0, 0, 1}, {1, 0, 0}}, // 90 degrees around Z-axis, then 90 degrees around X-axis
            {{0, 0, 1}, {0, -1, 0}, {1, 0, 0}}, // 90 degrees around Z-axis, then 180 degrees around X-axis
            {{0, -1, 0}, {0, 0, -1}, {1, 0, 0}}, // 90 degrees around Z-axis, then 270 degrees around X-axis

            {{0, 0, -1}, {0, -1, 0}, {-1, 0, 0}}, // 270 degrees around Z-axis
            {{0, -1, 0}, {0, 0, 1}, {-1, 0, 0}}, // 270 degrees around Z-axis, then 90 degrees around X-axis
            {{0, 0, 1}, {0, 1, 0}, {-1, 0, 0}}, // 270 degrees around Z-axis, then 180 degrees around X-axis
            {{0, 1, 0}, {0, 0, -1}, {-1, 0, 0}}, // 270 degrees around Z-axis, then 270 degrees around X-axis
        };
    }
}


