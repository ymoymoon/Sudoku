public class gemini {

    // 1. あなたが解きたい問題をここに記入してください（0は空セル）
    private static int[][] board = {
        {5, 3, 0, 0, 7, 0, 0, 0, 0},
        {6, 0, 0, 1, 9, 5, 0, 0, 0},
        {0, 9, 8, 0, 0, 0, 0, 6, 0},
        {8, 0, 0, 0, 6, 0, 0, 0, 3},
        {4, 0, 0, 8, 0, 3, 0, 0, 1},
        {7, 0, 0, 0, 2, 0, 0, 0, 6},
        {0, 6, 0, 0, 0, 0, 2, 8, 0},
        {0, 0, 0, 4, 1, 9, 0, 0, 5},
        {0, 0, 0, 0, 8, 0, 0, 7, 9}
    };

    public static void main(String[] args) {
        System.out.println("--- 問題 ---");
        printBoard();

        if (solve()) {
            System.out.println("\n--- 解答 ---");
            printBoard();
        } else {
            System.out.println("\n解が存在しません。");
        }
    }

    // 数独を解く再帰関数（バックトラッキング）
    private static boolean solve() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                // 空セル（0）を見つけた場合
                if (board[row][col] == 0) {
                    // 1から9までの数字を試す
                    for (int num = 1; num <= 9; num++) {
                        if (isValid(row, col, num)) {
                            board[row][col] = num; // 数字を配置

                            // 再帰的に次のセルを解く
                            if (solve()) {
                                return true;
                            }

                            board[row][col] = 0; // バックトラック（元に戻す）
                        }
                    }
                    return false; // 1~9のどれも置けない場合は失敗
                }
            }
        }
        return true; // すべてのセルが埋まったら終了
    }

    // 数字がルール（行・列・3x3ブロック）に違反していないかチェック
    private static boolean isValid(int row, int col, int num) {
        // 行と列のチェック
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == num || board[i][col] == num) {
                return false;
            }
        }

        // 3x3ブロックのチェック
        int startRow = 3 * (row / 3);
        int startCol = 3 * (col / 3);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[startRow + i][startCol + j] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    // 盤面を綺麗に表示するメソッド
    private static void printBoard() {
        for (int i = 0; i < 9; i++) {
            if (i % 3 == 0 && i != 0) {
                System.out.println("- - - - - - - - - - -");
            }
            for (int j = 0; j < 9; j++) {
                if (j % 3 == 0 && j != 0) {
                    System.out.print("| ");
                }
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }
}