package de.avtest.testaufgabe.juniortask.data.enums;

public enum GameMark {

  NONE("__"),
  CROSS("❌"),
  CIRCLE("⭕");

  public final String symbol;

  /**
   * @return Returns true if the space is free
   */
  public boolean isFree() {
    return this == GameMark.NONE;
  }

  /**
   * @return Returns true if the spot is already marked
   */
  public boolean isMarked() {
    return !isFree();
  }

  /**
   * Returns the player that has marked this spot, or null if the spot is free
   * @return {@link GamePlayer} or {@code null}
   */
  public GamePlayer player() {
    return switch (this) {
      case CROSS -> GamePlayer.ROBOT;
      case CIRCLE -> GamePlayer.HUMAN;
      case NONE -> null;
    };
  }

  GameMark(String symbol) {
    this.symbol = symbol;
  }
}
