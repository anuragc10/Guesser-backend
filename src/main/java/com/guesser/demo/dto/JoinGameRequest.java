package com.guesser.demo.dto;

import lombok.Data;

@Data
public class JoinGameRequest {
    private String gameId;
    private String playerId;
} 