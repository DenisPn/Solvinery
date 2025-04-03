package groupId.DTO.Records.Requests.Commands;

import jakarta.validation.constraints.NotBlank;

public record LogInDTO(@NotBlank String userName, @NotBlank String password) {
}
