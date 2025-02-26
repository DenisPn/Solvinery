package DTO.Records.Image;

import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 *
 * @param solved True if solved, else otherwise
 * @param solvingTime Solving time, in seconds
 * @param objectiveValue The total values of the objective function (maximize/minimize)
 * @param errorMsg Error message, in case the problem could not be solved
 * @param solution A map from a variables to their solutions, in a DTO.
 * @see SolutionVariable
 */
public record SolutionDTO(@NotNull boolean solved, @Min(0) double solvingTime,
                          @NotNull double objectiveValue, @NotNull String errorMsg,
                          @NotNull Map<@NotBlank String, @Valid @NotNull SolutionVariable> solution) {}
