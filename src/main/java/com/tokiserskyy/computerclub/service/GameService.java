package com.tokiserskyy.computerclub.service;

import com.tokiserskyy.computerclub.dto.GameDto;
import com.tokiserskyy.computerclub.exception.BadRequestException;
import com.tokiserskyy.computerclub.exception.NotFoundException;
import com.tokiserskyy.computerclub.mapper.GameMapper;
import com.tokiserskyy.computerclub.model.Computer;
import com.tokiserskyy.computerclub.model.Game;
import com.tokiserskyy.computerclub.repository.ComputerRepository;
import com.tokiserskyy.computerclub.repository.GameRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final ComputerRepository computerRepository;
    private static final String GAME_WTIH_ID = "Game with ID ";

    public List<GameDto> getAllGames() {
        List<GameDto> games = gameRepository.findAll()
                .stream()
                .map(GameMapper::toDto)
                .toList();
        if (games.isEmpty()) {
            throw new NotFoundException("Game not found");
        }
        return games;
    }

    public List<GameDto> findGamesByGenre(String genre) {
        if (genre == null || genre.isBlank()) {
            throw new BadRequestException("Genre must not be empty.");
        }

        List<GameDto> games = gameRepository.getGamesByGenre(genre)
                .stream()
                .map(GameMapper::toDto)
                .toList();
        if (games.isEmpty()) {
            throw new NotFoundException("No Games found.");
        }
        return games;
    }

    public GameDto getGameById(int id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(GAME_WTIH_ID + id));
        return GameMapper.toDto(game);
    }

    public GameDto addGame(GameDto dto) {
        if (dto == null || dto.getName() == null || dto.getGenre() == null) {
            throw new BadRequestException("Game name and genre are required.");
        }

        if (gameRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("This games is already exist.");
        }
        Game game = GameMapper.toEntity(dto);
        return GameMapper.toDtoShallow(gameRepository.save(game));
    }

    @Transactional
    public GameDto addGameOnComputer(int gameId, int computerId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(GAME_WTIH_ID + gameId));
        Computer computer = computerRepository.findById(computerId)
                .orElseThrow(() -> new NotFoundException("Computer with ID " + computerId));

        if (!game.getComputers().contains(computer)) {
            game.getComputers().add(computer);
        }

        if (computer.getGames() == null) {
            computer.setGames(new java.util.ArrayList<>());
        }
        if (!computer.getGames().contains(game)) {
            computer.getGames().add(game);
        }

        return GameMapper.toDto(gameRepository.save(game));
    }

    public GameDto updateGame(int id, GameDto dto) {
        if (dto == null || dto.getName() == null || dto.getGenre() == null) {
            throw new BadRequestException("Game name and genre are required.");
        }

        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(GAME_WTIH_ID + id));
        if (!game.getName().equals(dto.getName()) &&
                gameRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("This games is already exist.");
        }

        game.setName(dto.getName());
        game.setGenre(dto.getGenre());

        return GameMapper.toDto(gameRepository.save(game));
    }

    @Transactional
    public void deleteGameById(int id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(GAME_WTIH_ID + id));

        for (Computer computer : game.getComputers()) {
            if (computer.getGames() != null) {
                computer.getGames().remove(game);
            }
        }

        gameRepository.delete(game);
    }
}
