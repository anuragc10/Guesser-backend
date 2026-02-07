package com.guesser.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameRoomResponse {
    private String roomId;
    private String status;
    private int level;
    private boolean limitAttempts;
    private String player1Id;
    private String player2Id;
}
