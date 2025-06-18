package groupId.DTO.Records.Model.ModelData;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 *
 * @param parameterDefinition The parameter's definition (name, structure etc.)
 * @param value The parameter's actual value
 * @see ParameterDefinitionDTO
 */
public record ParameterDTO(@NotNull @Valid ParameterDefinitionDTO parameterDefinition, String value) {
}
