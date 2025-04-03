package groupId.DTO.Records.Model.ModelDefinition;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * represents a constraint parsed from zimpl code
 * @param identifier the constraint's identifier
 * @param dep the constraint's dependencies
 * @see DependenciesDTO
 */
public record ConstraintDTO(
        @NotBlank String identifier
    //    @NotNull @Valid DependenciesDTO structure
) {}
