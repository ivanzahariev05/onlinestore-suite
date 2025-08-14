package softuni.bg.supplementsonlinestore.notification;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import softuni.bg.supplementsonlinestore.notification.dto.NotificationPreference;
import softuni.bg.supplementsonlinestore.notification.dto.NotificationRequest;
import softuni.bg.supplementsonlinestore.notification.dto.UpsertNotificationPreference;

import java.util.UUID;

@FeignClient(name = "notification-svc", url = "localhost:8081/api/v1/notifications")
public interface NotificationClient {

    @PostMapping("/preferences")
    ResponseEntity<Void> upsertNotificationPreferences(@RequestBody UpsertNotificationPreference upsertNotificationPreference);

    @GetMapping("/preferences")
    ResponseEntity<NotificationPreference> getNotificationPreferences(@RequestParam(name = "userId") UUID id);

    @PostMapping("/toggle")
    ResponseEntity<Void> toggleNotification(@RequestParam(name = "userId") UUID id);

    @PostMapping("/welcome")
    ResponseEntity<Void> sendWelcomeEmail(@RequestParam(name = "userId") UUID id);

    @PostMapping
    ResponseEntity<Void> sendNotification(@RequestParam(name = "userId") UUID id);


}

