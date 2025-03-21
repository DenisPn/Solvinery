package groupId.DTO.Records.Model.ModelData;

/**
 *
 * @param parameterDefinition The parameter's definition (name, type etc.)
 * @param value The parameter's actual value
 * @see ParameterDefinitionDTO
 */
public record ParameterDTO(ParameterDefinitionDTO parameterDefinition, String value) {
}
