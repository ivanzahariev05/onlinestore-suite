package softuni.bg.supplementsonlinestore.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import softuni.bg.supplementsonlinestore.notification.NotificationClient;
import softuni.bg.supplementsonlinestore.notification.dto.NotificationPreference;
import softuni.bg.supplementsonlinestore.notification.dto.UpsertNotificationPreference;
import softuni.bg.supplementsonlinestore.notification.service.NotificationService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private NotificationService notificationService;

    private UUID userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
    }

    @Test
    void saveNotificationPreference_ShouldCallNotificationClient() {
        UpsertNotificationPreference preference = UpsertNotificationPreference.builder()
                .userId(userId).enabled(true)
                .email("test@example.com").build();

        when(notificationClient.upsertNotificationPreferences(preference))
                .thenReturn(ResponseEntity.ok().build());

        // Act
        notificationService.saveNotificationPreference(userId, true, "test@example.com");

        // Assert
        verify(notificationClient, times(1)).upsertNotificationPreferences(any(UpsertNotificationPreference.class));
    }

    @Test
    void saveNotificationPreference_ShouldHandleFailure() {
        when(notificationClient.upsertNotificationPreferences(any()))
                .thenReturn(ResponseEntity.badRequest().build());


        assertDoesNotThrow(() -> notificationService.saveNotificationPreference(userId, true, "test@example.com"));

        verify(notificationClient, times(1)).upsertNotificationPreferences(any(UpsertNotificationPreference.class));
    }

    @Test
    void getNotificationPreferences_ShouldReturnPreference_WhenSuccessful() {
        NotificationPreference preference = new NotificationPreference(userId, true);

        when(notificationClient.getNotificationPreferences(userId))
                .thenReturn(ResponseEntity.ok(preference));

        NotificationPreference result = notificationService.getNotificationPreferences(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertTrue(result.isEnabled());
        verify(notificationClient, times(1)).getNotificationPreferences(userId);
    }

    @Test
    void getNotificationPreferences_ShouldThrowException_WhenNotFound() {
        when(notificationClient.getNotificationPreferences(userId))
                .thenReturn(ResponseEntity.notFound().build());

        assertThrows(RuntimeException.class, () -> notificationService.getNotificationPreferences(userId));

        verify(notificationClient, times(1)).getNotificationPreferences(userId);
    }

    @Test
    void sendWelcomeEmail_ShouldCallNotificationClient() {
        when(notificationClient.sendWelcomeEmail(userId)).thenReturn(ResponseEntity.ok().build());

        // Act
        notificationService.sendWelcomeEmail(userId);

        // Assert
        verify(notificationClient, times(1)).sendWelcomeEmail(userId);
    }

    @Test
    void sendWelcomeEmail_ShouldHandleFailure() {
        when(notificationClient.sendWelcomeEmail(userId)).thenReturn(ResponseEntity.badRequest().build());

        // Act & Assert (No exception should be thrown)
        assertDoesNotThrow(() -> notificationService.sendWelcomeEmail(userId));

        verify(notificationClient, times(1)).sendWelcomeEmail(userId);
    }


}
