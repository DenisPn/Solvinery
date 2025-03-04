package DTO.Records.Image;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Set;


/**
 *
 * @param moduleName the module's name
 * @param description the module's description
 * @param constraints a list of constraint names which are in the module
 * @param inputSets a list of sets which the constraints in the module depend on
 * @param inputParams a list of parameters which the constraints in the module depend on
 */
public record ConstraintModuleDTO(@NotBlank String moduleName, @NotNull String description, @Valid Set<@NotBlank String> constraints,
                                  @Valid Set<@NotBlank String> inputSets,
                                  @Valid Set<@NotBlank String> inputParams) {}
