package com.guesser.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class GameRoom {
    @Id
    private String roomId;
    
    private String player1Id;
    private String player2Id;
    private String status; // WAITING_FOR_PLAYER, IN_PROGRESS, COMPLETED
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int level;
    
    {
        this.startTime = LocalDateTime.now();
        this.status = "WAITING_FOR_PLAYER";
        this.level = 1;
    }
} 