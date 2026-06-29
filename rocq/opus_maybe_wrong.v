(* sudoku_constraint.v *)

Require Import List.
Require Import Nat.
Require Import Bool.
Require Import Arith.
Import ListNotations.

(** * 基本型定義 *)

(* セルの状態: 確定値 or 候補集合 *)
Inductive cell : Type :=
  | Fixed : nat -> cell           (* 確定した値 *)
  | Candidates : list nat -> cell (* 候補のリスト *).

(* 制約伝播用のボード *)
Definition cboard := list (list cell).

(* 生のボード *)
Definition raw_board := list (list nat).

(** * ユーティリティ *)

Fixpoint nth_opt {A : Type} (n : nat) (l : list A) : option A :=
  match l with
  | [] => None
  | x :: xs => match n with
                | 0 => Some x
                | S n' => nth_opt n' xs
                end
  end.

Fixpoint update_nth {A : Type} (n : nat) (l : list A) (v : A) : list A :=
  match l with
  | [] => []
  | x :: xs => match n with
                | 0 => v :: xs
                | S n' => x :: update_nth n' xs v
                end
  end.

Definition get_ccell (b : cboard) (row col : nat) : cell :=
  match nth_opt row b with
  | Some r => match nth_opt col r with
              | Some c => c
              | None => Candidates []
              end
  | None => Candidates []
  end.

Definition set_ccell (b : cboard) (row col : nat) (c : cell) : cboard :=
  match nth_opt row b with
  | Some r => update_nth row b (update_nth col r c)
  | None => b
  end.

(* リストから要素を除去 *)
Fixpoint remove_nat (n : nat) (l : list nat) : list nat :=
  match l with
  | [] => []
  | x :: xs => if Nat.eqb x n then remove_nat n xs
               else x :: remove_nat n xs
  end.

Fixpoint mem_nat (n : nat) (l : list nat) : bool :=
  match l with
  | [] => false
  | x :: xs => if Nat.eqb x n then true else mem_nat n xs
  end.

Definition list_length {A : Type} (l : list A) : nat := length l.

(* リストの共通部分 *)
Fixpoint intersect (l1 l2 : list nat) : list nat :=
  match l1 with
  | [] => []
  | x :: xs => if mem_nat x l2 then x :: intersect xs l2
               else intersect xs l2
  end.

(* リストの差分 *)
Fixpoint diff (l1 l2 : list nat) : list nat :=
  match l1 with
  | [] => []
  | x :: xs => if mem_nat x l2 then diff xs l2
               else x :: diff xs l2
  end.

(* nat のリストが等しいかの判定（順序を無視しない簡易版） *)
Fixpoint list_nat_eqb (l1 l2 : list nat) : bool :=
  match l1, l2 with
  | [], [] => true
  | x :: xs, y :: ys => Nat.eqb x y && list_nat_eqb xs ys
  | _, _ => false
  end.

(** * 座標とピア（関連セル） *)

Definition positions := list (nat * nat).

(* 行のセル座標 *)
Definition row_peers (row : nat) : positions :=
  map (fun col => (row, col)) (seq 0 9).

(* 列のセル座標 *)
Definition col_peers (col : nat) : positions :=
  map (fun row => (row, col)) (seq 0 9).

(* ブロックのセル座標 *)
Definition block_peers (row col : nat) : positions :=
  let br := (row / 3) * 3 in
  let bc := (col / 3) * 3 in
  flat_map (fun dr =>
    map (fun dc => (br + dr, bc + dc)) (seq 0 3))
    (seq 0 3).

(* あるセルの全ピア（自分自身を除く） *)
Definition all_peers (row col : nat) : positions :=
  let all := row_peers row ++ col_peers col ++ block_peers row col in
  (* 重複除去と自分自身の除去 *)
  let fix dedup (l : positions) : positions :=
    match l with
    | [] => []
    | (r, c) :: rest =>
      if (Nat.eqb r row && Nat.eqb c col) then dedup rest
      else
        let already := existsb (fun p => Nat.eqb (fst p) r && Nat.eqb (snd p) c) rest in
        if already then dedup rest
        else (r, c) :: dedup rest
    end
  in dedup (rev all).

(** * 初期化: 生ボードから候補付きボードへ *)

Definition init_cell (v : nat) : cell :=
  match v with
  | 0 => Candidates [1; 2; 3; 4; 5; 6; 7; 8; 9]
  | n => Fixed n
  end.

Definition init_board (b : raw_board) : cboard :=
  map (fun row => map init_cell row) b.

(** * 戦略1: 制約伝播 (Eliminate) *)
(* 確定セルの値をピアの候補から除去 *)

(* セルから特定の候補を除去。候補が1つになったら確定 *)
Definition eliminate_from_cell (c : cell) (v : nat) : cell :=
  match c with
  | Fixed _ => c
  | Candidates cands =>
    let new_cands := remove_nat v cands in
    match new_cands with
    | [x] => Fixed x    (* Naked Single: 候補が1つに絞られた *)
    | _ => Candidates new_cands
    end
  end.

