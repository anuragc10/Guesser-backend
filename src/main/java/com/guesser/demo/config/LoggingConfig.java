package com.guesser.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import java.io.File;

@Configuration
public class LoggingConfig {
    private static final Logger logger = LoggerFactory.getLogger(LoggingConfig.class);

    @PostConstruct
    public void init() {

        // Create logs directory if it doesn't exist
        File logDir = new File("logs");
        if (!logDir.exists()) {
            boolean created = logDir.mkdirs();
            if (created) {
                logger.info("Created logs directory at: {}", logDir.getAbsolutePath());
            } else {
                logger.warn("Failed to create logs directory at: {}", logDir.getAbsolutePath());
            }
        }

        // Add shutdown hook to ensure logs are flushed
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Application shutting down, flushing logs...");
        }));
    }
} 