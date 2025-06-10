package groupId.DTO.Records.Image;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Set;

/**
 * Holds data about the data of a variables, and a DTO of the variable's values
 * @param setStructure a list of set names of whom the variable's solutions are composed of, in order. (sets may be aliased)
 * @param typeStructure a list of types which make up the solution, in order
 * @param solutions a set of the variable's values.
 * @see SolutionValueDTO
 */
public record SolutionVariable(//@Valid @NotNull List<@NotNull String> setStructure,
                               @Valid @NotNull List<@NotNull String> typeStructure,
                               @Valid @NotNull Set<@Valid @NotNull SolutionValueDTO> solutions) {
}
