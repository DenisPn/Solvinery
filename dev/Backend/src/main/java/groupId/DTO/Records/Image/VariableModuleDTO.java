package groupId.DTO.Records.Image;

import java.util.List;
import java.util.Map;
import java.util.Set;
import groupId.DTO.Records.Model.ModelDefinition.VariableDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Deprecated, replaced with a set of {@link VariableDTO}
 * @param variablesOfInterest variables which are mutable/visible in the image
 * @param variableAliases alias structure of a variable, a map from variable name to a list of aliases
 */
@Deprecated
public record VariableModuleDTO(@Valid @NotNull Set<@NotBlank String> variablesOfInterest,
                                @Valid @NotNull Map<@NotBlank String, @NotBlank String> variableAliases
                                ) {}
