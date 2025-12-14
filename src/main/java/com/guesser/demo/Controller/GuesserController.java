package com.guesser.demo.Controller;

import com.guesser.demo.dto.*;
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
            @RequestBody GuessRequest request) {
        return ResponseEntity.ok(guesserService.submitGuess(request.getGameId(), request.getGuess(), request.getPlayerId()));
    }
    
    @GetMapping("/history")
    public ResponseEntity<List<GuessHistoryResponse>> getGuessHistory(
            @RequestBody GuessHistoryRequest request) {
        return ResponseEntity.ok(guesserService.getGuessHistory(request.getGameId()));
    }
    
    @PostMapping("/end")
    public ResponseEntity<EndGameResponse> endGame(@RequestBody EndGameRequest request) {
        return ResponseEntity.ok(guesserService.endGame(request));
    }
} 