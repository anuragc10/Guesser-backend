package com.guesser.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnNotification {
    private String roomId;
    private String playerId; // Player who made the guess
    private String guessedNumber;
    private int correctDigits;
    private int guessNumber;
    private Integer remainingAttempts;
    private String currentPlayerId; // Player whose turn it is now
    private String status; // Game status
    private String message; // Notification message
}

