package com.guesser.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuessHistoryResponse {
    private String guessedNumber;
    private int correctDigits;
    private LocalDateTime guessTime;
}