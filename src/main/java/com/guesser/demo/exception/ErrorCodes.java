package com.guesser.demo.exception;

import lombok.Getter;

@Getter
public enum ErrorCodes {
    // Game related errors
    GAME_NOT_FOUND("GAME_001", "Game with ID '%s' not found. Please start a new game first."),
    GAME_NOT_IN_PROGRESS("GAME_002", "Game with ID '%s' is not in progress. Current status: %s"),
    CANNOT_JOIN_OWN_ROOM("PLAYER_001", "You cannot join your own room %s"),
    GAME_ALREADY_COMPLETED("GAME_003", "Game with ID '%s' is already completed. Status: %s"),
    GAME_NOT_MULTIPLAYER("GAME_004", "This game is not a multiplayer game"),
    NOT_YOUR_TURN("PLAYER_002", "It's not your turn to make a guess"),
    OPPONENT_NOT_FOUND("PLAYER_003", "Opponent not found in the game"),
    
    // Room related errors
    ROOM_NOT_FOUND("ROOM_001", "Room with ID '%s' not found"),
    ROOM_NOT_AVAILABLE("ROOM_002", "Room is not available for joining"),
    ROOM_ID_REQUIRED("ROOM_003", "Room ID is required for multiplayer mode"),
    ROOM_FULL("ROOM_004", "Room is already full with 2 players"),
    PLAYER_ALREADY_IN_ROOM("ROOM_005", "Player is already in room '%s' with game ID '%s'"),
    
    // Input validation errors
    INVALID_GAME_ID("INPUT_001", "Game ID cannot be null or empty"),
    INVALID_GUESS_LENGTH("INPUT_002", "Digit count is not correct"),
    INVALID_GUESS_FORMAT("INPUT_003", "Your guess should contain only numbers (0-9)"),
    INVALID_GUESS_NULL("INPUT_004", "Guess cannot be null or empty"),
    
    // System errors
    INTERNAL_SERVER_ERROR("SYS_001", "An internal server error occurred. Please try again later");

    private final String code;
    private final String message;

    ErrorCodes(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getFormattedMessage(Object... args) {
        return String.format(message, args);
    }
} 