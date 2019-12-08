public class Cell {

	private boolean currentState;
	private boolean nextState;

	private Cell[] neighbours;

	private int numNeighboursAlive;

	public Cell(boolean alive) {
		this.currentState = alive;
	}

	public void setAlive() {
		this.currentState = true;
	}

	public void setNeighbours(Cell[] neighbours) {
		if (neighbours != null) {
			this.neighbours = neighbours;
		}
	}

	public void prepareNextState() {
		if (neighbours != null) {

			nextState = currentState;
			
			numNeighboursAlive = 0;
			for (int i = 0; i < neighbours.length; i++) {
				if (neighbours[i].isAlive()) {
					numNeighboursAlive++;
				}
			}

			if (currentState) {
				if (numNeighboursAlive < 2 || numNeighboursAlive > 3) {
					nextState = false;
				}
			} else {
				if (numNeighboursAlive == 3) {
					nextState = true;
				}
			}
		}
	}

	public void transition() {
		currentState = nextState;
	}

	public boolean isAlive() {
		return currentState;
	}
}