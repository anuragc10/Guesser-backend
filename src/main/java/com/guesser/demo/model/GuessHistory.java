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
public class GuessHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @ManyToOne
    @JoinColumn(name = "game_id")
    private Guesser game;
    
    private String guessedNumber;
    private int correctDigits;
    private LocalDateTime guessTime;
    private String playerId;
    
    public GuessHistory(Guesser game, String guessedNumber, int correctDigits) {
        this.game = game;
        this.guessedNumber = guessedNumber;
        this.correctDigits = correctDigits;
        this.guessTime = LocalDateTime.now();
    }
} 