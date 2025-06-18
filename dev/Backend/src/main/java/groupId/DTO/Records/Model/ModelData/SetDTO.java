package groupId.DTO.Records.Model.ModelData;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 *
 * @param setDefinition The set's definition: name, structure, etc.
 * @param values The set's actual value (only applicable for non-composition sets)
 */
public record SetDTO(@NotNull @Valid SetDefinitionDTO setDefinition,@NotNull List<String> values) { }
