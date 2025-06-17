package com.guesser.demo.repository;

import com.guesser.demo.model.GameRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GameRoomRepository extends JpaRepository<GameRoom, String> {
    Optional<GameRoom> findByStatusAndPlayer2IdIsNull(String status);
} 