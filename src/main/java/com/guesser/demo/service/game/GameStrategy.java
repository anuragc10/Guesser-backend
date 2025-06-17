package com.guesser.demo.service.game;

import com.guesser.demo.model.Guesser;
import com.guesser.demo.dto.GuessResponse;
import com.guesser.demo.dto.StartGuesserResponse;
import com.guesser.demo.dto.StartGuesserRequest;

public interface GameStrategy {
    StartGuesserResponse startGame(StartGuesserRequest request);
    GuessResponse submitGuess(Guesser game, String guess, String playerId);
} 