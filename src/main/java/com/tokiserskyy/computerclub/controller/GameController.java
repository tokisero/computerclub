package com.tokiserskyy.computerclub.controller;


import com.tokiserskyy.computerclub.dto.GameDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.tokiserskyy.computerclub.service.GameService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/game")
public class GameController {
    private final GameService gameService;

    @GetMapping
    public List<GameDto> getAllGames() {
        return gameService.getAllGames();
    }

    @GetMapping("/{id}")
    public GameDto getGameById(@PathVariable int id) {
        return gameService.getGameById(id);
    }

    @GetMapping("/search")
    public List<GameDto> findGamesByGenre(@RequestParam String genre) {
        return gameService.findGamesByGenre(genre);
    }

    @PostMapping("/add")
    public GameDto addGame(@RequestBody GameDto dto) {
        return gameService.addGame(dto);
    }

    @PostMapping("/setup/{computerId}")
    public GameDto addGameToComputer(@RequestParam int id, @PathVariable int computerId) {
        return gameService.addGameOnComputer(id, computerId);
    }

    @PutMapping("/{id}")
    public GameDto updateGame(@PathVariable int id, @RequestBody GameDto dto) {
        return gameService.updateGame(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteGame(@PathVariable int id) {
        gameService.deleteGameById(id);
    }
}
