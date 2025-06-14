package com.guesser.demo.service;

import com.guesser.demo.model.Game;
import com.guesser.demo.repository.GameRepository;
import com.guesser.demo.dto.GameResponse;
import com.guesser.demo.dto.StartGameResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.Random;

@Service
public class GameService {
    
    @Autowired
    private GameRepository gameRepository;
    
    private static final int NUMBER_LENGTH = 4;
    private static final int MAX_GUESSES = 1;
    
    public StartGameResponse startNewGame() {
        Game game = new Game();
        game.setSecretNumber(generateSecretNumber());
        game = gameRepository.save(game);
        return new StartGameResponse(game.getGameId(), game.getStatus(), game.getSecretNumber());
    }
    
    public GameResponse submitGuess(String gameId, String guess) {
        if (gameId == null || gameId.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game ID cannot be null or empty");
        }
        
        Game game = gameRepository.findById(gameId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                String.format("Game with ID '%s' not found. Please start a new game first.", gameId)));
            
        if (!"IN_PROGRESS".equals(game.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                String.format("Game with ID '%s' is not in progress. Current status: %s", gameId, game.getStatus()));
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
        
        return new GameResponse(
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
        if (guess == null || guess.length() != NUMBER_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Please enter exactly 4 digits for your guess");
        }
        if (!guess.matches("\\d+")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Your guess should contain only numbers (0-9)");
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