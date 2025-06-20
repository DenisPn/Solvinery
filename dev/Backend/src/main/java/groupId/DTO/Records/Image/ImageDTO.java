package groupId.DTO.Records.Image;

import com.fasterxml.jackson.core.JsonProcessingException;
import groupId.DTO.Records.Model.ModelData.ParameterDTO;
import groupId.DTO.Records.Model.ModelData.SetDTO;
import groupId.DTO.Records.Model.ModelDefinition.VariableDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.Size;
import org.springframework.lang.NonNull;

import java.util.Set;

/**
 *
 * @param variables modules which make are in use.
 * @param constraintModules modules which are in use.
 * @param preferenceModules modules which are in use.
 * @param parameters parameters which are in use.
 * @param sets sets which are in use.
 * @see ConstraintModuleDTO
 * @see PreferenceModuleDTO
 */
public record ImageDTO(
                       @NotNull @Valid @Size(min = 1, message = "An image has to have at least one active variable.") Set<VariableDTO> variables,
                       @NotNull @Valid Set<ConstraintModuleDTO> constraintModules,
                       @NotNull @Valid Set<PreferenceModuleDTO> preferenceModules,
                       @NotNull Set<@NotNull @Valid SetDTO> sets,
                       @NotNull Set<@NotNull @Valid ParameterDTO> parameters,
                       @NotBlank @Size(max = 255, message = "Image name has be shorter then 255 characters") String name,
                       @NotNull @Size(max = 4000, message = "Image description has be shorter then 4000 characters") String description,
                       @NotNull String code
                       )
{
    @NonNull
    @Override
    //for readable logs
    public String toString() {
        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while creating Image string: "+e.getMessage(),e);
        }
    }
}
