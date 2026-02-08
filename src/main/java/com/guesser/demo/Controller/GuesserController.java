package com.guesser.demo.Controller;

import com.guesser.demo.dto.*;
import com.guesser.demo.service.GuesserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/guess")
public class GuesserController {

    private static final Logger logger = LoggerFactory.getLogger(GuesserController.class);

    @Autowired
    private GuesserService guesserService;

    @PostMapping("/start")
    public ResponseEntity<StartGuesserResponse> startNewGame(@RequestBody(required = false) StartGuesserRequest request) {
        logger.info("Starting new game with request: {}", request);
        return ResponseEntity.ok(guesserService.startNewGame(request));
    }

    @PostMapping("/guess")
    public ResponseEntity<GuessResponse> submitGuess(
            @RequestBody GuessRequest request) {
        return ResponseEntity.ok(guesserService.submitGuess(request.getGameId(), request.getGuess(), request.getPlayerId()));
    }
    
    @PostMapping("/history")
    public ResponseEntity<List<GuessHistoryResponse>> getGuessHistory(
            @RequestBody GuessHistoryRequest request) {
        return ResponseEntity.ok(guesserService.getGuessHistory(request.getRoomId()));
    }
    
    @PostMapping("/end")
    public ResponseEntity<EndGameResponse> endGame(@RequestBody EndGameRequest request) {
        return ResponseEntity.ok(guesserService.endGame(request));
    }

    @GetMapping("/roomDetails/{roomId}")
    public ResponseEntity<GameRoomResponse> getRoomDetails(
            @PathVariable String roomId) {
        
        logger.info("Checking room details for roomId: {}", roomId);
        
        return ResponseEntity.ok(
            guesserService.getRoomDetails(roomId)
        );
    }
}