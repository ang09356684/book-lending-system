package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.service.ScheduledNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Notification controller for manual testing of scheduled tasks
 * 
 * @author Library System
 * @version 1.0.0
 */
@Tag(name = "Notifications", description = "Notification management and testing endpoints")
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    
    private final ScheduledNotificationService scheduledNotificationService;
    
    public NotificationController(ScheduledNotificationService scheduledNotificationService) {
        this.scheduledNotificationService = scheduledNotificationService;
    }
    
    /**
     * Manually trigger overdue notification check
     * This endpoint is for testing purposes only
     */
    @Operation(
        summary = "Manually trigger overdue notification check",
        description = "Manually execute overdue notification check for testing scheduled tasks"
    )
    @PostMapping("/trigger-overdue-check")
    public ResponseEntity<ApiResponse<String>> triggerOverdueCheck() {
        try {
            // Manually trigger the scheduled task
            scheduledNotificationService.checkOverdueNotifications();
            
            return ResponseEntity.ok(ApiResponse.success("Overdue notification check triggered successfully"));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_SERVER_ERROR", "Failed to trigger overdue notification check: " + e.getMessage()));
        }
    }
}
