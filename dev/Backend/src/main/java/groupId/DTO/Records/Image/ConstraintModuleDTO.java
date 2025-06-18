package groupId.DTO.Records.Image;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;


/**
 *
 * @param moduleName the module's name
 * @param description the module's description
 * @param constraints a list of constraint names which are in the module
 */
public record ConstraintModuleDTO(@NotBlank String moduleName,
                                  @NotNull String description,
                                  @Valid @Size(min = 1,message = "Constraint module has to have at least one constraint") Set<@NotBlank String> constraints,
                                  Boolean active) {}
