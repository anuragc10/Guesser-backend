package com.guesser.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartGuesserResponse {
    private String gameId;
    private String status;
    private String secretNumber;
    private int level;
    private String gameMode; // SINGLE_PLAYER or MULTIPLAYER
    private String playerId;
    private String roomId;

} 