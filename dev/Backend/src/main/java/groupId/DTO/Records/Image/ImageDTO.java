package groupId.DTO.Records.Image;

import com.fasterxml.jackson.core.JsonProcessingException;
import groupId.DTO.Records.Model.ModelData.ParameterDTO;
import groupId.DTO.Records.Model.ModelData.SetDTO;
import groupId.DTO.Records.Model.ModelDefinition.VariableDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.databind.ObjectMapper;
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
                       @NotNull @Valid Set<VariableDTO> variables,
                       @NotNull @Valid Set<ConstraintModuleDTO> constraintModules,
                       @NotNull @Valid Set<PreferenceModuleDTO> preferenceModules,
                       @NotNull Set<@NotNull @Valid SetDTO> sets,
                       @NotNull Set<@NotNull @Valid ParameterDTO> parameters,
                       @NotBlank String name,
                       @NotNull String description,
                       @NotNull String code
                       )
{
    @NonNull
    @Override
    public String toString() {
        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while creating Image string: "+e.getMessage(),e);
        }
    }
}
