# RoboRally SignUp Backend (`signup4games-backend`)

Runs on **`http://localhost:8070/`** and exposes both the HAL Explorer
(at `/`) and a REST API under `/roborally/…` for the client.

## How to run

1. Open this project in IntelliJ.
2. Right-click `AccessingDataRestApplication` and Run.
3. Start the `roborally-client` project to use the application.

H2 file database persists at `./db/h2-test-7a` (configured in
`application.properties`).

## Endpoint summary

### User (`/roborally/user`)
| Method | Path | Description |
|--------|------|-------------|
| GET    | `/roborally/user/search?name=<name>` | Find users by exact name (7b) |
| POST   | `/roborally/user`                    | Sign up a new user (7c) — checks length and uniqueness, `@Transactional` |

### Game (`/roborally/game`)
| Method | Path | Description |
|--------|------|-------------|
| GET    | `/roborally/game`            | List all games (7b) |
| POST   | `/roborally/game/create`     | Create a new game with owner-as-first-player (7b/7c, `@Transactional`) |
| DELETE | `/roborally/game/{id}`       | Delete a game and its players (7d, `@Transactional`) |
| PATCH  | `/roborally/game/{id}`       | Update game (used to set `state = ACTIVE`, 7e, `@Transactional`) |

### Player (`/roborally/player`)
| Method | Path | Description |
|--------|------|-------------|
| POST   | `/roborally/player`          | Add a player to a game (7c, `@Transactional`); throws `CouldNotCreatePlayerException` on capacity / duplicate / wrong state |
| DELETE | `/roborally/player/{id}`     | Remove a player (7d, `@Transactional`); refuses to remove the game owner |

## Implemented features (Assignments 7a – 7e)

### 7a — Data model
- Bidirectional `Player` ↔ `User` association completed (`@ManyToOne`/`@OneToMany`).

### 7b — REST endpoints
- `UserService.searchUsers` returning users by name.
- `GameService.getGames` returning all games as a list.
- `GameService.createGame` saving a new game (extended in 7c).

### 7c — Owner, join, sign up
- `Game.owner` field with `@ManyToOne`/`@JoinColumn(name = "owner_user_id")`.
- `GameService.createGame` is `@Transactional`: looks up the owner in the
  repository and adds them as the first `Player` for the new game.
- `PlayerController.addPlayer` + `PlayerService.addPlayer` (`@Transactional`):
  validates that game and user exist, that the game still has room, and
  that the user is not already a player. Throws
  `CouldNotCreatePlayerException` otherwise.
- `UserController.signUp` + `UserService.signUp` (`@Transactional`):
  validates name length and uniqueness, then saves a new user.

### 7d — Leave / delete
- `GameController.deleteGame` + `GameService.deleteGame` (`@Transactional`):
  deletes attached players first, then the game.
- `PlayerController.deletePlayer` + `PlayerService.deletePlayer`
  (`@Transactional`): refuses to delete the player that represents the
  game owner; refuses to delete from an `ACTIVE` game.

### 7e — Start game
- `GameState` enum (`SIGNUP`, `ACTIVE`); `Game.state` field stored as a
  string column with default `SIGNUP`.
- `GameController.updateGame` (PATCH) + `GameService.updateGame`
  (`@Transactional`): only `SIGNUP → ACTIVE` is allowed and only when
  `players >= minPlayers`.
- Once `ACTIVE`, both `addPlayer` and `deletePlayer` reject changes for
  that game.

## Project layout

```
src/main/java/com/example/accessing_data_rest/
├── controllers/
│   ├── GameController.java
│   ├── PlayerController.java
│   └── UserController.java
├── services/
│   ├── GameService.java
│   ├── PlayerService.java
│   ├── UserService.java
│   └── CouldNotCreatePlayerException.java
├── model/
│   ├── Game.java
│   ├── GameState.java
│   ├── Player.java
│   └── User.java
└── repositories/
    ├── GameRepository.java
    ├── PlayerRepository.java
    ├── UserRepository.java
    └── RestConfiguration.java
```

