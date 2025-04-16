package com.tokiserskyy.computerclub.service;

import com.tokiserskyy.computerclub.dto.GameDto;
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

    public List<GameDto> getAllGames() {
        return gameRepository.findAll()
                .stream()
                .map(GameMapper::toDto)
                .toList();
    }

    public List<GameDto> findGamesByGenre(String genre) {
        return gameRepository.getGamesByGenre(genre)
                .stream()
                .map(GameMapper::toDto)
                .toList();
    }

    public GameDto getGameById(int id) {
        Game game = gameRepository.findById(id).orElse(null);
        return game != null ? GameMapper.toDto(game) : null;
    }

    public GameDto addGame(GameDto dto) {
        Game game = GameMapper.toEntity(dto);
        return GameMapper.toDtoShallow(gameRepository.save(game));
    }

    @Transactional
    public GameDto addGameOnComputer(int gameId, int computerId) {
        Game game = gameRepository.findById(gameId).orElse(null);
        Computer computer = computerRepository.findById(computerId).orElse(null);
        if (game == null || computer == null) return null;

        game.getComputers().add(computer);
        computer.getGames().add(game);
        return GameMapper.toDto(gameRepository.save(game));
    }

    public GameDto updateGame(int id, GameDto dto) {
        Game game = gameRepository.findById(id).orElse(null);
        if (game != null) {
            game.setName(dto.getName());
            game.setGenre(dto.getGenre());
            return GameMapper.toDto(gameRepository.save(game));
        }
        return null;
    }

    @Transactional
    public void deleteGameById(int id) {
        Game game = gameRepository.findById(id).orElse(null);
        for (Computer computer : game.getComputers()) {
            computer.getGames().remove(game);
        }
        gameRepository.delete(game);
    }
}
