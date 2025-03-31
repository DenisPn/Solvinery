package groupId.DTO.Records.Image;

import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 *
 * @param variablesOfInterest variables which are mutable/visible in the image
 * @param variablesConfigurableSets sets that the variables are composed of, which are mutable/visible in the image
 * @param variablesConfigurableParams parameters that the variables are composed of, which are mutable/visible in the image
 * @param variableAliases alias structure of a variable, a map from variable name to a list of aliases
 */
public record VariableModuleDTO(@Valid @NotNull Set<@NotBlank String> variablesOfInterest,
                                /*@Valid @NotNull Set<@NotBlank String> variablesConfigurableSets,
                                @Valid @NotNull Set<@NotBlank String> variablesConfigurableParams,*/
                                @Valid @NotNull Map<@NotBlank String, @NotBlank String> variableAliases
                                ) {}
