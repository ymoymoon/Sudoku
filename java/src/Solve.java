import java.io.*;
import java.util.*;

// ONE INSTANSE HAS ONE "numberplate"

public class Solve { // normal 9*9
	protected int size;
//	protected int blocksize; //sizeの平方根
	int[] numberplate = new int[size*size];
	int[] numberplate2 = new int[size*size];
	protected int[][] COLUMN; // only in this class changeable
	protected int[][] ROW;
	protected int[][] BLOCK;
	protected ArrayList<Integer> yet = new ArrayList<>(); // 最初に数字がはいっていないセルの配列
	private int canSolve; // -3 : ?, -2 : no answer, -1 : can solve, k : some answers (index k が一意でなく、複数の値が入りうる)
	
/*	static Boolean[][] maybe = {}; // "maybe[i][j] = true" means number j may enter cell i
	static ArrayList<Integer> changed = new ArrayList<>(); // used in method "re_maybe"
*/	

	Solve(){ // normal
		size = 9;
//		blocksize = 3;
		// cell i is in COLUMN[i%9]
			  COLUMN = new int[][] {{0, 9, 18, 27, 36, 45, 54, 63, 72},
						{1, 10, 19, 28, 37, 46, 55, 64, 73},
						{2, 11, 20, 29, 38, 47, 56, 65, 74},
						{3, 12, 21, 30, 39, 48, 57, 66, 75},
						{4, 13, 22, 31, 40, 49, 58, 67, 76},
						{5, 14, 23, 32, 41, 50, 59, 68, 77},
						{6, 15, 24, 33, 42, 51, 60, 69, 78},
						{7, 16, 25, 34, 43, 52, 61, 70, 79},
						{8, 17, 26, 35, 44, 53, 62, 71, 80}};
		// cell i is in ROW[i/9]
				  ROW = new int[][] {{0, 1, 2, 3, 4, 5, 6, 7, 8},
						{9, 10, 11, 12, 13, 14, 15, 16, 17},
						{18, 19, 20, 21, 22, 23, 24, 25, 26},
						{27, 28, 29, 30, 31, 32, 33, 34, 35},
						{36, 37, 38, 39, 40, 41, 42, 43, 44},
						{45, 46, 47, 48, 49, 50, 51, 52, 53},
						{54, 55, 56, 57, 58, 59, 60, 61, 62},
						{63, 64, 65, 66, 67, 68, 69, 70, 71},
						{72, 73, 74, 75, 76, 77, 78, 79, 80}};
		// cell i is in BLOCK[(i/27)*3 + (i%9)/3] 幾何学ナンプレはここかえる
				 BLOCK = new int[][] {{0, 1, 2, 9, 10, 11, 18, 19, 20},
						 {3, 4, 5, 12, 13, 14, 21, 22, 23},
						 {6, 7, 8, 15, 16, 17, 24, 25, 26},
						 {27, 28, 29, 36, 37, 38, 45, 46, 47},
						 {30, 31, 32, 39, 40, 41, 48, 49, 50},
						 {33, 34, 35, 42, 43, 44, 51, 52, 53},
						 {54, 55, 56, 63, 64, 65, 72, 73, 74},
						 {57, 58, 59, 66, 67, 68, 75, 76, 77},
						 {60, 61, 62, 69, 70, 71, 78, 79, 80}};	
	}
	
