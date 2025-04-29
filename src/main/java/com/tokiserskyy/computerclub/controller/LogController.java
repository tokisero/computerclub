package com.tokiserskyy.computerclub.controller;

import com.tokiserskyy.computerclub.exception.ErrorResponse;
import com.tokiserskyy.computerclub.exception.LogNotFoundException;
import com.tokiserskyy.computerclub.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
@Tag(name = "Logs", description = "API for managing logs")
public class LogController {

    private static final String LOG_DIRECTORY = "logs";
    private final LogService logService;

    @Operation(summary = "Download log by date", description = "Allows downloading a log file by specific date (yyyy-MM-dd)")
    @GetMapping("/{date}")
    public ResponseEntity<?> downloadLog(
            @Parameter(description = "Log file date in the format yyyy-MM-dd") @PathVariable String date) {
        String filename = String.format("computerclub-%s.log", date);
        Path filePath = Paths.get(LOG_DIRECTORY).resolve(filename);

        try {
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                ErrorResponse errorResponse = new ErrorResponse(
                        HttpStatus.NOT_FOUND.value(),
                        "Log file not found for date: " + date
                );
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .body(resource);
        } catch (MalformedURLException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Failed to access log file: " + e.getMessage()
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
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
            @Parameter(description = "ID of the generated log file") @PathVariable String id) {
        try {
            byte[] content = logService.getLogFile(id);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"log_" + id + ".txt\"")
                    .body(content);
        } catch (LogNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
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
            return ResponseEntity.badRequest().body("Invalid date format. Use yyyy-MM-dd");
        }
    }
}
