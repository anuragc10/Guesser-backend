package com.guesser.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuessResponse {
    private int correctDigits;
    private int guessNumber;
    private String status;
    private String guessedNumber;
    private Integer remainingAttempts;
} 