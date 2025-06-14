package com.guesser.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartGameResponse {
    private String gameId;
    private String status;
    private String secretNumber;
} 