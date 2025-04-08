package com.tokiserskyy.computerclub.controller;


import lombok.extern.slf4j.Slf4j;
import com.tokiserskyy.computerclub.model.Game;
import com.tokiserskyy.computerclub.service.GameService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/game")
public class GameController {
    GameService gameService;

    @GetMapping
    public List<Game> getAllGames(){
        return gameService.getAllGames();
    }

    @GetMapping("/{id}")
    public Game getGameById(@PathVariable int id){
        return gameService.getGameById(id);
    }

    @GetMapping("/search")
    public List<Game> findGamesByGenre(@RequestParam String genre){
        return gameService.findByGamesByGenre(genre);
    }

    @PostMapping("/add")
    public Game addGame(@RequestBody Game game){
        return gameService.addGame(game);
    }

    @PostMapping("/setup/{computerId}")
    public Game addGameToComputer(@RequestParam int id, @PathVariable int computerId){
        return gameService.addGameOnComputer(id,computerId);
    }

    @PostMapping("/add-to-favourite/{userId}")
    public Game addGameToFavourite(@RequestParam int id, @PathVariable int userId){
        return gameService.addFavouriteGame(id,userId);
    }

    @PutMapping("/{id}")
    public Game updateGame(@PathVariable int id, @RequestBody Game game){
        return gameService.updateGame(id, game);
    }

    @DeleteMapping("/{id}")
    public void deleteGame(@PathVariable int id){
        gameService.deleteGameById(id);
    }
}
