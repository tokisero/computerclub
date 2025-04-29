package com.tokiserskyy.computerclub.repository;

import com.tokiserskyy.computerclub.model.Game;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {


    List<Game> getGamesByGenre(String genre);

    boolean existsByName(@NotBlank(message = "Game name cannot be empty") String name);
}
