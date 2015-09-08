package games.puzzles;

public class Puzzle extends Tile {

	private static final long serialVersionUID = 1L;
	private final int value;

	public Puzzle(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		return this.getValue() + "";
	}

}
