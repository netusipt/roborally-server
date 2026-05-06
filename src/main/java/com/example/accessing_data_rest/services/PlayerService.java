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

import java.util.Optional;

// Assignment 7c: service handling logic for adding players to games
@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    // Assignment 7d: delete a player (used when a user leaves a game).
    //                Refuses to delete the player representing the game owner.
    @Transactional
    public void deletePlayer(long playerUid) {
        Optional<Player> playerOpt = playerRepository.findById(playerUid);
        if (playerOpt.isEmpty()) {
            throw new RuntimeException("Player not found");
        }
        Player player = playerOpt.get();
        Game game = player.getGame();
        User user = player.getUser();
        // Assignment 7e: cannot leave after the game has started
        if (game != null && game.getState() == GameState.ACTIVE) {
            throw new RuntimeException("Cannot leave a game that has already started");
        }
        if (game != null && user != null && game.getOwner() != null
                && game.getOwner().getUid() == user.getUid()) {
            throw new RuntimeException("Cannot remove the game owner from the game");
        }
        playerRepository.deleteById(playerUid);
    }

    @Transactional
    public Player addPlayer(Player player) {
        if (player.getGame() == null || player.getUser() == null) {
            throw new CouldNotCreatePlayerException("Player must reference both a game and a user");
        }

        Optional<Game> gameOpt = gameRepository.findById(player.getGame().getUid());
        Optional<User> userOpt = userRepository.findById(player.getUser().getUid());
        if (gameOpt.isEmpty()) {
            throw new CouldNotCreatePlayerException("Game does not exist");
        }
        if (userOpt.isEmpty()) {
            throw new CouldNotCreatePlayerException("User does not exist");
        }

        Game game = gameOpt.get();
        User user = userOpt.get();

        // Assignment 7e: cannot join after the game has started
        if (game.getState() == GameState.ACTIVE) {
            throw new CouldNotCreatePlayerException("Game has already started");
        }

        // Check max players capacity
        if (game.getPlayers() != null && game.getPlayers().size() >= game.getMaxPlayers()) {
            throw new CouldNotCreatePlayerException("Game is full");
        }

        // Check user is not already a player of this game
        if (game.getPlayers() != null) {
            for (Player p : game.getPlayers()) {
                if (p.getUser() != null && p.getUser().getUid() == user.getUid()) {
                    throw new CouldNotCreatePlayerException("User is already a player of this game");
                }
            }
        }

        Player newPlayer = new Player();
        newPlayer.setGame(game);
        newPlayer.setUser(user);
        newPlayer.setName(player.getName() != null ? player.getName() : user.getName());
        return playerRepository.save(newPlayer);
    }

}
