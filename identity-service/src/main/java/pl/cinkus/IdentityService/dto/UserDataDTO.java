package pl.cinkus.IdentityService.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserDataDTO {
    @NotBlank
    @Size(min = 3)
    private String name;

    @NotBlank
    @Size(min = 3)
    private String surname;

    @NotBlank
    private String nickName;

    @Email
    private String email;

    @NotBlank
    @Size(min = 8, message = "Password must contains at least 8 characters")
    private String password;
}
