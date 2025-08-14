package softuni.bg.supplementsonlinestore.notification.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class NotificationRequest {

    private UUID userId;

    private String subject;

    private String body;

    private LocalDateTime createdAt;
}
