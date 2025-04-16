package com.tokiserskyy.computerclub.mapper;

import lombok.experimental.UtilityClass;
import com.tokiserskyy.computerclub.dto.GameDto;
import com.tokiserskyy.computerclub.model.Game;

import java.util.stream.Collectors;

@UtilityClass
public class GameMapper {
    public GameDto toDtoShallow(Game game) {
        if (game == null) return null;
        GameDto dto = new GameDto();
        dto.setId(game.getId());
        dto.setName(game.getName());
        dto.setGenre(game.getGenre());
        return dto;
    }

    public GameDto toDto(Game game) {
        if (game == null) return null;
        GameDto dto = toDtoShallow(game);
        dto.setComputers(game.getComputers().stream()
                .map(ComputerMapper::toDtoShallow)
                .collect(Collectors.toList()));
        return dto;
    }

    public Game toEntity(GameDto dto) {
        if (dto == null) return null;
        Game game = new Game();
        game.setId(dto.getId());
        game.setName(dto.getName());
        game.setGenre(dto.getGenre());
        return game;
    }
}
