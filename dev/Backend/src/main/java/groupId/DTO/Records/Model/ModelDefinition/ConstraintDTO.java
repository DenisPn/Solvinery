package groupId.DTO.Records.Model.ModelDefinition;

import jakarta.validation.constraints.NotBlank;

/**
 * represents a constraint parsed from zimpl code
 * @param identifier the constraint's identifier
 */
public record ConstraintDTO(
        @NotBlank String identifier
    //    @NotNull @Valid DependenciesDTO structure
) {}
