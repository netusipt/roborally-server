package com.example.accessing_data_rest.services;

import com.example.accessing_data_rest.model.Game;
import com.example.accessing_data_rest.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;


@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    public List<Game> getGames() {
        // TODO Assignment 7b: Implement the method for obtaining all games from the
        //      GameRepository (using finaAll) and returning it as a list
        return StreamSupport.stream(gameRepository.findAll().spliterator(), false).toList();
    }

    // DONE Assignment 7b: create a game in the repository and return the result
    // TODO Assignment 7c: make sure that the game is created with the owner
    //      who must be in the repository already, and also with the owner as first player
    public Game createGame(Game game) {
        return gameRepository.save(game);
    }

}
