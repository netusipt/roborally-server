package com.example.accessing_data_rest.controllers;

import com.example.accessing_data_rest.model.Player;
import com.example.accessing_data_rest.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("roborally/player")
public class PlayerController {

    @Autowired
    PlayerService playerService;

    // TODO Assignment 7c: For adding players to a game, you will need to add a @PostMapping here
    //      and you will need to add corresponding service class PlayerService in package services,
    //      and implement the respective logic there.
    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    public Player addPlayer(@RequestBody Player player) {
        return playerService.addPlayer(player);
    }

    // TODO Assignment 7d for a player (user) leaving the game, you need to have a delete method for
    //      players here.
    @DeleteMapping(value = "{id}")
    public void deletePlayer(@PathVariable("id") long playerUid) {
        playerService.deletePlayer(playerUid);
    }

}
