package com.guesser.demo.Controller;

import com.guesser.demo.dto.GuessRequest;
import com.guesser.demo.dto.GuessResponse;
import com.guesser.demo.dto.StartGuesserResponse;
import com.guesser.demo.dto.StartGuesserRequest;
import com.guesser.demo.dto.GuessHistoryResponse;
import com.guesser.demo.service.GuesserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/guess")
@CrossOrigin(origins = "*")
public class GuesserController {

    @Autowired
    private GuesserService guesserService;

    @PostMapping("/start")
    public ResponseEntity<StartGuesserResponse> startNewGame(@RequestBody(required = false) StartGuesserRequest request) {
        return ResponseEntity.ok(guesserService.startNewGame(request));
    }

    @PostMapping("/guess")
    public ResponseEntity<GuessResponse> submitGuess(
            @RequestHeader("X-Game-ID") String gameId,
            @RequestHeader("X-Player-ID") String playerId,
            @RequestBody GuessRequest request) {
        return ResponseEntity.ok(guesserService.submitGuess(gameId, request.getGuess(), playerId));
    }
    
    @GetMapping("/history")
    public ResponseEntity<List<GuessHistoryResponse>> getGuessHistory(
            @RequestHeader("X-Game-ID") String gameId) {
        return ResponseEntity.ok(guesserService.getGuessHistory(gameId));
    }
} 