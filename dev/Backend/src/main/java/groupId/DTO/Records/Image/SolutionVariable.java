package groupId.DTO.Records.Image;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Set;

/**
 * Holds data about the data of a variable, and a DTO of the variable's values
 * @param typeStructure a list of types which make up the solution, in order
 * @param objectiveValueAlias an alias for the objective value.
 * @param solutions a set of the variable's values.
 * @see SolutionValueDTO
 */
public record SolutionVariable(//@Valid @NotNull List<@NotNull String> setStructure,
                               @Valid @NotNull List<@NotNull String> typeStructure,
                               @Valid @NotBlank String objectiveValueAlias,
                               @Valid @NotNull Set<@Valid @NotNull SolutionValueDTO> solutions) {
}
