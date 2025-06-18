package groupId.DTO.Records.Model.ModelData;

import jakarta.validation.constraints.NotBlank;

/**
 *
 * @param name The parameter's name in the zimpl code
 * @param structure The parameter's structure
 */
public record ParameterDefinitionDTO(@NotBlank String name,@NotBlank String structure, String alias) {


}
