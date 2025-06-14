package com.guesser.demo.service;

import com.guesser.demo.model.Guesser;
import com.guesser.demo.repository.GameRepository;
import com.guesser.demo.dto.GuessResponse;
import com.guesser.demo.dto.StartGuesserResponse;
import com.guesser.demo.exception.ErrorCodes;
import com.guesser.demo.exception.GuesserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import java.util.Random;

@Service
public class GuesserService {
    
    @Autowired
    private GameRepository gameRepository;
    
    private static final int NUMBER_LENGTH = 4;
    private static final int MAX_GUESSES = 100;
    
    public StartGuesserResponse startNewGame() {
        Guesser game = new Guesser();
        game.setSecretNumber(generateSecretNumber());
        game = gameRepository.save(game);
        return new StartGuesserResponse(game.getGameId(), game.getStatus(), game.getSecretNumber());
    }
    
    public GuessResponse submitGuess(String gameId, String guess) {
        if (gameId == null || gameId.trim().isEmpty()) {
            throw new GuesserException(ErrorCodes.INVALID_GAME_ID, HttpStatus.BAD_REQUEST);
        }
        
        Guesser game = gameRepository.findById(gameId)
            .orElseThrow(() -> new GuesserException(ErrorCodes.GAME_NOT_FOUND, HttpStatus.NOT_FOUND, gameId));
            
        if (!"IN_PROGRESS".equals(game.getStatus())) {
            throw new GuesserException(ErrorCodes.GAME_NOT_IN_PROGRESS, HttpStatus.BAD_REQUEST, gameId, game.getStatus());
        }
        
        validateGuess(guess);
        
        int correctDigits = countCorrectDigits(game.getSecretNumber(), guess);
        game.setGuessCount(game.getGuessCount() + 1);
        
        if (correctDigits == NUMBER_LENGTH) {
            game.setStatus("SUCCESS");
            game.setEndTime(java.time.LocalDateTime.now());
        } else if (game.getGuessCount() >= MAX_GUESSES) {
            game.setStatus("FAILED");
            game.setEndTime(java.time.LocalDateTime.now());
        }
        
        game = gameRepository.save(game);
        
        int remainingAttempts = MAX_GUESSES - game.getGuessCount();
        
        return new GuessResponse(
            game.getGameId(),
            correctDigits,
            game.getGuessCount(),
            game.getStatus(),
            guess,
            remainingAttempts
        );
    }
    
    private String generateSecretNumber() {
        Random random = new Random();
        StringBuilder number = new StringBuilder();
        for (int i = 0; i < NUMBER_LENGTH; i++) {
            number.append(random.nextInt(10));
        }
        return number.toString();
    }
    
    private void validateGuess(String guess) {
        if (guess == null || guess.trim().isEmpty()) {
            throw new GuesserException(ErrorCodes.INVALID_GUESS_NULL, HttpStatus.BAD_REQUEST);
        }
        if (guess.length() != NUMBER_LENGTH) {
            throw new GuesserException(ErrorCodes.INVALID_GUESS_LENGTH, HttpStatus.BAD_REQUEST);
        }
        if (!guess.matches("\\d+")) {
            throw new GuesserException(ErrorCodes.INVALID_GUESS_FORMAT, HttpStatus.BAD_REQUEST);
        }
    }
    
    private int countCorrectDigits(String secret, String guess) {
        int count = 0;
        for (int i = 0; i < NUMBER_LENGTH; i++) {
            if (secret.charAt(i) == guess.charAt(i)) {
                count++;
            }
        }
        return count;
    }
} 