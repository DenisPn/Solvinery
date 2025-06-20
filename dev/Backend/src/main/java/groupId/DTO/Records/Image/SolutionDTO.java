package groupId.DTO.Records.Image;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 *
 * @param solved True if solved, else otherwise
 * @param solvingTime Solving time, in seconds
 * @param objectiveValue The total values of the objective function (maximize/minimize)
 * @param solution A map from a variables to their solutions, in a DTO.
 * @see SolutionVariable
 */
public record SolutionDTO(boolean solved, @Min(0) double solvingTime,
                          double objectiveValue,
                          @NotNull Map<@NotBlank String, @Valid @NotNull SolutionVariable> solution) {}
