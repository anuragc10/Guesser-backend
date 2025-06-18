package com.guesser.demo.repository;

import com.guesser.demo.model.Guesser;
import com.guesser.demo.model.GameRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Guesser, String> {
    Optional<Guesser> findByRoomAndPlayerIdNot(GameRoom room, String playerId);
    boolean existsByRoomAndPlayerId(GameRoom room, String playerId);
    long countByRoom(GameRoom room);
    Optional<Guesser> findByRoomAndPlayerId(GameRoom room, String playerId);
    boolean existsByPlayerId(String playerId);
    Optional<Guesser> findByPlayerId(String playerId);
    boolean existsByPlayerIdAndStatus(String playerId, String status);
} 