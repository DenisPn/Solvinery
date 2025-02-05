package DTO.Records.Model.ModelDefinition;


import java.util.List;
import java.util.Set;

public record DependenciesDTO(
    Set<String> setDependencies,
    Set<String> paramDependencies
) {}
