package de.avtest.testaufgabe.juniortask.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/copyright")
public class CopyrightController {

  String gameName = " _____ _        _____             _____           \n" +
    "/__   (_) ___  /__   \\__ _  ___  /__   \\___   ___ \n" +
    "  / /\\/ |/ __|   / /\\/ _` |/ __|   / /\\/ _ \\ / _ \\\n" +
    " / /  | | (__   / / | (_| | (__   / / | (_) |  __/\n" +
    " \\/   |_|\\___|  \\/   \\__,_|\\___|  \\/   \\___/ \\___|\n" +
    "                                                  \n";
  String author = "\n" +
          "\n" +
          " ______              ______   _______  _       _________ _______  _          _______           _______  _______  _______           _______ \n" +
          "(  ___ \\ |\\     /|  (  __  \\ (  ___  )( (    /|\\__   __/(  ____ \\( \\        (  ____ )|\\     /|(  ____ \\(  ____ \\(  ____ \\|\\     /|(  ____ \\\n" +
          "| (   ) )( \\   / )  | (  \\  )| (   ) ||  \\  ( |   ) (   | (    \\/| (        | (    )|| )   ( || (    \\/| (    \\/| (    \\/| )   ( || (    \\/\n" +
          "| (__/ /  \\ (_) /   | |   ) || (___) ||   \\ | |   | |   | (__    | |        | (____)|| |   | || (__    | (_____ | |      | (___) || (__    \n" +
          "|  __ (    \\   /    | |   | ||  ___  || (\\ \\) |   | |   |  __)   | |        |  _____)| |   | ||  __)   (_____  )| |      |  ___  ||  __)   \n" +
          "| (  \\ \\    ) (     | |   ) || (   ) || | \\   |   | |   | (      | |        | (      | |   | || (            ) || |      | (   ) || (      \n" +
          "| )___) )   | |     | (__/  )| )   ( || )  \\  |___) (___| (____/\\| (____/\\  | )      | (___) || (____/\\/\\____) || (____/\\| )   ( || (____/\\\n" +
          "|/ \\___/    \\_/     (______/ |/     \\||/    )_)\\_______/(_______/(_______/  |/       (_______)(_______/\\_______)(_______/|/     \\|(_______/\n" +
          "                                                                                                                                           \n" +
          "\n";


  @GetMapping(produces = "text/plain")
  public String getCopyright() {
    return gameName + author;
  }

}
