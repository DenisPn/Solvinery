package groupId.DTO.Records.Requests.Commands;

import jakarta.validation.constraints.NotBlank;

public record LoginDTO(@NotBlank String userName, @NotBlank String password) {
}
