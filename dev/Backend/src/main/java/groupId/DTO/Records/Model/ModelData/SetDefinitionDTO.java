package groupId.DTO.Records.Model.ModelData;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 *
 * @param name The set's name
 * @param structure the types the set is made of, size one if its normal set, or the types of its composition if it's complex
 * @param alias the alias of the set, replaces set name
 */
public record SetDefinitionDTO(@NotBlank String name, @NotBlank List<String> structure, String alias/*, List<String> typeAlias*/) {
}
