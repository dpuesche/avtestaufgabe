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
  String avTest = " __                  ______  __  __          ______  ____    ____    ______   \n" +
    "/\\ \\                /\\  _  \\/\\ \\/\\ \\        /\\__  _\\/\\  _`\\ /\\  _`\\ /\\__  _\\  \n" +
    "\\ \\ \\____  __  __   \\ \\ \\L\\ \\ \\ \\ \\ \\       \\/_/\\ \\/\\ \\ \\L\\_\\ \\,\\L\\_\\/_/\\ \\/  \n" +
    " \\ \\ '__`\\/\\ \\/\\ \\   \\ \\  __ \\ \\ \\ \\ \\  _______\\ \\ \\ \\ \\  _\\L\\/_\\__ \\  \\ \\ \\  \n" +
    "  \\ \\ \\L\\ \\ \\ \\_\\ \\   \\ \\ \\/\\ \\ \\ \\_/ \\/\\______\\\\ \\ \\ \\ \\ \\L\\ \\/\\ \\L\\ \\ \\ \\ \\ \n" +
    "   \\ \\_,__/\\/`____ \\   \\ \\_\\ \\_\\ `\\___/\\/______/ \\ \\_\\ \\ \\____/\\ `\\____\\ \\ \\_\\\n" +
    "    \\/___/  `/___/> \\   \\/_/\\/_/`\\/__/            \\/_/  \\/___/  \\/_____/  \\/_/\n" +
    "               /\\___/                                                         \n" +
    "               \\/__/                                                          ";


  @GetMapping(produces = "text/plain")
  public String getCopyright() {
    return gameName + avTest;
  }

}
