package com.tokiserskyy.computerclub.controller;

import com.tokiserskyy.computerclub.model.Computer;
import com.tokiserskyy.computerclub.service.ComputerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/computers")
public class ComputerController {
    ComputerService computerService;

    @GetMapping
    public List<Computer> getAllComputers() {
        return computerService.getAllComputers();
    }

    @GetMapping("/{id}")
    public Computer getComputerById(@PathVariable int id) {
        return computerService.getComputerById(id);
    }

    @PostMapping("/add")
    public Computer addComputer(@RequestBody Computer computer) {
        return computerService.addComputer(computer);
    }

    @PostMapping("/adds")
    public List<Computer> addComputers(@RequestBody List<Computer> computers) {
        return computerService.addComputers(computers);
    }

    @PutMapping("/{id}")
    public Computer updateComputer(@PathVariable int id, @RequestBody Computer computerDetails) {
        return computerService.updateComputer(id, computerDetails);
    }


    @DeleteMapping("/{id}")
    public void deleteComputerById(@PathVariable int id) {
        computerService.deleteComputerById(id);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Computer>> searchComputers(
            @RequestParam(required = false) String cpu,
            @RequestParam(required = false) String ram,
            @RequestParam(required = false) String gpu,
            @RequestParam(required = false) String monitor) {
        List<Computer> computers = computerService.searchComputers(cpu, ram, gpu, monitor);
        return computers.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(computers);
    }
}
