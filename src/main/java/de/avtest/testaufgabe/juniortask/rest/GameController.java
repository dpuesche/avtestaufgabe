package de.avtest.testaufgabe.juniortask.rest;

import de.avtest.testaufgabe.juniortask.data.GameBoard;
import de.avtest.testaufgabe.juniortask.data.GameBoardSlice;
import de.avtest.testaufgabe.juniortask.data.enums.GameMark;
import de.avtest.testaufgabe.juniortask.data.enums.GamePlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("api/game")
public class GameController {

  private final Map<String, GameBoard> storedGames = new LinkedHashMap<>();
  private final Random random = new Random();
  @Autowired private CopyrightController copyrightController;

  /**
   *
   * @param gameBoard
   * @return
   */
  protected ResponseEntity<String> statusOutput(GameBoard gameBoard) {
    var winner = this.whoHasWon(gameBoard);
    var finalOutput = "";
    if (this.someoneHasWon(gameBoard) && winner == null) {
      finalOutput = System.lineSeparator() + "Someone won the game";
    } else if (this.someoneHasWon(gameBoard) && winner.equals(GamePlayer.HUMAN)) {
      finalOutput = System.lineSeparator() + "You won the game! Congratulations!";
    } else if (this.someoneHasWon(gameBoard) && winner.equals(GamePlayer.ROBOT)) {
      finalOutput = System.lineSeparator() + "The bot won the game...";
    } else if (!gameBoard.spaceIsLeft()) {
      finalOutput = System.lineSeparator() + "It's a draw";
    } else {
      finalOutput = "";
    }

    return ResponseEntity.ok(copyrightController.getCopyright() +
      System.lineSeparator() +
      System.lineSeparator() +
      gameBoard.draw() +
      finalOutput
    );
  }

  /**
   *
   * @param gameBoard
   * @return
   */
  protected boolean someoneHasWon(GameBoard gameBoard) {
    // ##### TASK 7 - Make this check more efficient ###############################################################
    // =============================================================================================================
    // This function checks if the game has already won. It does this by checking for every possible winning
    // condition. For example, the first block below checks if the first row contains identical marks that are not
    // GameMark.NONE.
    // As you can see, this function is exorbitantly long and highly redundant. Your task is to find a way to
    // shorten this function without compromising its functionality. Note that by "shorten", we don't mean to just
    // remove spaces and line breaks ;)
    // =============================================================================================================
    if ( // Check the first row
      gameBoard.getRow(0).getSpace(0) == gameBoard.getRow(0).getSpace(1) &&
        gameBoard.getRow(0).getSpace(0) == gameBoard.getRow(0).getSpace(2) &&
        gameBoard.getRow(0).getSpace(0) != GameMark.NONE
    ) return true;

    if ( // Check the second row
      gameBoard.getRow(1).getSpace(0) == gameBoard.getRow(1).getSpace(1) &&
        gameBoard.getRow(1).getSpace(0) == gameBoard.getRow(1).getSpace(2) &&
        gameBoard.getRow(1).getSpace(0) != GameMark.NONE
    ) return true;

    if ( // Check the third row
      gameBoard.getRow(2).getSpace(0) == gameBoard.getRow(2).getSpace(1) &&
        gameBoard.getRow(2).getSpace(0) == gameBoard.getRow(2).getSpace(2) &&
        gameBoard.getRow(2).getSpace(0) != GameMark.NONE
    ) return true;

    if ( // Check the first column
      gameBoard.getColumn(0).getSpace(0) == gameBoard.getColumn(0).getSpace(1) &&
        gameBoard.getColumn(0).getSpace(0) == gameBoard.getColumn(0).getSpace(2) &&
        gameBoard.getColumn(0).getSpace(0) != GameMark.NONE
    ) return true;

    if ( // Check the second column
      gameBoard.getColumn(1).getSpace(0) == gameBoard.getColumn(1).getSpace(1) &&
        gameBoard.getColumn(1).getSpace(0) == gameBoard.getColumn(1).getSpace(2) &&
        gameBoard.getColumn(1).getSpace(0) != GameMark.NONE
    ) return true;

    if ( // Check the third column
      gameBoard.getColumn(2).getSpace(0) == gameBoard.getColumn(2).getSpace(1) &&
        gameBoard.getColumn(2).getSpace(0) == gameBoard.getColumn(2).getSpace(2) &&
        gameBoard.getColumn(2).getSpace(0) != GameMark.NONE
    ) return true;

    if ( // Check the main diagonal
      gameBoard.getMainDiagonal().getSpace(0) == gameBoard.getMainDiagonal().getSpace(1) &&
        gameBoard.getMainDiagonal().getSpace(0) == gameBoard.getMainDiagonal().getSpace(2) &&
        gameBoard.getMainDiagonal().getSpace(0) != GameMark.NONE
    ) return true;

    if ( // Check the anti-diagonal
      gameBoard.getAntiDiagonal().getSpace(0) == gameBoard.getAntiDiagonal().getSpace(1) &&
        gameBoard.getAntiDiagonal().getSpace(0) == gameBoard.getAntiDiagonal().getSpace(2) &&
        gameBoard.getAntiDiagonal().getSpace(0) != GameMark.NONE
    ) return true;

    return false;
  }

