package com.guesser.demo.controller;

import com.guesser.demo.dto.StartGuesserRequest;
import com.guesser.demo.dto.StartGuesserResponse;
import com.guesser.demo.dto.GuessRequest;
import com.guesser.demo.dto.GuessResponse;
import com.guesser.demo.service.GuesserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketGameController {

    @Autowired
    private GuesserService guesserService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/game/start")
    @SendTo("/topic/game")
    public StartGuesserResponse startGame(StartGuesserRequest request) {
        return guesserService.startNewGame(request);
    }

    @MessageMapping("/game/guess")
    public void submitGuess(
            @Payload GuessRequest request
    ) {
        GuessResponse response = guesserService.submitGuess(
                request.getGameId(),
                request.getGuess(),
                request.getPlayerId()
        );
        messagingTemplate.convertAndSend("/topic/game/" + request.getGameId(), response);
    }
} 