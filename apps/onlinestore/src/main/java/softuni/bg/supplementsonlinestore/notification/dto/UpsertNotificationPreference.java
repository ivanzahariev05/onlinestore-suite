package softuni.bg.supplementsonlinestore.notification.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UpsertNotificationPreference {

    @NotNull
    private UUID userId;

    private boolean enabled;

    @NotNull
    private String email;

}
