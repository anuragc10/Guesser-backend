package com.guesser.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
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
    
    @ManyToOne
    @JoinColumn(name = "room_id")
    private GameRoom room;
    
    private String playerId;
    private String secretNumber;
    private int guessCount;
    private boolean hasWon;
    private String status; // IN_PROGRESS, COMPLETED
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int level;
    private String gameMode; // SINGLE_PLAYER, MULTIPLAYER
    private String currentPlayerId; // ID of the player whose turn it is
    
    {
        this.startTime = LocalDateTime.now();
        this.status = "IN_PROGRESS";
        this.guessCount = 0;
        this.hasWon = false;
        this.level = 1;
        this.gameMode = "SINGLE_PLAYER";
    }
} 