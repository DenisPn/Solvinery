package DTO.Records.Model.ModelDefinition;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
/**
 * represents a preference parsed from zimpl code
 * @param identifier the preference's identifier
 * @param dep the preference's dependencies
 * @see DependenciesDTO
 */
public record PreferenceDTO(
    @NotBlank String identifier,
    @NotNull @Valid DependenciesDTO dep
) {}