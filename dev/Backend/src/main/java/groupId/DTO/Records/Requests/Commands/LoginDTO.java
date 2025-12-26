package groupId.DTO.Records.Requests.Commands;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
@Schema(description = "Login request data")
public record LoginDTO(
        @Schema(
                description = "User name"
        )
        @NotBlank
        String userName,
        @NotBlank
        String password) {
}
