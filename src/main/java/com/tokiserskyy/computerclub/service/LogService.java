package com.tokiserskyy.computerclub.service;

import com.tokiserskyy.computerclub.exception.LogNotFoundException;
import com.tokiserskyy.computerclub.exception.LogServiceInitializationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LogService {

    private final Map<String, LogStatus> logStatusMap = new ConcurrentHashMap<>();
    private final Map<String, Path> logFiles = new ConcurrentHashMap<>();
    private final Executor executor = Executors.newCachedThreadPool();
    private final Path logsDirectory = Paths.get("logs");

    public LogService() {
        try {
            Files.createDirectories(logsDirectory);
        } catch (IOException e) {
            log.error("Failed to create the logs directory", e);
            throw new LogServiceInitializationException("Error initializing LogService", e);
        }
    }

    public String generateLogForPeriodAsync(LocalDate startDate, LocalDate endDate) {
        String id = UUID.randomUUID().toString();
        logStatusMap.put(id, LogStatus.IN_PROGRESS);

        executor.execute(() -> {
            try {
                Thread.sleep(20_000); // Имитируем длительную генерацию логов

                StringBuilder collectedLogs = new StringBuilder();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                LocalDate current = startDate;
                while (!current.isAfter(endDate)) {
                    String fileName = "computerclub-" + current.format(formatter) + ".log";
                    Path filePath = logsDirectory.resolve(fileName);

                    if (!Files.exists(filePath)) {
                        log.error("Log file not found: {}", filePath);
                        logStatusMap.put(id, LogStatus.FAILED);
                        return;
                    }

                    try {
                        collectedLogs.append("===== ").append(fileName).append(" =====\n");
                        collectedLogs.append(Files.readString(filePath)).append("\n");
                    } catch (IOException e) {
                        log.error("Failed to read log file: {}", filePath, e);
                        logStatusMap.put(id, LogStatus.FAILED);
                        return;
                    }

                    current = current.plusDays(1);
                }

                if (collectedLogs.isEmpty()) {
                    log.warn("No logs found in the specified date range: {} to {}", startDate, endDate);
                    logStatusMap.put(id, LogStatus.FAILED);
                    return;
                }

                Path mergedLog = logsDirectory.resolve("log_period_" + id + ".log");
                Files.writeString(mergedLog, collectedLogs.toString());

                logFiles.put(id, mergedLog);
                logStatusMap.put(id, LogStatus.READY);
                log.info("Log successfully generated for ID: {}", id);

            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                logStatusMap.put(id, LogStatus.FAILED);
                log.error("Log generation interrupted for ID: {}", id, ie);

            } catch (IOException e) {
                logStatusMap.put(id, LogStatus.FAILED);
                log.error("I/O error during log generation for ID: {}", id, e);

            } catch (Exception e) {
                logStatusMap.put(id, LogStatus.FAILED);
                log.error("Unexpected error during log generation for ID: {}", id, e);
            }
        });

        return id;
    }


    public LogStatus getStatus(String id) {
        return logStatusMap.getOrDefault(id, LogStatus.NOT_FOUND);
    }

    public byte[] getLogFile(String id) throws IOException {
        LogStatus status = logStatusMap.getOrDefault(id, LogStatus.NOT_FOUND);

        switch (status) {
            case IN_PROGRESS:
                throw new LogNotFoundException("Log with ID " + id + " is still being generated");

            case FAILED:
                throw new LogNotFoundException("Log generation for ID " + id + " has failed");

            case READY:
                Path filePath = logFiles.get(id);
                if (filePath != null && Files.exists(filePath)) {
                    return Files.readAllBytes(filePath);
                } else {
                    throw new LogNotFoundException("Log file for ID " + id + " is missing");
                }

            case NOT_FOUND:
            default:
                throw new LogNotFoundException("Log with ID " + id + " not found");
        }
    }


    public enum LogStatus {
        IN_PROGRESS, READY, FAILED, NOT_FOUND
    }
}
