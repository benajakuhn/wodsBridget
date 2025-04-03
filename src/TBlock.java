public class TBlock {
    private int rotation; // 0 to 3

    public TBlock() {
        this.rotation = 0;
    }

    public void rotate() {
        rotation = (rotation + 1) % 4;
    }

    public void place(int x, int y) {
        switch (rotation) {
        case 0 -> placeRotation0(x, y);
        case 1 -> placeRotation1(x, y);
        case 2 -> placeRotation2(x, y);
        case 3 -> placeRotation3(x, y);
        }
    }

    private void placeRotation0(int x, int y) {
        if (x >= 0 && x + 1 < 8 && y - 1 >= 0 && y + 1 < 8) {
            Main.GAME_BOARD[x][y - 1][0] = 1;
            Main.GAME_BOARD[x][y][0]     = 1;
            Main.GAME_BOARD[x][y + 1][0] = 1;
            Main.GAME_BOARD[x + 1][y][0] = 1;
        }
    }

    private void placeRotation1(int x, int y) {
        if (x - 1 >= 0 && x + 1 < 8 && y + 1 < 8) {
            Main.GAME_BOARD[x - 1][y][0] = 1;
            Main.GAME_BOARD[x][y][0]     = 1;
            Main.GAME_BOARD[x + 1][y][0] = 1;
            Main.GAME_BOARD[x][y + 1][0] = 1;
        }
    }

    private void placeRotation2(int x, int y) {
        if (x - 1 >= 0 && y - 1 >= 0 && y + 1 < 8) {
            Main.GAME_BOARD[x][y - 1][0] = 1;
            Main.GAME_BOARD[x][y][0]     = 1;
            Main.GAME_BOARD[x][y + 1][0] = 1;
            Main.GAME_BOARD[x - 1][y][0] = 1;
        }
    }

    private void placeRotation3(int x, int y) {
        if (x - 1 >= 0 && x + 1 < 8 && y - 1 >= 0) {
            Main.GAME_BOARD[x - 1][y][0] = 1;
            Main.GAME_BOARD[x][y][0]     = 1;
            Main.GAME_BOARD[x + 1][y][0] = 1;
            Main.GAME_BOARD[x][y - 1][0] = 1;
        }
    }
}
