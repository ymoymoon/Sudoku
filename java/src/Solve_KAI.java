import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Solve_KAI extends Solve{
	// remain to fix: readFromFile の正規表現, canEnter のBLOCKがいくつめに入っているかの式
	Solve_KAI(int size){ // different size
		if (size == 4) {
			this.size = 4;
//			blocksize = 2;
			numberplate = new int[16];
			numberplate2 = new int[16];
			COLUMN = new int[][] {{0, 4, 8, 12}, 
				{1, 5, 9, 13},
				{2, 6, 10, 14},
				{3, 7, 11, 15}};
			ROW = new int[][] {{0, 1, 2, 3}, 
				{4, 5, 6, 7}, 
				{8, 9, 10, 11}, 
				{12, 13, 14, 15}};
			BLOCK = new int[][] {{0, 1, 4, 5}, 
				{2, 3, 6, 7},
				{8, 9, 12, 13},
				{10, 11, 14, 15}};
				
		}else if (size == 6) {
			this.size = 6;
			numberplate = new int[36];
			numberplate2 = new int[36];
			// cell i is in COLUMN[i%6]
			  COLUMN = new int[][] {{0, 6, 12, 18, 24, 30},
						{1, 7, 13, 19, 25, 31},
						{2, 8, 14, 20, 26, 32},
						{3, 9, 15, 21, 27, 33},
						{4, 10, 16, 22, 28, 34},
						{5, 11, 17, 23, 29, 35}};
			// cell i is in ROW[i/6]
				  ROW = new int[][] {{0, 1, 2, 3, 4, 5},
						{6, 7, 8, 9, 10, 11},
						{12, 13, 14, 15, 16, 17},
						{18, 19, 20, 21, 22, 23},
						{24, 25, 26, 27, 28, 29},
						{30, 31, 32, 33, 34, 35}};
		    // cell i is in BLOCK[(i/12)*2 + (i%6)/3] 幾何学ナンプレはここかえる
				 BLOCK = new int[][] {{0, 1, 2, 6, 7, 8},
						 {3, 4, 5, 9, 10, 11},
						 {12, 13, 14, 18, 19, 20},
						 {15, 16, 17, 21, 22, 23},
						 {24, 25, 26, 30, 31, 32},
						 {27, 28, 29, 33, 34, 35}};
			
		}else if (size == 9) {
			this.size = 9;
//			blocksize = 3;
			numberplate = new int[81];
			numberplate2 = new int[81];
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
			
		}else if (size == 12) {
			this.size = 12;
			numberplate = new int[144];
			numberplate2 = new int[144];
			COLUMN = new int[12][12];
			for (int i = 0; i < 12; i++) {
				for (int j = 0; j < 12; j++) {
					COLUMN[i][j] = 12 * j + i;
				}
			}
			ROW = new int[12][12];
			for (int i = 0; i < 12; i++) {
				for (int j = 0; j < 12; j++) {
					ROW[i][j] = 12 * i + j;
				}
			}
			BLOCK = new int[12][12];
			int[] tmp;
			for (int num = 0; num < 144; num++) {
				tmp = BLOCK[(num/36)*3 + (num%12)/4]; // num の属するブロック（セルの集合）
				for (int j = 0; j < 12; j++) {
					if (tmp[j] == 0) { // BLOCK[0] の0の位置がずれるが気にしない
						BLOCK[(num/36)*3 + (num%12)/4][j] = num;
						break;
					}
				}
			}
			
		}else if (size == 16) {
			this.size = 16;
//			blocksize = 4;
			numberplate = new int[256];
			numberplate2 = new int[256];
			COLUMN = new int[16][16];
			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 16; j++) {
					COLUMN[i][j] = 16 * j + i;
				}
			}
			ROW = new int[16][16];
			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 16; j++) {
					ROW[i][j] = 16 * i + j;
				}
			}
			BLOCK = new int[16][16];
			int[] tmp;
			for (int num = 0; num < 256; num++) {
				tmp = BLOCK[(num/64)*4 + (num%16)/4];
				for (int j = 0; j < 16; j++) {
					if (tmp[j] == 0) { // BLOCK[0] の0の位置がずれるが気にしない
						BLOCK[(num/64)*4 + (num%16)/4][j] = num;
						break;
					}
				}
			}
			
		}else if (size == 25) {
			this.size = 25;
//			blocksize = 5;
			numberplate = new int[625];
			numberplate2 = new int[625];
			COLUMN = new int[][] {};
			for (int i = 0; i < 25; i++) {
				for (int j = 0; j < 25; j++) {
					COLUMN[i][j] = 25 * j + i;
				}
			}
			ROW = new int[][] {};
			for (int i = 0; i < 25; i++) {
				for (int j = 0; j < 25; j++) {
					ROW[i][j] = 25 * i + j;
				}
			}
			BLOCK = new int[][] {};
			int[] tmp;
			for (int num = 0; num < 625; num++) {
				tmp = BLOCK[(num/125)*5 + (num%25)/5];
				for (int j = 0; j < 25; j++) {
					if (tmp[j] == 0) { // BLOCK[0] の0の位置がずれるが気にしない
						BLOCK[(num/125)*5 + (num%25)/5][j] = num;
						break;
					}
				}
			}
			
		}else if (size == 30) {
			this.size = 30;
			numberplate = new int[900];
			numberplate2 = new int[900];
			COLUMN = new int[30][30];
			for (int i = 0; i < 30; i++) {
				for (int j = 0; j < 30; j++) {
					COLUMN[i][j] = 30 * j + i;
				}
			}
			ROW = new int[30][30];
			for (int i = 0; i < 30; i++) {
				for (int j = 0; j < 30; j++) {
					ROW[i][j] = 30 * i + j;
				}
			}
			BLOCK = new int[30][30];
			int[] tmp;
			for (int num = 0; num < 900; num++) {
				tmp = BLOCK[(num/150)*5 + (num%30)/6]; // num の属するブロック（セルの集合）
				for (int j = 0; j < 30; j++) {
					if (tmp[j] == 0) { // BLOCK[0] の0の位置がずれるが気にしない
						BLOCK[(num/150)*5 + (num%30)/6][j] = num;
						break;
					}
				}
			}
			
		}else {
			System.out.println("Size should be 4, 6, 9, 12, 16, or 25. Other sizes are preparing...");
			System.exit(0);
		}
	}
	
	Solve_KAI(int[][] block){ // geometric version
		this.size = 9;
//		blocksize = 3;
		numberplate = new int[81];
		numberplate2 = new int[81];
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
		// confirm size!!
		BLOCK = block;
	}
	
	Solve_KAI(int size, int[][] block){ // big and geometric version
		if (size == 4) {
			this.size = 4;
//			blocksize = 2;
			numberplate = new int[16];
			numberplate2 = new int[16];
			COLUMN = new int[][] {{0, 4, 8, 12}, 
				{1, 5, 9, 13},
				{2, 6, 10, 14},
				{3, 7, 11, 15}};
			ROW = new int[][] {{0, 1, 2, 3}, 
				{4, 5, 6, 7}, 
				{8, 9, 10, 11}, 
				{12, 13, 14, 15}};
			BLOCK = block;
			
		}else if (size == 9) {
			this.size = 9;
//			blocksize = 3;
			numberplate = new int[81];
			numberplate2 = new int[81];
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
			BLOCK = block;
			
		}else if (size == 16) {
			this.size = 16;
//			blocksize = 4;
			numberplate = new int[256];
			numberplate2 = new int[256];
			COLUMN = new int[][] {};
			ROW = new int[][] {};
			BLOCK = block;
			
		}else if (size == 25) {
			this.size = 25;
//			blocksize = 5;
			numberplate = new int[625];
			numberplate2 = new int[625];
			COLUMN = new int[][] {};
			ROW = new int[][] {};
			BLOCK = block;
			
		}else {
			System.out.println("Size should be 4, 9, 16, or 25. Other sizes are preparing...");
			System.exit(0);
		}
	}
	
	
	public void readFromFile_KAI(String filename) {
		Scanner sc = null;
		
		try {
			File file  = new File(filename);
			BufferedReader br = new BufferedReader(new FileReader(file));
			sc = new Scanner(br);
			String ideal = "";
			
			// contents of the file to array
			if (this.size == 9) {
				ideal = "((\\d)(\\s)+){8}(\\d)";
			}else if (this.size == 4) {
				ideal = "([0-4](\\s)+){3}[0-4]";
			}else if (this.size == 6) {
				ideal = "([0-6](\\s)+){5}[0-6]";
			}else if (this.size == 12) {
				ideal = "(([0-9]|(1[0-2]))(\\s)+){11}([0-9]|(1[0-2]))";
			}else if (this.size == 16){
				ideal = "(([0-9]|(1[0-6]))(\\s)+){15}([0-9]|(1[0-6]))";
			}else if (this.size == 25){
				ideal = "(([0-9]|(1[0-9])|(2[0-5]))(\\s)+){24}([0-9]|(1[0-9])|(2[0-5]))";
			}else if (this.size == 30){
				ideal = "(([0-9]|([1-2][0-9])|(30))(\\s)+){29}([0-9]|([1-2][0-9]|(30)))";
			}else {
				System.out.println("This size is not supported.");
				System.exit(0);
			}
			String[] tmpline;
			
			for(int i = 0; i < this.size; i++) {
				String tmp = br.readLine();
				tmp = tmp.trim();
				if(tmp.matches(ideal)) { //形が合っているならば
					
					tmpline = tmp.split("(\\s)+");
					for (int j = 0; j < this.size; j++) {
						int tmp2 = Integer.parseInt(tmpline[j]);//exceptionの可能性?
						if (tmp2 == 0 || canEnter(this.size*i+j, tmp2, numberplate)) { // おかしな問題でない限りtrue
							numberplate[this.size*i + j] = tmp2; // 【初期化】
							numberplate2[this.size*i + j] = tmp2; // 後のため
						}else {
							System.out.println("This problem cannot be solved.");
							System.exit(0);
						}
						
					}
					
				} else if(tmp.isEmpty()) { 
					i = i - 1;
				} else { //形があっていない
					System.out.println("Each line should be like '2 0 5 0 0 0 4 0 8' when size == 9 (0 means no number in the cell.)");
					System.exit(0);
				}
			}
		
//			if(sc.hasNext()){
//				System.out.println("There should be only " + this.size +" lines.");
//				System.exit(0);
//			}
			
			
		}catch(IOException e) {
			System.out.println(filename + " was not found.");
			System.exit(0);
		}catch(NumberFormatException e) {
			System.out.println("範囲外の数字が書かれている可能性があります");
			System.exit(0);
		}catch (NullPointerException e){
			System.out.println("行数が少ない可能性があります");
			System.exit(0);
		}finally {
			sc.close();
		}
		
	}
	
	

}
