package de.avtest.testaufgabe.juniortask.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.avtest.testaufgabe.juniortask.data.enums.GameBoardSliceType;
import de.avtest.testaufgabe.juniortask.data.enums.GameMark;
import de.avtest.testaufgabe.juniortask.data.enums.GamePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class GameBoard {

  @JsonProperty("size")
  // Board size
  private final int size = 4;

  @JsonProperty("board")
  // Board data storage
  private List<GameMark> board;

  @JsonProperty("lastPlayer")
  private GamePlayer lastPlayer;

  public GameBoard() {
    this.clear();
  }

  /**
   * Checks position validity and throws an exception in case of an invalid position
   * @param x X position
   * @param y Y position
   * @throws Exception if the position is outside of the {@link GameBoard}
   */
  private void validatePosition(int x, int y) {
    if (x < 0 || x >= size) throw new RuntimeException(String.format("Invalid X position: %s", x));
    if (y < 0 || y >= size) throw new RuntimeException(String.format("Invalid Y position: %s", x));
  }

  /**
   * Returns the content of a given space
   * @param x X position
   * @param y Y position
   * @return
   */
  @JsonIgnore
  public GameMark getSpace(int x, int y) {
    this.validatePosition(x, y);
    return this.board.get(y * size + x);
  }

  /**
   * Sets the content of a given space
   * @param x
   * @param y
   * @param gameMark
   */
  public void setSpace(int x, int y, GameMark gameMark) {
    this.validatePosition(x, y);
    this.lastPlayer = gameMark.player();
    this.board.remove(y * size + x);
    this.board.add(y * size + x, gameMark);
  }

  /**
   * Returns an entire row
   * @param row
   * @return
   */
  public GameBoardSlice getRow(int row) {
    return new GameBoardSlice(this, GameBoardSliceType.ROW, this.size, row);
  }

  /**
   * Return all rows
   * @return
   * @throws Exception
   */
  @JsonIgnore
  public List<GameBoardSlice> getRows() {
    var temp = new ArrayList<GameBoardSlice>(this.size);
    for (int i = 0; i < this.size; i++) {
      temp.add(this.getRow(i));
    }
    return temp;
  }

  /**
   * Returns an entire row
   * @param row
   * @return
   */
  public GameBoardSlice getColumn(int row) {
    return new GameBoardSlice(this, GameBoardSliceType.COLUMN, this.size, row);
  }

  /**
   * Return all columns
   * @return
   * @throws Exception
   */
  @JsonIgnore
  public List<GameBoardSlice> getColumns() {
    var temp = new ArrayList<GameBoardSlice>(this.size);
    for (int i = 0; i < this.size; i++) {
      temp.add(this.getColumn(i));
    }
    return temp;
  }

  /**
   * Returns the main diagonal
   * @return
   */
  @JsonIgnore
  public GameBoardSlice getMainDiagonal() {
    return new GameBoardSlice(this, GameBoardSliceType.MAIN_DIAGONAL, this.size);
  }

  /**
   * Returns the anti diagonal
   * @return
   */
  @JsonIgnore
  public GameBoardSlice getAntiDiagonal() {
    return new GameBoardSlice(this, GameBoardSliceType.ANTI_DIAGONAL, this.size);
  }

  /**
   * Clears the GameBoard
   */
  private void clear() {
    // Create a square empty board based on the size
    this.board = new ArrayList<>(size * size);
    IntStream.range(0, size * size).forEach(value -> this.board.add(GameMark.NONE));

    // Reset last player
    this.lastPlayer = null;
  }

  /**
   * Is there still some space left on the game board?
   * @return {@link Boolean} indicating if there is a free spot on the board
   */
  public boolean spaceIsLeft() {
    return this.board.stream().anyMatch(GameMark::isFree);
  }

  public String draw() {
    var gameBoardString = new StringBuilder("   ");
    // draw the first row with the column numbers
    for (int i = 0; i < this.size; i++) {
      gameBoardString.append(String.format("%s  ", i));
    }

    gameBoardString.append(System.lineSeparator());

    // iterate over all rows
    for (int i = 0; i < this.size; i++) {
      var row = this.getRow(i);
      // draw the row number in each row
      gameBoardString.append(String.format("%s ", i));

      // get all spaces inside the row
      for (int j = 0; j < row.getSpaces().size(); j++) {
        var space = row.getSpace(j);
        if (j != 0) {
          gameBoardString.append("|");
        }

        // if the row is the last row and the game mark is "none", draw spaces instead of underscores
        if (i == this.size - 1 && GameMark.NONE.equals(space)) {
          gameBoardString.append("  ");
        } else {
          gameBoardString.append(space.symbol);
        }
      }

      // close the row
      gameBoardString.append(System.lineSeparator());
    }

    return gameBoardString.toString();
  }

  public GamePlayer getLastPlayer() {
    return lastPlayer;
  }

  public int getSize() {
    return size;
  }
}
