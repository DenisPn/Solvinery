package DTO.Records.Image;

import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VariableModuleDTO(@Valid @NotNull Set<@NotBlank String> variablesOfInterest,
                                @Valid @NotNull Set<@NotBlank String> variablesConfigurableSets,
                                @Valid @NotNull Set<@NotBlank String> variablesConfigurableParams,
                                @Valid @NotNull Map<@NotBlank String, @Valid @NotNull List<@NotBlank String>> variableAliases
                                ) {}
