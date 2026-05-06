package com.example.accessing_data_rest.services;

import com.example.accessing_data_rest.model.Game;
import com.example.accessing_data_rest.model.GameState;
import com.example.accessing_data_rest.model.Player;
import com.example.accessing_data_rest.model.User;
import com.example.accessing_data_rest.repositories.GameRepository;
import com.example.accessing_data_rest.repositories.PlayerRepository;
import com.example.accessing_data_rest.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;


@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlayerRepository playerRepository;

    public List<Game> getGames() {
        // TODO Assignment 7b: Implement the method for obtaining all games from the
        //      GameRepository (using finaAll) and returning it as a list
        return StreamSupport.stream(gameRepository.findAll().spliterator(), false).toList();
    }

    // DONE Assignment 7b: create a game in the repository and return the result
    // TODO Assignment 7c: make sure that the game is created with the owner
    //      who must be in the repository already, and also with the owner as first player
    @Transactional
    public Game createGame(Game game) {
        User owner = game.getOwner();
        if (owner == null) {
            throw new RuntimeException("Game must have an owner");
        }
        Optional<User> ownerInRepo = userRepository.findById(owner.getUid());
        if (ownerInRepo.isEmpty()) {
            throw new RuntimeException("Owner does not exist");
        }
        User persistedOwner = ownerInRepo.get();
        game.setOwner(persistedOwner);

        Game savedGame = gameRepository.save(game);

        Player player = new Player();
        player.setGame(savedGame);
        player.setUser(persistedOwner);
        player.setName(persistedOwner.getName());
        playerRepository.save(player);

        return savedGame;
    }

    // Assignment 7e: update mutable fields of a game (currently only the state).
    @Transactional
    public Game updateGame(long gameUid, Game patch) {
        Game game = gameRepository.findById(gameUid)
                .orElseThrow(() -> new RuntimeException("Game not found"));
        if (patch.getState() != null) {
            // Sanity-check the transition: only SIGNUP -> ACTIVE is allowed
            if (game.getState() == GameState.ACTIVE && patch.getState() == GameState.SIGNUP) {
                throw new RuntimeException("Cannot move an active game back to SIGNUP");
            }
            if (patch.getState() == GameState.ACTIVE) {
                int playerCount = game.getPlayers() == null ? 0 : game.getPlayers().size();
                if (playerCount < game.getMinPlayers()) {
                    throw new RuntimeException("Not enough players to start the game");
                }
            }
            game.setState(patch.getState());
        }
        return gameRepository.save(game);
    }

    // Assignment 7d: delete a game (and its associated players to avoid dangling rows).
    @Transactional
    public void deleteGame(long gameUid) {
        Optional<Game> gameOpt = gameRepository.findById(gameUid);
        if (gameOpt.isEmpty()) {
            throw new RuntimeException("Game not found");
        }
        Game game = gameOpt.get();
        if (game.getPlayers() != null) {
            for (Player p : game.getPlayers()) {
                playerRepository.deleteById(p.getUid());
            }
        }
        gameRepository.deleteById(gameUid);
    }

}
