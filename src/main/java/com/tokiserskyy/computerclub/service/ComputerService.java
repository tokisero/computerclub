package com.tokiserskyy.computerclub.service;

import com.tokiserskyy.computerclub.cache.InMemoryCache;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ComputerService {

    private final ComputerRepository computerRepository;
    private final InMemoryCache<String, Object> cache;

    private static final long TTL_MILLIS = 300_000;
    private static final String ALL_COMPUTERS_KEY = "all_computers";
    private static final String COMPUTER_BY_ID_KEY_PREFIX = "computer_";
    private static final String SEARCH_COMPUTERS_KEY_PREFIX = "search_";

    @SuppressWarnings("unchecked")
    public List<ComputerDto> getAllComputers() {
        Optional<Object> cached = cache.get(ALL_COMPUTERS_KEY);
        if (cached.isPresent()) {
            return (List<ComputerDto>) cached.get();
        }

        List<ComputerDto> computers = computerRepository.findAll()
                .stream()
                .map(ComputerMapper::toDto)
                .toList();
        cache.put(ALL_COMPUTERS_KEY, computers, TTL_MILLIS);
        return computers;
    }

    @SuppressWarnings("unchecked")
    public ComputerDto getComputerById(int id) {
        String cacheKey = COMPUTER_BY_ID_KEY_PREFIX + id;
        Optional<Object> cached = cache.get(cacheKey);
        if (cached.isPresent()) {
            return (ComputerDto) cached.get();
        }

        ComputerDto computer = computerRepository.findById(id)
                .map(ComputerMapper::toDto)
                .orElse(null);
        if (computer != null) {
            cache.put(cacheKey, computer, TTL_MILLIS);
        }
        return computer;
    }

    public ComputerDto addComputer(Computer computer) {
        ComputerDto result = ComputerMapper.toDtoShallow(computerRepository.save(computer));
        cache.remove(ALL_COMPUTERS_KEY);
        return result;
    }

    public void deleteComputerById(int id) {
        computerRepository.deleteById(id);
        cache.remove(COMPUTER_BY_ID_KEY_PREFIX + id);
        cache.remove(ALL_COMPUTERS_KEY);
    }

    public ComputerDto updateComputer(int id, Computer computerDetails) {
        Computer computer = computerRepository.findById(id).orElse(null);
        if (computer != null) {
            computer.setCpu(computerDetails.getCpu());
            computer.setRam(computerDetails.getRam());
            computer.setGpu(computerDetails.getGpu());
            computer.setMonitor(computerDetails.getMonitor());
            ComputerDto result = ComputerMapper.toDto(computerRepository.save(computer));
            cache.remove(COMPUTER_BY_ID_KEY_PREFIX + id);
            cache.remove(ALL_COMPUTERS_KEY);
            return result;
        }
        return null;
    }

    public List<ComputerDto> addComputers(List<Computer> computers) {
        List<ComputerDto> result = computerRepository.saveAll(computers)
                .stream()
                .map(ComputerMapper::toDtoShallow)
                .toList();
        cache.remove(ALL_COMPUTERS_KEY);
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<ComputerDto> searchComputers(String cpu, String ram, String gpu, String monitor) {
        String cacheKey = SEARCH_COMPUTERS_KEY_PREFIX + cpu + "_" + ram + "_" + gpu + "_" + monitor;
        Optional<Object> cached = cache.get(cacheKey);
        if (cached.isPresent()) {
            return (List<ComputerDto>) cached.get();
        }

        Specification<Computer> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (cpu != null) predicates.add(cb.equal(root.get("cpu"), cpu));
            if (ram != null) predicates.add(cb.equal(root.get("ram"), Integer.parseInt(ram)));
            if (gpu != null) predicates.add(cb.equal(root.get("gpu"), gpu));
            if (monitor != null) predicates.add(cb.equal(root.get("monitor"), Integer.parseInt(monitor)));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<ComputerDto> computers = computerRepository.findAll(specification)
                .stream()
                .map(ComputerMapper::toDto)
                .toList();
        cache.put(cacheKey, computers, TTL_MILLIS);
        return computers;
    }
}