package com.guesser.demo.dto;

import lombok.Data;

@Data
public class StartGuesserRequest {
    private int level = 1; // Default level is 1
    private String gameMode = "SINGLE_PLAYER"; // SINGLE_PLAYER or MULTIPLAYER
    private String playerId; // ID of the player
    private String secretNumber; // Secret number for multiplayer mode
    private String roomId; // Optional room ID for multiplayer mode
    private Boolean limitAttempts = true; // Toggle to enforce attempt limits per game
} 