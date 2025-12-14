package com.guesser.demo.dto;

import lombok.Data;

@Data
public class EndGameRequest {
    private String gameId;
    private String playerId;
}

