package DTO.Records.Model.ModelDefinition;

import java.util.List;

public record PreferenceDTO(
    String name,
    DependenciesDTO dep
) {}