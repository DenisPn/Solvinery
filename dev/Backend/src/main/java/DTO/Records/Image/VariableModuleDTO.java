package DTO.Records.Image;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record VariableModuleDTO(Set<String> variablesOfInterest,
                                Set<String> variablesConfigurableSets,
                                Set<String> variablesConfigurableParams,
                                Map<String, List<String>> variableAliases
                                ) { }
