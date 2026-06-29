(* sudoku_solver.v *)

Require Import List.
Require Import Nat.
Require Import Bool.
Require Import Arith.
Import ListNotations.
(* Require Import ExtrOcamlBasic.
Require Import ExtrOcamlNatInt.
Require Import ExtrOcamlString. *)

(** * 数独の表現 *)

(* 9x9のボードを自然数のリストのリストで表現 *)
(* 0は空セルを表す *)
Definition board := list (list nat).

(** * ユーティリティ関数 *)

(* リストのn番目の要素を取得 *)
Fixpoint nth_opt {A : Type} (n : nat) (l : list A) : option A :=
  match l with
  | [] => None
  | x :: xs => match n with
                | 0 => Some x
                | S n' => nth_opt n' xs
                end
  end.

(* リストのn番目の要素を更新 *)
Fixpoint update_nth {A : Type} (n : nat) (l : list A) (v : A) : list A :=
  match l with
  | [] => []
  | x :: xs => match n with
                | 0 => v :: xs
                | S n' => x :: update_nth n' xs v
                end
  end.

(* ボードの(row, col)の値を取得 *)
Definition get_cell (b : board) (row col : nat) : nat :=
  match nth_opt row b with
  | Some r => match nth_opt col r with
              | Some v => v
              | None => 0
              end
  | None => 0
  end.

(* ボードの(row, col)に値を設定 *)
Definition set_cell (b : board) (row col val : nat) : board :=
  match nth_opt row b with
  | Some r => update_nth row b (update_nth col r val)
  | None => b
  end.

(** * 制約チェック *)

(* リスト内にある値が存在するか *)
Fixpoint mem_nat (n : nat) (l : list nat) : bool :=
  match l with
  | [] => false
  | x :: xs => if Nat.eqb x n then true else mem_nat n xs
  end.

(* 指定行の値をリストで取得 *)
Definition get_row (b : board) (row : nat) : list nat :=
  match nth_opt row b with
  | Some r => r
  | None => []
  end.

(* 指定列の値をリストで取得 *)
Definition get_col (b : board) (col : nat) : list nat :=
  map (fun r => match nth_opt col r with
                | Some v => v
                | None => 0
                end) b.

(* 3x3ブロックの値をリストで取得 *)
Definition get_block (b : board) (row col : nat) : list nat :=
  let br := (row / 3) * 3 in
  let bc := (col / 3) * 3 in
  flat_map (fun dr =>
    map (fun dc => get_cell b (br + dr) (bc + dc))
        [0; 1; 2])
    [0; 1; 2].

(* 値valが(row, col)に配置可能か *)
Definition is_valid_placement (b : board) (row col val : nat) : bool :=
  negb (mem_nat val (get_row b row)) &&
  negb (mem_nat val (get_col b col)) &&
  negb (mem_nat val (get_block b row col)).

(** * 空セルの検索 *)

(* 最初の空セル(値が0)の位置を見つける *)
Fixpoint find_empty_aux (b : board) (row col : nat) : option (nat * nat) :=
  match b with
  | [] => None
  | r :: rs =>
    let fix find_in_row (cells : list nat) (c : nat) : option nat :=
      match cells with
      | [] => None
      | v :: vs => if Nat.eqb v 0 then Some c
                   else find_in_row vs (S c)
      end
    in
    match find_in_row r 0 with
    | Some c => Some (row, c)
    | None => find_empty_aux rs (S row) 0
    end
  end.

Definition find_empty (b : board) : option (nat * nat) :=
  find_empty_aux b 0 0.

(** * ソルバー（バックトラッキング） *)

(* 燃料（再帰の上限）を用いたソルバー *)
Fixpoint solve_aux (fuel : nat) (b : board) : option board :=
  match fuel with
  | 0 => None  (* 燃料切れ *)
  | S fuel' =>
    match find_empty b with
    | None => Some b  (* 空セルなし = 解決済み *)
    | Some (row, col) =>
      (* 1から9を試す *)
      let fix try_values (vals : list nat) : option board :=
        match vals with
        | [] => None
        | v :: vs =>
          if is_valid_placement b row col v then
            let new_board := set_cell b row col v in
            match solve_aux fuel' new_board with
            | Some solution => Some solution
            | None => try_values vs
            end
          else
            try_values vs
        end
      in
      try_values [1; 2; 3; 4; 5; 6; 7; 8; 9]
    end
  end.

(* 十分な燃料でソルバーを呼び出す *)
Definition solve (b : board) : option board :=
  solve_aux 1000 b.

(** * テスト用の数独パズル *)

(* 簡単な数独の例 *)
Definition example_puzzle : board :=
  [ [5; 3; 0;  0; 7; 0;  0; 0; 0];
    [6; 0; 0;  1; 9; 5;  0; 0; 0];
    [0; 9; 8;  0; 0; 0;  0; 6; 0];

    [8; 0; 0;  0; 6; 0;  0; 0; 3];
    [4; 0; 0;  8; 0; 3;  0; 0; 1];
    [7; 0; 0;  0; 2; 0;  0; 0; 6];

    [0; 6; 0;  0; 0; 0;  2; 8; 0];
    [0; 0; 0;  4; 1; 9;  0; 0; 5];
    [0; 0; 0;  0; 8; 0;  0; 7; 9]
  ].

(** * 計算による検証 *)

(* 解を計算 *)
Definition example_solution := Eval compute in solve example_puzzle.

(* 結果の確認 *)
Compute solve example_puzzle.

(** * 正しさの仕様（オプション） *)

(* 値が1-9の範囲か *)
Definition valid_value (v : nat) : Prop := 1 <= v /\ v <= 9.

(* リスト内の値が全てユニーク（0を除く） *)
Fixpoint all_different_nonzero (l : list nat) : Prop :=
  match l with
  | [] => True
  | x :: xs => (x <> 0 -> ~ In x xs) /\ all_different_nonzero xs
  end.

(* ボードが有効な数独の解であることの仕様 *)
Definition is_complete_solution (b : board) : Prop :=
  (* 全セルが1-9 *)
  (forall row col, row < 9 -> col < 9 -> valid_value (get_cell b row col)) /\
  (* 各行がユニーク *)
  (forall row, row < 9 -> all_different_nonzero (get_row b row)) /\
  (* 各列がユニーク *)
  (forall col, col < 9 -> all_different_nonzero (get_col b col)) /\
  (* 各ブロックがユニーク *)
  (forall br bc, br < 3 -> bc < 3 ->
    all_different_nonzero (get_block b (br * 3) (bc * 3))).

(* 解が元のパズルと整合的であることの仕様 *)
Definition consistent_with_puzzle (puzzle solution : board) : Prop :=
  forall row col,
    row < 9 -> col < 9 ->
    get_cell puzzle row col <> 0 ->
    get_cell puzzle row col = get_cell solution row col.

    
(* Extraction Language OCaml.
Extraction "sudoku_solver" solve example_puzzle. *)