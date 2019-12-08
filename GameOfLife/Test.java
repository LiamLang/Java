import java.util.Random;

public class Test {

	private static final int t = 5;

	private static final int xDim = 192 * 5;
	private static final int yDim = 108 * 5;
	private static final int px = 10 / 5;

	public static void main(String[] args) throws Exception {

		// Cell[][] cells = randomCells();

		Cell[][] cells = blankCells();

		addBlinker(cells, 1, 2);
		addBeacon(cells, 10, 1);

		addGlider(cells, 800, 200);

		addDiehard(cells, 90, 50);

		addAcorn(cells, 300, 200);

		addGliderGun(cells, 600, 250);

		addRPentomino(cells, 850, 50);

		addLwss(cells, 10, 20);
		
		addBlockLayingSwitchEngine(cells, 800, 300);
		
		addLwss(cells, 10, 480);
		addLwss(cells, 20, 490);
		addLwss(cells, 10, 500);

		Game game = new Game(cells, px);

		Thread.sleep(5000);

		while (true) {

			Thread.sleep(t);
			game.step();
		}
	}

	private static void addBlinker(Cell[][] cells, int x, int y) {
		addSet(cells, new int[][] {

				{ 0, 0 }, { 1, 0 }, { 2, 0 }

		}, x, y);
	}

	private static void addBeacon(Cell[][] cells, int x, int y) {
		addSet(cells, new int[][] {

				{ 0, 0 }, { 1, 0 }, { 0, 1 }, { 1, 1 },

				{ 2, 2 }, { 3, 2 }, { 2, 3 }, { 3, 3 }

		}, x, y);
	}

	private static void addGlider(Cell[][] cells, int x, int y) {
		addSet(cells, new int[][] {

				{ 0, 0 }, { 1, 1 }, { 2, 1 }, { 0, 2 }, { 1, 2 }

		}, x, y);
	}

	private static void addDiehard(Cell[][] cells, int x, int y) {
		addSet(cells, new int[][] {

				{ 0, 1 }, { 1, 1 }, { 1, 2 }, { 6, 0 }, { 5, 2 }, { 6, 2 }, { 7, 2 }

		}, x, y);
	}

	private static void addAcorn(Cell[][] cells, int x, int y) {
		addSet(cells, new int[][] {

				{ 0, 2 }, { 1, 0 }, { 1, 2 }, { 3, 1 }, { 4, 2 }, { 5, 2 }, { 6, 2 }

		}, x, y);
	}

	private static void addRPentomino(Cell[][] cells, int x, int y) {
		addSet(cells, new int[][] {

				{ 0, 1 }, { 1, 0 }, { 1, 1 }, { 1, 2 }, { 2, 0 }

		}, x, y);
	}

	private static void addLwss(Cell[][] cells, int x, int y) {
		addSet(cells, new int[][] {

				{ 0, 1 }, { 0, 3 }, { 1, 0 }, { 2, 0 }, { 3, 0 }, { 4, 0 }, { 4, 1 }, { 4, 2 }, { 3, 3 }

		}, x, y);
	}

	private static void addGliderGun(Cell[][] cells, int x, int y) {
		addSet(cells, new int[][] {

				{ 0, 2 }, { 0, 3 }, { 1, 2 }, { 1, 3 }, { 34, 0 }, { 34, 1 }, { 35, 0 }, { 35, 1 }, { 16, 4 },
				{ 16, 5 }, { 16, 6 }, { 17, 4 }, { 18, 5 }, { 24, 12 }, { 24, 13 }, { 25, 12 }, { 25, 14 }, { 26, 12 },
				{ 35, 7 }, { 35, 8 }, { 35, 9 }, { 36, 7 }, { 37, 8 }, { 8, 3 }, { 8, 4 }, { 9, 2 }, { 9, 4 },
				{ 10, 2 }, { 10, 3 }, { 22, 1 }, { 22, 2 }, { 23, 0 }, { 23, 2 }, { 24, 0 }, { 24, 1 },

		}, x, y);
	}

	private static void addBlockLayingSwitchEngine(Cell[][] cells, int x, int y) {
		addSet(cells, new int[][] {

				{ 0, 5 }, { 2, 4 }, { 2, 5 }, { 4, 1 }, { 4, 2 }, { 4, 3 }, { 6, 0 }, { 6, 1 }, { 6, 2 }, { 7, 1 }

		}, x, y);
	}

	private static void addSet(Cell[][] cells, int[][] coOrds, int offsetX, int offsetY) {
		for (int i = 0; i < coOrds.length; i++) {
			cells[coOrds[i][0] + offsetX][coOrds[i][1] + offsetY].setAlive();
		}
	}

	private static Cell[][] blankCells() {
		Cell[][] cells = new Cell[xDim][yDim];
		for (int i = 0; i < xDim; i++) {
			for (int j = 0; j < yDim; j++) {
				cells[i][j] = new Cell(false);
			}
		}
		return cells;
	}

	private static Cell[][] randomCells() {
		Cell[][] cells = new Cell[xDim][yDim];
		Random random = new Random();
		for (int i = 0; i < xDim; i++) {
			for (int j = 0; j < yDim; j++) {
				cells[i][j] = new Cell(random.nextBoolean());
			}
		}
		return cells;
	}
}