package groupId.DTO.Records.Model.ModelDefinition;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Deque;
import java.util.List;
import java.util.SequencedCollection;

/**
 * represents a variable parsed from zimpl code and included in the image
 * @param identifier the variable's identifier
 * @param structure the variable's structure, every string is supposed to have a Set associated with it.
 * @param alias the variable's alias. Can be null to signify no alias
 */
public record VariableDTO(
    @NotBlank String identifier,
    @Valid @NotNull List<String> structure,
    String alias
) {}
