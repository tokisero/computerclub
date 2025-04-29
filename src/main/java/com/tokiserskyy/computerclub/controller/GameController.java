package com.tokiserskyy.computerclub.controller;


import com.tokiserskyy.computerclub.dto.GameDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.tokiserskyy.computerclub.service.GameService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/game")
@Tag(name = "Games", description = "Endpoints for managing games and installing them on computers")
public class GameController {
    private final GameService gameService;

    @GetMapping
    @Operation(summary = "Get all games")
    public List<GameDto> getAllGames() {
        return gameService.getAllGames();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get game by ID")
    public GameDto getGameById(@PathVariable int id) {
        return gameService.getGameById(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Search games by genre")
    public List<GameDto> findGamesByGenre(@RequestParam String genre) {
        return gameService.findGamesByGenre(genre);
    }

    @PostMapping("/add")
    @Operation(summary = "Add new game")
    public GameDto addGame(@Valid @RequestBody GameDto dto) {
        return gameService.addGame(dto);
    }

    @PostMapping("/setup/{computerId}")
    @Operation(summary = "Install game on computer")
    public GameDto addGameToComputer(@RequestParam int id, @PathVariable int computerId) {
        return gameService.addGameOnComputer(id, computerId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update game by ID")
    public GameDto updateGame(@PathVariable int id, @Valid @RequestBody GameDto dto) {
        return gameService.updateGame(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete game by ID")
    public void deleteGame(@PathVariable int id) {
        gameService.deleteGameById(id);
    }
}
