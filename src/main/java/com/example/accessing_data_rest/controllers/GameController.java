package com.example.accessing_data_rest.controllers;

import com.example.accessing_data_rest.model.Game;

import com.example.accessing_data_rest.model.User;
import com.example.accessing_data_rest.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("roborally/game")
public class GameController {

    @Autowired
    private GameService gameService;

    // TODO Assignment 7b: add a method with @GetMapping, which obtains
    //      a list of all games from the games repository  (via the service
    //      getGames) and returns this list (in JSON representation).
    //      See class UserController for inspiration and class GameService
    @GetMapping(value = "", produces="application/json")
    public List<Game> getGames() {
        return gameService.getGames();
    }


    // TODO Assignment 7b: Create a post method in this controller for creating a new game
    //      this method should call the corresponding service for creating a game
    @PostMapping(value = "/create")
    public Game createGame(@RequestBody Game game) {
        return gameService.createGame(game);
    }


    // TODO Assignment 7d: Create a method and @RequestMpping for deleting a game
    @DeleteMapping(value = "{id}")
    public void deleteGame(@PathVariable("id") long gameUid) {
        gameService.deleteGame(gameUid);
    }

    // Assignment 7e: PATCH endpoint to update a game (used to switch state to ACTIVE)
    @PatchMapping(value = "{id}", consumes = "application/json", produces = "application/json")
    public Game updateGame(@PathVariable("id") long gameUid, @RequestBody Game patch) {
        return gameService.updateGame(gameUid, patch);
    }

    // TODO Assignment 7c-7e: At some point you might want to implement an
    //      endpoint for obtaining open games (open for joining) only or
    //      games that have started.
}
