package com.tokiserskyy.computerclub.service;

import com.tokiserskyy.computerclub.exception.LogServiceInitializationException;

import java.io.FileNotFoundException;
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
                Thread.sleep(20_000);
                StringBuilder collectedLogs = new StringBuilder();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                LocalDate current = startDate;
                while (!current.isAfter(endDate)) {
                    String fileName = "computerclub-" + current.format(formatter) + ".log";
                    Path filePath = logsDirectory.resolve(fileName);

                    if (Files.exists(filePath)) {
                        collectedLogs.append("===== ").append(fileName).append(" =====\n");
                        collectedLogs.append(Files.readString(filePath)).append("\n");
                    }

                    current = current.plusDays(1);
                }

                if (collectedLogs.isEmpty()) {
                    logStatusMap.put(id, LogStatus.FAILED);
                    return;
                }

                Path mergedLog = logsDirectory.resolve("log_period_" + id + ".log");
                Files.writeString(mergedLog, collectedLogs.toString());
                logFiles.put(id, mergedLog);
                logStatusMap.put(id, LogStatus.READY);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                logStatusMap.put(id, LogStatus.FAILED);
                log.error("Log generation was interrupted", ie);
            } catch (IOException e) {
                logStatusMap.put(id, LogStatus.FAILED);
                log.error("Error collecting logs for the period", e);
            }
        });

        return id;
    }

    public LogStatus getStatus(String id) {
        return logStatusMap.getOrDefault(id, LogStatus.NOT_FOUND);
    }

    public byte[] getLogFile(String id) throws IOException {
        if (logStatusMap.get(id) == LogStatus.READY) {
            return Files.readAllBytes(logFiles.get(id));
        }
        throw new FileNotFoundException("File is not ready or does not exist");
    }

    public enum LogStatus {
        IN_PROGRESS, READY, FAILED, NOT_FOUND
    }
}
