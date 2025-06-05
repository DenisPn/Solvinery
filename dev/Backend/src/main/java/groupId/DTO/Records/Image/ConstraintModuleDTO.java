package groupId.DTO.Records.Image;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;


/**
 *
 * @param moduleName the module's name
 * @param description the module's description
 * @param constraints a list of constraint names which are in the module
 */
public record ConstraintModuleDTO(@NotBlank String moduleName, @NotNull String description, @Valid Set<@NotBlank String> constraints) {}
