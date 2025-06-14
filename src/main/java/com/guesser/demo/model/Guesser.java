package com.guesser.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Guesser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String gameId;
    
    private String secretNumber;
    private int guessCount;
    private String status; // IN_PROGRESS, SUCCESS, FAILED
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    {
        this.startTime = LocalDateTime.now();
        this.status = "IN_PROGRESS";
        this.guessCount = 0;
    }
} 