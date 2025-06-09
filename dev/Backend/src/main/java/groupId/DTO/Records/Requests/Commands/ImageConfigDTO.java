package groupId.DTO.Records.Requests.Commands;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.Set;


public record ImageConfigDTO(
        @Valid @NotNull Map<@NotBlank String, @Min(0) @Max(1) @NotNull Float> preferenceModulesScalars,
        @Valid @NotNull Set<@NotBlank String> enabledConstraintModules,
        @Min(0) int timeout) { }
