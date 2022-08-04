package de.avtest.testaufgabe.juniortask.data;

import de.avtest.testaufgabe.juniortask.data.enums.GameBoardSliceType;
import de.avtest.testaufgabe.juniortask.data.enums.GameMark;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class GameBoardSlice {
  // Slice type
  private final GameBoardSliceType type;
  // Main GameBoard instance
  private final GameBoard gameBoard;
  // Board size
  private final int size;
  // Offset position (only needed for ROW and COL)
  private final int other;

  public GameBoardSlice(GameBoard gameBoard, GameBoardSliceType type, int size, int other) {
    this.gameBoard = gameBoard;
    this.type = type;
    this.size = size;
    this.other = other;

    if (size <= 0) throw new RuntimeException("Board slice size must be at least 1");
    if (other < 0 || other >= size) throw new RuntimeException("Board slice offset invalid");
  }

  public GameBoardSlice(GameBoard gameBoard, GameBoardSliceType type, int size) {
    this.gameBoard = gameBoard;
    this.type = type;
    this.size = size;
    this.other = 0;

    if (size <= 0) throw new RuntimeException("Board slice size must be at least 1");
  }

  /**
   * Gets the content of a given space
   * @param p
   * @return
   */
  public GameMark getSpace(int p) {
    return switch (this.type) {
      case ROW -> this.gameBoard.getSpace(p, this.other);
      case COLUMN -> this.gameBoard.getSpace(this.other, p);
      case MAIN_DIAGONAL -> this.gameBoard.getSpace(p, p);
      case ANTI_DIAGONAL -> this.gameBoard.getSpace(p, this.size - 1 - p);
    };
  }

  /**
   * Sets the content of a given space
   * @param p
   * @param gameMark
   */
  public void setSpace(int p, GameMark gameMark) {
    switch (this.type) {
      case ROW -> this.gameBoard.setSpace(p, this.other, gameMark);
      case COLUMN -> this.gameBoard.setSpace(this.other, p, gameMark);
      case MAIN_DIAGONAL -> this.gameBoard.setSpace(p, p, gameMark);
      case ANTI_DIAGONAL -> this.gameBoard.setSpace(p, this.size - 1 - p, gameMark);
    }
  }

  /**
   * Get all spaces belonging to one row inside an array.
   * @return
   */
  public List<GameMark> getSpaces() {
    return IntStream.range(0, this.size).mapToObj(this::getSpace).toList();
  }

  /**
   * Checks if every position of the slice contains the same player mark type
   *
   * @return true, if all player marks of the slice are the same
   */
  public boolean containsSameMarks(){
    List<GameMark> markList = getSpaces();
    Set<GameMark> markSet = new HashSet<GameMark>(markList);
    if(!markList.contains(GameMark.NONE) && markSet.size() == 1){
      return true;
    }
    return false;
  }
}