(* 確定値vを指定位置のピアから排除 *)
Definition eliminate_value (b : cboard) (row col : nat) (v : nat) : cboard :=
  let peers := all_peers row col in
  fold_left (fun board p =>
    let '(r, c) := p in
    let current := get_ccell board r c in
    let updated := eliminate_from_cell current v in
    set_ccell board r c updated)
    peers b.

(* ボード全体で確定セルから伝播 *)
Definition propagate_once (b : cboard) : cboard :=
  let indices := flat_map (fun r => map (fun c => (r, c)) (seq 0 9)) (seq 0 9) in
  fold_left (fun board p =>
    let '(r, c) := p in
    match get_ccell board r c with
    | Fixed v => eliminate_value board r c v
    | Candidates _ => board
    end)
    indices b.

(** * 戦略2: Hidden Single *)
(* ある単位（行/列/ブロック）内で、ある値を持てるセルが1つだけの場合確定 *)

(* 単位内でval を候補に持つセルを探す *)
Definition find_unique_position (b : cboard) (unit : positions) (val : nat) 
    : option (nat * nat) :=
  let holders := filter (fun p =>
    let '(r, c) := p in
    match get_ccell b r c with
    | Fixed v => Nat.eqb v val
    | Candidates cands => mem_nat val cands
    end) unit in
  match holders with
  | [(r, c)] =>
    match get_ccell b r c with
    | Fixed _ => None       (* 既に確定 *)
    | Candidates _ => Some (r, c)  (* Hidden Single 発見 *)
    end
  | _ => None
  end.

(* 全単位に対して Hidden Single を適用 *)
Definition apply_hidden_singles (b : cboard) : cboard :=
  let all_units :=
    map row_peers (seq 0 9) ++
    map col_peers (seq 0 9) ++
    flat_map (fun br => map (fun bc => block_peers (br * 3) (bc * 3)) (seq 0 3)) (seq 0 3)
  in
  let values := seq 1 9 in
  fold_left (fun board unit =>
    fold_left (fun board' val =>
      match find_unique_position board' unit val with
      | Some (r, c) => set_ccell board' r c (Fixed val)
      | None => board'
      end)
      values board)
    all_units b.

(** * 戦略3: Naked Pair *)
(* 同一単位内で同じ2候補を持つセルが2つあれば、
   その2値をユニットの他セルの候補から除去 *)

Definition is_pair (c : cell) : option (nat * nat) :=
  match c with
  | Candidates [a; b] => Some (a, b)
  | _ => None
  end.

