package com.tokiserskyy.computerclub.controller;

import com.tokiserskyy.computerclub.dto.ComputerDto;
import com.tokiserskyy.computerclub.model.Computer;
import com.tokiserskyy.computerclub.service.ComputerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/computers")
public class ComputerController {
    private final ComputerService computerService;

    @GetMapping
    public List<ComputerDto> getAllComputers() {
        return computerService.getAllComputers();
    }

    @GetMapping("/{id}")
    public ComputerDto getComputerById(@PathVariable int id) {
        return computerService.getComputerById(id);
    }

    @PostMapping("/add")
    public ComputerDto addComputer(@Valid @RequestBody ComputerDto computer) {
        return computerService.addComputer(computer);
    }

    @PostMapping("/adds")
    public List<ComputerDto> addComputers(@RequestBody List<Computer> computers) {
        return computerService.addComputers(computers);
    }

    @PutMapping("/{id}")
    public ComputerDto updateComputer(@PathVariable int id, @Valid @RequestBody ComputerDto computerDetails) {
        return computerService.updateComputer(id, computerDetails);
    }


    @DeleteMapping("/{id}")
    public void deleteComputerById(@PathVariable int id) {
        computerService.deleteComputerById(id);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ComputerDto>> searchComputers(
            @RequestParam(required = false) String cpu,
            @RequestParam(required = false) String ram,
            @RequestParam(required = false) String gpu,
            @RequestParam(required = false) String monitor) {
        List<ComputerDto> computers = computerService.searchComputers(cpu, ram, gpu, monitor);
        return computers.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(computers);
    }
}
