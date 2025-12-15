package com.guesser.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerJoinedNotification {
    private String roomId;
    private String joinedPlayerId;
    private String message;
    private String status; // Room status after player joined
}

