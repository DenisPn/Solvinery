package groupId.DTO.Records.Model.ModelData;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 *
 * @param name The set's name
 * @param structure the types the set is made of, size one if its normal set, or the types of its composition if it's complex
 * @param alias the alias of the set, replaces set name
 */
public record SetDefinitionDTO(@Size(max = 255, message = "Set name has be shorter then 255 characters") @NotBlank String name,
                               @Valid @NotNull @Size(min = 1, message = "Set structure has to be at least of size one") List<@NotBlank @Size(max = 255, message = "Set structure has be shorter then 255 characters") String> structure,
                               @Size(max = 255, message = "Set alias has be shorter then 255 characters") String alias)  {
}
