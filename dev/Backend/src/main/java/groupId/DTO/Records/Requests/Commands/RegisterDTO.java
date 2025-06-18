package groupId.DTO.Records.Requests.Commands;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterDTO(
        @NotBlank String userName,
        String nickname,
        @NotBlank String password,
        @NotBlank @Email String email) {}
