
public class Main {
	
	//solve
	public static void main(String[] args) {
		
		// to solve
		if (args.length != 1) {
			System.out.println("One filename, please.");
			System.exit(0);
		}
		
		boolean uniqueAnswer = false;
		// uniqueAnswerがTrueのときは、先に大きな数字をできるだけ入れておいた方が速くできる
		
//		int[][] block = {{0, 9, 10, 18, 19, 27, 28, 36, 37},
//				{1, 2, 3, 11, 12, 20, 21, 29, 30},
//				{38, 45, 46, 47, 54, 55, 56, 63, 64},
//				{39, 48, 57, 65, 66, 72, 73, 74, 75},
//				{4, 5, 13, 22, 23, 31, 32, 40, 41},
//				{6, 7, 8, 14, 15, 16, 17, 25, 26},
//				{24, 33, 34, 35, 43, 44, 52, 53, 62},
//				{42, 49, 50, 51, 58, 59, 60, 67, 76},
//				{61, 68, 69, 70, 71, 77, 78, 79, 80}};
//		Solve_KAI solve = new Solve_KAI(block); // geometry
		Solve_KAI solve = new Solve_KAI(16); // big or small
//		Solve_KAI solve = new Solve_KAI(9); // normal
		String filename = args[0];
		solve.readFromFile_KAI(filename);
		
		solve.solve(uniqueAnswer); // Solve 内のメソッドを継承している
		solve.outputToConsole();
		
		
		// to make
//		if (args.length != 0) {
//			System.out.println("No arguments, please.");
//			System.exit(0);
//		}
//		Make m = new Make();
//		m.makeandOutput();
		
	}

}
