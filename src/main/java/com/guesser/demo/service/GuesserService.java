package com.guesser.demo.service;

import com.guesser.demo.constants.GameConstants;
import com.guesser.demo.model.Guesser;
import com.guesser.demo.model.GameRoom;
import com.guesser.demo.model.GuessHistory;
import com.guesser.demo.repository.GameRepository;
import com.guesser.demo.repository.GameRoomRepository;
import com.guesser.demo.repository.GuessHistoryRepository;
import com.guesser.demo.dto.GuessResponse;
import com.guesser.demo.dto.StartGuesserResponse;
import com.guesser.demo.dto.StartGuesserRequest;
import com.guesser.demo.dto.GuessHistoryResponse;
import com.guesser.demo.dto.JoinGameRequest;
import com.guesser.demo.exception.ErrorCodes;
import com.guesser.demo.exception.GuesserException;
import com.guesser.demo.service.game.GameStrategy;
import com.guesser.demo.service.game.SinglePlayerStrategy;
import com.guesser.demo.service.game.MultiplayerStrategy;
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
    private GameRoomRepository gameRoomRepository;
    
    @Autowired
    private GameLevelService gameLevelService;
    
    @Autowired
    private GuessHistoryRepository guessHistoryRepository;
    
    @Autowired
    private SinglePlayerStrategy singlePlayerStrategy;
    
    @Autowired
    private MultiplayerStrategy multiplayerStrategy;
    
    public StartGuesserResponse startNewGame(StartGuesserRequest request) {
        if (request == null) {
            request = new StartGuesserRequest();
        }
        
        GameStrategy strategy = GameConstants.MULTIPLAYER.equals(request.getGameMode())
            ? multiplayerStrategy 
            : singlePlayerStrategy;
            
        return strategy.startGame(request);
    }
    
    public GuessResponse submitGuess(String gameId, String guess, String playerId) {
        if (gameId == null || gameId.trim().isEmpty()) {
            throw new GuesserException(ErrorCodes.INVALID_GAME_ID, HttpStatus.BAD_REQUEST);
        }
        
        Guesser game = gameRepository.findById(gameId)
            .orElseThrow(() -> new GuesserException(ErrorCodes.GAME_NOT_FOUND, HttpStatus.NOT_FOUND, gameId));
            
        if (!"IN_PROGRESS".equals(game.getStatus())) {
            throw new GuesserException(ErrorCodes.GAME_NOT_IN_PROGRESS, HttpStatus.BAD_REQUEST, gameId, game.getStatus());
        }
        
        GameStrategy strategy = GameConstants.MULTIPLAYER.equals(game.getGameMode())
            ? multiplayerStrategy 
            : singlePlayerStrategy;
            
        return strategy.submitGuess(game, guess, playerId);
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
                history.getGuessTime(),
                history.getPlayerId()
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