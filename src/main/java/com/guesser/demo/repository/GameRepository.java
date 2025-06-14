package com.guesser.demo.repository;

import com.guesser.demo.model.Guesser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Guesser, String> {
} 