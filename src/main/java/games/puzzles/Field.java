package games.puzzles;

import java.io.Serializable;
import java.util.Random;

public class Field implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Tile[][] tiles;

	private final int rowCount;

	private final int columnCount;
	private GameState state = GameState.PLAYING;
	public long startTime;

	public Field(int rowCount, int columnCount) {

		if (rowCount < 1) {
			System.err.println("Pocet riadkov musi byt vacsi ako nula!");
			System.exit(0);

		} else if (columnCount < 1) {
			System.err.println("Pocet stpcov musi byt vacsi ako nula!");
			System.exit(0);
		}
		this.rowCount = rowCount;
		this.columnCount = columnCount;
		tiles = new Tile[rowCount][columnCount];
		generate();
		startTime = System.currentTimeMillis();
	}

	public void generate() {

		int i = 1;
		for (int r = 0; r < rowCount; r++) {
			for (int c = 0; c < columnCount; c++) {
				tiles[r][c] = new Puzzle(i++);
			}
		}
		shufflePuzzles();
	}

	public void shufflePuzzles() {
		for (int i = 0; i < 100; i++) {
			int r1 = 0;
			int r2 = 0;
			int c1 = 0;
			int c2 = 0;
			do {
				r1 = new Random().nextInt(rowCount);
				c1 = new Random().nextInt(columnCount);
				r2 = new Random().nextInt(rowCount);
				c2 = new Random().nextInt(columnCount);
			} while (r1 == r2 && c1 == c2);
			swapPuzzles(r1, c1, r2, c2);
		}
	}

	public void swapPuzzles(int row1, int column1, int row2, int column2) {
		Tile tile = tiles[row1][column1];
		tiles[row1][column1] = tiles[row2][column2];
		tiles[row2][column2] = tile;
	}

	public boolean isSolved(Field field) {
		for (int r = 0; r < rowCount; r++) {
			for (int c = 0; c < (columnCount); c++) {
				Tile tile = field.getTile(r, c);
				if ((tile instanceof Puzzle && ((Puzzle) tile).getValue() == (r * columnCount + c + 1))) {
				} else
					return false;
			}
		}
		return true;
	}

	public GameState getState() {
		return state;
	}

	public long getTime() {
		return (System.currentTimeMillis() - startTime) / 1000;
	}

	public void setState(GameState state) {
		this.state = state;
	}

	public Tile getTile(int row, int column) {
		return tiles[row][column];
	}

	public int getRowCount() {
		return rowCount;
	}

	public int getColumnCount() {
		return columnCount;
	}

}
