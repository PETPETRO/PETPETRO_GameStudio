package games.stones;

public class Stone extends Tile {

	private static final long serialVersionUID = 1L;
	private final int value;

	public Stone(int value) {
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
