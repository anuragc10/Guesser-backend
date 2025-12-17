package com.guesser.demo.service;

import com.guesser.demo.constants.GameConstants;
import com.guesser.demo.dto.*;
import com.guesser.demo.model.Guesser;
import com.guesser.demo.model.GameRoom;
import com.guesser.demo.model.GuessHistory;
import com.guesser.demo.repository.GameRepository;
import com.guesser.demo.repository.GameRoomRepository;
import com.guesser.demo.repository.GuessHistoryRepository;
import com.guesser.demo.exception.ErrorCodes;
import com.guesser.demo.exception.GuesserException;
import com.guesser.demo.service.game.GameStrategy;
import com.guesser.demo.service.game.SinglePlayerStrategy;
import com.guesser.demo.service.game.MultiplayerStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GuesserService {
    
    private static final Logger logger = LoggerFactory.getLogger(GuesserService.class);
    
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
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

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
            
        if (!GameConstants.STATUS_IN_PROGRESS.equals(game.getStatus())) {
            throw new GuesserException(ErrorCodes.GAME_NOT_IN_PROGRESS, HttpStatus.BAD_REQUEST, gameId, game.getStatus());
        }
        
        GameStrategy strategy = GameConstants.MULTIPLAYER.equals(game.getGameMode())
            ? multiplayerStrategy 
            : singlePlayerStrategy;
            
        return strategy.submitGuess(game, guess, playerId);
    }
    
    public List<GuessHistoryResponse> getGuessHistory(String roomId) {
        if (roomId == null || roomId.trim().isEmpty()) {
            throw new GuesserException(ErrorCodes.INVALID_GAME_ID, HttpStatus.BAD_REQUEST);
        }
        
        // Validate room exists
        GameRoom room = gameRoomRepository.findById(roomId)
            .orElseThrow(() -> new GuesserException(ErrorCodes.ROOM_NOT_FOUND, HttpStatus.NOT_FOUND, roomId));
            
        return guessHistoryRepository.findByRoomIdOrderByGuessTimeDesc(roomId)
            .stream()
            .map(history -> new GuessHistoryResponse(
                history.getGuessedNumber(),
                history.getCorrectDigits(),
                history.getGuessTime(),
                history.getPlayerId()
            ))
            .collect(Collectors.toList());
    }
    
    public EndGameResponse endGame(EndGameRequest request) {
        if (request == null || request.getGameId() == null || request.getGameId().trim().isEmpty()) {
            throw new GuesserException(ErrorCodes.INVALID_GAME_ID, HttpStatus.BAD_REQUEST);
        }
        
        if (request.getPlayerId() == null || request.getPlayerId().trim().isEmpty()) {
            throw new GuesserException(ErrorCodes.INVALID_GAME_ID, HttpStatus.BAD_REQUEST);
        }
        
        Guesser game = gameRepository.findById(request.getGameId())
            .orElseThrow(() -> new GuesserException(ErrorCodes.GAME_NOT_FOUND, HttpStatus.NOT_FOUND, request.getGameId()));
        
        // Verify the player owns this game
        if (!request.getPlayerId().equals(game.getPlayerId())) {
            throw new GuesserException(ErrorCodes.GAME_NOT_FOUND, HttpStatus.FORBIDDEN, request.getGameId());
        }
        
        // Check if game is already completed
        if (GameConstants.STATUS_COMPLETED.equals(game.getStatus())) {
            throw new GuesserException(ErrorCodes.GAME_ALREADY_COMPLETED, HttpStatus.BAD_REQUEST, 
                request.getGameId(), game.getStatus());
        }
        
        LocalDateTime endTime = LocalDateTime.now();
        
        // Mark current player's game as completed (they lose)
        game.setStatus(GameConstants.STATUS_COMPLETED);
        game.setHasWon(false);
        game.setEndTime(endTime);
        gameRepository.save(game);
        
        String winnerPlayerId = null;
        String message;
        
        // Handle multiplayer: make the other player win
        if (GameConstants.MULTIPLAYER.equals(game.getGameMode())) {
            if (game.getRoom() != null) {
                // Find the opponent's game
                Guesser opponentGame = gameRepository.findByRoomAndPlayerIdNot(game.getRoom(), request.getPlayerId())
                    .orElse(null);
                
                if (opponentGame != null && GameConstants.STATUS_IN_PROGRESS.equals(opponentGame.getStatus())) {
                    // Mark opponent as winner
                    opponentGame.setStatus(GameConstants.STATUS_COMPLETED);
                    opponentGame.setHasWon(true);
                    opponentGame.setEndTime(endTime);
                    gameRepository.save(opponentGame);
                    winnerPlayerId = opponentGame.getPlayerId();
                    
                    logger.info("Player {} ended the game. Player {} wins. Game: {}, Room: {}", 
                        request.getPlayerId(), winnerPlayerId, request.getGameId(), game.getRoom().getRoomId());
                }
                
                // Update room status
                GameRoom room = game.getRoom();
                room.setStatus(GameConstants.ROOM_STATUS_COMPLETED);
                room.setEndTime(endTime);
                gameRoomRepository.save(room);
                message = winnerPlayerId != null
                        ? String.format("Player left the game,  %s wins!", winnerPlayerId)
                        : "Game ended.";

                // --- WebSocket Broadcast ---
                TurnNotification notification = new TurnNotification(
                        room.getRoomId(),
                        request.getPlayerId(),  // player who left
                        null,                   // guessedNumber
                        0,                      // correctDigits
                        game.getGuessCount(),
                        null,                   // remainingAttempts
                        null,                   // nextPlayerId
                        GameConstants.STATUS_COMPLETED,
                        message
                );

                messagingTemplate.convertAndSend("/topic/room/" + room.getRoomId(), notification);

            }
            
            if (winnerPlayerId != null) {
                message = String.format("Game ended. Player %s wins!", winnerPlayerId);
            } else {
                message = "Game ended.";
            }
        } else {
            // Single player: player loses
            message = "Game ended. You did not complete the game.";
            logger.info("Player {} ended single player game {}", request.getPlayerId(), request.getGameId());
        }
        
        return new EndGameResponse(
            game.getGameId(),
            GameConstants.STATUS_COMPLETED,
            message,
            false, // Current player loses
            winnerPlayerId
        );
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