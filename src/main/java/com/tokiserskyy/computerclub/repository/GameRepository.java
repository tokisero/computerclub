package com.tokiserskyy.computerclub.repository;

import com.tokiserskyy.computerclub.model.Computer;
import com.tokiserskyy.computerclub.model.Game;

import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {

    @Query("SELECT DISTINCT c FROM Game g JOIN g.computers c")
    List<Computer> getComputersWithGame();

    @Query("SELECT DISTINCT u FROM Game g JOIN g.persons u")
    List<User> getPersonsWithGame();

    List<Game> getGamesByGenre(String genre);
}
