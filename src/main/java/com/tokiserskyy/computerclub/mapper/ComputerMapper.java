package com.tokiserskyy.computerclub.mapper;

import java.util.ArrayList;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import com.tokiserskyy.computerclub.dto.ComputerDto;
import com.tokiserskyy.computerclub.model.Computer;

@UtilityClass
public class ComputerMapper {
    public ComputerDto toDtoShallow(Computer computer) {
        if (computer == null) return null;
        ComputerDto dto = new ComputerDto();
        dto.setId(computer.getId());
        dto.setMonitor(computer.getMonitor());
        dto.setGpu(computer.getGpu());
        dto.setCpu(computer.getCpu());
        dto.setRam(computer.getRam());
        return dto;
    }

    public ComputerDto toDto(Computer computer) {
        if (computer == null) return null;
        ComputerDto dto = toDtoShallow(computer);
        dto.setGames(
                computer.getGames() == null
                        ? new ArrayList<>()
                        : computer.getGames().stream()
                        .map(GameMapper::toDtoShallow)
                        .collect(Collectors.toList())
        );

        dto.setBookings(
                computer.getBookings() == null
                        ? new ArrayList<>()
                        : computer.getBookings().stream()
                        .map(BookingMapper::toDtoWithoutComputers)
                        .collect(Collectors.toList())
        );
        return dto;
    }

    public ComputerDto toDtoWithoutBookings(Computer computer) {
        if (computer == null) return null;
        ComputerDto dto = toDtoShallow(computer);
        dto.setGames(computer.getGames().stream()
                .map(GameMapper::toDtoShallow)
                .collect(Collectors.toList()));
        return dto;
    }

    public Computer toEntity(ComputerDto dto) {
        if (dto == null) return null;
        Computer computer = new Computer();
        computer.setId(dto.getId());
        computer.setMonitor(dto.getMonitor());
        computer.setGpu(dto.getGpu());
        computer.setCpu(dto.getCpu());
        computer.setRam(dto.getRam());
        return computer;
    }
}
