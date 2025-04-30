package com.tokiserskyy.computerclub.controller;

import com.tokiserskyy.computerclub.dto.ComputerDto;
import com.tokiserskyy.computerclub.service.ComputerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Computers", description = "Endpoints for managing computers")
public class ComputerController {
    private final ComputerService computerService;

    @GetMapping
    @Operation(summary = "Get all computers")
    public List<ComputerDto> getAllComputers() {
        return computerService.getAllComputers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get computer by ID")
    public ComputerDto getComputerById(@PathVariable int id) {
        return computerService.getComputerById(id);
    }

    @PostMapping("/add")
    @Operation(summary = "Add a new computer")
    public ComputerDto addComputer(@Valid @RequestBody ComputerDto computer) {
        return computerService.addComputer(computer);
    }

    @PostMapping("/bulk-add")
    @Operation(
            summary = "Add multiple computers at once",
            description = "Takes a list of ComputerDto objects and saves them to the database in bulk. " +
                    "All fields are validated. Returns the saved objects with generated IDs."
    )
    public List<ComputerDto> addComputers(@Valid @RequestBody List<@Valid ComputerDto> computers) {
        return computerService.addComputersBulk(computers);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Update a computer")
    public ComputerDto updateComputer(@PathVariable int id, @Valid @RequestBody ComputerDto computerDetails) {
        return computerService.updateComputer(id, computerDetails);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a computer by ID")
    public void deleteComputerById(@PathVariable int id) {
        computerService.deleteComputerById(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Search computers by components")
    public ResponseEntity<List<ComputerDto>> searchComputers(
            @RequestParam(required = false) String cpu,
            @RequestParam(required = false) String ram,
            @RequestParam(required = false) String gpu,
            @RequestParam(required = false) String monitor) {
        List<ComputerDto> computers = computerService.searchComputers(cpu, ram, gpu, monitor);
        return computers.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(computers);
    }
}
