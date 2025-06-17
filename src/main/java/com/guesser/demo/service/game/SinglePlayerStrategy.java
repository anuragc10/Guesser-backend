package com.guesser.demo.service.game;

import com.guesser.demo.constants.GameConstants;
import com.guesser.demo.dto.GuessResponse;
import com.guesser.demo.dto.StartGuesserResponse;
import com.guesser.demo.dto.StartGuesserRequest;
import com.guesser.demo.model.Guesser;
import com.guesser.demo.repository.GameRepository;
import com.guesser.demo.repository.GuessHistoryRepository;
import com.guesser.demo.service.GameLevelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SinglePlayerStrategy implements GameStrategy {
    private static final Logger logger = LoggerFactory.getLogger(SinglePlayerStrategy.class);
    private final GameRepository gameRepository;
    private final GuessHistoryRepository guessHistoryRepository;
    private final GameLevelService gameLevelService;

    public SinglePlayerStrategy(GameRepository gameRepository,
                              GuessHistoryRepository guessHistoryRepository,
                              GameLevelService gameLevelService) {
        this.gameRepository = gameRepository;
        this.guessHistoryRepository = guessHistoryRepository;
        this.gameLevelService = gameLevelService;
    }

    @Override
    public StartGuesserResponse startGame(StartGuesserRequest request) {
        logger.info(GameConstants.LOG_SINGLE_PLAYER_STARTING, request.getPlayerId());

        // Generate a secret number based on the level
        String secretNumber = gameLevelService.generateSecretNumber(request.getLevel());
        logger.info("Generated secret number for level {}: {}", request.getLevel(), secretNumber);

        Guesser game = new Guesser();
        game.setPlayerId(request.getPlayerId());
        game.setSecretNumber(secretNumber); // Use computer-generated number
        game.setLevel(request.getLevel());
        game.setGameMode(GameConstants.SINGLE_PLAYER);
        game.setStatus(GameConstants.STATUS_IN_PROGRESS);
        game.setCurrentPlayerId(request.getPlayerId());

        game = gameRepository.save(game);
        logger.info(GameConstants.LOG_SINGLE_PLAYER_GAME_CREATED, request.getPlayerId(), game.getGameId());

        return new StartGuesserResponse(
            game.getGameId(),
            game.getStatus(),
            game.getSecretNumber(),
            game.getLevel(),
            game.getGameMode(),
            game.getPlayerId(),
            null
        );
    }

    @Override
    public GuessResponse submitGuess(Guesser game, String guess, String playerId) {
        logger.info(GameConstants.LOG_SINGLE_PLAYER_GUESS, playerId, guess, game.getGameId());

        if (GameConstants.STATUS_COMPLETED.equals(game.getStatus())) {
            throw new RuntimeException(String.format(GameConstants.ERROR_GAME_COMPLETED, game.getGameId()));
        }

        if (!playerId.equals(game.getCurrentPlayerId())) {
            throw new RuntimeException(String.format(GameConstants.ERROR_NOT_YOUR_TURN, playerId, game.getGameId()));
        }

        // Validate guess format and length
        if (guess.length() != gameLevelService.getNumberLengthForLevel(game.getLevel())) {
            throw new RuntimeException(String.format(GameConstants.ERROR_INVALID_GUESS, game.getLevel()));
        }

        int correctDigits = calculateCorrectDigits(guess, game.getSecretNumber());
        boolean isCorrect = correctDigits == game.getSecretNumber().length();

        game.setGuessCount(game.getGuessCount() + 1);

        if (isCorrect) {
            game.setStatus(GameConstants.STATUS_COMPLETED);
            game.setHasWon(true);
            gameRepository.save(game);
            logger.info(GameConstants.LOG_SINGLE_PLAYER_WON, playerId, game.getGameId());
            return new GuessResponse(
                correctDigits,
                game.getGuessCount(),
                GameConstants.STATUS_COMPLETED,
                guess,
                getMaxGuessesForLevel(game.getLevel()) - game.getGuessCount()
            );
        }

        if (game.getGuessCount() >= getMaxGuessesForLevel(game.getLevel())) {
            game.setStatus(GameConstants.STATUS_COMPLETED);
            gameRepository.save(game);
            logger.info(GameConstants.LOG_SINGLE_PLAYER_GAME_OVER, playerId, game.getGameId(), game.getGuessCount());
            return new GuessResponse(
                correctDigits,
                game.getGuessCount(),
                GameConstants.STATUS_COMPLETED,
                guess,
                0
            );
        }

        gameRepository.save(game);
        return new GuessResponse(
            correctDigits,
            game.getGuessCount(),
            game.getStatus(),
            guess,
            getMaxGuessesForLevel(game.getLevel()) - game.getGuessCount()
        );
    }

    private int getMaxGuessesForLevel(int level) {
        return switch (level) {
            case GameConstants.LEVEL_1 -> GameConstants.LEVEL_1_MAX_GUESSES;
            case GameConstants.LEVEL_2 -> GameConstants.LEVEL_2_MAX_GUESSES;
            case GameConstants.LEVEL_3 -> GameConstants.LEVEL_3_MAX_GUESSES;
            default -> GameConstants.LEVEL_1_MAX_GUESSES;
        };
    }

    private int calculateCorrectDigits(String guess, String secretNumber) {
        int count = 0;
        for (int i = 0; i < secretNumber.length(); i++) {
            if (guess.charAt(i) == secretNumber.charAt(i)) {
                count++;
            }
        }
        return count;
    }
} 