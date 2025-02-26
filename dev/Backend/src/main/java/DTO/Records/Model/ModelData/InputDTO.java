package DTO.Records.Model.ModelData;

import DTO.Records.Image.SolutionDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A DTO of a solve command, with all its data
 * @param setsToValues a map from set names to their data
 * @param paramsToValues a map from parameter names to their data
 * @param constraintsToggledOff constraint modules which are toggled off
 * @param preferencesToggledOff preference modules which are toggled off
 * @see SolutionDTO
 */
public record InputDTO(
        @Valid Map<String, List<List<@NotBlank String>>> setsToValues,
        @Valid Map<String, List<@NotBlank String>> paramsToValues,
        @Valid List<@NotBlank String> constraintsToggledOff,
        @Valid List<@NotBlank String> preferencesToggledOff
    ) {}