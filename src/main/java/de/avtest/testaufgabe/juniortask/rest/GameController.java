package de.avtest.testaufgabe.juniortask.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
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

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("api/game")
public class GameController {

  private Map<String, GameBoard> storedGames = new LinkedHashMap<>();
  private final Random random = new Random();
  @Autowired private CopyrightController copyrightController;

  /**
   *
   * @param gameBoard
   * @return
   */
  protected ResponseEntity<String> statusOutput(GameBoard gameBoard, String gameId) throws IOException {
    var winner = this.whoHasWon(gameBoard);
    var finalOutput = "";
    if (this.someoneHasWon(gameBoard) && winner == null) {
      finalOutput = System.lineSeparator() + "Someone won the game";
      this.storedGames.remove(gameId);
    } else if (this.someoneHasWon(gameBoard) && winner.equals(GamePlayer.HUMAN)) {
      finalOutput = System.lineSeparator() + "You won the game! Congratulations!";
      this.storedGames.remove(gameId);
    } else if (this.someoneHasWon(gameBoard) && winner.equals(GamePlayer.ROBOT)) {
      finalOutput = System.lineSeparator() + "The bot won the game...";
      this.storedGames.remove(gameId);
    } else if (!gameBoard.spaceIsLeft()) {
      finalOutput = System.lineSeparator() + "It's a draw";
    } else {
      finalOutput = "";
    }

    saveGames();
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
    // Check diagonals
    if(gameBoard.getMainDiagonal().containsSameMarks() || gameBoard.getAntiDiagonal().containsSameMarks()){
      return true;
    }

    // Check rows and columns
    for(int i = 0; i < gameBoard.getSize(); i++){
      if(gameBoard.getColumns().get(i).containsSameMarks() || gameBoard.getRows().get(i).containsSameMarks()){
        return true;
      }
    }

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
  public ResponseEntity<String> play(String gameId, @RequestParam int x, @RequestParam int y) throws IOException {

    // Loading the game board
    var gameBoard = this.storedGames.get(gameId);
    if(gameBoard == null){
      loadGames();
      gameBoard = this.storedGames.get(gameId);
    }


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
    if (gameBoard.getSpace(x, y).isMarked()){
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This space has already been claimed!");
    }

    // Set player's mark
    gameBoard.setSpace( x, y, GameMark.CIRCLE );

    // Saving the game board and output it to the player
    return this.statusOutput(gameBoard, gameId);
  }

  @GetMapping(value = "playBot", produces = "text/plain")
  public ResponseEntity<String> playBot(String gameId) throws IOException {
    // Loading the game board
    var gameBoard = this.storedGames.get(gameId);
    if(gameBoard == null){
      loadGames();
      gameBoard = this.storedGames.get(gameId);
    }

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

    return this.statusOutput(gameBoard, gameId);
  }

  @GetMapping(value = "display", produces = "text/plain")
  public ResponseEntity<String> display(String gameId) throws IOException {
    // Loading the game board
    var gameBoard = this.storedGames.get(gameId);
    if(gameBoard == null){
      loadGames();
      gameBoard = this.storedGames.get(gameId);
    }
    return this.statusOutput(gameBoard, gameId);
  }

  @GetMapping(value = "create", produces = "text/plain")
  public ResponseEntity<String> create() throws IOException {
    // Loading the game board
    var uuid = UUID.randomUUID().toString();
    storedGames.put(uuid, new GameBoard());

    // Save games to JSON
    saveGames();

    return ResponseEntity.ok(uuid);
  }

  /**
   * Loads the map of stored games from json file
   *
   * @throws IOException
   */
  private void loadGames() throws IOException {
    ObjectMapper om = new ObjectMapper();
    Map<String, GameBoard> loadedGames = om.readValue(new File("target/storedGames.json"), TypeFactory.defaultInstance().constructMapType(LinkedHashMap.class, String.class, GameBoard.class));
    this.storedGames = loadedGames;
  }

  /**
   * Saves the map of stored games to json file
   *
   * @throws IOException
   */
  private void saveGames() throws IOException {
    ObjectMapper om = new ObjectMapper();
    om.writeValue(new File("target/storedGames.json"), this.storedGames);
  }
}
