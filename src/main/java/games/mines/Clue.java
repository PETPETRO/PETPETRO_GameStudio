package games.mines;

/**
 * Clue tile.
 */
public class Clue extends Tile {
	/** Value of the clue. */
	private final int value;

	/**
	 * Constructor.
	 * 
	 * @param value
	 *            value of the clue
	 */
	public Clue(int value) {
		this.value = value;
	}

	/**
	 * Returns current value of this clue.
	 * 
	 * @return current value of this clue
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Returns string that represents state of current clue, number of adjacent
	 * mines if OPEN
	 * 
	 * @return string that represents state of current clue
	 */
	@Override
	public String toString() {
		if (this.getState() == State.OPEN) {
			return value + " ";
		} else
			return super.toString();
	}
}