  /**
   *
   * @param gameBoard
   * @return
   */
  protected GamePlayer whoHasWon(GameBoard gameBoard) {
    if(someoneHasWon(gameBoard)){
      return gameBoard.getLastPlayer();
    }
    return null;
  }

  /**
   * Is the given player allowed to take the next turn?
   * @param gameBoard
   * @param player
   * @return
   */
  protected boolean isAllowedToPlay(GameBoard gameBoard, GamePlayer player) {
    if(gameBoard.getLastPlayer() == player){
      return false;
    }
    return true;
  }

  /**
   *
   * @param gameId The ID of the game
   * @param x The x position entered by the player
   * @param y The y position entered by the player
   * @return
   */
  @GetMapping(value = "play", produces = "text/plain")
  public ResponseEntity<String> play(@RequestParam String gameId, @RequestParam int x, @RequestParam int y) {
    // Loading the game board
    var gameBoard = storedGames.get(gameId);

    // Check if the given position is actually valid; can't have the player draw a cross on the table next to the
    // game board ;)
    if (x < 0 || y < 0 || x >= gameBoard.getSize() || y >= gameBoard.getSize()) {
      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Position outside of the game board");
    }

    // Prevent the player from playing if the game has already ended
    if (this.someoneHasWon(gameBoard) || !gameBoard.spaceIsLeft()) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to play. The game has already ended.");
    }

    // Prevent the player from playing if it is not his turn
    if (!this.isAllowedToPlay(gameBoard, GamePlayer.HUMAN)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to play. It is the bots turn!");
    }

    // Prevent the player from picking a position that is not empty
    if (gameBoard.getSpace(x, y) != GameMark.NONE){
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This space has already been claimed!");
    }

    // Set player's mark
    gameBoard.setSpace( x, y, GameMark.CIRCLE );

    // Saving the game board and output it to the player
    return this.statusOutput(gameBoard);
  }

  @GetMapping(value = "playBot", produces = "text/plain")
  public ResponseEntity<String> playBot(@RequestParam String gameId) {
    // Loading the game board
    var gameBoard = storedGames.get(gameId);

    // ##### TASK 5 - Understand the bot ###########################################################################
    // =============================================================================================================
    // This first step to beat your enemy is to thoroughly understand them.
    // Luckily, as a developer, you can literally look into its head. So, check out the bot logic and try to
    // understand what it does.
    // =============================================================================================================

    // Prevent the player from playing if the game has already ended
    if (this.someoneHasWon(gameBoard) || !gameBoard.spaceIsLeft()) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to play. The game has already ended.");
    }

    // Prevent the player from playing if it is not his turn
    if (!this.isAllowedToPlay(gameBoard, GamePlayer.ROBOT)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to play. It is the humans turn!");
    }

    var freeSpaces = new LinkedList<Map<String, Integer>>();
    // get all rows of our game board
    for (int y = 0; y < gameBoard.getSize(); y++) {
      var row = gameBoard.getRow(y);
      // get all spaces inside the row
      for (int x = 0; x < gameBoard.getSize(); x++) {
        // check whether the space is still free
        var space = row.getSpace(x);
        if (space.isFree()) {
          // save the free space to our free spaces list
          freeSpaces.add(Map.of("x", x, "y", y));
        }
      }
    }

    // get random free space from our list
    var randomFreeSpace = freeSpaces.stream().skip(random.nextInt(freeSpaces.size())).findFirst().orElseGet(() -> freeSpaces.get(0));

    gameBoard.setSpace(randomFreeSpace.get("x"), randomFreeSpace.get("y"), GameMark.CROSS);

    return this.statusOutput(gameBoard);
  }

  @GetMapping(value = "display", produces = "text/plain")
  public ResponseEntity<String> display(@RequestParam String gameId) {
    // Loading the game board
    var gameBoard = storedGames.get(gameId);
    return this.statusOutput(gameBoard);
  }

  @GetMapping(value = "create", produces = "text/plain")
  public ResponseEntity<String> create() {
    // Loading the game board
    var uuid = UUID.randomUUID().toString();
    storedGames.put(uuid, new GameBoard());
    return ResponseEntity.ok(uuid);
  }
}
