package com.guesser.demo.constants;

public final class GameConstants {
    // Game Modes
    public static final String SINGLE_PLAYER = "00ff00";
    public static final String MULTIPLAYER = "0000ff";

    // Game Status
    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_WAITING_FOR_PLAYER = "WAITING_FOR_PLAYER";

    // Room Status
    public static final String ROOM_STATUS_WAITING = "WAITING_FOR_PLAYER";
    public static final String ROOM_STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String ROOM_STATUS_COMPLETED = "COMPLETED";

    // Game Levels
    public static final int LEVEL_1 = 1;
    public static final int LEVEL_2 = 2;
    public static final int LEVEL_3 = 3;

    // Level 1 Configuration
    public static final int LEVEL_1_NUMBER_LENGTH = 2;
    public static final int LEVEL_1_MAX_GUESSES = 20;

    // Level 2 Configuration
    public static final int LEVEL_2_NUMBER_LENGTH = 3;
    public static final int LEVEL_2_MAX_GUESSES = 30;

    // Level 3 Configuration
    public static final int LEVEL_3_NUMBER_LENGTH = 4;
    public static final int LEVEL_3_MAX_GUESSES = 50;

    // Default Values
    public static final int DEFAULT_LEVEL = LEVEL_1;
    public static final String DEFAULT_GAME_MODE = SINGLE_PLAYER;

    // HTTP Headers
    public static final String HEADER_GAME_ID = "X-Game-ID";
    public static final String HEADER_PLAYER_ID = "X-Player-ID";

    // API Endpoints
    public static final String API_START_GAME = "/api/guess/start";
    public static final String API_SUBMIT_GUESS = "/api/guess/guess";
    public static final String API_GET_HISTORY = "/api/guess/history";

    // Logging Messages - Multiplayer
    public static final String LOG_PLAYER_STARTING = "Player {} attempting to start a multiplayer game";
    public static final String LOG_PLAYER_ALREADY_IN_ROOM = "Player {} is already in room {}";
    public static final String LOG_ROOM_FOUND = "Found available room {} for player {}. Room currently has player {}";
    public static final String LOG_ROOM_CREATED = "Created new room {} for player {}";
    public static final String LOG_FIRST_PLAYER = "Player {} is the first player in room {}";
    public static final String LOG_SECOND_PLAYER = "Player {} joined room {} as second player";
    public static final String LOG_GAME_STARTED = "Game started for player {} in room {} with status {}";
    public static final String LOG_GUESS_SUBMITTED = "Player {} submitting guess in game {} for room {}";
    public static final String LOG_GUESS_MADE = "Player {} made guess {} in game {}. Correct digits: {}";
    public static final String LOG_PLAYER_WON = "Player {} won the game {} in room {}";
    public static final String LOG_PLAYER_USED_ALL_GUESSES = "Player {} used all guesses in game {} in room {}";
    public static final String LOG_TURN_SWITCHED = "Turn switched to player {} in game {} in room {}";

    // Logging Messages - Single Player
    public static final String LOG_SINGLE_PLAYER_STARTING = "Player {} starting a single player game";
    public static final String LOG_SINGLE_PLAYER_GAME_CREATED = "Single player game created for player {} with game ID {}";
    public static final String LOG_SINGLE_PLAYER_GUESS = "Player {} made guess {} in game {}. Correct digits: {}";
    public static final String LOG_SINGLE_PLAYER_WON = "Player {} won the single player game {}";
    public static final String LOG_SINGLE_PLAYER_GAME_OVER = "Player {} completed single player game {} with {} attempts";

    // Error Messages - Multiplayer
    public static final String ERROR_ROOM_FULL = "Room {} is already full";
    public static final String ERROR_ROOM_NOT_AVAILABLE = "Room {} is not available for joining. Current status: {}";
    public static final String ERROR_NOT_YOUR_TURN = "Player {} attempted to guess out of turn in game {}";

    // Error Messages - Single Player
    public static final String ERROR_GAME_NOT_FOUND = "Game {} not found for player {}";
    public static final String ERROR_GAME_COMPLETED = "Game {} is already completed";
    public static final String ERROR_INVALID_GUESS = "Invalid guess format or length for level {}";

    // Game Result Messages
    public static final String MESSAGE_GAME_WON = "Congratulations! You've won the game!";
    public static final String MESSAGE_GAME_LOST = "Game Over! You've used all your attempts.";
    public static final String MESSAGE_GAME_IN_PROGRESS = "Game is in progress. Keep guessing!";
    public static final String MESSAGE_INVALID_GUESS = "Invalid guess. Please enter a {}-digit number.";

    private GameConstants() {
        // Private constructor to prevent instantiation
        throw new AssertionError("Constants class should not be instantiated");
    }
} 