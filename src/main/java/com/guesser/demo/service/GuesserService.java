package com.guesser.demo.service;

import com.guesser.demo.model.Guesser;
import com.guesser.demo.model.GuessHistory;
import com.guesser.demo.repository.GameRepository;
import com.guesser.demo.repository.GuessHistoryRepository;
import com.guesser.demo.dto.GuessResponse;
import com.guesser.demo.dto.StartGuesserResponse;
import com.guesser.demo.dto.StartGuesserRequest;
import com.guesser.demo.dto.GuessHistoryResponse;
import com.guesser.demo.exception.ErrorCodes;
import com.guesser.demo.exception.GuesserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GuesserService {
    
    @Autowired
    private GameRepository gameRepository;
    
    @Autowired
    private GameLevelService gameLevelService;
    
    @Autowired
    private GuessHistoryRepository guessHistoryRepository;
    
    public StartGuesserResponse startNewGame(StartGuesserRequest request) {
        if (request == null) {
            request = new StartGuesserRequest();
        }
        
        Guesser game = new Guesser();
        game.setSecretNumber(gameLevelService.generateSecretNumber(request.getLevel()));
        game.setLevel(request.getLevel());
        game = gameRepository.save(game);
        return new StartGuesserResponse(game.getGameId(), game.getStatus(), game.getSecretNumber(), game.getLevel());
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
        
        gameLevelService.validateGuess(guess, game.getLevel());
        
        int correctDigits = countCorrectDigits(game.getSecretNumber(), guess);
        game.setGuessCount(game.getGuessCount() + 1);
        
        // Store guess history
        GuessHistory guessHistory = new GuessHistory(game, guess, correctDigits);
        guessHistoryRepository.save(guessHistory);
        
        int numberLength = gameLevelService.getNumberLengthForLevel(game.getLevel());
        int maxGuesses = gameLevelService.getMaxGuessesForLevel(game.getLevel());
        
        if (correctDigits == numberLength) {
            game.setStatus("SUCCESS");
            game.setEndTime(java.time.LocalDateTime.now());
        } else if (game.getGuessCount() >= maxGuesses) {
            game.setStatus("FAILED");
            game.setEndTime(java.time.LocalDateTime.now());
        }
        
        game = gameRepository.save(game);
        
        int remainingAttempts = maxGuesses - game.getGuessCount();
        
        return new GuessResponse(
            correctDigits,
            game.getGuessCount(),
            game.getStatus(),
            guess,
            remainingAttempts
        );
    }
    
    public List<GuessHistoryResponse> getGuessHistory(String gameId) {
        if (gameId == null || gameId.trim().isEmpty()) {
            throw new GuesserException(ErrorCodes.INVALID_GAME_ID, HttpStatus.BAD_REQUEST);
        }
        
        Guesser game = gameRepository.findById(gameId)
            .orElseThrow(() -> new GuesserException(ErrorCodes.GAME_NOT_FOUND, HttpStatus.NOT_FOUND, gameId));
            
        return guessHistoryRepository.findByGameGameIdOrderByGuessTimeDesc(gameId)
            .stream()
            .map(history -> new GuessHistoryResponse(
                history.getGuessedNumber(),
                history.getCorrectDigits(),
                history.getGuessTime()
            ))
            .collect(Collectors.toList());
    }
    
    private int countCorrectDigits(String secret, String guess) {
        int count = 0;
        for (int i = 0; i < secret.length(); i++) {
            if (secret.charAt(i) == guess.charAt(i)) {
                count++;
            }
        }
        return count;
    }
} 