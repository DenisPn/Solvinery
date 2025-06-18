package groupId.DTO.Records.Requests.Commands;
import groupId.DTO.Records.Model.ModelData.InputDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SolveCommandDTO(
        @NotBlank String imageId,
        @NotNull @Valid InputDTO input,
        @Min(0) @NotNull int timeout) {}
