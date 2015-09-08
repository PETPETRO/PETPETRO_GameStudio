package games.mines;

import java.util.Random;

import games.mines.Tile.State;



/**
 * Field represents playing field and game logic.
 */
public class Field {
	/**
	 * Playing field tiles.
	 */
	private final Tile[][] tiles;

	/**
	 * Field row count. Rows are indexed from 0 to (rowCount - 1).
	 */
	private final int rowCount;

	/**
	 * Column count. Columns are indexed from 0 to (columnCount - 1).
	 */
	private final int columnCount;

	/**
	 * Mine count.
	 */
	private final int mineCount;

	/**
	 * Game state.
	 */
	private GameState state = GameState.PLAYING;
	public long startTime;

	/**
	 * Constructor.
	 *
	 * @param rowCount
	 *            row count
	 * @param columnCount
	 *            column count
	 * @param mineCount
	 *            mine count
	 */
	public Field(int rowCount, int columnCount, int mineCount) {

		if (rowCount < 0) {
			System.err.println("Pocet riadkov nemoze byt mensi ako nula!");
			System.exit(0);

		} else if (columnCount < 0) {
			System.err.println("Pocet stpcov nemoze byt mensi ako nula!");
			System.exit(0);
		} else if (mineCount > rowCount * columnCount || mineCount < 0) {
			System.err.println("Pocet min musi byt vacsi ako nula a mensi alebo rovny poctu dlazdic!");
			System.exit(0);
		}

		this.rowCount = rowCount;
		this.columnCount = columnCount;
		this.mineCount = mineCount;
		tiles = new Tile[rowCount][columnCount];
		generate();
		startTime = System.currentTimeMillis();

	}

	/**
	 * Opens tile at specified indexes.
	 *
	 * @param row
	 *            row number
	 * @param column
	 *            column number
	 */
	public void openTile(int row, int column) {
		Tile tile = tiles[row][column];
		if (tile.getState() == Tile.State.CLOSED) {
			tile.setState(Tile.State.OPEN);
			if (tile instanceof Mine) {
				state = GameState.FAILED;
				return;
			}

			if (isSolved()) {
				state = GameState.SOLVED;
				return;
			}

			if (((Clue) tile).getValue() == 0) {
				openAdjacentTiles(row, column);
			}
		}
	}

	/**
	 * Marks tile at specified indexes.
	 *
	 * @param row
	 *            row number
	 * @param column
	 *            column number
	 */

	public void markTile(int row, int column) {
		Tile tile = getTile(row, column);
		if (tile.getState() == State.CLOSED) {
			tile.setState(State.MARKED);
		} else if (tile.getState() == State.MARKED) {
			tile.setState(State.CLOSED);
		}
	}

	/**
	 * Generates playing field.
	 */
	private void generate() {

		for (int c = 0; c < mineCount; c++) {
			int row = new Random().nextInt(rowCount);
			int column = new Random().nextInt(columnCount);
			if (tiles[row][column] == null) {
				tiles[row][column] = new Mine();
			} else {
				c--;
			}
		}

		for (int r = 0; r < rowCount; r++) {
			for (int c = 0; c < columnCount; c++) {
				if (tiles[r][c] == null) {
					tiles[r][c] = new Clue(countAdjacentMines(r, c));
				}
			}

		}

	}

	/**
	 * Returns true if game is solved, false otherwise.
	 *
	 * @return true if game is solved, false otherwise
	 */
	private boolean isSolved() {
		return ((rowCount * columnCount) - getNumberOf(State.OPEN) == getMineCount());
	}

	/**
	 * Returns number of adjacent mines for a tile at specified position in the
	 * field.
	 *
	 * @param row
	 *            row number.
	 * @param column
	 *            column number.
	 * @return number of adjacent mines.
	 */
	private int countAdjacentMines(int row, int column) {
		int count = 0;
		for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
			int actRow = row + rowOffset;
			if (actRow >= 0 && actRow < rowCount) {
				for (int columnOffset = -1; columnOffset <= 1; columnOffset++) {
					int actColumn = column + columnOffset;
					if (actColumn >= 0 && actColumn < columnCount) {
						if (tiles[actRow][actColumn] instanceof Mine) {
							count++;
						}
					}
				}
			}
		}

		return count;
	}

	public int getRemainingMineCount() {
		return mineCount - getNumberOf(State.MARKED);
	}

	/**
	 * Returns count of rows of current field.
	 * 
	 * @return count of rows of current field
	 */
	public int getRowCount() {
		return rowCount;
	}

	/**
	 * Returns count of columns of current field.
	 * 
	 * @return count of columns of current field
	 */
	public int getColumnCount() {
		return columnCount;
	}

	/**
	 * Returns count of mines in field.
	 * 
	 * @return count of mines in field
	 */
	public int getMineCount() {
		return mineCount;
	}

	/**
	 * Returns current state of game (PLAYING, FAILED, SOLVED).
	 * 
	 * @return current state of game
	 */
	public GameState getState() {
		return state;
	}

	/**
	 * Returns tile in current row and column.
	 * 
	 * @param row
	 *            row number
	 * @param column
	 *            column number
	 * @return tile in current row and column
	 */
	public Tile getTile(int row, int column) {
		return tiles[row][column];
	}

	/**
	 * Returns string that represents field of tiles.
	 * 
	 * @return string that represents field of tiles
	 */
	@Override
	public String toString() {

		String s = "";
		for (int row = 0; row < rowCount; row++) {
			for (int column = 0; column < columnCount; column++) {
				s += tiles[row][column].toString();
			}

			// s+="\n";

			s += "\n";

		}
		return s;
	}

	private int getNumberOf(Tile.State state) {
		int numberOf = 0;
		for (int row = 0; row < rowCount; row++) {
			for (int column = 0; column < columnCount; column++) {
				if (state == getTile(row, column).getState())
					numberOf++;
			}
		}
		return numberOf;
	}

	public void openAdjacentTiles(int row, int column) {
		for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
			int actRow = row + rowOffset;
			if (actRow >= 0 && actRow < rowCount) {
				for (int columnOffset = -1; columnOffset <= 1; columnOffset++) {
					int actColumn = column + columnOffset;
					if (actColumn >= 0 && actColumn < columnCount) {
						openTile(actRow, actColumn);
					}
				}
			}
		}
	}

	public long getTime() {
		return (System.currentTimeMillis() - startTime) / 1000;
	}

}
