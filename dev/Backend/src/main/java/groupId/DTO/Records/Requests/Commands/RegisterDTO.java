package groupId.DTO.Records.Requests.Commands;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterDTO(@NotBlank String userName, @NotBlank String password, @Email String email) {}
