package com.guesser.demo.service;

import org.springframework.stereotype.Service;

@Service
public class GameLevelService {
    
    private static final int LEVEL_1_NUMBER_LENGTH = 2;
    private static final int LEVEL_1_MAX_GUESSES = 20;
    private static final int LEVEL_2_NUMBER_LENGTH = 3;
    private static final int LEVEL_2_MAX_GUESSES = 30;
    private static final int LEVEL_3_NUMBER_LENGTH = 4;
    private static final int LEVEL_3_MAX_GUESSES = 50;
    
    public int getNumberLengthForLevel(int level) {
        switch (level) {
            case 1:
                return LEVEL_1_NUMBER_LENGTH;
            case 2:
                return LEVEL_2_NUMBER_LENGTH;
            case 3:
                return LEVEL_3_NUMBER_LENGTH;
            default:
                return LEVEL_1_NUMBER_LENGTH;
        }
    }
    
    public int getMaxGuessesForLevel(int level) {
        switch (level) {
            case 1:
                return LEVEL_1_MAX_GUESSES;
            case 2:
                return LEVEL_2_MAX_GUESSES;
            case 3:
                return LEVEL_3_MAX_GUESSES;
            default:
                return LEVEL_1_MAX_GUESSES;
        }
    }
    
    public String generateSecretNumber(int level) {
        java.util.Random random = new java.util.Random();
        StringBuilder number = new StringBuilder();
        int numberLength = getNumberLengthForLevel(level);
        for (int i = 0; i < numberLength; i++) {
            number.append(random.nextInt(10));
        }
        return number.toString();
    }
    
    public void validateGuess(String guess, int level) {
        if (guess == null || guess.trim().isEmpty()) {
            throw new com.guesser.demo.exception.GuesserException(
                com.guesser.demo.exception.ErrorCodes.INVALID_GUESS_NULL, 
                org.springframework.http.HttpStatus.BAD_REQUEST
            );
        }
        int numberLength = getNumberLengthForLevel(level);
        if (guess.length() != numberLength) {
            throw new com.guesser.demo.exception.GuesserException(
                com.guesser.demo.exception.ErrorCodes.INVALID_GUESS_LENGTH, 
                org.springframework.http.HttpStatus.BAD_REQUEST
            );
        }
        if (!guess.matches("\\d+")) {
            throw new com.guesser.demo.exception.GuesserException(
                com.guesser.demo.exception.ErrorCodes.INVALID_GUESS_FORMAT, 
                org.springframework.http.HttpStatus.BAD_REQUEST
            );
        }
    }
} 