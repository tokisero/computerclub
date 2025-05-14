package com.tokiserskyy.computerclub.controller;

import com.tokiserskyy.computerclub.service.VisitCounterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/visits")
@RequiredArgsConstructor
public class VisitCounterController {

    private final VisitCounterService visitCounterService;

    @GetMapping("/track")
    public String trackVisit() {
        visitCounterService.increment();
        return "Visit counted!";
    }

    @GetMapping("/count")
    public int getCount() {
        return visitCounterService.getCount();
    }

    @PostMapping("/reset")
    public String reset() {
        visitCounterService.reset();
        return "Counter reset!";
    }
}
