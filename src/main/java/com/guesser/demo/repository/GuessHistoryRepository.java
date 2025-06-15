package com.guesser.demo.repository;

import com.guesser.demo.model.GuessHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GuessHistoryRepository extends JpaRepository<GuessHistory, String> {
    List<GuessHistory> findByGameGameIdOrderByGuessTimeDesc(String gameId);
} 