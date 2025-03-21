package groupId.DTO.Records.Model.ModelDefinition;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * represents a preference parsed from zimpl code
 * @param identifier the variable's identifier
 * @param dep the variable's dependencies
 * @see DependenciesDTO
 */
public record VariableDTO(
    @NotBlank String identifier,
    @Valid @NotNull DependenciesDTO dep
) {}
