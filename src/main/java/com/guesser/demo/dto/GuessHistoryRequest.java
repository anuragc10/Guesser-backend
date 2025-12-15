package com.guesser.demo.dto;

import lombok.Data;

@Data
public class GuessHistoryRequest {
    private String roomId; // Changed from gameId to roomId
}
