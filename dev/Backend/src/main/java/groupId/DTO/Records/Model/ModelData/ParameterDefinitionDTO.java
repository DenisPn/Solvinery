package groupId.DTO.Records.Model.ModelData;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 *
 * @param name The parameter's name in the zimpl code
 * @param structure The parameter's structure
 */
public record ParameterDefinitionDTO( @Size(max = 255, message = "Parameter name has be shorter then 255 characters") @NotBlank String name,
                                     @Size(max = 255, message = "Parameter structure has be shorter then 255 characters") @NotBlank String structure,
                                     @Size(max = 255, message = "Parameter alias has be shorter then 255 characters") String alias) {


}
