package com.tokiserskyy.computerclub.controller;

import com.tokiserskyy.computerclub.exception.LogNotFoundException;
import com.tokiserskyy.computerclub.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
@Tag(name = "Logs", description = "API for managing logs")
public class LogController {

    private static final String LOG_DIRECTORY = "logs";
    private final LogService logService;

    @Operation(summary = "Download log by date", description = "Allows downloading a log file by specific date (yyyy-MM-dd)")
    @GetMapping("/{date}")
    public ResponseEntity<Resource> downloadLog(
            @Parameter(description = "Log file date in the format yyyy-MM-dd") @PathVariable String date) {
        String filename = String.format("computerclub-%s.log", date);
        Path filePath = Paths.get(LOG_DIRECTORY).resolve(filename);

        try {
            if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
                throw new LogNotFoundException("Log file not found for date: " + date);
            }

            Resource resource = new UrlResource(filePath.toUri());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .body(resource);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Failed to access log file: " + e.getMessage(), e);
        }
    }

    @Operation(summary = "Get generation status", description = "Returns the current status of asynchronous log generation by ID")
    @GetMapping("/status/{id}")
    public ResponseEntity<String> getStatus(
            @Parameter(description = "ID of the log generation request") @PathVariable String id) {
        return ResponseEntity.ok(logService.getStatus(id).name());
    }

    @Operation(summary = "Download generated log", description = "Downloads a merged log file if it has been generated")
    @GetMapping("/file/{id}")
    public ResponseEntity<byte[]> getFile(
            @Parameter(description = "ID of the generated log file") @PathVariable String id) throws IOException {
        byte[] content = logService.getLogFile(id); // выбросит LogNotFoundException, если нужно
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"log_" + id + ".txt\"")
                .body(content);
    }

    @Operation(summary = "Generate logs for a period", description = "Triggers asynchronous generation of logs from a given date range")
    @PostMapping("/generate/period")
    public ResponseEntity<String> generateLogForPeriod(
            @Parameter(description = "Start date in format yyyy-MM-dd") @RequestParam String from,
            @Parameter(description = "End date in format yyyy-MM-dd") @RequestParam String to) {
        try {
            LocalDate start = LocalDate.parse(from);
            LocalDate end = LocalDate.parse(to);

            String id = logService.generateLogForPeriodAsync(start, end);
            return ResponseEntity.ok(id);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd");
        }
    }
}
