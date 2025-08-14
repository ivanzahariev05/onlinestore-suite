package softuni.bg.supplementsonlinestore.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import softuni.bg.supplementsonlinestore.notification.NotificationClient;
import softuni.bg.supplementsonlinestore.notification.dto.NotificationPreference;
import softuni.bg.supplementsonlinestore.notification.dto.UpsertNotificationPreference;

import java.util.UUID;

@Slf4j
@Service
public class NotificationService  {

    private final NotificationClient notificationClient;

    public NotificationService(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    public void saveNotificationPreference(UUID userId, boolean enabled, String email){
        UpsertNotificationPreference notificationPreference = UpsertNotificationPreference.builder()
                .userId(userId).enabled(enabled)
                .email(email)
                .enabled(true)
                .build();

        try{
            ResponseEntity<Void> httpResponse = notificationClient.upsertNotificationPreferences(notificationPreference);
            if (!httpResponse.getStatusCode().is2xxSuccessful()) {
                log.error("Failed to upsert notification preference");


            }
        }catch (Exception e){
            log.error("Unable to call notification-svc");
        }
    }

    public NotificationPreference getNotificationPreferences(UUID userId) {
        ResponseEntity<NotificationPreference> notificationPreferences = notificationClient.getNotificationPreferences(userId);
        if (!notificationPreferences.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Notification preferences not found");
        }
        return notificationPreferences.getBody();
    }

    public void sendWelcomeEmail(UUID userId) {
        try {
            ResponseEntity<Void> response = notificationClient.sendWelcomeEmail(userId);
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Failed to send welcome email for user");
            }
        } catch (Exception e) {
            log.error("Error sending welcome email");
        }
    }

    public void toggleNotification(UUID id) {
       notificationClient.toggleNotification(id);
    }
}

