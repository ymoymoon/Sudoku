# ==========================================
# 1. あなたが解きたい問題をここに記入してください
# ==========================================
# 0 は空セルです。数字を9文字ずつ、9行で並べてください。
problem_str = """
530070000
600195000
098000060
800060003
400803001
700020006
060002800
000419005
000080079
"""

def solve(board):
    find = find_empty(board)
    if not find:
        return True
    row, col = find

    for i in range(1, 10):
        if is_valid(board, row, col, i):
            board[row][col] = i
            if solve(board):
                return True
            board[row][col] = 0
    return False

def is_valid(board, row, col, num):
    # 行・列・3x3ブロックの重複チェック
    for i in range(9):
        if board[row][i] == num or board[i][col] == num:
            return False
    sr, sc = 3 * (row // 3), 3 * (col // 3)
    for i in range(3):
        for j in range(3):
            if board[sr + i][sc + j] == num:
                return False
    return True

def find_empty(board):
    for i in range(9):
        for j in range(9):
            if board[i][j] == 0:
                return (i, j)
    return None

def print_board(board):
    for i in range(9):
        if i % 3 == 0 and i != 0:
            print("- - - - - - - - - - -")
        for j in range(9):
            if j % 3 == 0 and j != 0:
                print("|", end=" ")
            print(board[i][j], end=" ")
        print()

# 文字列をプログラム用のリスト形式に変換
board = [[int(c) for c in line] for line in problem_str.strip().split('\n')]

print("--- 問題 ---")
print_board(board)

if solve(board):
    print("\n--- 解答 ---")
    print_board(board)
else:
    print("\n解が見つかりませんでした。")