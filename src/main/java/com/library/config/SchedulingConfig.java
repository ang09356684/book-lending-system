package com.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Scheduling configuration for cron jobs
 * 
 * @author Library System
 * @version 1.0.0
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
    
    /**
     * Configure thread pool task scheduler for scheduled tasks
     * 
     * @return ThreadPoolTaskScheduler with custom configuration
     */
    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        
        // Set pool size for scheduled tasks
        scheduler.setPoolSize(5);
        
        // Set thread name prefix for easy identification
        scheduler.setThreadNamePrefix("scheduled-task-");
        
        // Set wait for tasks to complete on shutdown
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        
        // Set await termination period (in seconds)
        scheduler.setAwaitTerminationSeconds(30);
        
        // Initialize the scheduler
        scheduler.initialize();
        
        return scheduler;
    }
}
