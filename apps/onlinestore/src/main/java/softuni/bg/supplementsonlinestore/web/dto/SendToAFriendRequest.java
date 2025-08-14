package softuni.bg.supplementsonlinestore.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendToAFriendRequest {


    @NotNull
    private String toUser;

    @NotNull
    @Positive
    private BigDecimal amount;
}
