import java.util.Random;

public class Make {
	int size = 9;
	int[] numberplate = new int[81];
	
	public void makeandOutput() {
		Random r = new Random();
		Solve solve = new Solve();
		int count = 0;
		int faillimit = 0;
		int num;
		int cell;
		
		while (count < 23 && faillimit < 20) { // 22, 20 is arbitrary
			num = r.nextInt(size) + 1;
			cell = r.nextInt(size*size - 8) + 8; // 10 is arbitrary, 後半のみ埋める
			if (numberplate[cell] == 0 && solve.canEnter(cell, num, numberplate)) {
				numberplate[cell] = num; // その位置にヒントを追加
				count++;
			} else {
				faillimit++;
			}
		}
		
		solve = new Solve(numberplate); // 解く、数回に1回ほど解がない場合もでてくる
		int canSolve = solve.solve(false);
		if (canSolve < -2) {
			System.out.println("Failed..."); // 数字を減らしていくのもあり
			System.out.println();
			outputToConsole(numberplate);
			System.exit(0);
		}
		
		while (canSolve > -1) { // 解けるパズルになる (canSolve == -1) まで, かなり重い
			numberplate[canSolve] = solve.numberplate[canSolve];
			solve = new Solve(numberplate);
			canSolve = solve.solve(false);
			count++;
		}
		
		cell = 0; // ちょっとヒントを減らせないか
		while (cell <= 30) {
			int tmp = numberplate[cell]; // 退避
			if (tmp != 0) {
				numberplate[cell] = 0;
				solve = new Solve(numberplate);
				canSolve = solve.solve(false); // ここで-3になっているときがある　数字を抜いただけなのになぜ？
				if (canSolve >= 0) { // そのマスは0にしてはいけない
					numberplate[cell] = tmp;
				} else { // そのマスから数字を抜いてもいい
					count--;
				}
			}
			cell++;
		}
		
		solve = new Solve(numberplate);
		canSolve = solve.solve(false); // 上のwhile で更新したものを戻すために必要
		if (canSolve == -1) {
			System.out.println("Succeeded! Number of hints is " + count);
			System.out.println();
			outputToConsole(numberplate);
		} else { // なぜ呼ばれる？
			System.out.println("Failed...." + canSolve);
			System.out.println();
			outputToConsole(numberplate);
		}
		
	}
	
	public void outputToConsole(int[] numberplate) {
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				System.out.printf("%5d", numberplate[i*size + j]);
			}
			System.out.println("");
		}
	}

}
