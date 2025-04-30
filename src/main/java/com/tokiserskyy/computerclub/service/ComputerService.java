    package com.tokiserskyy.computerclub.service;

    import com.tokiserskyy.computerclub.cache.InMemoryCache;
    import com.tokiserskyy.computerclub.dto.ComputerDto;
    import com.tokiserskyy.computerclub.exception.BadRequestException;
    import com.tokiserskyy.computerclub.exception.NotFoundException;
    import com.tokiserskyy.computerclub.mapper.ComputerMapper;
    import com.tokiserskyy.computerclub.model.Computer;
    import com.tokiserskyy.computerclub.repository.ComputerRepository;
    import jakarta.transaction.Transactional;
    import lombok.RequiredArgsConstructor;
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
        private static final String SEARCH_COMPUTERS_KEY_PREFIX = "search_";
        private static final String COMPUTER_WITH_ID = "Computer with id ";

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

            cache.put(ALL_COMPUTERS_KEY, new ArrayList<>(computers), TTL_MILLIS);
            return computers;
        }

        public ComputerDto getComputerById(int id) {
            Optional<ComputerDto> cached = cache.getById(ALL_COMPUTERS_KEY, id);
            if (cached.isPresent()) {
                return cached.get();
            }

            Computer computer = computerRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(COMPUTER_WITH_ID + id));

            ComputerDto result = ComputerMapper.toDto(computer);

            List<ComputerDto> computers = getAllComputers();
            computers.add(result);
            cache.put(ALL_COMPUTERS_KEY, new ArrayList<>(computers), TTL_MILLIS);

            return result;
        }

        public ComputerDto addComputer(ComputerDto computerDto) {

            Computer computerEntity = new Computer();
            computerEntity.setMonitor(computerDto.getMonitor());
            computerEntity.setCpu(computerDto.getCpu());
            computerEntity.setGpu(computerDto.getGpu());
            computerEntity.setRam(computerDto.getRam());

            ComputerDto result = ComputerMapper.toDtoShallow(computerRepository.save(computerEntity));

            if(cache.get(ALL_COMPUTERS_KEY).isPresent()) {
                List<ComputerDto> computers = getAllComputers();
                computers.add(result);
                cache.put(ALL_COMPUTERS_KEY, new ArrayList<>(computers), TTL_MILLIS);
                invalidateSearchCaches();
            }
            return result;
        }

        @Transactional
        public void deleteComputerById(int id) {
            if (!computerRepository.existsById(id)) {
                throw new BadRequestException(COMPUTER_WITH_ID + id + " does not exist");
            }

            computerRepository.deleteById(id);

            List<ComputerDto> computers = getAllComputers();
            computers.removeIf(dto -> dto.getId() == id);
            cache.put(ALL_COMPUTERS_KEY, new ArrayList<>(computers), TTL_MILLIS);

            invalidateSearchCaches();
        }

        public ComputerDto updateComputer(int id, ComputerDto computerDetails) {
            Computer computer = computerRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(COMPUTER_WITH_ID + id));

            computer.setCpu(computerDetails.getCpu());
            computer.setRam(computerDetails.getRam());
            computer.setGpu(computerDetails.getGpu());
            computer.setMonitor(computerDetails.getMonitor());

            ComputerDto result = ComputerMapper.toDto(computerRepository.save(computer));

            List<ComputerDto> computers = getAllComputers();
            computers.removeIf(dto -> dto.getId() == id);
            computers.add(result);
            cache.put(ALL_COMPUTERS_KEY, new ArrayList<>(computers), TTL_MILLIS);

            invalidateSearchCaches();
            return result;
        }

        @Transactional
        public List<ComputerDto> addComputersBulk(List<ComputerDto> computerDtos) {
            if (computerDtos == null || computerDtos.isEmpty()) {
                throw new BadRequestException("Computer list must not be empty");
            }

            List<Computer> entities = computerDtos.stream().map(dto -> {
                Computer computer = new Computer();
                computer.setCpu(dto.getCpu());
                computer.setRam(dto.getRam());
                computer.setGpu(dto.getGpu());
                computer.setMonitor(dto.getMonitor());
                return computer;
            }).toList();

            List<ComputerDto> savedDtos = computerRepository.saveAll(entities)
                    .stream()
                    .map(ComputerMapper::toDtoShallow)
                    .toList();
            if(cache.get(ALL_COMPUTERS_KEY).isPresent()) {
                List<ComputerDto> currentCache = getAllComputers();
                currentCache.addAll(savedDtos);
                cache.put(ALL_COMPUTERS_KEY, new ArrayList<>(currentCache), TTL_MILLIS);
                invalidateSearchCaches();
            }
            return savedDtos;
        }


        @SuppressWarnings("unchecked")
        public List<ComputerDto> searchComputers(String cpu, String ram, String gpu, String monitor) {
            String cacheKey = SEARCH_COMPUTERS_KEY_PREFIX + (cpu != null ? cpu : "null") + "_" +
                    (ram != null ? ram : "null") + "_" +
                    (gpu != null ? gpu : "null") + "_" +
                    (monitor != null ? monitor : "null");

            Optional<Object> cachedSearch = cache.get(cacheKey);
            if (cachedSearch.isPresent()) {
                return (List<ComputerDto>) cachedSearch.get();
            }

            List<ComputerDto> allComputers = getAllComputers();

            List<ComputerDto> filteredComputers = allComputers.stream()
                    .filter(computer -> cpu == null || (computer.getCpu() != null && cpu.equals(computer.getCpu())))
                    .filter(computer -> ram == null || Integer.parseInt(ram) == computer.getRam())
                    .filter(computer -> gpu == null || (computer.getGpu() != null && gpu.equals(computer.getGpu())))
                    .filter(computer -> monitor == null || Integer.parseInt(monitor) == computer.getMonitor())
                    .toList();

            cache.put(cacheKey, new ArrayList<>(filteredComputers), TTL_MILLIS);
            return filteredComputers;
        }

        private void invalidateSearchCaches() {
            cache.getCache().keySet().stream()
                    .filter(key -> key.startsWith(SEARCH_COMPUTERS_KEY_PREFIX))
                    .forEach(cache::remove);
        }

        public Computer getComputerEntityById(int computerId) {
            return computerRepository.findById(computerId)
                    .orElseThrow(() -> new NotFoundException(COMPUTER_WITH_ID + computerId));
        }
    }
