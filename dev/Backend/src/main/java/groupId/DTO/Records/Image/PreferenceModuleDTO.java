package groupId.DTO.Records.Image;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.Set;


/**
 *
 * @param moduleName the module's name
 * @param description the module's description
 * @param preferences a list of preference names which are in the module
 */
public record PreferenceModuleDTO(@NotBlank String moduleName,
                                  @NotNull  @Size(max = 4000, message = "Preference module description size invalid") String description,
                                  @Size(max = 255, message = "Preference module name size invalid") @Valid Set<@NotBlank String> preferences,
                                  @NotNull @Min(0) @Max(1) Float scalar
                                ) {}