	Solve(int[] numberplate){
		size = 9;
//		blocksize = 3;
		this.numberplate = new int[81];
		this.numberplate2 = new int[81];
		// cell i is in COLUMN[i%9]
			  COLUMN = new int[][] {{0, 9, 18, 27, 36, 45, 54, 63, 72},
						{1, 10, 19, 28, 37, 46, 55, 64, 73},
						{2, 11, 20, 29, 38, 47, 56, 65, 74},
						{3, 12, 21, 30, 39, 48, 57, 66, 75},
						{4, 13, 22, 31, 40, 49, 58, 67, 76},
						{5, 14, 23, 32, 41, 50, 59, 68, 77},
						{6, 15, 24, 33, 42, 51, 60, 69, 78},
						{7, 16, 25, 34, 43, 52, 61, 70, 79},
						{8, 17, 26, 35, 44, 53, 62, 71, 80}};
		// cell i is in ROW[i/9]
			  ROW = new int[][] {{0, 1, 2, 3, 4, 5, 6, 7, 8},
						{9, 10, 11, 12, 13, 14, 15, 16, 17},
						{18, 19, 20, 21, 22, 23, 24, 25, 26},
						{27, 28, 29, 30, 31, 32, 33, 34, 35},
						{36, 37, 38, 39, 40, 41, 42, 43, 44},
						{45, 46, 47, 48, 49, 50, 51, 52, 53},
						{54, 55, 56, 57, 58, 59, 60, 61, 62},
						{63, 64, 65, 66, 67, 68, 69, 70, 71},
						{72, 73, 74, 75, 76, 77, 78, 79, 80}};
		// cell i is in BLOCK[(i/27)*3 + (i%9)/3] 幾何学ナンプレはここかえる
			  BLOCK = new int[][] {{0, 1, 2, 9, 10, 11, 18, 19, 20},
						 {3, 4, 5, 12, 13, 14, 21, 22, 23},
						 {6, 7, 8, 15, 16, 17, 24, 25, 26},
						 {27, 28, 29, 36, 37, 38, 45, 46, 47},
						 {30, 31, 32, 39, 40, 41, 48, 49, 50},
						 {33, 34, 35, 42, 43, 44, 51, 52, 53},
						 {54, 55, 56, 63, 64, 65, 72, 73, 74},
						 {57, 58, 59, 66, 67, 68, 75, 76, 77},
						 {60, 61, 62, 69, 70, 71, 78, 79, 80}};	
						 
		for (int k = 0; k < size*size; k++) {
		    this.numberplate[k] = numberplate[k];
		    this.numberplate2[k] = numberplate[k];
		}
	}

	
	public void readFromFile(String filename) {
		Scanner sc = null;
		
		try {
			File file  = new File(filename);
			BufferedReader br = new BufferedReader(new FileReader(file));
			sc = new Scanner(br);
			
			// contents of the file to array
			String ideal = "((\\d)(\\s)+){8}(\\d)";
			String[] tmpline;
			
			for(int i = 0; i < size; i++) {
				String tmp = br.readLine();
				tmp = tmp.trim();
				if(tmp.matches(ideal)) { //形が合っているならば
					
					tmpline = tmp.split(" ");
					for (int j = 0; j < size; j++) {
						int tmp2 = Integer.parseInt(tmpline[j]);//exceptionの可能性?
						if (tmp2 == 0 || canEnter(size*i+j, tmp2, numberplate)) { // おかしな問題でない限りtrue
							numberplate[size*i + j] = tmp2; // 【初期化】
							numberplate2[size*i + j] = tmp2; // 後のため
						}else {
							System.out.println("This problem cannot be solved.");
							System.exit(0);
						}
						
					}
					
				}else { //形があっていない
					System.out.println("Each line should be like '2 0 5 0 0 0 4 0 8' when size == 9 (0 means no number in the cell.)");
					System.exit(0);
				}
			}
		
			if(sc.hasNext()){
				System.out.println("There should be only " + size +" lines.");
				System.exit(0);
			}
			
			
		}catch(IOException e) {
			System.out.println(filename + " was not found.");
			System.exit(0);
		}catch(NumberFormatException e) {
			System.out.println("範囲外の数字が書かれている可能性があります");
			System.exit(0);
		}finally {
			sc.close();
		}
		
	}
	
	public void outputToConsole() {
		if (canSolve == -3) {
			System.out.println("This problem cannot be solved.");
			
		}else if (canSolve == -1 | canSolve == -2) {
			System.out.println("This is the answer.");
		
			for(int i = 0; i < size; i++) {
				for(int j = 0; j < size; j++) {
					System.out.printf("%5d", numberplate[i*size + j]);
				}
				System.out.println("");
			}
			
		}else if (canSolve >= 0) {
			System.out.println("This problem has some answers. Example:");
			for(int i = 0; i < size; i++) {
				for(int j = 0; j < size; j++) {
					System.out.printf("%5d", numberplate[i*size + j]);
				}
				System.out.println("");
			}
			System.out.println(); // 区切り
			for(int i = 0; i < size; i++) {
				for(int j = 0; j < size; j++) {
					System.out.printf("%5d", numberplate2[i*size + j]);
				}
				System.out.println("");
			}
					
		}else {
			System.out.println("???");
		}
		
	}
	
