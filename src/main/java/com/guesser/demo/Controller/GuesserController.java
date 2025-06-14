package com.guesser.demo.Controller;

import com.guesser.demo.dto.StartGuesserResponse;
import com.guesser.demo.service.GuesserService;
import com.guesser.demo.dto.GuessResponse;
import com.guesser.demo.dto.GuessRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
public class GuesserController {

    @Autowired
    private GuesserService gameService;

    @PostMapping("/start")
    public ResponseEntity<StartGuesserResponse> startGame() {
        return ResponseEntity.ok(gameService.startNewGame());
    }

    @PostMapping("/guess")
    public ResponseEntity<GuessResponse> submitGuess(@RequestBody GuessRequest request) {
        return ResponseEntity.ok(gameService.submitGuess(request.getGameId(), request.getGuess()));
    }
} 