package com.tokiserskyy.computerclub.repository;

import com.tokiserskyy.computerclub.model.Computer;
import com.tokiserskyy.computerclub.model.Game;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {


    List<Game> getGamesByGenre(String genre);
}
