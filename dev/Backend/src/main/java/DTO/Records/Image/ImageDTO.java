package DTO.Records.Image;

import DTO.Records.Model.ModelDefinition.VariableDTO;

import java.util.List;
import java.util.Map;


public record ImageDTO(String imageId,
                       VariableModuleDTO variablesModule,
                       List<ConstraintModuleDTO> constraintModules,
                       List<PreferenceModuleDTO> preferenceModules
                       )
{}
