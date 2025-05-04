package groupId.DTO.Records.Requests.Responses;

import jakarta.validation.constraints.NotNull;

public record ConfirmationDTO(@NotNull String message) {
}
