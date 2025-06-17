package groupId.DTO.Records.Model.ModelDefinition;

import jakarta.validation.constraints.NotBlank;

/**
 * represents a preference parsed from zimpl code
 * @param identifier the preference's identifier
 */
public record PreferenceDTO(
    @NotBlank String identifier
    //@NotNull @Valid DependenciesDTO structure
) {
}