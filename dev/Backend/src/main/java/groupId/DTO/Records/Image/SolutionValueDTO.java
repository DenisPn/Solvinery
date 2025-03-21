package groupId.DTO.Records.Image;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * A single solution for a variable.
 * @param values the value of the solution
 * @param objectiveValue the objective (numeric) value of a solution
 */
public record SolutionValueDTO(@NotNull @Valid List<@NotNull String> values, @NotNull int objectiveValue) {
}
