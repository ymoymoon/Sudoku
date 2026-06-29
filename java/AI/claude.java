/**
 * 数独ソルバー（バックトラッキング法）
 * =====================================
 * コンパイル: javac SudokuSolver.java
 * 実行:       java SudokuSolver
 */
public class claude {

    static final int SIZE = 9;

    // ------------------------------------------------------------------
    // 空マス（0）を左上から順に探す。見つかれば {row, col}、なければ null
    // ------------------------------------------------------------------
    static int[] findEmpty(int[][] board) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (board[r][c] == 0) return new int[]{r, c};
            }
        }
        return null;
    }

    // ------------------------------------------------------------------
    // (row, col) に num を置いてもルール違反にならないか検査
    // ------------------------------------------------------------------
    static boolean isValid(int[][] board, int row, int col, int num) {
        // 行チェック
        for (int c = 0; c < SIZE; c++) {
            if (board[row][c] == num) return false;
        }
        // 列チェック
        for (int r = 0; r < SIZE; r++) {
            if (board[r][col] == num) return false;
        }
        // 3×3 ボックスチェック
        int boxR = (row / 3) * 3;
        int boxC = (col / 3) * 3;
        for (int r = boxR; r < boxR + 3; r++) {
            for (int c = boxC; c < boxC + 3; c++) {
                if (board[r][c] == num) return false;
            }
        }
        return true;
    }

    // ------------------------------------------------------------------
    // バックトラッキングで数独を解く（in-place）
    // 解が存在すれば true、存在しなければ false を返す
    // ------------------------------------------------------------------
    static boolean solve(int[][] board) {
        int[] cell = findEmpty(board);
        if (cell == null) return true;   // 空マスなし → 解が完成

        int row = cell[0], col = cell[1];
        for (int num = 1; num <= 9; num++) {
            if (isValid(board, row, col, num)) {
                board[row][col] = num;       // 仮置き
                if (solve(board)) return true;
                board[row][col] = 0;         // バックトラック
            }
        }
        return false;  // どの数字も置けない → 失敗
    }

    // ------------------------------------------------------------------
    // 盤面を見やすく表示
    // ------------------------------------------------------------------
    static void printBoard(int[][] board) {
        for (int r = 0; r < SIZE; r++) {
            if (r % 3 == 0 && r != 0) System.out.println("------+-------+------");
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < SIZE; c++) {
                if (c % 3 == 0 && c != 0) sb.append("| ");
                sb.append(board[r][c] == 0 ? "." : board[r][c]);
                sb.append(" ");
            }
            System.out.println(sb);
        }
    }

    // ------------------------------------------------------------------
    // main
    // ------------------------------------------------------------------
    public static void main(String[] args) {
        int[][] puzzle = {
            {5, 3, 0,  0, 7, 0,  0, 0, 0},
            {6, 0, 0,  1, 9, 5,  0, 0, 0},
            {0, 9, 8,  0, 0, 0,  0, 6, 0},

            {8, 0, 0,  0, 6, 0,  0, 0, 3},
            {4, 0, 0,  8, 0, 3,  0, 0, 1},
            {7, 0, 0,  0, 2, 0,  0, 0, 6},

            {0, 6, 0,  0, 0, 0,  2, 8, 0},
            {0, 0, 0,  4, 1, 9,  0, 0, 5},
            {0, 0, 0,  0, 8, 0,  0, 7, 9},
        };

        System.out.println("=== 問題 ===");
        printBoard(puzzle);

        if (solve(puzzle)) {
            System.out.println("\n=== 解答 ===");
            printBoard(puzzle);
        } else {
            System.out.println("\n解なし");
        }
    }
}