	// 入力：　一つだけ解を出してほしかったらtrueで、他に解があるかも確かめてほしかったらfalseで
	// 出力：　-4: ?, -3 : no answer, -2 : uniqueAnswer=trueで解を求めたとき, -1 : can solve, 
	// k : some answers (index k won't be decidable) 但しインデックスは0始まり（一番左上が0）
	public int solve (boolean uniqueAnswer) { 
		for (int i = 0; i < size*size; i++) {
			if (numberplate[i] == 0) { 
				yet.add(i); // 【yet作成】
				numberplate2[i] = size + 1; // 後のため, size==9 なら10が代入される
			}
		}
			
		int empty = yet.size();
		int i = 0;
	
		// 非常に遅い、16×16でもきつい　先に「簡単に」埋まるマスを埋めてしまうことと、仮定した後もできるだけループを深くせず「簡単に」埋まるマスは入れること
		// 下にコメントアウトされたコードも参考になるかと思う
		while (i >= 0 && i < empty) { // すべて埋まるまで、もしくは i<0 で抜ける
			int cell = yet.get(i); // 今から埋めたいセルを一つ選ぶ
			
			for (int num = numberplate[cell]; num <= size; num++) { // 今入っている数字から増やしていく, 既に num=9 でも for の中に入りたいので
				if (canEnter(cell, num, numberplate)) { // 今入っている num は numberplate[cell] に一致するので、絶対に false が返る
					numberplate[cell] = num;
					i++;
					break;
					
				}else if (num == size) { // all number cannot enter
					numberplate[cell] = 0; // 0に戻して
					i--; // 一つ前のセルへ
				}
			}
		}
		if (i == -1) {
			canSolve = -3;
			return -3;
		}
		
		if (i == empty) { // ここまで来たということは、whileをi>=emptyで抜けた、つまり全てのマスをちゃんと埋められた。　しかし、This problem may be incomplete!!
			if (uniqueAnswer == true) {
				canSolve = -2;
				return -2; // これでおわり
			}
			
			int j = 0;
			
			while (j >= 0 && j < empty) {
				int cell = yet.get(j);
				
				for (int num = numberplate2[cell]; num >= 1; num--) { // inverse version
					if (canEnter(cell, num, numberplate2)) {
						numberplate2[cell] = num;
						j++;
						break;
						
					}else if (num == 1) { // all number cannot enter
						numberplate2[cell] = size + 1;
						j--;
					}
				}
			}
			
			for (int k = 0; k < size*size; k++) {
				if (numberplate[k] != numberplate2[k]) {
					canSolve = k+1;
					return k+1;
				}
			}
			canSolve = -1; // can solve
			return -1;
		}
		canSolve = -4;
		return -4; // probably never called...
	}
	
	// third argument is necessary for the latter of method "solve"
	public boolean canEnter(int cell, int num, int[] numberplate) { // true : "num" can enter "cell"
		// cell is an element of "yet"
		
		if (num < 1 || num > size) { // necessary for method "solve"
			return false;
		}
		
		int[] co = COLUMN[cell % size];
		for (int tatecell :co) {
			if (numberplate[tatecell] == num) {
				return false;
			}
		}
		
		int[] ro = ROW[cell / size];
		for (int yokocell :ro) {
			if (numberplate[yokocell] == num) {
				return false;
			}
		}
		
		loop: for (int[] blo :BLOCK) {
			for (int i :blo) {
				if (i == cell) { // cell の属するブロック blo について
					for (int blockcell :blo) {
						if (numberplate[blockcell] == num) {
							return false;
						}
					}
					break loop;
				}
			}
		}
		
		return true;
	}
		
}
	
/*	BOTSU 
    // These deal with renewal of maybe
	static void re_maybe_column (int cell, int num) {
		for (int tatecell : COLUMN[cell % 9]) {
			if (maybe[tatecell][num] == true) {
				maybe[tatecell][num] = false;
				changed.add(tatecell);
			}
		}
	}
	static void re_maybe_row (int cell, int num) {
		for (int yokocell : ROW[cell / 9]) {
			if (maybe[yokocell][num] == true) {
				maybe[yokocell][num] = false;
				changed.add(yokocell);
			}
		}
	}
	static void re_maybe_block (int cell, int num) {
		for (int blockcell : BLOCK[(cell / 27)*3 + (cell % 9)/3]) {
			if (maybe[blockcell][num] == true) {
				maybe[blockcell][num] = false;
				changed.add(blockcell);
			}
		}
	}
	
	
	// These allow a number enter a cell
	static void cellIn (int cell) {
		if (maybe[cell].count(1) == 1) { // cell can accept only one number
			numberplate[cell] = maybe[cell].index(true) + 1;
			maybe[cell] = null;
			re_maybe_column(cell, maybe[cell].index(true) + 1);
			re_maybe_row(cell, maybe[cell].index(true) + 1);
			re_maybe_block(cell, maybe[cell].index(true) + 1);			
		}
	}
	static void columnIn (int[] column, int num) {
		
	}
	static void rowIn (int[] row, int num) {
		
	}
	static void blockIn (int[] block, int num) {
		
	}
	
	
	public void solve () {
		// make maybe
		for (int i = 0; i < 81; i++) {
			if (numberplate[i] == 0) { 
				yet.add(i);
			}else {
				maybe[i] = null;
				re_maybe_column(i, numberplate[i]);
				re_maybe_row(i, numberplate[i]);
				re_maybe_block(i, numberplate[i]);
			}
		}
		
		int count = 0;
		while (yet.size() > 3 || count == 5) { // what number is appropriate?
			for (int num = 1; num < 10; num++) {
				for (int b = 0; b < 9; b++) {
					blockIn(BLOCK[b], num);
				}
			}
			for (int i = 0; i < 81; i++) {
				cellIn(i);
			}
			
			if (yet.size() > 3) {
				for (int num = 1; num < 10; num++) {
					for (int tateyoko = 0; tateyoko < 9; tateyoko++) {
						columnIn(COLUMN[tateyoko], num);
						rowIn(ROW[tateyoko], num);
					}
				}
			}
			count++;
		}
		
		for (int cell : yet) {
			cellIn(cell);
		}
	}*/

	