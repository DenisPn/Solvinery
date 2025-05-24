package groupId.DTO.Records.Requests.Commands;

import jakarta.validation.constraints.NotBlank;

public record CreateImageFromFileDTO(@NotBlank String userId,@NotBlank String code) {}
