package com.guesser.demo.service.game;

import com.guesser.demo.dto.*;
import com.guesser.demo.model.Guesser;
import com.guesser.demo.model.GameRoom;
import com.guesser.demo.model.GuessHistory;
import com.guesser.demo.repository.GameRepository;
import com.guesser.demo.repository.GameRoomRepository;
import com.guesser.demo.repository.GuessHistoryRepository;
import com.guesser.demo.service.GameLevelService;
import com.guesser.demo.exception.ErrorCodes;
import com.guesser.demo.exception.GuesserException;
import com.guesser.demo.constants.GameConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MultiplayerStrategy implements GameStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(MultiplayerStrategy.class);
    
    @Autowired
    private GameRepository gameRepository;
    
    @Autowired
    private GameRoomRepository gameRoomRepository;
    
    @Autowired
    private GameLevelService gameLevelService;
    
    @Autowired
    private GuessHistoryRepository guessHistoryRepository;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Override
    public StartGuesserResponse startGame(StartGuesserRequest request) {
        logger.info(GameConstants.LOG_PLAYER_STARTING, request.getPlayerId());
        
        // Check if player is already in any active game
        if (gameRepository.existsByPlayerIdAndStatus(request.getPlayerId(), GameConstants.STATUS_IN_PROGRESS)) {
            Guesser existingGame = gameRepository.findByPlayerIdAndStatus(request.getPlayerId(), GameConstants.STATUS_IN_PROGRESS)
                .orElseThrow(() -> new GuesserException(ErrorCodes.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR));
            
            logger.warn(GameConstants.LOG_PLAYER_ALREADY_IN_ROOM, request.getPlayerId(), existingGame.getRoom().getRoomId());
            throw new GuesserException(ErrorCodes.PLAYER_ALREADY_IN_ROOM, HttpStatus.BAD_REQUEST, 
                existingGame.getRoom().getRoomId(), existingGame.getGameId());
        }

        GameRoom room;
        boolean isPlayer2Joining = false;
        String player1Id = null;
        boolean limitAttempts = request.getLimitAttempts() == null || request.getLimitAttempts();
        
        // If roomId is provided, try to join that specific room
        if (request.getRoomId() != null && !request.getRoomId().trim().isEmpty()) {
            room = gameRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new GuesserException(ErrorCodes.ROOM_NOT_FOUND, HttpStatus.NOT_FOUND, request.getRoomId()));
                
            // Check if room is full
            if (room.getPlayer2Id() != null) {
                logger.warn(GameConstants.ERROR_ROOM_FULL, room.getRoomId());
                throw new GuesserException(ErrorCodes.ROOM_FULL, HttpStatus.BAD_REQUEST);
            }
            
            // Check if room is in waiting state
            if (!GameConstants.ROOM_STATUS_WAITING.equals(room.getStatus())) {
                logger.warn(GameConstants.ERROR_ROOM_NOT_AVAILABLE, room.getRoomId(), room.getStatus());
                throw new GuesserException(ErrorCodes.ROOM_NOT_AVAILABLE, HttpStatus.BAD_REQUEST);
            }
            
            // Check if levels match
            if (room.getLevel() != request.getLevel()) {
                logger.warn("Player {} attempted to join room {} with different level. Room level: {}, Player level: {}", 
                    request.getPlayerId(), room.getRoomId(), room.getLevel(), request.getLevel());
                throw new GuesserException(ErrorCodes.ROOM_NOT_AVAILABLE, HttpStatus.BAD_REQUEST);
            }
            
            logger.info(GameConstants.LOG_ROOM_FOUND, room.getRoomId(), request.getPlayerId(), room.getPlayer1Id());
            player1Id = room.getPlayer1Id();
            isPlayer2Joining = true;
            room.setPlayer2Id(request.getPlayerId());
            room.setStatus(GameConstants.ROOM_STATUS_IN_PROGRESS);
            limitAttempts = room.isLimitAttempts();
        } else {
            // Try to find a room with one player waiting AND matching level
            Optional<GameRoom> availableRoom = gameRoomRepository.findByStatusAndPlayer2IdIsNullAndLevel(
                GameConstants.ROOM_STATUS_WAITING, request.getLevel());
            
            if (availableRoom.isPresent()) {
                // Join existing room with matching level
                room = availableRoom.get();
                logger.info(GameConstants.LOG_ROOM_FOUND, room.getRoomId(), request.getPlayerId(), room.getPlayer1Id());
                player1Id = room.getPlayer1Id();
                isPlayer2Joining = true;
                room.setPlayer2Id(request.getPlayerId());
                room.setStatus(GameConstants.ROOM_STATUS_IN_PROGRESS);
                limitAttempts = room.isLimitAttempts();
            } else {
                // Create new room with the requested level
                room = new GameRoom();
                room.setRoomId(java.util.UUID.randomUUID().toString());
                room.setPlayer1Id(request.getPlayerId());
                room.setStatus(GameConstants.ROOM_STATUS_WAITING);
                room.setLevel(request.getLevel());
                room.setLimitAttempts(limitAttempts);
                logger.info(GameConstants.LOG_ROOM_CREATED, room.getRoomId(), request.getPlayerId());
            }
        }
        
        // Create player's game
        // Use room level to ensure consistency (room level is set when room is created or validated when joining)
        int gameLevel = room.getLevel();
        
        // Validate secret number if provided
        String secretNumber;
        if (request.getSecretNumber() != null && !request.getSecretNumber().trim().isEmpty()) {
            gameLevelService.validateSecretNumber(request.getSecretNumber(), gameLevel);
            secretNumber = request.getSecretNumber();
        } else {
            secretNumber = gameLevelService.generateSecretNumber(gameLevel);
        }
        
        Guesser game = new Guesser();
        game.setRoom(room);
        game.setPlayerId(request.getPlayerId());
        game.setSecretNumber(secretNumber);
        game.setLevel(gameLevel);
        game.setGameMode(GameConstants.MULTIPLAYER);
        game.setLimitAttempts(room.isLimitAttempts());
        
        // Set current player
        if (room.getPlayer2Id() == null) {
            // First player - they start
            game.setCurrentPlayerId(request.getPlayerId());
            logger.info(GameConstants.LOG_FIRST_PLAYER, request.getPlayerId(), room.getRoomId());
        } else {
            // Second player - get current player from P1's game
            Guesser player1Game = gameRepository.findByRoomAndPlayerId(room, room.getPlayer1Id())
                .orElseThrow(() -> new GuesserException(ErrorCodes.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR));
            game.setCurrentPlayerId(player1Game.getCurrentPlayerId());
            logger.info(GameConstants.LOG_SECOND_PLAYER, request.getPlayerId(), room.getRoomId());
        }
        
        room = gameRoomRepository.save(room);
        game = gameRepository.save(game);
        
        // Notify P1 if P2 just joined
        if (isPlayer2Joining && player1Id != null) {
            PlayerJoinedNotification notification = new PlayerJoinedNotification(
                room.getRoomId(),
                request.getPlayerId(),
                "Player " + request.getPlayerId() + " has joined the game!",
                room.getStatus()
            );
            messagingTemplate.convertAndSend("/topic/room/" + room.getRoomId(), notification);
            logger.info("Sent player joined notification to room {} for player {}", room.getRoomId(), player1Id);
        }
        
        logger.info(GameConstants.LOG_GAME_STARTED, request.getPlayerId(), room.getRoomId(), game.getStatus());

        Integer remainingAttempts = gameLevelService.getRemainingAttempts(
            game.getLevel(),
            game.getGuessCount(),
            room.isLimitAttempts()
        );
        Integer maxGuesses = room.isLimitAttempts()
            ? gameLevelService.getMaxGuessesForLevel(game.getLevel(), true)
            : null;
        
        return new StartGuesserResponse(
            game.getGameId(),
            game.getStatus(),
            game.getSecretNumber(),
            game.getLevel(),
            game.getGameMode(),
            game.getPlayerId(),
            room.getRoomId(),
            room.getStatus(),
            game.getCurrentPlayerId(),
            room.isLimitAttempts(),
            remainingAttempts,
            maxGuesses
        );
    }
    
    @Override
    public GuessResponse submitGuess(Guesser game, String guess, String playerId) {
        logger.info(GameConstants.LOG_GUESS_SUBMITTED, playerId, game.getGameId(), game.getRoom().getRoomId());
            
        // Check if it's the player's turn
        if (!playerId.equals(game.getCurrentPlayerId())) {
            logger.warn(GameConstants.ERROR_NOT_YOUR_TURN, playerId, game.getGameId());
            throw new GuesserException(ErrorCodes.NOT_YOUR_TURN, HttpStatus.BAD_REQUEST);
        }
        
        gameLevelService.validateGuess(guess, game.getLevel());
        
        // Get opponent's game to check their secret number
        Guesser opponentGame = gameRepository.findByRoomAndPlayerIdNot(game.getRoom(), playerId)
            .orElseThrow(() -> new GuesserException(ErrorCodes.OPPONENT_NOT_FOUND, HttpStatus.NOT_FOUND));
        
        int correctDigits = countCorrectDigits(opponentGame.getSecretNumber(), guess);
        game.setGuessCount(game.getGuessCount() + 1);
        
        logger.info(GameConstants.LOG_GUESS_MADE, playerId, guess, game.getGameId(), correctDigits);


        // TODO : It is not needed now, for future we can use it
//        // Store guess history
//        GuessHistory guessHistory = new GuessHistory(game, guess, correctDigits);
//        guessHistory.setPlayerId(playerId);
//        guessHistoryRepository.save(guessHistory);
        
        int numberLength = gameLevelService.getNumberLengthForLevel(game.getLevel());
        boolean limitAttempts = game.isLimitAttempts();
        int maxGuesses = gameLevelService.getMaxGuessesForLevel(game.getLevel(), limitAttempts);
        
        // Check if the guess is correct
        if (correctDigits == numberLength) {
            logger.info(GameConstants.LOG_PLAYER_WON, playerId, game.getGameId(), game.getRoom().getRoomId());
            game.setHasWon(true);
            game.setStatus(GameConstants.STATUS_COMPLETED);
            game.setEndTime(java.time.LocalDateTime.now());
            
            // Update opponent's game
            opponentGame.setStatus(GameConstants.STATUS_COMPLETED);
            opponentGame.setEndTime(java.time.LocalDateTime.now());
            gameRepository.save(opponentGame);
            
            // Update room status
            GameRoom room = game.getRoom();
            room.setStatus(GameConstants.ROOM_STATUS_COMPLETED);
            room.setEndTime(java.time.LocalDateTime.now());
            gameRoomRepository.save(room);
        } else if (limitAttempts && game.getGuessCount() >= maxGuesses) {
            logger.info(GameConstants.LOG_PLAYER_USED_ALL_GUESSES, playerId, game.getGameId(), game.getRoom().getRoomId());
            game.setStatus(GameConstants.STATUS_COMPLETED);
            game.setEndTime(java.time.LocalDateTime.now());
            
            // Check if opponent has won or used all guesses
            if (opponentGame.isHasWon() || opponentGame.getGuessCount() >= maxGuesses) {
                opponentGame.setStatus(GameConstants.STATUS_COMPLETED);
                opponentGame.setEndTime(java.time.LocalDateTime.now());
                gameRepository.save(opponentGame);
                
                // Update room status
                GameRoom room = game.getRoom();
                room.setStatus(GameConstants.ROOM_STATUS_COMPLETED);
                room.setEndTime(java.time.LocalDateTime.now());
                gameRoomRepository.save(room);
            }
        }
        
        String nextPlayerId = null;
        GameRoom room = game.getRoom();
        
        // Switch turns if the game is still in progress
        if (GameConstants.STATUS_IN_PROGRESS.equals(game.getStatus())) {
            nextPlayerId = opponentGame.getPlayerId();
            game.setCurrentPlayerId(nextPlayerId);
            game = gameRepository.save(game);
            
            opponentGame.setCurrentPlayerId(nextPlayerId);
            gameRepository.save(opponentGame);
            
            logger.info(GameConstants.LOG_TURN_SWITCHED, nextPlayerId, game.getGameId(), room.getRoomId());
        } else {
            // Game ended, no next player
            nextPlayerId = null;
        }
        
        game = gameRepository.save(game);
        
        Integer remainingAttempts = gameLevelService.getRemainingAttempts(
            game.getLevel(),
            game.getGuessCount(),
            limitAttempts
        );
        
        // Send WebSocket notification to both players in the room
        String message;
        if (GameConstants.STATUS_COMPLETED.equals(game.getStatus())) {
            if (game.isHasWon()) {
                message = "Player " + playerId + " has won the game!";
            } else {
                message = limitAttempts
                    ? "Game ended. Player " + playerId + " has used all attempts."
                    : "Game ended.";
            }
        } else {
            message = "Player " + playerId + " made a guess. It's now " + nextPlayerId + "'s turn!";
        }
        
        TurnNotification turnNotification = new TurnNotification(
            room.getRoomId(),
            playerId,
            guess,
            correctDigits,
            game.getGuessCount(),
            remainingAttempts,
            nextPlayerId,
            game.getStatus(),
            message
        );
        
        // Send notification to room topic so both players receive it
        messagingTemplate.convertAndSend("/topic/room/" + room.getRoomId(), turnNotification);
        logger.info("Sent turn notification to room {} - Player {} guessed, next player: {}", 
            room.getRoomId(), playerId, nextPlayerId);
        
        return new GuessResponse(
            correctDigits,
            game.getGuessCount(),
            game.getStatus(),
            guess,
            remainingAttempts
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