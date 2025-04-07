import java.util.*;

public class BlockRotator3D {
    public static final int[][][] ROTATION_MATRICES = generateRotationMatrices();
    public static final int[] TBLOCK_ROTATION_INDICES = getAllRotationIndices(Main.tBlockShape);
    public static final int[] LBLOCK_ROTATION_INDICES = getAllRotationIndices(Main.lBlockShape);
    public static final int[] ZBLOCK_ROTATION_INDICES = getAllRotationIndices(Main.zBlockShape);
    public static final int[] OBLOCK_ROTATION_INDICES = getAllRotationIndices(Main.oBlockShape);

    public static void placeAllRotations(int[][] shape, int originX, int originY, int player) {
        for (int[][] rotation : ROTATION_MATRICES) {
            List<int[]> transformed = applyRotation(shape, rotation);
            if(placeShape(transformed, originX, originY, player)){
                System.out.println(Main.toAsciiString());
                Main.clearGameBoard();
            }

        }
    }

    public static void placeAllUniqueRotations(int[][] shape, int originX, int originY, int player, int[] rotationIndices) {

        // Place each unique rotation of the shape on the game board
        for (int index : rotationIndices) {
            List<int[]> transformed = applyRotation(shape, ROTATION_MATRICES[index]);
            if (placeShape(transformed, originX, originY, player)) {
                System.out.println(Main.toAsciiString());
                Main.clearGameBoard();
            }
        }
    }

    // Applies a rotation matrix to the shape's points
    public static List<int[]> applyRotation(int[][] shape, int[][] matrix) {
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
    public static boolean placeShape(List<int[]> points, int originX, int originY, int player) {
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

    public static void removeShape(List<int[]> points, int originX, int originY) {
        int originZ = 0;
        checkZ(points);

        for (int[] p : points) {
            int x = originX + p[0];
            int y = originY + p[1];
            int z = originZ + p[2];

            Main.GAME_BOARD[x][y][z] = 0;
        }
    }


    public static void checkZ(List<int[]> points) {
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
        return new int[][][] {
                // indices: 0 - 3
                {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}},    // identity matrix
                {{1, 0, 0}, {0, 0, -1}, {0, 1, 0}},   // 90 degrees around X-axis
                {{1, 0, 0}, {0, -1, 0}, {0, 0, -1}},  // 180 degrees around X-axis
                {{1, 0, 0}, {0, 0, 1}, {0, -1, 0}},   // 270 degrees around X-axis

                // indices: 4 - 7, rotated 90 degrees around Y-axis
                {{0, 0, 1}, {0, 1, 0}, {-1, 0, 0}},   // 90 degrees around Y-axis
                {{0, 0, 1}, {-1, 0, 0}, {0, 1, 0}},   // 90 degrees around Y-axis, then 90 degrees around Z-axis
                {{0, -1, 0}, {-1, 0, 0}, {0, 0, -1}}, // 90 degrees around Y-axis, then 180 degrees around Z-axis
                {{0, 0, -1}, {-1, 0, 0}, {0, -1, 0}}, // 90 degrees around Y-axis, then 270 degrees around Z-axis

                // indices: 8 - 11, rotated 180 degrees around Y-axis
                {{-1, 0, 0}, {0, -1, 0}, {0, 0, 1}},   // 180 degrees around Y-axis
                {{-1, 0, 0}, {0, 0, 1}, {0, 1, 0}},    // 180 degrees around Y-axis, then 90 degrees around Z-axis
                {{-1, 0, 0}, {0, 1, 0}, {0, 0, -1}},   // 180 degrees around Y-axis, then 180 degrees around Z-axis
                {{-1, 0, 0}, {0, 0, -1}, {0, -1, 0}},  // 180 degrees around Y-axis, then 270 degrees around Z-axis

                // indices: 12 - 15, rotated 270 degrees around Y-axis
                {{0, -1, 0}, {1, 0, 0}, {0, 0, 1}},    // 270 degrees around Y-axis
                {{0, 0, 1}, {1, 0, 0}, {0, -1, 0}},    // 270 degrees around Y-axis, then 90 degrees around Z-axis
                {{0, 1, 0}, {1, 0, 0}, {0, 0, -1}},    // 270 degrees around Y-axis, then 180 degrees around Z-axis
                {{0, 0, -1}, {1, 0, 0}, {0, 1, 0}},    // 270 degrees around Y-axis, then 270 degrees around Z-axis

                // indices: 16 - 19, rotated 90 degrees around Z-axis
                {{0, 0, -1}, {0, 1, 0}, {1, 0, 0}},    // 90 degrees around Z-axis
                {{0, 1, 0}, {0, 0, 1}, {1, 0, 0}},     // 90 degrees around Z-axis, then 90 degrees around X-axis
                {{0, 0, 1}, {0, -1, 0}, {1, 0, 0}},    // 90 degrees around Z-axis, then 180 degrees around X-axis
                {{0, -1, 0}, {0, 0, -1}, {1, 0, 0}},   // 90 degrees around Z-axis, then 270 degrees around X-axis

                // indices: 20 - 23, rotated 270 degrees around Z-axis
                {{0, 0, -1}, {0, -1, 0}, {-1, 0, 0}},  // 270 degrees around Z-axis
                {{0, -1, 0}, {0, 0, 1}, {-1, 0, 0}},   // 270 degrees around Z-axis, then 90 degrees around X-axis
                {{0, 0, 1}, {0, 1, 0}, {-1, 0, 0}},    // 270 degrees around Z-axis, then 180 degrees around X-axis
                {{0, 1, 0}, {0, 0, -1}, {-1, 0, 0}},   // 270 degrees around Z-axis, then 270 degrees around X-axis
        };
    }


    private static int[] getAllRotationIndices(int[][] shape) {
        List<Integer> indices = new ArrayList<>();
        Set<Integer> uniqueShapes = new HashSet<>();

        // Iterate over all rotation matrices
        for (int i = 0; i < ROTATION_MATRICES.length; i++) {
            // Get the rotated shape
            List<int[]> rotated = applyRotation(shape, ROTATION_MATRICES[i]);

            checkZ(rotated);

            // Calculate a hash code for the rotated shape
            int hashCode = rotated.stream()
                    .map(Arrays::hashCode)
                    .sorted()
                    .reduce(0, Integer::sum);

            // If this rotation produces a shape we haven't seen before, add its index.
            if (uniqueShapes.add(hashCode)) {
                indices.add(i);
            }
        }

        // Convert the list of indices to an array
        return indices.stream().mapToInt(Integer::intValue).toArray();
    }
}


