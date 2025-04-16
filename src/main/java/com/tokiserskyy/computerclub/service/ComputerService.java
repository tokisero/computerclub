package com.tokiserskyy.computerclub.service;

import com.tokiserskyy.computerclub.dto.ComputerDto;
import com.tokiserskyy.computerclub.mapper.ComputerMapper;
import com.tokiserskyy.computerclub.model.Computer;
import com.tokiserskyy.computerclub.repository.ComputerRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComputerService {

    private final ComputerRepository computerRepository;

    public List<ComputerDto> getAllComputers() {
        return computerRepository.findAllWithBookingsAndPersons()
                .stream()
                .map(ComputerMapper::toDto)
                .toList();
    }

    public ComputerDto getComputerById(int id) {
        return computerRepository.findById(id)
                .map(ComputerMapper::toDto)
                .orElse(null);
    }

    public ComputerDto addComputer(Computer computer) {
        return ComputerMapper.toDtoShallow(computerRepository.save(computer));
    }

    public void deleteComputerById(int id) {
        computerRepository.deleteById(id);
    }

    public ComputerDto updateComputer(int id, Computer computerDetails) {
        Computer computer = computerRepository.findById(id).orElse(null);
        if (computer != null) {
            computer.setCpu(computerDetails.getCpu());
            computer.setRam(computerDetails.getRam());
            computer.setGpu(computerDetails.getGpu());
            computer.setMonitor(computerDetails.getMonitor());
            return ComputerMapper.toDto(computerRepository.save(computer));
        }
        return null;
    }

    public List<ComputerDto> addComputers(List<Computer> computers) {
        return computerRepository.saveAll(computers)
                .stream()
                .map(ComputerMapper::toDtoShallow)
                .toList();
    }

    public List<ComputerDto> searchComputers(String cpu, String ram, String gpu, String monitor) {
        Specification<Computer> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (cpu != null) predicates.add(cb.equal(root.get("cpu"), cpu));
            if (ram != null) predicates.add(cb.equal(root.get("ram"), Integer.parseInt(ram)));
            if (gpu != null) predicates.add(cb.equal(root.get("gpu"), gpu));
            if (monitor != null) predicates.add(cb.equal(root.get("monitor"), Integer.parseInt(monitor)));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return computerRepository.findAll(specification)
                .stream()
                .map(ComputerMapper::toDto)
                .toList();
    }
}
