package com.guesser.demo.controller;

import com.guesser.demo.dto.GuessRequest;
import com.guesser.demo.dto.GuessResponse;
import com.guesser.demo.dto.StartGuesserResponse;
import com.guesser.demo.service.GuesserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
public class GuesserController {

    @Autowired
    private GuesserService guesserService;

    @PostMapping("/start")
    public ResponseEntity<StartGuesserResponse> startNewGame() {
        return ResponseEntity.ok(guesserService.startNewGame());
    }

    @PostMapping("/guess")
    public ResponseEntity<GuessResponse> submitGuess(
            @RequestHeader("X-Game-ID") String gameId,
            @RequestBody GuessRequest request) {
        return ResponseEntity.ok(guesserService.submitGuess(gameId, request.getGuess()));
    }
} 