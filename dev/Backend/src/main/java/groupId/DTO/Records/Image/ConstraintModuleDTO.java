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
public record ConstraintModuleDTO(@NotBlank @Size(max = 255, message = "Constraint module name has be shorter then 255 characters") String moduleName,
                                  @NotNull @Size(max = 4000, message = "Constraint module has be shorter then 4000 characters") String description,
                                  @NotNull @Valid @Size(min = 1,message = "Constraint module has to have at least one constraint") Set<@NotBlank String> constraints,
                                  Boolean active) {}
