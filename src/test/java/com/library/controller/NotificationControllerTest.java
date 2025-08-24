package com.library.controller;

import com.library.service.ScheduledNotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * NotificationController Test
 * Tests REST API endpoints for notification management
 * 
 * @author Library System
 * @version 1.0.0
 */
@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScheduledNotificationService scheduledNotificationService;

    @Test
    @WithMockUser(username = "librarian@library.com", roles = "LIBRARIAN")
    void testTriggerOverdueCheck_Success() throws Exception {
        // Arrange
        doNothing().when(scheduledNotificationService).checkOverdueNotifications();

        // Act & Assert
        mockMvc.perform(post("/notifications/trigger-overdue-check"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Overdue check triggered successfully"));

        verify(scheduledNotificationService).checkOverdueNotifications();
    }

    @Test
    @WithMockUser(username = "member@example.com", roles = "MEMBER")
    void testTriggerOverdueCheck_AccessDenied() throws Exception {
        // Act & Assert - MEMBER role cannot access this endpoint
        mockMvc.perform(post("/notifications/trigger-overdue-check"))
                .andExpect(status().isForbidden());

        verify(scheduledNotificationService, never()).checkOverdueNotifications();
    }
}
