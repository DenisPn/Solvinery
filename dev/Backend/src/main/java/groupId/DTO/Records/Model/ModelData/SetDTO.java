package groupId.DTO.Records.Model.ModelData;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 *
 * @param setDefinition The set's definition: name, structure, etc.
 * @param values The set's actual value (only applicable for non-composition sets)
 */
public record SetDTO(@NotNull @Valid SetDefinitionDTO setDefinition, @Valid List<@NotBlank @Size(max = 255, message = "Set values have be shorter then 255 characters") String> values) { }
