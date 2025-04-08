package com.tokiserskyy.computerclub.service;

import com.tokiserskyy.computerclub.model.Computer;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

import com.tokiserskyy.computerclub.repository.ComputerRepository;
import com.tokiserskyy.computerclub.model.Game;
import com.tokiserskyy.computerclub.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ComputerService {
    @Autowired
    private ComputerRepository computerRepository;

    public List<Computer> getAllComputers() {
        return computerRepository.findAllWithBookingsAndPersons();
    }

    public Computer getComputerById(int id) {
        return computerRepository.findByIdWithBookingsAndPersons(id).orElse(null);
    }

    public Computer addComputer(Computer computer) {
        return computerRepository.save(computer);
    }

    public void deleteComputerById(int id) {
        computerRepository.deleteById(id);
    }


    public Computer updateComputer(int id, Computer computerDetails) {
        Computer computer = getComputerById(id);
        if (computer != null) {
            computer.setCpu(computerDetails.getCpu());
            computer.setRam(computerDetails.getRam());
            computer.setGpu(computerDetails.getGpu());
            computer.setMonitor(computerDetails.getMonitor());
            return computerRepository.save(computer);
        }
        return null;
    }

    public List<Computer> addComputers(List<Computer> computers) {
        return computerRepository.saveAll(computers);
    }

    public List<Computer> searchComputers(String cpu, String ram, String gpu, String monitor) {
        Specification<Computer> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (cpu != null) predicates.add(cb.equal(root.get("cpu"), cpu));
            if (ram != null) predicates.add(cb.equal(root.get("ram"), ram));
            if (gpu != null) predicates.add(cb.equal(root.get("gpu"), gpu));
            if (monitor != null) predicates.add(cb.equal(root.get("monitor"), monitor));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return computerRepository.findAll(specification);
    }
}

