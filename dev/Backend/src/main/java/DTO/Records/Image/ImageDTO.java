package DTO.Records.Image;

import DTO.Records.Model.ModelDefinition.VariableDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @param variablesModule modules which make up the image
 * @param constraintModules modules which make up the image
 * @param preferenceModules modules which make up the image
 * @see VariableModuleDTO
 * @see ConstraintModuleDTO
 * @see PreferenceModuleDTO
 */
public record ImageDTO(
                       @NotNull @Valid VariableModuleDTO variablesModule,
                       @NotNull @Valid Set<ConstraintModuleDTO> constraintModules,
                       @NotNull @Valid Set<PreferenceModuleDTO> preferenceModules
                       )
{}
