package com.guesser.demo.exception;

import lombok.Getter;

@Getter
public enum ErrorCodes {
    // Game related errors
    GAME_NOT_FOUND("GAME_001", "Game with ID '%s' not found. Please start a new game first."),
    GAME_NOT_IN_PROGRESS("GAME_002", "Game with ID '%s' is not in progress. Current status: %s"),
    GAME_ALREADY_COMPLETED("GAME_003", "Game with ID '%s' is already completed. Status: %s"),
    
    // Input validation errors
    INVALID_GAME_ID("INPUT_001", "Game ID cannot be null or empty"),
    INVALID_GUESS_LENGTH("INPUT_002", "Please enter exactly 4 digits for your guess"),
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