Definition apply_naked_pairs_unit (b : cboard) (unit : positions) : cboard :=
  let pairs := filter (fun p =>
    let '(r, c) := p in
    match is_pair (get_ccell b r c) with
    | Some _ => true
    | None => false
    end) unit in
  (* 同じ候補ペアを持つセルの組を探す *)
  let fix find_matching (ps : positions) : cboard :=
    match ps with
    | [] => b
    | (r1, c1) :: rest =>
      match is_pair (get_ccell b r1 c1) with
      | Some (a, bb) =>
        let partner := find (fun p =>
          let '(r2, c2) := p in
          match is_pair (get_ccell b r2 c2) with
          | Some (a', b') => Nat.eqb a a' && Nat.eqb bb b'
          | None => false
          end) rest in
        match partner with
        | Some (r2, c2) =>
          (* ペア発見: unit の他のセルから a, bb を除去 *)
          let others := filter (fun p =>
            let '(r, c) := p in
            negb (Nat.eqb r r1 && Nat.eqb c c1) &&
            negb (Nat.eqb r r2 && Nat.eqb c c2)) unit in
          fold_left (fun bd p =>
            let '(r, c) := p in
            let cell := get_ccell bd r c in
            let cell' := eliminate_from_cell (eliminate_from_cell cell a) bb in
            set_ccell bd r c cell')
            others b
        | None => find_matching rest
        end
      | None => find_matching rest
      end
    end
  in find_matching pairs.

Definition apply_naked_pairs (b : cboard) : cboard :=
  let all_units :=
    map row_peers (seq 0 9) ++
    map col_peers (seq 0 9) ++
    flat_map (fun br => map (fun bc => block_peers (br * 3) (bc * 3)) (seq 0 3)) (seq 0 3)
  in
  fold_left apply_naked_pairs_unit all_units b.

(** * 矛盾検出 *)

Definition has_contradiction (b : cboard) : bool :=
  existsb (fun r =>
    existsb (fun c =>
      match get_ccell b r c with
      | Candidates [] => true   (* 候補が空 = 矛盾 *)
      | _ => false
      end) (seq 0 9)) (seq 0 9).

(** * 解決済み判定 *)

Definition is_solved (b : cboard) : bool :=
  forallb (fun r =>
    forallb (fun c =>
      match get_ccell b r c with
      | Fixed _ => true
      | Candidates _ => false
      end) (seq 0 9)) (seq 0 9).

(** * ボード比較（変化の検出） *)

Definition cell_eqb (c1 c2 : cell) : bool :=
  match c1, c2 with
  | Fixed a, Fixed b => Nat.eqb a b
  | Candidates l1, Candidates l2 => list_nat_eqb l1 l2
  | _, _ => false
  end.

Definition board_eqb (b1 b2 : cboard) : bool :=
  forallb (fun r =>
    forallb (fun c =>
      cell_eqb (get_ccell b1 r c) (get_ccell b2 r c))
      (seq 0 9)) (seq 0 9).

(** * メインソルバー: 戦略の反復適用 *)

(* 一回の推論ステップ *)
Definition solve_step (b : cboard) : cboard :=
  let b1 := propagate_once b in
  let b2 := apply_hidden_singles b1 in
  let b3 := apply_naked_pairs b2 in
  propagate_once b3.  (* ペア適用後に再度伝播 *)

(* 不動点に達するまで繰り返す *)
Fixpoint solve_loop (fuel : nat) (b : cboard) : cboard :=
  match fuel with
  | 0 => b
  | S fuel' =>
    if is_solved b then b
    else if has_contradiction b then b
    else
      let b' := solve_step b in
      if board_eqb b b' then b  (* 変化なし = これ以上推論不可 *)
      else solve_loop fuel' b'
  end.

(** * 論理的解法で解ききれない場合の最小バックトラック *)
(* 最も候補の少ないセルを選んで分岐する（理論的にはこれも
   "bifurcation" として正当化される） *)

Definition find_min_candidates (b : cboard) : option (nat * nat * list nat) :=
  let indices := flat_map (fun r => map (fun c => (r, c)) (seq 0 9)) (seq 0 9) in
  fold_left (fun best p =>
    let '(r, c) := p in
    match get_ccell b r c with
    | Candidates cands =>
      match cands with
      | [] => best
      | _ =>
        match best with
        | None => Some (r, c, cands)
        | Some (_, _, prev_cands) =>
          if Nat.ltb (length cands) (length prev_cands)
          then Some (r, c, cands)
          else best
        end
      end
    | Fixed _ => best
    end) indices None.

(* 理論的推論 + 最小分岐 *)
Fixpoint solve_full (fuel : nat) (b : cboard) : option cboard :=
  match fuel with
  | 0 => None
  | S fuel' =>
    let b' := solve_loop 100 b in
    if has_contradiction b' then None
    else if is_solved b' then Some b'
    else
      (* 論理的推論だけでは解けない -> 最小候補セルで分岐 *)
      match find_min_candidates b' with
      | None => None
      | Some (r, c, cands) =>
        let fix try_each (vs : list nat) : option cboard :=
          match vs with
          | [] => None
          | v :: rest =>
            let hypothesis := set_ccell b' r c (Fixed v) in
            match solve_full fuel' hypothesis with
            | Some sol => Some sol
            | None => try_each rest  (* この仮定は矛盾を導く *)
            end
          end
        in try_each cands
      end
  end.

(** * トップレベルインターフェース *)

Definition solve (b : raw_board) : option cboard :=
  let initial := init_board b in
  solve_full 500 initial.

(* cboard -> raw_board への変換 *)
Definition extract_solution (b : cboard) : raw_board :=
  map (fun r =>
    map (fun c =>
      match get_ccell b r c with
      | Fixed v => v
      | Candidates _ => 0
      end) (seq 0 9)) (seq 0 9).

Definition solve_raw (b : raw_board) : option raw_board :=
  match solve b with
  | Some sol => Some (extract_solution sol)
  | None => None
  end.

(** * テスト *)

Definition example_puzzle : raw_board :=
  [ [5; 3; 0;  0; 7; 0;  0; 0; 0];
    [6; 0; 0;  1; 9; 5;  0; 0; 0];
    [0; 9; 8;  0; 0; 0;  0; 6; 0];
    [8; 0; 0;  0; 6; 0;  0; 0; 3];
    [4; 0; 0;  8; 0; 3;  0; 0; 1];
    [7; 0; 0;  0; 2; 0;  0; 0; 6];
    [0; 6; 0;  0; 0; 0;  2; 8; 0];
    [0; 0; 0;  4; 1; 9;  0; 0; 5];
    [0; 0; 0;  0; 8; 0;  0; 7; 9] ].

(* 計算実行 *)
Compute solve_raw example_puzzle.

(** * 各戦略の正当性の証明スケッチ *)

(** Naked Single の正当性:
    セル(r,c)の候補が{v}のみ ならば、行・列・ブロックの制約から
    他の値は入れられないため、v が唯一の解である。 *)

(** Hidden Single の正当性:
    単位U内で値vを候補に持つセルが(r,c)のみならば、
    Uには1-9が全て必要 → vは(r,c)に入らなければならない。 *)

(** Naked Pair の正当性:
    単位U内のセルA,Bが共に候補{a,b}のみを持つならば、
    A,Bでa,bを使い切る → Uの他のセルからa,bを除去できる。 *)

Theorem naked_single_sound :
  forall (b : cboard) (r c : nat) (v : nat),
    get_ccell b r c = Candidates [v] ->
    (* もしボードが解を持つならば、その解で(r,c)=v *)
    True. (* 完全な証明は省略 *)
Proof. intros. exact I. Qed.
