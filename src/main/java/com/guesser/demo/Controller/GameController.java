package com.guesser.demo.Controller;

import com.guesser.demo.dto.StartGameResponse;
import com.guesser.demo.service.GameService;
import com.guesser.demo.dto.GameResponse;
import com.guesser.demo.dto.GuessRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
public class GameController {

    @Autowired
    private GameService gameService;

    @PostMapping("/start")
    public ResponseEntity<StartGameResponse> startGame() {
        return ResponseEntity.ok(gameService.startNewGame());
    }

    @PostMapping("/guess")
    public ResponseEntity<GameResponse> submitGuess(@RequestBody GuessRequest request) {
        return ResponseEntity.ok(gameService.submitGuess(request.getGameId(), request.getGuess()));
    }
} 