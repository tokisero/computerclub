package com.tokiserskyy.computerclub.service;

import com.tokiserskyy.computerclub.repository.VisitCounterRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VisitCounterService {
    private final VisitCounterRepository visitCounterRepository;

    public VisitCounterService(VisitCounterRepository visitCounterRepository) {
        this.visitCounterRepository = visitCounterRepository;
    }

    @PostConstruct
    public void init() {
        log.debug("Initializing Visit Cache");
        visitCounterRepository.deleteAll();
    }

    public void incrementVisit(String url) {
        visitCounterRepository.incrementVisit(url);
    }

    public long getVisitCount(String url) {
        return visitCounterRepository.findByUrl(url).orElse(0L);
    }
}
