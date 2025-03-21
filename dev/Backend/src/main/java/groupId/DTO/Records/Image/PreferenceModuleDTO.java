package groupId.DTO.Records.Image;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;


/**
 *
 * @param moduleName the module's name
 * @param description the module's description
 * @param preferences a list of preference names which are in the module
 * @param inputSets a list of sets which the preferences in the module depend on
 * @param inputParams a list of parameters which the preferences in the module depend on
 */
public record PreferenceModuleDTO(@NotBlank String moduleName, @NotNull String description, @Valid Set<@NotBlank String> preferences,
                                  @Valid Set<@NotBlank String> inputSets,
                                  @Valid Set<@NotBlank String> inputParams) {}
