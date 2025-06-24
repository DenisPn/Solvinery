package groupId.DTO.Records.Model.ModelDefinition;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * represents a variable parsed from zimpl code and included in the image
 * @param identifier the variable's identifier
 * @param structure the variable's structure, every string is supposed to have a Set associated with it.
 * @param alias the variable's alias. Can be null to signify no alias
 */
public record VariableDTO(
    @NotBlank  @Size(max = 255, message = "Variable name length has to be at most 255") String identifier,
    @Valid @NotNull List<@NotBlank String> structure,
    @Nullable @Size(max = 255, message = "Variable alias length has to be at most 255") String alias,
    @Nullable @Size(max = 255, message = "Variable objective value alias length has to be at most 255") String objectiveValueAlias
) {}
