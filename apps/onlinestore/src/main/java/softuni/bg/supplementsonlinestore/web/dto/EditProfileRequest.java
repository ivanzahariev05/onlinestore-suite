package softuni.bg.supplementsonlinestore.web.dto;


import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditProfileRequest {

    @Size(min = 2, max = 20, message = "First name length must be between 2 and 20 characters!")
    private String firstName;

    @Size(min = 2, max = 20, message = "Last name length must be between 2 and 20 characters!")
    private String lastName;

    @URL(message = "Enter valid link!")
    private String imageUrl;
}
