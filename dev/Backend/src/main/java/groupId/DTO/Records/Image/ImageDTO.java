package groupId.DTO.Records.Image;

import Image.Modules.Single.SetModule;
import groupId.DTO.Records.Model.ModelData.ParameterDTO;
import groupId.DTO.Records.Model.ModelData.SetDTO;
import groupId.DTO.Records.Model.ModelDefinition.VariableDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

/**
 *
 * @param variables modules which make up the image
 * @param constraintModules modules which make up the image
 * @param preferenceModules modules which make up the image
 * @see VariableModuleDTO
 * @see ConstraintModuleDTO
 * @see PreferenceModuleDTO
 */
public record ImageDTO(
                       @NotNull @Valid Set<VariableDTO> variables,
                       @NotNull @Valid Set<ConstraintModuleDTO> constraintModules,
                       @NotNull @Valid Set<PreferenceModuleDTO> preferenceModules,
                       @NotNull Set<@NotNull @Valid SetDTO> sets,
                       @NotNull Set<@NotNull @Valid ParameterDTO> parameters
                       )
{}
