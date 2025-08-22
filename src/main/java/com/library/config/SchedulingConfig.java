package com.library.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Scheduling Configuration - Enables scheduled tasks
 *
 * @author Library System
 * @version 1.0.0
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
    // This configuration enables @Scheduled annotation support
}
