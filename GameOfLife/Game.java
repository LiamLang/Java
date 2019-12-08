import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Game extends JFrame {

	private int cellDimPx;
	private int numCellsHoriz;
	private int numCellsVert;

	private Cell[][] cells;

	private ArrayList<Cell> neighbours = new ArrayList<Cell>();

	public Game(Cell[][] cells, int cellDimPx) {

		super();

		this.cells = cells;
		this.cellDimPx = cellDimPx;
		this.numCellsHoriz = cells.length;
		this.numCellsVert = cells[0].length;

		setCellNeighbours();

		refreshGui();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void step() {

		for (int i = 0; i < numCellsHoriz; i++) {
			for (int j = 0; j < numCellsVert; j++) {

				cells[i][j].prepareNextState();
			}
		}

		for (int i = 0; i < numCellsHoriz; i++) {
			for (int j = 0; j < numCellsVert; j++) {

				cells[i][j].transition();
			}
		}

		refreshGui();
	}

	private void refreshGui() {

		Squares squares = new Squares();

		for (int i = 0; i < numCellsHoriz; i++) {
			for (int j = 0; j < numCellsVert; j++) {

				if (cells[i][j].isAlive()) {

					squares.addSquare(i * cellDimPx, j * cellDimPx, cellDimPx, cellDimPx);
				}
			}
		}

		//setVisible(false);
		getContentPane().removeAll();
		getContentPane().add(squares);
		pack();
		setSize(cellDimPx * numCellsHoriz, cellDimPx * numCellsVert);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void setCellNeighbours() {
		for (int i = 0; i < numCellsHoriz; i++) {
			for (int j = 0; j < numCellsVert; j++) {

				neighbours.clear();

				if (i > 0) {

					if (j > 0) {
						neighbours.add(cells[i - 1][j - 1]);
					}

					neighbours.add(cells[i - 1][j]);

					if (j < numCellsVert - 1) {
						neighbours.add(cells[i - 1][j + 1]);
					}

				}

				if (j > 0) {
					neighbours.add(cells[i][j - 1]);
				}

				if (j < numCellsVert - 1) {
					neighbours.add(cells[i][j + 1]);
				}

				if (i < numCellsHoriz - 1) {

					if (j > 0) {
						neighbours.add(cells[i + 1][j - 1]);
					}

					neighbours.add(cells[i + 1][j]);

					if (j < numCellsVert - 1) {
						neighbours.add(cells[i + 1][j + 1]);
					}

				}
				

				cells[i][j].setNeighbours(neighbours.toArray(new Cell[0]));

			}
		}
	}
}

class Squares extends JPanel {

	private static final int PREF_W = 100;
	private static final int PREF_H = PREF_W;
	private List<Rectangle> squares = new ArrayList<Rectangle>();

	public void addSquare(int x, int y, int width, int height) {
		Rectangle rect = new Rectangle(x, y, width, height);
		squares.add(rect);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(PREF_W, PREF_H);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		for (Rectangle rect : squares) {
			g2.fillRect(rect.x, rect.y, rect.width, rect.height);
		}
	}

}